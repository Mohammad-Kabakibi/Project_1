package com.revature.Project_1;

import com.revature.Project_1.DAO.ReimbursementDAO;
import com.revature.Project_1.DAO.UserDAO;
import com.revature.Project_1.exception.*;
import com.revature.Project_1.model.DTO.IncomingReimbDTO;
import com.revature.Project_1.model.Reimbursement;
import com.revature.Project_1.model.User;
import com.revature.Project_1.service.ReimbursementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReimbursementServiceTest {

    @Mock
    ReimbursementDAO reimbursementDAO;

    @Mock
    UserDAO userDAO;

    @InjectMocks
    ReimbursementService reimbursementService;

    @Test
    public void testCreateReimbursement() throws Exception {
        User user = new User(1, "fname", "lname", "uname", "12345",null);
        Reimbursement reimbursement = new Reimbursement(1, "some description", 1000, "pending", user, null, Date.from(Instant.now()), null);
        IncomingReimbDTO incomingReimbDTO = new IncomingReimbDTO("some description", 1000);
        IncomingReimbDTO invalid_incomingReimbDTO = new IncomingReimbDTO("s", -200);

        when(reimbursementDAO.save(any(Reimbursement.class))).thenReturn(reimbursement);
        when(userDAO.findByUsername("uname")).thenReturn(Optional.of(user));

        Reimbursement savedReimbursement = reimbursementService.createReimbursement(incomingReimbDTO,"uname");

        assertEquals(savedReimbursement, reimbursement);
        assertThrows(InvalidReimbursementException.class, () -> reimbursementService.createReimbursement(invalid_incomingReimbDTO, "uname"));
        verify(reimbursementDAO, times(1)).save(any(Reimbursement.class));
    }

    @Test
    public void testGetAllReimbursements(){
        List<Reimbursement> reimbursements = Arrays.asList(new Reimbursement(1, "some description", 1000, "pending", null, null, Date.from(Instant.now()), null),
                new Reimbursement(2, "some description", 1000, "pending", null, null, Date.from(Instant.now()), null),
                new Reimbursement(3, "some description", 1000, "pending", null, null, Date.from(Instant.now()), null));

        when(reimbursementDAO.findAll()).thenReturn(reimbursements);

        List<Reimbursement> returnedReimbursements = reimbursementService.getAllReimbursements();

        assertEquals(returnedReimbursements, reimbursements);
        verify(reimbursementDAO, times(1)).findAll();

    }

    @Test
    public void testgetLoggedInUserReimbursements(){
        User user1 = new User(1, "fname", "lname", "uname1", "12345",null);
//        User user2 = new User(2, "fname", "lname", "uname2", "12345",null);
//
//        List<Reimbursement> all_reimbursements = Arrays.asList(new Reimbursement(1, "some description", 1000, "pending", user1, null, Date.from(Instant.now()), null),
//                new Reimbursement(2, "some description", 1000, "pending", user1, null, Date.from(Instant.now()), null),
//                new Reimbursement(3, "some description", 1000, "pending", user2, null, Date.from(Instant.now()), null));

        List<Reimbursement> user1_reimbursements = Arrays.asList(new Reimbursement(1, "some description", 1000, "pending", user1, null, Date.from(Instant.now()), null),
                new Reimbursement(2, "some description", 1000, "pending", user1, null, Date.from(Instant.now()), null));

        when(reimbursementDAO.findByUser_username("uname1")).thenReturn(user1_reimbursements);

        List<Reimbursement> returnedReimbursements = reimbursementService.getLoggedInUserReimbursements("uname1");

        assertEquals(returnedReimbursements, user1_reimbursements);
        verify(reimbursementDAO, times(1)).findByUser_username("uname1");
    }

    @Test
    public void testGetLoggedInUserPendingReimbursements(){
        List<Reimbursement> user1_reimbursements = Arrays.asList(new Reimbursement(),
                new Reimbursement());

        when(reimbursementDAO.findByStatusAndUser_username("pending", "uname1")).thenReturn(user1_reimbursements);

        List<Reimbursement> returnedReimbursements = reimbursementService.getLoggedInUserPendingReimbursements("uname1");

        assertEquals(returnedReimbursements, user1_reimbursements);
        verify(reimbursementDAO, times(1)).findByStatusAndUser_username("pending", "uname1");
    }

    @Test
    public void testGetReimbursementsByUserId() throws CustomException {
        List<Reimbursement> reimbursements = Arrays.asList(new Reimbursement(),
                new Reimbursement());
        User user = new User(1, "fname", "lname", "uname", "12345",null);

        when(reimbursementDAO.findByUser_userId(1)).thenReturn(reimbursements);
        when(userDAO.findById(1)).thenReturn(Optional.of(user)); // user exists
        when(userDAO.findById(2)).thenReturn(Optional.empty()); // user doesn't exist

        List<Reimbursement> returnedReimbursements = reimbursementService.getReimbursementsByUserId(1);

        assertEquals(returnedReimbursements, reimbursements);
        assertThrows(InvalidIDException.class, () -> reimbursementService.getReimbursementsByUserId(-1));
        assertThrows(UserNotFoundException.class, () -> reimbursementService.getReimbursementsByUserId(2));
        verify(reimbursementDAO, times(1)).findByUser_userId(1);
        verify(reimbursementDAO, times(0)).findByUser_userId(-1);
        verify(reimbursementDAO, times(0)).findByUser_userId(2);
    }

    @Test
    public void testUpdateReimbursementById() throws CustomException {
        Reimbursement reimbursement = new Reimbursement(1, "some description", 1000, "pending", null, null, Date.from(Instant.now()), null);

        User user = new User(1, "fname", "lname", "uname", "12345",null);

        when(reimbursementDAO.findById(1)).thenReturn(Optional.of(reimbursement));
        when(reimbursementDAO.findById(2)).thenReturn(Optional.empty()); // Reimbursement doesn't exist
        when(reimbursementDAO.save(any(Reimbursement.class))).thenReturn(reimbursement);
        when(userDAO.findByUsername("uname")).thenReturn(Optional.of(user));

        HashMap<String, String> map = new HashMap<>();
        map.put("status","approved");
        Reimbursement returnedReimbursement = reimbursementService.updateReimbursementById(1,map,true,"uname");

        HashMap<String, String> invalid_map = new HashMap<>();
        invalid_map.put("status","");

        assertEquals(returnedReimbursement, reimbursement);
        assertThrows(InvalidIDException.class, () -> reimbursementService.updateReimbursementById(-1,map,true,"uname"));
        assertThrows(ReimbursementNotFoundException.class, () -> reimbursementService.updateReimbursementById(2,map,true,"uname"));
        assertThrows(InvalidReimbursementException.class, () -> reimbursementService.updateReimbursementById(1,invalid_map,true,"uname"));
        assertThrows(ForbiddenActionException.class, () -> reimbursementService.updateReimbursementById(1,map,false,"uname")); // if an employee tried to update the status
        verify(reimbursementDAO, times(3)).findById(1);
        verify(reimbursementDAO, times(0)).findById(-1);
        verify(reimbursementDAO, times(1)).findById(2);
    }

    @Test
    public void testGetReimbursementsResolvedBefore() throws CustomException {
        List<Reimbursement> reimbursements = Arrays.asList(new Reimbursement(),
                new Reimbursement());

        Date date = Date.valueOf("2020-10-10");
        when(reimbursementDAO.findByResolvedAtBefore(date)).thenReturn(reimbursements);
        when(reimbursementDAO.findByResolvedAtBeforeAndResolvedBy_username(date, "uname")).thenReturn(reimbursements);

        List<Reimbursement> returnedReimbursements = reimbursementService.getReimbursementsResolvedBefore("2020-10-10",false, "uname");
        List<Reimbursement> returnedReimbursements_by_me = reimbursementService.getReimbursementsResolvedBefore("2020-10-10",true, "uname");

        assertEquals(returnedReimbursements, reimbursements);
        assertEquals(returnedReimbursements_by_me, reimbursements);
        assertThrows(InvalidDateException.class, () -> reimbursementService.getReimbursementsResolvedBefore("2020-jul-10",false, "uname"));
        verify(reimbursementDAO, times(1)).findByResolvedAtBeforeAndResolvedBy_username(date,"uname");
        verify(reimbursementDAO, times(1)).findByResolvedAtBefore(date);
    }

    @Test
    public void testGetReimbursementsResolvedAfter() throws CustomException {
        List<Reimbursement> reimbursements = Arrays.asList(new Reimbursement(),
                new Reimbursement());

        Date date = Date.valueOf("2020-10-10");
        when(reimbursementDAO.findByResolvedAtAfter(date)).thenReturn(reimbursements);
        when(reimbursementDAO.findByResolvedAtAfterAndResolvedBy_username(date, "uname")).thenReturn(reimbursements);

        List<Reimbursement> returnedReimbursements = reimbursementService.getReimbursementsResolvedAfter("2020-10-10",false, "uname");
        List<Reimbursement> returnedReimbursements_by_me = reimbursementService.getReimbursementsResolvedAfter("2020-10-10",true, "uname");

        assertEquals(returnedReimbursements, reimbursements);
        assertEquals(returnedReimbursements_by_me, reimbursements);
        assertThrows(InvalidDateException.class, () -> reimbursementService.getReimbursementsResolvedAfter("2020-jul-10",false, "uname"));
        assertThrows(InvalidDateException.class, () -> reimbursementService.getReimbursementsResolvedAfter("2029-10-10",false, "uname"));
        verify(reimbursementDAO, times(1)).findByResolvedAtAfterAndResolvedBy_username(date,"uname");
        verify(reimbursementDAO, times(1)).findByResolvedAtAfter(date);
    }

    @Test
    public void testGetReimbursementsResolvedBetween() throws CustomException {
        List<Reimbursement> reimbursements = Arrays.asList(new Reimbursement(),
                new Reimbursement());

        Date date1 = Date.valueOf("2020-10-10");
        Date date2 = Date.valueOf("2021-10-10");
        when(reimbursementDAO.findByResolvedAtBetween(date1,date2)).thenReturn(reimbursements);
        when(reimbursementDAO.findByResolvedAtBetweenAndResolvedBy_username(date1, date2, "uname")).thenReturn(reimbursements);

        List<Reimbursement> returnedReimbursements = reimbursementService.getReimbursementsResolvedBetween("2020-10-10", "2021-10-10",false, "uname");
        List<Reimbursement> returnedReimbursements_by_me = reimbursementService.getReimbursementsResolvedBetween("2020-10-10", "2021-10-10",true, "uname");

        assertEquals(returnedReimbursements, reimbursements);
        assertEquals(returnedReimbursements_by_me, reimbursements);
        assertThrows(InvalidDateException.class, () -> reimbursementService.getReimbursementsResolvedBetween("2020-jul-10","2021-10-10",false, "uname"));
        assertThrows(InvalidDateException.class, () -> reimbursementService.getReimbursementsResolvedBetween("2021-10-10","2020-10-10",false, "uname"));
        verify(reimbursementDAO, times(1)).findByResolvedAtBetweenAndResolvedBy_username(date1, date2,"uname");
        verify(reimbursementDAO, times(1)).findByResolvedAtBetween(date1, date2);
    }

}
