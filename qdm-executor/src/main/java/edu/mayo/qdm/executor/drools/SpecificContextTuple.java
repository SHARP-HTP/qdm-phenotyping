package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class SpecificContextTuple {

    private Map<SpecificOccurrenceId,Event> context = new HashMap<SpecificOccurrenceId,Event>();

    public SpecificContextTuple(){
        super();
    }

    public SpecificContextTuple(SpecificOccurrence occurrence){
        super();
        this.context.put(occurrence.getId(), occurrence.getEvent());
    }

    public Map<SpecificOccurrenceId, Event> getContext() {
        return context;
    }

    public void setContext(Map<SpecificOccurrenceId, Event> context) {
        this.context = context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpecificContextTuple that = (SpecificContextTuple) o;
        if (context.size() == that.context.size()) {
            EqualsBuilder eb =  new EqualsBuilder();
            for (Map.Entry<SpecificOccurrenceId, Event> entries : context.entrySet()) {
                eb.append(that.context.containsKey(entries.getKey()) && that.context.get(entries.getKey()).equals(entries.getValue()), true);
            }
            return eb.isEquals();
        } else return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
          .append(context)
          .toHashCode();
    }

}
