package com.example.schoolERP.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.schoolERP.project.dto.UserDTO;
import com.example.schoolERP.project.service.UserService;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    // ── Public landing page ─────────────────────────────────────
    @GetMapping({"", "/"})
    public String home() {
        return "home";
    }

    // ── Login page ──────────────────────────────────────────────
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ── Registration page ───────────────────────────────────────
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    // ── Handle Registration ─────────────────────────────────────
    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("userDTO") UserDTO userDTO,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {

        try {

            // Save user (and student profile if role = STUDENT)
            userService.StoreRegisteredUser(userDTO);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Registration successful! Please login."
            );

            return "redirect:/login";

        } catch (Exception e) {

            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("userDTO", userDTO);

            return "register";
        }
    }

}