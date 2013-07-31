package edu.mayo.qdm.executor.drools.parser

import edu.mayo.qdm.executor.MeasurementPeriod
import edu.mayo.qdm.executor.ResultCallback
import edu.mayo.qdm.executor.drools.DroolsUtil
import edu.mayo.qdm.executor.drools.PreconditionResult
import edu.mayo.qdm.executor.drools.SpecificOccurrence
import edu.mayo.qdm.executor.drools.parser.criteria.CriteriaFactory
import edu.mayo.qdm.executor.drools.parser.criteria.Interval
import edu.mayo.qdm.executor.drools.parser.criteria.MeasurementValue
import edu.mayo.qdm.patient.Concept
import edu.mayo.qdm.patient.Patient
import groovy.util.logging.Log4j
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.commons.io.IOUtils
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.InputStreamBody
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/*
 * The main JSON -> Drools converter.
 */
@Log4j
@Component
class Qdm2Drools {

    def qdm2jsonServiceUrl = "http://qdm2json.herokuapp.com";

    @Autowired
    CriteriaFactory criteriaFactory;

    Qdm2Drools(){
        super()
    }

    Qdm2Drools(String qdm2jsonServiceUrl){
        super()
        this.qdm2jsonServiceUrl = qdm2jsonServiceUrl
    }

    /**
     * Generate a JSON representation of a QDM/HQMF XML file.
     *
     * @param qdmXml the QDM/HQMF XML file as a String.
     * @return the JSON representation
     */
    protected def getJsonFromQdmFile(qdmXml) {
        def qdmXmlFile = new InputStreamBody(IOUtils.toInputStream(qdmXml), "qdm.xml")

        def http = new HTTPBuilder(this.qdm2jsonServiceUrl + "/qdm2json")

        def resp = http.request(Method.POST) { req ->
            def mpEntity = new MultipartEntity()
            mpEntity.addPart("file", qdmXmlFile)

            req.entity = mpEntity
        }

        resp
    }

    def String qdm2drools(String qdmXml, MeasurementPeriod measurementPeriod) {
        this.doGetQdm2drools(qdmXml, measurementPeriod)
    }

    def String doGetQdm2drools(qdmXml, measurementPeriod) {
        def json = getJsonFromQdmFile(qdmXml)

        def sb = new StringBuilder()

        sb.append(printRuleHeader(json))

        def usedDataCriteria = [] as HashSet
        json.population_criteria.each {
            printPopulationCriteria( it, sb, usedDataCriteria)
        }

        log.info "Listed Data Criteria Size: ${json.data_criteria.size()}"
        log.info "Used Data Criteria Size: ${usedDataCriteria.size()}"

        json.data_criteria.each {
            sb.append( printDataCriteria( it, measurementPeriod, json ) )
        }

        sb.append( printRuleFunctions(json) )

        def rule = sb.toString()

        log.isDebugEnabled() ? log.debug(rule):

        System.out.print(rule)

        rule
    }

    /**
     * Prints header/metadata info for the Drools rule.
     */
    private def printRuleFunctions(qdm){
        """
        function Long toDays(Date date) {
            if(date == null){
                return null;
            } else {
                return new Long(java.util.concurrent.TimeUnit.MILLISECONDS.toDays(date.getTime()));
            }
        }
        """
    }

    /**
     * Prints header/metadata info for the Drools rule.
     */
    private def printRuleHeader(qdm){
        """
        import ${Set.name};
        import ${Date.name};
        import ${Calendar.name};
        import ${PreconditionResult.name};
        import ${ResultCallback.name};
        import ${Patient.name};
        import ${Concept.name};
        import ${Interval.name};
        import ${MeasurementValue.name};
        import ${DroolsUtil.name};
        import ${MeasurementPeriod.name};
        import ${SpecificOccurrence.name};
        /*
            ID: ${qdm.id}
            Title: ${qdm.title}
            Description: ${qdm.description}
            HQMF Version: ${qdm.hqmf_version_number}
            CMS ID: ${qdm.cms_id}
        */

        global ResultCallback resultCallback
        global DroolsUtil droolsUtil
        global MeasurementPeriod measurementPeriod
        """
    }

    /**
     * Print the start of a Population Criteria section (IPP, DENOM, etc).
     */
    private def printPopulationCriteria(populationCriteria, sb, usedDataCriteria){
        def name = populationCriteria.key

        def salience
        switch(name){
            case "IPP": salience = "-1000"; break
            case "DENOM": salience = "-1001"; break
            case "DENEX": salience = "-1002"; break
            case "NUMER": salience = "-1003"; break
            default: salience = "-1004"; break
        }

        sb.append("""
        /* Rule */
        rule "$name"
            dialect "mvel"
            no-loop
            salience $salience

        when
            \$p : Patient( )
            not ( PreconditionResult(id == "$name", patient == \$p) )
            ${switch(name){
                case "DENOM": return """PreconditionResult(id == "IPP", patient == \$p)"""
                case "NUMER": return """
                                        PreconditionResult(id == "DENOM", patient == \$p)
                                        not ( PreconditionResult(id == "DENEX", patient == \$p) )
                                     """
                case "DENEX": return """PreconditionResult(id == "DENOM", patient == \$p)"""
                default: ""
            }}
        """)

        def nestedPreconditions = []

        def preconditions = populationCriteria.value.preconditions

        if(preconditions?.size() > 0){

            preconditions.eachWithIndex {
                prcn, idx ->
                    def cnj = conjunctionToBoolean(prcn.conjunction_code)
                    if(prcn.negation) sb.append("not(")
                    if(prcn.reference){
                        def dataCriteriaRef = prcn.reference
                        usedDataCriteria.add(dataCriteriaRef)

                        sb.append(printPreconditionReference(dataCriteriaRef))
                    } else {
                        nestedPreconditions.add(prcn)

                        sb.append(printPreconditionReference(prcn.id))
                    }
                    if(idx != preconditions.size() -1) {
                        sb.append(" ${cnj} ")
                    }
                    if(prcn.negation) sb.append(")")
            }
        }
        sb.append("""
        then
            insert(new PreconditionResult("$name", \$p))
            resultCallback.hit("$name", \$p);
        end
        """)

        printPreconditions( nestedPreconditions, sb, usedDataCriteria )
    }

    private def printPreconditions(preconditions, sb, usedDataCriteria){
        if (preconditions == null) return

        def nestedPreconditions = []

        if(preconditions.size() > 0){

            preconditions.each {
                prcn ->

                    sb.append(
        """
        /* Rule */
        rule "${prcn.id}"
            dialect "mvel"
            no-loop
            salience 0

        when
            \$p : Patient( )
            not ( PreconditionResult(id == "${prcn.id}", patient == \$p) )
        """
                    )

                    if(prcn.reference){
                        def dataCriteriaRef = prcn.reference
                        usedDataCriteria.add(dataCriteriaRef)

                        sb.append(printPreconditionReference(dataCriteriaRef))
                    } else {
                        nestedPreconditions.add(prcn.preconditions)

                        def cnj = conjunctionToBoolean(prcn.conjunction_code)

                        prcn.preconditions.eachWithIndex {
                            nestedPrc, idx ->

                                if(nestedPrc.negation) sb.append("not(")
                                sb.append(printPreconditionReference(nestedPrc.id))
                                if(nestedPrc.negation) sb.append(") ")

                                if(idx != prcn.preconditions.size() -1) {
                                    sb.append(" ${cnj} ")
                                }
                        }
                    }

                    sb.append(
        """
        then
            insert(new PreconditionResult("${prcn.id}", \$p))
        end

        """
                    )

            }
        }

        nestedPreconditions.each { printPreconditions(it, sb, usedDataCriteria)}
    }

    private def printPreconditionReference(preconditionReference){
            """
            PreconditionResult( id == "$preconditionReference", patient == \$p )
            """
    }

    private def printDataCriteria(dataCriteria, measurementPeriod, measureJson){
        def name = dataCriteria.key
        def criteria = criteriaFactory.getCriteria(dataCriteria.value, measurementPeriod, measureJson)
        def hasEventList = criteria.hasEventList()
        def negated = dataCriteria.value.negation
        def specificOccurrence = dataCriteria.value.specific_occurrence

        """
        /* Rule */
        rule "${name}"
            dialect "mvel"
            no-loop
            salience 0

        when
            \$p : Patient ${hasEventList ? "()" : "("} ${criteria.toDrools()} ${hasEventList ? "" : ")"}

        then
            ${
            if(specificOccurrence){
                """insert(new PreconditionResult("${name}", \$p ${hasEventList && !negated ? ",\$event" : ""}, "$specificOccurrence"))"""
            } else {
                """insert(new PreconditionResult("${name}", \$p ${hasEventList && !negated ? ",\$event" : ""}))"""
            }
        }

        end
        """
    }

    /**
     * JSON conjunction to AND/OR
     *
     * @param 'allTrue' or 'atLeastOneTrue'
     * @return 'and' or 'or'
     */
    private def conjunctionToBoolean(conjunction){
        if (conjunction == null) return "and"
        switch (conjunction){
            case "allTrue": return "and"
            case "atLeastOneTrue": return "or"
        }
    }

}
