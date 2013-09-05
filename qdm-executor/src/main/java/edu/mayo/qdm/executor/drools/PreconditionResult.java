package edu.mayo.qdm.executor.drools;

import com.google.common.collect.Sets;
import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PreconditionResult {

    private boolean isPopulation = false;
	private String id;
	private Patient patient;
    private Event event;
    private Map<SpecificOccurrenceId,Event> context = new HashMap<SpecificOccurrenceId,Event>();

    public PreconditionResult(String id, Patient patient) {
        super();
        this.id = id;
        this.patient = patient;
    }

    public PreconditionResult(String id, Patient patient, boolean isPopulation) {
        super();
        this.id = id;
        this.patient = patient;
        this.isPopulation = isPopulation;
    }

    public PreconditionResult(String id, Patient patient, Event event) {
        super();
        this.id = id;
        this.patient = patient;
        this.event = event;
    }

    public PreconditionResult(String id, Patient patient, Map<SpecificOccurrenceId,Event> context) {
        super();
        this.id = id;
        this.patient = patient;
        this.context = context;
    }


    public PreconditionResult(String id, Patient patient, Event event, Map<SpecificOccurrenceId,Event> context) {
        super();
        this.id = id;
        this.patient = patient;
        this.event = event;
        this.context = context;
    }

    public PreconditionResult(String id, Patient patient, Event event, SpecificOccurrence specificOccurrence) {
        this(id, patient, event, Arrays.asList(specificOccurrence));
    }

    public PreconditionResult(String id, Patient patient, Event event, Collection<SpecificOccurrence> specificOccurrences) {
        super();
        this.id = id;
        this.patient = patient;
        this.event = event;
        if(specificOccurrences != null){
            for (SpecificOccurrence specificOccurrence : specificOccurrences){
                this.context.put(specificOccurrence.getId(), specificOccurrence.getEvent());
            }
        }
    }

    public Map<SpecificOccurrenceId,Event> intersect(Map<SpecificOccurrenceId,Event> context){
        return context;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Map<SpecificOccurrenceId, Event> getContext() {
        return context;
    }

    public void setContext(Map<SpecificOccurrenceId, Event> context) {
        this.context = context;
    }

    public boolean compatible(Map<SpecificOccurrenceId,Event> context){
        Sets.SetView<SpecificOccurrenceId> common = Sets.intersection(context.keySet(), this.context.keySet());

        for (SpecificOccurrenceId element : common){
            Event e1 = context.get(element);
            Event e2 = this.context.get(element);

            if(e1 == null || e2 == null || (e1 != e2)){
                return false;
            }
        }

        return true;
    }

    public boolean isPopulation() {
        return isPopulation;
    }

    public void setPopulation(boolean population) {
        isPopulation = population;
    }
}
