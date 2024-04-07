package com.project.go2gym.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.go2gym.models.CalendarEvent;
import com.project.go2gym.models.CalendarEventRepository;

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
    
  

}
