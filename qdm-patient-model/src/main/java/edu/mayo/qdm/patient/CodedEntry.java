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

    protected boolean matches(Concept concept){
        if(! concept.getCode().equals(this.concept.getCode())){
            return false;
        }

        if(concept.getCodingScheme() != null
            && this.concept.getCodingScheme() != null){
            if(! concept.getCodingScheme().equals(this.concept.getCodingScheme())){
                return false;
            }
        }

        if(concept.getCodingSchemeVersion() != null
                && this.concept.getCodingSchemeVersion() != null){
            if(! concept.getCodingSchemeVersion().equals(this.concept.getCodingSchemeVersion())){
                return false;
            }
        }

        return true;
    }

    public Concept getConcept() {
        return concept;
    }

    protected void setConcept(Concept concept) {
        this.concept = concept;
    }
}
