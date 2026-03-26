package com.example.schoolERP.project.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.schoolERP.project.model.Faculty;
import com.example.schoolERP.project.model.User;
import com.example.schoolERP.project.repository.FacultyRepository;
import com.example.schoolERP.project.repository.UserRepository;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public FacultyService(FacultyRepository facultyRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.facultyRepository = facultyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Admin: Add a new faculty member ──────────────────────────────────────
    // Creates the User account first, then the Faculty profile linked to it.
    @Transactional
    public Faculty addFaculty(String name, String email, String phone, String specialization) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        // Default password is their email — they should change it on first login
        user.setPassword(passwordEncoder.encode(email));
        user.setRole("ROLE_FACULTY");
        userRepository.save(user);

        Faculty faculty = new Faculty();
        faculty.setUser(user);
        faculty.setPhone(phone);
        faculty.setSpecialization(specialization);
        return facultyRepository.save(faculty);
    }

    // ── Admin: Update faculty details ─────────────────────────────────────────
    @Transactional
    public Faculty updateFaculty(Long facultyId, String name, String phone, String specialization) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + facultyId));

        // Name lives on the linked User
        faculty.getUser().setName(name);
        userRepository.save(faculty.getUser());

        faculty.setPhone(phone);
        faculty.setSpecialization(specialization);
        return facultyRepository.save(faculty);
    }

    // ── Admin: Delete faculty and their User account ──────────────────────────
    @Transactional
    public void deleteFaculty(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + facultyId));
        Long userId = faculty.getUser().getId();
        facultyRepository.delete(faculty);
        userRepository.deleteById(userId);
    }

    // ── Get all faculty (Admin dashboard list) ────────────────────────────────
    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }

    // ── Get one faculty by their Faculty table ID ─────────────────────────────
    public Faculty getFacultyById(Long facultyId) {
        return facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + facultyId));
    }

    // ── Get faculty profile from logged-in User (used after login) ────────────
    public Faculty getFacultyByUser(User user) {
        return facultyRepository.findByUser(user);
    }

    // ── Count total faculty (Admin dashboard overview card) ───────────────────
    public long countFaculty() {
        return facultyRepository.count();
    }
}