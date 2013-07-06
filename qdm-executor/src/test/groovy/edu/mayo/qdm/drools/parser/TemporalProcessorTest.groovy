package edu.mayo.qdm.drools.parser
import org.junit.Test
import static org.junit.Assert.*

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

        def ivl = proc.processTemporalReference(temporalReferences, "age")

        assertEquals "age >= 65".trim(), ivl.trim()

    }

    @Test
    void testLowAndHigh(){
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
                            ],
                            high: [
                                    type: "PQ",
                                    unit: "a",
                                    value: "18",
                                    "inclusive?": false,
                                    "derived?": false
                            ]
                    ]
            ]

        def ivl = proc.processTemporalReference(temporalReferences, "age")

        assertEquals "age >= 65 age < 18".trim(), ivl.trim()

    }
}
