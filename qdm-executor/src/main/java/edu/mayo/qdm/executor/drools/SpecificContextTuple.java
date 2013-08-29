package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class SpecificContextTuple {

    private Map<SpecificOccurrenceId,EventOrAny> context = new HashMap<SpecificOccurrenceId,EventOrAny>();

    public SpecificContextTuple(){
        super();
    }

    public SpecificContextTuple(Map<SpecificOccurrenceId,EventOrAny> context){
        super();
        this.context = context;
    }

    public SpecificContextTuple(SpecificOccurrenceId id, Event event){
        super();
        this.context.put(id, new EventOrAny(event));
    }

    public Map<SpecificOccurrenceId, EventOrAny> getContext() {
        return context;
    }

    public void setContext(Map<SpecificOccurrenceId, EventOrAny> context) {
        this.context = context;
    }


}
