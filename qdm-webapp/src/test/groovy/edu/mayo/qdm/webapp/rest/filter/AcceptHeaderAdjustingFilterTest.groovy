package edu.mayo.qdm.webapp.rest.filter;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class AcceptHeaderAdjustingFilterTest {

	@Test
	void TestFilterNotOverridden() {
		def filter = new AcceptHeaderAdjustingFilter()
		
		def req = new MockHttpServletRequest()
		req.addHeader("Accept", "application/something")
		
		def resp = new MockHttpServletResponse()
		def chain = new MockFilterChain()
		
		filter.doFilter(req, resp, chain);
		
		assertEquals "application/something", chain.getRequest().getHeader("Accept")
	}
	
	@Test
	void TestFilterOverriddenXml() {
		def filter = new AcceptHeaderAdjustingFilter()
		
		def req = new MockHttpServletRequest()
		req.addHeader("Accept", "application/something")
		req.setParameter("format", "xml")
		
		def resp = new MockHttpServletResponse()
		def chain = new MockFilterChain()
		
		filter.doFilter(req, resp, chain);
		
		assertEquals "application/xml", chain.getRequest().getHeader("Accept")
	}
}
