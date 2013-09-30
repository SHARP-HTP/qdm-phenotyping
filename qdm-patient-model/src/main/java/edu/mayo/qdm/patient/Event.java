package edu.mayo.qdm.patient;

import java.util.Date;
import java.util.Set;

/**
 * A {@link CodedEntry} with a specific temporal range.
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

    public Event(Set<Concept> concepts, Date startDate, Date endDate){
        super(concepts);
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
}
