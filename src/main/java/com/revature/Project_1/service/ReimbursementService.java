package com.revature.Project_1.service;

import com.revature.Project_1.DAO.ReimbursementDAO;
import com.revature.Project_1.model.Reimbursement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReimbursementService {
    private ReimbursementDAO reimbDAO;

    public ReimbursementService(ReimbursementDAO reimbDAO) {
        this.reimbDAO = reimbDAO;
    }

    public Reimbursement createReimbursement(Reimbursement reimb){

        //TODO: BUSINESS LOGIC: GET user_id from current session

        //Set status to Pending
        reimb.setStatus("Pending");

        Reimbursement createdReimb = reimbDAO.save(reimb);

        return createdReimb;
    }


}
