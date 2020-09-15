package org.example.test24.lib.functioninterface;

@FunctionalInterface
public interface FunctionException<E> {
    boolean function(E exception);
}
