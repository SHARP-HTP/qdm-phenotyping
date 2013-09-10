package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

public class Characteristic extends Event {
    private static Logger logger = Logger.getLogger(Characteristic.class);

    private Value value;

    /*
     * For JSON only
     */
    private Characteristic() {
        super(null,null);
    }

    public Characteristic(Concept concept, Date date) {
        super(concept, date);
    }

    public Characteristic(Concept concept, Date startDate, Date endDate) {
        super(concept, startDate, endDate);
        this.value = value;
    }
}