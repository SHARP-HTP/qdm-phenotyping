package edu.mayo.qdm.patient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Dingcheng Li
 */
public class Symptom {
    private static Logger logger = Logger.getLogger(Symptom.class);

    private String symptomCode;
    private String codingSystem;
    private double value;
    private String units;
    private Date startingDate;
    private Date endDate;

    /*
     * For JSON only
     */
    private Symptom() {
        super();
    }

    public Symptom(String symptomCode, String codingSystem, Date startingDate) {

        if (StringUtils.isEmpty(symptomCode)
                || StringUtils.isEmpty(codingSystem)) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Medication.constructor() - Constructor arguments cannot be null or empty.");
        }
        this.symptomCode = symptomCode;
        this.codingSystem = codingSystem;
        this.startingDate = startingDate;
    }

    public Symptom(String symptomCode, String codingSystem, Date startingDate, Date endDate) {

        if (StringUtils.isEmpty(symptomCode)
                || StringUtils.isEmpty(codingSystem)
                || startingDate == null
                || endDate == null
                ) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Medication.constructor() - Constructor arguments cannot be null or empty.");
        }
        this.symptomCode = symptomCode;
        this.codingSystem = codingSystem;
        this.startingDate = startingDate;
        this.endDate = endDate;
    }

    public Symptom(String term, String symptomCode, String codingSystem, Date startingDate, Date endDate) {

        if (StringUtils.isEmpty(symptomCode)
                || StringUtils.isEmpty(codingSystem)
                || startingDate == null
                || endDate == null
                ) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Medication.constructor() - Constructor arguments cannot be null or empty.");
        }
        this.symptomCode = symptomCode;
        this.codingSystem = codingSystem;
        this.startingDate = startingDate;
        this.endDate = endDate;
    }

    public String getSymptomCode() {
        return symptomCode;
    }

    public String getCodingSystem() {
        return this.codingSystem;
    }

    public double getValue() {
        return value;
    }

    public String getUnits() {
        return units;
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
        String displayStr = " code System: " + this.getCodingSystem() + " symptom code: "
                + this.getSymptomCode() + "starting date: " + this.getStartingDate() + " end date: " + this.getEndDate();
        return displayStr;
    }
}