package com.revature.Project_1.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenActionException extends CustomException{
    public ForbiddenActionException(){
        super("Forbidden Action.");
    }
    public ForbiddenActionException(String message){
        super(message);
    }

    @Override
    public int getStatus() {
        return HttpStatus.FORBIDDEN.value();
    }
}
