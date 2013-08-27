package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpecificOccurrence that = (SpecificOccurrence) o;
        return new EqualsBuilder()
          .append(event, that.event)
          .append(id, that.id)
          .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
          .append(event)
          .append(id)
          .toHashCode();
    }
}
