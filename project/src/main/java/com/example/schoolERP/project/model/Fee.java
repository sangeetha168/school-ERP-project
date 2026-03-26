package com.example.schoolERP.project.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Fees")
@Data
@NoArgsConstructor
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double totalAmount;

    private Double paidAmount;

    // True when paidAmount >= totalAmount
    private boolean cleared;

    // Date of last payment update — useful for student dashboard display
    private LocalDate lastPaymentDate;

    // Academic year, e.g. "2024-25" — so fees can be tracked per year	
    private String academicYear;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    

}