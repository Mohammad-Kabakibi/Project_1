package com.revature.Project_1.service;

import com.revature.Project_1.DAO.UserDAO;
import com.revature.Project_1.JWT.JwtSecurityConfiguration;
import com.revature.Project_1.exception.CustomException;
import com.revature.Project_1.exception.InvalidIDException;
import com.revature.Project_1.exception.UserNotFoundException;
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

//        String password = user.getPassword();
//        user.setPassword(JwtSecurityConfiguration.passwordEncoder().encode(password));

        User createdUser = userDAO.save(user);

        return createdUser;
    }


    public User deleteUserById(int userId) throws CustomException {
        if(userId <= 0)
            throw new InvalidIDException();
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

        if(newUser.containsKey("firstName"))
            user.setFirstName((String) newUser.get("firstName"));
        if(newUser.containsKey("lastName"))
            user.setLastName((String) newUser.get("lastName"));
        if(newUser.containsKey("username"))
            user.setUsername((String) newUser.get("username"));
        if(newUser.containsKey("password")) {
            String password = (String) newUser.get("password");
            user.setPassword(JwtSecurityConfiguration.passwordEncoder().encode(password));
        }
        if(newUser.containsKey("role")){
//                int role_id = Integer.parseInt((String)newUser.get("role"));
//                Role role = roleService.getRoleById(role_id);
            Role role = roleService.getManagerRole();
            user.setRole(role);
        }
        return userDAO.save(user);
    }
}
