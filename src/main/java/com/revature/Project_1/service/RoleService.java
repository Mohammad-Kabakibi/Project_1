package com.revature.Project_1.service;

import com.revature.Project_1.DAO.RoleDAO;
import com.revature.Project_1.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private RoleDAO roleDAO;

    @Autowired
    public RoleService(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    public Role getEmployeeRole(){
        return roleDAO.findByName("ROLE_Employee");
    }

    public Role getManagerRole(){
        return roleDAO.findByName("ROLE_Manager");
    }

//    public Role getRoleById(int roleId) {
//        var role = roleDAO.findById(roleId);
//        if(role.isPresent())
//            return role.get();
//        return null;
//    }
}
