package edu.mayo.qdm.patient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Dingcheng Li adapated from Herman and Darin IHC
 */
public class Problem {
    private static Logger logger = Logger.getLogger(Problem.class);

    private String problemCode;
    private String codingSystem;
    private Date startingDate;
    private Date endDate;

    /*
     * For JSON only
     */
    private Problem() {
        super();
    }

    /**
     * @param problemCode
     * @param codingSystem we may not know the starting date.
     */
    public Problem(String problemCode, String codingSystem) {

        if (StringUtils.isEmpty(problemCode)
                || StringUtils.isEmpty(codingSystem)) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Problem.constructor() - Constructor arguments cannot be null or empty.");
        }

        this.problemCode = problemCode;
        this.codingSystem = codingSystem;
    }

    /**
     * @param problemCode
     * @param codingSystem
     * @param startingDate
     */
    public Problem(String problemCode, String codingSystem, Date startingDate) {

        if (StringUtils.isEmpty(problemCode)
                || StringUtils.isEmpty(codingSystem)) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Lab.constructor() - Constructor arguments cannot be null or empty.");
        }

        this.problemCode = problemCode;
        this.codingSystem = codingSystem;
        this.startingDate = startingDate;
    }

    /**
     * @param problemCode
     * @param codingSystem
     * @param startingDate
     */
    public Problem(String problemCode, String codingSystem, Date startingDate, Date endDate) {

        if (StringUtils.isEmpty(problemCode)
                || StringUtils.isEmpty(codingSystem)
                || startingDate == null
                || endDate == null) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Lab.constructor() - Constructor arguments cannot be null or empty.");
        }

        this.problemCode = problemCode;
        this.codingSystem = codingSystem;
        this.startingDate = startingDate;
        this.endDate = endDate;
    }

    public String getproblemCode() {
        return problemCode;
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
        String displayStr = " code System: " + this.getCodingSystem() + " problem code: " + this.getproblemCode()
                + " starting date: " + this.getStartingDate() + " end date: " + this.getEndDate();
        return displayStr;
    }
}