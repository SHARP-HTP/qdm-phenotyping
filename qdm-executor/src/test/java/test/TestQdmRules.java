package test;

import edu.mayo.qdm.patient.Lab;
import edu.mayo.qdm.patient.Patient;
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
import org.yaoqiang.bpmn.editor.swing.BPMNGraphComponent;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.bpmn.model.BPMNModelParsingErrors;
import org.yaoqiang.graph.io.bpmn.BPMNCodec;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.graph.util.GraphUtils;
import org.yaoqiang.util.Constants;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Collection;
import java.util.List;

public class TestQdmRules {
	
	@Before
	public void setupDrools() throws Exception {
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		// this will parse and compile in one step

        //kbuilder.add(ResourceFactory.newClassPathResource("testbpmn.xml"), ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("testrule.drl", this.getClass()), ResourceType.DRL);

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

/*
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
*/
		Patient p1 = new Patient("1");
        p1.addLab(new Lab(null,null,new DateTime(1980,1,1,0,0).toDate(),new DateTime(4000,1,1,0,0).toDate()));
        p1.addLab(new Lab(null,null,new DateTime(3000,1,1,0,0).toDate(),new DateTime(4000,1,1,0,0).toDate()));
        p1.addLab(new Lab(null,null,new DateTime(1980,1,1,0,0).toDate(),new DateTime(1980,1,1,0,0).toDate()));

		p1.setBirthdate(new DateTime(1980,1,1,0,0).toDate());
        Patient p2 = new Patient("2");
        p2.setBirthdate(new DateTime(2000, 10, 10, 10, 10).toDate());
		
		ksession.insert(p1);
        //ksession.insert(p2);
		
		//ksession.fireAllRules();

        //ksession.startProcess("com.sample.HelloWorld");

        ksession.getAgenda().getAgendaGroup("4").setFocus();
        ksession.getAgenda().getAgendaGroup("3").setFocus();
        ksession.getAgenda().getAgendaGroup("2").setFocus();
        ksession.getAgenda().getAgendaGroup("1").setFocus();
        ksession.fireAllRules();
/*
        BpmnXMLConverter converter = new BpmnXMLConverter();
        XMLInputFactory f = XMLInputFactory.newInstance();
        XMLStreamReader r;

        try {
            r = f.createXMLStreamReader(new ClassPathResource("testbpmn.bpmn").getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        BpmnModel model = converter.convertToBpmnModel(r);

        FileUtils.copyInputStreamToFile(ProcessDiagramGenerator.generatePngDiagram(model), new File("test.jpg"));

        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("com.sample.process");
        factory
                .name("HelloWorldProcess")
                .version("1.0")
                .packageName("defaultPackage")
                        // Nodes
                .startNode(1)
                .name("Start")
                .done()
                .actionNode(2)
                .name("Action")
                .action("java", "System.out.println(\"Hello World\");")
                .done().
                endNode(3)
                .name("End")
                .done()
                        // Connections
                .connection(1, 2)
                .connection(2, 3);

        RuleFlowProcess process = factory.getProcess();

        String xml = XmlRuleFlowProcessDumper.INSTANCE.dump(process);
*/
       // bpmn2Png(IOUtils.toString(new org.springframework.core.io.ClassPathResource("testbpmn.xml").getInputStream()));

    }

    static void bpmn2Png(String bpmnxmlfile) throws Exception {
        BPMNGraph graph = new BPMNGraph(new GraphModel(Constants.VERSION));
        BPMNGraphComponent graphComponent = new BPMNGraphComponent(graph);

        BPMNCodec codec = new BPMNCodec(graph);
        List<BPMNModelParsingErrors.ErrorMessage> errorMessages = codec.decode(bpmnxmlfile);
        if (errorMessages.size() > 0) {
            return;
        }

        ImageIO.write(GraphUtils.generateDiagram(graphComponent), "png", new File("test.png"));
    }

    @Test
	public void test() {
		//
	}

}
