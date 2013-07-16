/*
 * Copyright: (c) 2004-2012 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.mayo.qdm.drools;

import edu.mayo.qdm.Executor;
import edu.mayo.qdm.MeasurementPeriod;
import edu.mayo.qdm.QdmProcessor;
import edu.mayo.qdm.Results;
import edu.mayo.qdm.drools.parser.Qdm2Drools;
import edu.mayo.qdm.patient.Patient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collection;

/**
 * The Class DroolsExecutor.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class DroolsExecutor implements Executor {

    private final Log log = LogFactory.getLog(this.getClass());

    @Resource
	private Qdm2Drools qdm2Drools;

    @Resource
    private DroolsUtil droolsUtil;

	/* (non-Javadoc)
	 * @see edu.mayo.qdm.Executor#execute(java.lang.Iterable, java.io.InputStream)
	 */
	public DroolsResults execute(Iterable<Patient> patients, String qdmXml, MeasurementPeriod measurementPeriod) {
        return this.doExecute(patients, this.createKnowledgeBase(qdmXml, measurementPeriod));
    }

    public DroolsResults doExecute(Iterable<Patient> patients, KnowledgeBase knowledgeBase) {
		final StatefulKnowledgeSession ksession = knowledgeBase
				.newStatefulKnowledgeSession();

		DroolsResults results = new DroolsResults();
		ksession.setGlobal("results", results);
        ksession.setGlobal("droolsUtil", this.droolsUtil);

		for(Patient patient : patients){
			ksession.insert(patient);
		}

		ksession.fireAllRules();

		ksession.dispose();   

		return results;	
	}

    @Override
    public QdmProcessor getQdmProcessor(String qdmXml, MeasurementPeriod measurementPeriod) {
        final KnowledgeBase knowledgeBase = this.createKnowledgeBase(qdmXml, measurementPeriod);

        return new QdmProcessor() {

            @Override
            public Results execute(Iterable<Patient> patients) {
                return doExecute(patients, knowledgeBase);
            }
        };
    }

    protected synchronized KnowledgeBase createKnowledgeBase(String qdmXml, MeasurementPeriod measurementPeriod){
        final KnowledgeBuilder kbuilder =
                KnowledgeBuilderFactory.newKnowledgeBuilder();

        // this will parse and compile in one step
        try {
            kbuilder.add(ResourceFactory.newByteArrayResource(
                    this.getDroolsRules(qdmXml, measurementPeriod)),
                    ResourceType.DRL);
        } catch (IOException e) {
            throw new IllegalStateException("Problem reading XSLT file.");
        }

        // Check the builder for errors
        if (kbuilder.hasErrors()) {
            System.out.println(kbuilder.getErrors().toString());
            throw new RuntimeException("Unable to compile DRL.");
        }

        // get the compiled packages (which are serializable)
        final Collection<KnowledgePackage> pkgs = kbuilder
                .getKnowledgePackages();

        // add the packages to a knowledgebase (deploy the knowledge packages).
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        kbase.addKnowledgePackages(pkgs);

        return kbase;
    }
	/**
	 * Gets the drools rules.
	 *
	 * @param qdmXml the qdm xml
	 * @return the drools rules
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected byte[] getDroolsRules(String qdmXml, MeasurementPeriod measurementPeriod) throws IOException {
        return this.qdm2Drools.qdm2drools(qdmXml, measurementPeriod).getBytes();
	}


}
