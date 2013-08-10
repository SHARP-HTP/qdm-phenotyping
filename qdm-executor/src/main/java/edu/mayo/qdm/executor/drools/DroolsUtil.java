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
package edu.mayo.qdm.executor.drools;


import edu.mayo.qdm.executor.valueset.ValueSetCodeResolver;
import edu.mayo.qdm.patient.CodedEntry;
import edu.mayo.qdm.patient.Concept;
import edu.mayo.qdm.patient.Event;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * The Class DroolsUtil.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public final class DroolsUtil {

    @Resource
    private ValueSetCodeResolver valueSetCodeResolver;

    public DroolsUtil(){
        super();
    }

    public DroolsUtil(ValueSetCodeResolver valueSetCodeResolver){
        super();
        this.valueSetCodeResolver = valueSetCodeResolver;
    }

    public boolean contains(String valueSetOid, Iterable<Concept> concepts){
        for(Concept concept : concepts){
            if(this.valueSetCodeResolver.isCodeInSet(valueSetOid, concept)){
                return true;
            }
        }

        return false;
    }

    public <T extends CodedEntry> Collection<T>  findMatches(String valueSetOid, Iterable <T> codedEntries){
        List<T> returnList = new ArrayList<T>();
        for(T entry : codedEntries){
            if(this.valueSetCodeResolver.isCodeInSet(valueSetOid, entry.getConcept())){
                returnList.add(entry);
            }
        }

        return returnList;
    }

    public Calendar getCalendar(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar;
    }

    public Event toEvent(Date date, String code, String codingScheme){
        return new Event(new Concept(code,codingScheme,null), date);
    }

    public Date add(Calendar calendar, int unit, int value){
        calendar.add(unit, value);

        return calendar.getTime();
    }
/*
    public Collection<Event> combine(SpecificOccurrence o, Set<Event> e){
        if(o.getOccurrences() == null){
            return e;
        } else {
            Set<Event> events = new HashSet<Event>();
            events.addAll(e);
            events.retainAll(o.getOccurrences());
            return events;
        }
    }


    public <T extends Event> T mostRecent(Iterable <T> codedEntries){
        T mostRecent = null;
        for(T entry : codedEntries){
            if(mostRecent == null || mostRecent.getStartDate().after(entry.getStartDate())){
                mostRecent = entry;
            }
        }

        return (T) new Lab(null, null, new Date(), new Date());
    }

    public void intersect(List<Event> list, SpecificOccurrence occurs) {
        occurs.getOccurrences().retainAll(list);
    }
    */
}
