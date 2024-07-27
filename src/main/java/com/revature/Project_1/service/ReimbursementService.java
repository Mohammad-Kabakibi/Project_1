package com.revature.Project_1.service;

import com.revature.Project_1.DAO.ReimbursementDAO;
import com.revature.Project_1.DAO.UserDAO;
import com.revature.Project_1.exception.*;
import com.revature.Project_1.model.DTO.IncomingReimbDTO;
import com.revature.Project_1.model.Reimbursement;

import com.revature.Project_1.model.User;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public Reimbursement createReimbursement(Reimbursement reimbursement){

        Reimbursement reimb = new Reimbursement();

        reimb.setDescription(reimbursement.getDescription());
        reimb.setAmount(reimbursement.getAmount());
        //Set status to pending as default
        reimb.setStatus("pending");

        //TODO: BUSINESS LOGIC: GET user_id from current session
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDAO.findByUsername(username).get();
        reimb.setUser(user);

        Reimbursement createdReimb = reimbursementDAO.save(reimb);

        return createdReimb;
    }

    public List<Reimbursement> getLoggedInUserReimbursements() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return reimbursementDAO.findByUser_username(username);
    }

    public List<Reimbursement> getLoggedInUserPendingReimbursements() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return reimbursementDAO.findByStatusAndUser_username("pending", username);
    }


    public List<Reimbursement> getAllReimbursements() {
//        return reimbursementDAO.findAll(Sort.by("reimbId"));
        return reimbursementDAO.findAll();
    }

    public List<Reimbursement> getPendingReimbursements() {
        return reimbursementDAO.findByStatus("pending");
    }

    public List<Reimbursement> getReimbursementsByUserId(int userId) throws CustomException {
        if(userId <= 0)
            throw new InvalidIDException();
        if(userDAO.findById(userId).isEmpty())
            throw new UserNotFoundException(userId);
        return reimbursementDAO.findByUser_userId(userId);
    }

    public Reimbursement updateReimbursementById(int reimbursementId, HashMap<String, Object> newReimbursement) throws CustomException {
        var reimbursement_optional = reimbursementDAO.findById(reimbursementId);
        if(reimbursement_optional.isEmpty())
            throw new ReimbursementNotFoundException(reimbursementId);

        Reimbursement reimbursement = reimbursement_optional.get();

        if(newReimbursement.containsKey("description"))
            reimbursement.setDescription((String) newReimbursement.get("description"));
        if(newReimbursement.containsKey("amount"))
            reimbursement.setAmount(Float.parseFloat(newReimbursement.get("amount").toString()));
        if(newReimbursement.containsKey("status"))
            reimbursement.setStatus((String) newReimbursement.get("status"));

        try(var validator = Validation.buildDefaultValidatorFactory()){
            var errs = validator.getValidator().validate(reimbursement);
            if(!errs.isEmpty()){
                var exception = new InvalidReimbursementException();
                errs.forEach(err -> exception.addMessage(err.getPropertyPath().toString(),err.getMessage()));
                throw exception;
            }
        }
        return reimbursementDAO.save(reimbursement);
    }
}
