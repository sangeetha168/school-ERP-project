package com.example.schoolERP.project.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.schoolERP.project.model.Attendance;
import com.example.schoolERP.project.model.Student;
import com.example.schoolERP.project.repository.AttendanceRepository;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    // ─────────────────────────────────────────────
    // ✅ MARK OR UPDATE ATTENDANCE
    // ─────────────────────────────────────────────
    @Transactional
    public Attendance markAttendance(Student student, LocalDate date, boolean present) {

    	Optional<Attendance> existing = attendanceRepository
    	        .findByStudentAndDate(student, date);

        Attendance attendance;

        if (existing.isPresent()) {
            // ✅ Update existing record
            attendance = existing.get();
            attendance.setPresent(present);
        } else {
            // ✅ Create new record
            attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setDate(date);
            attendance.setPresent(present);
        }

        return attendanceRepository.save(attendance);
    }

    // ─────────────────────────────────────────────
    // ✅ GET ALL ATTENDANCE FOR STUDENT
    // ─────────────────────────────────────────────
    public List<Attendance> getAttendanceByStudent(Student student) {
        return attendanceRepository.findByStudent(student);
    }

    // ─────────────────────────────────────────────
    // ✅ CALCULATE ATTENDANCE %
    // ─────────────────────────────────────────────
    public double calculateAttendancePercentage(Student student) {

        List<Attendance> records = attendanceRepository.findByStudent(student);

        if (records.isEmpty()) {
            return 0.0;
        }

        long presentCount = records.stream()
                .filter(Attendance::isPresent)
                .count();

        double percentage = ((double) presentCount / records.size()) * 100;

        // ✅ Round to 2 decimal places
        return Math.round(percentage * 100.0) / 100.0;
    }

    // ─────────────────────────────────────────────
    // ✅ GET ATTENDANCE BY DATE
    // ─────────────────────────────────────────────
    public Optional<Attendance> getAttendanceByStudentAndDate(Student student, LocalDate date) {
        return attendanceRepository.findByStudentAndDate(student, date);
    }
    // ─────────────────────────────────────────────
    // ✅ COUNT PRESENT DAYS
    // ─────────────────────────────────────────────
    public long countPresentDays(Student student) {
        return attendanceRepository.findByStudent(student).stream()
                .filter(Attendance::isPresent)
                .count();
    }

    // ─────────────────────────────────────────────
    // ✅ COUNT TOTAL DAYS
    // ─────────────────────────────────────────────
    public long countTotalDays(Student student) {
        return attendanceRepository.findByStudent(student).size();
    }
}