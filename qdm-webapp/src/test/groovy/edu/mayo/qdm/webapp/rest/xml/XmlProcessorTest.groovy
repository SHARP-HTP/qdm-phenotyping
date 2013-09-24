package edu.mayo.qdm.webapp.rest.xml
import edu.mayo.qdm.webapp.rest.model.Execution
import edu.mayo.qdm.webapp.rest.model.Executions
import edu.mayo.qdm.webapp.rest.store.ExecutionInfo.Status
import edu.mayo.qdm.webapp.rest.store.Parameters
import org.custommonkey.xmlunit.XMLAssert
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Test

import static org.junit.Assert.assertNotNull

class XmlProcessorTest {

	@Test
	void TestOutputXmlOneExecution() {
		def proc = new XmlProcessor();
		
		def executions = new Executions(executions: [
			new Execution(id:"1")	
		])
		def xml = proc.executionsToXml(executions)

		assertNotNull xml
		
		XMLUnit.setIgnoreWhitespace(true)
		
		
		XMLAssert.assertXMLEqual('''<executions>
						  <execution id='1'>
						  </execution>
						</executions>''', xml)
	
	}
	
	@Test
	void TestOutputXmlTwoExecutions() {
		def proc = new XmlProcessor();
		
		def executions = new Executions(executions: [
			new Execution(id:"1"),
			new Execution(id:"2")
		])
		def xml = proc.executionsToXml(executions)

		assertNotNull xml
		
		XMLUnit.setIgnoreWhitespace(true)
		
		
		XMLAssert.assertXMLEqual('''<executions>
						  <execution id='1'>
						  </execution>
						  <execution id='2'>
						  </execution>
						</executions>''', xml)
	
	}

	@Test
	void TestOutputXmlWithParameter() {
		def proc = new XmlProcessor();
		
		def executions = new Executions(executions: [
			new Execution(id:"1",
				status:Status.COMPLETE,
				parameters:new Parameters("10-Jun-2000","10-Jun-2000", "test.zip"))
		])
		def xml = proc.executionsToXml(executions)

		assertNotNull xml
		
		print xml
		
		XMLUnit.setIgnoreWhitespace(true)

		XMLAssert.assertXMLEqual('''<executions>
						  <execution id='1' status='COMPLETE'>
							<parameters>
								<startDate>10-Jun-2000</startDate>
								<endDate>10-Jun-2000</endDate>
								<xmlFileName>test.zip</xmlFileName>
							</parameters>
						  </execution>
						</executions>''', xml)
	
	}
	


}
