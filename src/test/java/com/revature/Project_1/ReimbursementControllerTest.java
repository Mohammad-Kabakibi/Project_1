package com.revature.Project_1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.Project_1.DAO.UserDAO;
import com.revature.Project_1.JWT.JwtSecurityConfiguration;
import com.revature.Project_1.model.DTO.IncomingReimbDTO;
import com.revature.Project_1.model.Reimbursement;
import com.revature.Project_1.model.Role;
import com.revature.Project_1.model.User;
import com.revature.Project_1.service.ReimbursementService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class ReimbursementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ReimbursementService reimbursementService;

    @MockBean
    UserDAO userDAO;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testCreateReimbursement() throws Exception {
        IncomingReimbDTO incomingReimbDTO = new IncomingReimbDTO("some description", 100);

        when(reimbursementService.createReimbursement(incomingReimbDTO,1)).thenReturn(new Reimbursement());

        String token = get_token("Employee");

        mockMvc.perform(MockMvcRequestBuilders.post("/reimbursements")
                        .header("Authorization","Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingReimbDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/reimbursements")
                        .header("Authorization","Bearer "+token+"123") // wrong token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingReimbDTO)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(MockMvcRequestBuilders.post("/reimbursements")
                        // without a token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingReimbDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAllReimbursements() throws Exception {
        var list = Arrays.asList(new Reimbursement(), new Reimbursement());
        when(reimbursementService.getAllReimbursements()).thenReturn(list);
        when(reimbursementService.getLoggedInUserReimbursements(1)).thenReturn(list);

        String token = get_token("Employee");
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk());

        verify(reimbursementService,times(1)).getLoggedInUserReimbursements(1);


        token = get_token("Manager");
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk());

        verify(reimbursementService,times(1)).getAllReimbursements();
    }

    @Test
    public void testGetPendingReimbursements() throws Exception {
        var list = Arrays.asList(new Reimbursement(), new Reimbursement());
        when(reimbursementService.getPendingReimbursements()).thenReturn(list);
        when(reimbursementService.getLoggedInUserPendingReimbursements(1)).thenReturn(list);

        String token = get_token("Employee");
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/pending")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk());

        verify(reimbursementService,times(1)).getLoggedInUserPendingReimbursements(1);


        token = get_token("Manager");
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/pending")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk());

        verify(reimbursementService,times(1)).getPendingReimbursements();
    }

    @Test
    public void testResolveReimbursement() throws Exception {
//        var reimbursement = new Reimbursement();
//        when(reimbursementService.updateReimbursementDescription(anyInt(),anyString(),anyInt())).thenReturn(reimbursement);

        String token = get_token("Manager");
        mockMvc.perform(MockMvcRequestBuilders.patch("/reimbursements/resolve/1")
                        .header("Authorization","Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("approved")))
                .andExpect(status().isOk());

        // id as text instead of number
        mockMvc.perform(MockMvcRequestBuilders.patch("/reimbursements/resolve/text")
                        .header("Authorization","Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("approved")))
                .andExpect(status().isBadRequest());

        verify(reimbursementService,times(1)).resolveReimbursementById(anyInt(),anyString(), anyInt());
    }

    @Test
    public void testUpdateReimbursementDescription() throws Exception {
//        var reimbursement = new Reimbursement();
//        when(reimbursementService.updateReimbursementDescription(anyInt(),anyString(),anyInt())).thenReturn(reimbursement);

        String token = get_token("Employee");
        mockMvc.perform(MockMvcRequestBuilders.patch("/reimbursements/1")
                        .header("Authorization","Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("new description")))
                .andExpect(status().isOk());

        verify(reimbursementService,times(1)).updateReimbursementDescription(anyInt(),anyString(), anyInt());
    }

    @Test
    public void testGetReimbursementsResolvedAfter() throws Exception {
        var list = Arrays.asList(new Reimbursement(), new Reimbursement());
        when(reimbursementService.getReimbursementsResolvedAfter(anyString(),anyBoolean(),anyInt())).thenReturn(list);

        String token = get_token("Employee"); // forbidden
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/resolved/after/2020-10-10")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isForbidden());

        token = get_token("Manager");
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/resolved/after/2020-10-10")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk());

        token = get_token("Manager");
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/resolved/after/2020-10-10?by_me=true")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk());

        token = get_token("Manager");
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/resolved/after/2020-10-10?by_me=trueeee")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isBadRequest());

        //by_me = false (default)
        verify(reimbursementService,times(1)).getReimbursementsResolvedAfter("2020-10-10",false,1);
        //by_me = true
        verify(reimbursementService,times(1)).getReimbursementsResolvedAfter("2020-10-10",true,1);
    }

    @Test
    public void testGetReimbursementsResolvedBefore() throws Exception {
        var list = Arrays.asList(new Reimbursement(), new Reimbursement());
        when(reimbursementService.getReimbursementsResolvedBefore(anyString(),anyBoolean(),anyInt())).thenReturn(list);

        String token = get_token("Employee"); // forbidden
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/resolved/before/2020-10-10")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isForbidden());

        token = get_token("Manager");
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/resolved/before/2020-10-10")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk());

        token = get_token("Manager");
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/resolved/before/2020-10-10?by_me=true")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk());

        token = get_token("Manager");
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/resolved/before/2020-10-10?by_me=trueeee")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isBadRequest());

        //by_me = false (default)
        verify(reimbursementService,times(1)).getReimbursementsResolvedBefore("2020-10-10",false,1);
        //by_me = true
        verify(reimbursementService,times(1)).getReimbursementsResolvedBefore("2020-10-10",true,1);
    }

    @Test
    public void testGetReimbursementsResolvedBetween() throws Exception {
        var list = Arrays.asList(new Reimbursement(), new Reimbursement());
        when(reimbursementService.getReimbursementsResolvedBetween(anyString(),anyString(),anyBoolean(),anyInt())).thenReturn(list);

        String token = get_token("Employee"); // forbidden
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/resolved/between/2020-10-10/2021-10-10")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isForbidden());

        token = get_token("Manager");
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/resolved/between/2020-10-10/2021-10-10")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk());

        token = get_token("Manager");
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/resolved/between/2020-10-10/2021-10-10?by_me=true")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isOk());

        token = get_token("Manager");
        mockMvc.perform(MockMvcRequestBuilders.get("/reimbursements/resolved/between/2020-10-10/2021-10-10?by_me=trueeee")
                        .header("Authorization","Bearer "+token))
                .andExpect(status().isBadRequest());

        //by_me = false (default)
        verify(reimbursementService,times(1)).getReimbursementsResolvedBetween("2020-10-10","2021-10-10",false,1);
        //by_me = true
        verify(reimbursementService,times(1)).getReimbursementsResolvedBetween("2020-10-10","2021-10-10",true,1);
    }



    public String get_token(String role) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", "uname");
        map.put("password", "12345");

        User user = new User(1, "fname","lname","uname",null,null);
        user.setPassword(JwtSecurityConfiguration.passwordEncoder().encode("12345"));
        user.setRole(new Role(1,role));

        when(userDAO.findByUsername(anyString())).thenReturn(Optional.of(user));

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk()).andReturn();
        return new JSONObject(result.getResponse().getContentAsString()).get("token").toString();
    }
}
