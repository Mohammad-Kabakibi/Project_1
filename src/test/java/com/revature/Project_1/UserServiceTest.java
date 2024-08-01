package com.revature.Project_1;

import com.revature.Project_1.DAO.UserDAO;
import com.revature.Project_1.exception.UsernameAlreadyExistsException;
import com.revature.Project_1.model.Role;
import com.revature.Project_1.model.User;
import com.revature.Project_1.service.RoleService;
import com.revature.Project_1.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserDAO userDAOMock;

    @Mock
    private RoleService roleService;

    @Spy
    private UserService userServiceSpy;

    @BeforeEach
    public void setup(){
        userServiceSpy = spy(new UserService(userDAOMock,roleService));
    }

    @Test
    public void createUserTest() throws UsernameAlreadyExistsException {

        //Creating testing model
        User returnedUser = new User();
        returnedUser.setUserId(101);
        returnedUser.setFirstName("FirstNameTest");
        returnedUser.setLastName("LastNameTest");
        returnedUser.setUsername("UsernameTest");
        returnedUser.setPassword("PasswordTest");

        //fake this user as it was never in the database
        when((userDAOMock.findByUsername(returnedUser.getUsername()))).thenReturn(Optional.empty());

        //stubbing userDAOMock.save()
        when(userDAOMock.save(any(User.class))).thenReturn(returnedUser);

        User cretedUser = userServiceSpy.createUser(returnedUser);


        Assertions.assertNotNull(cretedUser);

        verify(userDAOMock,times(1)).findByUsername(returnedUser.getUsername());

        verify(userDAOMock,times(1)).save(any(User.class));


    }

    @Test
    public void duplicateUsernameExceptionTest() {

        //Existing user
        String existingUsername = "existingUsername";
        User existingUser = new User();
        existingUser.setUsername(existingUsername);

        //New user - try to use existing username
        User newUser = new User();
        newUser.setUsername(existingUsername);


        //Excepted exception

        Exception expectedException = new UsernameAlreadyExistsException(existingUsername);

        //stubbing userDAOMock.findByUsername() the user already taken "existingUsername"
        when((userDAOMock.findByUsername(existingUsername))).thenReturn(Optional.of(existingUser));


        Exception thrownException = Assertions.assertThrows(expectedException.getClass(),()->{
            userServiceSpy.createUser(newUser);
        });

        verify(userDAOMock,times(1)).findByUsername(existingUsername);

        //check that save() never called
        verify(userDAOMock,times(0)).save(any(User.class));


        Assertions.assertEquals(expectedException.getMessage(),thrownException.getMessage());


    }

}
