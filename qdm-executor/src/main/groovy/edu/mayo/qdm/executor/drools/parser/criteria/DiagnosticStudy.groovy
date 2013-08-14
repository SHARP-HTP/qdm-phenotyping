package edu.mayo.qdm.executor.drools.parser.criteria
/**
 */
class DiagnosticStudy extends AbstractBaseCriteria {

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

        result.criteria
    }

    def getEventCriteria() {
        eventCriteria
    }
}

