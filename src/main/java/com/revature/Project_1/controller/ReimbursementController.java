package com.revature.Project_1.controller;

import com.revature.Project_1.model.Reimbursement;
import com.revature.Project_1.service.ReimbursementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/reimbursements")
public class ReimbursementController {

    private ReimbursementService reimbursementService;

    public ReimbursementController(ReimbursementService reimbursementService){
        this.reimbursementService = reimbursementService;
    }

    @GetMapping
    public ResponseEntity<List<Reimbursement>> getAllReimbursements(){
        var reimbursements = reimbursementService.getAllReimbursements();
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