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


import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import edu.mayo.qdm.executor.valueset.ValueSetCodeResolver;
import edu.mayo.qdm.patient.CodedEntry;
import edu.mayo.qdm.patient.Concept;
import edu.mayo.qdm.patient.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * The Class DroolsUtil.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public final class DroolsUtil {

    @Autowired
    private ValueSetCodeResolver valueSetCodeResolver;

    public DroolsUtil(){
        super();
//        JsonValueSetCodeResolver resolver = new JsonValueSetCodeResolver();
//        try {
//            resolver.afterPropertiesSet();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        this.valueSetCodeResolver = resolver;
    }

    public DroolsUtil(ValueSetCodeResolver valueSetCodeResolver){
        super();
        this.valueSetCodeResolver = valueSetCodeResolver;
    }

    public boolean contains(String valueSetOid, Iterable<Concept> concepts){
        return contains(valueSetOid, null, concepts);
    }

    public boolean contains(String valueSetOid, Object definition, Iterable<Concept> concepts){
        String def = definition != null ? definition.toString() : null;
        for(Concept concept : concepts){
            if(this.valueSetCodeResolver.isCodeInSet(valueSetOid, def, concept)){
                return true;
            }
        }

        return false;
    }

    public static Long toDays(Date date) {
        if(date == null){
            return null;
        } else {
            return new Long(java.util.concurrent.TimeUnit.MILLISECONDS.toDays(date.getTime()));
        }
    }

    public boolean matches(String valueSetOid, Concept concept){
        return matches(valueSetOid, null, concept);
    }

    public boolean matches(String valueSetOid, Object definition, Concept concept){
        if(concept == null){
            return false;
        }
        String def = definition != null ? definition.toString() : null;
        return this.valueSetCodeResolver.isCodeInSet(valueSetOid, def, concept);
    }

    public <T extends CodedEntry> Collection<T>  findMatches(String valueSetOid, Iterable <T> codedEntries){
        return findMatches(valueSetOid, null, codedEntries);
    }

    public <T extends CodedEntry> Collection<T>  findMatches(String valueSetOid, String definition, Iterable <T> codedEntries){
        String def = definition != null ? definition.toString() : null;
        List<T> returnList = new ArrayList<T>();

        if(Iterables.isEmpty(codedEntries)){
            return returnList;
        }

        for(T entry : codedEntries){
            for(Concept c : entry.getConcepts()){
                if(this.valueSetCodeResolver.isCodeInSet(valueSetOid, def, c)){
                    returnList.add(entry);
                    break;
                }
            }

        }

        return returnList;
    }

    public boolean allEquals(Iterable<?> objects){
       Object test = null;
       for(Object object : objects){
           if(test == null){
               test = object;
           } else {
               if(! object.equals(test)){
                   return false;
               } else {
                   test = object;
               }
           }
       }

        return true;
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
        Date originalDate = calendar.getTime();

        calendar.add(unit, value);

        Date date = calendar.getTime();

        return date;
    }

    public Map<SpecificOccurrenceId, Event> combine(SpecificOccurrence specificOccurrence, Map<SpecificOccurrenceId, Event> context){
        Map<SpecificOccurrenceId, Event> returnMap = new HashMap<SpecificOccurrenceId, Event>();

        returnMap.put(specificOccurrence.getId(), specificOccurrence.getEvent());
        returnMap.putAll(context);

        return returnMap;
    }

    public Map<SpecificOccurrenceId, Event> combine(Collection<Map<SpecificOccurrenceId, Event>> contexts, SpecificOccurrence specificOccurrence){
        Map<SpecificOccurrenceId, Event> returnMap = new HashMap<SpecificOccurrenceId, Event>();

        returnMap.put(specificOccurrence.getId(), specificOccurrence.getEvent());

        for(Map<SpecificOccurrenceId, Event> context : contexts){
            returnMap.putAll(context);
        }

        return returnMap;
    }

    public Map<SpecificOccurrenceId, Event> combine(Map<SpecificOccurrenceId, Event> context, SpecificOccurrence specificOccurrence){
        return this.combine(specificOccurrence, context);
    }

    public Map<SpecificOccurrenceId, Event> combine(Collection<Map<SpecificOccurrenceId, Event>> contexts){
        Map<SpecificOccurrenceId, Event> returnMap = new HashMap<SpecificOccurrenceId, Event>();

        for(Map<SpecificOccurrenceId, Event> context : contexts){
            returnMap.putAll(context);
        }

        return returnMap;
    }

    public Map<SpecificOccurrenceId, Event> intersect(String preconditionId, Collection<Map<SpecificOccurrenceId, Event>> contexts){
        Map<SpecificOccurrenceId, Event> returnMap = new HashMap<SpecificOccurrenceId, Event>();

        Set<SpecificOccurrenceId> intersect = null;
        for(Map<SpecificOccurrenceId, Event> context : contexts){
            if(intersect == null){
                intersect = context.keySet();
            } else {
                intersect = Sets.intersection(intersect, context.keySet());
            }
        }

        Map<SpecificOccurrenceId, Set<Event>> comboMap = new HashMap<SpecificOccurrenceId, Set<Event>>();
        for(Map<SpecificOccurrenceId, Event> context : contexts){
            for(Map.Entry<SpecificOccurrenceId, Event> entry : context.entrySet()){
                if(! comboMap.containsKey(entry.getKey())){
                    comboMap.put(entry.getKey(), new HashSet<Event>());
                }
                comboMap.get(entry.getKey()).add(entry.getValue());
            }
        }

        for(Map.Entry<SpecificOccurrenceId, Set<Event>> entry : comboMap.entrySet()){
            if(entry.getValue().size() > 1){
                returnMap.put(entry.getKey(), null);
            } else {
                returnMap.put(entry.getKey(), entry.getValue().iterator().next());
            }
        }

        return returnMap;
    }

}
