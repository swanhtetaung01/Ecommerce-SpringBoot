package com.ecommerce.project.exceptions;

public class EmptyCategoryException extends RuntimeException{
    public EmptyCategoryException() {
    }

    public EmptyCategoryException(String message) {
        super(message);
    }
}
