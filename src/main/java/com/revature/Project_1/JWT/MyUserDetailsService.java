package com.revature.Project_1.JWT;

import com.revature.Project_1.DAO.UserDAO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

//@Service
public class MyUserDetailsService implements UserDetailsService {

    private UserDAO userDAO;

    public MyUserDetailsService(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    private Set<GrantedAuthority> set = new HashSet<>();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userDAO.findByUsername(username);
        GrantedAuthority authorities = new SimpleGrantedAuthority(user.get().getRole().getName());
        System.out.println(user.get().getRole().getName());
        set.clear();
        set.add(authorities);

        return new org.springframework.security.core.userdetails.User(username, user.get().getPassword(), set);
    }
}
