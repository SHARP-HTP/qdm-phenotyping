package edu.mayo.qdm.drools.parser
import edu.mayo.qdm.drools.DroolsDateFormat
import org.apache.commons.lang.BooleanUtils
import org.joda.time.DateTime
/**
 */
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

    def EAE = {temporalReference, startProperty, endProperty, measurementPeriod ->
        return this.temporalReference(temporalReference, endProperty, measurementPeriod, EndOrStart.END, BeforeOrAfter.AFTER)
    }

    def EBE = {temporalReference, startProperty, endProperty, measurementPeriod ->
        return this.temporalReference(temporalReference, endProperty, measurementPeriod, EndOrStart.END, BeforeOrAfter.BEFORE)
    }

    def EBS = {temporalReference, startProperty, endProperty, measurementPeriod ->
        return this.temporalReference(temporalReference, endProperty, measurementPeriod, EndOrStart.START, BeforeOrAfter.BEFORE)
    }

    def SAS = {temporalReference, startProperty, endProperty, measurementPeriod ->
        return this.temporalReference(temporalReference, startProperty, measurementPeriod, EndOrStart.START, BeforeOrAfter.AFTER)
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
                        minusOrPlusFn = "minusYears"
                        break
                    case BeforeOrAfter.AFTER:
                        lowOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '>=' : '>'}
                        highOp = {r -> BooleanUtils.toBoolean(r.'inclusive?') ? '<=' : '<'}
                        minusOrPlusFn = "plusYears"
                        break
                }

                if(range.high){
                    sb.append """
                        ${property} ${highOp(range.high)} '${time."$minusOrPlusFn"(Integer.parseInt(range.high.value)).toString(DroolsDateFormat.PATTERN)}'
                        """
                }
                if(range.low){
                    sb.append """
                        ${property} ${lowOp(range.low)} '${time."$minusOrPlusFn"(Integer.parseInt(range.low.value)).toString(DroolsDateFormat.PATTERN)}'
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

        throw new UnsupportedOperationException("Non-measurement period: " + temporalReference.toString())
    }

}
