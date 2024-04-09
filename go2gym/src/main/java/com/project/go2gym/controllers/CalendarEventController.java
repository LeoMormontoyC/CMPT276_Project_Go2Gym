package com.project.go2gym.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.go2gym.models.CalendarEvent;
import com.project.go2gym.models.CalendarEventRepository;
import org.springframework.web.bind.annotation.RequestParam;

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
import java.sql.Time;


//import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StringUtils;


@Controller
public class CalendarEventController {

    @Autowired
    private CalendarEventRepository calendarEventsRepository;

    @GetMapping("/admin/admin_schedule")
    public String calendarEvents(Model model){
        List<CalendarEvent> calendarList = calendarEventsRepository.findAll(); 
        model.addAttribute("calendarEvents", calendarList);
        return "admin/admin_schedule";
        
    }

    @GetMapping("/staff/staff_schedule")
    public String staffCalendar(Model model) {
        List<CalendarEvent> calendarList = calendarEventsRepository.findAll(); 
        model.addAttribute("calendarEvents", calendarList);
        return "staff/staff_schedule";
    }

    @PostMapping("/schedule/add")
    public String addSchedule(@RequestParam Map<String, String> formData,
                            RedirectAttributes redirectAttributes) {
        try {
            String name = formData.get("name");
            String startingTime = String.valueOf(formData.get("startingTime")); // This assumes the time is in the format "HH:mm:ss"
            String endingTime = String.valueOf(formData.get("endingTime")); // Same assumption as above
            String instructor = formData.get("instructor"); // Typo corrected from 'insturctor' to 'instructor'
            String description = formData.get("description");
            String daysOfClass = formData.get("daysofClass");

            // Instantiate a new CalendarEvent object
            CalendarEvent newEvent = new CalendarEvent(name, startingTime, endingTime, instructor, description, daysOfClass);
            
            // Save the new event to the repository
            calendarEventsRepository.save(newEvent); // Assuming your repository is named 'scheduleRepository'

            // Add a success message and redirect to the admin schedule page
            redirectAttributes.addFlashAttribute("successMessage", "Schedule added successfully!");
            return "redirect:/admin/admin_schedule"; // Make sure this endpoint exists and is a GET mapping in your controller

        } catch (Exception e) {
            // If there is an error, add the error message and redirect back to the static form
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding schedule: " + e.getMessage());
            return "redirect:/addSchedule.html"; // Use the actual path to your static form
        }
    }
    
    
  

}
