package edu.mayo.qdm.grid.worker;

import edu.mayo.qdm.executor.Executor;
import edu.mayo.qdm.executor.ExecutorFactory;
import edu.mayo.qdm.executor.Results;
import edu.mayo.qdm.grid.common.WorkerExecutionRequest;
import edu.mayo.qdm.grid.common.WorkerRegistrationRequest;
import org.apache.camel.CamelContext;
import org.apache.camel.Handler;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component("gridWorker")
public class GridWorker implements InitializingBean {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CamelContext camelContext;

    private Executor executor;

    private String workerHostName = "localhost";
    private int workerPort = 5150;

    private String masterHostName = "localhost";
    private int masterPort = 1984;

    public static void main(String[] args){
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("qdm-grid-worker-context.xml");
        context.registerShutdownHook();

        context.getBean(GridWorker.class).register();
    }

    public void register(){
        final String workerPortString = Integer.toString(this.workerPort);
        final String masterPortString = Integer.toString(this.masterPort);

        try {
            this.camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("netty:tcp://localhost:"+workerPortString+"?sync=true").to("bean:gridWorker");
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String uri = "netty:tcp://"+this.workerHostName+":"+workerPortString+"?sync=true";

        this.producerTemplate.requestBody("netty:tcp://"+this.masterHostName+":"+masterPortString+"?sync=true", new WorkerRegistrationRequest(uri));

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.executor = ExecutorFactory.instance().getExecutor();
    }

    @Handler
    public Results process(WorkerExecutionRequest workerExecutionRequest) {
        return this.executor.execute(
                workerExecutionRequest.getPatients(),
                workerExecutionRequest.getQdmXml(),
                workerExecutionRequest.getMeasurementPeriod(),
                workerExecutionRequest.getValueSetDefinitions());
    }

}
