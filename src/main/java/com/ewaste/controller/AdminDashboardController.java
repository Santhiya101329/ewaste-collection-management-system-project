
package com.ewaste.controller;

import com.ewaste.model.User;
import com.ewaste.repository.CollectorRepository;
import com.ewaste.service.WasteRequestService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    @Autowired
    private WasteRequestService wasteRequestService;

    @Autowired
    private CollectorRepository collectorRepository;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(
            HttpSession session,
            Model model) {

        User user =
                (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        if (!"ADMIN".equals(user.getRole())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", user);

        model.addAttribute(
                "totalRequests",
                wasteRequestService.getAllRequests().size()
        );

        model.addAttribute(
                "pendingCount",
                wasteRequestService.countByStatus("PENDING")
        );

        model.addAttribute(
                "scheduledCount",
                wasteRequestService.countByStatus("SCHEDULED")
        );

        model.addAttribute(
                "verifiedCount",
                wasteRequestService.countByStatus("VERIFIED")
        );

        model.addAttribute(
                "completedCount",
                wasteRequestService.countByStatus("COMPLETED")
        );

        model.addAttribute(
                "closedCount",
                wasteRequestService.countByStatus("CLOSED")
        );

        model.addAttribute(
                "totalCollectors",
                collectorRepository.count()
        );

        model.addAttribute(
                "requests",
                wasteRequestService.getAllRequests()
        );

        return "admin-dashboard";
    }
}
