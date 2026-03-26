package com.example.schoolERP.project.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.schoolERP.project.model.Student;
import com.example.schoolERP.project.model.User;
import com.example.schoolERP.project.repository.StudentRepository;
import com.example.schoolERP.project.repository.UserRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ───────────────── STUDENT SELF REGISTRATION ─────────────────
    // Creates both User and Student when a student registers
    @Transactional
    public Student registerStudent(String name, String email, String password,
                                   String phone, Integer classNumber,
                                   String section, String rollNumber) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_STUDENT");

        userRepository.save(user);

        Student student = new Student();
        student.setUser(user);
        student.setPhone(phone);
        student.setClassNumber(classNumber);
        student.setSection(section);
        student.setRollNumber(rollNumber);

        return studentRepository.save(student);
    }

    // ── Admin / Faculty: Add a new student ────────────────────────────────────
    @Transactional
    public Student addStudent(String name, String email, String phone,
                              Integer classNumber, String section, String rollNumber) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(email)); // default password
        user.setRole("ROLE_STUDENT");

        userRepository.save(user);

        Student student = new Student();
        student.setUser(user);
        student.setPhone(phone);
        student.setClassNumber(classNumber);
        student.setSection(section);
        student.setRollNumber(rollNumber);

        return studentRepository.save(student);
    }

    // ── Update student ───────────────────────────────────────────
    @Transactional
    public Student updateStudent(Long studentId, String name, String phone,
                                 Integer classNumber, String section, String rollNumber) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        student.getUser().setName(name);
        userRepository.save(student.getUser());

        student.setPhone(phone);
        student.setClassNumber(classNumber);
        student.setSection(section);
        student.setRollNumber(rollNumber);

        return studentRepository.save(student);
    }

    // ── Delete student ───────────────────────────────────────────
    @Transactional
    public void deleteStudent(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        Long userId = student.getUser().getId();

        studentRepository.delete(student);
        userRepository.deleteById(userId);
    }

    // ── Get all students ─────────────────────────────────────────
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // ── Get students by class ────────────────────────────────────
    public List<Student> getStudentsByClass(Integer classNumber) {
        return studentRepository.findByClassNumber(classNumber);
    }

    // ── Get students by class + section ──────────────────────────
    public List<Student> getStudentsByClassAndSection(Integer classNumber, String section) {
        return studentRepository.findByClassNumberAndSection(classNumber, section);
    }

    // ── Get student by ID ────────────────────────────────────────
    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
    }

    // ── Get student using logged-in user ─────────────────────────
    public Student getStudentByUser(User user) {
        return studentRepository.findByUser(user);
    }

    // ── Count students ───────────────────────────────────────────
    public long countStudents() {
        return studentRepository.count();
    }
}