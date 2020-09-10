package org.example.lib.functioninterface;

@FunctionalInterface
public interface FunctionException<E> {
    boolean function(E exception);
}
