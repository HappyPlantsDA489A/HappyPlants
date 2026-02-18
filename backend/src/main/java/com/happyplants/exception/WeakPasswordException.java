package com.happyplants.exception;

import java.util.List;

public class WeakPasswordException extends RuntimeException {

    private final List<String> errors;

    public WeakPasswordException(List<String> errors) {
        super("Password does not meet security requirements");
        this.errors = errors;
    }

    public List<String> getErrors(){
        return errors;
    }
}
