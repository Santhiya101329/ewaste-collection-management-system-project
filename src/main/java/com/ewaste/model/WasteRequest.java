package com.ewaste.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "waste_requests")
public class WasteRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String category;

    private String address;

    private LocalDate preferredDate;

    private String status = "PENDING";

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "collector_id")
    private Collector collector;

    // User's selected pickup location
    private Double latitude;
    private Double longitude;

    // Collector's location captured during pickup
    private Double collectorLatitude;
    private Double collectorLongitude;

    // Time when collector location was captured
    private LocalDateTime collectorLocationCapturedAt;

    // Photo captured by collector
    private String pickupPhotoPath;

    // Distance between user location and collector location
    private Double locationDistance;

    // ---------------- GETTERS AND SETTERS ----------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(LocalDate preferredDate) {
        this.preferredDate = preferredDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getCollectorLatitude() {
        return collectorLatitude;
    }

    public void setCollectorLatitude(Double collectorLatitude) {
        this.collectorLatitude = collectorLatitude;
    }

    public Double getCollectorLongitude() {
        return collectorLongitude;
    }

    public void setCollectorLongitude(Double collectorLongitude) {
        this.collectorLongitude = collectorLongitude;
    }

    public LocalDateTime getCollectorLocationCapturedAt() {
        return collectorLocationCapturedAt;
    }

    public void setCollectorLocationCapturedAt(LocalDateTime collectorLocationCapturedAt) {
        this.collectorLocationCapturedAt = collectorLocationCapturedAt;
    }

    public String getPickupPhotoPath() {
        return pickupPhotoPath;
    }

    public void setPickupPhotoPath(String pickupPhotoPath) {
        this.pickupPhotoPath = pickupPhotoPath;
    }

    public Double getLocationDistance() {
        return locationDistance;
    }

    public void setLocationDistance(Double locationDistance) {
        this.locationDistance = locationDistance;
    }
}