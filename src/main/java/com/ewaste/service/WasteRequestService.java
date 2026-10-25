package com.ewaste.service;

import com.ewaste.model.Collector;
import com.ewaste.model.User;
import com.ewaste.model.WasteRequest;
import com.ewaste.repository.CollectorRepository;
import com.ewaste.repository.WasteRequestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class WasteRequestService {

    @Autowired
    private WasteRequestRepository wasteRequestRepository;

    @Autowired
    private CollectorRepository collectorRepository;

    // Maximum distance for collector assignment
    private static final double MAX_DISTANCE_KM = 5.0;

    // ================= CREATE REQUEST =================

    public WasteRequest createRequest(WasteRequest request) {
        return wasteRequestRepository.save(request);
    }

    // ================= USER REQUESTS =================

    public List<WasteRequest> getRequestsForUser(User user) {
        return wasteRequestRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    // ================= ALL REQUESTS =================

    public List<WasteRequest> getAllRequests() {
        return wasteRequestRepository
                .findAllByOrderByCreatedAtDesc();
    }

    // ================= UPDATE STATUS =================

    public void updateStatus(Long id, String status) {
        WasteRequest request =
                wasteRequestRepository.findById(id).orElse(null);

        if (request != null) {
            request.setStatus(status);
            wasteRequestRepository.save(request);
        }
    }

    // ================= COUNT STATUS =================

    public long countByStatus(String status) {
        return wasteRequestRepository.countByStatus(status);
    }

    // ================= DUPLICATE REQUEST =================

    public boolean isDuplicateRequest(User user, String category) {
        return wasteRequestRepository
                .existsByUserAndCategoryAndStatus(
                        user, category, "PENDING");
    }

    // ================= FIND NEAREST ELIGIBLE COLLECTOR =================

    public Collector findNearestCollector(WasteRequest request) {

        // Customer location must be available
        if (request == null ||
                request.getLatitude() == null ||
                request.getLongitude() == null) {
            return null;
        }

        double customerLat = request.getLatitude();
        double customerLng = request.getLongitude();

        // Validate customer coordinates
        if (customerLat < -90 || customerLat > 90 ||
                customerLng < -180 || customerLng > 180) {
            return null;
        }

        List<Collector> availableCollectors =
                collectorRepository.findByAvailableTrue();

        if (availableCollectors.isEmpty()) {
            return null;
        }

        List<CollectorDistance> nearbyCollectors =
                new ArrayList<>();

        // Find collectors with saved locations within 5 km
        for (Collector collector : availableCollectors) {

            if (collector.getLatitude() == null ||
                    collector.getLongitude() == null) {
                continue;
            }

            double collectorLat = collector.getLatitude();
            double collectorLng = collector.getLongitude();

            if (collectorLat < -90 || collectorLat > 90 ||
                    collectorLng < -180 || collectorLng > 180) {
                continue;
            }

            double distance = calculateDistance(
                    customerLat,
                    customerLng,
                    collectorLat,
                    collectorLng
            );

            if (distance <= MAX_DISTANCE_KM) {
                nearbyCollectors.add(
                        new CollectorDistance(collector, distance)
                );
            }
        }

        // Never randomly assign outside the radius
        if (nearbyCollectors.isEmpty()) {
            return null;
        }

        // Count active assignments for each nearby collector
        List<WasteRequest> allRequests =
                wasteRequestRepository.findAll();

        for (CollectorDistance candidate : nearbyCollectors) {

            long activePickups = allRequests.stream()
                    .filter(r -> r.getCollector() != null)
                    .filter(r -> r.getCollector().getId()
                            .equals(candidate.collector.getId()))
                    .filter(r ->
                            "SCHEDULED".equals(r.getStatus()) ||
                            "IN_PROGRESS".equals(r.getStatus()))
                    .count();

            candidate.activePickups = activePickups;
        }

        // First: fewest active pickups among nearby collectors.
        // Tie-breaker: shortest distance.
        nearbyCollectors.sort(
                Comparator
                        .comparingLong(
                                (CollectorDistance c) -> c.activePickups)
                        .thenComparingDouble(c -> c.distanceKm)
        );

        return nearbyCollectors.get(0).collector;
    }

    // ================= ASSIGN COLLECTOR =================

    public boolean assignCollector(Long requestId) {

        WasteRequest request =
                wasteRequestRepository.findById(requestId).orElse(null);

        if (request == null) {
            return false;
        }

        // Do not reassign an already assigned request
        if (request.getCollector() != null) {
            return false;
        }

        // Only assign requests that are still pending
        if (!"PENDING".equals(request.getStatus())) {
            return false;
        }

        Collector collector = findNearestCollector(request);

        // No eligible collector: keep request unassigned
        if (collector == null) {
            return false;
        }

        request.setCollector(collector);
        request.setStatus("SCHEDULED");

        wasteRequestRepository.save(request);

        /*
         * Do not set collector.available=false here.
         * Otherwise that collector cannot be considered for
         * other nearby requests, making workload balancing ineffective.
         *
         * Active workload is calculated from SCHEDULED and IN_PROGRESS
         * requests. The collector's available flag remains true until
         * your application explicitly marks the collector unavailable.
         */

        return true;
    }

    // ================= DISTANCE CALCULATION =================

    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final double EARTH_RADIUS_KM = 6371.0;

        double latDifference = Math.toRadians(lat2 - lat1);
        double lonDifference = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(latDifference / 2) *
                Math.sin(latDifference / 2) +
                Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2)) *
                Math.sin(lonDifference / 2) *
                Math.sin(lonDifference / 2);

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        return EARTH_RADIUS_KM * c;
    }

    // ================= COLLECTOR DISTANCE HELPER =================

    private static class CollectorDistance {

        private final Collector collector;
        private final double distanceKm;
        private long activePickups;

        private CollectorDistance(
                Collector collector,
                double distanceKm) {

            this.collector = collector;
            this.distanceKm = distanceKm;
            this.activePickups = 0;
        }
    }
}