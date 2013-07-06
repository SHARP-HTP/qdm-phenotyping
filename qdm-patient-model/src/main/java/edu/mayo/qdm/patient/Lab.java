package edu.mayo.qdm.patient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Lab {
    private static Logger logger = Logger.getLogger(Lab.class);

    private String labResultCode;
    private String codingSystem;
    private String labTerm;
    private Date specimenCollectionDate;
    private double value;
    private String units;

    /*
     * For JSON only
     */
    private Lab() {
        super();
    }

    public Lab(String labResultCode, String labTerm, String codingSystem, Date specimenCollectionDate, double value, String units) {

        if (StringUtils.isEmpty(labResultCode)
                || StringUtils.isEmpty(codingSystem)
                //|| specimenCollectionDate == null
                || StringUtils.isEmpty(units)) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Lab.constructor() - Constructor arguments cannot be null or empty.");
        }

        this.labResultCode = labResultCode;
        this.codingSystem = codingSystem;
        this.labTerm = labTerm;
        this.specimenCollectionDate = specimenCollectionDate;
        this.value = value;
        this.units = units;
    }

    public Lab(String labResultCode, String codingSystem, Date specimenCollectionDate, double value, String units) {

        if (StringUtils.isEmpty(labResultCode)
                || StringUtils.isEmpty(codingSystem)
                //|| specimenCollectionDate == null
                || StringUtils.isEmpty(units)) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Lab.constructor() - Constructor arguments cannot be null or empty.");
        }

        this.labResultCode = labResultCode;
        this.codingSystem = codingSystem;
        this.specimenCollectionDate = specimenCollectionDate;
        this.value = value;
        this.units = units;
    }

    public Lab(String labResultCode, String codingSystem, Date specimenCollectionDate, double value) {

        if (StringUtils.isEmpty(labResultCode)
                || StringUtils.isEmpty(codingSystem)
                ) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Lab.constructor() - Constructor arguments cannot be null or empty.");
        }

        this.labResultCode = labResultCode;
        this.codingSystem = codingSystem;
        this.specimenCollectionDate = specimenCollectionDate;
        this.value = value;
    }

    public Lab(String labResultCode, String codingSystem, double value) {

        if (StringUtils.isEmpty(labResultCode)
                || StringUtils.isEmpty(codingSystem)
                ) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Lab.constructor() - Constructor arguments cannot be null or empty.");
        }

        this.labResultCode = labResultCode;
        this.codingSystem = codingSystem;
        this.value = value;
    }

    public Lab(String labResultCode, String codingSystem, Date specimenCollectionDate) {

        if (StringUtils.isEmpty(labResultCode)
                || StringUtils.isEmpty(codingSystem)
                || specimenCollectionDate == null) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Lab.constructor() - Constructor arguments cannot be null or empty.");
        }

        this.labResultCode = labResultCode;
        this.codingSystem = codingSystem;
        this.specimenCollectionDate = specimenCollectionDate;
    }

    /**
     * @return labResultCode;
     */
    public String getLabResultCode() {
        return labResultCode;
    }

    /**
     * @return codingSystem
     */
    public String getCodingSystem() {
        return this.codingSystem;
    }

    public String getLabTerm() {
        return this.labTerm;
    }

    public Date getSpecimenCollectionDate() {
        return specimenCollectionDate;
    }

    public double getValue() {
        return value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public void setSpecimenCollectionDate(Date collectDate) {
        this.specimenCollectionDate = collectDate;
    }

    public String toString() {
        String displayStr = " code System: " + this.getCodingSystem() + " lab code: " + this.getLabResultCode() + " units: " + this.getUnits() + " value: " + this.getValue() + " specimenCollectionDate: " + this.getSpecimenCollectionDate();
        return displayStr;
    }
}