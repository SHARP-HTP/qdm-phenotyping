package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Procedure extends CodedEntry {
    private static Logger logger = Logger.getLogger(Procedure.class);

    private Date startDate;
    private Date endDate;

    /*
     * For JSON only
     */
    private Procedure() {
        super(null);
    }

    public Procedure(Concept concept) {
        this(concept, null);
    }

    public Procedure(Concept concept, Date startDate) {
        this(concept, startDate, null);
    }

    public Procedure(Concept concept, Date startDate, Date endDate) {
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

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String toString() {
        String displayStr = " code System: " + this.getConcept().getCodingScheme() + " lab code: "
                + this.getConcept().getCode() + " starting date: " + this.getStartDate() + " end date: " + this.getEndDate();
        return displayStr;
    }
}