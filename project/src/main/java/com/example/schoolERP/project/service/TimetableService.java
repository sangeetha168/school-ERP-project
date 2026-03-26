package com.example.schoolERP.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.schoolERP.project.model.Course;
import com.example.schoolERP.project.model.Faculty;
import com.example.schoolERP.project.model.Timetable;
import com.example.schoolERP.project.repository.CourseRepository;
import com.example.schoolERP.project.repository.FacultyRepository;
import com.example.schoolERP.project.repository.TimetableRepository;

@Service
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;

    public TimetableService(TimetableRepository timetableRepository,
                            FacultyRepository facultyRepository,
                            CourseRepository courseRepository) {
        this.timetableRepository = timetableRepository;
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
    }

    public Timetable addTimetableSlot(Long facultyId,
                                      Long courseId,
                                      Integer classNumber,
                                      String dayOfWeek,
                                      String timeSlot,
                                      Integer periodNumber) {

        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Timetable slot = new Timetable();
        slot.setFaculty(faculty);
        slot.setCourse(course);
        slot.setClassNumber(classNumber);
        slot.setDayOfWeek(dayOfWeek);
        slot.setTimeSlot(timeSlot);
        slot.setPeriodNumber(periodNumber);

        return timetableRepository.save(slot);
    }

    public List<Timetable> getAllTimetableSlots() {
        return timetableRepository.findAll();
    }

    public void deleteTimetableSlot(Long id) {
        timetableRepository.deleteById(id);
    }

    public List<Timetable> getTimetableByFaculty(Faculty faculty) {
        return timetableRepository.findByFaculty(faculty);
    }

    public List<Timetable> getTimetableByClass(Integer classNumber) {
        return timetableRepository.findByClassNumber(classNumber);
    }
}