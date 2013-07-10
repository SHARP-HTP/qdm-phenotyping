package edu.mayo.qdm.patient;

/**
 */
public class Value {

    private float value;
    private String unit;

    public Value(float value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
