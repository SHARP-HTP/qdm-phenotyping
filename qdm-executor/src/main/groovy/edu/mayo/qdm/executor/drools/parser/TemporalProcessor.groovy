package edu.mayo.qdm.executor.drools.parser

import groovy.util.logging.Log4j
import org.apache.commons.lang.BooleanUtils
/**
 * Processes QDM/HQMF Temporal relationships such as SAS, EBE, etc.
 */
@Log4j
class TemporalProcessor {

    class TemporalResult{
        def variables = []
        String criteria
    }

    def processTemporalReferences(temporalReferences, measureJson, startProperty="startDate", endProperty="endDate"){
        def result = temporalReferences.collect(processTemporalReference.rcurry(measureJson, startProperty, endProperty))

        def variables = result.collect { it.variables }.flatten()

        new TemporalResult(
            variables: variables,
            criteria: result.inject('') { str, element -> str + (element.criteria ?: '') }
        )
    }

    def processTemporalReference = {temporalReference, measureJson, startProperty, endProperty ->
        def type = temporalReference.type

        if(this.hasProperty(type)){
            this."$type"(temporalReference, startProperty, endProperty, measureJson)
        } else {
            throw new RuntimeException("Temporal Reference Type `$type` not recognized\n. JSON ->  $temporalReference")
        }
    }

    def CONCURRENT = {temporalReference, startProperty, endProperty, measureJson ->
        def reference = temporalReference.reference

        def lowTime
        def highTime
        if(reference == "MeasurePeriod"){
            lowTime = """toDays(new Date(parseLong(measurementPeriod.lowValue.value)))"""
            highTime = """toDays(new Date(parseLong(measurementPeriod.highValue.value)))"""
        } else {
            lowTime = """$startProperty == \$${reference}.event.startDate"""
            highTime = """$endProperty == \$${reference}.event.endDate"""
        }
        return new TemporalResult(
                variables: reference,
                criteria:
                        """
        $lowTime,
        $highTime
        """)
    }

    def DURING = {temporalReference, startProperty, endProperty, measureJson ->
        def start = SAS(temporalReference, startProperty, endProperty, measureJson, true)
        def end = EBE(temporalReference, startProperty, endProperty, measureJson, true)

        if(temporalReference.reference == "MeasurePeriod"){
            return new TemporalResult(
                    variables: start.variables,
                    criteria: " this during \$measurementPeriod ")
        } else {
            return new TemporalResult(
                    variables: start.variables,
                    criteria:
            """
            $start.criteria,
            $end.criteria
            """)
        }
    }

    def EDU = {temporalReference, startProperty, endProperty, measureJson ->
        def endAfterStart = EAS(temporalReference, startProperty, endProperty, measureJson, true)
        def endBeforeEnd = EBE(temporalReference, startProperty, endProperty, measureJson, true)

        return new TemporalResult(
                variables: endAfterStart.variables,
                criteria:
                        """
        $endAfterStart.criteria,
        $endBeforeEnd.criteria
        """)
    }

    def SDU = {temporalReference, startProperty, endProperty, measureJson ->
        def startAfterStart = SAS(temporalReference, startProperty, endProperty, measureJson, true)
        def startBeforeEnd = SBE(temporalReference, startProperty, endProperty, measureJson, true)

        return new TemporalResult(
                variables: startAfterStart.variables,
                criteria:
        """
        $startAfterStart.criteria,
        $startBeforeEnd.criteria
        """)
    }

    def EAE = {temporalReference, startProperty, endProperty, measureJson ->
        return this.temporalReference(temporalReference, endProperty, measureJson, EndOrStart.END, BeforeOrAfter.AFTER)
    }

    def EBE = {temporalReference, startProperty, endProperty, measureJson, forceInclusive=false ->
        return this.temporalReference(temporalReference, endProperty, measureJson, EndOrStart.END, BeforeOrAfter.BEFORE, forceInclusive)
    }

    def EBS = {temporalReference, startProperty, endProperty, measureJson ->
        return this.temporalReference(temporalReference, endProperty, measureJson, EndOrStart.START, BeforeOrAfter.BEFORE)
    }

    def EAS = {temporalReference, startProperty, endProperty, measureJson, forceInclusive=false ->
        return this.temporalReference(temporalReference, endProperty, measureJson, EndOrStart.START, BeforeOrAfter.AFTER, forceInclusive)
    }

    def SAS = {temporalReference, startProperty, endProperty, measureJson, forceInclusive=false ->
        return this.temporalReference(temporalReference, startProperty, measureJson, EndOrStart.START, BeforeOrAfter.AFTER, forceInclusive)
    }

    def SAE = {temporalReference, startProperty, endProperty, measureJson ->
        return this.temporalReference(temporalReference, startProperty, measureJson, EndOrStart.END, BeforeOrAfter.AFTER)
    }

    def SBS = {temporalReference, startProperty, endProperty, measureJson ->
        return this.temporalReference(temporalReference, startProperty, measureJson, EndOrStart.START, BeforeOrAfter.BEFORE)
    }

    def SBE = {temporalReference, startProperty, endProperty, measureJson, forceInclusive=false  ->
        return this.temporalReference(temporalReference, startProperty, measureJson, EndOrStart.END, BeforeOrAfter.BEFORE, forceInclusive)
    }

    enum EndOrStart {END,START}
    enum BeforeOrAfter {BEFORE,AFTER}

    def temporalReference(temporalReference, property, measureJson, EndOrStart endOrStart, BeforeOrAfter beforeOrAfter, forceInclusive=false){
        def reference = temporalReference.reference

        if(reference == "MeasurePeriod"){
            def sb = new StringBuilder()

            def time
            switch (endOrStart){
                case EndOrStart.START:
                    time = "new Date(parseLong(measurementPeriod.lowValue.value))"
                    break
                case EndOrStart.END:
                    time = "new Date(parseLong(measurementPeriod.highValue.value))"
                    break
            }

            if(temporalReference.range){
                def range = temporalReference.range

                def highOp
                def lowOp
                def minusOrPlus
                switch (beforeOrAfter){
                    case BeforeOrAfter.BEFORE:
                        highOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '>' : '>'}
                        lowOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '<' : '<'}
                        minusOrPlus = "-"
                        break

                    case BeforeOrAfter.AFTER:
                        lowOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '>' : '>'}
                        highOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '<' : '<'}
                        minusOrPlus = ""
                        break
                }

                if(range.high){
                    def calendarType = toCalendarType(range.high.unit)
                    def value = adjustForInclusive(range.high.value, range.high.'inclusive?')
                    sb.append """
                        ${property} != null,
                        toDays(${property}) ${highOp(range.high)} toDays(droolsUtil.add(droolsUtil.getCalendar(${time}), Calendar.$calendarType, $minusOrPlus$value)),
                        """
                }
                if(range.low){
                    def calendarType = toCalendarType(range.low.unit)
                    def value = range.low.value
                    sb.append """
                        ${property} != null,
                        toDays(${property}) ${lowOp(range.low)} toDays(droolsUtil.add(droolsUtil.getCalendar(${time}), Calendar.$calendarType, $minusOrPlus$value))
                        """
                } else {
                    def op = getOperator(beforeOrAfter)
                    sb.append("""toDays($property) $op toDays(${time})\n""")
                }
            } else {
                def op = getOperator(beforeOrAfter)

                sb.append """
                        ${property} != null,
                        toDays(${property}) $op toDays(${time})
                        """
            }

            return new TemporalResult(criteria: sb.toString())
        } else {
            def sb = new StringBuffer()

            def targetProperty;
            switch (endOrStart){
                case EndOrStart.START:
                    targetProperty = "startDate"
                    break
                case EndOrStart.END:
                    targetProperty = "endDate"
                    break
            }

            if(temporalReference.range){
                def range = temporalReference.range

                def highOp
                def lowOp
                def minusOrPlus
                switch (beforeOrAfter){
                    case BeforeOrAfter.BEFORE:
                        highOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '>=' : '>'}
                        lowOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '<=' : '<'}
                        minusOrPlus = "-"
                        break

                    case BeforeOrAfter.AFTER:
                        lowOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '>=' : '>'}
                        highOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '<=' : '<'}
                        minusOrPlus = ""
                        break
                }

                if(range.high){
                    def calendarType = toCalendarType(range.high.unit)
                    def value = range.high.value//adjustForInclusive(range.high.value, range.high.'inclusive?')
                    sb.append("toDays(${property}) ${highOp(range.high)} toDays(droolsUtil.add(droolsUtil.getCalendar(\$${temporalReference.reference}.event.$targetProperty), Calendar.$calendarType, $minusOrPlus${value})),\n")
                }
                if(range.low){
                    def calendarType = toCalendarType(range.low.unit)
                    def value = range.low.value//adjustForInclusive(range.low.value, range.low.'inclusive?')
                    sb.append("toDays(${property}) ${lowOp(range.low)} toDays(droolsUtil.add(droolsUtil.getCalendar(\$${temporalReference.reference}.event.$targetProperty), Calendar.$calendarType, $minusOrPlus${value}))\n")
                } else {
                    def op = getOperator(beforeOrAfter)
                    sb.append("${property} $op \$${temporalReference.reference}.event.${targetProperty}\n")
                }
            } else {
                def op = getOperator(beforeOrAfter)
                sb.append("${property} $op${forceInclusive ? "=" : ""} \$${temporalReference.reference}.event.${targetProperty}")
            }

            return new TemporalResult(
                    variables: [temporalReference.reference],
                    criteria:
            """
            \$${temporalReference.reference}.event != null,
            eval(! (this.equals(\$${temporalReference.reference}.event))),
            ${sb.toString()}
            """)
        }
    }

    private class SpecificOccurrence{
        def id
        def constant
    }

    def getSpecificOccurrence(reference, measureJson){
        def criteria = measureJson.data_criteria.get(reference)
        if(criteria.specific_occurrence_const && criteria.specific_occurrence_const){
            new SpecificOccurrence(
                    id: criteria.specific_occurrence,
                    constant: criteria.specific_occurrence_const)
        }
    }

    def getOperator(beforeOrAfter){
        def op
        switch (beforeOrAfter){
            case BeforeOrAfter.BEFORE:
                op = "<"
                break
            case BeforeOrAfter.AFTER:
                op = ">"
                break
        }
    }

    def getUnit(unit){
        switch (unit){
            case "a" : return "Years"
            case "mo" : return "Months"
            case "wk" : return "Weeks"
            case "d" : return "Days"
            case "h" : return "Hours"
            default: throw new UnsupportedOperationException("Unit: $unit is not recognized.")
        }
    }

    def adjustForInclusive(value, inclusive){
        if(inclusive){
           Integer.toString(Integer.parseInt(value) + 1)
        } else {
            value
        }
    }

    def toCalendarType(unit){
        switch (unit){
            case "a" : return "YEAR"
            case "mo" : return "MONTH"
            case "wk" : return "WEEK_OF_YEAR"
            case "d" : return "DATE"
            case "h" : return "HOUR"
            default: throw new UnsupportedOperationException("Unit: $unit is not recognized.")
        }
    }

}
