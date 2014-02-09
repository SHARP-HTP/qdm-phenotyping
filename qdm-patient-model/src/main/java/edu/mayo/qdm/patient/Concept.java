package edu.mayo.qdm.patient;

import java.io.Serializable;

public class Concept implements Serializable {

    private String code;
    private String codingScheme;
    private String codingSchemeVersion;

    /*
     * For JSON Only
     */
    private Concept(){
        super();
    }

    public Concept(String code, String codingScheme, String codingSchemeVersion) {
        this.code = code;
        this.codingScheme = codingScheme;
        this.codingSchemeVersion = codingSchemeVersion;
    }

    public boolean matches(Concept concept){
        if(! concept.getCode().equals(this.getCode())){
            return false;
        }

        if(concept.getCodingScheme() != null
                && this.getCodingScheme() != null){
            if(! concept.getCodingScheme().equals(this.getCodingScheme())){
                return false;
            }
        }

        if(concept.getCodingSchemeVersion() != null
                && this.getCodingSchemeVersion() != null){
            if(! concept.getCodingSchemeVersion().equals(this.getCodingSchemeVersion())){
                return false;
            }
        }

        return true;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodingScheme() {
        return codingScheme;
    }

    public void setCodingScheme(String codingScheme) {
        this.codingScheme = codingScheme;
    }

    public String getCodingSchemeVersion() {
        return codingSchemeVersion;
    }

    public void setCodingSchemeVersion(String codingSchemeVersion) {
        this.codingSchemeVersion = codingSchemeVersion;
    }
}