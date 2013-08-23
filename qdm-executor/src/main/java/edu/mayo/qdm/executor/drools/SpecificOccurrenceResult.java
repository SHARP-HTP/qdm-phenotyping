package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;

/**
 */
public class SpecificOccurrenceResult {

    private Event event;
    private String id;

    public SpecificOccurrenceResult(String id, Event event) {
        this.event = event;
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
