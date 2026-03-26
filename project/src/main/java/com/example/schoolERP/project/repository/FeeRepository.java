package com.example.schoolERP.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.schoolERP.project.model.Fee;
import com.example.schoolERP.project.model.Student;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    // Used when you have the full Student object (e.g. from student dashboard)
    Fee findByStudent(Student student);

    // Used when you only have the student's ID (e.g. from a form submission)
    Fee findByStudentId(Long studentId);
}