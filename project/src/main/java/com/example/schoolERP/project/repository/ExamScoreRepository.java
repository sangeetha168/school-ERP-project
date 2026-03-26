package com.example.schoolERP.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.schoolERP.project.model.Course;
import com.example.schoolERP.project.model.ExamScore;
import com.example.schoolERP.project.model.Student;

public interface ExamScoreRepository extends JpaRepository<ExamScore, Long> {

	
    // Student dashboard: all scores for this student
    List<ExamScore> findByStudent(Student student);

    // Faculty: all scores for a specific course (to view/update class results)
    List<ExamScore> findByCourse(Course course);

    // Faculty: score for a specific student in a specific course
    List<ExamScore> findByStudentAndCourse(Student student, Course course);

    // Faculty: all scores for a student grouped by exam type
//    List<ExamScore> findByStudentAndExamType(Student student, String examType);
}
