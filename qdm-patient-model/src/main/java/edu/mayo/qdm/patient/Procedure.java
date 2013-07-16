package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Procedure extends Event {
    private static Logger logger = Logger.getLogger(Procedure.class);
  /*
     * For JSON only
     */
    private Procedure() {
        this(null);
    }

    public Procedure(Concept concept) {
        this(concept, null);
    }

    public Procedure(Concept concept, Date startDate) {
        this(concept, startDate, null);
    }

    public Procedure(Concept concept, Date startDate, Date endDate) {
        super(concept, startDate, endDate);
    }

    public String toString() {
        String displayStr = " code System: " + this.getConcept().getCodingScheme() + " lab code: "
                + this.getConcept().getCode() + " starting date: " + this.getStartDate() + " end date: " + this.getEndDate();
        return displayStr;
    }
}