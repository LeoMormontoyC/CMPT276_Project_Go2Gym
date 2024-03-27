package com.project.go2gym.models;

import jakarta.persistence.*;

@Entity
// The user database
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uid;

    // we have 6 attributes
    private String name;
    private String username;
    private String password;
    private String email;
    private String userType; // userType whether the user is an admin, staff or a student
    private boolean membershipStatus; // if the status is "yes", then green color strip
    private String imagePath; // New field for image path
    // private boolean checkInStatus; // New field for check in status


    public User() {
        // empty default constructor
    }

    public User(String name, String username, String password, String email, String userType, boolean membershipStatus, String imagePath) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.userType = userType;
        this.membershipStatus = membershipStatus;
        this.imagePath = imagePath; // Include this new field
        // this.checkInStatus = checkInStatus; // Include this new field
    }

    public int getId() {
        return uid;
    }

    public void setId(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(boolean membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    // public boolean isCheckInStatus() {
    //     return checkInStatus;
    // }

    // public void setCheckInStatus(boolean checkInStatus) {
    //     this.checkInStatus = checkInStatus;
    // }
}