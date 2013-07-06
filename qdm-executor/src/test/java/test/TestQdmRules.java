package test;

import edu.mayo.qdm.patient.Patient;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

public class TestQdmRules {
	
	@Before
	public void setupDrools(){
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
		
		Patient p1 = new Patient("1");
		p1.setAge(80);

        Patient p2 = new Patient("2");
        p2.setAge(8);
		
		ksession.insert(p1);
        ksession.insert(p2);
		
		ksession.fireAllRules();
	}

	@Test
	public void test() {
		//
	}

}
