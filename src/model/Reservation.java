package model;

import java.time.LocalDateTime;

public class Reservation {

    private int reservationId;
    private int spotId;
    private int driverId;

    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;

    private String status;

    private double calculatedPrice;
    private String paymentStatus;
    private LocalDateTime paymentDeadline;

    private boolean pricingAccepted;
    private String bookingType;

    public Reservation() {
    }

    public Reservation(
            int reservationId,
            int spotId,
            int driverId,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            String status
    ) {

        this(
                reservationId,
                spotId,
                driverId,
                startDatetime,
                endDatetime,
                status,
                0.00,
                "NOT_REQUIRED",
                null,
                false,
                "NORMAL"
        );
    }

    public Reservation(
            int spotId,
            int driverId,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            String status
    ) {

        this(
                0,
                spotId,
                driverId,
                startDatetime,
                endDatetime,
                status,
                0.00,
                "NOT_REQUIRED",
                null,
                false,
                "NORMAL"
        );
    }

    public Reservation(
            int reservationId,
            int spotId,
            int driverId,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            String status,
            double calculatedPrice,
            String paymentStatus,
            LocalDateTime paymentDeadline,
            boolean pricingAccepted,
            String bookingType
    ) {

        this.reservationId = reservationId;
        this.spotId = spotId;
        this.driverId = driverId;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.status = status;
        this.calculatedPrice = calculatedPrice;
        this.paymentStatus = paymentStatus;
        this.paymentDeadline = paymentDeadline;
        this.pricingAccepted = pricingAccepted;
        this.bookingType = bookingType;
    }

    public Reservation(
            int spotId,
            int driverId,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            String status,
            double calculatedPrice,
            String paymentStatus,
            LocalDateTime paymentDeadline,
            boolean pricingAccepted,
            String bookingType
    ) {

        this(
                0,
                spotId,
                driverId,
                startDatetime,
                endDatetime,
                status,
                calculatedPrice,
                paymentStatus,
                paymentDeadline,
                pricingAccepted,
                bookingType
        );
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getSpotId() {
        return spotId;
    }

    public void setSpotId(int spotId) {
        this.spotId = spotId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
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

    public double getCalculatedPrice() {
        return calculatedPrice;
    }

    public void setCalculatedPrice(double calculatedPrice) {
        this.calculatedPrice = calculatedPrice;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentDeadline() {
        return paymentDeadline;
    }

    public void setPaymentDeadline(LocalDateTime paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }

    public boolean isPricingAccepted() {
        return pricingAccepted;
    }

    public void setPricingAccepted(boolean pricingAccepted) {
        this.pricingAccepted = pricingAccepted;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }
}