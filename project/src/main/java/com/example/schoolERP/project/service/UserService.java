package com.example.schoolERP.project.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.schoolERP.project.dto.UserDTO;
import com.example.schoolERP.project.model.Student;
import com.example.schoolERP.project.model.Faculty;
import com.example.schoolERP.project.model.User;
import com.example.schoolERP.project.repository.StudentRepository;
import com.example.schoolERP.project.repository.FacultyRepository;
import com.example.schoolERP.project.repository.UserRepository;

@Service
public class UserService {

    private UserRepository userRepository;
    private StudentRepository studentRepository;
    private FacultyRepository facultyRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       StudentRepository studentRepository,
                       FacultyRepository facultyRepository,
                       PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void StoreRegisteredUser(UserDTO userDTO) {

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Assign role safely
        if (userDTO.getRole() != null &&
                (userDTO.getRole().equals("ROLE_ADMIN")
                || userDTO.getRole().equals("ROLE_FACULTY")
                || userDTO.getRole().equals("ROLE_STUDENT"))) {

            user.setRole(userDTO.getRole());

        } else {
            user.setRole("ROLE_STUDENT");
        }

        userRepository.save(user);

        // ── Create Student Profile ─────────────────
        if (user.getRole().equals("ROLE_STUDENT")) {

            Student student = new Student();
            student.setUser(user);

            student.setPhone("");
            student.setClassNumber(null);
            student.setSection("");
            student.setRollNumber("");

            studentRepository.save(student);
        }

        // ── Create Faculty Profile ─────────────────
        if (user.getRole().equals("ROLE_FACULTY")) {

            Faculty faculty = new Faculty();
            faculty.setUser(user);

            faculty.setPhone("");
            faculty.setSpecialization(null);

            facultyRepository.save(faculty);
        }
    }
}