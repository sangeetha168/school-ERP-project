package com.example.schoolERP.project.model;

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
@Table(name = "Students")
@Data
@NoArgsConstructor
public class Student {
	
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	private Long id;
	
	private String name;

	private String rollNumber;
	
	private Integer classNumber;
	
	private String phone;
	
    // Section is always "A" per your spec (1 section per class),
    // but stored here so it can be displayed and extended later
    private String section;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
}
