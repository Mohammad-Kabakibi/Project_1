package com.revature.Project_1.service;

import com.revature.Project_1.DAO.ReimbursementDAO;
import com.revature.Project_1.DAO.UserDAO;
import com.revature.Project_1.exception.*;
import com.revature.Project_1.model.DTO.IncomingReimbDTO;
import com.revature.Project_1.model.Reimbursement;

import com.revature.Project_1.model.User;
import jakarta.validation.Validation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
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

    public Reimbursement createReimbursement(IncomingReimbDTO reimbursement) throws CustomException {

        Reimbursement reimb = new Reimbursement();

        reimb.setDescription(reimbursement.getDescription());
        reimb.setAmount(reimbursement.getAmount());
        //Set status to pending as default
        reimb.setStatus("pending");
        reimb.setCreatedAt(Date.from(Instant.now()));

        //TODO: BUSINESS LOGIC: GET user_id from current session
        User user = userDAO.findByUsername(username()).get();
        reimb.setUser(user);

        try(var validator = Validation.buildDefaultValidatorFactory()){
            var errs = validator.getValidator().validate(reimb);
            if(!errs.isEmpty()){
                var exception = new InvalidReimbursementException();
                errs.forEach(err -> exception.addMessage(err.getPropertyPath().toString(),err.getMessage()));
                throw exception;
            }
        }

        Reimbursement createdReimb = reimbursementDAO.save(reimb);

        return createdReimb;
    }

    public List<Reimbursement> getLoggedInUserReimbursements() {
        return reimbursementDAO.findByUser_username(username());
    }

    public List<Reimbursement> getLoggedInUserPendingReimbursements() {
        return reimbursementDAO.findByStatusAndUser_username("pending", username());
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

    public Reimbursement updateReimbursementById(int reimbursementId, HashMap<String, String> newReimbursement, boolean isManager) throws CustomException {
        var reimbursement_optional = reimbursementDAO.findById(reimbursementId);
        if(reimbursement_optional.isEmpty())
            throw new ReimbursementNotFoundException(reimbursementId);

        Reimbursement reimbursement = reimbursement_optional.get();

        if(!isManager && !reimbursement.getStatus().equals("pending"))
            throw new ForbiddenActionException("you cannot update non-pending reimbursements");

        if(newReimbursement.containsKey("description"))
            reimbursement.setDescription(newReimbursement.get("description"));
//        if(isManager && newReimbursement.containsKey("amount"))
//            reimbursement.setAmount(Float.parseFloat(newReimbursement.get("amount")));
        if(isManager && newReimbursement.containsKey("status")) {
            reimbursement.setStatus(newReimbursement.get("status"));
            reimbursement.setResolvedAt(Date.from(Instant.now()));
            reimbursement.setResolvedBy(userDAO.findByUsername(username()).get());
        }

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

    public List<Reimbursement> getReimbursementsResolvedByManager() {
        return reimbursementDAO.findByResolvedBy_username(username());
    }

    public List<Reimbursement> getReimbursementsResolvedBefore(String date_str, boolean by_me) throws InvalidDateException {
        Date date = valueOf(date_str);
        if(by_me)
            return reimbursementDAO.findByResolvedAtBeforeAndResolvedBy_username(date, username());
        return reimbursementDAO.findByResolvedAtBefore(date);
    }

    public List<Reimbursement> getReimbursementsResolvedAfter(String date_str, boolean by_me) throws InvalidDateException {
        Date date = valueOf(date_str);
        if(date.after(Date.from(Instant.now())))
            throw new InvalidDateException("Date cannot be in the future.");
        if(by_me)
            return reimbursementDAO.findByResolvedAtAfterAndResolvedBy_username(date, username());
        return reimbursementDAO.findByResolvedAtAfter(date);
    }

    public List<Reimbursement> getReimbursementsResolvedBetween(String date1_str, String date2_str, boolean by_me) throws InvalidDateException {
        Date date1 = valueOf(date1_str);
        Date date2 = valueOf(date2_str);
        if(date1.after(date2))
            throw new InvalidDateException("Date1 cannot be after Date2.");
        if(by_me)
            return reimbursementDAO.findByResolvedAtBetweenAndResolvedBy_username(date1, date2, username());
        return reimbursementDAO.findByResolvedAtBetween(date1, date2);
    }

//    public List<Reimbursement> getReimbursementsResolvedAfter(Date date) {
//        return reimbursementDAO.findByResolvedBy_username(username);
//    }
//
//    public List<Reimbursement> getReimbursementsResolvedBetween(Date date1, Date date2) {
//        return reimbursementDAO.findByResolvedBy_username(username);
//    }

    private Date valueOf(String date) throws InvalidDateException {
        try{
            return Date.valueOf(date);
        }catch (Exception q){
            throw new InvalidDateException();
        }
    }
    
    private String username(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
