package com.revature.Project_1.controller;

import com.revature.Project_1.exception.UsernameAlreadyExistsException;
import com.revature.Project_1.model.User;
import com.revature.Project_1.service.ReimbursementService;
import com.revature.Project_1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;
    private ReimbursementService reimbursementService;

    @Autowired
    public UserController(UserService userService, ReimbursementService reimbursementService) {
        this.userService = userService;
        this.reimbursementService = reimbursementService;
    }


    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User newUser){

        User user = userService.createUser(newUser);

        return ResponseEntity.status(201).body(user);
    }

    //Exception handler for duplicate username
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlreadyExistsException( UsernameAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        var users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable String id){
        try{
            int user_id = Integer.parseInt(id);
            var user = userService.deleteUserById(user_id);
            return ResponseEntity.ok(user);
        }catch (Exception ex){ // later we'll catch custom exceptions...
            return ResponseEntity.badRequest().body("ID must be an integer number.");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable String id, @RequestBody HashMap<String,Object> newUser){
        try{
            int user_id = Integer.parseInt(id);
            var user = userService.updateUserById(user_id, newUser);
            return ResponseEntity.ok(user);
        }catch (Exception ex){ // later we'll catch custom exceptions...
            return ResponseEntity.badRequest().body("ID must be an integer number.");
        }
    }

    @GetMapping("/{id}/reimbursements")
    public ResponseEntity<Object> getReimbursementsByUserId(@PathVariable String id){
        try{
            int user_id = Integer.parseInt(id);
            // todo: check if user exists...
            var reimbursements = reimbursementService.getReimbursementsByUserId(user_id);
            return ResponseEntity.ok(reimbursements);
        }catch (Exception ex){ // later we'll catch custom exceptions...
            return ResponseEntity.badRequest().body("ID must be an integer number.");
        }
    }
}
