package edu.mayo.qdm.drools.parser.criteria
/**
 */
class PhysicalExamFinding implements Criteria {

    def json
    def valueSetCodeResolver

    @Override
    def toDrools() {
        def valueSetOid = json.code_list_id
        def lowValue = json.value.low
        def highValue = json.value.high


        """
        droolsUtil.findMatches("$valueSetOid", \$p.getPhysicalExamFindings(),
            new Interval(${valueToString(lowValue)}, ${valueToString(highValue)}))).size() > 0
        """
    }

    def valueToString(value){
        value == null ? "null" : """new MeasurementValue(${value.value}, "${value.unit}", ${value['inclusive?']}"""
    }
}
