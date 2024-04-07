package com.project.go2gym.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Integer> {
    List<CalendarEvent> findByUid(int uid);
    List<CalendarEvent> findByinstructor(String instructor);
    
}
