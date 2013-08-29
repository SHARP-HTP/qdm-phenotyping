package edu.mayo.qdm.executor.drools

import edu.mayo.qdm.patient.Lab
import org.junit.Test

import static org.junit.Assert.assertEquals
/**
 */
class SpecificContextTest {

    @Test
    void testCompactAllOk(){
        def context = new SpecificContext(null,null,null)

        def lab = new Lab()

        def t = new SpecificContextTuple([
                (new SpecificOccurrenceId("a","a")) : new EventOrAny(lab),
                (new SpecificOccurrenceId("a","b")) : new EventOrAny(lab)])

        context.specificContextTuples = [t] as Set

        assertEquals 1, context.specificContextTuples.size()
        context.compact()
        assertEquals 1, context.specificContextTuples.size()
    }


    @Test
    void testCompactSameEvent(){
        def context = new SpecificContext(null,null,null)

        def lab = new Lab()

        def t = new SpecificContextTuple([
                (new SpecificOccurrenceId("a","a")) : new EventOrAny(lab),
                (new SpecificOccurrenceId("b","a")) : new EventOrAny(lab)])

        context.specificContextTuples = [t] as Set

        assertEquals 1, context.specificContextTuples.size()
        context.compact()
        assertEquals 0, context.specificContextTuples.size()
    }

}
