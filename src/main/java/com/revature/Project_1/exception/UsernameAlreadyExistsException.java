package com.revature.Project_1.exception;

public class UsernameAlreadyExistsException extends RuntimeException{

    public UsernameAlreadyExistsException(String username){
        super("The username {"+username+"} is already exists.");
    }

}
