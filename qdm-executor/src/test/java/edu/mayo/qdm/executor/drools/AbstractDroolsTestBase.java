package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Patient;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.util.Collection;

public abstract class AbstractDroolsTestBase {
	
	@Test
	public void setupDrools() throws Exception {
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		// this will parse and compile in one step

        kbuilder.add(ResourceFactory.newClassPathResource(this.getDroolsFile(), this.getClass()), ResourceType.DRL);

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

		for(Patient p : this.getPatients()){
            ksession.insert(p);
        }

        ksession.fireAllRules();
    }

    protected abstract Iterable<Patient> getPatients();

    protected abstract String getDroolsFile();

}
