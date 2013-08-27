package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Concept;
import edu.mayo.qdm.patient.Lab;
import edu.mayo.qdm.patient.Patient;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntersectTest {

    static Patient p1;
    static SpecificOccurrence S1_A;
    static SpecificOccurrence S2_1;
    static SpecificOccurrence S2_3;
    static SpecificOccurrence S3_2;


    @BeforeClass
    public static void setUp() {
        Lab labA = new Lab(new Concept("a", "a", "a"),null,new DateTime(1980,1,1,0,0).toDate(),new DateTime(4000,1,1,0,0).toDate());
        Lab labB = new Lab(new Concept("b", "b", "b"),null,new DateTime(3000,1,1,0,0).toDate(),new DateTime(4000,1,1,0,0).toDate());
        Lab labC = new Lab(new Concept("c", "c", "c"),null,new DateTime(1980,1,1,0,0).toDate(),new DateTime(4000,1,1,0,0).toDate());
        Lab labD = new Lab(new Concept("d", "d", "d"),null,new DateTime(3000,1,1,0,0).toDate(),new DateTime(4000,1,1,0,0).toDate());

        p1 = new Patient("1");
        p1.addLab(labA);
        p1.addLab(labB);
        p1.addLab(labC);
        p1.addLab(labD);

        S1_A = new SpecificOccurrence("1", "CONST", labA);
        S2_1 = new SpecificOccurrence("2", "CONST", labB);
        S2_3 = new SpecificOccurrence("2", "CONST", labD);
        S3_2 = new SpecificOccurrence("3", "CONST", labC);
    }

    @Test
    public void intersect1() { // [(1,a)(2,b)] intersect [(2,b)(3,c)] = [(1,a)(2,b)(3,c)]
        SpecificContext specificContextA = new SpecificContext("A", p1);
        specificContextA.add(S1_A);
        specificContextA.add(S2_1);

        SpecificContext specificContextB = new SpecificContext("B", p1);
        specificContextB.add(S2_1);
        specificContextB.add(S3_2);

        List<SpecificContext> contexts = new ArrayList<>();
        contexts.add(specificContextA);
        contexts.add(specificContextB);

        SpecificContext intersectResult = doIntersect(p1, contexts);
        assertEquals(3, intersectResult.getSpecificContextTuples().size());
    }

    @Test
    public void intersect2() { // [(1,a)(2,b)] intersect [(2,d)(3,c)] = empty set
        SpecificContext specificContextA = new SpecificContext("A", p1);
        specificContextA.add(S1_A);
        specificContextA.add(S2_1);

        SpecificContext specificContextB = new SpecificContext("B", p1);
        specificContextB.add(S2_3);
        specificContextB.add(S3_2);

        List<SpecificContext> contexts = new ArrayList<>();
        contexts.add(specificContextA);
        contexts.add(specificContextB);

        SpecificContext intersectResult = doIntersect(p1, contexts);
        assertEquals(0, intersectResult.getSpecificContextTuples().size());
    }

    @Test
    public void intersect3() { // [(1,a)] intersect [(2,b)(3,c)] = [(1,a)(2,b)(3,c)]
        SpecificContext specificContextA = new SpecificContext("A", p1);
        specificContextA.add(S1_A);

        SpecificContext specificContextB = new SpecificContext("B", p1);
        specificContextB.add(S2_1);
        specificContextB.add(S3_2);

        List<SpecificContext> contexts = new ArrayList<>();
        contexts.add(specificContextA);
        contexts.add(specificContextB);

        SpecificContext intersectResult = doIntersect(p1, contexts);
        assertEquals(3, intersectResult.getSpecificContextTuples().size());
    }


    private SpecificContext doIntersect(Patient patient, List<SpecificContext> specificContexts) {
        return SpecificContextManager.intersect(patient, "I", specificContexts);
    }
}
