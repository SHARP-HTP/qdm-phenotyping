package edu.mayo.qdm.executor.drools;

/**
 */
public class SpecificOccurrenceId {

    private String id;
    private String constant;

    public SpecificOccurrenceId(String constant, String id) {
        this.constant = constant;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpecificOccurrenceId that = (SpecificOccurrenceId) o;

        if (!constant.equals(that.constant)) return false;
        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + constant.hashCode();
        return result;
    }
}
