package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Patient;
import org.springframework.util.Assert;

import java.util.*;

/**
 */
public class SpecificContextManager {

    private Object mutex = new Object();

    private Map<Key,Set<SpecificContextTuple>> specificContexts = new HashMap<Key,Set<SpecificContextTuple>>();

    public SpecificContext union(Patient p, String id, List<SpecificContext> contexts){
        Set<SpecificContextTuple> returnSet = new HashSet<SpecificContextTuple>();

        for (SpecificContext context : contexts){
            returnSet.addAll(context.getSpecificContextTuples());
        }

        SpecificContext c = new SpecificContext(id, p, contexts.get(0).getUniverse());
        c.setSpecificContextTuples(returnSet);
        return c;
    }

    public SpecificContext intersect(Patient p, String id, List<SpecificContext> contexts){
       if(contexts.size() == 0){
           throw new IllegalStateException();
       }
        if(contexts.size() == 1){
            return contexts.get(0);
        }

        Set<SpecificContextTuple> currentContext = contexts.get(0).getSpecificContextTuples();

        for(SpecificContext context : contexts.subList(1, contexts.size() - 1)){
            currentContext = this.intersect(currentContext, context.getSpecificContextTuples());
        }

        SpecificContext newContext = new SpecificContext(id, p, contexts.get(0).getUniverse());
        newContext.setSpecificContextTuples(currentContext);

        return newContext;
    }

    private Set<SpecificContextTuple> intersect(Set<SpecificContextTuple> tuples1, Set<SpecificContextTuple> tuples2) {
        Set<SpecificContextTuple> returnSet = new HashSet<SpecificContextTuple>();

        for (SpecificContextTuple tuple : tuples1){
            for (SpecificContextTuple inner : tuples2){

                Map<SpecificOccurrenceId, EventOrAny> result = doIntersect(tuple.getContext(), inner.getContext());
                if(result != null && result.size() > 0){
                    returnSet.add(new SpecificContextTuple(result));
                }
            }
        }

        return returnSet;
    }

    private Map<SpecificOccurrenceId, EventOrAny> doIntersect(Map<SpecificOccurrenceId, EventOrAny> context1, Map<SpecificOccurrenceId, EventOrAny> context2){
        Assert.isTrue(context1.size() == context2.size());

        Map<SpecificOccurrenceId, EventOrAny> returnContext = new HashMap<SpecificOccurrenceId, EventOrAny>();

        for (SpecificOccurrenceId key : context1.keySet()){
            EventOrAny event1 = context1.get(key);
            EventOrAny event2 = context2.get(key);

            EventOrAny intersectionEventOrAny = event1.intersect(event2);
            if(intersectionEventOrAny != null){
                returnContext.put(key, intersectionEventOrAny);
            } else {
                return null;
            }
        }

        return returnContext;
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
