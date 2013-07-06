package edu.mayo.qdm.patient;

public class Concept {

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