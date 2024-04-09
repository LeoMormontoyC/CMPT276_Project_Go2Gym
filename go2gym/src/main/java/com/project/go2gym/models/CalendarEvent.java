package com.project.go2gym.models;

import java.sql.Time;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
//Calender event database
@Table(name = "calendarEvents")
public class CalendarEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uid;

    //attributes

    // for startdate
    private String name;


    // @DateTimeFormat(pattern = "yyyy-MM-dd")
    // private Date dateStart;

    //time should be format HH:MM:SS
    private Time timeStart;
    //for end date
    // @DateTimeFormat(pattern = "yyyy-MM-dd")
    // private Date dateEnd;

    private Time timeEnd;

    private String instructor;

    private String description;
    private String daysofclass;

    public CalendarEvent(){

    }

    public CalendarEvent(String name, Time timeStart, Time timeEnd, String insturctor, String description, String daysofClass){
        this.name = name;
        // this.dateStart = dateStart;
        this.timeStart = timeStart;
        // this.dateEnd = dateEnd;
        this.timeEnd = timeEnd;
        this.description = description;
        this.daysofclass = daysofClass;
        

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }


    public Time getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Time timeStart) {
        this.timeStart = timeStart;
    }


    public Time getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Time timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDaysofclass() {
        return daysofclass;
    }

    public void setDaysofclass(String daysofclass) {
        this.daysofclass = daysofclass;
    } 
       
}
