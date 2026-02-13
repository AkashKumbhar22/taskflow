package com.taskflow.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {   // Throws excpetion if the id is not found in the DB
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}