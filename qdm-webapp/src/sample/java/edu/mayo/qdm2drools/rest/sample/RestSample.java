/*
 * Copyright: (c) 2004-2012 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.mayo.qdm.webapp.rest.sample;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;

/**
 * The Class RestSample.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class RestSample {

	/**
	 * Poll status.
	 *
	 * @throws Exception the exception
	 */
	public static void pollStatus() throws Exception {
		URL executions = new URL(
				"http://.../execution/{id}");

		URLConnection connection = executions.openConnection();
		connection.setRequestProperty("Accept", "application/xml");
		InputStream in = connection.getInputStream();

		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(in);

		Attr status = (Attr) doc.getElementsByTagName("execution").item(0)
				.getAttributes().getNamedItem("status");

		System.out.println("Status: " + status.getValue());

		in.close();
	}

	/**
	 * Gets the xml.
	 *
	 * @return the xml
	 * @throws Exception the exception
	 */
	public static void getXml() throws Exception {
		URL executions = new URL(
				"http://.../execution/{id}/xml");

		URLConnection connection = executions.openConnection();
		connection.setRequestProperty("Accept", "application/xml");
		InputStream in = connection.getInputStream();

		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(in);

		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer trans = transFactory.newTransformer();
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc.getDocumentElement());

		trans.transform(source, result);

		System.out.println("XML: " + sw.toString());

		in.close();
	}

	/**
	 * Gets the image.
	 *
	 * @return the image
	 * @throws Exception the exception
	 */
	public static void getImage() throws Exception {
		URL executions = new URL(
				"http://.../execution/{id}/image");

		URLConnection connection = executions.openConnection();
		connection.setRequestProperty("Accept", "image/png");
		InputStream in = connection.getInputStream();

		File tempFile = File.createTempFile(UUID.randomUUID().toString(),
				".png");

		OutputStream out = new BufferedOutputStream(new FileOutputStream(
				tempFile));
		for (int b; (b = in.read()) != -1;) {
			out.write(b);
		}

		out.flush();
		out.close();
		in.close();

		System.out.println("Image Saved to: " + tempFile.getAbsolutePath());
	}

	/**
	 * Creates the execution.
	 *
	 * @param targetUrl the target url
	 * @param zipFile the zip file
	 * @param startDate the start date
	 * @param endDate the end date
	 * @throws Exception the exception
	 */
	public static void createExecution(String targetUrl, File zipFile, String startDate,
	        String endDate) throws Exception {
	    String charset = "UTF-8";
	 
	    String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
	    String CRLF = "\r\n"; // Line separator required by multipart/form-data.
	 
	    URL url = new URL(targetUrl);
	    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
	
	    connection.setDoOutput(true);
	    connection.setInstanceFollowRedirects(false);
	    connection.setRequestProperty("Accept", "application/xml");
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
	     
	    PrintWriter writer = null;
	    try {
	        OutputStream output = connection.getOutputStream();
	        writer = new PrintWriter(new OutputStreamWriter(output, charset), true); // true = autoFlush, important!
	 
	        // Send start date.
	        writer.append("--" + boundary).append(CRLF);
	        writer.append("Content-Disposition: form-data; name=\"startDate\"").append(CRLF);
	        writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
	        writer.append(CRLF);
	        writer.append(startDate).append(CRLF).flush();
	 
	        // Send end date.
	        writer.append("--" + boundary).append(CRLF);
	        writer.append("Content-Disposition: form-data; name=\"endDate\"").append(CRLF);
	        writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
	        writer.append(CRLF);
	        writer.append(endDate).append(CRLF).flush();
	 
	        // Send binary file.
	        writer.append("--" + boundary).append(CRLF);
	        writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + zipFile.getName() + "\"").append(CRLF);
	        writer.append("Content-Type: application/zip").append(CRLF);
	        writer.append("Content-Transfer-Encoding: binary").append(CRLF);
	        writer.append(CRLF).flush();
	        InputStream input = null;
	        try {
	            input = new FileInputStream(zipFile);
	            byte[] buffer = new byte[1024];
	            for (int length = 0; (length = input.read(buffer)) > 0;) {
	                output.write(buffer, 0, length);
	            }
	            output.flush(); // Important! Output cannot be closed. Close of writer will close output as well.
	        } finally {
	            if (input != null) {
	                input.close();
	            }
	        }
	        writer.append(CRLF).flush(); // CRLF is important! It indicates end of binary boundary.
	 
	        // End of multipart/form-data.
	        writer.append("--" + boundary + "--").append(CRLF).flush();
	         
	        Map<String, List<String>> headerfields = connection.getHeaderFields();
	         
	        System.out.println("Resource Created At: " + headerfields.get("Location").get(0));
	 
	    } finally {
	        if (writer != null) {
	            writer.close();
	        }
	        connection.disconnect();
	    }
	}
	
	/**
	 * Delete execution.
	 *
	 * @throws Exception the exception
	 */
	public static void deleteExecution() throws Exception {
		URL url = new URL("http://.../execution/{id}");
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestProperty("Accept", "application/xml");
		connection.setDoOutput(false);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("DELETE");
		
		if(connection.getResponseCode() == 200){
			System.out.println("DELETED");
		} else {
			System.out.println("ERROR: " + connection.getResponseCode());
		}
	}
	
	public static void main(String[] args) throws Exception{
		createExecution();
		
	}
	public static void createExecution() throws Exception {
		setAuth();
		setHostNameVerifier();
		trustSelfSignedSSL() ;
		
		createExecution("https://172.24.156.143:8081/htp/executions", new File(
				"src/test/resources/NQF_0064_HHS_Updated_Dec_2011.zip"),
				"01-Jan-2000", "01-May-2011");
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

    
    private static void setHostNameVerifier(){
    	HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
	        @Override
	        public boolean verify(String s, SSLSession sslsession) {
	            return true;
	        }
	    });
    }
    
    private static void setAuth(){
	    Authenticator.setDefault (new Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication ("admin", "xxxxx".toCharArray());
	        }
	    });
    }

}
