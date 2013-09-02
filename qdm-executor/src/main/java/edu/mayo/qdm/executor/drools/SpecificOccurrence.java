package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

/**
 */
public class SpecificOccurrence {

    private Event event;
    private String id;
    private String constant;
    private Patient patient;

    public SpecificOccurrence(String constant, String id, Event event, Patient patient) {
        this.patient = patient;
        this.constant = constant;
        this.id = id;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
