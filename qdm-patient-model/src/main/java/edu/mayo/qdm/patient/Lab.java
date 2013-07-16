package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Lab extends Event {
    private static Logger logger = Logger.getLogger(Lab.class);

    private Value value;

    /*
     * For JSON only
     */
    private Lab() {
        super(null,null,null);
    }

    public Lab(Concept concept, Value value, Date startDate, Date endDate) {
        super(concept, startDate, endDate);
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public String toString() {
        String displayStr = " code System: " + this.getConcept().getCodingScheme() + " lab code: " + this.getConcept().getCode();
        return displayStr;
    }
}