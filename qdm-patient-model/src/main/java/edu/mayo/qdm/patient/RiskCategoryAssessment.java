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
        this(concept, date, date);
    }

    public RiskCategoryAssessment(Concept concept, Date startDate, Date endDate) {
        super(concept, startDate, endDate);
    }

    @Override
    public Date getEndDate() {
        return super.getEndDate();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Date getStartDate() {
        return super.getStartDate();    //To change body of overridden methods use File | Settings | File Templates.
    }


}