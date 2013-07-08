package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class RiskCategoryAssessment extends CodedEntry {
    private static Logger logger = Logger.getLogger(RiskCategoryAssessment.class);

    private Date date;

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
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String toString() {
        String displayStr = " code System: " + this.getConcept().getCodingScheme() + " code: "
                + this.getConcept().getCode() + " date: " + this.getDate();
        return displayStr;
    }
}