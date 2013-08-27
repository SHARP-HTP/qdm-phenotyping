package edu.mayo.qdm.executor.drools;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 */
public class SpecificOccurrenceId {

    private String id;
    private String constant;

    public SpecificOccurrenceId(String id, String constant) {
        this.id = id;
        this.constant = constant;
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
        return new EqualsBuilder()
          .append(id, that.id)
          .append(constant, that.constant)
          .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
          .append(id)
          .append(constant)
          .toHashCode();
    }
}
