package com.revature.Project_1.controller;

import com.revature.Project_1.model.Reimbursement;
import com.revature.Project_1.service.ReimbursementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reimb")
public class ReimbursementController {

    private ReimbursementService reimbService;

    public ReimbursementController(ReimbursementService reimbService) {
        this.reimbService = reimbService;
    }

    @PostMapping
    public ResponseEntity<Reimbursement> createReimbursement(@RequestBody Reimbursement reimb){

        Reimbursement createdReimb = reimbService.createReimbursement(reimb);

        return ResponseEntity.status(201).body(createdReimb);
    }

}
