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
package edu.mayo.qdm.executor;

import edu.mayo.qdm.patient.Patient;

import java.util.Map;

/**
 * The main entry point for the QDM Executor.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public interface Executor {

	/**
	 * Execute a given set of patients in a single session.
	 *
	 * @param patients the patients
	 * @return the results
	 */
	public void execute(Iterable<Patient> patients, String qdmXml, MeasurementPeriod measurementPeriod, Map<String,String> valueSetDefinitions, ResultCallback callback);

    public Results execute(Iterable<Patient> patients, String qdmXml, MeasurementPeriod measurementPeriod, Map<String,String> valueSetDefinitions);

    /**
     * Create a QdmProcessor for the given QDM XML.
     *
     * @param qdmXml the QDM XML
     * @return the QdmProcessor
     */
    public QdmProcessor getQdmProcessor(String qdmXml, MeasurementPeriod measurementPeriod, Map<String,String> valueSetDefinitions);

}
