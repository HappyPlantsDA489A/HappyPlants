package com.happyplants.exception;

public class InvalidWateringFrequencyException extends RuntimeException {
    public InvalidWateringFrequencyException() {
        super("Watering frequency cannot be negative.");
    }
}
