package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class SpecificContextTuple {

    private Map<SpecificOccurrenceId,Event> context = new HashMap<SpecificOccurrenceId,Event>();

    public SpecificContextTuple(){
        super();
    }

    public SpecificContextTuple(SpecificOccurrence occurrence){
        super();
        this.context.put(occurrence.getId(), occurrence.getEvent());
    }

    public Map<SpecificOccurrenceId, Event> getContext() {
        return context;
    }

    public void setContext(Map<SpecificOccurrenceId, Event> context) {
        this.context = context;
    }


}
