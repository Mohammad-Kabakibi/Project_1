package com.revature.Project_1.exception;


import org.springframework.http.HttpStatus;

public class CustomException extends Exception{
    public CustomException(String msg){
        super(msg);
    }
    public CustomException(){
        super("Something Went Wrong.");
    }


    public Object getMsg() {
        return new CustomMessage(super.getMessage());
    }

    public int getStatus() {
        return HttpStatus.BAD_REQUEST.value();
    }

    private record CustomMessage(String message){}
}
