package com.ewaste.controller;

import com.ewaste.model.User;
import com.ewaste.service.UserService;
import com.ewaste.service.WasteRequestService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private WasteRequestService wasteRequestService;

    // Home page
    @GetMapping("/")
    public String home() {
        return "home";
    }

    // Register page
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Register user
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute User user,
            Model model) {

        try {
            user.setRole("USER");
            userService.register(user);
            return "redirect:/login";

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(
                    "error",
                    "This email is already registered. Please login instead."
            );
            model.addAttribute("user", user);
            return "register";
        }
    }

    // Login page
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // Process login
    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String loginAs,
            HttpSession session,
            Model model) {

        User user = userService.login(email, password);

        // Invalid email or password
        if (user == null) {
            model.addAttribute(
                    "error",
                    "Invalid email or password"
            );
            return "login";
        }

        // Verify selected role
        if (user.getRole() == null
                || !user.getRole().equals(loginAs)) {

            if ("ADMIN".equals(loginAs)) {
                model.addAttribute(
                        "error",
                        "This account does not have admin access."
                );

            } else if ("COLLECTOR".equals(loginAs)) {
                model.addAttribute(
                        "error",
                        "This account does not have collector access."
                );

            } else {
                model.addAttribute(
                        "error",
                        "This account does not have user access."
                );
            }

            return "login";
        }

        // Store logged-in user
        session.setAttribute("loggedInUser", user);

        // ADMIN → Admin dashboard
        if ("ADMIN".equals(user.getRole())) {
            return "redirect:/admin/dashboard";
        }

        // COLLECTOR → Collector dashboard
        if ("COLLECTOR".equals(user.getRole())) {
            return "redirect:/collector/dashboard";
        }

        // USER → Normal dashboard
        return "redirect:/dashboard";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // Normal user dashboard
    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model) {

        User user =
                (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        // Prevent ADMIN and COLLECTOR from opening user dashboard
        if ("ADMIN".equals(user.getRole())) {
            return "redirect:/admin/dashboard";
        }

        if ("COLLECTOR".equals(user.getRole())) {
            return "redirect:/collector/dashboard";
        }

        model.addAttribute("user", user);

        return "dashboard";
    }
}
