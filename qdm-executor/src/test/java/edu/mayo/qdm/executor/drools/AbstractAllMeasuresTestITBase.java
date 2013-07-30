package edu.mayo.qdm.executor.drools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@RunWith(Parameterized.class)
@ContextConfiguration("classpath:/qdm-executor-context.xml")
public class AbstractAllMeasuresTestITBase {

	@Autowired
	protected DroolsExecutor executor;

	protected XmlStream xml;

	protected static class XmlStream {
        protected InputStream xmlStream;
        protected String name;
	}

	public AbstractAllMeasuresTestITBase(XmlStream xml){
		super();
		this.xml = xml;
	}

	@Parameterized.Parameters
	public static Collection<XmlStream[]> getXmlToTest() throws Exception {
		return getXmlFiles();
	}
	
	public static List<XmlStream[]> getXmlFiles() throws Exception {
		List<XmlStream[]> returnList = new ArrayList<XmlStream[]>();
		
		ZipInputStream zis = null;
		InputStream is = null;
		try {
			ZipFile zipFile = new ZipFile(new ClassPathResource(
                    "ESpecs_2014_eCQM_EP.zip").getFile());

			for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
				ZipEntry ze = e.nextElement();
				if (ze.getName().contains(".zip")) {	
					
					File innerZip = File.createTempFile("temp", "zip");
					FileUtils.copyInputStreamToFile(zipFile.getInputStream(ze), innerZip);
					
					is = zipFile.getInputStream(ze);
					zis = new ZipInputStream(is);
					ZipEntry zentry = zis.getNextEntry();
					
					while (zentry != null) {
						if(zentry.getName().endsWith(".xml")){
							InputStream xmlStream = 
								new ZipFile(innerZip).getInputStream(zentry);

							XmlStream stream = new XmlStream();
							stream.xmlStream = IOUtils.toBufferedInputStream(xmlStream);
							stream.name = zentry.getName();
							
							returnList.add(
								new XmlStream[]{stream});
							
							FileUtils.deleteQuietly(innerZip);
						}
						zentry = zis.getNextEntry();
					}
					//is.close();
				}
			}
		} finally {
			//
		}
		
		return returnList;
	}

	@Before
	public void setUpSpringContext() throws Exception {
		TestContextManager testContextManager = new TestContextManager(
				getClass());
		testContextManager.prepareTestInstance(this);
	}

}