package com.ewaste.service;

import com.ewaste.model.Collector;
import com.ewaste.model.User;
import com.ewaste.model.WasteRequest;
import com.ewaste.repository.CollectorRepository;
import com.ewaste.repository.WasteRequestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class WasteRequestService {

    @Autowired
    private WasteRequestRepository wasteRequestRepository;

    @Autowired
    private CollectorRepository collectorRepository;

    // CREATE REQUEST
    public WasteRequest createRequest(WasteRequest request) {
        return wasteRequestRepository.save(request);
    }

    // USER REQUESTS
    public List<WasteRequest> getRequestsForUser(User user) {
        return wasteRequestRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    // ALL REQUESTS
    public List<WasteRequest> getAllRequests() {
        return wasteRequestRepository
                .findAllByOrderByCreatedAtDesc();
    }

    // UPDATE STATUS
    public void updateStatus(Long id, String status) {
        WasteRequest request =
                wasteRequestRepository.findById(id).orElse(null);

        if (request != null) {
            request.setStatus(status);
            wasteRequestRepository.save(request);
        }
    }

    // COUNT STATUS
    public long countByStatus(String status) {
        return wasteRequestRepository.countByStatus(status);
    }

    // DUPLICATE REQUEST
    public boolean isDuplicateRequest(User user, String category) {
        return wasteRequestRepository
                .existsByUserAndCategoryAndStatus(
                        user, category, "PENDING");
    }

    // FIND NEAREST AVAILABLE COLLECTOR
    public Collector findNearestCollector(WasteRequest request) {

        List<Collector> availableCollectors =
                collectorRepository.findByAvailableTrue();

        System.out.println(
                "Available collectors found: "
                        + availableCollectors.size());

        if (availableCollectors.isEmpty()) {
            System.out.println(
                    "Assignment failed: no available collectors.");
            return null;
        }

        List<WasteRequest> allRequests =
                wasteRequestRepository.findAll();

        // If pickup coordinates are valid, calculate distances.
        boolean validPickupLocation = request != null
                && request.getLatitude() != null
                && request.getLongitude() != null
                && request.getLatitude() >= -90
                && request.getLatitude() <= 90
                && request.getLongitude() >= -180
                && request.getLongitude() <= 180;

        List<CollectorDistance> candidates = new ArrayList<>();

        for (Collector collector : availableCollectors) {

            long activePickups = allRequests.stream()
                    .filter(r -> r.getCollector() != null)
                    .filter(r -> r.getCollector().getId()
                            .equals(collector.getId()))
                    .filter(r ->
                            "SCHEDULED".equals(r.getStatus())
                            || "IN_PROGRESS".equals(r.getStatus()))
                    .count();

            boolean validCollectorLocation =
                    collector.getLatitude() != null
                    && collector.getLongitude() != null
                    && collector.getLatitude() >= -90
                    && collector.getLatitude() <= 90
                    && collector.getLongitude() >= -180
                    && collector.getLongitude() <= 180;

            // Collector without GPS is kept as a fallback candidate.
            if (!validPickupLocation || !validCollectorLocation) {

                System.out.println(
                        "Collector " + collector.getId()
                        + ": GPS unavailable or pickup location invalid.");

                candidates.add(new CollectorDistance(
                        collector, Double.MAX_VALUE, activePickups));

                continue;
            }

            double distance = calculateDistance(
                    request.getLatitude(),
                    request.getLongitude(),
                    collector.getLatitude(),
                    collector.getLongitude());

            System.out.println(
                    "Collector " + collector.getId()
                    + " distance: "
                    + String.format("%.2f", distance) + " km");

            // No 5 km restriction.
            candidates.add(new CollectorDistance(
                    collector, distance, activePickups));
        }

        if (candidates.isEmpty()) {
            System.out.println("No collector candidates found.");
            return null;
        }

        // Prefer collectors with valid distances.
        // Among them, prefer the nearest collector.
        // If distance is unavailable, prefer lower workload.
        candidates.sort(
                Comparator
                        .comparingDouble(
                                (CollectorDistance c) -> c.distanceKm)
                        .thenComparingLong(c -> c.activePickups));

        Collector selected = candidates.get(0).collector;

        System.out.println(
                "Selected collector ID: " + selected.getId());

        return selected;
    }

    // ASSIGN COLLECTOR
    @Transactional
    public boolean assignCollector(Long requestId) {

        WasteRequest request =
                wasteRequestRepository.findById(requestId).orElse(null);

        if (request == null) {
            System.out.println("Assignment failed: request not found.");
            return false;
        }

        if (request.getCollector() != null) {
            System.out.println(
                    "Assignment failed: request already assigned.");
            return false;
        }

        if (!"PENDING".equals(request.getStatus())) {
            System.out.println(
                    "Assignment failed: request is not PENDING.");
            return false;
        }

        Collector collector = findNearestCollector(request);

        if (collector == null) {
            return false;
        }

        request.setCollector(collector);
        request.setStatus("SCHEDULED");

        wasteRequestRepository.save(request);

        System.out.println(
                "Request " + requestId
                + " assigned to collector "
                + collector.getId());

        return true;
    }

    // DISTANCE CALCULATION
    private double calculateDistance(
            double lat1, double lon1,
            double lat2, double lon2) {

        final double EARTH_RADIUS_KM = 6371.0;

        double latDifference = Math.toRadians(lat2 - lat1);
        double lonDifference = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(latDifference / 2)
                * Math.sin(latDifference / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDifference / 2)
                * Math.sin(lonDifference / 2);

        a = Math.max(0.0, Math.min(1.0, a));

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1.0 - a));

        return EARTH_RADIUS_KM * c;
    }

    // COLLECTOR DISTANCE HELPER
    private static class CollectorDistance {

        private final Collector collector;
        private final double distanceKm;
        private final long activePickups;

        private CollectorDistance(
                Collector collector,
                double distanceKm,
                long activePickups) {

            this.collector = collector;
            this.distanceKm = distanceKm;
            this.activePickups = activePickups;
        }
    }
}