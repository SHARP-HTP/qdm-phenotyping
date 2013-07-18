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
package edu.mayo.qdm.webapp.rest.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * The Class AcceptHeaderAdjustingFilter.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class AcceptHeaderAdjustingFilter implements Filter {

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		//
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(
			ServletRequest request, 
			ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if(! (request instanceof HttpServletRequest)){
			throw new IllegalStateException("ServletRequest expected to be of type HttpServletRequest");
		}

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		@SuppressWarnings("unchecked")
		Map<String, String[]> params = httpRequest.getParameterMap();
		
		if(params.containsKey("format")){
			String[] formats = params.get("format");
			if(formats.length != 1){
				throw new IllegalStateException("Only one 'format' parameter allowed.");
			}
			
			String format = formats[0];
			
			String type;
			
			if(format.equals("json")){
				type = "application/json";
			} else if (format.equals("xml")){
				type = "application/xml";
			} else {
				throw new IllegalStateException("Format: " + format + " not recognized.");
			}
			
			chain.doFilter(new AcceptTypeChangingRequest(httpRequest, type), response);
		} else {
			chain.doFilter(request, response);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		//
	}
	
	/**
	 * The Class AcceptTypeChangingRequest.
	 *
	 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
	 */
	public class AcceptTypeChangingRequest extends HttpServletRequestWrapper {

		private String acceptHeader;
		
		/**
		 * Instantiates a new accept type changing request.
		 *
		 * @param request the request
		 * @param acceptHeader the accept header
		 */
		public AcceptTypeChangingRequest(HttpServletRequest request, String acceptHeader) {
			super(request);
			this.acceptHeader = acceptHeader;
		}
		
		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequestWrapper#getHeaders(java.lang.String)
		 */
		@SuppressWarnings("rawtypes")
		public Enumeration getHeaders(String name){
			if(name.equalsIgnoreCase("accept")){
				return Collections.enumeration(Arrays.asList(acceptHeader));
			}
			
			return super.getHeaders(name);
		}
		
		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequestWrapper#getHeader(java.lang.String)
		 */
		@Override
		public String getHeader(String name) {
			if(name.equalsIgnoreCase("accept")){
				return acceptHeader;
			}
			
			return super.getHeader(name);
		}
		
	}

}
