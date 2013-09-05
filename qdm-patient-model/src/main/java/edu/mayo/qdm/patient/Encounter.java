package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Set;


/**
 * @author m048100
 *         I am not sure if encounterId and encounterCode are the same thing. So, I keep encounteId for the moment to represent encounterCode.
 *         In cemDb, they always call encounterID. But it looks like that encounterId is what I mean encounterCode. Yet, from Herm, we can
 *         see that they also add some icd9 code to it for confirm that the encounter is what we want though according to QDM, it seems
 *         that this is not really necessary.
 */
public class Encounter extends Event {

    private static Logger logger = Logger.getLogger(Encounter.class);

    private String encounterId;

    /*
     * For JSON only
     */
    private Encounter() {
        super(null,null);
    }

    public Encounter(String encounterId, Concept concept, Date date) {
        this(encounterId, concept, date, date);
    }

    public Encounter(String encounterId, Concept concept, Date startDate, Date endDate) {
        super(concept, startDate, endDate);
        this.encounterId = encounterId;
    }

    public Encounter(String encounterId, Set<Concept> concepts, Date startDate, Date endDate) {
        super(concepts, startDate, endDate);
        this.encounterId = encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public String getEncounterId() {
        return this.encounterId;
    }

}