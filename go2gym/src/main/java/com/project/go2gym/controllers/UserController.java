package com.project.go2gym.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
// import java.util.Arrays;

import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.file.Path;

//import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

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
    public ModelAndView showSignupForm(Model model) {
        ModelAndView mav = new ModelAndView("users/signup");
        // Any necessary model attributes
        return mav; // This is the path to the Thymeleaf template inside
                    // `src/main/resources/templates/users/`
    }

    @GetMapping("/forgotpassword")
    public ModelAndView showForgotPasswordForm() {
        return new ModelAndView("users/forgotPassword");
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam Map<String, String> newuser, @RequestParam("image") MultipartFile image,
            Model model,
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
        boolean newRole = false;
        String newNotes = newuser.get("notes");

        String imageUrl = saveImage(image); // Call the image saving method here

        // boolean newCheckInStatus = false; // Assuming default check-in status is
        // false

        User newUser = new User(newName, username, password, newEmail, newUserType, newMembershipStatus, imageUrl, newRole, newNotes);
        // newUser.setImagePath(imageUrl); // Set the image path for the new user
        usersRepository.save(newUser);

        redirectAttributes.addFlashAttribute("successMessage", "Successfully signed up! Welcome to GoGym :)");
        // After successful signup, redirect to the login page
        return "redirect:/login";
    }

    @GetMapping("/admin/admin_dashboard_protected")
    public String adminDashboardProtected(Model model, Authentication authentication) {
        // Check if the authenticated user has ROLE_ADMIN authority
        if (authentication != null
                && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            List<User> allUsers = usersRepository.findAll();
            model.addAttribute("users", allUsers);

            // Optionally, if you still need to add admin user details to the model
            String username = authentication.getName(); // Get the username from the Authentication object
            User adminUser = usersRepository.findByUsername(username)
                    .stream().findFirst()
                    .orElse(null); // Handle null appropriately

            model.addAttribute("admin", adminUser);
            return "admin/admin_dashboard_protected";
        } else {
            // Redirect to login if the user is not authenticated or does not have
            // ROLE_ADMIN
            return "redirect:/login";
        }
    }

    @GetMapping("/admin_dashboard_protected")
    public String adminDash() {
        return "admin/admin_dashboard_protected"; // This should match the name of your HTML file inside
                                                  // resources/templates (if using Thymeleaf) or your JSP file location.
    }

    // @GetMapping("/staff/staff_dashboard")
    // public String staffDashboard(Model model, HttpSession session) {
    // // Similar check for staff members
    // User sessionUser = (User) session.getAttribute("session_user");
    // if (sessionUser != null && "STAFF".equals(sessionUser.getUserType())) {
    // List<User> allUsers = usersRepository.findByUserTypeNot("ADMIN"); // Assuming
    // userRepository is injected and
    // // method
    // model.addAttribute("users", allUsers); // Add the list of users to the model
    // model.addAttribute("staff", sessionUser);
    // return "staff/staff_dashboard";
    // } else {
    // // Redirect to login if the user is not staff or not logged in
    // return "redirect:/login";
    // }
    // }

    @GetMapping("/staff/staff_dashboard")
    public String staffDashboard(Model model, Authentication authentication) {
        // Check if the authenticated user has ROLE_STAFF authority
        if (authentication != null
                && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STAFF"))) {
            List<User> allUsers = usersRepository.findByUserTypeNot("ADMIN");
            model.addAttribute("users", allUsers);

            // Optionally, if you still need to add staff user details to the model
            String username = authentication.getName(); // Get the username from the Authentication object
            User staffUser = usersRepository.findByUsername(username)
                    .stream().findFirst()
                    .orElse(null); // Handle null appropriately

            model.addAttribute("staff", staffUser);
            return "staff/staff_dashboard";
        } else {
            // Redirect to login if the user is not authenticated or does not have
            // ROLE_STAFF
            return "redirect:/login";
        }
    }

    // @GetMapping("/users/showAll")
    // public String memberCheckIn(Model model, HttpSession session) {
    // User sessionUser = (User) session.getAttribute("session_user");
    // if (sessionUser != null) {
    // // No need to find all users, just add the logged-in user to the model
    // model.addAttribute("user", sessionUser);
    // return "users/showAll"; // This should be the path to your Thymeleaf template
    // } else {
    // // Redirect to login if the user is not logged in
    // return "redirect:/login";
    // }
    // }

    @GetMapping("/users/showAll")
    public String memberCheckIn(Model model, Authentication authentication) {
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            // Fetch the current user's username from the Authentication object
            String username = authentication.getName();

            // Use userRepository to fetch the user details based on the username
            User currentUser = usersRepository.findByUsername(username)
                    .stream().findFirst()
                    .orElse(null); // Adjust this part based on your actual method's return type

            if (currentUser != null) {
                // Add the current user to the model
                model.addAttribute("user", currentUser);
                return "users/showAll"; // Path to the template
            }
        }
        // Redirect to login page if not authenticated or user is not found
        return "redirect:/login";
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
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false means: don't create if it doesn't exist
        if (session != null) {
            session.removeAttribute("session_user"); // Explicitly remove the user attribute
            session.invalidate(); // Invalidate session
        }
        return "redirect:/login"; // Redirect to login page
    }

    // public String destroySession(HttpServletRequest request) {
    // request.getSession().invalidate();
    // return "redirect:/login";
    // }

    // @GetMapping("/logout")
    // public String logout(HttpSession session) {
    // // Invalidating the session
    // session.invalidate();

    // // Redirect to login page or home page after logout
    // return "redirect:/login";
    // }

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

    // this endpoint is for submitting an edit and redirects to showAll pagenmnm
    @PostMapping("/users/edit/{uid}")
    public String editUser(
            @PathVariable(value = "uid") int uid,
            @RequestParam("newName") String newName,
            @RequestParam("newUsername") String newUsername,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("newEmail") String newEmail,
            @RequestParam("newMembershipStatus") String newMembershipStatus,
            @RequestParam("image") MultipartFile image, // To handle image file
            @RequestParam("role") String newRole,
            @RequestParam("newNotes") String newNotes,
            RedirectAttributes redirectAttributes) {
        try {
            User user = usersRepository.findById(uid).orElseThrow(() -> new Exception("User not found"));

            // Update user details
            user.setName(newName);
            user.setUsername(newUsername);
            user.setPassword(newPassword); // Consider encrypting the password before saving
            user.setEmail(newEmail);
            user.setMembershipStatus(Boolean.parseBoolean(newMembershipStatus));
            user.setRole(Boolean.parseBoolean(newRole));
            user.setNotes(newNotes);

            // Handle image upload
            if (!image.isEmpty()) {
                String imagePath = saveImage(image); // Call your image saving method
                if (imagePath != null && !imagePath.isEmpty()) {
                    user.setImagePath(imagePath); // Set the new image path
                }
            }

            // Save the updated user
            usersRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/admin/admin_dashboard_protected";
    }

    @GetMapping("/users/profile/{uid}")
    public String viewUserProfile(@PathVariable("uid") int uid, Model model) {
        Optional<User> userOptional = usersRepository.findById(uid);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("profileUser", user);
        } else {
            // Handle the case where user is not found
        }
        return "admin/admin_dashboard_protected"; // Return the dashboard view so the profile info can be updated on the
                                                  // sidebar
    }

    @GetMapping("/users/fragment/profile/{uid}")
    public String getUserProfileFragment(@PathVariable("uid") Integer userId, Model model) {
        Optional<User> userOpt = usersRepository.findById(userId);
        if (userOpt.isPresent()) {
            model.addAttribute("profileUser", userOpt.get());
        } else {
            // handle user not found situation
        }
        return "admin/admin_dashboard_protected :: profile-fragment"; // Replace 'profile-fragment' with the actual
                                                                      // fragment identifier
    }

    // helper method for saveImage method
    // helper method for saveImage method
    private String saveImage(MultipartFile image) {
        // The directory to upload to
        String uploadDirPath = "uploads"; // This is the directory name where files will be saved
        Path uploadDirectory = Paths.get(uploadDirPath);

        // Check if the directory exists, if not, attempt to create it
        if (Files.notExists(uploadDirectory)) {
            try {
                Files.createDirectories(uploadDirectory);
            } catch (IOException e) {
                // Handle the error accordingly (log it, notify the user, etc.)
                e.printStackTrace();
                return "default.jpg"; // Assuming you have a default image in your static images folder
            }
        }

        if (image.isEmpty()) {
            // Handle the case where no image is uploaded, perhaps setting a default image
            return "profile.jpg"; // Again, assuming a default image
        }

        try {
            String fileName = StringUtils.cleanPath(image.getOriginalFilename());
            Path filePath = uploadDirectory.resolve(fileName);
            if (!filePath.getParent().equals(uploadDirectory)) {
                // This is a security check to prevent path traversal attacks
                throw new StorageException("Cannot store file outside of the current directory.");
            }

            // Get the file bytes and write it to the resolved file path
            byte[] bytes = image.getBytes();
            Files.write(filePath, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            // Return only the file name, not the full path
            return fileName;
        } catch (IOException e) {
            // Handle the error accordingly
            e.printStackTrace();
            return "profile.jpg"; // Fallback to default image on error
        }
    }

    public class StorageException extends RuntimeException {

        public StorageException(String message) {
            super(message);
        }

        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // @GetMapping("/users/checkIn/{uid}")
    // public String gotocheckInUser(@PathVariable(value = "uid") int uid, Model
    // model, HttpServletResponse response)
    // {return "users/checkIn";}

    // // this endpoint is for submitting an edit and redirects to showAll page
    // @PostMapping("/users/checkIn/{uid}")
    // public String checkInUser(
    // @PathVariable(value = "uid") int uid,
    // @RequestParam Map<String, String> newUser,
    // HttpServletResponse response) {
    // try {
    // System.out.println("CHECKIN user uid:");
    // System.out.println(uid);
    // boolean newCheckInStatus =
    // Boolean.parseBoolean(newUser.get("newCheckInStatus"));
    // User user = usersRepository.findById(uid).orElseThrow(() -> (new
    // Exception("null")));
    // if (newCheckInStatus == true) {
    // user.setCheckInStatus(false);
    // } else {
    // user.setCheckInStatus(true);
    // }
    // usersRepository.save(user);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // // end of db call
    // if (newUser.get("newUserType").equals("ADMIN")) {
    // return "redirect:/admin/admin_dashboard_protected";
    // } else {
    // return "redirect:/staff/staff_dashboard";
    // }
    // }

    @PostMapping("/users/updateRole")
    public ResponseEntity<String> updateRole(@RequestParam("userId") Integer userId,
            @RequestParam("role") Boolean role) {
        Optional<User> userOptional = usersRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRole(role);
            usersRepository.save(user);
            return ResponseEntity.ok("Role updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/users/checkedInCount")
    @ResponseBody
    public Long getCheckedInCount() {
        return usersRepository.countByRoleAsBoolean();
    }



    @GetMapping("/users/crowdhistory")
    public String crowdHistory() {
        return "users/crowdhistory";
    }
    

}