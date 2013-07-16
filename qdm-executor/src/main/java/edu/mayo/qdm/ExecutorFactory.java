package edu.mayo.qdm;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A singleton {@link Executor} factory.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class ExecutorFactory {

    private static ExecutorFactory instance;

    private Executor executor;

    private ExecutorFactory(Executor executor){
        super();
        this.executor = executor;
    }

    public static synchronized ExecutorFactory instance(){
        if(instance == null){
            ClassPathXmlApplicationContext context =
                    new ClassPathXmlApplicationContext("qdm-executor-context.xml");

            context.registerShutdownHook();

            instance = new ExecutorFactory(context.getBean(Executor.class));
        }

        return instance;
    }

    public Executor getExecutor(){
        return this.executor;
    }

}
