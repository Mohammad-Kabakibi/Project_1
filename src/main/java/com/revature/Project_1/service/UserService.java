package com.revature.Project_1.service;

import com.revature.Project_1.DAO.UserDAO;
import com.revature.Project_1.exception.UsernameAlreadyExistsException;
import com.revature.Project_1.model.Role;
import com.revature.Project_1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class UserService {

    private UserDAO userDAO;
    private RoleService roleService;

    @Autowired
    public UserService(UserDAO userDAO, RoleService roleService) {
        this.userDAO = userDAO;
        this.roleService = roleService;
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public User createUser(User user) throws UsernameAlreadyExistsException{

        //Check if the username already exists
        if(userDAO.findByUsername(user.getUsername()).isPresent()){
            throw new UsernameAlreadyExistsException(user.getUsername());
        }

        //By default, assigning the Employee Role
        Role employeeRole = roleService.getEmployeeRole();
        user.setRole(employeeRole);

        User createdUser = userDAO.save(user);

        return createdUser;
    }


    public User deleteUserById(int userId) {
        var user = userDAO.findById(userId);
        if(user.isPresent()) {
            userDAO.deleteById(userId);
            return user.get();
        }
        else
            return null; // later we'll throw a custom exception(user not found)...
    }

    public User updateUserById(int userId, HashMap<String,Object> newUser) {
        var user_optional = userDAO.findById(userId);
        if(user_optional.isPresent()) {
            User user = user_optional.get();

            if(newUser.containsKey("firstName"))
                user.setFirstName((String) newUser.get("firstName"));
            if(newUser.containsKey("lastName"))
                user.setLastName((String) newUser.get("lastName"));
            if(newUser.containsKey("username"))
                user.setUsername((String) newUser.get("username"));
            if(newUser.containsKey("password"))
                user.setPassword((String) newUser.get("password"));
            if(newUser.containsKey("role")){
//                int role_id = Integer.parseInt((String)newUser.get("role"));
//                Role role = roleService.getRoleById(role_id);
                Role role = roleService.getManagerRole();
                user.setRole(role);
            }

            return userDAO.save(user);
        }
        else
            return null; // later we'll throw a custom exception(user not found)...
    }
}
