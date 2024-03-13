package com.project.go2gym.models;

import java.util.List;
//import java.util.Optional;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByUid(int userId);

    List<User> findByEmail(String email);

    List<User> findByUsernameAndPassword(String username, String password);

    List<User> findByUsername(String username);
    // Optional<User> findByUsernameAndPassword(String username, String password);

    List<User> findByNameStartingWithAndUserTypeIn(String name, Collection<String> userType);

    List<User> findByNameStartingWithAndUserTypeNot(String name, String userType);

    List<User> findByUserTypeNot(String userType);

    List<User> findByNameStartingWith(String name);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
