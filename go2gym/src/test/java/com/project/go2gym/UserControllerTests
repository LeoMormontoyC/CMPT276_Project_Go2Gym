package com.project.go2gym.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;


@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository usersRepository; // Mock the UserRepository

    @Test
    public void testLogout() throws Exception {
        HttpSession session = new MockHttpSession();
        session.setAttribute("session_user", new User()); // Assume User class is properly constructed

        mockMvc.perform(get("/logout").session((MockHttpSession) session))
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testAddUser() throws Exception {
        Map<String, String> newUser = Map.of(
            "username", "testUser",
            "password", "Test@1234",
            "email", "test@example.com"
            // Add other fields as necessary
        );

        // Mock the behavior of your repository
        Mockito.when(usersRepository.existsByUsername(anyString())).thenReturn(false);
        Mockito.when(usersRepository.existsByEmail(anyString())).thenReturn(false);
        // Mock more interactions as necessary

        // Perform the post request
        mockMvc.perform(post("/users/add")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("username", newUser.get("username"))
                .param("password", newUser.get("password"))
                .param("email", newUser.get("email"))
                // Add other parameters as necessary
                // For file uploads, you'll use .file(...)
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login")); // Or wherever you redirect upon successful addition

        // Verify interactions with the mock
        Mockito.verify(usersRepository).save(any(User.class));
    }

    @Test
    public void testAddUserWithExistingUsername() throws Exception {
        // Mock repository behavior for an existing username
        Mockito.when(usersRepository.existsByUsername(anyString())).thenReturn(true);

        mockMvc.perform(post("/users/add")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "existingUser")
                .param("password", "Passw0rd!")
                .param("email", "newuser@example.com")
                // Include other necessary parameters
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/signup"));

        // Verify the user was not saved
        Mockito.verify(usersRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    @WithMockUser(username="adminUser", roles={"ADMIN"})
    public void testAdminDashboardAccessByAdmin() throws Exception {
        // Mock the user details to return for the admin
        User adminUser = new User(); // Setup admin user details
        adminUser.setUsername("adminUser");

        List<User> allUsers = List.of(new User(), new User()); // Mock some user data

        when(userRepository.findAll()).thenReturn(allUsers);
        when(userRepository.findByUsername("adminUser")).thenReturn(List.of(adminUser));

        mockMvc.perform(get("/admin/admin_dashboard_protected"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("admin"))
                .andExpect(view().name("admin/admin_dashboard_protected"));
    }

    @Test
    @WithMockUser(username="regularUser", roles={"USER"})
    public void testAdminDashboardAccessByNonAdmin() throws Exception {
        mockMvc.perform(get("/admin/admin_dashboard_protected"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testAdminDashboardAccessUnauthenticated() throws Exception {
        mockMvc.perform(get("/admin/admin_dashboard_protected"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login")); // Use pattern matching to accommodate any redirection strategy
    }

    @Test
    @WithMockUser(username="staffUser", roles={"STAFF"})
    public void testStaffDashboardAccessByStaff() throws Exception {
        // Setup mock data
        List<User> nonAdminUsers = List.of(new User(), new User()); // Assume these are non-admin users
        User staffUser = new User(); // Mock a staff user for the model
        staffUser.setUsername("staffUser");

        when(userRepository.findByUserTypeNot("ADMIN")).thenReturn(nonAdminUsers);
        when(userRepository.findByUsername("staffUser")).thenReturn(List.of(staffUser));

        mockMvc.perform(get("/staff/staff_dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("staff"))
                .andExpect(view().name("staff/staff_dashboard"));
    }

    @Test
    @WithMockUser(username="nonStaffUser", roles={"STUDENT"}) // Note: The role is not STAFF
    public void testStaffDashboardAccessByNonStaff() throws Exception {
        mockMvc.perform(get("/staff/staff_dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testStaffDashboardUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/staff/staff_dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login")); // Use pattern matching for flexibility
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"STUDENT"})
    public void testMemberCheckInWithAuthenticatedUser() throws Exception {
        User testUser = new User(); // Create a test user, set necessary fields
        testUser.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(List.of(testUser));

        mockMvc.perform(get("/users/showAll"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", testUser))
                .andExpect(view().name("users/showAll"));
    }

    @Test
    public void testMemberCheckInUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/users/showAll"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login")); // Use pattern matching to accommodate any redirection strategy
    }

    @Test
    @WithMockUser(username = "unknownUser", roles = {"STUDENT"})
    public void testMemberCheckInUserNotFound() throws Exception {
        when(userRepository.findByUsername("unknownUser")).thenReturn(List.of());

        mockMvc.perform(get("/users/showAll"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    public void testSuccessfulLoginAndRedirect() throws Exception {
        User adminUser = new User(); // Mock a user
        adminUser.setUsername("adminUser");
        adminUser.setUserType("ADMIN");

        when(userRepository.findByUsernameAndPassword("adminUser", "password")).thenReturn(List.of(adminUser));

        mockMvc.perform(post("/login")
                .param("username", "adminUser")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admin_dashboard_protected"));

        // Repeat this test block for "STAFF" and "STUDENT" roles with their respective redirects
    }

    @Test
    public void testUnsuccessfulLoginAttempt() throws Exception {
        when(userRepository.findByUsernameAndPassword("wrongUser", "wrongPassword")).thenReturn(List.of());

        mockMvc.perform(post("/login")
                .param("username", "wrongUser")
                .param("password", "wrongPassword"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("loginError"))
                .andExpect(view().name("users/login"));
    }

    // This is more of a logic test rather than a MockMvc test.
// Ensure redirectToUserDashboard redirects correctly based on userType
    @Test
    public void testRedirectToUserDashboard() {
        User studentUser = new User();
        studentUser.setUserType("STUDENT");
        String redirectUrl = userController.redirectToUserDashboard(studentUser);
        assertEquals("redirect:/users/showAll", redirectUrl);

        // Repeat for other user types (ADMIN, STAFF)
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        mockMvc.perform(get("/logout")
                .sessionAttr("session_user", new User())) // Assume User is a valid user object
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(request().sessionAttributeDoesNotExist("session_user"));
    }

    @Test
    public void testFilterNames() throws Exception {
        List<User> filteredUsers = new ArrayList<>();
        filteredUsers.add(new User()); // Add a mocked User to the list, ensure it matches the search criteria

        when(userRepository.findByNameStartingWith("test")).thenReturn(filteredUsers);

        mockMvc.perform(get("/admin/filter")
                .param("search", "test"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("users", filteredUsers))
                .andExpect(view().name("admin/admin_dashboard_protected :: user-table"));
    }

    @Test
    public void testFilterNameStaff() throws Exception {
        // Prepare a list of filtered users matching the search criteria and excluding ADMINs
        List<User> filteredUsers = new ArrayList<>();
        User testUser = new User(); // Mock a User object according to your User class
        // Set properties on testUser as needed
        filteredUsers.add(testUser);

        // Mock the repository call
        when(userRepository.findByNameStartingWithAndUserTypeNot("test", "ADMIN")).thenReturn(filteredUsers);

        // Perform the request and verify the outcomes
        mockMvc.perform(get("/staff/filter")
                .param("search", "test"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("users", filteredUsers))
                .andExpect(view().name("staff/staff_dashboard :: user-table"));
    }

    @Test
    public void testDeleteUserSuccess() throws Exception {
        int userId = 1;
        User mockUser = new User(); // Assuming a constructor or builder pattern to set fields
        mockUser.setId(userId);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).deleteById(userId);

        mockMvc.perform(post("/users/delete/{uid}", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admin_dashboard_protected"));

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {
        int userId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/delete/{uid}", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admin_dashboard_protected"));

        verify(userRepository, times(0)).deleteById(userId);
    }

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
    //profile/edit
}
