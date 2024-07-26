package com.revature.Project_1.DAO;

import com.revature.Project_1.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDAO extends JpaRepository<Role,Integer> {

    Role findByName(String name);
}
