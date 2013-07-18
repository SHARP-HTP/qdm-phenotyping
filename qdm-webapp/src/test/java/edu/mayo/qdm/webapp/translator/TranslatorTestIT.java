package edu.mayo.qdm.webapp.translator;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import junit.framework.Assert;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.mayo.qdm.webapp.rest.controller.DateValidator.DateType;

//@Ignore
@SuppressWarnings("deprecation")
public class TranslatorTestIT {

	//private String url = "https://172.24.156.143:8081/htp/";
	private String url = "https://bmidev3.mayo.edu:8081/htp/";

	private static RestTemplate getRestTemplate() {
		trustSelfSignedSSL();
		
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
	        @Override
	        public boolean verify(String s, SSLSession sslsession) {
	            return true;
	        }
	    });

		HttpClient client = new HttpClient();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				"admin", "htp$$rest");
		client.getState().setCredentials(AuthScope.ANY, credentials);
		CommonsClientHttpRequestFactory commons = new CommonsClientHttpRequestFactory(
				client);

		return new RestTemplate(commons);
	}

	HttpHeaders headers = new HttpHeaders();
	{
		headers.set("Accept", "application/xml");
	}

	private String[] zips = { "0001", "0002", "0056", "0064", "0069", "0074" };

	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	@Test
	public void testAllRun() throws Throwable {
		//Set<Future<?>> futures = new HashSet<Future<?>>();
		
		final int[] i = new int[]{0};
		
		for (; i[0] < 10; i[0]++) {
			for (final String zip : zips) {
				
				//Future<?> f = this.executorService.submit(new Runnable(){

				//	@Override
				//	public void run() {
						try {
							testRun(zip);
							System.out.println(" -> PASS on: " + zip + " Run: " + i[0]);
						} catch (Throwable e) {
							System.out.println(" -> FAIL on: " + zip + " Run: " + i[0] + " -- Error: " + e.getMessage());
						}
				//	}
					
				//});
				
				//futures.add(f);
			}
		}
		
		//this.executorService.awaitTermination(10, TimeUnit.HOURS);
	}

	public void testRun(String zipName) throws Throwable {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("startDate", getRandomDate(DateType.START));
		parts.add("endDate", getRandomDate(DateType.END));

		Resource zip = new ClassPathResource("NQF_" + zipName
				+ "_HHS_Updated_Dec_2011.zip");
		parts.add("file", zip);
		
		System.out.println("Exeucting ZIP: " + zipName + 
				" Start Date: " + parts.get("startDate") +
				" End Date: " + parts.get("endDate"));

		ResponseEntity<Object> response = getRestTemplate().exchange(url + "executions",
				HttpMethod.POST, new HttpEntity<Object>(parts, headers),
				Object.class, parts);

		String location = response.getHeaders().get("Location").get(0);

		while (!this.isDone(location)) {
			Thread.sleep(2000);
		}

		String xml = this.getXml(location);
		
		this.delete(location);

		int denominator = checkXml(xml, "Denominator");
		int numerator = checkXml(xml, "Numerator");

		Assert.assertTrue("No Denominator", denominator > 0);
		Assert.assertTrue("No Numerator", numerator > 0);
		
		Set<String> types = checkXmlForTypes(xml);
		
		Assert.assertTrue("Returned Types: " + types, types.size() >= 3);
		System.out.println(" * Returned Types: " + types);
		Assert.assertTrue("Did not return expected type: Denominator", startsWith(types,"Denominator"));
		Assert.assertTrue("Did not return expected type: Numerator", startsWith(types,"Numerator"));
		Assert.assertTrue("Did not return expected type: Initial Patient Population", types.contains("Initial Patient Population"));


	}
	
	private static boolean startsWith(Set<String> set , String search){
		for(String s : set){
			if(s.startsWith(search)){
				return true;
			}
		}
		return false;
	}

	private void delete(String location) {
		getRestTemplate().delete(url + location);
	}

	private boolean isDone(String location) {
		ResponseEntity<String> xml = getRestTemplate().exchange(url + location,
				HttpMethod.GET, new HttpEntity<Object>(headers), String.class);

		return !xml.getBody().contains("status=\"PROCESSING\"");
	}

	private String getXml(String location) {
		ResponseEntity<String> xml = getRestTemplate().exchange(url + location + "/xml",
				HttpMethod.GET, new HttpEntity<Object>(headers), String.class);

		if(StringUtils.isBlank(xml.getBody())){
			throw new RuntimeException("No XML Returned.");
		}
		return xml.getBody();
	}

	private static int checkXml(String xml, String check) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(IOUtils.toInputStream(xml));

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath
				.compile("//DemographicType[starts-with(type/text(),'" + check
						+ "')]//value");

		NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

		int value = 0;
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			value += Integer.parseInt(node.getTextContent());
		}

		return value;
	}
	
	private static Set<String> checkXmlForTypes(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(IOUtils.toInputStream(xml));

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath
				.compile("//DemographicType/type");

		NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

		Set<String> values = new HashSet<String>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			values.add(node.getTextContent());
		}

		return values;
	}

	public static void main(String[] args) throws Exception {
	
		Resource r = new ClassPathResource("sample.xml");

		String xml = IOUtils.toString(r.getInputStream());

		System.out.println(checkXml(xml, "Denominator"));
		System.out.println(checkXml(xml, "Numerator"));
		System.out.println(checkXmlForTypes(xml));
	
		/*
		for(int i=0;i<10;i++){
			System.out.println("START: " + getRandomDate(DateType.START));
			System.out.println("END: " + getRandomDate(DateType.END));
		}
		*/
	}

	public static void trustSelfSignedSSL() {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLContext.setDefault(ctx);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

    public static String getRandomDate(DateType type) throws Exception {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

    	long min,max;

    	
    	switch ( type ) {
			case START : {
				min = dateFormat.parse("01-Jan-1995").getTime();
				max = dateFormat.parse("31-Dec-2002").getTime();
				break;
			}
			case END : {
				min = dateFormat.parse("31-Dec-2010").getTime();
				max = dateFormat.parse("31-June-2012").getTime();
				break;
			}
			default : {
				throw new IllegalStateException();
			}
		}

    	Random rand = new Random(System.nanoTime());
    	long mills = nextLong(rand, max - min) + min;
    
    	Date returnDate = new Date(mills);

        return dateFormat.format(returnDate);
    }

	private static long nextLong(Random rng, long n) {
		long bits, val;
		do {
			bits = (rng.nextLong() << 1) >>> 1;
			val = bits % n;
		} while (bits - val + (n - 1) < 0L);
		return val;
	}

}
