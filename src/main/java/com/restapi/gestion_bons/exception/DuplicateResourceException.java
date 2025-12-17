package com.restapi.gestion_bons.exception;

public class DuplicateResourceException extends RuntimeException {
    private final String field;
    private final String value;

    public DuplicateResourceException(String field, String value) {
        super(String.format("Resource with %s='%s' already exists", field, value));
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}
