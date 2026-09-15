package model;

import java.time.LocalDateTime;

public class Availability {

    private int availabilityId;
    private int spotId;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private String status;

    public Availability() {
    }

    public Availability(int availabilityId, int spotId, LocalDateTime startDatetime,
                        LocalDateTime endDatetime, String status) {
        this.availabilityId = availabilityId;
        this.spotId = spotId;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.status = status;
    }

    public Availability(int spotId, LocalDateTime startDatetime,
                        LocalDateTime endDatetime, String status) {
        this.spotId = spotId;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.status = status;
    }

    public int getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(int availabilityId) {
        this.availabilityId = availabilityId;
    }

    public int getSpotId() {
        return spotId;
    }

    public void setSpotId(int spotId) {
        this.spotId = spotId;
    }

    public LocalDateTime getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(LocalDateTime startDatetime) {
        this.startDatetime = startDatetime;
    }

    public LocalDateTime getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(LocalDateTime endDatetime) {
        this.endDatetime = endDatetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}