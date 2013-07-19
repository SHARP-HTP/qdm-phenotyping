package edu.mayo.qdm.drools.parser
import edu.mayo.qdm.drools.DroolsDateFormat
import groovy.util.logging.Log4j
import org.apache.commons.lang.BooleanUtils
import org.joda.time.DateTime

/**
 * Processes QDM/HQMF Temporal relationships such as SAS, EBE, etc.
 */
@Log4j
class TemporalProcessor {

    def processTemporalReferences(temporalReferences, measurementPeriod, startProperty="startDate", endProperty="endDate"){
        temporalReferences.collect(processTemporalReference.rcurry(measurementPeriod, startProperty, endProperty)).join(" and ")
    }

    def processTemporalReference = {temporalReference, measurementPeriod, startProperty, endProperty ->
        def type = temporalReference.type

        if(this.hasProperty(type)){
            this."$type"(temporalReference, startProperty, endProperty, measurementPeriod)
        } else {
            throw new RuntimeException("Temporal Reference Type `$type` not recognized\n. JSON ->  $temporalReference")
        }
    }

    def DURING = {temporalReference, startProperty, endProperty, measurementPeriod ->
        def start = SAS(temporalReference, startProperty, endProperty, measurementPeriod)
        def end = EBE(temporalReference, startProperty, endProperty, measurementPeriod)

        """
        $start,
        $end
        """
    }

    def SDU = {temporalReference, startProperty, endProperty, measurementPeriod ->
        def startAfterStart = SAS(temporalReference, startProperty, endProperty, measurementPeriod)
        def startBeforeEnd = SBE(temporalReference, startProperty, endProperty, measurementPeriod)

        """
        $startAfterStart,
        $startBeforeEnd
        """
    }

    def EAE = {temporalReference, startProperty, endProperty, measurementPeriod ->
        return this.temporalReference(temporalReference, endProperty, measurementPeriod, EndOrStart.END, BeforeOrAfter.AFTER)
    }

    def EBE = {temporalReference, startProperty, endProperty, measurementPeriod ->
        return this.temporalReference(temporalReference, endProperty, measurementPeriod, EndOrStart.END, BeforeOrAfter.BEFORE)
    }

    def EBS = {temporalReference, startProperty, endProperty, measurementPeriod ->
        return this.temporalReference(temporalReference, endProperty, measurementPeriod, EndOrStart.START, BeforeOrAfter.BEFORE)
    }

    def EAS = {temporalReference, startProperty, endProperty, measurementPeriod ->
        return this.temporalReference(temporalReference, endProperty, measurementPeriod, EndOrStart.START, BeforeOrAfter.AFTER)
    }

    def SAS = {temporalReference, startProperty, endProperty, measurementPeriod ->
        return this.temporalReference(temporalReference, startProperty, measurementPeriod, EndOrStart.START, BeforeOrAfter.AFTER)
    }

    def SAE = {temporalReference, startProperty, endProperty, measurementPeriod ->
        return this.temporalReference(temporalReference, startProperty, measurementPeriod, EndOrStart.END, BeforeOrAfter.AFTER)
    }

    def SBS = {temporalReference, startProperty, endProperty, measurementPeriod ->
        return this.temporalReference(temporalReference, startProperty, measurementPeriod, EndOrStart.START, BeforeOrAfter.BEFORE)
    }

    def SBE = {temporalReference, startProperty, endProperty, measurementPeriod ->
        return this.temporalReference(temporalReference, startProperty, measurementPeriod, EndOrStart.END, BeforeOrAfter.BEFORE)
    }

    enum EndOrStart {END,START}
    enum BeforeOrAfter {BEFORE,AFTER}

    def temporalReference(temporalReference, property, measurementPeriod, EndOrStart endOrStart, BeforeOrAfter beforeOrAfter){
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
                        ${property} ${highOp(range.high)} '${time."$minusOrPlusFn$unit"(Integer.parseInt(range.high.value)).toString(DroolsDateFormat.PATTERN)}'
                        """
                }
                if(range.low){
                    def unit = getUnit(range.low.unit)
                    sb.append """
                        ${property} ${lowOp(range.low)} '${time."$minusOrPlusFn$unit"(Integer.parseInt(range.low.value)).toString(DroolsDateFormat.PATTERN)}'
                        """
                }
            } else {
                def op
                switch (beforeOrAfter){
                    case BeforeOrAfter.BEFORE:
                        op = "<"
                        break
                    case BeforeOrAfter.AFTER:
                        op = ">"
                        break
                }
                sb.append """
                        ${property} $op '${time.toString(DroolsDateFormat.PATTERN)}'
                        """
            }

            return sb.toString()
        }

        //TODO: Need to be able to handle non Measurement Period temporal references.
        log.warn("Non-measurement period: " + temporalReference.toString())

        "/* TODO Non-measurement peroid */ true "
    }

    def getUnit(unit){
        switch (unit){
            case "a" : return "Years"
            case "mo" : return "Months"
            default: throw new UnsupportedOperationException("Unit: $unit is not recognized.")
        }
    }

}
