package com.revature.Project_1.controller;

import com.revature.Project_1.exception.CustomException;
import com.revature.Project_1.exception.InvalidIDException;
import com.revature.Project_1.exception.UserNotFoundException;
import com.revature.Project_1.model.User;
import com.revature.Project_1.service.ReimbursementService;
import com.revature.Project_1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user")
@EnableMethodSecurity(securedEnabled = true)
@CrossOrigin(origins = "*")
public class UserController {

    private UserService userService;
    private ReimbursementService reimbursementService;

    @Autowired
    public UserController(UserService userService, ReimbursementService reimbursementService) {
        this.userService = userService;
        this.reimbursementService = reimbursementService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid User newUser) throws CustomException{

        User user = userService.createUser(newUser);

        return ResponseEntity.status(201).body(user);
    }

    //Exception handler for duplicate username
//    @ExceptionHandler(UsernameAlreadyExistsException.class)
//    public ResponseEntity<String> handleUsernameAlreadyExistsException( UsernameAlreadyExistsException e){
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
//    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException( CustomException e){
        return ResponseEntity.status(e.getStatus()).body(e.getMsg());
    }

    @GetMapping
    @Secured("Manager")
    //return employees only
    public ResponseEntity<List<User>> getAllEmployees(){
        var users = userService.getAllEmployees();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    @Secured("Manager")
    //return employees only
    public ResponseEntity<User> getEmployeeById(@PathVariable int userId) throws UserNotFoundException {
        var user = userService.getEmployeeById(userId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @Secured("Manager")
    public ResponseEntity<Object> deleteUser(@PathVariable String id) throws CustomException {
        try {
            int user_id = Integer.parseInt(id);
            var user = userService.deleteUserById(user_id, userId());
            return ResponseEntity.ok(user);
        }
        catch (NumberFormatException ex){ // if the parsing failed
//            return ResponseEntity.badRequest().body("ID must be an integer number.");
            throw new InvalidIDException();
        }
    }

    @PatchMapping("/{id}")
    @Secured("Manager")
    public ResponseEntity<Object> updateUser(@PathVariable String id, @RequestBody HashMap<String,Object> newUser) throws CustomException {
        try{
            int user_id = Integer.parseInt(id);
            var user = userService.updateUserById(user_id, newUser);
            return ResponseEntity.ok(user);
        }catch (NumberFormatException ex){
            throw new InvalidIDException();
        }
    }

    @PatchMapping
    @Secured({"Manager","Employee"})
    public ResponseEntity<Object> updateLoggedInUser(@RequestBody HashMap<String,String> newUser) throws CustomException {
        var user = userService.updateLoggedInUserById(userId(), newUser);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/reimbursements")
    @Secured("Manager")
    public ResponseEntity<Object> getReimbursementsByUserId(@PathVariable String id) throws CustomException {
        try{
            int user_id = Integer.parseInt(id);
            var reimbursements = reimbursementService.getReimbursementsByUserId(user_id);
            return ResponseEntity.ok(reimbursements);
        }catch (NumberFormatException ex){
            throw new InvalidIDException();
        }
    }

    private String username(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private int userId(){
        return Integer.parseInt(((Jwt)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaims().get("userId").toString());
    }
}
