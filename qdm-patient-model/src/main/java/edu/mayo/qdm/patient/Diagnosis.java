package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Diagnosis extends Event {
    private static Logger logger = Logger.getLogger(Diagnosis.class);

    /*
     * For JSON only
     */
    private Diagnosis() {
        super(null,null);
    }

    public Diagnosis(Concept concept) {
        this(concept, null);
    }

    public Diagnosis(Concept concept, Date startingDate) {
        this(concept, startingDate, null);
    }

    public Diagnosis(Concept concept, Date startingDate, Date endDate) {
        super(concept, startingDate, endDate);
    }

}