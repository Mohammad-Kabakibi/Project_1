package com.revature.Project_1.JWT;

import java.time.Instant;
import java.util.HashMap;

import com.revature.Project_1.DAO.UserDAO;
import com.revature.Project_1.exception.CustomException;
import com.revature.Project_1.exception.UnauthorizedException;
import com.revature.Project_1.model.DTO.LoggedInUserDetailsDTO;
import com.revature.Project_1.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class JwtAuthenticationResource {

    private JwtEncoder jwtEncoder;
    private UserDAO userDAO;
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationResource(JwtEncoder jwtEncoder, UserDAO userDAO, AuthenticationManager authenticationManager) {
        this.userDAO = userDAO;
        this.jwtEncoder = jwtEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Object> authenticate(@RequestBody HashMap<String, String> login) throws CustomException {
        if(!(login.containsKey("username") && login.containsKey("password")))
            return ResponseEntity.badRequest().body("please enter username and password.");

        var user_optional = userDAO.findByUsername(login.get("username"));
        if(user_optional.isEmpty()){
            throw new UnauthorizedException("No account found (username : "+login.get("username")+").");
        }
//            return ResponseEntity.badRequest().body("No account found (username : "+login.get("username")+").");
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.get("username"),
                    login.get("password")));
        }catch (Exception ex){
//            return ResponseEntity.badRequest().body("wrong password.");
            throw new UnauthorizedException("Wrong Password.");
        }
        User user = user_optional.get();
        //Create Logged In user details
        LoggedInUserDetailsDTO loggedInUserDetails = new LoggedInUserDetailsDTO();
        loggedInUserDetails.setFirstName(user.getFirstName());
        loggedInUserDetails.setLastName(user.getLastName());
        loggedInUserDetails.setUsername(user.getUsername());
        loggedInUserDetails.setRole(user.getRole().getName());
        return ResponseEntity.ok(new JwtResponse(createToken(authentication, user.getUserId()), user.getRole().getName(),loggedInUserDetails));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException( CustomException e){
        return ResponseEntity.status(e.getStatus()).body(e.getMsg());
    }

    private String createToken(Authentication authentication, int id) {
        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject(authentication.getName())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60 * 60 * 24))
                .claim("userId",id)
                .claim("scope", createScope(authentication))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    private String createScope(Authentication authentication) {
        var l = authentication.getAuthorities().stream().toList();
        return l.get(l.size()-1).toString();
//        return authentication.getAuthorities().stream()
//                .map(a -> a.getAuthority())
//                .collect(Collectors.joining(" "));
    }

}

record JwtResponse(String token, String role, LoggedInUserDetailsDTO loggedInUserDetails) {}
