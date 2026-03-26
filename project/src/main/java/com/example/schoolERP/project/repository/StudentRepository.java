package com.example.schoolERP.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.schoolERP.project.model.Student;
import com.example.schoolERP.project.model.User;

public interface StudentRepository extends JpaRepository<Student, Long> {

    
    // Used after login to load a student's profile from their User account
    Student findByUser(User user);

    // Faculty dashboard: get all students in a specific class
    List<Student> findByClassNumber(Integer classNumber);

    // Admin: filter students by class and section
    List<Student> findByClassNumberAndSection(Integer classNumber, String section);
}
