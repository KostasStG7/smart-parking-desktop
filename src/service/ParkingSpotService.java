package service;

import dao.ParkingSpotDAO;

import model.ParkingSpot;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class ParkingSpotService {

    private final ParkingSpotDAO parkingSpotDAO =
            new ParkingSpotDAO();

    private final PricingService pricingService =
            new PricingService();

    private String lastErrorMessage =
            "";

    public String getLastErrorMessage() {

        return lastErrorMessage;
    }

    private boolean fail(
            String message
    ) {

        lastErrorMessage =
                message;

        return false;
    }

    public List<ParkingSpot> getAllActiveSpots() {

        return parkingSpotDAO
                .findAllActive();
    }

    public List<ParkingSpot> searchByArea(
            String area
    ) {

        if (
                area == null
                        ||
                area.trim().isEmpty()
        ) {

            return new ArrayList<>();
        }

        return parkingSpotDAO
                .findByArea(
                        area
                );
    }

    public List<ParkingSpot> searchAvailableSpots(
            String area,
            String vehicleType,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime
    ) {

        if (
                startDatetime == null
                        ||
                endDatetime == null
        ) {

            return new ArrayList<>();
        }

        if (
                !endDatetime.isAfter(
                        startDatetime
                )
        ) {

            return new ArrayList<>();
        }

        String safeArea =
                area == null
                        ? ""
                        : area.trim();

        String safeVehicle =
                vehicleType == null
                        ||
                vehicleType.trim().isEmpty()
                        ? "All"
                        : vehicleType.trim();

        return parkingSpotDAO
                .searchAvailableSpots(
                        safeArea,
                        safeVehicle,
                        startDatetime,
                        endDatetime
                );
    }

    public ParkingSpot getSpotById(
            int spotId
    ) {

        return parkingSpotDAO
                .findById(
                        spotId
                );
    }

    public List<ParkingSpot> getSpotsByOwner(
            int ownerId
    ) {

        if (
                ownerId <= 0
        ) {

            return new ArrayList<>();
        }

        return parkingSpotDAO
                .findByOwnerId(
                        ownerId
                );
    }

    public boolean addParkingSpot(
            int currentUserId,
            String address,
            String area,
            String description,
            String vehicle,
            double lat,
            double lon
    ) {

        return addParkingSpot(
                currentUserId,
                address,
                area,
                description,
                vehicle,
                false,
                null,
                false,
                lat,
                lon
        );
    }

    public boolean addParkingSpot(
            int currentUserId,
            String address,
            String area,
            String description,
            String vehicle,
            boolean pricingEnabled,
            Double pricePerHour,
            boolean emergencyBookingEnabled,
            double lat,
            double lon
    ) {

        lastErrorMessage =
                "";

        if (
                currentUserId <= 0
        ) {

            return fail(
                    "Invalid user."
            );
        }

        if (
                address == null
                        ||
                address.trim().isEmpty()
        ) {

            return fail(
                    "Address is required."
            );
        }

        if (
                area == null
                        ||
                area.trim().isEmpty()
        ) {

            return fail(
                    "Area is required."
            );
        }

        if (
                vehicle == null
                        ||
                vehicle.trim().isEmpty()
        ) {

            return fail(
                    "Vehicle type is required."
            );
        }

        if (
                pricingEnabled
        ) {

            if (
                    pricePerHour == null
            ) {

                return fail(
                        "Price per hour is required for a paid parking spot."
                );
            }

            if (
                    !pricingService.isValidPrice(
                            vehicle,
                            pricePerHour
                    )
            ) {

                return fail(
                        pricingService.getPriceRuleText(
                                vehicle
                        )
                );
            }

        } else {

            pricePerHour =
                    null;
        }

        ParkingSpot spot =
                new ParkingSpot(
                        currentUserId,
                        address.trim(),
                        area.trim(),
                        description == null
                                ? ""
                                : description.trim(),
                        vehicle.trim(),
                        pricingEnabled,
                        pricePerHour,
                        emergencyBookingEnabled,
                        lat,
                        lon,
                        true
                );

        boolean inserted =
                parkingSpotDAO
                        .insertParkingSpot(
                                spot
                        );

        if (
                !inserted
        ) {

            return fail(
                    "Failed to save the parking spot."
            );
        }

        return true;
    }

    public boolean setSpotActive(
            int ownerId,
            int spotId,
            boolean active
    ) {

        lastErrorMessage =
                "";

        if (
                ownerId <= 0
                        ||
                spotId <= 0
        ) {

            return fail(
                    "Invalid parking spot."
            );
        }

        ParkingSpot spot =
                parkingSpotDAO
                        .findById(
                                spotId
                        );

        if (
                spot == null
        ) {

            return fail(
                    "Parking spot not found."
            );
        }

        if (
                spot.getOwnerId()
                        != ownerId
        ) {

            return fail(
                    "You cannot modify another user's parking spot."
            );
        }

        boolean updated =
                parkingSpotDAO
                        .updateActiveStatus(
                                spotId,
                                ownerId,
                                active
                        );

        if (
                !updated
        ) {

            return fail(
                    "Failed to update the parking spot."
            );
        }

        return true;
    }

    public boolean updateParkingSpot(
            int ownerId,
            int spotId,
            String address,
            String area,
            String description,
            String vehicleType,
            double latitude,
            double longitude
    ) {

        ParkingSpot existing =
                parkingSpotDAO
                        .findById(
                                spotId
                        );

        if (
                existing == null
        ) {

            lastErrorMessage =
                    "Parking spot not found.";

            return false;
        }

        return updateParkingSpot(
                ownerId,
                spotId,
                address,
                area,
                description,
                vehicleType,
                existing.isPricingEnabled(),
                existing.getPricePerHour(),
                existing.isEmergencyBookingEnabled(),
                latitude,
                longitude
        );
    }

    public boolean updateParkingSpot(
            int ownerId,
            int spotId,
            String address,
            String area,
            String description,
            String vehicleType,
            boolean pricingEnabled,
            Double pricePerHour,
            boolean emergencyBookingEnabled,
            double latitude,
            double longitude
    ) {

        lastErrorMessage =
                "";

        if (
                ownerId <= 0
                        ||
                spotId <= 0
        ) {

            return fail(
                    "Invalid parking spot."
            );
        }

        if (
                address == null
                        ||
                address.trim().isEmpty()
        ) {

            return fail(
                    "Address is required."
            );
        }

        if (
                area == null
                        ||
                area.trim().isEmpty()
        ) {

            return fail(
                    "Area is required."
            );
        }

        if (
                vehicleType == null
                        ||
                vehicleType.trim().isEmpty()
        ) {

            return fail(
                    "Vehicle type is required."
            );
        }

        ParkingSpot existing =
                parkingSpotDAO
                        .findById(
                                spotId
                        );

        if (
                existing == null
        ) {

            return fail(
                    "Parking spot not found."
            );
        }

        if (
                existing.getOwnerId()
                        != ownerId
        ) {

            return fail(
                    "You cannot modify another user's parking spot."
            );
        }

        if (
                pricingEnabled
        ) {

            if (
                    pricePerHour == null
            ) {

                return fail(
                        "Price per hour is required for a paid parking spot."
                );
            }

            if (
                    !pricingService.isValidPrice(
                            vehicleType,
                            pricePerHour
                    )
            ) {

                return fail(
                        pricingService.getPriceRuleText(
                                vehicleType
                        )
                );
            }

        } else {

            pricePerHour =
                    null;
        }

        existing.setAddress(
                address.trim()
        );

        existing.setArea(
                area.trim()
        );

        existing.setDescription(
                description == null
                        ? ""
                        : description.trim()
        );

        existing.setVehicleType(
                vehicleType.trim()
        );

        existing.setPricingEnabled(
                pricingEnabled
        );

        existing.setPricePerHour(
                pricePerHour
        );

        existing.setEmergencyBookingEnabled(
                emergencyBookingEnabled
        );

        existing.setLatitude(
                latitude
        );

        existing.setLongitude(
                longitude
        );

        boolean updated =
                parkingSpotDAO
                        .updateParkingSpot(
                                existing
                        );

        if (
                !updated
        ) {

            return fail(
                    "Failed to update the parking spot."
            );
        }

        return true;
    }
}