package com.example.schoolERP.project.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.schoolERP.project.model.Attendance;
import com.example.schoolERP.project.model.Faculty;
import com.example.schoolERP.project.model.Student;
import com.example.schoolERP.project.model.User;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudent(Student student);

    Optional<Attendance> findByStudentAndDate(Student student, LocalDate date);
}
