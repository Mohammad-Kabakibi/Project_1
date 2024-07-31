package com.revature.Project_1;

import com.revature.Project_1.DAO.ReimbursementDAO;
import com.revature.Project_1.DAO.UserDAO;
import com.revature.Project_1.model.DTO.IncomingReimbDTO;
import com.revature.Project_1.model.Reimbursement;
import com.revature.Project_1.model.User;
import com.revature.Project_1.service.ReimbursementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

        when(reimbursementDAO.save(any(Reimbursement.class))).thenReturn(reimbursement);
        when(userDAO.findByUsername("uname")).thenReturn(Optional.of(user));

        Reimbursement savedReimbursement = reimbursementService.createReimbursement(incomingReimbDTO,"uname");

        assertEquals(savedReimbursement, reimbursement);
        verify(reimbursementDAO, times(1)).save(any(Reimbursement.class));
    }

    @Test
    public void testGetAllReimbursements() throws Exception {
        List<Reimbursement> reimbursements = Arrays.asList(new Reimbursement(1, "some description", 1000, "pending", null, null, Date.from(Instant.now()), null),
                new Reimbursement(2, "some description", 1000, "pending", null, null, Date.from(Instant.now()), null),
                new Reimbursement(3, "some description", 1000, "pending", null, null, Date.from(Instant.now()), null));

        when(reimbursementDAO.findAll()).thenReturn(reimbursements);

        List<Reimbursement> returnedReimbursements = reimbursementService.getAllReimbursements();

        assertEquals(returnedReimbursements, reimbursements);
        verify(reimbursementDAO, times(1)).findAll();

    }
}
