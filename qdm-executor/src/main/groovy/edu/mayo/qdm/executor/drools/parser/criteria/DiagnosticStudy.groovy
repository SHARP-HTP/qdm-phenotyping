package edu.mayo.qdm.executor.drools.parser.criteria
/**
 */
class DiagnosticStudy extends AbstractBaseCriteria {

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
            if(this.json.value.value.type != "CD"){
                throw new UnsupportedOperationException(
                        """`CD` type is the only supported value for a Diagnostic Study.
                            Found a `${this.json.value.value.type}`
                        """)
            }

            def valueSetOid = this.json.value.value.code_list_id

            """
            droolsUtil.contains("$valueSetOid", this.results)
            """
        }

    }
}

