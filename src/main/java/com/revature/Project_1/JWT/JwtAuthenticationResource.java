package com.revature.Project_1.JWT;

import java.time.Instant;
import java.util.HashMap;

import com.revature.Project_1.DAO.UserDAO;
import com.revature.Project_1.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
    public ResponseEntity<Object> authenticate(@RequestBody HashMap<String, String> login) {
        if(!(login.containsKey("username") && login.containsKey("password")))
            return ResponseEntity.badRequest().body("please enter username and password.");

        var user_optional = userDAO.findByUsername(login.get("username"));
        if(user_optional.isEmpty())
            return ResponseEntity.badRequest().body("No account found (username : "+login.get("username")+").");
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.get("username"),
                    login.get("password")));
        }catch (Exception ex){
            return ResponseEntity.badRequest().body("wrong password.");
        }
        User user = user_optional.get();
        return ResponseEntity.ok(new JwtResponse(createToken(authentication, user.getUserId())));
    }

    private String createToken(Authentication authentication, int id) {
        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject(authentication.getName())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60 * 60))
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

record JwtResponse(String token) {}
