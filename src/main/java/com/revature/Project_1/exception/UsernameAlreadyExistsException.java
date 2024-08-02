package com.revature.Project_1.exception;

public class UsernameAlreadyExistsException extends CustomException{

    public UsernameAlreadyExistsException(String username){
        super("username {"+username+"} is already exists.");
    }

    public Object getMsg() {
        return new CustomMessage(super.getMessage());
    }

    private record CustomMessage(String username){}
}
