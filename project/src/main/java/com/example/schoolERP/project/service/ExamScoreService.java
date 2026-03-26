package com.example.schoolERP.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.schoolERP.project.model.Course;
import com.example.schoolERP.project.model.ExamScore;
import com.example.schoolERP.project.model.Student;
import com.example.schoolERP.project.repository.ExamScoreRepository;

@Service
public class ExamScoreService {

    private final ExamScoreRepository examScoreRepository;

    public ExamScoreService(ExamScoreRepository examScoreRepository) {
        this.examScoreRepository = examScoreRepository;
    }

    // ── Faculty: Add or update a student's score for a course ─────────────────
    // If a score already exists for this student+course, it updates it.
    @Transactional
    public ExamScore saveScore(Student student, Course course, Double marks, Double maxMarks) {
        List<ExamScore> existing = examScoreRepository.findByStudentAndCourse(student, course);

        ExamScore score;
        if (!existing.isEmpty()) {
            // Update the first (and should be only) existing record
            score = existing.get(0);
        } else {
            score = new ExamScore();
            score.setStudent(student);
            score.setCourse(course);
        }

        score.setMarks(marks);
        score.setMaxMarks(maxMarks);
        score.setGrade(calculateGrade(marks, maxMarks));
        return examScoreRepository.save(score);
    }

    // ── Student dashboard: Get all scores for a student ───────────────────────
    public List<ExamScore> getScoresByStudent(Student student) {
        return examScoreRepository.findByStudent(student);
    }

    // ── Faculty: Get all scores for a course (to view whole class results) ─────
    public List<ExamScore> getScoresByCourse(Course course) {
        return examScoreRepository.findByCourse(course);
    }

    // ── Faculty: Get a student's score in a specific course ───────────────────
    public List<ExamScore> getScoreByStudentAndCourse(Student student, Course course) {
        return examScoreRepository.findByStudentAndCourse(student, course);
    }

    // ── Delete a score record ─────────────────────────────────────────────────
    public void deleteScore(Long scoreId) {
        examScoreRepository.deleteById(scoreId);
    }

    // ── Private helper: Auto-calculate grade from marks and maxMarks ──────────
    // Standard grading scale — adjust ranges to match your school's policy
    private String calculateGrade(Double marks, Double maxMarks) {
        if (marks == null || maxMarks == null || maxMarks == 0) {
            return "N/A";
        }

        double percentage = (marks / maxMarks) * 100;

        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B+";
        else if (percentage >= 60) return "B";
        else if (percentage >= 50) return "C";
        else if (percentage >= 40) return "D";
        else return "F";
    }
}