package com.revature.Project_1.DAO;

import com.revature.Project_1.model.Reimbursement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReimbursementDAO extends JpaRepository<Reimbursement,Integer> {
    List<Reimbursement> findByStatus(String status);

    List<Reimbursement> findByUser_userId(int userId); // when we use [_] the name after it is the property name
}