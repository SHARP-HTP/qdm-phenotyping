package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class PhysicalExamFinding extends CodedEntry {
    private static Logger logger = Logger.getLogger(PhysicalExamFinding.class);

    private Date date;
    private Value value;

    /*
     * For JSON only
     */
    private PhysicalExamFinding() {
        super(null);
    }

    public PhysicalExamFinding(Concept concept, Value value) {
        this(concept, null, value);
    }

    public PhysicalExamFinding(Concept concept, Date date, Value value) {
        super(concept);
        this.date = date;
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
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