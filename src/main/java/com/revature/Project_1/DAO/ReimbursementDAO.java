package com.revature.Project_1.DAO;

import com.revature.Project_1.model.Reimbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ReimbursementDAO extends JpaRepository<Reimbursement,Integer> {
    List<Reimbursement> findByStatus(String status);

    List<Reimbursement> findByUser_userId(int userId); // when we use [_] the name after it is the property name

    List<Reimbursement> findByUser_username(String username); // when we use [_] the name after it is the property name

    List<Reimbursement> findByStatusAndUser_username(String status, String username);

    List<Reimbursement> findByResolvedBy_username(String username);

    List<Reimbursement> findByResolvedAtBefore(Date date);
    List<Reimbursement> findByResolvedAtAfter(Date date);
    List<Reimbursement> findByResolvedAtBetween(Date date1, Date date2);
}