package com.happyplants.exception;

public class UserPlantNotFoundException extends RuntimeException {
    public UserPlantNotFoundException() {
        super("Plant not found in your collection");
    }
}
