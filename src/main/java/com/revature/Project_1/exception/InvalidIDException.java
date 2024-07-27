package com.revature.Project_1.exception;

public class InvalidIDException extends CustomException{
    public InvalidIDException(){
        super("Please Enter an Integer Positive Number.");
    }
}
