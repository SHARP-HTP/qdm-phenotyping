package edu.mayo.qdm.patient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author m048100
 */
public class Exception {
    private static Logger logger = Logger.getLogger(Exception.class);

    private String patExceptionCode;
    private String codingSystem;
    private Date startingDate;
    private Date endDate;

    /*
     * For JSON only
     */
    private Exception() {
        super();
    }

    public Exception(String patExceptionCode, String codingSystem, Date startingDate) {

        if (StringUtils.isEmpty(patExceptionCode)
                || StringUtils.isEmpty(codingSystem)
                ) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Lab.constructor() - Constructor arguments cannot be null or empty.");
        }

        this.patExceptionCode = patExceptionCode;
        this.codingSystem = codingSystem;
        this.startingDate = startingDate;
    }

    public Exception(String patExceptionCode, String codingSystem, Date startingDate, Date endDate) {

        if (StringUtils.isEmpty(patExceptionCode)
                || StringUtils.isEmpty(codingSystem)
                ) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Lab.constructor() - Constructor arguments cannot be null or empty.");
        }

        this.patExceptionCode = patExceptionCode;
        this.codingSystem = codingSystem;
        this.startingDate = startingDate;
        this.endDate = endDate;
    }

    public Exception(String patExceptionCode, String codingSystem) {

        if (StringUtils.isEmpty(patExceptionCode)
                || StringUtils.isEmpty(codingSystem)
                ) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Lab.constructor() - Constructor arguments cannot be null or empty.");
        }

        this.patExceptionCode = patExceptionCode;
        this.codingSystem = codingSystem;
    }

    public String getPatExceptionCode() {
        return patExceptionCode;
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
        String displayStr = " code System: " + this.getCodingSystem() + " lab code: " + this.getPatExceptionCode()
                + "starting date: " + this.getStartingDate() + " end Date: " + this.getEndDate();
        return displayStr;
    }
}