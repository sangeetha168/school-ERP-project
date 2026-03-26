package com.example.schoolERP.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.schoolERP.project.model.Faculty;
import com.example.schoolERP.project.model.Timetable;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    // Faculty dashboard: get this faculty's full weekly schedule
    List<Timetable> findByFaculty(Faculty faculty);

    // Student dashboard: get the timetable for their class
    List<Timetable> findByClassNumber(Integer classNumber);

    // Get schedule for a specific faculty on a specific day
    List<Timetable> findByFacultyAndDayOfWeek(Faculty faculty, String dayOfWeek);

    // Get schedule for a specific class on a specific day
    List<Timetable> findByClassNumberAndDayOfWeek(Integer classNumber, String dayOfWeek);
}