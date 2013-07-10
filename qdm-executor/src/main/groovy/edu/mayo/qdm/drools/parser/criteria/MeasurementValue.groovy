package edu.mayo.qdm.drools.parser.criteria
/**
 */
class MeasurementValue {
    float value
    String unit
    boolean inclusive

    public MeasurementValue(float value, String unit, boolean inclusive) {
        this.value = value
        this.unit = unit
        this.inclusive = inclusive
    }

}
