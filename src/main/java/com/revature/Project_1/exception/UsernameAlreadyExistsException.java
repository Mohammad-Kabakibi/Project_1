package com.revature.Project_1.exception;

public class UsernameAlreadyExistsException extends CustomException{

    public UsernameAlreadyExistsException(String username){
        super("The username {"+username+"} is already exists.");
    }

}
