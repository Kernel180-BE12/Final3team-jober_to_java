package com.example.final_projects.exception.user;

public class UserException extends RuntimeException {
    private final UserErrorCode errorCode;
    private final Object details;

    public UserException(UserErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.details = null;
    }
    public UserException(UserErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }
    public UserException(UserErrorCode errorCode, String message, Object details){
        super(message);
        this.errorCode = errorCode;
        this.details =details;
    }

    public UserErrorCode getErrorCode() {


        return errorCode;
    }

    public Object getDetails() {
        return details;
    }
}
