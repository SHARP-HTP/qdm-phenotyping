package edu.mayo.qdm.executor.drools.parser.criteria

import edu.mayo.qdm.patient.Value

/**
 */
class Interval {
    MeasurementValue lowValue
    MeasurementValue highValue

    public Interval(MeasurementValue lowValue, MeasurementValue highValue) {
        this.lowValue = lowValue
        this.highValue = highValue
    }

    boolean satisfied(Value value) {
        def satisfied = true;
        if(lowValue != null){
            if(lowValue.inclusive){
                satisfied &= (lowValue.value <= value.value)
            } else {
                satisfied &= (lowValue.value < value.value)
            }
        }
        if(highValue != null){
            if(highValue.inclusive){
                satisfied &= (highValue.value >= value.value)
            } else {
                satisfied &= (highValue.value > value.value)
            }
        }

        satisfied
    }

    MeasurementValue getLowValue() {
        return lowValue
    }

    void setLowValue(MeasurementValue lowValue) {
        this.lowValue = lowValue
    }

    MeasurementValue getHighValue() {
        return highValue
    }

    void setHighValue(MeasurementValue highValue) {
        this.highValue = highValue
    }
}
