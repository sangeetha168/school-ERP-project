package com.example.schoolERP.project.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.schoolERP.project.model.Course;
import com.example.schoolERP.project.model.ExamScore;
import com.example.schoolERP.project.model.Student;
import com.example.schoolERP.project.service.ExamScoreService;
import com.example.schoolERP.project.service.CourseService;
import com.example.schoolERP.project.service.StudentService;

@RestController
@RequestMapping("/api/exam-scores")
public class ExamScoreController {

    private final ExamScoreService examScoreService;
    private final StudentService studentService;
    private final CourseService courseService;

    public ExamScoreController(ExamScoreService examScoreService,
                               StudentService studentService,
                               CourseService courseService) {
        this.examScoreService = examScoreService;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    // ✅ Add or Update Score
    @PostMapping("/add")
    public ExamScore addOrUpdateScore(
            @RequestParam Long studentId,
            @RequestParam Long courseId,
            @RequestParam Double marks,
            @RequestParam Double maxMarks) {

        Student student = studentService.getStudentById(studentId);
        Course course = courseService.getCourseById(courseId);

        return examScoreService.saveScore(student, course, marks, maxMarks);
    }

    // ✅ Get all scores of a student
    @GetMapping("/student/{studentId}")
    public List<ExamScore> getScoresByStudent(@PathVariable Long studentId) {
        Student student = studentService.getStudentById(studentId);
        return examScoreService.getScoresByStudent(student);
    }

    // ✅ Get all scores of a course (faculty view)
    @GetMapping("/course/{courseId}")
    public List<ExamScore> getScoresByCourse(@PathVariable Long courseId) {
        Course course = courseService.getCourseById(courseId);
        return examScoreService.getScoresByCourse(course);
    }

    // ✅ Get specific student + course score
    @GetMapping("/student/{studentId}/course/{courseId}")
    public List<ExamScore> getScoreByStudentAndCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {

        Student student = studentService.getStudentById(studentId);
        Course course = courseService.getCourseById(courseId);

        return examScoreService.getScoreByStudentAndCourse(student, course);
    }

    // ✅ Delete score
    @DeleteMapping("/{id}")
    public String deleteScore(@PathVariable Long id) {
        examScoreService.deleteScore(id);
        return "Score deleted successfully";
    }
}