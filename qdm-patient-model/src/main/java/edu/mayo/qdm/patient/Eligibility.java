package edu.mayo.qdm.patient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Eligibility {
    private static Logger logger = Logger.getLogger(Eligibility.class);

    private String eligibilityCode;
    private String codingSystem;
    private Date startingDate;
    private Date endDate;

    /*
     * For JSON only
     */
    private Eligibility() {
        super();
    }

    public Eligibility(String eligibilityCode, String codingSystem, Date startingDate, Date endDate) {

        if (StringUtils.isEmpty(eligibilityCode)
                || StringUtils.isEmpty(codingSystem)
                || startingDate == null
                || endDate == null) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Lab.constructor() - Constructor arguments cannot be null or empty.");
        }

        this.eligibilityCode = eligibilityCode;
        this.codingSystem = codingSystem;
        this.startingDate = startingDate;
        this.endDate = endDate;
    }

    public String getEligibilityCode() {
        return eligibilityCode;
    }

    public String getCodingSystem() {
        return this.codingSystem;
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
        String displayStr = " code System: " + this.getCodingSystem()
                + " eligibility code: " + this.getEligibilityCode()
                + " startDate: " + this.getStartingDate() + " endDate: " + this.getEndDate();
        return displayStr;
    }
}