package com.revature.Project_1.service;

import com.revature.Project_1.DAO.UserDAO;
import com.revature.Project_1.JWT.JwtSecurityConfiguration;
import com.revature.Project_1.exception.*;
import com.revature.Project_1.model.Role;
import com.revature.Project_1.model.User;
import jakarta.validation.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserDAO userDAO;
    private RoleService roleService;

    public UserService() {
    }

    @Autowired
    public UserService(UserDAO userDAO, RoleService roleService) {
        this.userDAO = userDAO;
        this.roleService = roleService;
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public List<User> getAllEmployees() {
        return userDAO.findAllByRoleName("Employee");
    }

    public User getEmployeeById(int userId) throws UserNotFoundException {
        Optional<User> optionalUser = userDAO.findById(userId);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        }
        else{
            throw new UserNotFoundException(userId);
        }
    }

    public User createUser(User user) throws UsernameAlreadyExistsException{

        //Check if the username already exists
        if(userDAO.findByUsername(user.getUsername()).isPresent()){
            throw new UsernameAlreadyExistsException(user.getUsername());
        }

        //By default, assigning the Employee Role
        Role employeeRole = roleService.getEmployeeRole();
        user.setRole(employeeRole);

        String password = user.getPassword();
        user.setPassword(JwtSecurityConfiguration.passwordEncoder().encode(password));

        User createdUser = userDAO.save(user);

        return createdUser;
    }


    public User deleteUserById(int userId, int myId) throws CustomException {
        if(userId <= 0)
            throw new InvalidIDException();
        if(userId == myId)
            throw new ForbiddenActionException("You Cannot Delete Yourself");
        var user = userDAO.findById(userId);
        if(user.isEmpty())
            throw new UserNotFoundException(userId);

        userDAO.deleteById(userId);
        return user.get();
    }

    public User updateUserById(int userId, HashMap<String,Object> newUser) throws CustomException {
        if(userId <= 0)
            throw new InvalidIDException();
        var user_optional = userDAO.findById(userId);
        if(user_optional.isEmpty())
            throw new UserNotFoundException(userId);

        User user = user_optional.get();
        if(newUser.containsKey("role")){
//                int role_id = Integer.parseInt((String)newUser.get("role"));
//                Role role = roleService.getRoleById(role_id);
            Role role = roleService.getManagerRole();
            user.setRole(role);
        }
        return userDAO.save(user);
    }

    public User updateLoggedInUserById(int userId, HashMap<String,String> newUser) throws CustomException {
        User user = userDAO.findById(userId).get();

        if(newUser.containsKey("firstName"))
            user.setFirstName(newUser.get("firstName"));
        if(newUser.containsKey("lastName"))
            user.setLastName(newUser.get("lastName"));
        if(newUser.containsKey("username"))
            user.setUsername(newUser.get("username"));
        if(newUser.containsKey("password"))
            user.setPassword(newUser.get("password")); // not encrypting here for the validation

        try(var validator = Validation.buildDefaultValidatorFactory()){
            var errs = validator.getValidator().validate(user);
            if(!errs.isEmpty()){
                var exception = new InvalidUserException();
                errs.forEach(err -> exception.addMessage(err.getPropertyPath().toString(),err.getMessage()));
                throw exception;
            }
        }
        // checking if username already exists
        var u = userDAO.findByUsername(user.getUsername());
        if(u.isPresent())
            if(u.get().getUserId() != user.getUserId())
                throw new UsernameAlreadyExistsException(user.getUsername());

        if(newUser.containsKey("password")) { // encrypting the new password
            String password = newUser.get("password");
            user.setPassword(JwtSecurityConfiguration.passwordEncoder().encode(password));
        }
        
        return userDAO.save(user);
    }

    public Optional<User> getByUsername(String username) {
        return userDAO.findByUsername(username);
    }
}
