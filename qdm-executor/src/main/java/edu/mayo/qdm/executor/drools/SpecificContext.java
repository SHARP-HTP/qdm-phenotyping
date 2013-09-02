package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 */
public class SpecificContext {

    private String id;
    private Patient patient;

    private Map<String,Set<Event>> universe = new HashMap<String,Set<Event>>();

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

    public void add(SpecificOccurrenceResult occurrence){
        this.specificContextTuples.add(new SpecificContextTuple(occurrence));
    }

    public boolean match(Set<SpecificContextTuple> tuples, boolean negation){
        Set<SpecificContextTuple> thisTuples;
        if(negation){
            thisTuples = this.negate(this.getSpecificContextTuples());
        } else {
            thisTuples = this.getSpecificContextTuples();
        }

        for (SpecificContextTuple tuple : tuples){
            for (SpecificContextTuple innerTuple : thisTuples){
                if(tuple.isMatch(innerTuple)){
                    return true;
                }
            }
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

        for(SpecificContextTuple universeTuple : this.getUniverse()){
            if(CollectionUtils.isEmpty(tuples)){
                returnSet.add(universeTuple);
            } else {
                for(SpecificContextTuple negated : tuples){
                    if(! universeTuple.isMatch(negated)){
                        returnSet.add(universeTuple);
                        returnSet.add(negated);
                    }
                }
            }
        }

        return returnSet;
    }

    private Set<SpecificContextTuple> getUniverse(){
        Set<SpecificContextTuple> returnSet = new HashSet<SpecificContextTuple>();

        for(String key : this.universe.keySet()){
            for(Event event : this.universe.get(key)){
                SpecificContextTuple t = new SpecificContextTuple();
                t.getContext().put(key, event);

                returnSet.add(t);
            }

        }

        return returnSet;
    }

    public void addUniverse(SpecificOccurrence a) {
        //universe.put(a.getId(), new HashSet<Event>(a.getEvents()));
    }
}
