package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 */
public class SpecificContext {

    private String id;
    private Patient patient;

    private Map<SpecificOccurrenceId,Set<Event>> universe = new HashMap<SpecificOccurrenceId,Set<Event>>();

    private Set<SpecificContextTuple> specificContextTuples = new HashSet<SpecificContextTuple>();

    public SpecificContext(String id, Patient patient) {
        this.id = id;
        this.patient = patient;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Set<SpecificContextTuple> getSpecificContextTuples() {
        return specificContextTuples;
    }

    public void setSpecificContextTuples(Set<SpecificContextTuple> specificContextTuples) {
        this.specificContextTuples = specificContextTuples;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void add(SpecificOccurrence occurrence){
        this.specificContextTuples.add(new SpecificContextTuple(occurrence));
    }

    public boolean match(Set<SpecificContextTuple> tuples, boolean negation){
        Set<SpecificContextTuple> thisTuples;
        if(negation){
            thisTuples = this.negate(this.getSpecificContextTuples());
        } else {
            thisTuples = this.getSpecificContextTuples();
        }



        return false;
    }

    public SpecificContext negate() {
        SpecificContext c = new SpecificContext(this.id, this.patient);
        c.setSpecificContextTuples(this.negate(this.getSpecificContextTuples()));

        return c;
    }

    private Set<SpecificContextTuple> negate(Set<SpecificContextTuple> tuples) {
        Set<SpecificContextTuple> returnSet = new HashSet<SpecificContextTuple>();



        return returnSet;
    }

    private Set<SpecificContextTuple> getUniverse(){
        Set<SpecificContextTuple> returnSet = new HashSet<SpecificContextTuple>();


        return returnSet;
    }

    public void addToUniverse(SpecificOccurrence occurrence) {
        SpecificOccurrenceId id = occurrence.getId();
        if(! this.universe.containsKey(id)){
            this.universe.put(id, new HashSet<Event>());
        }

        this.universe.get(id).add(occurrence.getEvent());
    }
}
