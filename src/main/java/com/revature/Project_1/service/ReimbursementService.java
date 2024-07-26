package com.revature.Project_1.service;

import com.revature.Project_1.DAO.ReimbursementDAO;
import com.revature.Project_1.DAO.UserDAO;
import com.revature.Project_1.model.DTO.IncomingReimbDTO;
import com.revature.Project_1.model.Reimbursement;

import com.revature.Project_1.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ReimbursementService {


    private ReimbursementDAO reimbursementDAO;
    private UserDAO userDAO;

    public ReimbursementService(ReimbursementDAO reimbursementDAO, UserDAO userDAO) {
        this.reimbursementDAO = reimbursementDAO;
        this.userDAO = userDAO;
    }

    public Reimbursement createReimbursement(IncomingReimbDTO reimbDTO){

        Reimbursement reimb = new Reimbursement();

        reimb.setDescription(reimbDTO.getDescription());
        reimb.setAmount(reimbDTO.getAmount());
        //Set status to pending as default
        reimb.setStatus("pending");

        //TODO: BUSINESS LOGIC: GET user_id from current session
        User user = userDAO.findById(reimbDTO.getUserId()).get();
        reimb.setUser(user);

        Reimbursement createdReimb = reimbursementDAO.save(reimb);

        return createdReimb;
    }

    public List<Reimbursement> getLoggedInUserReimbursements() {
        int userId = 1;
        return reimbursementDAO.findByUser_userId(userId);
    }

    public List<Reimbursement> getLoggedInUserPendingReimbursements() {
        int userId = 1;
        return reimbursementDAO.findByStatusAndUser_userId("pending",userId);
    }



    public List<Reimbursement> getAllReimbursements() {
//        return reimbursementDAO.findAll(Sort.by("reimbId"));
        return reimbursementDAO.findAll();
    }

    public List<Reimbursement> getPendingReimbursements() {
        return reimbursementDAO.findByStatus("pending");
    }

    public List<Reimbursement> getReimbursementsByUserId(int userId) {
        return reimbursementDAO.findByUser_userId(userId);
    }

    public Reimbursement updateReimbursementById(int reimbursementId, HashMap<String, Object> newReimbursement) {
        var reimbursement_optional = reimbursementDAO.findById(reimbursementId);
        if(reimbursement_optional.isPresent()) {
            Reimbursement reimbursement = reimbursement_optional.get();

            if(newReimbursement.containsKey("description"))
                reimbursement.setDescription((String) newReimbursement.get("description"));
            if(newReimbursement.containsKey("amount"))
                reimbursement.setAmount(Float.parseFloat(newReimbursement.get("amount").toString()));
            if(newReimbursement.containsKey("status"))
                reimbursement.setStatus((String) newReimbursement.get("status"));

            return reimbursementDAO.save(reimbursement);
        }
        else
            return null; // later we'll throw a custom exception(user not found)...
    }
}
