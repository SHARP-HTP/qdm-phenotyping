package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

import java.util.*;

/**
 */
public class SpecificContextManager {

    public static SpecificContext intersect(Patient p, String id, List<SpecificContext> contexts){
        Set<SpecificContextTuple> intersectResult = null;

        for (SpecificContext context : contexts){
            Set<SpecificContextTuple> tuples = context.getSpecificContextTuples();
            intersectResult = intersect(intersectResult, tuples);
        }

        SpecificContext c = new SpecificContext(id, p);
        c.setSpecificContextTuples(intersectResult);

        return c;
    }

    private static Set<SpecificContextTuple> intersect(Set<SpecificContextTuple> tuples1, Set<SpecificContextTuple> tuples2) {
        if(tuples1 == null) return tuples2;
        if(tuples2 == null) return tuples1;

        Set<SpecificContextTuple> returnSet = new HashSet<>(tuples1);
        for (SpecificContextTuple tuple : tuples2) {
            for(Map.Entry<SpecificOccurrenceId, Event> entry : tuple.getContext().entrySet()) {
                SpecificContextTuple foundTuple = findMatch(tuples1, entry);
                if (foundTuple != null) {
                    if (!returnSet.contains(foundTuple))
                        returnSet.add(foundTuple);
                } else {
                    return new HashSet<>();
                }
            }
        }

        return returnSet;
    }


    private static SpecificContextTuple findMatch(Set<SpecificContextTuple> tuples, Map.Entry<SpecificOccurrenceId, Event> entry) {
        for (SpecificContextTuple tuple : tuples) {
            for (Map.Entry<SpecificOccurrenceId, Event> entries : tuple.getContext().entrySet()) {
                if (entries.getKey().equals(entry.getKey())) {
                    if (entries.getValue().equals(entry.getValue())) {
                        return new SpecificContextTuple(new SpecificOccurrence(entry.getKey(), entry.getValue()));
                    }
                    else {
                        return null;
                    }
                }
            }
        }
        return new SpecificContextTuple(new SpecificOccurrence(entry.getKey(), entry.getValue()));
    }

}
