package com.ewaste.service;

import com.ewaste.model.Collector;
import com.ewaste.model.WasteRequest;
import com.ewaste.repository.CollectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectorService {

    @Autowired
    private CollectorRepository collectorRepository;


    /*
     * Find the nearest available collector
     * for a pickup request.
     */
    public Collector findNearestCollector(WasteRequest request) {

        // Get all available collectors
        List<Collector> collectors =
                collectorRepository.findByAvailableTrue();

        // If no collectors are available
        if (collectors.isEmpty()) {
            return null;
        }

        // User pickup location
        Double userLat = request.getLatitude();
        Double userLng = request.getLongitude();

        // If request has no location
        if (userLat == null || userLng == null) {
            return null;
        }

        Collector nearestCollector = null;
        double shortestDistance = Double.MAX_VALUE;


        // Check every available collector
        for (Collector collector : collectors) {

            // Skip collector if GPS location is missing
            if (collector.getLatitude() == null ||
                collector.getLongitude() == null) {
                continue;
            }

            // Calculate distance
            double distance = calculateDistance(
                    userLat,
                    userLng,
                    collector.getLatitude(),
                    collector.getLongitude()
            );


            // Check whether this collector is nearer
            if (distance < shortestDistance) {

                shortestDistance = distance;
                nearestCollector = collector;

            }
        }

        return nearestCollector;
    }


    /*
     * Calculate distance between two GPS locations
     * using the Haversine formula.
     *
     * Result is in kilometres.
     */
    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final double EARTH_RADIUS = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);

        double dLon = Math.toRadians(lon2 - lon1);


        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                +
                Math.cos(Math.toRadians(lat1))
                *
                Math.cos(Math.toRadians(lat2))
                *
                Math.sin(dLon / 2)
                *
                Math.sin(dLon / 2);


        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );


        return EARTH_RADIUS * c;
    }
}