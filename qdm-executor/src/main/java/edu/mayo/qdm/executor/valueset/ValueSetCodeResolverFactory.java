package edu.mayo.qdm.executor.valueset;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Primary
public class ValueSetCodeResolverFactory implements FactoryBean<ValueSetCodeResolver> {

    private enum ValueSetResolverStrategy {CTS2, CYPRESS}

    @Value("${valuesets.valueSetResolver:CYPRESS}")
    private String valueSetResolverStrategy;

    @Resource
    @Qualifier("CTS2")
    private ValueSetCodeResolver cts2ValueSetCodeResolver;

    @Resource
    @Qualifier("Cypress")
    private ValueSetCodeResolver cypressValueSetCodeResolver;

    @Override
    public ValueSetCodeResolver getObject() {
        ValueSetResolverStrategy strategy;

        try {
            strategy = ValueSetResolverStrategy.valueOf(this.valueSetResolverStrategy.toUpperCase());
        } catch (IllegalArgumentException e){
            throw new RuntimeException("Value for configuration item `valuesets.valueSetResolver` is invald ("+this.valueSetResolverStrategy+").\n" +
                    "Possible values are: `" + ValueSetResolverStrategy.CTS2.toString() + "` and `" + ValueSetResolverStrategy.CYPRESS.toString() + "`");
        }

        switch (strategy){
            case CTS2: return this.cts2ValueSetCodeResolver;
            case CYPRESS: return this.cypressValueSetCodeResolver;
            default: throw new IllegalStateException();
        }
    }

    @Override
    public Class<?> getObjectType() {
        return ValueSetCodeResolver.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
