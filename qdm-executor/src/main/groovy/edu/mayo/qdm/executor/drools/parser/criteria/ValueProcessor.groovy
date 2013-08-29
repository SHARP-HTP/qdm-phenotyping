package edu.mayo.qdm.executor.drools.parser.criteria

/**
 */
class ValueProcessor {

    class ValuleProcessorResult {
        def criteria
        def eventCriteria
    }

    def getValueCriteria(json) {
        def eventCriteria = ""
        def criteria = ""
        if(json.value.value){
            switch (json.value.value.type){
                case "CD" :
                    def valueSetOid = json.value.value.code_list_id

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
                    criteria =
                        """
                        values != null
                        """
                    eventCriteria =
                        """
                        ${IVL_PQ(json.value.value, "java.text.NumberFormat.getInstance().parse(values[0].value)")}
                    """
                    break
                default : throw new UnsupportedOperationException(
                        """`CD, ANYNonNull, IVL_PQ` types are the only supported value for a Diagnostic Study.
                            Found a `${json.value.value.type}`
                    """)

            }

        }

        new ValuleProcessorResult(criteria: criteria, eventCriteria: eventCriteria)
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
}
