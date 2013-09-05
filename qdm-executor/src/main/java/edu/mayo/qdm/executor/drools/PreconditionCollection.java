package edu.mayo.qdm.executor.drools;

import com.google.common.collect.Sets;
import edu.mayo.qdm.patient.Event;

import java.util.*;

/**
 */
public class PreconditionCollection extends HashSet<PreconditionResult> {

    public Map<SpecificOccurrenceId, Event> getContext() {
        Collection<Map<SpecificOccurrenceId, Event>> preconditions = new HashSet<Map<SpecificOccurrenceId, Event>>();
        for(PreconditionResult precondition : this){
            preconditions.add(precondition.getContext());
        }

        return this.combine(preconditions);
    }

    private Map<SpecificOccurrenceId, Event> combine(Collection<Map<SpecificOccurrenceId, Event>> contexts){
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
