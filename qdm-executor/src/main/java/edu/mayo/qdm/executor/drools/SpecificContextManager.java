package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

import java.util.*;

/**
 */
public class SpecificContextManager {

    private Object mutex = new Object();

    private Map<Key,Set<SpecificContextTuple>> specificContexts = new HashMap<Key,Set<SpecificContextTuple>>();

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

        Set<SpecificContextTuple> returnSet = new HashSet<SpecificContextTuple>(tuples1);



        return returnSet;
    }


    private static Set<SpecificContextTuple> findTuplesWithMatch(Set<SpecificContextTuple> tuples, String key, Event event){
        Set<SpecificContextTuple> returnSet = new HashSet<SpecificContextTuple>();

        for(SpecificContextTuple tuple : tuples){
            if(! tuple.getContext().containsKey(key) ||
                    tuple.getContext().get(key) == event){
                returnSet.add(tuple);
            }
        }

        return returnSet;
    }


    private static final class Key {
        private Patient p;
        private String id;

        private Key(Patient p, String id) {
            this.p = p;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (id != null ? !id.equals(key.id) : key.id != null) return false;
            if (p != null ? !p.equals(key.p) : key.p != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = p != null ? p.hashCode() : 0;
            result = 31 * result + (id != null ? id.hashCode() : 0);
            return result;
        }
    }
}
