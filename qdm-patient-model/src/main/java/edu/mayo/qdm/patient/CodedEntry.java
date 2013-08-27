package edu.mayo.qdm.patient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 */
public class CodedEntry {

    private Concept concept;

    /*
     * For JSON Only
     */
    private CodedEntry(){
        super();
    }

    protected CodedEntry(Concept concept){
        super();
        this.concept = concept;
    }

    public boolean matches(Concept concept){
        return this.getConcept().matches(concept);
    }

    public Concept getConcept() {
        return concept;
    }

    protected void setConcept(Concept concept) {
        this.concept = concept;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return new EqualsBuilder()
          .append(concept, ((CodedEntry) o).concept)
          .isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
          .append(concept)
          .toHashCode();
    }
}
