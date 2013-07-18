package edu.mayo.qdm.webapp.rest.controller;

import static org.junit.Assert.*

import org.junit.Test
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.servlet.ModelAndView

import edu.mayo.qdm.webapp.rest.store.FileSystemResolver
import edu.mayo.qdm.webapp.rest.xml.XmlProcessor




class TranslatorControllerTest {
	
	def incrementor
	
	def filePath;

	@Test
	void TestGetExceuctions(){
		def controller = new TranslatorController()
		controller.setFileSystemResolver(
			[
				getExecutionInfo:{ [] as Set }
			] as FileSystemResolver
			
		)
		
		controller.setXmlProcessor(
			[
				executionsToXml: { "<test>asdf</test>" }
			] as XmlProcessor
		)
		def req = new MockHttpServletRequest()
		
		assertNotNull controller.getExceuctions(req)
		
	}
	
	@Test
	void TestGetExceuctionsDefault() {
		def controller = new TranslatorController()
		controller.setFileSystemResolver(
			[
				getExecutionInfo:{ [] as Set }
			] as FileSystemResolver
			
		)
		
		controller.setXmlProcessor(
			[
				executionsToXml: { "<test>asdf</test>" }
			] as XmlProcessor
		)
		def req = new MockHttpServletRequest()
		
		assertTrue controller.getExceuctions(req) instanceof ResponseEntity
		
	}
	
	@Test
	void TestGetExceuctionsHtml() {
		def controller = new TranslatorController()
		controller.setFileSystemResolver(
			[
				getExecutionInfo:{ [] as Set }
			] as FileSystemResolver
			
		)
		
		controller.setXmlProcessor(
			[
				executionsToXml: { "<test>asdf</test>" }
			] as XmlProcessor
		)
		def req = new MockHttpServletRequest()
		req.addHeader("Accept", "text/html")
		
		assertTrue controller.getExceuctions(req) instanceof ModelAndView
		
	}
	
	@Test
	void TestIsHtmlRequestDefault() {
		def controller = new TranslatorController()
		
		def req = new MockHttpServletRequest()
		
		assertFalse controller.isHtmlRequest(req)
		
	}
	
	@Test
	void TestIsHtmlRequestTrue() {
		def controller = new TranslatorController()
		
		def req = new MockHttpServletRequest()
		req.addHeader("Accept", "text/html")
		
		assertTrue controller.isHtmlRequest(req)
		
	}
	
	@Test
	void TestIsHtmlRequestFalse() {
		def controller = new TranslatorController()
		
		def req = new MockHttpServletRequest()
		req.addHeader("Accept", "application/xml")
		
		assertFalse controller.isHtmlRequest(req)
		
	}
	
	
}
