package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class RiskCategoryAssessment extends CodedEntry {
    private static Logger logger = Logger.getLogger(RiskCategoryAssessment.class);

    private Date startDate;
    private Date endDate;

    /*
     * For JSON only
     */
    private RiskCategoryAssessment() {
        super(null);
    }

    public RiskCategoryAssessment(Concept concept) {
        this(concept, null);
    }

    public RiskCategoryAssessment(Concept concept, Date date) {
        super(concept);
        this.startDate = date;
        this.endDate = date;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String toString() {
        String displayStr = " code System: " + this.getConcept().getCodingScheme() + " code: "
                + this.getConcept().getCode() + " date: " + this.getStartDate();
        return displayStr;
    }
}