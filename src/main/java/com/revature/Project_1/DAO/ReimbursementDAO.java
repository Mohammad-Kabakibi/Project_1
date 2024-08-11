package com.revature.Project_1.DAO;

import com.revature.Project_1.model.Reimbursement;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ReimbursementDAO extends JpaRepository<Reimbursement,Integer> {
    List<Reimbursement> findByStatus(String status);
    List<Reimbursement> findAll(Sort s);


    List<Reimbursement> findByUser_userId(int userId, Sort s); // when we use [_] the name after it is the property name

//    List<Reimbursement> findByUser_username(String username); // when we use [_] the name after it is the property name

    List<Reimbursement> findByStatusAndUser_userId(String pending, int userId);
//    List<Reimbursement> findByStatusAndUser_username(String status, String username);

//    List<Reimbursement> findByResolvedBy_username(String username);
    List<Reimbursement> findByResolvedBy_userId(int userId);

    List<Reimbursement> findByResolvedAtBefore(Date date);
    List<Reimbursement> findByResolvedAtAfter(Date date);
    List<Reimbursement> findByResolvedAtBetween(Date date1, Date date2);

    List<Reimbursement> findByResolvedAtAfterAndResolvedBy_userId(Date date, int userId);
    List<Reimbursement> findByResolvedAtBeforeAndResolvedBy_userId(Date date, int userId);
    List<Reimbursement> findByResolvedAtBetweenAndResolvedBy_userId(Date date1, Date date2, int userId);

    @Query("select sum(amount) from Reimbursement where status = :status")
    double findSumAmountByStatus(String status);

    @Query("SELECT SUM(t.amount) FROM Reimbursement t WHERE t.user.userId = :userId")
    Double findTotalAmountByUserId(int userId);

    @Query("SELECT SUM(t.amount) FROM Reimbursement t WHERE t.status = :status AND t.user.userId = :userId")
    Double findTotalAmountByUserIdAndStatus(int userId, String status);

    @Query("SELECT AVG(t.amount) FROM Reimbursement t WHERE t.user.userId = :userId")
    Double findAverageAmountByUserId(int userId);

}