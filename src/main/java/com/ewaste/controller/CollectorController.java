package com.ewaste.controller;

import com.ewaste.model.Collector;
import com.ewaste.model.User;
import com.ewaste.model.WasteRequest;
import com.ewaste.repository.CollectorRepository;
import com.ewaste.repository.WasteRequestRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class CollectorController {

    @Autowired
    private CollectorRepository collectorRepository;

    @Autowired
    private WasteRequestRepository wasteRequestRepository;


    // ================= COLLECTOR DASHBOARD =================

    @GetMapping("/collector/dashboard")
    public String collectorDashboard(
            HttpSession session,
            Model model) {

        User user =
                (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        if (!"COLLECTOR".equals(user.getRole())) {
            return "redirect:/dashboard";
        }

        Collector collector =
                collectorRepository.findAll()
                        .stream()
                        .filter(c ->
                                c.getName() != null &&
                                c.getName().equals(user.getName()))
                        .findFirst()
                        .orElse(null);

        if (collector == null) {
            model.addAttribute(
                    "error",
                    "Collector profile not found."
            );

            return "collector-dashboard";
        }

        List<WasteRequest> requests =
                wasteRequestRepository
                        .findByCollectorOrderByCreatedAtDesc(
                                collector
                        );

        model.addAttribute("collector", collector);
        model.addAttribute("requests", requests);

        return "collector-dashboard";
    }


    // ================= UPDATE COLLECTOR LOCATION =================

    @PostMapping("/collector/update-location")
    public String updateLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            HttpSession session) {

        User user =
                (User) session.getAttribute("loggedInUser");

        if (user == null ||
                !"COLLECTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Collector collector =
                collectorRepository.findAll()
                        .stream()
                        .filter(c ->
                                c.getName() != null &&
                                c.getName().equals(user.getName()))
                        .findFirst()
                        .orElse(null);

        if (collector == null) {
            return "redirect:/collector/dashboard";
        }

        // Validate manually selected map coordinates
        if (latitude == null || longitude == null ||
                latitude < -90 || latitude > 90 ||
                longitude < -180 || longitude > 180) {
            return "redirect:/collector/dashboard?locationError";
        }

        // Save selected location to database
        collector.setLatitude(latitude);
        collector.setLongitude(longitude);
        collector.setLocationUpdatedAt(LocalDateTime.now());

        collectorRepository.save(collector);

        return "redirect:/collector/dashboard?locationUpdated";
    }


    // ================= COLLECT PICKUP =================

    @PostMapping("/collector/complete-pickup")
    public String completePickup(
            @RequestParam Long requestId,
            @RequestParam("pickupPhoto") MultipartFile pickupPhoto,
            HttpSession session) {

        User user =
                (User) session.getAttribute("loggedInUser");

        if (user == null ||
                !"COLLECTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Collector collector =
                collectorRepository.findAll()
                        .stream()
                        .filter(c ->
                                c.getName() != null &&
                                c.getName().equals(user.getName()))
                        .findFirst()
                        .orElse(null);

        if (collector == null) {
            return "redirect:/collector/dashboard";
        }

        WasteRequest request =
                wasteRequestRepository
                        .findById(requestId)
                        .orElse(null);

        if (request == null) {
            return "redirect:/collector/dashboard";
        }

        // Check that this request belongs to this collector
        if (request.getCollector() == null ||
                !request.getCollector().getId()
                        .equals(collector.getId())) {
            return "redirect:/collector/dashboard";
        }

        if (pickupPhoto == null || pickupPhoto.isEmpty()) {
            return "redirect:/collector/dashboard";
        }

        try {
            String uploadDirectory = "uploads/pickups/";

            Path directory = Paths.get(uploadDirectory);
            Files.createDirectories(directory);

            String originalFileName =
                    Paths.get(pickupPhoto.getOriginalFilename())
                            .getFileName()
                            .toString();

            String fileName =
                    request.getId()
                    + "_"
                    + System.currentTimeMillis()
                    + "_"
                    + originalFileName;

            Path filePath = directory.resolve(fileName);

            Files.write(filePath, pickupPhoto.getBytes());

            request.setPickupPhotoPath(filePath.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/collector/dashboard";
        }

        // Submit pickup for admin review
        request.setStatus("VERIFIED");

        wasteRequestRepository.save(request);

        // Make collector available again
        collector.setAvailable(true);
        collectorRepository.save(collector);

        return "redirect:/collector/dashboard";
    }
}