package com.project.go2gym.models;

import java.util.List;
//import java.util.Optional;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    List<Equipment> findByUid(int userId);
    List<Equipment> findByEquipmentTypeStartingWith(String equipmentType);

}
