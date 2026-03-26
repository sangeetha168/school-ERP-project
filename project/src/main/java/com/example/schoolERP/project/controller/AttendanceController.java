package com.example.schoolERP.project.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.schoolERP.project.model.*;
import com.example.schoolERP.project.service.*;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final FacultyService facultyService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final AttendanceService attendanceService;

    public AttendanceController(FacultyService facultyService,
                                StudentService studentService,
                                CourseService courseService,
                                AttendanceService attendanceService) {
        this.facultyService = facultyService;
        this.studentService = studentService;
        this.courseService = courseService;
        this.attendanceService = attendanceService;
    }

    // ✅ VIEW ATTENDANCE PAGE
    @GetMapping
    public String viewAttendance(@RequestParam(required = false) Integer classNumber,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model) {

        if (userDetails == null) return "redirect:/login";

        Faculty faculty = facultyService.getFacultyByUser(userDetails.getUser());
        if (faculty == null) return "redirect:/login";

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

        // ✅ If class selected → load students
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

    // ✅ MARK ATTENDANCE
    @PostMapping("/mark")
    public String markAttendance(@RequestParam Long studentId,
                                @RequestParam boolean present,
                                @RequestParam Integer classNumber,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {

        Faculty faculty = facultyService.getFacultyByUser(userDetails.getUser());
        Student student = studentService.getStudentById(studentId);

        attendanceService.markAttendance(student, LocalDate.now(), present);
        return "redirect:/attendance?classNumber=" + classNumber;
    }
}