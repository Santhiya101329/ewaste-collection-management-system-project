package com.ewaste.controller;

import com.ewaste.model.User;
import com.ewaste.model.WasteRequest;
import com.ewaste.service.WasteRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WasteRequestController {

    @Autowired
    private WasteRequestService wasteRequestService;


    // ================= USER =================

    @GetMapping("/request/new")
    public String showRequestForm(
            HttpSession session,
            Model model) {

        User user =
                (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "wasteRequest",
                new WasteRequest()
        );

        return "request-form";
    }


    @PostMapping("/request/new")
    public String submitRequest(
            @ModelAttribute WasteRequest wasteRequest,
            HttpSession session,
            Model model) {

        User user =
                (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }


        // Check duplicate request
        if (wasteRequestService.isDuplicateRequest(
                user,
                wasteRequest.getCategory())) {

            model.addAttribute(
                    "wasteRequest",
                    wasteRequest
            );

            model.addAttribute(
                    "error",
                    "You already have a pending request for this category. Please wait until it is processed."
            );

            return "request-form";
        }


        // Connect request with logged-in user
        wasteRequest.setUser(user);


        // New request starts as PENDING
        wasteRequest.setStatus("PENDING");


        // Save request
        wasteRequestService.createRequest(
                wasteRequest
        );

        return "redirect:/request/my";
    }


    // ================= MY REQUESTS =================

    @GetMapping("/request/my")
    public String myRequests(
            HttpSession session,
            Model model) {

        User user =
                (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "requests",
                wasteRequestService
                        .getRequestsForUser(user)
        );

        return "my-requests";
    }


    // ================= ADMIN =================

    @GetMapping("/admin/requests")
    public String allRequests(
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

        model.addAttribute(
                "requests",
                wasteRequestService.getAllRequests()
        );

        return "admin-requests";
    }


    // ================= ADMIN STATUS UPDATE =================

    @PostMapping("/admin/requests/update-status")
    public String updateStatus(
            @RequestParam Long id,
            @RequestParam String status,
            HttpSession session,
            Model model) {

        User user =
                (User) session.getAttribute("loggedInUser");

        if (user == null ||
            !"ADMIN".equals(user.getRole())) {

            return "redirect:/login";
        }


        /*
         * Admin should NOT manually change a request
         * to VERIFIED.
         *
         * VERIFIED is automatically decided by the
         * collector GPS verification system.
         */
        if ("VERIFIED".equals(status)) {

            model.addAttribute(
                    "error",
                    "VERIFIED status is automatically set after collector GPS verification."
            );

            model.addAttribute(
                    "requests",
                    wasteRequestService.getAllRequests()
            );

            return "admin-requests";
        }


        /*
         * COMPLETED is allowed only when the request
         * has already been VERIFIED.
         */
        if ("COMPLETED".equals(status)) {

            WasteRequest request =
                    wasteRequestService
                            .getAllRequests()
                            .stream()
                            .filter(r -> r.getId().equals(id))
                            .findFirst()
                            .orElse(null);

            if (request == null) {

                model.addAttribute(
                        "error",
                        "Request not found."
                );

                model.addAttribute(
                        "requests",
                        wasteRequestService.getAllRequests()
                );

                return "admin-requests";
            }


            if (!"VERIFIED".equals(request.getStatus())) {

                model.addAttribute(
                        "error",
                        "Only VERIFIED requests can be marked as COMPLETED."
                );

                model.addAttribute(
                        "requests",
                        wasteRequestService.getAllRequests()
                );

                return "admin-requests";
            }
        }


        /*
         * CLOSED is allowed only after COMPLETED.
         */
        if ("CLOSED".equals(status)) {

            WasteRequest request =
                    wasteRequestService
                            .getAllRequests()
                            .stream()
                            .filter(r -> r.getId().equals(id))
                            .findFirst()
                            .orElse(null);

            if (request == null) {

                model.addAttribute(
                        "error",
                        "Request not found."
                );

                model.addAttribute(
                        "requests",
                        wasteRequestService.getAllRequests()
                );

                return "admin-requests";
            }


            if (!"COMPLETED".equals(request.getStatus())) {

                model.addAttribute(
                        "error",
                        "Only COMPLETED requests can be CLOSED."
                );

                model.addAttribute(
                        "requests",
                        wasteRequestService.getAllRequests()
                );

                return "admin-requests";
            }
        }


        /*
         * PENDING and SCHEDULED should not be changed
         * manually through this final-status button.
         *
         * Assignment already changes:
         * PENDING → SCHEDULED
         */
        if ("PENDING".equals(status) ||
            "SCHEDULED".equals(status)) {

            model.addAttribute(
                    "error",
                    "Request status is controlled by the pickup workflow."
            );

            model.addAttribute(
                    "requests",
                    wasteRequestService.getAllRequests()
            );

            return "admin-requests";
        }


        // Update allowed status
        wasteRequestService.updateStatus(
                id,
                status
        );

        return "redirect:/admin/requests";
    }


    // ================= ASSIGN COLLECTOR =================

    @PostMapping("/admin/requests/assign-collector")
    public String assignCollector(
            @RequestParam Long id,
            HttpSession session,
            Model model) {

        User user =
                (User) session.getAttribute("loggedInUser");

        if (user == null ||
            !"ADMIN".equals(user.getRole())) {

            return "redirect:/login";
        }


        boolean assigned =
                wasteRequestService.assignCollector(id);


        if (!assigned) {

            model.addAttribute(
                    "error",
                    "No available collector found for this request."
            );

            model.addAttribute(
                    "requests",
                    wasteRequestService.getAllRequests()
            );

            return "admin-requests";
        }


        return "redirect:/admin/requests";
    }
}