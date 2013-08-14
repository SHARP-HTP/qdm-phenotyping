package edu.mayo.qdm.executor.drools.parser

import edu.mayo.qdm.executor.MeasurementPeriod
import edu.mayo.qdm.executor.ResultCallback
import edu.mayo.qdm.executor.drools.DroolsUtil
import edu.mayo.qdm.executor.drools.PreconditionResult
import edu.mayo.qdm.executor.drools.SpecificOccurrence
import edu.mayo.qdm.executor.drools.parser.criteria.CriteriaFactory
import edu.mayo.qdm.executor.drools.parser.criteria.Interval
import edu.mayo.qdm.executor.drools.parser.criteria.MeasurementValue
import edu.mayo.qdm.patient.*
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

    //def qdm2jsonServiceUrl = "http://qdm2json.herokuapp.com";
    def qdm2jsonServiceUrl = "http://localhost:4567";

    final GROUP_DATA_CRITERIA_AGENDA_GROUP = "groupDataCriteria"
    final GROUP_DEPENDENT_DATA_CRITERIA_AGENDA_GROUP = "groupDependentDataCriteria"
    final SPECIFIC_OCCURRENCE_AGENDA_GROUP = "specificOccurrence"
    final SPECIFIC_OCCURRENCE_CRITERIA_AGENDA_GROUP = "specificOccurrenceCriteria"
    final GENERAL_DATA_CRITERIA_AGENDA_GROUP = "generalDataCriteria"

    @Autowired
    CriteriaFactory criteriaFactory;

    Qdm2Drools(){
        super()
    }

    Qdm2Drools(String qdm2jsonServiceUrl){
        super()
        this.qdm2jsonServiceUrl = qdm2jsonServiceUrl
    }

    /*
    def populationPriority = ["IPP":4,"DENOM":3,"DENEX":2,"NUMER":1]
    def populationSorter = { a, b ->
        def rank1 = populationPriority.get(a,0)
        def rank2 = populationPriority.get(b,0)

        rank1 <=> rank2
    } as Comparator
    */

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

    def getPopulationCriteriaStack(preconditions){
        def stack = []
        preconditions?.each {
            stack << it.id
            getPopulationCriteriaStack(it.preconditions).each { stack << it }
        }

        stack
    }

    def getDependencies(json, criteria){
        if(criteria == null) return []

        if(isGroup(criteria)){
            def dependencies = []
            dependencies << criteria.key
            criteria.value.children_criteria.each {
                dependencies << it
                def match = json.data_criteria.find {
                    entry -> entry.key == it
                }
                getDependencies(json, match).each { dependencies << it }
            }
            return dependencies
        } else {
            return criteria.value.temporal_references.findAll { it.reference != "MeasurePeriod" }.collect { it.reference }
        }
    }

    def String qdm2drools(String qdmXml, MeasurementPeriod measurementPeriod) {
        def json = getJsonFromQdmFile(qdmXml)

        def sb = new StringBuilder()

        sb.append(printRuleHeader(json))

        def ruleOrderStack = []
        /*
        json.population_criteria.sort(populationSorter).each {
            getPopulationCriteriaStack( it.value.preconditions ).each { ruleOrderStack << it }
        }
        */

        json.population_criteria.each {
            printPopulationCriteria( it, sb )
        }

        ruleOrderStack << GENERAL_DATA_CRITERIA_AGENDA_GROUP

        json.data_criteria.findAll(isGroup).each {
            getDependencies(json, it).each { ruleOrderStack << it}
        }

        json.data_criteria.each {
            switch(it){
                //case isSpecificOccurrence : sb.append( printSpecificOccurrenceDataCriteria( it, measurementPeriod, json, priorityStack ) ); break;
                //case isSpecificOccurrenceCriteria : sb.append( printSpecificOccurrenceCritieriaDataCriteria( it, measurementPeriod, json, priorityStack ) ); break;
                default : sb.append( printDataCriteria( it, measurementPeriod, json, ruleOrderStack ) ); break;
            }
        }

        sb.append( printRuleFunctions(json) )

        if(ruleOrderStack.size() > 0){
            sb.append( printInitRule(ruleOrderStack) )
        }

        def rule = sb.toString()

        log.isDebugEnabled() ? log.debug(rule):

        System.out.print(rule)

        rule
    }

    def isGroup = {
        it.value.type == "derived"
    }

    def isSpecificOccurrence = {
        it.value.specific_occurrence &&
        it.value.specific_occurrence_const &&
        it.key == it.value.source_data_criteria
    }

    def isSpecificOccurrenceCriteria = {
        it.value.specific_occurrence &&
        it.value.specific_occurrence_const &&
        it.key != it.value.source_data_criteria
    }

    private def printInitRule(agendaGroupStack){
        """
        /* Initialization Rule */
        rule "Initialize QDM Drools"
            dialect "mvel"
            no-loop
            salience ${Integer.MAX_VALUE}

        when

        then
            ${
            agendaGroupStack.collect {
            """
            drools.setFocus( "$it" );"""
            }.join()

            }
        end
        """
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
        import ${Event.name};
        import ${Interval.name};
        import ${MeasurementValue.name};
        import ${DroolsUtil.name};
        import ${MeasurementPeriod.name};
        import ${SpecificOccurrence.name};
        import ${MedicationStatus.name};
        import ${ProcedureStatus.name};
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
    private def printPopulationCriteria(populationCriteria, sb){
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
                case "DENOM": return """
                                        PreconditionResult(id == "IPP", patient == \$p)
                                     """
                case "NUMER": return """
                                        PreconditionResult(id == "DENOM", patient == \$p)
                                        not ( PreconditionResult(id == "DENEX", patient == \$p) )
                                     """
                case "DENEX": return """
                                        PreconditionResult(id == "DENOM", patient == \$p)
                                     """
                case "DENEXCEP": return """
                                        PreconditionResult(id == "DENOM", patient == \$p)
                                        not PreconditionResult(id == "NUMER", patient == \$p)
                                        """
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

        printPreconditions( nestedPreconditions, sb )
    }

    private def printPreconditions(preconditions, sb ){
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
            //salience 0
            //agenda-group "${prcn.id}"

        when
            \$p : Patient( )
            not ( PreconditionResult(id == "${prcn.id}", patient == \$p) )
        """
                    )

                    if(prcn.reference){
                        def dataCriteriaRef = prcn.reference

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
            System.out.println("${prcn.id}");
            insert(new PreconditionResult("${prcn.id}", \$p))
        end

        """
                    )

            }
        }

        nestedPreconditions.each { printPreconditions(it, sb)}
    }

    private def printPreconditionReference(preconditionReference){
            """
            PreconditionResult( id == "$preconditionReference", patient == \$p )
            """
    }

    private def printDataCriteria(dataCriteria, measurementPeriod, measureJson, priorityStack){
        def name = dataCriteria.key
        def criterias = criteriaFactory.getCriteria(dataCriteria, measurementPeriod, measureJson)

        def agendaGroup
        if(priorityStack.contains(name)){
            agendaGroup = """agenda-group "$name" """
        } else {
            agendaGroup = """agenda-group "$GENERAL_DATA_CRITERIA_AGENDA_GROUP" """
        }
        def idx = 0

        criterias.collect {
        def fullName = """${name}${"'" * idx++}"""
        """
        /* Rule */
        rule "$fullName"
            dialect "mvel"
            no-loop
            //$agendaGroup

        when
            ${it.getLHS()}

        then
            System.out.println("$fullName");
            ${it.getRHS()}
        end
        """
        }.join("\n")
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
