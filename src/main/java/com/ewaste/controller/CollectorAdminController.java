package com.ewaste.controller;

import com.ewaste.model.Collector;
import com.ewaste.model.User;
import com.ewaste.repository.CollectorRepository;
import com.ewaste.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CollectorAdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CollectorRepository collectorRepository;

    // Show collector management page
    @GetMapping("/admin/collectors")
    public String showCollectors(
            HttpSession session,
            Model model) {

        User admin =
                (User) session.getAttribute("loggedInUser");

        // Check login
        if (admin == null) {
            return "redirect:/login";
        }

        // Only ADMIN can access
        if (!"ADMIN".equals(admin.getRole())) {
            return "redirect:/dashboard";
        }

        model.addAttribute(
                "collectors",
                collectorRepository.findAll()
        );

        model.addAttribute(
                "collector",
                new Collector()
        );

        return "admin-collectors";
    }

    // Create collector
    @PostMapping("/admin/collectors/add")
    public String addCollector(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User admin =
                (User) session.getAttribute("loggedInUser");

        // Check admin
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/login";
        }

        try {

            // Create login account
            User user = new User();

            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(password);

            // Important: Collector role
            user.setRole("COLLECTOR");

            userService.register(user);

            // Create collector profile
            Collector collector = new Collector();

            collector.setName(name);
            collector.setPhone(phone);
            collector.setAvailable(true);

            collectorRepository.save(collector);

            return "redirect:/admin/collectors";

        } catch (DataIntegrityViolationException e) {

            model.addAttribute(
                    "error",
                    "This email is already registered."
            );

            model.addAttribute(
                    "collectors",
                    collectorRepository.findAll()
            );

            model.addAttribute(
                    "collector",
                    new Collector()
            );

            return "admin-collectors";
        }
    }
}