package com.example.schoolERP.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.schoolERP.project.model.Course;
import com.example.schoolERP.project.model.Faculty;
import com.example.schoolERP.project.repository.CourseRepository;
import com.example.schoolERP.project.repository.FacultyRepository;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;

    public CourseService(CourseRepository courseRepository,
                         FacultyRepository facultyRepository) {
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
    }

    // ── Admin: Add a new course and assign it to a faculty member ─────────────
    @Transactional
    public Course addCourse(String courseName, Integer classNumber, Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + facultyId));

        Course course = new Course();
        course.setCourseName(courseName);
        course.setClassNumber(classNumber);
        course.setFaculty(faculty);
        return courseRepository.save(course);
    }

    // ── Admin: Update course name, class, or reassign to different faculty ─────
    @Transactional
    public Course updateCourse(Long courseId, String courseName, Integer classNumber, Long facultyId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + facultyId));

        course.setCourseName(courseName);
        course.setClassNumber(classNumber);
        course.setFaculty(faculty);
        return courseRepository.save(course);
    }

    // ── Admin: Delete a course ────────────────────────────────────────────────
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    // ── Get all courses (Admin view) ──────────────────────────────────────────
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // ── Get courses for a specific class (Student dashboard view) ─────────────
    public List<Course> getCoursesByClass(Integer classNumber) {
        return courseRepository.findByClassNumber(classNumber);
    }

    // ── Get courses taught by a specific faculty (Faculty dashboard view) ──────
    public List<Course> getCoursesByFaculty(Faculty faculty) {
        return courseRepository.findByFaculty(faculty);
    }

    // ── Get a single course by ID ─────────────────────────────────────────────
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
    }

    // ── Count total courses (Admin dashboard overview card) ───────────────────
    public long countCourses() {
        return courseRepository.count();
    }
}