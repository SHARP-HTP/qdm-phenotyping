package edu.mayo.qdm.drools.parser.criteria
/**
 */
class PhysicalExamFinding extends AbstractBaseCriteria {

    @Override
    def getName() {
        "PhysicalExamFinding"
    }

    //new Interval(${valueToString(lowValue)}, ${valueToString(highValue)}))
    def valueToString(value){
        value == null ? "null" : """new MeasurementValue(${value.value}, "${value.unit}", ${value['inclusive?']}"""
    }

}
