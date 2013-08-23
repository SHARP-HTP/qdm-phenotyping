package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 */
public class Universe {

    private Patient patient;

    private Map<String,Set<Event>> specificContext = new HashMap<String,Set<Event>>();

    public Universe(Patient patient, Map<String, Set<Event>> specificContext) {
        this.patient = patient;
        this.specificContext = specificContext;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Map<String, Set<Event>> getSpecificContext() {
        return specificContext;
    }

    public void setSpecificContext(Map<String, Set<Event>> specificContext) {
        this.specificContext = specificContext;
    }
}
