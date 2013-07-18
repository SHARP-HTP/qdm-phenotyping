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
package edu.mayo.qdm;

import edu.mayo.qdm.patient.Patient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Result holder for Population results (i.e. Initial Patient Population (IPP), etc).
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class Results {
	
	private Map<String,Set<Patient>> resultMap = new HashMap<String,Set<Patient>>();

    public void add(String populationCriteria, Patient patient){
        synchronized (this.resultMap){
            if(! this.resultMap.containsKey(populationCriteria)){
                this.resultMap.put(populationCriteria, new HashSet<Patient>());
            }

            this.resultMap.get(populationCriteria).add(patient);
        }
    }

    public Set<String> getPopulationCriterias() {
        return this.resultMap.keySet();
    }

    public Set<Patient> get(String populationCriteria) {
        Set<Patient> set = this.resultMap.get(populationCriteria);
        if(set == null){
            return new HashSet<Patient>();
        } else {
            return set;
        }
    }

    public void addAll(String populationCriteria, Set<Patient> patients) {
        synchronized (this.resultMap){
            if(! this.resultMap.containsKey(populationCriteria)){
                this.resultMap.put(populationCriteria, new HashSet<Patient>());
            }

            this.resultMap.get(populationCriteria).addAll(patients);
        }
    }
}
