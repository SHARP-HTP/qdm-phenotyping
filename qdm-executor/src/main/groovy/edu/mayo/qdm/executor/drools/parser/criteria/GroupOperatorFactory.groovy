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
                countCriteria << """ this >${low."inclusive?" ? "=" : ""} ${low.value}"""
            }
            if(high){
                countCriteria << """ this <${low."inclusive?" ? "=" : ""} ${high.value}"""
            }
            def droolsString = """
            \$p : Patient ( )
            \$result : PreconditionResult( id == "${fullJson.key}", patient == \$p )

            \$events : Set( ) from collect(
                PreconditionResult( (${json.children_criteria.collect {"""id == "$it" """}.join(" || ")}), patient == \$p ) )

            Number( ${countCriteria.join(",")} ) from droolsUtil.countEvents(\$events)
            """
            [
                getLHS:{droolsString},
                getRHS:{
                    """modify(\$result) {
                        status = PreconditionResultStatus.SUCCESS
                    }
                    """
                }
            ] as Criteria
    }

    def RECENT = {
        fullJson, subsetOperator ->
            [
                firstOrRecent(fullJson, subsetOperator, "max"),
            ]
    }

    def FIRST = {
        fullJson, subsetOperator ->
            [
                firstOrRecent(fullJson, subsetOperator, "min"),
            ]
    }

    def firstOrRecent(fullJson, subsetOperator, minOrMax){
        def json = fullJson.value

        def childCriteria = json.children_criteria
        def droolsString = """
        \$p : Patient( )

        \$result : PreconditionResult( id == "${fullJson.key}", patient == \$p )

        \$results : Set() from collect(
            PreconditionResult( (${childCriteria.collect {"id == \"$it\""}.join(" || ")}), patient == \$p ) )

        \$total : Set() from collect( Event() from droolsUtil.combineEvents(\$results))

        \$m : Number() from accumulate(
                    Event(
                        startDate != null,
                        \$startDate : startDate ) from \$total,
                $minOrMax( \$startDate.time ) )

        \$specificEvent : Event( startDate == new java.util.Date(\$m) ) from \$total

        """
        [
            getLHS:{droolsString},
            getRHS:{
               """modify(\$result){
                    setEvent(\$specificEvent),
                    status = PreconditionResultStatus.SUCCESS
               }
               """
            }
        ] as Criteria
    }
}
