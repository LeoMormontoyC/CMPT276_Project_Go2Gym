package com.project.go2gym.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.project.go2gym.models.EquipmentRepository;

import javax.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

public class EquipmentControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipmentRepository equipmentsRepository;

    @Test
    public void testAdminEquipment_ReturnsCorrectModelAndView() throws Exception {
        // Prepare a mock list of equipment
        List<Equipment> mockEquipmentsList = new ArrayList<>();
        mockEquipmentsList.add(new Equipment(/* constructor args */));
        mockEquipmentsList.add(new Equipment(/* constructor args */));

        // Mock the findAll call to return the mock list
        when(equipmentsRepository.findAll()).thenReturn(mockEquipmentsList);

        // Perform the request and assert the response
        mockMvc.perform(get("/admin/admin_equipment"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("equipments"))
                .andExpect(model().attribute("equipments", mockEquipmentsList))
                .andExpect(view().name("admin/admin_equipment"));

        // Verify interactions
        verify(equipmentsRepository, times(1)).findAll();
    }

    @Test
    public void testStaffEquipment_ReturnsCorrectModelAndView() throws Exception {
        // Prepare a mock list of equipment
        List<Equipment> mockEquipmentsList = new ArrayList<>();
        mockEquipmentsList.add(new Equipment(/* constructor arguments */));
        mockEquipmentsList.add(new Equipment(/* constructor arguments */));

        // Mock the findAll call to return the mock list
        when(equipmentsRepository.findAll()).thenReturn(mockEquipmentsList);

        // Perform the request and assert the response
        mockMvc.perform(get("/staff/staff_equipment"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("equipments"))
                .andExpect(model().attribute("equipments", mockEquipmentsList))
                .andExpect(view().name("staff/staff_equipment"));

        // Verify interactions with the repository
        verify(equipmentsRepository, times(1)).findAll();
    }

    @PostMapping("/equipments/add")
    public String addEquipment(@RequestParam Map<String, String> formData,
            @RequestParam("image") MultipartFile image,
            RedirectAttributes redirectAttributes) {
        try {
            String equipmentType = formData.get("equimentType"); // Check the correct form name attribute
            String description = formData.get("description");
            double totalAmount = Double.parseDouble(formData.get("totalAmount"));
            double borrowed = Double.parseDouble(formData.get("borrowed"));
            double inStock = Double.parseDouble(formData.get("inStock"));

            // Assuming you have a method to save the image and return the file path
            String imagePath = saveImage(image); // Implement this method

            // Instantiate a new Equipment object
            Equipment newEquipment = new Equipment(equipmentType, description, totalAmount, borrowed, inStock,
                    imagePath);

            // Save the new equipment to the repository
            equipmentsRepository.save(newEquipment);

            // Add a success message and redirect to the admin equipment page
            redirectAttributes.addFlashAttribute("successMessage", "Equipment added successfully!");
            return "redirect:/admin/admin_equipment"; // Make sure this endpoint exists and is a GET mapping in your
                                                      // controller

        } catch (Exception e) {
            // If there is an error, add the error message and redirect back to the static
            // form
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding equipment: " + e.getMessage());
            return "redirect:/static/equipment.html"; // Use the actual path to your static form
        }
    }

    @Test
    public void testAddEquipment_Success() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "<<jpeg data>>".getBytes());
        Map<String, String> formData = Map.of(
                "equipmentType", "Type1",
                "description", "Description",
                "totalAmount", "10",
                "borrowed", "2",
                "inStock", "8");

        mockMvc.perform(multipart("/equipments/add")
                .file(image)
                .params(new LinkedMultiValueMap<String, String>() {
                    {
                        formData.forEach(this::add);
                    }
                })
                .with(csrf())) // Include CSRF token
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admin_equipment"))
                .andExpect(flash().attribute("successMessage", "Equipment added successfully!"));

        verify(equipmentsRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    public void testAddEquipment_Failure() throws Exception {
        doThrow(new RuntimeException("Database error")).when(equipmentsRepository).save(any(Equipment.class));

        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "<<jpeg data>>".getBytes());
        // Assuming the form data is correct but mocking a save failure
        mockMvc.perform(multipart("/equipments/add")
                .file(image)
                .param("equipmentType", "Type1")
                .param("description", "Description")
                .param("totalAmount", "10")
                .param("borrowed", "2")
                .param("inStock", "8")
                .with(csrf())) // CSRF token
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/static/equipment.html"))
                .andExpect(flash().attribute("errorMessage", "Error adding equipment: Database error"));

        verify(equipmentsRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    public void testDeleteEquipment_Success() throws Exception {
        int equipmentId = 1;
        Equipment mockEquipment = new Equipment(); // Assuming the existence of a suitable constructor or setter methods
        mockEquipment.setId(equipmentId);

        when(equipmentsRepository.findById(equipmentId)).thenReturn(Optional.of(mockEquipment));
        doNothing().when(equipmentsRepository).deleteById(equipmentId);

        mockMvc.perform(post("/equipments/delete/{uid}", equipmentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admin_equipment"));

        verify(equipmentsRepository, times(1)).deleteById(equipmentId);
    }

    @Test
    public void testDeleteEquipment_NotFound() throws Exception {
        int nonexistentEquipmentId = 99; // An ID that does not correspond to any equipment

        when(equipmentsRepository.findById(nonexistentEquipmentId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/equipments/delete/{uid}", nonexistentEquipmentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admin_equipment"));

        verify(equipmentsRepository, times(0)).deleteById(nonexistentEquipmentId);
    }

    @Test
    public void testGotoEditEquipment_WhenEquipmentExists() throws Exception {
        int equipmentId = 1;
        Equipment mockEquipment = new Equipment(); // Assuming the existence of a suitable constructor or setter methods
        mockEquipment.setId(equipmentId);
        // Optionally set additional fields here

        when(equipmentsRepository.findById(equipmentId)).thenReturn(Optional.of(mockEquipment));

        mockMvc.perform(get("/equipments/edit/{uid}", equipmentId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("equipment"))
                .andExpect(model().attribute("equipment", mockEquipment))
                .andExpect(model().attribute("uid", equipmentId))
                .andExpect(view().name("users/edit_equipment"));

        verify(equipmentsRepository, times(1)).findById(equipmentId);
    }

    @Test
    public void testGotoEditEquipment_WhenEquipmentDoesNotExist() throws Exception {
        int nonexistentEquipmentId = 99; // An ID that does not correspond to any equipment

        when(equipmentsRepository.findById(nonexistentEquipmentId)).thenReturn(Optional.empty());

        // Assuming modification to redirect to an error page or equipment list
        mockMvc.perform(get("/equipments/edit/{uid}", nonexistentEquipmentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/errorPage")); // Placeholder, replace with actual redirection target

        verify(equipmentsRepository, times(1)).findById(nonexistentEquipmentId);
    }

    @Test
    public void testEditEquipment_Success() throws Exception {
        int equipmentId = 1;
        Equipment mockEquipment = new Equipment(); // Set up mock equipment
        mockEquipment.setId(equipmentId);

        when(equipmentsRepository.findById(equipmentId)).thenReturn(Optional.of(mockEquipment));

        MockMultipartFile image = new MockMultipartFile("image", "filename.jpg", "text/plain", "some image".getBytes());

        mockMvc.perform(multipart("/equipments/edit/{uid}", equipmentId)
                .file(image)
                .param("newequimentType", "Type1")
                .param("newdescription", "Description")
                .param("newtotalAmount", "10")
                .param("newborrowed", "2")
                .param("newinStock", "8")
                .with(csrf())) // If CSRF protection is enabled
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admin_equipment"))
                .andExpect(flash().attribute("successMessage", "Profile updated successfully!"));

        verify(equipmentsRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    public void testEditEquipment_EquipmentNotFound() throws Exception {
        int nonexistentEquipmentId = 99; // An ID that does not correspond to any equipment

        when(equipmentsRepository.findById(nonexistentEquipmentId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/equipments/edit/{uid}", nonexistentEquipmentId)
                .param("newequimentType", "Type1")
                .with(csrf())) // Including CSRF token for completeness
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admin_equipment"))
                .andExpect(flash().attribute("errorMessage", "Error updating profile: User not found"));

        verify(equipmentsRepository, times(0)).save(any(Equipment.class));
    }

    @Test
    public void testFilterNamesEquipment_Success() throws Exception {
        // Prepare a mock list of filtered equipment
        List<Equipment> mockFilteredEquipments = new ArrayList<>();
        mockFilteredEquipments.add(new Equipment(/* constructor arguments */));
        mockFilteredEquipments.add(new Equipment(/* constructor arguments */));

        // Mock the findByEquipmentTypeStartingWith call to return the mock list
        when(equipmentsRepository.findByEquipmentTypeStartingWith("test")).thenReturn(mockFilteredEquipments);

        // Perform the request and assert the response
        mockMvc.perform(get("/equipment/filter")
                .param("search", "test"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("equipments"))
                .andExpect(model().attribute("equipments", mockFilteredEquipments))
                .andExpect(view().name("admin/admin_equipment :: user-table"));

        // Verify interactions with the repository
        verify(equipmentsRepository, times(1)).findByEquipmentTypeStartingWith("test");
    }

    @Test
    public void testViewEquipmentProfile_WhenEquipmentExists() throws Exception {
        int equipmentId = 1;
        Equipment mockEquipment = new Equipment(); // Assume the existence of a suitable constructor or setter methods
        mockEquipment.setId(equipmentId);
        // Optionally set additional fields here

        when(equipmentsRepository.findById(equipmentId)).thenReturn(Optional.of(mockEquipment));

        mockMvc.perform(get("/equipments/profile/{uid}", equipmentId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("profileUser"))
                .andExpect(model().attribute("profileUser", mockEquipment))
                .andExpect(view().name("admin/admin_equipment"));

        verify(equipmentsRepository, times(1)).findById(equipmentId);
    }

    @Test
    public void testViewEquipmentProfile_WhenEquipmentDoesNotExist() throws Exception {
        int nonexistentEquipmentId = 99; // An ID that does not correspond to any equipment

        when(equipmentsRepository.findById(nonexistentEquipmentId)).thenReturn(Optional.empty());

        // Assuming modification to handle non-existent equipment
        mockMvc.perform(get("/equipments/profile/{uid}", nonexistentEquipmentId))
                .andExpect(status().isOk()) // Or .is3xxRedirection() if redirecting
                // Optionally, check for an error message or redirection
                .andExpect(view().name("admin/admin_equipment")); // Or the view name for the error page

        verify(equipmentsRepository, times(1)).findById(nonexistentEquipmentId);
    }

    @Test
    public void testGetUserProfileFragment_EquipmentExists() throws Exception {
        Integer equipmentId = 1;
        Equipment mockEquipment = new Equipment(); // Assume the existence of a suitable constructor or setter methods
        mockEquipment.setId(equipmentId);
        // Optionally set additional fields on mockEquipment here

        when(equipmentsRepository.findById(equipmentId)).thenReturn(Optional.of(mockEquipment));

        mockMvc.perform(get("/equipments/fragment/profile/{uid}", equipmentId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("profileUser"))
                .andExpect(model().attribute("profileUser", mockEquipment))
                .andExpect(view().name("admin/admin_equipment :: profile-fragment"));

        verify(equipmentsRepository, times(1)).findById(equipmentId);
    }

    @Test
    public void testGetUserProfileFragment_EquipmentDoesNotExist() throws Exception {
        Integer nonexistentEquipmentId = 99; // An ID that does not correspond to any equipment

        when(equipmentsRepository.findById(nonexistentEquipmentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/equipments/fragment/profile/{uid}", nonexistentEquipmentId))
                .andExpect(status().isOk())
                // Since the method does not explicitly handle non-existence, there's no
                // attribute "profileUser"
                .andExpect(model().attributeDoesNotExist("profileUser"))
                .andExpect(view().name("admin/admin_equipment :: profile-fragment"));

        verify(equipmentsRepository, times(1)).findById(nonexistentEquipmentId);
    }

    @Test
    public void testGotoEditStaffEquipment_WhenEquipmentExists() throws Exception {
        int equipmentId = 1;
        Equipment mockEquipment = new Equipment(); // Assume Equipment has an appropriate constructor or setters
        mockEquipment.setId(equipmentId);
        // Set additional properties as needed for mockEquipment

        when(equipmentsRepository.findById(equipmentId)).thenReturn(Optional.of(mockEquipment));

        mockMvc.perform(get("/staffequipments/edit/{uid}", equipmentId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("equipment"))
                .andExpect(model().attribute("equipment", mockEquipment))
                .andExpect(model().attributeExists("uid"))
                .andExpect(view().name("users/editstaff_equipment"));

        verify(equipmentsRepository, times(1)).findById(equipmentId);
    }

    @Test
    public void testGotoEditStaffEquipment_WhenEquipmentDoesNotExist() throws Exception {
        int nonexistentEquipmentId = 99; // An ID that does not correspond to any equipment

        when(equipmentsRepository.findById(nonexistentEquipmentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/staffequipments/edit/{uid}", nonexistentEquipmentId))
                .andExpect(status().isOk())
                // Assuming some form of error handling is implemented, like showing an error
                // page
                .andExpect(view().name("users/editstaff_equipment")); // Adjust based on actual error handling

        verify(equipmentsRepository, times(1)).findById(nonexistentEquipmentId);
    }

    @BeforeEach
    void setUp() {
        doReturn("path/to/saved/image.jpg").when(staffEquipmentController).saveImage(any(MultipartFile.class));
    }

    @Test
    public void testEditStaffEquipment_Success() throws Exception {
        int equipmentId = 1;
        Equipment mockEquipment = new Equipment();
        mockEquipment.setId(equipmentId);

        when(equipmentsRepository.findById(equipmentId)).thenReturn(Optional.of(mockEquipment));

        MockMultipartFile image = new MockMultipartFile("image", "filename.jpg", "text/plain",
                "image content".getBytes());

        mockMvc.perform(multipart("/staffequipments/edit/{uid}", equipmentId)
                .file(image)
                .param("newequimentType", "New Type")
                .param("newdescription", "New Description")
                .param("newtotalAmount", "100.0")
                .param("newborrowed", "5.0")
                .param("newinStock", "95.0")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/staff_equipment"))
                .andExpect(flash().attribute("successMessage", "Profile updated successfully!"));

        verify(equipmentsRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    public void testEditStaffEquipment_EquipmentNotFound() throws Exception {
        int nonexistentEquipmentId = 99;

        when(equipmentsRepository.findById(nonexistentEquipmentId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/staffequipments/edit/{uid}", nonexistentEquipmentId)
                .param("newequimentType", "New Type")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/staff_equipment"))
                .andExpect(flash().attribute("errorMessage", "Error updating profile: User not found"));

        verify(equipmentsRepository, never()).save(any(Equipment.class));
    }

}
