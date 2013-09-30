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

import java.util.Set;

/**
 * Allows for the resolving and existence checking of a {@link Concept}.
 * in a ValueSet.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public interface ValueSetCodeResolver {
	
	/**
	 * Resolve codes.
	 *
	 * @param valueSetOid the value set oid
	 * @return the sets the
	 */
	public Set<Concept> resolveConcpets(String valueSetOid);

    /**
     * Determines whether or not the given {@link Concept} is contained in the
     * provided ValueSet.
     *
     * @param valueSetOid
     * @param concept
     * @return true, if the {@link Concept} is contained
     */
    public boolean isCodeInSet(String valueSetOid, Concept concept);

    /**
     * Determines whether or not the given {@link Concept} is contained in the
     * provided ValueSet.
     *
     * @param valueSetOid
     * @param concept
     * @return true, if the {@link Concept} is contained
     */
    public boolean isCodeInSet(String valueSetOid, String definition, Concept concept);

}
