package com.project.go2gym.models;

import jakarta.persistence.*;

@Entity
// The equipment database
@Table(name = "equipments")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uid;

    // we have 6 attributes
    private String equimentType; // type of equipment what is it? dumbell etc
    private String description; // what is a dumbell used for? etc
    private double totalAmount; // total amount of equipment stock including borrowed + inStock
    private double borrowed; // # of equipments that are lended to students
    private double inStock; // # of equipment that are in the stock right now
    private String imagePath; // New field for image path of the equipment

    public Equipment() {
        // empty default constructor
    }

    public Equipment(String equimentType, String description, double totalAmount, double borrowed, double inStock,
            String imagePath) {
        this.equimentType = equimentType;
        this.description = description;
        this.totalAmount = totalAmount;
        this.borrowed = borrowed;
        this.inStock = inStock;
        this.imagePath = imagePath;
    }

    // Getters
    public int getUid() {
        return uid;
    }

    public String getEquimentType() {
        return equimentType;
    }

    public String getDescription() {
        return description;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getBorrowed() {
        return borrowed;
    }

    public double getInStock() {
        return inStock;
    }

    public String getImagePath() {
        return imagePath;
    }

    // Setters
    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setEquimentType(String equimentType) {
        this.equimentType = equimentType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setBorrowed(double borrowed) {
        this.borrowed = borrowed;
    }

    public void setInStock(double inStock) {
        this.inStock = inStock;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
