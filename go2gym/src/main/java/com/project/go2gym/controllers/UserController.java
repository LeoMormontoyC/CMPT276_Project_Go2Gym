package com.project.go2gym.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
// import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.project.go2gym.models.User;
import com.project.go2gym.models.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Controller
public class UserController {
    @Autowired
    private UserRepository usersRepository;

    // my version for getmapping /login
    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("login");
    }

    @GetMapping("/users/all")
    public String getAllUsers(Model model) {
        System.out.println("Hello from all users");
        List<User> users = usersRepository.findAll(); // db
        model.addAttribute("users", users);
        return "redirect:/login";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        // Any necessary model attributes
        return "users/signup"; // This is the path to the Thymeleaf template inside
                               // `src/main/resources/templates/users/`
    }

    @GetMapping("/forgotpassword")
    public String showForgotPasswordForm(Model model) {
        return "users/forgotPassword";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam Map<String, String> newuser, Model model,
            RedirectAttributes redirectAttributes) {
        String username = newuser.get("username");
        String password = newuser.get("password");
        String email = newuser.get("email");

        // Password validation pattern
        Pattern pattern = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W]).{8,}$");
        Matcher matcher = pattern.matcher(password);

        // Check if the username or email already exists
        if (usersRepository.existsByUsername(username) || usersRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("signupError",
                    "Username or e-mail already exists. Please choose a different one.");
            return "redirect:/signup";
        } else if (!matcher.matches()) {
            // Check if the password meets the requirements
            model.addAttribute("signupError",
                    "Password does not meet the requirements. It must contain at least 8 characters, including UPPER/lowercase letters, numbers, and symbols.");
            return "redirect:/signup"; // Redirect back with an error
        }

        // If the username doesn't exist and the password meets the requirements,
        // proceed with creating the new user
        String newName = newuser.get("name");
        String newEmail = newuser.get("email");
        String newUserType = "STUDENT"; // Assuming default user type is STUDENT
        boolean newMembershipStatus = false; // Assuming default membership status

        User newUser = new User(newName, username, password, newEmail, newUserType, newMembershipStatus);
        usersRepository.save(newUser);

        redirectAttributes.addFlashAttribute("successMessage", "Successfully signed up! Welcome to GoGym :)");
        // After successful signup, redirect to the login page
        return "redirect:/login";
    }

    @GetMapping("/admin/admin_dashboard_protected")
    public String adminDashboardProtected(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute("session_user");
        if (sessionUser != null && "ADMIN".equals(sessionUser.getUserType())) {
            List<User> allUsers = usersRepository.findAll(); // Assuming userRepository is injected and has a findAll()
                                                             // method
            model.addAttribute("users", allUsers); // Add the list of users to the model
            model.addAttribute("admin", sessionUser); // Optionally keep this if you need to display admin-specific info
            return "admin/admin_dashboard_protected";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/staff/staff_dashboard")
    public String staffDashboard(Model model, HttpSession session) {
        // Similar check for staff members
        User sessionUser = (User) session.getAttribute("session_user");
        if (sessionUser != null && "STAFF".equals(sessionUser.getUserType())) {
            List<User> allUsers = usersRepository.findByUserTypeNot("ADMIN"); // Assuming userRepository is injected and
            // method
            model.addAttribute("users", allUsers); // Add the list of users to the model
            model.addAttribute("staff", sessionUser);
            return "staff/staff_dashboard";
        } else {
            // Redirect to login if the user is not staff or not logged in
            return "redirect:/login";
        }
    }

    @GetMapping("/users/showAll")
    public String usersShowAll(Model model, HttpSession session) {
        // You might want to limit this to admins or staff
        User sessionUser = (User) session.getAttribute("session_user");
        if (sessionUser != null && "STUDENT".equals(sessionUser.getUserType())) {
            List<User> users = usersRepository.findAll();
            model.addAttribute("student", users);
            return "users/showAll";
        } else {
            // Redirect to login if the user is not allowed to view all users
            return "redirect:/login";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model,
            HttpServletRequest request) {
        List<User> users = usersRepository.findByUsernameAndPassword(username, password);

        if (users.isEmpty()) {
            model.addAttribute("loginError", "Invalid username or password");
            return "users/login"; // Show the login page again with an error message
        }

        // Assuming username and password uniquely identify a user,
        // there should be only one match
        User user = users.get(0);
        request.getSession().setAttribute("session_user", user);
        model.addAttribute("user", user);

        // Redirect based on the user type using a private helper method
        return redirectToUserDashboard(user);
    }

    private String redirectToUserDashboard(User user) {
        switch (user.getUserType()) {
            case "ADMIN":
                return "redirect:/admin/admin_dashboard_protected";
            case "STAFF":
                return "redirect:/staff/staff_dashboard";
            case "STUDENT":
                return "redirect:/users/showAll";
            default:
                throw new IllegalStateException("Unknown user type: " + user.getUserType());
        }
    }

    @GetMapping("/logout")
    public String destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return "/users/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session) {
        // If there is already a session_user attribute, redirect to their dashboard
        User sessionUser = (User) session.getAttribute("session_user");
        if (sessionUser != null) {
            return redirectToUserDashboard(sessionUser);
        }
        // Otherwise, return the login view
        return "users/login";
    }

    @GetMapping("/admin/filter")
    public String filterNames(@RequestParam("search") String search, Model model) {
        // Assuming you have a service method to find users by name that starts with the
        // search string
        List<User> filteredUsers = usersRepository.findByNameStartingWith(search);
        model.addAttribute("users", filteredUsers);
        // Return the path to the fragment that generates the <tbody> part of your table
        return "admin/admin_dashboard_protected :: user-table";
    }

    @GetMapping("/staff/filter")
    public String filterNameStaff(@RequestParam("search") String search, Model model) {
        // Define the user types you want to include in the search

        // Update the repository call to use the new method
        List<User> filteredUsers = usersRepository.findByNameStartingWithAndUserTypeNot(search, "ADMIN");
        model.addAttribute("users", filteredUsers);

        // Return the path to the fragment that generates the <tbody> part of your table
        return "staff/staff_dashboard :: user-table";
    }

    // EDIT & REMOVE FUNTIONALITY HERE
    // POST request to delete student
    @PostMapping("/users/delete/{uid}")
    public String deleteUser(
            @PathVariable(value = "uid") int uid,
            HttpServletResponse response) {
        System.out.println("DELETE user uid:");
        System.out.println(uid);
        try {
            Optional<User> userRecord = usersRepository.findById(uid);
            if (userRecord.isPresent()) {
                System.out.println("[DELETE]Success");
                usersRepository.deleteById(uid);
                response.setStatus(205);
            } else {
                System.out.println("[DELETE]Record not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/admin_dashboard_protected";
    }

    // this get request redirects to edit.html page
    @GetMapping("/users/edit/{uid}")
    public String gotoEditUser(
            @PathVariable(value = "uid") int uid,
            Model model,
            HttpServletResponse response) {
        // get all students from db
        User user;
        try {
            user = usersRepository.findById(uid).orElseThrow(() -> (new Exception("null")));
            // end of db call
            model.addAttribute("user", user);
            model.addAttribute("uid", uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // end of db call
        return "users/edit";
    }

    // this endpoint is for submitting an edit and redirects to showAll page
    @PostMapping("/users/edit/{uid}")
    public String editUser(
            @PathVariable(value = "uid") int uid,
            @RequestParam Map<String, String> newUser,
            HttpServletResponse response) {
        try {
            System.out.println("EDIT user uid:");
            System.out.println(uid);
            String newName = newUser.get("newName");
            String newUsername = newUser.get("newUsername");
            String newPassword = newUser.get("newPassword");
            String newEmail = newUser.get("newEmail");
            boolean newMembershipStatus = Boolean.parseBoolean(newUser.get("newMembershipStatus"));
            User user = usersRepository.findById(uid).orElseThrow(() -> (new Exception("null")));
            user.setName(newName);
            user.setUsername(newUsername);
            user.setPassword(newPassword);
            user.setEmail(newEmail);
            user.setMembershipStatus(newMembershipStatus);
            usersRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // end of db call
        return "redirect:/admin/admin_dashboard_protected";
    }

}
