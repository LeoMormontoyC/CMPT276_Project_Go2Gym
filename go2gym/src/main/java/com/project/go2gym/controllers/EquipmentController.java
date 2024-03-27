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


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.project.go2gym.models.Equipment;
import com.project.go2gym.models.EquipmentRepository;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.file.Path;

//import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StringUtils;

@Controller
public class EquipmentController {
    @Autowired
    private EquipmentRepository equipmentsRepository;


    @GetMapping("/admin/admin_equipment")
    public String adminEquipment(Model model) {
        List<Equipment> equipmentsList = equipmentsRepository.findAll(); // Fetch all equipment from the database
        model.addAttribute("equipments", equipmentsList); // Add the list to the model
        return "admin/admin_equipment"; // Return the view name
    }

    @GetMapping("/staff/staff_equipment")
    public String staffEquipment(Model model) {
        List<Equipment> equipmentsList = equipmentsRepository.findAll(); // Fetch all equipment from the database
        model.addAttribute("equipments", equipmentsList); // Add the list to the model
        return "staff/staff_equipment"; // Return the view name
    }

    @GetMapping("/equipment-form")
    public String showEquipmentForm() {
        return "forward:/static/equipment.html"; // Assuming the file is at 'src/main/resources/static/equipment.html'
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
            Equipment newEquipment = new Equipment(equipmentType, description, totalAmount, borrowed, inStock, imagePath);
            
            // Save the new equipment to the repository
            equipmentsRepository.save(newEquipment);

            // Add a success message and redirect to the admin equipment page
            redirectAttributes.addFlashAttribute("successMessage", "Equipment added successfully!");
            return "redirect:/admin/admin_equipment"; // Make sure this endpoint exists and is a GET mapping in your controller

        } catch (Exception e) {
            // If there is an error, add the error message and redirect back to the static form
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding equipment: " + e.getMessage());
            return "redirect:/static/equipment.html"; // Use the actual path to your static form
        }
    }


    // Method to save the image and return the file path
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


    //DELETING THE EQUIPMENT CONTROLLER
    @PostMapping("/equipments/delete/{uid}")
    public String deleteEquipment(
            @PathVariable(value = "uid") int uid,
            HttpServletResponse response) {
        System.out.println("DELETE equipment uid:");
        System.out.println(uid);
        try {
            Optional<Equipment> equipmentRecord = equipmentsRepository.findById(uid);
            if (equipmentRecord.isPresent()) {
                System.out.println("[DELETE]Success");
                equipmentsRepository.deleteById(uid);
                response.setStatus(205);
            } else {
                System.out.println("[DELETE]Record not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/admin_equipment";
    }

    // GETMAPPING FOR EDITING AN EQUIPMENT
    // this get request redirects to edit.html page
    @GetMapping("/equipments/edit/{uid}")
    public String gotoEditEquipment(
            @PathVariable(value = "uid") int uid,
            Model model,
            HttpServletResponse response) {
        // get all students from db
        Equipment equipment;
        try {
            equipment = equipmentsRepository.findById(uid).orElseThrow(() -> (new Exception("null")));
            // end of db call
            model.addAttribute("equipment", equipment);
            model.addAttribute("uid", uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // end of db call
        return "users/edit_equipment"; // Return the view name
    }

    // this endpoint is for submitting an edit and redirects to showAll pagenmnm
    @PostMapping("/equipments/edit/{uid}")
    public String editEquipment(
            @PathVariable(value = "uid") int uid,
            @RequestParam("newequimentType") String newequimentType,
            @RequestParam("newdescription") String newdescription,
            @RequestParam("newtotalAmount") double newtotalAmount,
            @RequestParam("newborrowed") double newborrowed,
            @RequestParam("newinStock") double newinStock,
            @RequestParam("image") MultipartFile image, // To handle image file
            RedirectAttributes redirectAttributes) {
        try {
            Equipment equipment = equipmentsRepository.findById(uid).orElseThrow(() -> new Exception("User not found"));

            // Update user details
            equipment.setEquipmentType(newequimentType);
            equipment.setDescription(newdescription);
            equipment.setTotalAmount(newtotalAmount); 
            equipment.setBorrowed(newborrowed);
            equipment.setInStock(newinStock);

            // Handle image upload
            if (!image.isEmpty()) {
                String imagePath = saveImage(image); // Call your image saving method
                if (imagePath != null && !imagePath.isEmpty()) {
                    equipment.setImagePath(imagePath); // Set the new image path
                }
            }

            // Save the updated user
            equipmentsRepository.save(equipment);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/admin/admin_equipment";
    }


    //SEARCH BAR FEATURE HERE
    @GetMapping("/equipment/filter")
    public String filterNamesEquipment(@RequestParam("search") String search, Model model) {
        // Assuming you have a service method to find users by name that starts with the
        // search string
        List<Equipment> filterEquipments = equipmentsRepository.findByEquipmentTypeStartingWith(search);
        model.addAttribute("equipments", filterEquipments);
        // Return the path to the fragment that generates the <tbody> part of your table
        return "admin/admin_equipment :: user-table";
    }

    //PROFILE FRAGMENT
    @GetMapping("/equipments/profile/{uid}")
    public String viewUserProfile(@PathVariable("uid") int uid, Model model) {
        Optional<Equipment> equipmentOptional = equipmentsRepository.findById(uid);
        if (equipmentOptional.isPresent()) {
            Equipment equipment = equipmentOptional.get();
            model.addAttribute("profileUser", equipment);
        } else {
            // Handle the case where user is not found
        }
        return "admin/admin_equipment"; // Return the dashboard view so the profile info can be updated on the
                                                  // sidebar
    }

    @GetMapping("/equipments/fragment/profile/{uid}")
    public String getUserProfileFragment(@PathVariable("uid") Integer userId, Model model) {
        Optional<Equipment> equipmentOpt = equipmentsRepository.findById(userId);
        if (equipmentOpt.isPresent()) {
            model.addAttribute("profileUser", equipmentOpt.get());
        } else {
            // handle user not found situation
        }
        return "admin/admin_equipment :: profile-fragment"; // Replace 'profile-fragment' with the actual
                                                                      // fragment identifier
    }
}
