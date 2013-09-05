package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;

/**
 */
public class SpecificOccurrence {

    private Event event;
    private SpecificOccurrenceId id;
    private String constant;

    public SpecificOccurrence(String constant, String id, Event event) {
        this.id = new SpecificOccurrenceId(constant, id);
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public SpecificOccurrenceId getId() {
        return id;
    }

    public void setId(SpecificOccurrenceId id) {
        this.id = id;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

}
