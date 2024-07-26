package com.revature.Project_1.DAO;

import com.revature.Project_1.model.Reimbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReimbursementDAO extends JpaRepository<Reimbursement, Integer> {
}
