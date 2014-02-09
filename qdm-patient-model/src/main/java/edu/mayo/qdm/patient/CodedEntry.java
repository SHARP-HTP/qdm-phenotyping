package edu.mayo.qdm.patient;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 */
public class CodedEntry implements Serializable {

    private Set<Concept> concepts = new HashSet<Concept>();

    /*
     * For JSON Only
     */
    private CodedEntry(){
        super();
    }

    protected CodedEntry(Concept concept){
        super();
        this.concepts.add(concept);
    }

    protected CodedEntry(Set<Concept> concepts){
        super();
        this.concepts.addAll(concepts);
    }

    public boolean matches(Concept concept){
        for(Concept c : this.concepts){
            if(c.matches(concept)){
                return true;
            }
        }
        return false;
    }

    public Set<Concept> getConcepts() {
        return concepts;
    }
}
