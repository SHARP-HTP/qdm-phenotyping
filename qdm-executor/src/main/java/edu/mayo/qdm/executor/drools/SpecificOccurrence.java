package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;

/**
 */
public class SpecificOccurrence {

    private Event event;
    private SpecificOccurrenceId id;

    public SpecificOccurrence(String id, String constant, Event event) {
        this(new SpecificOccurrenceId(id, constant), event);
    }

    public SpecificOccurrence(SpecificOccurrenceId id, Event event) {
        this.event = event;
        this.id = id;
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
}
