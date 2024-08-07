package com.revature.Project_1.DAO;

import com.revature.Project_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User,Integer> {

    Optional<User> findByUsername(String username);

    List<User> findAllByRoleName(String roleName);

}
