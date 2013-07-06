package edu.mayo.qdm.patient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Medication {
    private static Logger logger = Logger.getLogger(Medication.class);

    private String medicationCode;
    private String codingSystem;
    private double value;
    private String units;
    private Date startingDate;
    private Date endDate;

    /*
     * For JSON only
     */
    private Medication() {
        super();
    }

    public Medication(String medicationCode, String codingSystem, Date startingDate) {

        if (StringUtils.isEmpty(medicationCode)
                || StringUtils.isEmpty(codingSystem)
                ) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Medication.constructor() - Constructor arguments cannot be null or empty.");
        }
        this.medicationCode = medicationCode;
        this.codingSystem = codingSystem;
        this.startingDate = startingDate;
    }

    public Medication(String medicationCode, String codingSystem, Date startingDate, Date endDate) {

        if (StringUtils.isEmpty(medicationCode)
                || StringUtils.isEmpty(codingSystem)
                || startingDate == null
                || endDate == null
                ) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Medication.constructor() - Constructor arguments cannot be null or empty.");
        }
        this.medicationCode = medicationCode;
        this.codingSystem = codingSystem;
        this.startingDate = startingDate;
        this.endDate = endDate;
    }

    public Medication(String term, String medicationCode, String codingSystem, Date startingDate, Date endDate) {

        if (StringUtils.isEmpty(medicationCode)
                || StringUtils.isEmpty(codingSystem)
                || startingDate == null
                || endDate == null
                ) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Medication.constructor() - Constructor arguments cannot be null or empty.");
        }
        this.medicationCode = medicationCode;
        this.codingSystem = codingSystem;
        this.startingDate = startingDate;
        this.endDate = endDate;
    }

    public String getmedicationCode() {
        return medicationCode;
    }

    public String getCodingSystem() {
        return this.codingSystem;
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
        String displayStr = " code System: " + this.getCodingSystem() + " med code: "
                + this.getmedicationCode() + " units: " + this.getUnits() + " value: " + this.getValue()
                + " starting date: " + this.getStartingDate() + " end date: " + this.getEndDate();
        return displayStr;
    }
}