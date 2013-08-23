package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;

/**
 */
public class SpecificOccurrence {

    private Event event;
    private String id;

    public SpecificOccurrence(String id, Event event) {
        this.event = event;
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public String getId() {
        return id;
    }

}
