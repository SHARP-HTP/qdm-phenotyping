package edu.mayo.qdm.patient;

/**
 */
public class Value {

    private String value;
    private String unit;

    public Value(String value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Value value1 = (Value) o;

        if (unit != null ? !unit.equals(value1.unit) : value1.unit != null) return false;
        if (value != null ? !value.equals(value1.value) : value1.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }
}
