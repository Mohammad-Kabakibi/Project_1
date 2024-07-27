package com.revature.Project_1.exception;

import org.springframework.http.HttpStatus;

public class ReimbursementNotFoundException extends CustomException{
    public ReimbursementNotFoundException(int id){
        super("Reimbursement with ID:"+id+" Not Found.");
    }
    public ReimbursementNotFoundException(String message){
        super(message);
    }

    @Override
    public int getStatus() {
        return HttpStatus.NOT_FOUND.value();
    }
}
