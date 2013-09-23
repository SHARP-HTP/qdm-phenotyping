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
package edu.mayo.qdm.executor.drools;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import edu.mayo.qdm.executor.*;
import edu.mayo.qdm.executor.drools.parser.Qdm2Drools;
import edu.mayo.qdm.patient.Patient;
import org.apache.log4j.Logger;
import org.drools.FactHandle;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.ObjectFilter;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.event.rule.DefaultWorkingMemoryEventListener;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * The Class DroolsExecutor.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class DroolsExecutor implements Executor {

    private final Logger log = Logger.getLogger(this.getClass());

    private static final int EXECUTION_BATCH_SIZE = 1000;
    private static final int KNOWLEDGE_BASE_CACHE_SIZE = 100;

    private Cache<String,KnowledgeBase> knowledgeBaseCache =
            CacheBuilder.newBuilder().maximumSize(KNOWLEDGE_BASE_CACHE_SIZE).build();

    @Resource
	private Qdm2Drools qdm2Drools;

    @Resource
    private DroolsUtil droolsUtil;

    @Override
    public Results execute(Iterable<Patient> patients, String qdmXml, MeasurementPeriod measurementPeriod, Map<String,String> valueSetDefinitions) {
        final Results results = new Results();

        this.execute(patients, qdmXml, measurementPeriod, valueSetDefinitions, new ResultCallback() {

            @Override
            public void hit(String population, Patient patient) {
                results.add(population, patient);
            }
        });

        return results;
    }

    /* (non-Javadoc)
         * @see edu.mayo.qdm.Executor#execute(java.lang.Iterable, java.io.InputStream)
         */
    @Override
	public void execute(Iterable<Patient> patients, String qdmXml, MeasurementPeriod measurementPeriod, Map<String,String> valueSetDefinitions, ResultCallback callback) {
        this.doExecute(patients, this.createKnowledgeBase(qdmXml), measurementPeriod, valueSetDefinitions, callback);
    }

    public void doExecute(Iterable<Patient> patients, KnowledgeBase knowledgeBase, MeasurementPeriod measurementPeriod, Map<String,String> valueSetDefinitions, ResultCallback callback) {
        Set<Patient> cache = new HashSet<Patient>();
        for(Patient p : patients){
            if(cache.size() < EXECUTION_BATCH_SIZE){
                cache.add(p);
            } else {
                this.doExecuteBatch(cache, knowledgeBase, measurementPeriod, valueSetDefinitions, callback);
                cache.clear();
                cache.add(p);
            }
        }

        if(cache.size() > 0){
            this.doExecuteBatch(cache, knowledgeBase, measurementPeriod, valueSetDefinitions, callback);
        }
    }

    public void doExecuteBatch(Iterable <Patient> patients, KnowledgeBase knowledgeBase, MeasurementPeriod measurementPeriod, Map<String,String> valueSetDefinitions, ResultCallback callback) {
		final StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();

        try {
            if(this.log.isDebugEnabled()){
                ksession.addEventListener(new DefaultWorkingMemoryEventListener() {

                    @Override
                    public void objectInserted(ObjectInsertedEvent event) {
                        Object obj = event.getObject();
                        if(obj instanceof PreconditionResult){
                            PreconditionResult precondition = (PreconditionResult) obj;
                            log.debug("Inserting Fact - Precondition: `" + precondition.getId() + "`, Patient: " + precondition.getPatient());
                        } else {
                            log.debug("Inserting Fact: " + obj.toString());
                        }
                    }

                    @Override
                    public void objectRetracted(ObjectRetractedEvent event) {
                        Object obj = event.getOldObject();
                        if(obj instanceof PreconditionResult){
                            PreconditionResult precondition = (PreconditionResult) obj;
                            log.debug("Retracting Fact - Precondition: `" + precondition.getId() + "`, Patient: " + precondition.getPatient());
                        } else {
                            log.debug("Retracting Fact: " + obj.toString());
                        }
                    }

                });
            }

            ksession.setGlobal("droolsUtil", this.droolsUtil);
            ksession.setGlobal("measurementPeriod", measurementPeriod);
            ksession.setGlobal("valueSetDefinitions", valueSetDefinitions != null ? valueSetDefinitions : Collections.EMPTY_MAP);

            for(Patient patient : patients){
                ksession.insert(patient);
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

            for(FactHandle handle : handles){
                PreconditionResult precondition = (PreconditionResult) ksession.getObject(handle);
                callback.hit(precondition.getId(), precondition.getPatient());
            }
        } finally {
		    ksession.dispose();
        }
	}

    @Override
    public QdmProcessor getQdmProcessor(String qdmXml, final MeasurementPeriod measurementPeriod, final Map<String,String> valueSetDefinitions) {
        final KnowledgeBase knowledgeBase = this.createKnowledgeBase(qdmXml);

        return new QdmProcessor() {

            @Override
            public void execute(Iterable<Patient> patients, ResultCallback callback) {
                doExecute(patients, knowledgeBase, measurementPeriod, valueSetDefinitions, callback);
            }

            @Override
            public Results execute(Iterable<Patient> patients) {
                final Results results = new Results();

                execute(patients, new ResultCallback() {

                    @Override
                    public void hit(String population, Patient patient) {
                        results.add(population, patient);
                    }
                });

                return results;
            }
        };
    }

    protected synchronized KnowledgeBase createKnowledgeBase(final String qdmXml){
        try {
            return this.knowledgeBaseCache.get(qdmXml.trim(), new Callable<KnowledgeBase>(){

                @Override
                public KnowledgeBase call() throws Exception {
                    return doCreateKnowledgeBase(qdmXml);
                }
            });
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    protected synchronized KnowledgeBase doCreateKnowledgeBase(String qdmXml){
        final KnowledgeBuilder kbuilder =
                KnowledgeBuilderFactory.newKnowledgeBuilder();

        // this will parse and compile in one step
        try {
            kbuilder.add(ResourceFactory.newByteArrayResource(
                    this.getDroolsRules(qdmXml)),
                    ResourceType.DRL);
        } catch (IOException e) {
            log.error(e);
            throw new IllegalStateException("Problem creating Drools Rule. Message: " +e.getMessage(), e);
        }

        // Check the builder for errors
        if (kbuilder.hasErrors()) {
            throw new RuntimeException("Unable to compile DRL: " + kbuilder.getErrors().toString());
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
	protected byte[] getDroolsRules(String qdmXml) throws IOException {
        return this.qdm2Drools.qdm2drools(qdmXml).getBytes();
	}


}
