package edu.mayo.qdm.webapp.rest.xml;

import static org.junit.Assert.*

import org.custommonkey.xmlunit.XMLAssert
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Test

import edu.mayo.qdm.webapp.rest.model.Execution
import edu.mayo.qdm.webapp.rest.model.Executions
import edu.mayo.qdm.webapp.rest.model.Image
import edu.mayo.qdm.webapp.rest.model.Xml
import edu.mayo.qdm.webapp.rest.store.Parameters
import edu.mayo.qdm.webapp.rest.store.ExecutionInfo.Status






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
	void TestOutputXmlWithImageAndXml() {
		def proc = new XmlProcessor();
		
		def executions = new Executions(executions: [
			new Execution(id:"1", 
				xml:new Xml(href:"../exection/1/xml"),
				image:new Image(href:"../exection/1/image"))
		])
		def xml = proc.executionsToXml(executions)

		assertNotNull xml
		
		print xml
		
		XMLUnit.setIgnoreWhitespace(true)

		XMLAssert.assertXMLEqual('''<executions>
						  <execution id='1'>
							<xml href='../exection/1/xml'/>
							<image href='../exection/1/image'/>
						  </execution>
						</executions>''', xml)
	
	}

	@Test
	void TestOutputXmlWithImageAndXmlAndStatus() {
		def proc = new XmlProcessor();
		
		def executions = new Executions(executions: [
			new Execution(id:"1",
				status:Status.COMPLETE,
				xml:new Xml(href:"../exection/1/xml"),
				image:new Image(href:"../exection/1/image"))
		])
		def xml = proc.executionsToXml(executions)

		assertNotNull xml
		
		print xml
		
		XMLUnit.setIgnoreWhitespace(true)

		XMLAssert.assertXMLEqual('''<executions>
						  <execution id='1' status='COMPLETE'>
							<xml href='../exection/1/xml'/>
							<image href='../exection/1/image'/>
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
								<zipFileName>test.zip</zipFileName>
							</parameters>
						  </execution>
						</executions>''', xml)
	
	}
	


}
