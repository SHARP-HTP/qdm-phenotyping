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
        if(this.json.value.value){
            def criteria = ""
            switch (this.json.value.value.type){
                case "CD" :
                    def valueSetOid = this.json.value.value.code_list_id

                    criteria =
                    """
                    droolsUtil.contains("$valueSetOid", this.results)
                    """
                    break
                case "ANYNonNull" :
                    criteria =
                    """
                    (
                        ( values != null && values.size() > 0 )
                        ||
                        ( results != null && results.size() > 0 )
                    )
                    """
                    break
                case "IVL_PQ" :
                    eventCriteria =
                    """
                    edu.mayo.qdm.patient.Value(
                        ${IVL_PQ(this.json.value.value, "java.text.NumberFormat.getInstance().parse(value)")}
                    ) from \$event.values
                    """
                    break
                default : throw new UnsupportedOperationException(
                    """`CD, ANYNonNull` types are the only supported value for a Diagnostic Study.
                            Found a `${this.json.value.value.type}`
                    """)

            }

            criteria
        }

    }

    def IVL_PQ(json, prop){
        def clauses = []
        if(json.low){
            def op = (json.low.'inclusive?') ? ">=" : ">"
            clauses << """$prop $op ${json.low.value}"""
        }
        if(json.high){
            def op = (json.high.'inclusive?') ? "<=" : "<"
            clauses << """$prop $op ${json.high.value}"""
        }

        clauses.join("\n")
    }

    def getEventCriteria() {
        eventCriteria
    }
}

