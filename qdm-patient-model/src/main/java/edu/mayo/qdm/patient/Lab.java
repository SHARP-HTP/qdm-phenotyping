package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Lab extends CodedEntry {
    private static Logger logger = Logger.getLogger(Lab.class);

    private Value value;
    private Date startDate;
    private Date endDate;

    /*
     * For JSON only
     */
    private Lab() {
        super(null);
    }

    public Lab(Concept concept, Value value, Date startDate, Date endDate) {
        super(concept);
        this.value = value;
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

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public String toString() {
        String displayStr = " code System: " + this.getConcept().getCodingScheme() + " lab code: " + this.getConcept().getCode();
        return displayStr;
    }
}