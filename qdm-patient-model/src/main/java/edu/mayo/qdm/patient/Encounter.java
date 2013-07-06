package edu.mayo.qdm.patient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Date;


/**
 * @author m048100
 *         I am not sure if encounterId and encounterCode are the same thing. So, I keep encounteId for the moment to represent encounterCode.
 *         In cemDb, they always call encounterID. But it looks like that encounterId is what I mean encounterCode. Yet, from Herm, we can
 *         see that they also add some icd9 code to it for confirm that the encounter is what we want though according to QDM, it seems
 *         that this is not really necessary.
 */
public class Encounter extends CodedEntry {

    private static Logger logger = Logger.getLogger(Encounter.class);

    private String encounterId;
    private Date visitDate;

    /*
     * For JSON only
     */
    private Encounter() {
        super(null);
    }

    public Encounter(String encounterId, Concept concept, Date visitDate) {
        super(concept);

        if (StringUtils.isEmpty(encounterId) || visitDate == null) {
            logger.debug("Constructor arguments cannot be null or empty.");
            throw new IllegalArgumentException("Encounter.ctor() - Constructor arguments cannot be null or empty.");
        }

        this.encounterId = encounterId;
        this.visitDate = visitDate;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public String getEncounterId() {
        return this.encounterId;
    }

    public void setVisistDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public Date getVisitDate() {
        return this.visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String toString() {
        String displayStr = " code System: " + this.getConcept().getCodingScheme() + " encounter code: " + this.getEncounterId() + " visit date: " + this.getVisitDate();

        return displayStr;
    }

}