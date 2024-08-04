package com.revature.Project_1.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends CustomException{
    public UnauthorizedException(String msg){
        super(msg);
    }

    @Override
    public int getStatus() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
