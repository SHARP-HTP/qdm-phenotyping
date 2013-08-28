package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author Herman and Darin IHC
 */
public class Procedure extends Event {
    private static Logger logger = Logger.getLogger(Procedure.class);

    private ProcedureStatus procedureStatus;

    /*
     * For JSON only
     */
    private Procedure() {
        this(null, null);
    }

    public Procedure(Concept concept, ProcedureStatus procedureStatus) {
        this(concept, procedureStatus, null);
    }

    public Procedure(Concept concept, ProcedureStatus procedureStatus, Date startDate) {
        this(concept, procedureStatus, startDate, startDate);

    }

    public Procedure(Concept concept, ProcedureStatus procedureStatus, Date startDate, Date endDate) {
        super(concept, startDate, endDate);
        this.procedureStatus = procedureStatus;
    }

    public ProcedureStatus getProcedureStatus() {
        return procedureStatus;
    }

    public void setProcedureStatus(ProcedureStatus procedureStatus) {
        this.procedureStatus = procedureStatus;
    }

}