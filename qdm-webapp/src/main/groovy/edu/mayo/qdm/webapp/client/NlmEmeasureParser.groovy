package edu.mayo.qdm.webapp.client

/**
 */
class NlmEmeasureParser {

    def parseEmeasureJson(emeasureJson){
        def emeasures = []
        emeasureJson.rows.each {
            emeasures.add(
                    new Emeasure(
                            measureId: it.measureid,
                            nqfId: it.nqf,
                            title: it.title))
        }

        emeasures
    }
}
