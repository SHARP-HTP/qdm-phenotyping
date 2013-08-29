package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

import java.util.*;

/**
 */
public class SpecificContext {

    private String id;
    private Patient patient;

    private Map<SpecificOccurrenceId,Set<Event>> universe = new HashMap<SpecificOccurrenceId,Set<Event>>();

    private SpecificContextTuple anyTuple = new SpecificContextTuple();

    private Set<SpecificContextTuple> specificContextTuples = new HashSet<SpecificContextTuple>();

    public SpecificContext(String id, Patient patient, Map<SpecificOccurrenceId,Set<Event>> universe) {
        this.id = id;
        this.patient = patient;
        this.anyTuple = new SpecificContextTuple();
        this.specificContextTuples.add(this.anyTuple);
        this.universe = universe;
    }

    public SpecificContext(String id, Patient patient) {
        this.id = id;
        this.patient = patient;
        this.anyTuple = new SpecificContextTuple();
        this.specificContextTuples.add(this.anyTuple);
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

    public Map<SpecificOccurrenceId, Set<Event>> getUniverse() {
        return universe;
    }

    public void setUniverse(Map<SpecificOccurrenceId, Set<Event>> universe) {
        this.universe = universe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void add(List<SpecificOccurrence> occurrences){
        this.getSpecificContextTuples().remove(this.anyTuple);

        SpecificContextTuple tuple = new SpecificContextTuple();

        for(SpecificOccurrence occurrence : occurrences){
            for(Event event : occurrence.getEvents()){
                tuple.getContext().put(occurrence.getId(), new EventOrAny(event));
            }

            Set<SpecificOccurrenceId> universeKeys = this.universe.keySet();

            for (SpecificOccurrenceId key : universeKeys){
                if(! tuple.getContext().containsKey(key)){
                    tuple.getContext().put(key, new EventOrAny());
                }
            }
        }

        this.specificContextTuples.add(tuple);

        this.compact();
    }

    public void add(SpecificOccurrence occurrence){
        this.add(Arrays.asList(occurrence));
    }

    public SpecificContext negate() {
        SpecificContext c = new SpecificContext(this.id, this.patient, this.getUniverse());

        c.getSpecificContextTuples().clear();

        SpecificContextTuple t = new SpecificContextTuple();

        for (Map.Entry<SpecificOccurrenceId, Set<Event>> entry : this.universe.entrySet()){
            t.getContext().put(entry.getKey(), new EventOrAny());
        }
        c.getSpecificContextTuples().add(t);



        return c;
    }

    private Set<SpecificContextTuple> universeToTuples() {
        Set<SpecificContextTuple> returnSet = new HashSet<SpecificContextTuple>();

        for (Map.Entry<SpecificOccurrenceId, Set<Event>> entry : this.universe.entrySet()){
            for(Event event : entry.getValue()){
                Map<SpecificOccurrenceId,EventOrAny> map = new HashMap<SpecificOccurrenceId,EventOrAny>();
                map.put(entry.getKey(), new EventOrAny(event));
                returnSet.add(new SpecificContextTuple(map));
            }
        }

        return returnSet;
    }

    private Set<SpecificContextTuple> negate(Set<SpecificContextTuple> tuples) {
        Set<SpecificContextTuple> returnSet = new HashSet<SpecificContextTuple>();

        for (SpecificContextTuple tuple : tuples){
            SpecificContextTuple newTuple = new SpecificContextTuple();
            for (Map.Entry<SpecificOccurrenceId, EventOrAny> entry : tuple.getContext().entrySet()){
                if(entry.getValue().isAny()){
                    newTuple.getContext().put(entry.getKey(), null);
                } else {
                    throw new UnsupportedOperationException();
                }
            }
            returnSet.add(newTuple);
        }

        return returnSet;
    }

    public void addToUniverse(SpecificOccurrenceUniverse occurrence) {
        SpecificOccurrenceId id = occurrence.getId();

        this.universe.put(id, occurrence.getEvents());

        anyTuple.getContext().put(id, new EventOrAny());
    }

    private static class CompactHolder {
        private Set<Event> events = new HashSet<Event>();
        private int count = 0;
    }

    public void compact(){
        if(this.getSpecificContextTuples() == null){
            return;
        }

        Set<SpecificContextTuple> tuplesToRemove = new HashSet<SpecificContextTuple>();
        for (SpecificContextTuple tuple : this.getSpecificContextTuples()){
            Map<String,CompactHolder> holderMap = new HashMap<String,CompactHolder>();
            for (SpecificOccurrenceId id : tuple.getContext().keySet()){
                Event event = tuple.getContext().get(id).getEvent();
                if(event != null){
                    String constant = id.getConstant();
                    if(! holderMap.containsKey(constant)){
                       holderMap.put(constant, new CompactHolder());
                    }
                    CompactHolder holder = holderMap.get(constant);
                    holder.count++;
                    holder.events.add(event);

                }
            }

            for(CompactHolder holder : holderMap.values()){
                if(holder.count > holder.events.size()){
                    tuplesToRemove.add(tuple);
                }
            }

        }

        this.getSpecificContextTuples().removeAll(tuplesToRemove);
    }

}
