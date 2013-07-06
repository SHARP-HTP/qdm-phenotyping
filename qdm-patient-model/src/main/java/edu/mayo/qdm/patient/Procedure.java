package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Procedure extends CodedEntry {
    private static Logger logger = Logger.getLogger(Procedure.class);

    private String procedureCode;
    private String codingSystem;
    private Date startingDate;
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

    public Procedure(Concept concept, Date startingDate) {
        this(concept, startingDate, null);
    }

    public Procedure(Concept concept, Date startingDate, Date endDate) {
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