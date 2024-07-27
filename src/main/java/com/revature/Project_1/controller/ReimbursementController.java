package com.revature.Project_1.controller;

import com.revature.Project_1.exception.CustomException;
import com.revature.Project_1.exception.InvalidIDException;
import com.revature.Project_1.model.Reimbursement;
import com.revature.Project_1.service.ReimbursementService;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/reimbursements")
//@EnableMethodSecurity(securedEnabled = true)
public class ReimbursementController {

    private ReimbursementService reimbursementService;

    public ReimbursementController(ReimbursementService reimbursementService){
        this.reimbursementService = reimbursementService;
    }

    @PostMapping
    public ResponseEntity<Reimbursement> createReimbursement(@RequestBody @Valid Reimbursement reimbursement){
        Reimbursement reimb = reimbursementService.createReimbursement(reimbursement);
        return ResponseEntity.status(201).body(reimb);
    }

    @GetMapping
//    @Secured("ROLE_Manager")
    public ResponseEntity<List<Reimbursement>> getAllReimbursements(){
//        to get the user info from the token:
//        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        var reimbursements = reimbursementService.getAllReimbursements();
        return ResponseEntity.ok(reimbursements);
    }

    @GetMapping("/profile")
    public ResponseEntity<List<Reimbursement>> getLoggedInUserReimbursements(){
        var reimbursements = reimbursementService.getLoggedInUserReimbursements();
        return ResponseEntity.ok(reimbursements);
    }

    @GetMapping("/profile/pending")
    public ResponseEntity<List<Reimbursement>> getLoggedInUserPendingReimbursements(){
        var reimbursements = reimbursementService.getLoggedInUserPendingReimbursements();
        return ResponseEntity.ok(reimbursements);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Reimbursement>> getPendingReimbursements(){
        var reimbursements = reimbursementService.getPendingReimbursements();
        return ResponseEntity.ok(reimbursements);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateReimbursement(@PathVariable String id, @RequestBody HashMap<String,Object> newReimbursement) throws CustomException {
        try{
            int reimbursement_id = Integer.parseInt(id);
            var reimbursement = reimbursementService.updateReimbursementById(reimbursement_id, newReimbursement);
            return ResponseEntity.ok(reimbursement);
        }catch (NumberFormatException ex){
            throw new InvalidIDException();
        }
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException( CustomException e){
        return ResponseEntity.status(e.getStatus()).body(e.getMsg());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException( Exception e){
        return ResponseEntity.status(400).body(e.getMessage());
    }

}