package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class SpecificContextTuple {

    private Map<String,Event> context = new HashMap<String,Event>();

    public SpecificContextTuple(){
        super();
    }

    public SpecificContextTuple(SpecificOccurrenceResult result){
        super();
        this.context.put(result.getId(), result.getEvent());
    }

    public Map<String, Event> getContext() {
        return context;
    }

    public void setContext(Map<String, Event> context) {
        this.context = context;
    }

    public boolean isMatch(SpecificContextTuple tuple){
        if(tuple.getContext().keySet().size() != this.context.keySet().size()){
            throw new IllegalStateException();
        }

        for (String key : tuple.getContext().keySet()){
            if(! this.context.containsKey(key)){
                throw new IllegalStateException();
            }

            Event a = tuple.getContext().get(key);
            Event b = this.context.get(key);

            if(a == null || b == null || a == b){
                return true;
            }
        }
        return false;
    }
}
