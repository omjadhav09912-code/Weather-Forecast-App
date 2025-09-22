package com.Weather.Weather_Forecast_App.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Weather.Weather_Forecast_App.entity.User;
import com.Weather.Weather_Forecast_App.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/weather/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                model.addAttribute("error", "Username and password are required.");
                return "login";
            }
            User user =userService.findByUsernameOrEmail(username);
            if (user == null) {
                model.addAttribute("error", "No user found with that username or email.");
                return "login";
            }
            if (!user.getPassword().equals(password)) {
                model.addAttribute("error", "Invalid password.");
                return "login";
            }
            session.setAttribute("user", user.getUsername());
            return "redirect:/weather/dashboard";
        } catch (Exception e) {
            // Optionally log the error here
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
