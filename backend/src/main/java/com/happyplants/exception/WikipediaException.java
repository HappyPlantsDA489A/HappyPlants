package com.happyplants.exception;

public class WikipediaException extends RuntimeException {
    public WikipediaException(String plantName) {
        super("Wikipedia search failed for '"+plantName+"'");
    }
}
