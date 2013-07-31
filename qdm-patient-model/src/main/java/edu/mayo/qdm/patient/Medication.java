package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Medication extends Event {
    private static Logger logger = Logger.getLogger(Medication.class);

    private double value;
    private String units;
    private MedicationStatus medicationStatus;

    /*
     * For JSON only
     */
    private Medication() {
        super(null,null,null);
    }

    public Medication(Concept concept, MedicationStatus medicationStatus, Date startDate, Date endDate) {
        super(concept, startDate, endDate);
        this.medicationStatus = medicationStatus;
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

    public MedicationStatus getMedicationStatus() {
        return medicationStatus;
    }

    public void setMedicationStatus(MedicationStatus medicationStatus) {
        this.medicationStatus = medicationStatus;
    }

    public String toString() {
        String displayStr = " code System: " + this.getConcept().getCodingScheme() + " med code: "
                + this.getConcept().getCode() + " units: " + this.getUnits() + " value: " + this.getValue()
                + " starting date: " + this.getStartDate() + " end date: " + this.getEndDate();
        return displayStr;
    }
}