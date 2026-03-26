package com.example.schoolERP.project.controller;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.schoolERP.project.model.*;
import com.example.schoolERP.project.service.*;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final AttendanceService attendanceService;
    private final ExamScoreService examScoreService;
    private final FeeService feeService;
    private final TimetableService timetableService;

    public FacultyController(FacultyService facultyService,
                             StudentService studentService,
                             CourseService courseService,
                             AttendanceService attendanceService,
                             ExamScoreService examScoreService,
                             FeeService feeService,
                             TimetableService timetableService) {
        this.facultyService = facultyService;
        this.studentService = studentService;
        this.courseService = courseService;
        this.attendanceService = attendanceService;
        this.examScoreService = examScoreService;
        this.feeService = feeService;
        this.timetableService = timetableService;
    }

    // ─────────────────────────────
    // DASHBOARD
    // ─────────────────────────────
    @GetMapping("/dashboard")
    public String facultyDashboard(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   Model model) {

        if (userDetails == null) return "redirect:/login";

        Faculty faculty = facultyService.getFacultyByUser(userDetails.getUser());
        if (faculty == null) return "redirect:/login";

        List<Course> myCourses = courseService.getCoursesByFaculty(faculty);
        List<Timetable> myTimetable = timetableService.getTimetableByFaculty(faculty);

        List<Integer> classNumbers = myCourses.stream()
                .map(Course::getClassNumber)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("faculty", faculty);
        model.addAttribute("myCourses", myCourses);
        model.addAttribute("myTimetable", myTimetable);
        model.addAttribute("classNumbers", classNumbers);

        return "faculty/faculty_dashboard";
    }

    // ─────────────────────────────
    // STUDENTS VIEW
    // ─────────────────────────────
    @GetMapping("/students")
    public String viewStudents(@RequestParam(required = false) Integer classNumber,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {

        Faculty faculty = facultyService.getFacultyByUser(userDetails.getUser());
        List<Course> myCourses = courseService.getCoursesByFaculty(faculty);

        List<Integer> classNumbers = myCourses.stream()
                .map(Course::getClassNumber)
                .distinct()
                .sorted()
                .toList();

        List<Student> students;

        if (classNumber != null) {
            students = studentService.getStudentsByClass(classNumber);
        } else {
            students = myCourses.stream()
                    .map(c -> studentService.getStudentsByClass(c.getClassNumber()))
                    .flatMap(List::stream)
                    .distinct()
                    .toList();
        }

        model.addAttribute("faculty", faculty);
        model.addAttribute("students", students);
        model.addAttribute("myCourses", myCourses);
        model.addAttribute("classNumbers", classNumbers);
        model.addAttribute("selectedClass", classNumber);

        return "faculty/manage_students";
    }

    // ─────────────────────────────
    // ADD STUDENT (🔥 FIXED)
    // ─────────────────────────────
    @PostMapping("/students/add")
    public String addStudent(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String phone,
                             @RequestParam Integer classNumber,
                             @RequestParam String section,
                             @RequestParam String rollNumber,
                             RedirectAttributes redirectAttributes) {

        try {
            Student student = studentService.addStudent(
                    name, email, phone, classNumber, section, rollNumber
            );

            feeService.createFeeRecord(student, 0.0, "2024-25");

            redirectAttributes.addFlashAttribute("success", "Student added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add student: " + e.getMessage());
        }

        return "redirect:/faculty/students";
    }

    // ─────────────────────────────
    // ATTENDANCE
    // ─────────────────────────────
    @GetMapping("/attendance")
    public String viewAttendance(@RequestParam(required = false) Integer classNumber,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 Model model) {

        Faculty faculty = facultyService.getFacultyByUser(userDetails.getUser());
        List<Course> myCourses = courseService.getCoursesByFaculty(faculty);

        List<Integer> classNumbers = myCourses.stream()
                .map(Course::getClassNumber)
                .distinct()
                .sorted()
                .toList();

        model.addAttribute("faculty", faculty);
        model.addAttribute("myCourses", myCourses);
        model.addAttribute("classNumbers", classNumbers);
        model.addAttribute("today", LocalDate.now());

        if (classNumber != null) {
            List<Student> students = studentService.getStudentsByClass(classNumber);

            Map<Long, Double> attendanceMap = students.stream()
                    .collect(Collectors.toMap(
                            Student::getId,
                            attendanceService::calculateAttendancePercentage
                    ));

            model.addAttribute("students", students);
            model.addAttribute("attendanceMap", attendanceMap);
            model.addAttribute("selectedClass", classNumber);
        }

        return "faculty/manage_attendance";
    }

    // ─────────────────────────────
    // SCORES
    // ─────────────────────────────
    @GetMapping("/scores")
    public String viewScores(@RequestParam(required = false) Long courseId,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {

        Faculty faculty = facultyService.getFacultyByUser(userDetails.getUser());
        List<Course> myCourses = courseService.getCoursesByFaculty(faculty);

        model.addAttribute("faculty", faculty);
        model.addAttribute("myCourses", myCourses);

        if (courseId != null) {
            Course selectedCourse = courseService.getCourseById(courseId);
            List<Student> students = studentService.getStudentsByClass(selectedCourse.getClassNumber());

            List<ExamScore> scores = examScoreService.getScoresByCourse(selectedCourse);

            Map<Long, ExamScore> scoreMap = new HashMap<>();
            for (ExamScore s : scores) {
                scoreMap.put(s.getStudent().getId(), s);
            }

            model.addAttribute("selectedCourse", selectedCourse);
            model.addAttribute("students", students);
            model.addAttribute("scoreMap", scoreMap);
        }

        return "faculty/manage_scores";
    }

    @PostMapping("/scores/save")
    public String saveScore(@RequestParam Long studentId,
                            @RequestParam Long courseId,
                            @RequestParam Double marks,
                            @RequestParam Double maxMarks,
                            RedirectAttributes redirectAttributes) {

        try {
            if (marks > maxMarks) {
                throw new RuntimeException("Marks cannot be greater than max marks");
            }

            Student student = studentService.getStudentById(studentId);
            Course course = courseService.getCourseById(courseId);

            examScoreService.saveScore(student, course, marks, maxMarks);

            redirectAttributes.addFlashAttribute("success", "Score saved!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/faculty/scores?courseId=" + courseId;
    }

    // ─────────────────────────────
    // FEES
    // ─────────────────────────────
    @GetMapping("/fees")
    public String viewFees(@RequestParam(required = false) Integer classNumber,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {

        Faculty faculty = facultyService.getFacultyByUser(userDetails.getUser());
        List<Course> myCourses = courseService.getCoursesByFaculty(faculty);

        List<Integer> classNumbers = myCourses.stream()
                .map(Course::getClassNumber)
                .distinct()
                .sorted()
                .toList();

        model.addAttribute("faculty", faculty);
        model.addAttribute("myCourses", myCourses);
        model.addAttribute("classNumbers", classNumbers);

        if (classNumber != null) {
            List<Student> students = studentService.getStudentsByClass(classNumber);

            Map<Long, Fee> feeMap = students.stream()
                    .collect(Collectors.toMap(
                            Student::getId,
                            feeService::getFeeByStudent
                    ));

            model.addAttribute("students", students);
            model.addAttribute("feeMap", feeMap);
            model.addAttribute("selectedClass", classNumber);
        }

        return "faculty/manage_fees";
    }
}