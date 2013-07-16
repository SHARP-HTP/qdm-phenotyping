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
    private Date startDate;
    private Date endDate;

    /*
     * For JSON only
     */
    private Medication() {
        super(null);
    }

    public Medication(Concept concept, Date startDate, Date endDate) {
        super(concept);
        this.startDate = startDate;
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
        String displayStr = " code System: " + this.getConcept().getCodingScheme() + " med code: "
                + this.getConcept().getCode() + " units: " + this.getUnits() + " value: " + this.getValue()
                + " starting date: " + this.getStartDate() + " end date: " + this.getEndDate();
        return displayStr;
    }
}