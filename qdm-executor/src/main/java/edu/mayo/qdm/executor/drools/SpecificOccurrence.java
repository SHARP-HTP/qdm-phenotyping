package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 */
public class SpecificOccurrence {

    private Set<Event> events;
    private SpecificOccurrenceId id;

    public SpecificOccurrence(String id, String constant, Event event) {
        this(new SpecificOccurrenceId(id, constant), new HashSet<Event>(Arrays.asList(event)));
    }

    public SpecificOccurrence(String id, String constant, Set<Event> events) {
        this(new SpecificOccurrenceId(id, constant), events);
    }

    public SpecificOccurrence(SpecificOccurrenceId id, Set<Event> events) {
        this.events = events;
        this.id = id;
    }

    public SpecificOccurrenceId getId() {
        return id;
    }

    public void setId(SpecificOccurrenceId id) {
        this.id = id;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }
}
