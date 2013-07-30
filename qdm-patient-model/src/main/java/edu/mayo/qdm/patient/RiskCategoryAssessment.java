package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class RiskCategoryAssessment extends Event {
    private static Logger logger = Logger.getLogger(RiskCategoryAssessment.class);

    /*
     * For JSON only
     */
    private RiskCategoryAssessment() {
        super(null,null,null);
    }

    public RiskCategoryAssessment(Concept concept, Date date) {
        super(concept, date);
    }

    public RiskCategoryAssessment(Concept concept, Date startDate, Date endDate) {
        super(concept, startDate, endDate);
    }

    public String toString() {
        String displayStr = " code System: " + this.getConcept().getCodingScheme() + " code: "
                + this.getConcept().getCode() + " date: " + this.getStartDate();
        return displayStr;
    }
}