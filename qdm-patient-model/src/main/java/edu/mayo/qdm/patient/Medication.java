package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Medication extends CodedEntry {
    private static Logger logger = Logger.getLogger(Medication.class);

    private double value;
    private String units;
    private Date startingDate;
    private Date endDate;

    /*
     * For JSON only
     */
    private Medication() {
        super(null);
    }

    public Medication(Concept concept, Date startingDate, Date endDate) {
        super(concept);
        this.startingDate = startingDate;
        this.endDate = endDate;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
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
        String displayStr = " code System: " + this.getConcept().getCodingScheme() + " med code: "
                + this.getConcept().getCode() + " units: " + this.getUnits() + " value: " + this.getValue()
                + " starting date: " + this.getStartingDate() + " end date: " + this.getEndDate();
        return displayStr;
    }
}