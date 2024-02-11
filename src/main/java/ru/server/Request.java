package ru.server;
import java.io.Serializable;

public class Request implements Serializable {
    private static final long SerialVersionUID = 1L;
    private Type type; 

    public enum Type {
        REGISTRATION,
        SET,
        GET
    }

    public Request(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }
}
