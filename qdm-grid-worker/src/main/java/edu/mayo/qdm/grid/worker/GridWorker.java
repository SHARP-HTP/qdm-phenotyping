package edu.mayo.qdm.grid.worker;

import edu.mayo.qdm.executor.Executor;
import edu.mayo.qdm.executor.ExecutorFactory;
import edu.mayo.qdm.executor.Results;
import edu.mayo.qdm.grid.common.WorkerExecutionRequest;
import edu.mayo.qdm.grid.common.WorkerRegistrationRequest;
import org.apache.camel.CamelContext;
import org.apache.camel.Handler;
import org.apache.camel.ProducerTemplate;
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

    public static void main(String[] args){
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("qdm-grid-worker-context.xml");
        context.registerShutdownHook();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.executor = ExecutorFactory.instance().getExecutor();

        String hostName = "localhost";

        this.camelContext.start();

        String uri = "netty:tcp://"+hostName+":5150?sync=true";

        this.producerTemplate.requestBody("netty:tcp://localhost:1984?sync=true", new WorkerRegistrationRequest(uri));
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
