package edu.mayo.qdm.patient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;

/**
 */
public class Event extends CodedEntry {

    private Date startDate;
    private Date endDate;
    private boolean negated = false;

    public Event(Concept concept, Date date){
        this(concept, date, date);
    }

    public Event(Concept concept, Date startDate, Date endDate){
        super(concept);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event that = (Event) o;
        return new EqualsBuilder()
          .appendSuper(super.equals(that))
          .append(startDate, that.startDate)
          .append(endDate, that.endDate)
          .append(negated, that.negated)
          .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
          .appendSuper(super.hashCode())
          .append(startDate)
          .append(endDate)
          .append(negated)
          .toHashCode();
    }
}
