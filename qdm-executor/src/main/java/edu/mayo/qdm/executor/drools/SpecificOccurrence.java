package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

import java.util.List;

/**
 */
public class SpecificOccurrence {

    private List<Event> event;
    private String id;
    private String constant;
    private Patient patient;

    public SpecificOccurrence(List<Event> event, String id, String constant, Patient patient) {
        this.event = event;
        this.id = id;
        this.constant = constant;
        this.patient = patient;
    }

    public List<Event> getEvent() {
        return event;
    }

    public String getId() {
        return id;
    }

    public String getConstant() {
        return constant;
    }

    public Patient getPatient() {
        return patient;
    }
}