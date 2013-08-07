package edu.mayo.qdm.executor.drools.parser.criteria
/**
 * Processes Precondition Group Operations, such as COUNTing, RECENT events, etc.
 */
class GroupOperatorFactory {

    def COUNT = {
        fullJson, subsetOperator ->
            def json = fullJson.value

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
            \$p : Patient ( )
            \$events : Set( ${countCriteria.join(",")} ) from collect ( PreconditionResult (
                     (${json.children_criteria.collect{"id == \"$it\""}.join(" || ")}),
                     patient == \$p))
            """
            [
                getLHS:{droolsString},
                getRHS:{
                    """
                    insert(new PreconditionResult("${fullJson.key}", \$p))
                    """
                }
            ] as Criteria
    }

    def RECENT = {
        fullJson, subsetOperator ->
            firstOrRecent(fullJson, subsetOperator, "max")
    }

    def FIRST = {
        fullJson, subsetOperator ->
            firstOrRecent(fullJson, subsetOperator, "min")
    }

    def firstOrRecent(fullJson, subsetOperator, minOrMax){
        def json = fullJson.value

        def childCriteria = json.children_criteria.collect { "id == \"$it\"" }.join(" || ")
        def droolsString = """
        \$p : Patient ( )
        \$m : Number() from accumulate(
                PreconditionResult(
                    patient == \$p,
                    ($childCriteria),
                    event.startDate != null,
                    \$startDate : event.startDate ),
                $minOrMax( \$startDate.time ) )

        \$specificEvent : PreconditionResult(
            patient == \$p,
            ($childCriteria),
            event.startDate == new java.util.Date(\$m) )

        """
        [
            getLHS:{droolsString},
            getRHS:{
                """
                insert(new PreconditionResult("${fullJson.key}", \$p, \$specificEvent.event))
                """
            }
        ] as Criteria
    }
}
