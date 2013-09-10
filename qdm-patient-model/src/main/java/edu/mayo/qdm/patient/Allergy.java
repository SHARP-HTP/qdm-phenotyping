package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Allergy extends Event {
    private static Logger logger = Logger.getLogger(Allergy.class);

    /*
     * For JSON only
     */
    private Allergy() {
        super(null,null);
    }

    public Allergy(Concept concept) {
        this(concept, null);
    }

    public Allergy(Concept concept, Date startingDate) {
        this(concept, startingDate, null);
    }

    public Allergy(Concept concept, Date startingDate, Date endDate) {
        super(concept, startingDate, endDate);
    }

}