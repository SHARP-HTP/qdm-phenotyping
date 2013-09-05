package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.executor.ResultCallback;
import edu.mayo.qdm.executor.Results;
import edu.mayo.qdm.patient.Patient;
import org.drools.FactHandle;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.ObjectFilter;
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

        ksession.setGlobal("droolsUtil", new DroolsUtil());

        final Results results = new Results();


        //ksession.setGlobal("specificContextManager", new SpecificContextManager());

		for(Patient p : this.getPatients()){
            ksession.insert(p);
        }

        ksession.fireAllRules();

        Collection<FactHandle> handles = ksession.getFactHandles(new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return
                        (object instanceof PreconditionResult)
                                &&
                                ((PreconditionResult) object).isPopulation();
            }
        });

        ResultCallback callback = new ResultCallback() {
            @Override
            public void hit(String population, Patient patient) {
                results.add(population, patient);
            }
        };

        for(FactHandle handle : handles){
            PreconditionResult precondition = (PreconditionResult) ksession.getObject(handle);
            callback.hit(precondition.getId(), precondition.getPatient());
        }

        System.out.println(results.asMap());
    }

    protected abstract Iterable<Patient> getPatients();

    protected abstract String getDroolsFile();

}
