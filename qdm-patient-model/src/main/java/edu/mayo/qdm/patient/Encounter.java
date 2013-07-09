package edu.mayo.qdm.patient;

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
    private Date startDate;
    private Date endDate;

    /*
     * For JSON only
     */
    private Encounter() {
        super(null);
    }

    public Encounter(String encounterId, Concept concept, Date startDate, Date endDate) {
        super(concept);
        this.encounterId = encounterId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public String getEncounterId() {
        return this.encounterId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String toString() {
        String displayStr = " code System: " + this.getConcept().getCodingScheme() + " encounter code: " + this.getEncounterId();

        return displayStr;
    }

}