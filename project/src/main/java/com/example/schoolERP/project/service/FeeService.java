package com.example.schoolERP.project.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.schoolERP.project.model.Fee;
import com.example.schoolERP.project.model.Student;
import com.example.schoolERP.project.repository.FeeRepository;

@Service
public class FeeService {

    private final FeeRepository feeRepository;

    public FeeService(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    // ── Admin / Faculty: Create a fee record for a student ────────────────────
    // Called when a new student is added — creates their fee entry for the year.
    @Transactional
    public Fee createFeeRecord(Student student, Double totalAmount, String academicYear) {
        Fee fee = new Fee();
        fee.setStudent(student);
        fee.setTotalAmount(totalAmount);
        fee.setPaidAmount(0.0);
        fee.setCleared(false);
        fee.setAcademicYear(academicYear);
        return feeRepository.save(fee);
    }

    // ── Faculty / Admin: Record a payment for a student ───────────────────────
    // Adds the payment amount to what's already been paid,
    // then automatically marks as cleared if fully paid.
    @Transactional
    public Fee recordPayment(Long studentId, Double amountPaid) {
        Fee fee = feeRepository.findByStudentId(studentId);

        if (fee == null) {
            throw new RuntimeException("No fee record found for student id: " + studentId);
        }

        double newPaidAmount = fee.getPaidAmount() + amountPaid;
        fee.setPaidAmount(newPaidAmount);
        fee.setLastPaymentDate(LocalDate.now());

        // Auto-mark as cleared when fully paid
        if (newPaidAmount >= fee.getTotalAmount()) {
            fee.setCleared(true);
        }

        return feeRepository.save(fee);
    }

    // ── Admin: Manually set cleared status (for cash/offline payments) ────────
    @Transactional
    public Fee setFeeCleared(Long studentId, boolean cleared) {
        Fee fee = feeRepository.findByStudentId(studentId);

        if (fee == null) {
            throw new RuntimeException("No fee record found for student id: " + studentId);
        }

        fee.setCleared(cleared);
        if (cleared) {
            fee.setPaidAmount(fee.getTotalAmount());
            fee.setLastPaymentDate(LocalDate.now());
        }

        return feeRepository.save(fee);
    }

    // ── Student / Faculty: Get fee record for one student ────────────────────
    public Fee getFeeByStudent(Student student) {
        return feeRepository.findByStudent(student);
    }

    // ── Faculty / Admin: Get fee record by student ID directly ───────────────
    public Fee getFeeByStudentId(Long studentId) {
        return feeRepository.findByStudentId(studentId);
    }

    // ── Admin: Get all fee records (for overview) ─────────────────────────────
    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    // ── Calculate remaining balance for a student ─────────────────────────────
    public double getRemainingBalance(Student student) {
        Fee fee = feeRepository.findByStudent(student);
        if (fee == null) return 0.0;
        return fee.getTotalAmount() - fee.getPaidAmount();
    }
}