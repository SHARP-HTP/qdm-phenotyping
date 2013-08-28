package edu.mayo.qdm.executor.drools
import edu.mayo.qdm.patient.Lab
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 */
class SpecificContextManagerTest {

    @Test
    void testIntersectionAllAny(){
        def manager = new SpecificContextManager()

        def t1 = new SpecificContextTuple([(new SpecificOccurrenceId("a","a")) : new EventOrAny()])
        def t2 = new SpecificContextTuple([(new SpecificOccurrenceId("a","a")) : new EventOrAny()])


        def intersect = manager.intersect([t1] as Set,[t2] as Set)

        assertEquals 1, intersect.context.size()
        assertTrue intersect.iterator().next().context.get(new SpecificOccurrenceId("a","a")).any

    }

    @Test
    void testIntersectionOneAny(){
        def manager = new SpecificContextManager()

        def lab = new Lab()

        def t1 = new SpecificContextTuple([(new SpecificOccurrenceId("a","a")) : new EventOrAny(lab)])
        def t2 = new SpecificContextTuple([(new SpecificOccurrenceId("a","a")) : new EventOrAny()])


        def intersect = manager.intersect([t1] as Set,[t2] as Set)

        assertEquals 1, intersect.context.size()
        assertEquals lab, intersect.iterator().next().context.get(new SpecificOccurrenceId("a","a")).event
    }

    @Test
    void testIntersectionDifferent(){
        def manager = new SpecificContextManager()

        def lab1 = new Lab()
        def lab2 = new Lab()

        def t1 = new SpecificContextTuple([(new SpecificOccurrenceId("a","a")) : new EventOrAny(lab1)])
        def t2 = new SpecificContextTuple([(new SpecificOccurrenceId("a","a")) : new EventOrAny(lab2)])

        def intersect = manager.intersect([t1] as Set,[t2] as Set)

        assertEquals 0, intersect.context.size()
    }

    @Test
    void testIntersectionTwo(){
        def manager = new SpecificContextManager()

        def lab1 = new Lab()
        def lab2 = new Lab()

        def t1 = new SpecificContextTuple([
                (new SpecificOccurrenceId("a","a")) : new EventOrAny(lab1),
                (new SpecificOccurrenceId("b","b")) : new EventOrAny(lab2)
        ])
        def t2 = new SpecificContextTuple([
                (new SpecificOccurrenceId("a","a")) : new EventOrAny(lab1),
                (new SpecificOccurrenceId("b","b")) : new EventOrAny(lab2)
        ])

        def intersect = manager.intersect([t1] as Set,[t2] as Set)

        assertEquals 1, intersect.context.size()
        assertEquals lab1, intersect.iterator().next().context.get(new SpecificOccurrenceId("a","a")).event
        assertEquals lab2, intersect.iterator().next().context.get(new SpecificOccurrenceId("b","b")).event
    }

    @Test
    void testIntersectionOneAnyOneSame(){
        def manager = new SpecificContextManager()

        def lab1 = new Lab()
        def lab2 = new Lab()

        def t1 = new SpecificContextTuple([
                (new SpecificOccurrenceId("a","a")) : new EventOrAny(lab1),
                (new SpecificOccurrenceId("b","b")) : new EventOrAny()
        ])
        def t2 = new SpecificContextTuple([
                (new SpecificOccurrenceId("a","a")) : new EventOrAny(lab1),
                (new SpecificOccurrenceId("b","b")) : new EventOrAny(lab2)
        ])

        def intersect = manager.intersect([t1] as Set,[t2] as Set)

        assertEquals 1, intersect.context.size()
        assertEquals lab1, intersect.iterator().next().context.get(new SpecificOccurrenceId("a","a")).event
        assertEquals lab2, intersect.iterator().next().context.get(new SpecificOccurrenceId("b","b")).event
    }
}
