package com.Weather.Weather_Forecast_App.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.Weather.Weather_Forecast_App.dto.UserDto;
import com.Weather.Weather_Forecast_App.service.UserService;

@Controller
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserDto userDto, Model model) {
        try {
            userService.registerUser(userDto);
            model.addAttribute("success", "User registered successfully!");
            return "login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    // Removed /login mappings to avoid conflict with LoginController
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard"; // this should map to dashboard.html
    }

}
