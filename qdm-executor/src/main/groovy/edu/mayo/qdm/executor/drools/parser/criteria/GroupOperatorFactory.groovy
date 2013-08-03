package edu.mayo.qdm.executor.drools.parser.criteria

/**
 * Processes Precondition Group Operations, such as COUNTing, RECENT events, etc.
 */
class GroupOperatorFactory {

    def COUNT = {
        json, subsetOperator ->
            def low = subsetOperator.value.low
            def high = subsetOperator.value.high

            def countCriteria = []
            if(low){
                countCriteria << """ size >${low."inclusive?" ? "=" : ""} ${low.value}"""
            }
            if(high){
                countCriteria << """ size <${low."inclusive?" ? "=" : ""} ${high.value}"""
            }
            def droolsString = """
            \$events : Set( ${countCriteria.join(",")} ) from collect ( PreconditionResult (
                     (${json.children_criteria.collect{"id == \"$it\""}.join(" || ")}),
                     patient == \$p))
            """
            [
                toDrools:{droolsString},
                hasEventList:{false},
                isPatientCriteria:{false}
            ] as Criteria
    }

    def RECENT = {
        json, subsetOperator ->
        throw new UnsupportedOperationException("`RECENT` Subset Operator not implemented.")
    }
}
