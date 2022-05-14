package com.market.logic.survey.app.exception;

public class ValidationFailureException extends RuntimeException {

    public ValidationFailureException(String message){
        super(message);
    }

}
