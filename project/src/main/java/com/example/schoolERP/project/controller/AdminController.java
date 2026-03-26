package com.example.schoolERP.project.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.schoolERP.project.model.Fee;
import com.example.schoolERP.project.model.Student;
import com.example.schoolERP.project.service.CourseService;
import com.example.schoolERP.project.service.FacultyService;
import com.example.schoolERP.project.service.FeeService;
import com.example.schoolERP.project.service.StudentService;
import com.example.schoolERP.project.service.TimetableService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final CourseService courseService;
    private final FeeService feeService;
    private final TimetableService timetableService;

    public AdminController(StudentService studentService,
                           FacultyService facultyService,
                           CourseService courseService,
                           FeeService feeService,
                           TimetableService timetableService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.courseService = courseService;
        this.feeService = feeService;
        this.timetableService = timetableService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DASHBOARD
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("totalStudents", studentService.countStudents());
        model.addAttribute("totalFaculty", facultyService.countFaculty());
        model.addAttribute("totalCourses", courseService.countCourses());
        model.addAttribute("allStudents", studentService.getAllStudents());
        model.addAttribute("allFaculty", facultyService.getAllFaculty());
        return "admin/admin_dashboard";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STUDENT MANAGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/students")
    public String manageStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "admin/manage_students";
    }

    @PostMapping("/students/add")
    public String addStudent(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String phone,
                             @RequestParam Integer classNumber,
                             @RequestParam String section,
                             @RequestParam String rollNumber,
                             @RequestParam(defaultValue = "0") Double totalFees,
                             @RequestParam(defaultValue = "2024-25") String academicYear,
                             RedirectAttributes redirectAttributes) {
        try {
            Student student = studentService.addStudent(name, email, phone,
                                                        classNumber, section, rollNumber);
            // Automatically create a fee record for the new student
            feeService.createFeeRecord(student, totalFees, academicYear);
            redirectAttributes.addFlashAttribute("success", "Student added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add student: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    @GetMapping("/students/edit/{id}")
    public String showEditStudent(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "admin/edit_student";
    }

    @PostMapping("/students/update/{id}")
    public String updateStudent(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String phone,
                                @RequestParam Integer classNumber,
                                @RequestParam String section,
                                @RequestParam String rollNumber,
                                RedirectAttributes redirectAttributes) {
        try {
            studentService.updateStudent(id, name, phone, classNumber, section, rollNumber);
            redirectAttributes.addFlashAttribute("success", "Student updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update student: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("success", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete student: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FACULTY MANAGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/faculty")
    public String manageFaculty(Model model) {
        model.addAttribute("facultyList", facultyService.getAllFaculty());
        return "admin/manage_faculty";
    }

    @PostMapping("/faculty/add")
    public String addFaculty(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String phone,
                             @RequestParam String specialization,
                             RedirectAttributes redirectAttributes) {
        try {
            facultyService.addFaculty(name, email, phone, specialization);
            redirectAttributes.addFlashAttribute("success", "Faculty added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add faculty: " + e.getMessage());
        }
        return "redirect:/admin/faculty";
    }

    @GetMapping("/faculty/edit/{id}")
    public String showEditFaculty(@PathVariable Long id, Model model) {
        model.addAttribute("faculty", facultyService.getFacultyById(id));
        return "admin/edit_faculty";
    }

    @PostMapping("/faculty/update/{id}")
    public String updateFaculty(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String phone,
                                @RequestParam String specialization,
                                RedirectAttributes redirectAttributes) {
        try {
            facultyService.updateFaculty(id, name, phone, specialization);
            redirectAttributes.addFlashAttribute("success", "Faculty updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update faculty: " + e.getMessage());
        }
        return "redirect:/admin/faculty";
    }

    @GetMapping("/faculty/delete/{id}")
    public String deleteFaculty(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            facultyService.deleteFaculty(id);
            redirectAttributes.addFlashAttribute("success", "Faculty deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete faculty: " + e.getMessage());
        }
        return "redirect:/admin/faculty";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // COURSE MANAGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/courses")
    public String manageCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("facultyList", facultyService.getAllFaculty());
        return "admin/manage_courses";
    }

    @PostMapping("/courses/add")
    public String addCourse(@RequestParam String courseName,
                            @RequestParam Integer classNumber,
                            @RequestParam Long facultyId,
                            RedirectAttributes redirectAttributes) {
        try {
            courseService.addCourse(courseName, classNumber, facultyId);
            redirectAttributes.addFlashAttribute("success", "Course added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/courses/edit/{id}")
    public String showEditCourse(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        model.addAttribute("facultyList", facultyService.getAllFaculty());
        return "admin/edit_course";
    }

    @PostMapping("/courses/update/{id}")
    public String updateCourse(@PathVariable Long id,
                               @RequestParam String courseName,
                               @RequestParam Integer classNumber,
                               @RequestParam Long facultyId,
                               RedirectAttributes redirectAttributes) {
        try {
            courseService.updateCourse(id, courseName, classNumber, facultyId);
            redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("success", "Course deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TIMETABLE MANAGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/timetable")
    public String manageTimetable(Model model) {
        model.addAttribute("facultyList", facultyService.getAllFaculty());
        model.addAttribute("courses", courseService.getAllCourses());
        // Show full timetable for all classes 1-10
        model.addAttribute("timetableList", timetableService.getAllTimetableSlots());
        return "admin/manage_timetable";
    }

    @PostMapping("/timetable/add")
    public String addTimetableSlot(@RequestParam Long facultyId,
                                   @RequestParam Long courseId,
                                   @RequestParam Integer classNumber,
                                   @RequestParam String dayOfWeek,
                                   @RequestParam String timeSlot,
                                   @RequestParam Integer periodNumber,
                                   RedirectAttributes redirectAttributes) {
        try {
            timetableService.addTimetableSlot(facultyId, courseId, classNumber,
                                              dayOfWeek, timeSlot, periodNumber);
            redirectAttributes.addFlashAttribute("success", "Timetable slot added!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add slot: " + e.getMessage());
        }
        return "redirect:/admin/timetable";
    }

    @GetMapping("/timetable/delete/{id}")
    public String deleteTimetableSlot(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            timetableService.deleteTimetableSlot(id);
            redirectAttributes.addFlashAttribute("success", "Timetable slot deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete slot: " + e.getMessage());
        }
        return "redirect:/admin/timetable";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FEE OVERVIEW (Admin can see all students' fee status)
    // ─────────────────────────────────────────────────────────────────────────

 // ─────────────────────────────────────────────────────────────────────────
 // FEE OVERVIEW
 // ─────────────────────────────────────────────────────────────────────────

 @GetMapping("/fees")
 public String viewAllFees(Model model) {

     List<Fee> fees = feeService.getAllFees();

     long clearedFees = fees.stream()
             .filter(Fee::isCleared)
             .count();

     long pendingFees = fees.size() - clearedFees;

     model.addAttribute("fees", fees);
     model.addAttribute("clearedFees", clearedFees);
     model.addAttribute("pendingFees", pendingFees);

     return "admin/manage_fees";
 }

 @PostMapping("/fees/update/{studentId}")
 public String updateFeeStatus(@PathVariable Long studentId,
                               @RequestParam boolean cleared,
                               RedirectAttributes redirectAttributes) {
     try {
         feeService.setFeeCleared(studentId, cleared);
         redirectAttributes.addFlashAttribute("success", "Fee status updated!");
     } catch (Exception e) {
         redirectAttributes.addFlashAttribute("error", "Failed to update fee: " + e.getMessage());
     }
     return "redirect:/admin/fees";
 }
}