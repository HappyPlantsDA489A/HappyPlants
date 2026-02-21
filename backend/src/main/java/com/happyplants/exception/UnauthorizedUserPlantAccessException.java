package com.happyplants.exception;

public class UnauthorizedUserPlantAccessException extends RuntimeException {
    public UnauthorizedUserPlantAccessException() {
        super("You are not allowed to modify this plant");
    }
}
