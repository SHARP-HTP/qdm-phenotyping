package test;

import edu.mayo.qdm.drools.DroolsUtil;
import edu.mayo.qdm.patient.Concept;
import edu.mayo.qdm.patient.Lab;
import edu.mayo.qdm.patient.Patient;
import edu.mayo.qdm.valueset.ValueSetCodeResolver;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class TestQdmRules {
	
	@Before
	public void setupDrools() throws Exception {
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		// this will parse and compile in one step

		kbuilder.add(ResourceFactory.newClassPathResource("testrule.drl",
		        this.getClass()), ResourceType.DRL);
		// Check the builder for errors

		if (kbuilder.hasErrors()) {
		    System.out.println(kbuilder.getErrors().toString());
		    throw new RuntimeException(kbuilder.getErrors().toString());
		}

		// get the compiled packages (which are serializable)
		final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

		// add the packages to a knowledgebase (deploy the knowledge packages).
		final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

		kbase.addKnowledgePackages(pkgs);

		final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();


        ksession.setGlobal("droolsUtil", new DroolsUtil(new ValueSetCodeResolver() {
            @Override
            public Set<Concept> resolveConcpets(String valueSetOid) {
                return new HashSet<Concept>();
            }

            @Override
            public boolean isCodeInSet(String valueSetOid, Concept concept) {
                return true;
            }
        }));
		
		Patient p1 = new Patient("1");
        p1.addLab(new Lab(null,null,new Date(),new Date()));
        p1.addLab(new Lab(null,null,new Date(),new Date()));

		p1.setBirthdate(new DateTime(1980,1,1,0,0).toDate());
        Patient p2 = new Patient("2");
        p2.setBirthdate(new DateTime(2000, 10, 10, 10, 10).toDate());
		
		ksession.insert(p1);
        ksession.insert(p2);
		
		ksession.fireAllRules();
	}

	@Test
	public void test() {
		//
	}

}
