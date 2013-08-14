package edu.mayo.qdm.executor.drools.parser.criteria
/**
 */
class Procedure extends AbstractBaseCriteria {

    @Override
    def getName() {
        "Procedure"
    }

    @Override
    def getCriteria() {
        def status = this.json.value.status
        if(status != null){
            """procedureStatus == ${toProcedureStatus(status)}"""
        } else {
            ""
        }
    }

    def toProcedureStatus(jsonStatus){
        switch(jsonStatus){
            case "ordered" : return "ProcedureStatus.ORDERED"
            case "performed" : return "ProcedureStatus.PERFORMED"
            case "applied" : return "ProcedureStatus.APPLIED"
            default: throw new RuntimeException("""Json Procedure Status: $jsonStatus not recognized.""")
        }
    }
}
