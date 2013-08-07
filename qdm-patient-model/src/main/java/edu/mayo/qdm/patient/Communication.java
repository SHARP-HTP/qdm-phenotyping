package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

public class Communication extends Event {
    private static Logger logger = Logger.getLogger(Communication.class);

    private Value value;

    /*
     * For JSON only
     */
    private Communication() {
        super(null,null,null);
    }

    public Communication(Concept concept, Date date) {
        super(concept, date);
    }

    public Communication(Concept concept, Date startDate, Date endDate) {
        super(concept, startDate, endDate);
        this.value = value;
    }
}