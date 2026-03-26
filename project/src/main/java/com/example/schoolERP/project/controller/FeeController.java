package com.example.schoolERP.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.schoolERP.project.model.Fee;
import com.example.schoolERP.project.service.FeeService;

@Controller
public class FeeController {

    @Autowired
    private FeeService feeService;

    @GetMapping("/fees")
    public String viewAllFees(Model model) {

        // Get all fees
        List<Fee> fees = feeService.getAllFees();

        // Count cleared fees
        long clearedFees = fees.stream()
                .filter(Fee::isCleared)
                .count();

        // Count pending fees
        long pendingFees = fees.size() - clearedFees;

        // Add to model
        model.addAttribute("fees", fees);
        model.addAttribute("clearedFees", clearedFees);
        model.addAttribute("pendingFees", pendingFees);

        return "admin/manage_fees"; // Thymeleaf template
    }
}