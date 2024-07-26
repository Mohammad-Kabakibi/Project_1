package com.revature.Project_1.service;

import com.revature.Project_1.DAO.UserDAO;
import com.revature.Project_1.model.Role;
import com.revature.Project_1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserDAO userDAO;
    private RoleService roleService;

    @Autowired
    public UserService(UserDAO userDAO, RoleService roleService) {
        this.userDAO = userDAO;
        this.roleService = roleService;
    }

    public User createUser(User user){

        //TODO: Business Logic: check username already exists


        //By default, assigning the Employee Role
        Role employeeRole = roleService.getEmployeeRole();
        user.setRole(employeeRole);

        User createdUser = userDAO.save(user);

        return createdUser;
    }

}
