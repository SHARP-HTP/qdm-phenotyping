package edu.mayo.qdm.drools.parser.criteria

import edu.mayo.qdm.patient.Value

/**
 */
class MeasurementValue extends Value {

    boolean inclusive

    public MeasurementValue(String value, String unit, boolean inclusive) {
        super(value, unit)
        this.inclusive = inclusive
    }

}
