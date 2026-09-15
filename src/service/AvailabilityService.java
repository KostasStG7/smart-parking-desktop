package service;

import dao.AvailabilityDAO;
import dao.ParkingSpotDAO;
import dao.ReservationDAO;

import model.Availability;
import model.ParkingSpot;
import model.Reservation;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class AvailabilityService {

    private String lastErrorMessage = "";

    private final AvailabilityDAO availabilityDAO =
            new AvailabilityDAO();

    private final ParkingSpotDAO parkingSpotDAO =
            new ParkingSpotDAO();

    private final ReservationDAO reservationDAO =
            new ReservationDAO();

    // =============================================================
    // LAST ERROR
    // =============================================================

    public String getLastErrorMessage() {

        return lastErrorMessage;
    }

    private boolean fail(
            String message
    ) {

        lastErrorMessage =
                message;

        System.out.println(
                message
        );

        return false;
    }

    // =============================================================
    // ADD AVAILABILITY
    // =============================================================

    public boolean addAvailability(
            int userId,
            int spotId,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime
    ) {

        lastErrorMessage = "";

        if (
                userId <= 0
        ) {

            return fail(
                    "Μη έγκυρο ID χρήστη."
            );
        }

        if (
                spotId <= 0
        ) {

            return fail(
                    "Μη έγκυρο ID θέσης."
            );
        }

        if (
                startDatetime == null
        ) {

            return fail(
                    "Η ημερομηνία/ώρα έναρξης δεν μπορεί να είναι κενή."
            );
        }

        if (
                endDatetime == null
        ) {

            return fail(
                    "Η ημερομηνία/ώρα λήξης δεν μπορεί να είναι κενή."
            );
        }

        if (
                !endDatetime.isAfter(
                        startDatetime
                )
        ) {

            return fail(
                    "Η ώρα λήξης πρέπει να είναι μετά την ώρα έναρξης."
            );
        }

        ParkingSpot spot =
                parkingSpotDAO.findById(
                        spotId
                );

        if (
                spot == null
        ) {

            return fail(
                    "Δεν βρέθηκε θέση parking με αυτό το ID."
            );
        }

        if (
                spot.getOwnerId()
                        != userId
        ) {

            return fail(
                    "Δεν μπορείτε να δηλώσετε διαθεσιμότητα για θέση που δεν σας ανήκει."
            );
        }

        if (
                !spot.isActive()
        ) {

            return fail(
                    "Η θέση parking δεν είναι ενεργή."
            );
        }

        boolean hasOverlap =
                availabilityDAO
                        .hasOverlappingAvailability(
                                spotId,
                                startDatetime,
                                endDatetime
                        );

        if (
                hasOverlap
        ) {

            return fail(
                    "Υπάρχει ήδη διαθέσιμο χρονικό διάστημα που επικαλύπτεται."
            );
        }

        Availability availability =
                new Availability(
                        spotId,
                        startDatetime,
                        endDatetime,
                        "AVAILABLE"
                );

        boolean inserted =
                availabilityDAO
                        .insertAvailability(
                                availability
                        );

        if (
                !inserted
        ) {

            return fail(
                    "Παρουσιάστηκε σφάλμα κατά την αποθήκευση της διαθεσιμότητας."
            );
        }

        return true;
    }

    // =============================================================
    // GET BY SPOT
    // =============================================================

    public List<Availability> getAvailabilitiesBySpot(
            int spotId
    ) {

        if (
                spotId <= 0
        ) {

            return new ArrayList<>();
        }

        return availabilityDAO
                .findBySpotId(
                        spotId
                );
    }

    // =============================================================
    // GET ALL AVAILABILITIES OF USER
    // =============================================================

    public List<Availability> getAvailabilitiesByOwner(
            int ownerId
    ) {

        if (
                ownerId <= 0
        ) {

            return new ArrayList<>();
        }

        return availabilityDAO
                .findByOwnerId(
                        ownerId
                );
    }

    // =============================================================
    // GET ACTIVE PERIODS
    // =============================================================

    public List<Availability> getAvailablePeriodsBySpot(
            int spotId
    ) {

        if (
                spotId <= 0
        ) {

            return new ArrayList<>();
        }

        return availabilityDAO
                .findAvailableBySpotId(
                        spotId
                );
    }

    // =============================================================
    // CANCEL AVAILABILITY
    // =============================================================

    public boolean cancelAvailability(
            int ownerId,
            int availabilityId
    ) {

        lastErrorMessage = "";

        if (
                ownerId <= 0
        ) {

            return fail(
                    "Μη έγκυρο ID χρήστη."
            );
        }

        if (
                availabilityId <= 0
        ) {

            return fail(
                    "Μη έγκυρο ID διαθεσιμότητας."
            );
        }

        Availability availability =
                availabilityDAO
                        .findById(
                                availabilityId
                        );

        if (
                availability == null
        ) {

            return fail(
                    "Η διαθεσιμότητα δεν βρέθηκε."
            );
        }

        ParkingSpot spot =
                parkingSpotDAO
                        .findById(
                                availability.getSpotId()
                        );

        if (
                spot == null
        ) {

            return fail(
                    "Η θέση parking δεν βρέθηκε."
            );
        }

        // Μόνο ο owner της θέσης
        if (
                spot.getOwnerId()
                        != ownerId
        ) {

            return fail(
                    "Δεν μπορείτε να αλλάξετε διαθεσιμότητα άλλου χρήστη."
            );
        }

        if (
                !"AVAILABLE".equalsIgnoreCase(
                        availability.getStatus()
                )
        ) {

            return fail(
                    "Η διαθεσιμότητα δεν είναι πλέον ενεργή."
            );
        }

        // =========================================================
        // CHECK CONFIRMED RESERVATIONS
        // =========================================================

        List<Reservation> reservations =
                reservationDAO
                        .findBySpotId(
                                availability.getSpotId()
                        );

        boolean hasConfirmedReservation =
                reservations
                        .stream()
                        .anyMatch(
                                reservation ->
                                        "CONFIRMED"
                                                .equalsIgnoreCase(
                                                        reservation.getStatus()
                                                )
                                                &&
                                        reservation
                                                .getStartDatetime()
                                                .isBefore(
                                                        availability
                                                                .getEndDatetime()
                                                )
                                                &&
                                        reservation
                                                .getEndDatetime()
                                                .isAfter(
                                                        availability
                                                                .getStartDatetime()
                                                )
                        );

        if (
                hasConfirmedReservation
        ) {

            return fail(
                    "Δεν μπορείτε να ακυρώσετε αυτή τη διαθεσιμότητα επειδή υπάρχει ενεργή κράτηση μέσα σε αυτό το χρονικό διάστημα."
            );
        }

        boolean cancelled =
                availabilityDAO
                        .cancelAvailability(
                                availabilityId
                        );

        if (
                !cancelled
        ) {

            return fail(
                    "Η ακύρωση της διαθεσιμότητας απέτυχε."
            );
        }

        return true;
    }

    // =============================================================
    // CHECK AVAILABILITY FOR PERIOD
    // =============================================================

    public boolean isSpotAvailableForPeriod(
            int spotId,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime
    ) {

        if (
                spotId <= 0
        ) {

            return false;
        }

        if (
                startDatetime == null
                        ||
                endDatetime == null
        ) {

            return false;
        }

        if (
                !endDatetime.isAfter(
                        startDatetime
                )
        ) {

            return false;
        }

        return availabilityDAO
                .isSpotAvailableForPeriod(
                        spotId,
                        startDatetime,
                        endDatetime
                );
    }
}