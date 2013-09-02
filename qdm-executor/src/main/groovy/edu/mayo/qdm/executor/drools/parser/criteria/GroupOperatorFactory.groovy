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
                    insertLogical(new PreconditionResult("${fullJson.key}", \$p))
                    """
                }
            ] as Criteria
    }

    def RECENT = {
        fullJson, subsetOperator ->
            [
                firstOrRecent(fullJson, subsetOperator, "max", true),
                firstOrRecent(fullJson, subsetOperator, "max", false)
            ]
    }

    def FIRST = {
        fullJson, subsetOperator ->
            [
                firstOrRecent(fullJson, subsetOperator, "min", true),
                firstOrRecent(fullJson, subsetOperator, "min", false)
            ]
    }

    def firstOrRecent(fullJson, subsetOperator, minOrMax, initial){
        def json = fullJson.value

        def childCriteria = json.children_criteria.collect { "id == \"$it\"" }.join(" || ")
        def droolsString = """
        \$p : Patient ( )
        ${
            if(!initial){
            """\$oldResult : PreconditionResult(id == "${fullJson.key}", patient == \$p)"""
            } else {
                ""
            }
        }

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
                if(initial){
                """
                insert(new PreconditionResult("${fullJson.key}", \$p, \$specificEvent.event))
                """
                } else {
                """
                modify( \$oldResult ) { setEvent(\$specificEvent.event) }
                """
                }
            }
        ] as Criteria
    }
}
