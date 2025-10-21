package com.coursehub.course_service.exception;

public class CategoryMismatchException extends RuntimeException {
    public CategoryMismatchException(String message) {
        super(message);
    }
}
