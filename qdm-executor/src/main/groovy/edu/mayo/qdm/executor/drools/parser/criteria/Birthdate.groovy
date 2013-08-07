package edu.mayo.qdm.executor.drools.parser.criteria

import edu.mayo.qdm.executor.drools.parser.TemporalProcessor
import org.apache.commons.lang.StringUtils

import java.text.SimpleDateFormat
/**
 */
class Birthdate implements Criteria {

    def temporalProcessor = new TemporalProcessor()

    def temporal

    def dateFormat = new SimpleDateFormat()

    def json

    Birthdate(json, measurementPeriod){
         this.json = json
         this.temporal = temporalProcessor.processTemporalReferences(
                json.value.temporal_references,
                measurementPeriod,
                "birthdate",
                "birthdate"
         )
    }

    @Override
    def getLHS() {
        boolean hasCriteria = StringUtils.isNotBlank(temporal.criteria)
        boolean hasVariables = StringUtils.isNotBlank(temporal.variables)

        """
        \$p : Patient( )
        ${
            if(hasCriteria){
                """
                ${hasVariables ? temporal.variables : ""}
                Patient(
                    this == \$p,
                    ${temporal.criteria}
                )
                """
            } else {
                ""
            }
        }
        """
    }

    @Override
    def getRHS() {
        """
        insert(new PreconditionResult("${json.key}", \$p, new Event(null, \$p.birthdate)))
        """
    }
}
