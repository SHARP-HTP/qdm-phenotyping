package edu.mayo.qdm.patient;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class PatientJsonTest {

	@Test
	public void TestToJson() {
		
		Patient p = new Patient("123");
        p.setBirthdate(new Date());

        System.out.print(p.toJson());

        assertNotNull(p.toJson());
	}

    @Test
    public void TestToJsonWithProcedure() {

        Patient p = new Patient("123");
        Procedure pr = new Procedure(new Concept("asdf", "asdf", "asd"));
        p.addProcedure(pr);

        String json = p.toJson();
        assertNotNull(json);
    }

    @Test
    public void TestToJsonWithEncounter() {

        Patient p = new Patient("123");
        Encounter e = new Encounter("1", new Concept("asdf", "asdf", "asd"), new Date(), new Date());
        p.addEncounter(e);

        String json = p.toJson();
        System.out.print(json);
        assertNotNull(json);

        Patient.fromJson(json);
    }

    @Test
    public void TestFromJson(){
        String json = "{\"age\":43,\"sourcePid\":\"43\"}";

        assertNotNull(Patient.fromJson(json));
    }

    @Test
    public void TestRoundTrip() {

        Patient p = new Patient("123");
        Procedure pr = new Procedure(new Concept("asdf", "asdf", "asd"));
        p.addProcedure(pr);

        String json = p.toJson();

        Patient pReturned = Patient.fromJson(json);

        assertEquals(pReturned.getSourcePid(), p.getSourcePid());
        assertEquals(1, pReturned.getProcedures().size());
    }
	


}
