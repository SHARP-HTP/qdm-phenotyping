package edu.mayo.qdm.executor.drools.parser

import edu.mayo.qdm.executor.drools.DroolsDateFormat
import groovy.util.logging.Log4j
import org.apache.commons.lang.BooleanUtils
import org.joda.time.DateTime

/**
 * Processes QDM/HQMF Temporal relationships such as SAS, EBE, etc.
 */
@Log4j
class TemporalProcessor {

    class TemporalResult{
        List variables
        String criteria
    }

    def processTemporalReferences(temporalReferences, measurementPeriod, measureJson, startProperty="startDate", endProperty="endDate"){
        def result = temporalReferences.collect(processTemporalReference.rcurry(measurementPeriod, measureJson, startProperty, endProperty))

        new TemporalResult(
            variables: result.collect { it.variables }.flatten(),
            criteria: result.inject('') { str, element -> str + (element.criteria ?: '') }
        )
    }

    def processTemporalReference = {temporalReference, measurementPeriod, measureJson, startProperty, endProperty ->
        def type = temporalReference.type

        if(this.hasProperty(type)){
            this."$type"(temporalReference, startProperty, endProperty, measurementPeriod, measureJson)
        } else {
            throw new RuntimeException("Temporal Reference Type `$type` not recognized\n. JSON ->  $temporalReference")
        }
    }

    def DURING = {temporalReference, startProperty, endProperty, measurementPeriod, measureJson ->
        def start = SAS(temporalReference, startProperty, endProperty, measurementPeriod, measureJson)
        def end = EBE(temporalReference, startProperty, endProperty, measurementPeriod, measureJson)

        return new TemporalResult(
                variables: start.variables,
                criteria:
        """
        $start.criteria,
        $end.criteria
        """)
    }

    def SDU = {temporalReference, startProperty, endProperty, measurementPeriod, measureJson ->
        def startAfterStart = SAS(temporalReference, startProperty, endProperty, measurementPeriod, measureJson)
        def startBeforeEnd = SBE(temporalReference, startProperty, endProperty, measurementPeriod, measureJson)

        return new TemporalResult(
                variables: startAfterStart.variables,
                criteria:
        """
        $startAfterStart.criteria,
        $startBeforeEnd.criteria
        """)
    }

    def EAE = {temporalReference, startProperty, endProperty, measurementPeriod, measureJson ->
        return this.temporalReference(temporalReference, endProperty, measurementPeriod, measureJson, EndOrStart.END, BeforeOrAfter.AFTER)
    }

    def EBE = {temporalReference, startProperty, endProperty, measurementPeriod, measureJson ->
        return this.temporalReference(temporalReference, endProperty, measurementPeriod, measureJson, EndOrStart.END, BeforeOrAfter.BEFORE)
    }

    def EBS = {temporalReference, startProperty, endProperty, measurementPeriod, measureJson ->
        return this.temporalReference(temporalReference, endProperty, measurementPeriod, measureJson, EndOrStart.START, BeforeOrAfter.BEFORE)
    }

    def EAS = {temporalReference, startProperty, endProperty, measurementPeriod, measureJson ->
        return this.temporalReference(temporalReference, endProperty, measurementPeriod, measureJson, EndOrStart.START, BeforeOrAfter.AFTER)
    }

    def SAS = {temporalReference, startProperty, endProperty, measurementPeriod, measureJson ->
        return this.temporalReference(temporalReference, startProperty, measurementPeriod, measureJson, EndOrStart.START, BeforeOrAfter.AFTER)
    }

    def SAE = {temporalReference, startProperty, endProperty, measurementPeriod, measureJson ->
        return this.temporalReference(temporalReference, startProperty, measurementPeriod, measureJson, EndOrStart.END, BeforeOrAfter.AFTER)
    }

    def SBS = {temporalReference, startProperty, endProperty, measurementPeriod, measureJson ->
        return this.temporalReference(temporalReference, startProperty, measurementPeriod, measureJson, EndOrStart.START, BeforeOrAfter.BEFORE)
    }

    def SBE = {temporalReference, startProperty, endProperty, measurementPeriod, measureJson ->
        return this.temporalReference(temporalReference, startProperty, measurementPeriod, measureJson, EndOrStart.END, BeforeOrAfter.BEFORE)
    }

    enum EndOrStart {END,START}
    enum BeforeOrAfter {BEFORE,AFTER}

    def temporalReference(temporalReference, property, measurementPeriod, measureJson, EndOrStart endOrStart, BeforeOrAfter beforeOrAfter){
        def reference = temporalReference.reference

        if(reference == "MeasurePeriod"){
            def sb = new StringBuilder()

            def time
            switch (endOrStart){
                case EndOrStart.START:
                    time = new DateTime(Long.parseLong(measurementPeriod.lowValue.value))
                    break
                case EndOrStart.END:
                    time = new DateTime(Long.parseLong(measurementPeriod.highValue.value))
                    break
            }

            if(temporalReference.range){
                def range = temporalReference.range

                def highOp
                def lowOp
                def minusOrPlusFn
                switch (beforeOrAfter){
                    case BeforeOrAfter.BEFORE:
                        highOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '>=' : '>'}
                        lowOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '<=' : '<'}
                        minusOrPlusFn = "minus"
                        break

                    case BeforeOrAfter.AFTER:
                        lowOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '>=' : '>'}
                        highOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '<=' : '<'}
                        minusOrPlusFn = "plus"
                        break
                }

                if(range.high){
                    def unit = getUnit(range.high.unit)
                    sb.append """
                        ${property} != null,
                        toDays(${property}) ${highOp(range.high)} toDays(new Date('${time."$minusOrPlusFn$unit"(Integer.parseInt(range.high.value)).toString(DroolsDateFormat.PATTERN)}')),
                        """
                }
                if(range.low){
                    def unit = getUnit(range.low.unit)
                    sb.append """
                        ${property} != null,
                        toDays(${property}) ${lowOp(range.low)} toDays(new Date('${time."$minusOrPlusFn$unit"(Integer.parseInt(range.low.value)).toString(DroolsDateFormat.PATTERN)}'))
                        """
                } else {
                    def op = getOperator(beforeOrAfter)
                    sb.append("""$property $op "${time.toString(DroolsDateFormat.PATTERN)}"\n""")
                }
            } else {
                def op = getOperator(beforeOrAfter)

                sb.append """
                        ${property} != null,
                        toDays(${property}) $op toDays(new Date('${time.toString(DroolsDateFormat.PATTERN)}'))
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
                    sb.append("toDays($property) ${highOp(range.high)} toDays(droolsUtil.add(droolsUtil.getCalendar(\$${temporalReference.reference}.event.$targetProperty), Calendar.$calendarType, $minusOrPlus${range.high.value})),\n")
                }
                if(range.low){
                    def calendarType = toCalendarType(range.low.unit)
                    sb.append("toDays($property) ${lowOp(range.low)} toDays(droolsUtil.add(droolsUtil.getCalendar(\$${temporalReference.reference}.event.$targetProperty), Calendar.$calendarType, $minusOrPlus${range.low.value}))\n")
                } else {
                    def op = getOperator(beforeOrAfter)
                    sb.append("$property $op \$${temporalReference.reference}.event.$targetProperty\n")
                }
            } else {
                def op = getOperator(beforeOrAfter)
                sb.append("toDays($property) $op toDays(\$${temporalReference.reference}.event.$targetProperty)")
            }

            return new TemporalResult(
                    variables: [temporalReference.reference],
                    criteria:
            """
            \$${temporalReference.reference}.event != null,
            ${sb.toString()}
            """)
        }
    }

    def getOperator(beforeOrAfter){
        def op
        switch (beforeOrAfter){
            case BeforeOrAfter.BEFORE:
                op = "<="
                break
            case BeforeOrAfter.AFTER:
                op = ">="
                break
        }

        op
    }

    def getUnit(unit){
        switch (unit){
            case "a" : return "Years"
            case "mo" : return "Months"
            case "d" : return "Days"
            default: throw new UnsupportedOperationException("Unit: $unit is not recognized.")
        }
    }

    def toCalendarType(unit){
        switch (unit){
            case "a" : return "YEAR"
            case "mo" : return "MONTH"
            case "d" : return "DATE"
            default: throw new UnsupportedOperationException("Unit: $unit is not recognized.")
        }
    }

}