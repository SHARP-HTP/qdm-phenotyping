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
    private AbstractApplicationContext context;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CamelContext camelContext;

    private Executor executor;

    public static void main(String[] args){
        if(args == null || args.length != 5){
            throw new IllegalArgumentException();
        }

        launch(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Boolean.parseBoolean(args[4]));
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static GridWorker launch(final String workerHostName, final int workerPort, String masterHostName, int masterPort, boolean local){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("qdm-grid-worker-context.xml");
        context.registerShutdownHook();

        GridWorker worker = context.getBean(GridWorker.class);
        worker.register(workerHostName, workerPort, masterHostName, masterPort, local);

        return worker;
    }

    public void register(final String workerHostName, final int workerPort, String masterHostName, int masterPort, final boolean local){
        final String workerPortString = Integer.toString(workerPort);
        final String masterPortString = Integer.toString(masterPort);

        final String uri;
        if(local){
            uri = "vm:worker" + workerPortString;
        } else {
            uri = "netty:tcp://"+workerHostName+":"+workerPortString+"?sync=true";
        }
        try {
            this.camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    if(local){
                    from("vm:worker" + workerPortString).to("bean:gridWorker");
                    } else {
                        from(uri).to("bean:gridWorker");
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.producerTemplate.sendBody("netty:udp://" + masterHostName + ":" + masterPortString, new WorkerRegistrationRequest(uri));

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

    public void shutdown(){
        context.close();
    }

}
