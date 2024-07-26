package com.revature.Project_1.controller;

import com.revature.Project_1.model.User;
import com.revature.Project_1.service.ReimbursementService;
import com.revature.Project_1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
