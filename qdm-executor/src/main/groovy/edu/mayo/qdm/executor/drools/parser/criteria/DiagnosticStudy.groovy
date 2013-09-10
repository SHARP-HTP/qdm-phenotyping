package edu.mayo.qdm.executor.drools.parser.criteria
/**
 */
class DiagnosticStudy extends DefaultCriteria {

    def eventCriteria = ""

    @Override
    def getName() {
        "DiagnosticStudy"
    }

    def getPluralName(){
        "DiagnosticStudies"
    }

    @Override
    def getCriteria() {
        def result = valueProcessor.getValueCriteria(this.json)
        eventCriteria = result.eventCriteria

        def reasonCriteria
        def reason = this.json.value.field_values?.REASON
        if(reason){
            if(reason.type != "CD"){
                throw new UnsupportedOperationException("Can only process `CD` types for Study Reason.")
            }
            reasonCriteria = """droolsUtil.matches("2.16.840.1.113883.3.526.3.320", reason)"""
        }

        """
        ${[reasonCriteria,result.criteria].findAll().join("\n") }
        """
        if(reasonCriteria){
            [reasonCriteria, result.criteria].findAll().join(",\n")
        } else {
            result.criteria
        }

    }

    def getEventCriteria() {
        eventCriteria
    }
}

