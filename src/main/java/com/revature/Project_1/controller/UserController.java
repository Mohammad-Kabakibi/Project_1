package com.revature.Project_1.controller;

import com.revature.Project_1.exception.CustomException;
import com.revature.Project_1.exception.InvalidIDException;
import com.revature.Project_1.model.User;
import com.revature.Project_1.service.ReimbursementService;
import com.revature.Project_1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user")
@EnableMethodSecurity(securedEnabled = true)
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
    @Secured("ROLE_Manager")
    public ResponseEntity<List<User>> getAllUsers(){
        var users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_Manager")
    public ResponseEntity<Object> deleteUser(@PathVariable String id) throws CustomException {
        try {
            int user_id = Integer.parseInt(id);
            var user = userService.deleteUserById(user_id);
            return ResponseEntity.ok(user);
        }
        catch (NumberFormatException ex){ // if the parsing failed
//            return ResponseEntity.badRequest().body("ID must be an integer number.");
            throw new InvalidIDException();
        }
    }

    @PatchMapping("/{id}")
    @Secured("ROLE_Manager")
    public ResponseEntity<Object> updateUser(@PathVariable String id, @RequestBody HashMap<String,Object> newUser) throws CustomException {
        try{
            int user_id = Integer.parseInt(id);
            var user = userService.updateUserById(user_id, newUser);
            return ResponseEntity.ok(user);
        }catch (NumberFormatException ex){
            throw new InvalidIDException();
        }
    }

    @GetMapping("/{id}/reimbursements")
    @Secured("ROLE_Manager")
    public ResponseEntity<Object> getReimbursementsByUserId(@PathVariable String id) throws CustomException {
        try{
            int user_id = Integer.parseInt(id);
            var reimbursements = reimbursementService.getReimbursementsByUserId(user_id);
            return ResponseEntity.ok(reimbursements);
        }catch (NumberFormatException ex){
            throw new InvalidIDException();
        }
    }

}
