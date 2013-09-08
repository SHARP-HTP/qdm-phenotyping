package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Herman and Darin IHC
 */
public class DiagnosticStudy extends Event {
    private static Logger logger = Logger.getLogger(DiagnosticStudy.class);

    private Set<Concept> results = new HashSet<Concept>();
    private Set<Value> values = new HashSet<Value>();
    private Concept reason;

    /*
     * For JSON only
     */
    private DiagnosticStudy() {
        super(null, null);
    }

    public DiagnosticStudy(Concept concept, Date startDate, Date endDate) {
        super(concept, startDate, endDate);
    }

    public DiagnosticStudy(Set<Concept> concepts, Date startDate, Date endDate) {
        super(concepts, startDate, endDate);
    }

    public Concept getReason() {
        return reason;
    }

    public void setReason(Concept reason) {
        this.reason = reason;
    }

    public Set<Concept> getResults() {
        return results;
    }

    public Set<Value> getValues() {
        return values;
    }
}