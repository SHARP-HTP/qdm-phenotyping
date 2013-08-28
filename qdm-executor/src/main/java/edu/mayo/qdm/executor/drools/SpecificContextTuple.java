package edu.mayo.qdm.executor.drools;

import java.util.HashMap;
import java.util.List;
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

    public SpecificContextTuple(SpecificOccurrence occurrence){
        super();
        this.context.put(occurrence.getId(), new EventOrAny(occurrence.getEvent()));
    }

    public SpecificContextTuple(List<SpecificOccurrence> occurrences) {
        super();
        for (SpecificOccurrence occurrence : occurrences){
            context.put(occurrence.getId(), new EventOrAny(occurrence.getEvent()));
        }
    }

    public Map<SpecificOccurrenceId, EventOrAny> getContext() {
        return context;
    }

    public void setContext(Map<SpecificOccurrenceId, EventOrAny> context) {
        this.context = context;
    }


}
