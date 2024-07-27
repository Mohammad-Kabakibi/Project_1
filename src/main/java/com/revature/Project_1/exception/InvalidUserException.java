package com.revature.Project_1.exception;

import java.util.HashMap;

public class InvalidUserException extends CustomException{
    private HashMap<String, String> map;
    public InvalidUserException() {
        map = new HashMap<>();
    }

    public void addMessage(String field, String msg){
        map.put(field, msg);
    }

    @Override
    public Object getMsg() {
        return map;
    }
}
