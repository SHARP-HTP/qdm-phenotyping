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

    Birthdate(json){
         this.json = json
         this.temporal = temporalProcessor.processTemporalReferences(
                json.value.temporal_references,
                "birthdate",
                "birthdate"
         )
    }

    @Override
    def getLHS() {
        boolean hasCriteria = StringUtils.isNotBlank(temporal.criteria)

        """
        \$p : Patient( )
        ${
            if(hasCriteria){
                """
                ${temporal.variables.collect { """\$$it : PreconditionResult(id == "$it", patient == \$p) """ }.join("\n")}
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
        insertLogical(new PreconditionResult("${json.key}", \$p, new Event(null, \$p.birthdate)))
        """
    }
}
