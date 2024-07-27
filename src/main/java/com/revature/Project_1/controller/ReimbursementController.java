package com.revature.Project_1.controller;

import com.revature.Project_1.model.DTO.IncomingReimbDTO;
import com.revature.Project_1.model.Reimbursement;
import com.revature.Project_1.service.ReimbursementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<Reimbursement> createReimbursement(@RequestBody IncomingReimbDTO reimbDTO){
        Reimbursement reimb = reimbursementService.createReimbursement(reimbDTO);

        return ResponseEntity.status(201).body(reimb);
    }

    @GetMapping
//    @Secured("ROLE_Manager")
    public ResponseEntity<List<Reimbursement>> getAllReimbursements(){
//        to get the user info from the token:
//        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
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
    public ResponseEntity<Object> updateReimbursement(@PathVariable String id, @RequestBody HashMap<String,Object> newReimbursement){
        try{
            int reimbursement_id = Integer.parseInt(id);
            var reimbursement = reimbursementService.updateReimbursementById(reimbursement_id, newReimbursement);
            return ResponseEntity.ok(reimbursement);
        }catch (Exception ex){ // later we'll catch custom exceptions...
            return ResponseEntity.badRequest().body("ID must be an integer number.");
        }
    }

}