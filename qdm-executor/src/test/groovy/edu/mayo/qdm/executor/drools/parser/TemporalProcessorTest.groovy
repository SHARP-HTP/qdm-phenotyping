package edu.mayo.qdm.executor.drools.parser

import org.junit.Test

import static org.junit.Assert.assertTrue
/**
 */
class TemporalProcessorTest {

    @Test
    void testOnlyLow(){
        def proc = new TemporalProcessor()

        def temporalReferences =
                [
                    type: "SBS",
                    reference: "MeasurePeriod",
                    range: [
                        type: "IVL_PQ",
                        low: [
                            type: "PQ",
                            unit: "a",
                            value: "65",
                            "inclusive?": true,
                            "derived?": false
                        ]
                    ]
                ]

        def ivl = proc.processTemporalReference(temporalReferences, null, "birthdate", "birthdate")

        println ivl.criteria

        assertTrue ivl.criteria.contains("toDays(birthdate) <")

    }

}
