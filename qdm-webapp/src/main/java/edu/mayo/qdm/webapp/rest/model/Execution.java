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
package edu.mayo.qdm.webapp.rest.model;

import org.springframework.web.util.UriTemplate;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import edu.mayo.qdm.webapp.rest.store.ExecutionInfo;

/**
 * A Decorator over the ExecutionInfo class that provides 'href' access
 * to the image and xml file.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@XStreamAlias("execution")
public class Execution extends ExecutionInfo {

	private Xml xml;

	/**
	 * Instantiates a new execution.
	 */
	public Execution(){
		super();
	}
	
	/**
	 * Instantiates a new execution.
	 *
	 * @param info the info
	 * @param template the template
	 */
	public Execution(ExecutionInfo info, UriTemplate template){
		super();
		this.setId(info.getId());
		this.setStatus(info.getStatus());
		this.setError(info.getError());
		this.setStart(info.getStart());
		this.setFinish(info.getFinish());
		this.setParameters(info.getParameters());
		
		if(info.getStatus() != null && info.getStatus().equals(Status.COMPLETE)){
			this.xml = new Xml(info.getId(), template);
		}
	}

	/**
	 * Gets the xml.
	 *
	 * @return the xml
	 */
	public Xml getXml() {
		return xml;
	}
	
	/**
	 * Sets the xml.
	 *
	 * @param xml the new xml
	 */
	public void setXml(Xml xml) {
		this.xml = xml;
	}
}
