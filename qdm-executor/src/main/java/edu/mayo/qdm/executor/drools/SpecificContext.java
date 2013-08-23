package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Patient;

import java.util.HashSet;
import java.util.Set;

/**
 */
public class SpecificContext {

    private String id;
    private Patient patient;

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
        if(! negation){
            for (SpecificContextTuple tuple : tuples){
                for (SpecificContextTuple innerTuple : this.getSpecificContextTuples()){
                    if(tuple.isMatch(innerTuple)){
                        return true;
                    }
                }
            }

            return false;
        } else {
            if(this.getSpecificContextTuples().size() == 0|| tuples.size() == 0){
                return true;
            }
            for (SpecificContextTuple tuple : tuples){
                for (SpecificContextTuple innerTuple : this.getSpecificContextTuples()){
                    if(! tuple.isMatch(innerTuple)){
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
