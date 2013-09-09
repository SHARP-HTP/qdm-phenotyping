package edu.mayo.qdm.executor.drools.cypress;

import edu.mayo.qdm.executor.drools.DroolsExecutor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
@ContextConfiguration("classpath:/qdm-executor-context.xml")
@Ignore
public abstract class AbstractAllCypressMeasuresTestIT {

	@Autowired
	protected DroolsExecutor executor;

	protected XmlStream xml;

	protected static class XmlStream {
        protected InputStream xmlStream;
        protected String name;
	}

	public AbstractAllCypressMeasuresTestIT(XmlStream xml){
		super();
		this.xml = xml;
	}

	@Parameterized.Parameters
	public static Collection<XmlStream[]> getXmlToTest() throws Exception {
		return getXmlFiles();
	}
	
	public static List<XmlStream[]> getXmlFiles() throws Exception {
		List<XmlStream[]> returnList = new ArrayList<XmlStream[]>();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        Resource[] resources = resolver.getResources("classpath:/cypress/measures/ep/*/hqmf1.xml");

        for(Resource resource : resources){
            XmlStream stream = new XmlStream();
            stream.xmlStream = resource.getInputStream();

            returnList.add(new XmlStream[]{stream});
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