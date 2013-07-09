package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Diagnosis extends CodedEntry {
    private static Logger logger = Logger.getLogger(Diagnosis.class);

    private Date startingDate;
    private Date endDate;

    /*
     * For JSON only
     */
    private Diagnosis() {
        super(null);
    }

    public Diagnosis(Concept concept) {
        this(concept, null);
    }

    public Diagnosis(Concept concept, Date startingDate) {
        this(concept, startingDate, null);
    }

    public Diagnosis(Concept concept, Date startingDate, Date endDate) {
        super(concept);
        this.startingDate = startingDate;
        this.endDate = endDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String toString() {
        String displayStr = " code System: " + this.getConcept().getCodingScheme() + " lab code: "
                + this.getConcept().getCode() + " starting date: " + this.getStartingDate() + " end date: " + this.getEndDate();
        return displayStr;
    }
}