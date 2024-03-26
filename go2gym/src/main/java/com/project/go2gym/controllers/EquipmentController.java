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
    public String adminEquipment() {
        // The name of the view to be returned (e.g., a Thymeleaf template name)
        // Make sure you have a template or page named "admin_equipment.html" in your resources
        return "admin/admin_equipment"; // This should match the name of your HTML file inside resources/templates (if using Thymeleaf) or your JSP file location.
    }

}
