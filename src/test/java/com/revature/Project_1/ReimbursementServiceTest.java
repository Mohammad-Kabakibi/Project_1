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
import org.springframework.data.domain.Sort;

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
        when(userDAO.findById(1)).thenReturn(Optional.of(user));

        Reimbursement savedReimbursement = reimbursementService.createReimbursement(incomingReimbDTO,1);

        assertEquals(savedReimbursement, reimbursement);
        assertThrows(InvalidReimbursementException.class, () -> reimbursementService.createReimbursement(invalid_incomingReimbDTO, 1));
        verify(reimbursementDAO, times(1)).save(any(Reimbursement.class));
    }

    @Test
    public void testGetAllReimbursements(){
        List<Reimbursement> reimbursements = Arrays.asList(new Reimbursement(1, "some description", 1000, "pending", null, null, Date.from(Instant.now()), null),
                new Reimbursement(2, "some description", 1000, "pending", null, null, Date.from(Instant.now()), null),
                new Reimbursement(3, "some description", 1000, "pending", null, null, Date.from(Instant.now()), null));

        when(reimbursementDAO.findAll(any(Sort.class))).thenReturn(reimbursements);

        List<Reimbursement> returnedReimbursements = reimbursementService.getAllReimbursements();

        assertEquals(returnedReimbursements, reimbursements);
        verify(reimbursementDAO, times(1)).findAll(any(Sort.class));

    }

    @Test
    public void testGetLoggedInUserReimbursements(){
        User user1 = new User(1, "fname", "lname", "uname1", "12345",null);
//        User user2 = new User(2, "fname", "lname", "uname2", "12345",null);
//
//        List<Reimbursement> all_reimbursements = Arrays.asList(new Reimbursement(1, "some description", 1000, "pending", user1, null, Date.from(Instant.now()), null),
//                new Reimbursement(2, "some description", 1000, "pending", user1, null, Date.from(Instant.now()), null),
//                new Reimbursement(3, "some description", 1000, "pending", user2, null, Date.from(Instant.now()), null));

        List<Reimbursement> user1_reimbursements = Arrays.asList(new Reimbursement(1, "some description", 1000, "pending", user1, null, Date.from(Instant.now()), null),
                new Reimbursement(2, "some description", 1000, "pending", user1, null, Date.from(Instant.now()), null));

        when(reimbursementDAO.findByUser_userId(anyInt(), any(Sort.class))).thenReturn(user1_reimbursements);

        List<Reimbursement> returnedReimbursements = reimbursementService.getLoggedInUserReimbursements(1);

        assertEquals(returnedReimbursements, user1_reimbursements);
        verify(reimbursementDAO, times(1)).findByUser_userId(anyInt(), any(Sort.class));
    }

    @Test
    public void testGetLoggedInUserPendingReimbursements(){
        List<Reimbursement> user1_reimbursements = Arrays.asList(new Reimbursement(),
                new Reimbursement());

        when(reimbursementDAO.findByStatusAndUser_userId("pending", 1)).thenReturn(user1_reimbursements);

        List<Reimbursement> returnedReimbursements = reimbursementService.getLoggedInUserPendingReimbursements(1);

        assertEquals(returnedReimbursements, user1_reimbursements);
        verify(reimbursementDAO, times(1)).findByStatusAndUser_userId("pending", 1);
    }

    @Test
    public void testGetReimbursementsByUserId() throws CustomException {
        List<Reimbursement> reimbursements = Arrays.asList(new Reimbursement(),
                new Reimbursement());
        User user = new User(1, "fname", "lname", "uname", "12345",null);

        when(reimbursementDAO.findByUser_userId(1, Sort.by(Sort.Direction.DESC,"ReimbId"))).thenReturn(reimbursements);
        when(userDAO.findById(1)).thenReturn(Optional.of(user)); // user exists
        when(userDAO.findById(2)).thenReturn(Optional.empty()); // user doesn't exist

        List<Reimbursement> returnedReimbursements = reimbursementService.getReimbursementsByUserId(1);

        assertEquals(returnedReimbursements, reimbursements);
        assertThrows(InvalidIDException.class, () -> reimbursementService.getReimbursementsByUserId(-1));
        assertThrows(UserNotFoundException.class, () -> reimbursementService.getReimbursementsByUserId(2));
        verify(reimbursementDAO, times(1)).findByUser_userId(1, Sort.by(Sort.Direction.DESC,"ReimbId"));
        verify(reimbursementDAO, times(0)).findByUser_userId(-1, Sort.by(Sort.Direction.DESC,"ReimbId"));
        verify(reimbursementDAO, times(0)).findByUser_userId(2, Sort.by(Sort.Direction.DESC,"ReimbId"));
    }

    @Test
    public void testResolveReimbursement() throws CustomException {
        Reimbursement reimbursement = new Reimbursement(1, "some description", 1000, "pending", null, null, Date.from(Instant.now()), null);

        when(reimbursementDAO.findById(1)).thenReturn(Optional.of(reimbursement));
        when(userDAO.findById(1)).thenReturn(Optional.of(new User()));
        when(reimbursementDAO.findById(2)).thenReturn(Optional.empty()); // Reimbursement doesn't exist
        when(reimbursementDAO.save(any(Reimbursement.class))).thenReturn(reimbursement);

//        HashMap<String, String> map = new HashMap<>();
        Reimbursement returnedReimbursement = reimbursementService.resolveReimbursementById(1,"approved",1);

        assertEquals(returnedReimbursement, reimbursement);
        assertThrows(InvalidIDException.class, () -> reimbursementService.resolveReimbursementById(-1,"approved",1));
        assertThrows(ReimbursementNotFoundException.class, () -> reimbursementService.resolveReimbursementById(2,"approved",1));
        assertThrows(InvalidReimbursementException.class, () -> reimbursementService.resolveReimbursementById(1,"",1));

        verify(reimbursementDAO, times(2)).findById(1);
        verify(reimbursementDAO, times(0)).findById(-1);
        verify(reimbursementDAO, times(1)).findById(2);
    }

    @Test
    public void testUpdateReimbursementDescription() throws CustomException {
        User user = new User(1, "fname", "lname", "uname", "12345",null);
        User other_user = new User(12, "fname", "lname", "uname", "12345",null);
        Reimbursement reimbursement = new Reimbursement(1, "some description", 1000, "pending", user, null, Date.from(Instant.now()), null);
        Reimbursement other_user_reimbursement = new Reimbursement(7, "some description", 1000, "pending", other_user, null, Date.from(Instant.now()), null);
        Reimbursement non_pending_reimbursement = new Reimbursement(77, "some description", 1000, "approved", user, null, Date.from(Instant.now()), null);

        when(reimbursementDAO.findById(1)).thenReturn(Optional.of(reimbursement));
        when(reimbursementDAO.findById(7)).thenReturn(Optional.of(other_user_reimbursement));
        when(reimbursementDAO.findById(77)).thenReturn(Optional.of(non_pending_reimbursement));
        when(reimbursementDAO.findById(2)).thenReturn(Optional.empty()); // Reimbursement doesn't exist
        when(reimbursementDAO.save(any(Reimbursement.class))).thenReturn(reimbursement);

//        HashMap<String, String> map = new HashMap<>();
        Reimbursement returnedReimbursement = reimbursementService.updateReimbursementDescription(1,"new description",1);

        assertEquals(returnedReimbursement, reimbursement);
        assertThrows(InvalidIDException.class, () -> reimbursementService.updateReimbursementDescription(-1,"new description",1));
        assertThrows(ReimbursementNotFoundException.class, () -> reimbursementService.updateReimbursementDescription(2,"new description",1));
        assertThrows(ForbiddenActionException.class, () -> reimbursementService.updateReimbursementDescription(7,"new description",1));
        assertThrows(ForbiddenActionException.class, () -> reimbursementService.updateReimbursementDescription(77,"new description",1));
        assertThrows(InvalidReimbursementException.class, () -> reimbursementService.updateReimbursementDescription(1,"",1));

        verify(reimbursementDAO, times(2)).findById(1);
        verify(reimbursementDAO, times(1)).findById(7);
        verify(reimbursementDAO, times(1)).findById(77);
        verify(reimbursementDAO, times(0)).findById(-1);
        verify(reimbursementDAO, times(1)).findById(2);
    }

    @Test
    public void testGetReimbursementsResolvedBefore() throws CustomException {
        List<Reimbursement> reimbursements = Arrays.asList(new Reimbursement(),
                new Reimbursement());

        Date date = Date.valueOf("2020-10-10");
        when(reimbursementDAO.findByResolvedAtBefore(date)).thenReturn(reimbursements);
        when(reimbursementDAO.findByResolvedAtBeforeAndResolvedBy_userId(date, 1)).thenReturn(reimbursements);

        List<Reimbursement> returnedReimbursements = reimbursementService.getReimbursementsResolvedBefore("2020-10-10",false, 1);
        List<Reimbursement> returnedReimbursements_by_me = reimbursementService.getReimbursementsResolvedBefore("2020-10-10",true, 1);

        assertEquals(returnedReimbursements, reimbursements);
        assertEquals(returnedReimbursements_by_me, reimbursements);
        assertThrows(InvalidDateException.class, () -> reimbursementService.getReimbursementsResolvedBefore("2020-jul-10",false, 1));
        verify(reimbursementDAO, times(1)).findByResolvedAtBeforeAndResolvedBy_userId(date,1);
        verify(reimbursementDAO, times(1)).findByResolvedAtBefore(date);
    }

    @Test
    public void testGetReimbursementsResolvedAfter() throws CustomException {
        List<Reimbursement> reimbursements = Arrays.asList(new Reimbursement(),
                new Reimbursement());

        Date date = Date.valueOf("2020-10-10");
        when(reimbursementDAO.findByResolvedAtAfter(date)).thenReturn(reimbursements);
        when(reimbursementDAO.findByResolvedAtAfterAndResolvedBy_userId(date, 1)).thenReturn(reimbursements);

        List<Reimbursement> returnedReimbursements = reimbursementService.getReimbursementsResolvedAfter("2020-10-10",false, 1);
        List<Reimbursement> returnedReimbursements_by_me = reimbursementService.getReimbursementsResolvedAfter("2020-10-10",true, 1);

        assertEquals(returnedReimbursements, reimbursements);
        assertEquals(returnedReimbursements_by_me, reimbursements);
        assertThrows(InvalidDateException.class, () -> reimbursementService.getReimbursementsResolvedAfter("2020-jul-10",false, 1));
        assertThrows(InvalidDateException.class, () -> reimbursementService.getReimbursementsResolvedAfter("2029-10-10",false, 1));
        verify(reimbursementDAO, times(1)).findByResolvedAtAfterAndResolvedBy_userId(date,1);
        verify(reimbursementDAO, times(1)).findByResolvedAtAfter(date);
    }

    @Test
    public void testGetReimbursementsResolvedBetween() throws CustomException {
        List<Reimbursement> reimbursements = Arrays.asList(new Reimbursement(),
                new Reimbursement());

        Date date1 = Date.valueOf("2020-10-10");
        Date date2 = Date.valueOf("2021-10-10");
        when(reimbursementDAO.findByResolvedAtBetween(date1,date2)).thenReturn(reimbursements);
        when(reimbursementDAO.findByResolvedAtBetweenAndResolvedBy_userId(date1, date2, 1)).thenReturn(reimbursements);

        List<Reimbursement> returnedReimbursements = reimbursementService.getReimbursementsResolvedBetween("2020-10-10", "2021-10-10",false, 1);
        List<Reimbursement> returnedReimbursements_by_me = reimbursementService.getReimbursementsResolvedBetween("2020-10-10", "2021-10-10",true, 1);

        assertEquals(returnedReimbursements, reimbursements);
        assertEquals(returnedReimbursements_by_me, reimbursements);
        assertThrows(InvalidDateException.class, () -> reimbursementService.getReimbursementsResolvedBetween("2020-jul-10","2021-10-10",false, 1));
        assertThrows(InvalidDateException.class, () -> reimbursementService.getReimbursementsResolvedBetween("2021-10-10","2020-10-10",false, 1));
        verify(reimbursementDAO, times(1)).findByResolvedAtBetweenAndResolvedBy_userId(date1, date2,1);
        verify(reimbursementDAO, times(1)).findByResolvedAtBetween(date1, date2);
    }

}
