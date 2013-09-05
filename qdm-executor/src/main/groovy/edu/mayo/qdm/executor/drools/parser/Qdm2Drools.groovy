package edu.mayo.qdm.executor.drools.parser

import edu.mayo.qdm.executor.MeasurementPeriod
import edu.mayo.qdm.executor.ResultCallback
import edu.mayo.qdm.executor.drools.*
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
    //def qdm2jsonServiceUrl = "http://qdm2json.phenotypeportal.org"

    final GROUP_DATA_CRITERIA_AGENDA_GROUP = "groupDataCriteria"
    final GROUP_DEPENDENT_DATA_CRITERIA_AGENDA_GROUP = "groupDependentDataCriteria"
    final SPECIFIC_OCCURRENCE_AGENDA_GROUP = "specificOccurrence"
    final SPECIFIC_OCCURRENCE_CRITERIA_AGENDA_GROUP = "specificOccurrenceCriteria"
    final GENERAL_DATA_CRITERIA_AGENDA_GROUP = "generalDataCriteria"

    @Autowired
    CriteriaFactory criteriaFactory;

    def specificOccurrencesProcessor = new SpecificOccurrencesProcessor()

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
        def specificOccurrences = json.source_data_criteria.collect {
            if(it.value.specific_occurrence_const){
                def constant = it.value.specific_occurrence_const
                def id = it.value.specific_occurrence
                    sb.append(printSpecificOccurrenceRule(constant, id,
                        json.data_criteria.findAll {
                            it.value.specific_occurrence_const == constant &&
                            it.value.specific_occurrence == id }
                            ))
            }
        }.findAll()
        */

        //sb.append(specificOccurrencesProcessor.getSpecificOccurrencesRules(json))

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

        /* TODO: For specific occurrences
        json.source_data_criteria.findAll(isSpecificOccurrenceDataCriteria).each {
            sb.append( printSpecificOccurrenceDataCriteria( it, measurementPeriod, json, ruleOrderStack ) )
        }
        */

        //sb.append( printRuleFunctions(json) )

        /*
        if(ruleOrderStack.size() > 0){
            sb.append( printInitRule(ruleOrderStack) )
        }
        */

        def rule = sb.toString()

        log.isDebugEnabled() ? log.debug(rule):

        System.out.print(rule)

        rule
    }

    def isNotSpecificOccurrenceDataCriteria = {
        ! (it.value.specific_occurrence &&
                it.value.specific_occurrence_const &&
                it.key == it.value.source_data_criteria)
    }

    def isSpecificOccurrenceDataCriteria = {
        it.value.specific_occurrence &&
                it.value.specific_occurrence_const &&
                it.key == it.value.source_data_criteria
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

    private def printSpecificOccurrenceRule(constant, id, preconditions){
        """
        /* Specific Occurrence Rule */
        rule "Specific Occurrence $constant $id Rule"
            dialect "mvel"
            no-loop

        when
            \$p : Patient()
            ${
                preconditions.collect {
                    """
                    \$${it.key} : PreconditionResult(id == "${it.key}", patient == \$p)"""
                }.join( " or ")
            }

        then
            if(! droolsUtil.allEquals([${preconditions.collect { """\$${it.key}.event""" }.join(",")}])){
                System.out.println("Retracting $constant $id!");
                ${
                preconditions.collect {
                    """
                    retract(\$${it.key})"""
                }.join()
            }
            } else {
                System.out.println("All Equal $constant $id!");
                insertLogical(new SpecificOccurrence("$constant", "$id", \$${preconditions.find().key}.event, \$p));
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
        import ${SpecificOccurrenceResult.name};
        import ${MedicationStatus.name};
        import ${ProcedureStatus.name};
        import ${PreconditionCollection.name};
        import function ${DroolsUtil.name}.toDays;
        /*
            ID: ${qdm.id}
            Title: ${qdm.title}
            Description: ${qdm.description}
            HQMF Version: ${qdm.hqmf_version_number}
            CMS ID: ${qdm.cms_id}
        */

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
            case "DENOM": salience = "-1000"; break
            case "DENEX": salience = "-1000"; break
            case "NUMER": salience = "-1000"; break
            default: salience = "-1000"; break
        }

        sb.append("""
        /* Rule */
        rule "$name"
            dialect "mvel"
            no-loop
            salience $salience

        when
            \$p : Patient( )
            //not ( PreconditionResult(id == "$name", patient == \$p) )
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

                        sb.append(printPreconditionReferenceNoContextVariable(dataCriteriaRef))
                    } else {
                        nestedPreconditions.add(prcn)

                        sb.append(printPreconditionReferenceNoContextVariable(prcn.id))
                    }
                    if(idx != preconditions.size() -1) {
                        sb.append(" ${cnj} ")
                    }
                    if(prcn.negation) sb.append(")")
            }
        }
        sb.append("""
        then
            insertLogical(new PreconditionResult("$name", \$p, true))
        end
        """)

        printPreconditions( nestedPreconditions, sb )
    }

    private def printPreconditions(preconditions, sb ){
        if (preconditions == null) return

        def nestedPreconditions = []

        if(preconditions.size() > 0){

            preconditions.eachWithIndex {
                prcn, index ->

                    sb.append(
        """
        /* Rule */
        rule "${prcn.id}"
            dialect "mvel"
            no-loop
            //salience ${0 - index}
            //agenda-group "${prcn.id}"

        when
            \$p : Patient( )
            //not ( PreconditionResult(id == "${prcn.id}", patient == \$p) )
        """
                    )
                    def cnj = conjunctionToBoolean(prcn.conjunction_code)

                    if(prcn.reference){
                        def dataCriteriaRef = prcn.reference

                        sb.append(printPreconditionReferenceWithContextVariable(dataCriteriaRef))
                    } else {
                        nestedPreconditions.add(prcn.preconditions)

                        if(prcn.preconditions.size() == 1){
                            sb.append(printPreconditionReferenceWithContextVariable(prcn.preconditions[0].id))
                        } else {

                            if(cnj == "and"){

                                prcn.preconditions.findAll {! it.negation }.eachWithIndex {
                                    nestedPrc, idx ->

                                        if(nestedPrc.negation) sb.append("not(")
                                        if(cnj == "and"){
                                            sb.append(printPreconditionReferenceNoContextVariable(nestedPrc.id, true))
                                        } else {
                                            sb.append(printPreconditionReferenceNoContextVariable(nestedPrc.id))
                                        }
                                        if(nestedPrc.negation) sb.append(") ")

                                        if(idx != prcn.preconditions.size() -1) {
                                            //sb.append(" ${cnj} ")
                                        }
                                }

                                sb.append("""\$context : java.util.Map() from droolsUtil.intersect([${prcn.preconditions.findAll {! it.negation }.collect {"""\$p${it.id}.context"""}.join(",")}])""")

                                sb.append(prcn.preconditions.findAll { it.negation }.collect {
                                    """
                                    not( ${printPreconditionReference(it.id)} )
                                    """
                                }.join())

                                sb.append(prcn.preconditions.findAll {! it.negation }.collect {
                                    """
                                    eval(\$p${it.id}.compatible(\$context))
                                    """
                                }.join())

                            } else {
                                sb.append("""\$preconditions : PreconditionResult(
                                    ${prcn.preconditions.collect {"""id == "${it.id}" """}.join(" || ")}, \$context : context, patient == \$p)""")
                            }
                        }
                    }

                    sb.append(
        """
        then
            System.out.println("${prcn.id}");
            insertLogical(new PreconditionResult("${prcn.id}", \$p, ${
                if(true || prcn.reference || cnj == "or" || prcn.preconditions?.size() == 1){
                    """\$context"""
                } else {
                    """droolsUtil.combine([${prcn.preconditions.collect {
                        (it.negation) ? null : """\$p${it.id}.context"""}.findAll().join(",")}])"""
                }
            }))
        end

        """
                    )

            }
        }

        nestedPreconditions.each { nestedPrc -> printPreconditions(nestedPrc, sb)}
    }

    private def printPreconditionReferenceNoContextVariable(preconditionReference, withAlias=false){
        """
        ${withAlias ? """\$p$preconditionReference: """ : "" }PreconditionResult( id == "$preconditionReference", patient == \$p )
        """
    }

    private def printPreconditionReferenceWithContextVariable(preconditionReference, withAlias=false){
        """
        ${withAlias ? """\$p$preconditionReference: """ : "" }PreconditionResult( id == "$preconditionReference", patient == \$p, \$context : context )
        """
    }

    private def printPreconditionReference(preconditionReference, withAlias=false, contextVariable="\$context"){
            """
            ${withAlias ? """\$p$preconditionReference : """ : "" }PreconditionResult( id == "$preconditionReference", patient == \$p, compatible($contextVariable) )
            """
    }

    private def printSpecificOccurrenceDataCriteria(dataCriteria, measurementPeriod, measureJson, priorityStack){
        def name = dataCriteria.key
        def criterias = criteriaFactory.getSpecificOccurrenceDataCriteria(dataCriteria, measurementPeriod, measureJson)

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
        rule "Specific Occurrence ${fullName}"
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
