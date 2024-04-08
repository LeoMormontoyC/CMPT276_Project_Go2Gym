package com.project.go2gym.service;

import com.project.go2gym.models.User;
import com.project.go2gym.models.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .stream()
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        System.out.println("Loading user: " + username + " with role: " +
                user.getUserType()); // Debugging line

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" +
                user.getUserType().toUpperCase());

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                Collections.singletonList(authority));
    }

    // @Override
    // public UserDetails loadUserByUsername(String username) throws
    // UsernameNotFoundException {
    // User user = userRepository.findByUsername(username)
    // .stream()
    // .findFirst()
    // .orElseThrow(() -> new UsernameNotFoundException("User not found with
    // username: " + username));

    // SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" +
    // user.getUserType().toUpperCase());
    // return new
    // org.springframework.security.core.userdetails.User(user.getUsername(),
    // user.getPassword(),
    // Collections.singletonList(authority));
    // }
}
