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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.web.util.UriTemplate;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import edu.mayo.qdm.webapp.rest.store.ExecutionInfo;

/**
 * The Class Executions.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@XStreamAlias("executions")
public class Executions {

	@XStreamImplicit(itemFieldName="execution")
	private List<Execution> executions = new ArrayList<Execution>();
	
	/**
	 * Instantiates a new executions.
	 */
	public Executions(){
		super();
	}
	
	/**
	 * Instantiates a new executions.
	 *
	 * @param infos the infos
	 * @param template the template
	 */
	public Executions(Set<ExecutionInfo> infos, UriTemplate template){
		super();
		for(ExecutionInfo info : infos){
			this.executions.add(new Execution(info, template));
		}
	}

	/**
	 * Gets the executions.
	 *
	 * @return the executions
	 */
	public List<Execution> getExecutions() {
		return executions;
	}

	/**
	 * Sets the execution.
	 *
	 * @param executions the new execution
	 */
	public void setExecution(List<Execution> executions) {
		this.executions = executions;
	}

}
