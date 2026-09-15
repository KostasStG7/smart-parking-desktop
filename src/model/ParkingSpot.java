package model;

public class ParkingSpot {

    private int spotId;
    private int ownerId;
    private String address;
    private String area;
    private String description;
    private String vehicleType;

    private boolean pricingEnabled;
    private Double pricePerHour;
    private boolean emergencyBookingEnabled;

    private Double latitude;
    private Double longitude;
    private boolean active;

    public ParkingSpot() {
    }

    public ParkingSpot(
            int ownerId,
            String address,
            String area,
            String description,
            String vehicleType,
            boolean pricingEnabled,
            Double pricePerHour,
            boolean emergencyBookingEnabled,
            Double latitude,
            Double longitude,
            boolean active
    ) {

        this.ownerId = ownerId;
        this.address = address;
        this.area = area;
        this.description = description;
        this.vehicleType = vehicleType;

        this.pricingEnabled = pricingEnabled;
        this.pricePerHour = pricePerHour;
        this.emergencyBookingEnabled = emergencyBookingEnabled;

        this.latitude = latitude;
        this.longitude = longitude;
        this.active = active;
    }

    public int getSpotId() {
        return spotId;
    }

    public void setSpotId(int spotId) {
        this.spotId = spotId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isPricingEnabled() {
        return pricingEnabled;
    }

    public void setPricingEnabled(boolean pricingEnabled) {
        this.pricingEnabled = pricingEnabled;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public boolean isEmergencyBookingEnabled() {
        return emergencyBookingEnabled;
    }

    public void setEmergencyBookingEnabled(boolean emergencyBookingEnabled) {
        this.emergencyBookingEnabled = emergencyBookingEnabled;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}