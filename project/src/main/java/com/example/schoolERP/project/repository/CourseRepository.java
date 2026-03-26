package com.example.schoolERP.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.schoolERP.project.model.Course;
import com.example.schoolERP.project.model.Faculty;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByClassNumber(Integer classNumber);

    List<Course> findByFaculty(Faculty faculty);
}
