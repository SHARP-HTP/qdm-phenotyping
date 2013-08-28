package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;

import java.util.Set;

/**
 */
public class SpecificOccurrenceUniverse {

    private Set<Event> events;
    private SpecificOccurrenceId id;

    public SpecificOccurrenceUniverse(String id, String constant, Set<Event> events) {
        this(new SpecificOccurrenceId(id, constant), events);
    }

    public SpecificOccurrenceUniverse(SpecificOccurrenceId id, Set<Event> events) {
        this.events = events;
        this.id = id;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public SpecificOccurrenceId getId() {
        return id;
    }

    public void setId(SpecificOccurrenceId id) {
        this.id = id;
    }
}
