package com.revature.Project_1.controller;

import com.revature.Project_1.exception.CustomException;
import com.revature.Project_1.exception.InvalidDateException;
import com.revature.Project_1.exception.InvalidIDException;
import com.revature.Project_1.model.DTO.IncomingReimbDTO;
import com.revature.Project_1.model.Reimbursement;
import com.revature.Project_1.service.ReimbursementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reimbursements")
@EnableMethodSecurity(securedEnabled = true)
@CrossOrigin(origins = "*")
public class ReimbursementController {

    private ReimbursementService reimbursementService;

    public ReimbursementController(ReimbursementService reimbursementService){
        this.reimbursementService = reimbursementService;
    }


    @PostMapping
    @Secured("Employee")
    public ResponseEntity<Reimbursement> createReimbursement(@RequestBody @Valid IncomingReimbDTO reimbDTO) throws CustomException {
        Reimbursement reimb = reimbursementService.createReimbursement(reimbDTO, username());
        return ResponseEntity.status(201).body(reimb);
    }

    @GetMapping
    @Secured({"Manager","Employee"})
    public ResponseEntity<List<Reimbursement>> getAllReimbursements(){
        List<Reimbursement> reimbursements;
        if(isManager())
            reimbursements = reimbursementService.getAllReimbursements();
        else
            reimbursements = reimbursementService.getLoggedInUserReimbursements(username());
        return ResponseEntity.ok(reimbursements);
    }

    @GetMapping("/pending")
    @Secured({"Manager","Employee"})
    public ResponseEntity<List<Reimbursement>> getPendingReimbursements(){
        List<Reimbursement> reimbursements;
        if(isManager())
            reimbursements = reimbursementService.getPendingReimbursements();
        else
            reimbursements = reimbursementService.getLoggedInUserPendingReimbursements(username());
        return ResponseEntity.ok(reimbursements);
    }

    @GetMapping("/approved")
    @Secured({"Manager","Employee"})
    public ResponseEntity<List<Reimbursement>> getApprovedReimbursements(){
        List<Reimbursement> reimbursements;
        if(isManager())
            reimbursements = reimbursementService.getApprovedReimbursements();
        else
            //TODO: Approved Reimbursements for Employee
            reimbursements = reimbursementService.getLoggedInUserPendingReimbursements(username());
        return ResponseEntity.ok(reimbursements);
    }

    @GetMapping("/denied")
    @Secured({"Manager","Employee"})
    public ResponseEntity<List<Reimbursement>> getDeniedReimbursements(){
        List<Reimbursement> reimbursements;
        if(isManager())
            reimbursements = reimbursementService.getDeniedReimbursements();
        else
            //TODO: Denied Reimbursements for Employee
            reimbursements = reimbursementService.getLoggedInUserPendingReimbursements(username());
        return ResponseEntity.ok(reimbursements);
    }

    //Employee can update description of their reimbursement
    @PatchMapping("/{id}")
    @Secured({"Employee"})
    public ResponseEntity<Object> updateReimbursementDescription(@PathVariable String id, @RequestBody String description) throws CustomException {
        try{
            int reimbursement_id = Integer.parseInt(id);
            var reimbursement = reimbursementService.updateReimbursementDescription(reimbursement_id, description, username());
            return ResponseEntity.ok(reimbursement);
        }catch (NumberFormatException ex){
            throw new InvalidIDException();
        }
    }


    //Resolve a reimbursement
    @PatchMapping("/resolve/{id}")
    @Secured({"Manager"})
    public ResponseEntity<Object> resolveReimbursement(@PathVariable String id, @RequestBody String status) throws CustomException {
        try{
            int reimbursement_id = Integer.parseInt(id);
            var reimbursement = reimbursementService.resolveReimbursementById(reimbursement_id,status,username());
            return ResponseEntity.ok(reimbursement);
        }catch (NumberFormatException ex){
            throw new InvalidIDException();
        }
    }

    /*
    @PatchMapping("/{id}")
    @Secured({"Manager","Employee"})
    public ResponseEntity<Object> updateReimbursement(@PathVariable String id, @RequestBody HashMap<String,String> newReimbursement) throws CustomException {
        try{
            int reimbursement_id = Integer.parseInt(id);
            var reimbursement = reimbursementService.updateReimbursementById(reimbursement_id, newReimbursement, isManager(), username());
            return ResponseEntity.ok(reimbursement);
        }catch (NumberFormatException ex){
            throw new InvalidIDException();
        }
    }
    */

    @GetMapping("/resolved/after/{date}")
    @Secured("Manager")
    public ResponseEntity<List<Reimbursement>> getReimbursementsResolvedAfter(@PathVariable String date, @RequestParam(defaultValue = "false") boolean by_me) throws InvalidDateException {
        List<Reimbursement> reimbursements = reimbursementService.getReimbursementsResolvedAfter(date, by_me, username());
        return ResponseEntity.ok(reimbursements);
    }

    @GetMapping("/resolved/before/{date}")
    @Secured("Manager")
    public ResponseEntity<List<Reimbursement>> getReimbursementsResolvedBefore(@PathVariable String date, @RequestParam(defaultValue = "false") boolean by_me) throws InvalidDateException {
        List<Reimbursement> reimbursements = reimbursementService.getReimbursementsResolvedBefore(date, by_me, username());
        return ResponseEntity.ok(reimbursements);
    }

    @GetMapping("/resolved/between/{date1}/{date2}")
    @Secured("Manager")
    public ResponseEntity<List<Reimbursement>> getReimbursementsResolvedByManager(@PathVariable String date1, @PathVariable String date2, @RequestParam(defaultValue = "false") boolean by_me) throws InvalidDateException {
        List<Reimbursement> reimbursements = reimbursementService.getReimbursementsResolvedBetween(date1, date2, by_me, username());
        return ResponseEntity.ok(reimbursements);
    }

    @GetMapping("/resolved_by_me")
    @Secured("Manager")
    public ResponseEntity<List<Reimbursement>> getReimbursementsResolvedByManager(){
        List<Reimbursement> reimbursements = reimbursementService.getReimbursementsResolvedByManager(username());
        return ResponseEntity.ok(reimbursements);
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
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Manager"));
    }

    private String username(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}