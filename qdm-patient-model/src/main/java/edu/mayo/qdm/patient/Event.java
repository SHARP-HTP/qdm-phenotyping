package edu.mayo.qdm.patient;

import java.util.Date;

/**
 */
public class Event extends CodedEntry {

    private Date startDate;
    private Date endDate;

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
}
