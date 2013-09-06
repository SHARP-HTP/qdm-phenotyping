package edu.mayo.qdm.patient;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Herman and Darin IHC
 */
public class Lab extends Event {
    private static Logger logger = Logger.getLogger(Lab.class);

    private Set<Concept> results = new HashSet<Concept>();
    private Set<Value> values = new HashSet<Value>();

    /*
     * For JSON only
     */
    private Lab() {
        super(null,null);
    }

    public Lab(Concept concept, Value value, Date date) {
        this(concept, value, date, date);
    }

    public Lab(Concept concept, Value value, Date startDate, Date endDate) {
        super(concept, startDate, endDate);
        if(value != null){
            this.values.add(value);
        }
    }

    public Set<Concept> getResults() {
        return results;
    }

    public void setResults(Set<Concept> results) {
        this.results = results;
    }

    public Set<Value> getValues() {
        return values;
    }

    public void setValues(Set<Value> values) {
        this.values = values;
    }

}