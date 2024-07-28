package com.revature.Project_1.controller;

import com.revature.Project_1.exception.CustomException;
import com.revature.Project_1.exception.InvalidIDException;
import com.revature.Project_1.model.DTO.IncomingReimbDTO;
import com.revature.Project_1.model.Reimbursement;
import com.revature.Project_1.service.ReimbursementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/reimbursements")
@EnableMethodSecurity(securedEnabled = true)
public class ReimbursementController {

    private ReimbursementService reimbursementService;

    public ReimbursementController(ReimbursementService reimbursementService){
        this.reimbursementService = reimbursementService;
    }


    @PostMapping
    @Secured("ROLE_Employee")
    public ResponseEntity<Reimbursement> createReimbursement(@RequestBody @Valid IncomingReimbDTO reimbDTO) throws CustomException {
        Reimbursement reimb = reimbursementService.createReimbursement(reimbDTO);
        return ResponseEntity.status(201).body(reimb);
    }

    @GetMapping
    @Secured({"ROLE_Manager","ROLE_Employee"})
    public ResponseEntity<List<Reimbursement>> getAllReimbursements(){
        List<Reimbursement> reimbursements;
        if(isManager())
            reimbursements = reimbursementService.getAllReimbursements();
        else
            reimbursements = reimbursementService.getLoggedInUserReimbursements();
        return ResponseEntity.ok(reimbursements);
    }

    @GetMapping("/pending")
    @Secured({"ROLE_Manager","ROLE_Employee"})
    public ResponseEntity<List<Reimbursement>> getPendingReimbursements(){
        List<Reimbursement> reimbursements;
        if(isManager())
            reimbursements = reimbursementService.getPendingReimbursements();
        else
            reimbursements = reimbursementService.getLoggedInUserPendingReimbursements();
        return ResponseEntity.ok(reimbursements);
    }


    @PatchMapping("/{id}")
    @Secured({"ROLE_Manager","ROLE_Employee"})
    public ResponseEntity<Object> updateReimbursement(@PathVariable String id, @RequestBody HashMap<String,Object> newReimbursement) throws CustomException {
        try{
            int reimbursement_id = Integer.parseInt(id);
            var reimbursement = reimbursementService.updateReimbursementById(reimbursement_id, newReimbursement, isManager());
            return ResponseEntity.ok(reimbursement);
        }catch (NumberFormatException ex){
            throw new InvalidIDException();
        }
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException( CustomException e){
        return ResponseEntity.status(e.getStatus()).body(e.getMsg());
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleException( Exception e){
//        return ResponseEntity.status(400).body(e.getMessage());
//    }

    private boolean isManager(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Manager"));
    }

}