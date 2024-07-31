package com.revature.Project_1.exception;

public class InvalidDateException extends CustomException{
    public InvalidDateException(){
        super("Please use the format [yyyy-mm-dd]");
    }
    public InvalidDateException(String msg){
        super(msg);
    }
}
