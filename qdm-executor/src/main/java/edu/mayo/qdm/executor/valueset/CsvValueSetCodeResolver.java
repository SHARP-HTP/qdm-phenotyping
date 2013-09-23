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
package edu.mayo.qdm.executor.valueset;

import edu.mayo.qdm.patient.Concept;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.InputStreamReader;
import java.util.*;

/**
 * The Class CsvValueSetCodeResolver.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
//@Component
public class CsvValueSetCodeResolver implements ValueSetCodeResolver, InitializingBean {

    private Map<ValueSetKey,Set<Concept>> valueSetMap;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.valueSetMap = this.readCsv();
    }

    protected Map<ValueSetKey,Set<Concept>> readCsv() throws Exception {
        ICsvMapReader mapReader = new CsvMapReader(
                new InputStreamReader(new ClassPathResource("2014-default-valuesets.csv").getInputStream()), CsvPreference.STANDARD_PREFERENCE);

        final String[] header = mapReader.getHeader(true);

        Map<ValueSetKey,Set<Concept>> valueSetMap = new HashMap<ValueSetKey,Set<Concept>>();

        Map<String, Object> customerMap;
        while( (customerMap = mapReader.read(header, new CellProcessor[14])) != null ) {
            //Not sure if this is necessary -->
            //String id = (String)customerMap.get("Measure Identifier");

            String oid = (String)customerMap.get("Value Set OID");
            String concept = (String)customerMap.get("Concept");
            String codingScheme = (String)customerMap.get("Code System");
            String codingSchemeVersion = (String)customerMap.get("Code System Version");

            ValueSetKey key = new ValueSetKey(oid);

            if(! valueSetMap.containsKey(key)){
                valueSetMap.put(key, new HashSet<Concept>());
            }
            valueSetMap.get(key).add(new Concept(concept, codingScheme, codingSchemeVersion));
        }

        return valueSetMap;
    }

    @Override
    public boolean isCodeInSet(String valueSetOid, Concept concept) {
        Set<Concept> valueSetConcepts = this.valueSetMap.get(new ValueSetKey(valueSetOid));
        if(CollectionUtils.isEmpty(valueSetConcepts)){
            return false;
        }

        Map<String,List<Concept>> initialMatchingConcepts = new HashMap<String,List<Concept>>();
        for(Concept valueSetConcept : valueSetConcepts){
            String code = valueSetConcept.getCode();
            if(! initialMatchingConcepts.containsKey(code)){
                initialMatchingConcepts.put(code, new ArrayList<Concept>());
            }
            initialMatchingConcepts.get(valueSetConcept.getCode()).add(concept);
        }

        List<Concept> matches = initialMatchingConcepts.get(concept.getCode());

        if(CollectionUtils.isNotEmpty(matches)){
            for(Concept match : matches){
                if(concept.matches(match)){
                    return true;
                }
            }
        }

        return false;
    }

    private static class ValueSetKey {
        private String oid;

        private ValueSetKey(String oid) {
            this.oid = oid;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ValueSetKey that = (ValueSetKey) o;

            if (oid != null ? !oid.equals(that.oid) : that.oid != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return oid != null ? oid.hashCode() : 0;
        }
    }
    /* (non-Javadoc)
         * @see ValueSetCodeResolver#resolveCodes(java.lang.String)
         */
	public Set<Concept> resolveConcpets(String valueSetOid) {
		return this.valueSetMap.get(new ValueSetKey(valueSetOid));
	}

    @Override
    public boolean isCodeInSet(String valueSetOid, String definition, Concept concept) {
        return isCodeInSet(valueSetOid, concept);
    }

}
