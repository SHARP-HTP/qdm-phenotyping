package edu.mayo.qdm.executor.drools;

import java.util.Map;

/**
 */
public class TypedStringMapAccessor {

    private Map<String,String> delegate;

    public TypedStringMapAccessor(Map<String, String> delegate) {
        this.delegate = delegate;
    }

    public String get(String key){
        return delegate.get(key);
    }
}
