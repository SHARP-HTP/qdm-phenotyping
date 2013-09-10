package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class PhysicalExamFinding extends Event {
    private static Logger logger = Logger.getLogger(PhysicalExamFinding.class);

    private Value value;

    /*
     * For JSON only
     */
    private PhysicalExamFinding() {
        super(null,null);
    }

    public PhysicalExamFinding(Concept concept, Value value) {
        this(concept, value, null);
    }

    public PhysicalExamFinding(Concept concept, Value value, Date date) {
        this(concept, value, date, date);
    }

    public PhysicalExamFinding(Concept concept, Value value, Date startDate, Date endDate) {
        super(concept, startDate, endDate);
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

}