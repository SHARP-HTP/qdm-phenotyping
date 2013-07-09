package edu.mayo.qdm.patient;

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
}
