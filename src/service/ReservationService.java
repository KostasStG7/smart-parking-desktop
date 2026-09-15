package service;

import dao.AvailabilityDAO;
import dao.ParkingSpotDAO;
import dao.ReservationDAO;
import dao.UserDAO;

import model.ParkingSpot;
import model.Reservation;
import model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReservationService {

    private String lastErrorMessage = "";

    private boolean lastCancellationRefunded =
            false;

    private boolean lastCancellationPaidWithoutRefund =
            false;

    private double lastRefundAmount =
            0.00;

    private final ReservationDAO reservationDAO =
            new ReservationDAO();

    private final ParkingSpotDAO parkingSpotDAO =
            new ParkingSpotDAO();

    private final AvailabilityDAO availabilityDAO =
            new AvailabilityDAO();

    private final UserDAO userDAO =
            new UserDAO();

    private final NotificationService notificationService =
            new NotificationService();

    private final PricingService pricingService =
            new PricingService();

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public boolean wasLastCancellationRefunded() {
        return lastCancellationRefunded;
    }

    public boolean wasLastCancellationPaidWithoutRefund() {
        return lastCancellationPaidWithoutRefund;
    }

    public double getLastRefundAmount() {
        return lastRefundAmount;
    }

    private boolean fail(
            String message
    ) {

        lastErrorMessage =
                message;

        return false;
    }

    public boolean createReservation(
            int driverId,
            int spotId,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime
    ) {

        return createReservation(
                driverId,
                spotId,
                startDatetime,
                endDatetime,
                true,
                false
        );
    }

    public boolean createReservation(
            int driverId,
            int spotId,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            boolean pricingAccepted,
            boolean emergencyPaymentCompleted
    ) {

        lastErrorMessage = "";

        refreshExpiredReservations();

        if (
                driverId <= 0
        ) {

            return fail(
                    "Μη έγκυρο ID οδηγού."
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
                        ||
                endDatetime == null
        ) {

            return fail(
                    "Η ημερομηνία και ώρα έναρξης και λήξης είναι υποχρεωτικές."
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

        LocalDateTime currentMinute =
                LocalDateTime
                        .now()
                        .withSecond(0)
                        .withNano(0);

        if (
                startDatetime.isBefore(
                        currentMinute
                )
        ) {

            return fail(
                    "Δεν μπορείτε να κάνετε κράτηση για χρονικό διάστημα που έχει ήδη ξεκινήσει."
            );
        }

        User driver =
                userDAO.findById(
                        driverId
                );

        if (
                driver == null
        ) {

            return fail(
                    "Δεν βρέθηκε ο χρήστης."
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
                    "Δεν βρέθηκε η θέση parking."
            );
        }

        if (
                !spot.isActive()
        ) {

            return fail(
                    "Η θέση parking δεν είναι ενεργή."
            );
        }

        if (
                spot.getOwnerId()
                        == driverId
        ) {

            return fail(
                    "Δεν μπορείτε να κάνετε κράτηση στη δική σας θέση."
            );
        }

        if (
                !availabilityDAO
                        .isSpotAvailableForPeriod(
                                spotId,
                                startDatetime,
                                endDatetime
                        )
        ) {

            return fail(
                    "Η θέση δεν είναι διαθέσιμη για το ζητούμενο χρονικό διάστημα."
            );
        }

        if (
                reservationDAO
                        .hasOverlappingReservation(
                                spotId,
                                startDatetime,
                                endDatetime
                        )
        ) {

            return fail(
                    "Υπάρχει ήδη ενεργή κράτηση που επικαλύπτεται με αυτό το διάστημα."
            );
        }

        double hourlyPrice =
                spot.getPricePerHour() == null
                        ? 0.00
                        : spot.getPricePerHour();

        double calculatedPrice =
                pricingService
                        .calculatePrice(
                                spot.isPricingEnabled(),
                                hourlyPrice,
                                startDatetime,
                                endDatetime
                        );

        boolean paymentRequired =
                calculatedPrice > 0;

        if (
                paymentRequired
                        &&
                !pricingAccepted
        ) {

            return fail(
                    "Πρέπει να αποδεχτείτε τους κανόνες κοστολόγησης."
            );
        }

        String bookingType;

        if (
                pricingService
                        .canCreateNormalBooking(
                                startDatetime
                        )
        ) {

            bookingType =
                    "NORMAL";

        } else {

            if (
                    !spot.isEmergencyBookingEnabled()
            ) {

                return fail(
                        "Η κράτηση ξεκινά σε λιγότερο από 30 λεπτά και η θέση δεν επιτρέπει emergency booking."
                );
            }

            bookingType =
                    "EMERGENCY";
        }

        String status;
        String paymentStatus;
        LocalDateTime paymentDeadline;

        if (
                !paymentRequired
        ) {

            status =
                    "CONFIRMED";

            paymentStatus =
                    "NOT_REQUIRED";

            paymentDeadline =
                    null;

        } else if (
                "EMERGENCY".equals(
                        bookingType
                )
        ) {

            if (
                    !emergencyPaymentCompleted
            ) {

                return fail(
                        "Η emergency κράτηση απαιτεί άμεση πληρωμή."
                );
            }

            status =
                    "CONFIRMED";

            paymentStatus =
                    "PAID";

            paymentDeadline =
                    null;

        } else {

            status =
                    "PENDING_PAYMENT";

            paymentStatus =
                    "UNPAID";

            paymentDeadline =
                    pricingService
                            .calculatePaymentDeadline(
                                    startDatetime
                            );
        }

        Reservation reservation =
                new Reservation(
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

        if (
                !reservationDAO
                        .insertReservation(
                                reservation
                        )
        ) {

            return fail(
                    "Παρουσιάστηκε σφάλμα κατά την αποθήκευση της κράτησης."
            );
        }

        if (
                "PENDING_PAYMENT".equals(
                        status
                )
        ) {

            notificationService
                    .createNotification(
                            driverId,
                            "Η κράτησή σας αναμένει πληρωμή μέχρι "
                                    +
                            paymentDeadline.format(
                                    formatter
                            )
                                    +
                            "."
                    );

            notificationService
                    .createNotification(
                            spot.getOwnerId(),
                            "Νέα κράτηση στη θέση "
                                    +
                            spot.getAddress()
                                    +
                            " αναμένει πληρωμή."
                    );

        } else {

            notificationService
                    .createNotification(
                            driverId,
                            "Η κράτησή σας επιβεβαιώθηκε για τη θέση: "
                                    +
                            spot.getAddress()
                    );

            notificationService
                    .createNotification(
                            spot.getOwnerId(),
                            "Έγινε νέα επιβεβαιωμένη κράτηση στη θέση σας: "
                                    +
                            spot.getAddress()
                    );
        }

        return true;
    }

    public double calculateReservationPrice(
            int spotId,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime
    ) {

        ParkingSpot spot =
                parkingSpotDAO.findById(
                        spotId
                );

        if (
                spot == null
                        ||
                startDatetime == null
                        ||
                endDatetime == null
                        ||
                !endDatetime.isAfter(
                        startDatetime
                )
        ) {

            return 0.00;
        }

        double hourlyPrice =
                spot.getPricePerHour() == null
                        ? 0.00
                        : spot.getPricePerHour();

        return pricingService
                .calculatePrice(
                        spot.isPricingEnabled(),
                        hourlyPrice,
                        startDatetime,
                        endDatetime
                );
    }

    public String getBookingType(
            int spotId,
            LocalDateTime startDatetime
    ) {

        ParkingSpot spot =
                parkingSpotDAO.findById(
                        spotId
                );

        if (
                spot == null
                        ||
                startDatetime == null
        ) {

            return null;
        }

        if (
                pricingService
                        .canCreateNormalBooking(
                                startDatetime
                        )
        ) {

            return "NORMAL";
        }

        if (
                spot.isEmergencyBookingEnabled()
        ) {

            return "EMERGENCY";
        }

        return null;
    }

    public boolean payReservation(
            int driverId,
            int reservationId,
            double amount
    ) {

        lastErrorMessage = "";

        refreshExpiredReservations();

        Reservation reservation =
                reservationDAO.findById(
                        reservationId
                );

        if (
                reservation == null
        ) {

            return fail(
                    "Η κράτηση δεν βρέθηκε."
            );
        }

        if (
                reservation.getDriverId()
                        != driverId
        ) {

            return fail(
                    "Δεν μπορείτε να πληρώσετε κράτηση άλλου χρήστη."
            );
        }

        if (
                !"PENDING_PAYMENT".equalsIgnoreCase(
                        reservation.getStatus()
                )
                        ||
                !"UNPAID".equalsIgnoreCase(
                        reservation.getPaymentStatus()
                )
        ) {

            return fail(
                    "Η συγκεκριμένη κράτηση δεν αναμένει πληρωμή."
            );
        }

        if (
                reservation.getPaymentDeadline() != null
                        &&
                !LocalDateTime
                        .now()
                        .isBefore(
                                reservation.getPaymentDeadline()
                        )
        ) {

            refreshExpiredReservations();

            return fail(
                    "Η προθεσμία πληρωμής έχει λήξει."
            );
        }

        if (
                Math.abs(
                        amount
                                -
                        reservation.getCalculatedPrice()
                )
                        > 0.001
        ) {

            return fail(
                    "Το ποσό πρέπει να είναι ακριβώς "
                            +
                    pricingService.formatPrice(
                            reservation.getCalculatedPrice()
                    )
                            +
                    "."
            );
        }

        if (
                !reservationDAO
                        .markAsPaid(
                                reservationId,
                                driverId
                        )
        ) {

            return fail(
                    "Η πληρωμή δεν ολοκληρώθηκε."
            );
        }

        ParkingSpot spot =
                parkingSpotDAO.findById(
                        reservation.getSpotId()
                );

        notificationService
                .createNotification(
                        driverId,
                        "Η πληρωμή ολοκληρώθηκε και η κράτησή σας επιβεβαιώθηκε."
                );

        if (
                spot != null
        ) {

            notificationService
                    .createNotification(
                            spot.getOwnerId(),
                            "Η κράτηση για τη θέση σας εξοφλήθηκε: "
                                    +
                            spot.getAddress()
                    );
        }

        return true;
    }

    public void refreshExpiredReservations() {

        List<Reservation> expired =
                reservationDAO
                        .findExpiredUnpaidReservations(
                                LocalDateTime.now()
                        );

        for (
                Reservation reservation :
                expired
        ) {

            if (
                    !reservationDAO
                            .expireReservation(
                                    reservation.getReservationId()
                            )
            ) {

                continue;
            }

            notificationService
                    .createNotification(
                            reservation.getDriverId(),
                            "Η κράτησή σας ακυρώθηκε επειδή δεν εξοφλήθηκε έγκαιρα."
                    );

            ParkingSpot spot =
                    parkingSpotDAO.findById(
                            reservation.getSpotId()
                    );

            if (
                    spot != null
            ) {

                notificationService
                        .createNotification(
                                spot.getOwnerId(),
                                "Η μη εξοφλημένη κράτηση για τη θέση "
                                        +
                                spot.getAddress()
                                        +
                                " ακυρώθηκε."
                        );
            }
        }
    }

    public List<Reservation> getReservationsByDriver(
            int driverId
    ) {

        refreshExpiredReservations();

        if (
                driverId <= 0
        ) {

            return new ArrayList<>();
        }

        return reservationDAO.findByDriverId(
                driverId
        );
    }

    public List<Reservation> getReservationsBySpot(
            int spotId
    ) {

        refreshExpiredReservations();

        if (
                spotId <= 0
        ) {

            return new ArrayList<>();
        }

        return reservationDAO.findBySpotId(
                spotId
        );
    }

    public List<Reservation> getReservationsForOwner(
            int ownerId
    ) {

        refreshExpiredReservations();

        if (
                ownerId <= 0
        ) {

            return new ArrayList<>();
        }

        List<ParkingSpot> ownerSpots =
                parkingSpotDAO.findByOwnerId(
                        ownerId
                );

        List<Reservation> result =
                new ArrayList<>();

        for (
                ParkingSpot spot :
                ownerSpots
        ) {

            result.addAll(
                    reservationDAO
                            .findBySpotId(
                                    spot.getSpotId()
                            )
            );
        }

        result.sort(
                Comparator
                        .comparing(
                                Reservation::getStartDatetime
                        )
                        .reversed()
        );

        return result;
    }

    public User getDriverById(
            int driverId
    ) {

        if (
                driverId <= 0
        ) {

            return null;
        }

        return userDAO.findById(
                driverId
        );
    }

    public boolean cancelReservation(
            int driverId,
            int reservationId
    ) {

        lastErrorMessage =
                "";

        lastCancellationRefunded =
                false;

        lastCancellationPaidWithoutRefund =
                false;

        lastRefundAmount =
                0.00;

        refreshExpiredReservations();

        Reservation reservation =
                reservationDAO.findById(
                        reservationId
                );

        if (
                reservation == null
        ) {

            return fail(
                    "Η κράτηση δεν βρέθηκε."
            );
        }

        if (
                reservation.getDriverId()
                        != driverId
        ) {

            return fail(
                    "Δεν μπορείτε να ακυρώσετε κράτηση άλλου χρήστη."
            );
        }

        if (
                !"CONFIRMED".equalsIgnoreCase(
                        reservation.getStatus()
                )
                        &&
                !"PENDING_PAYMENT".equalsIgnoreCase(
                        reservation.getStatus()
                )
        ) {

            return fail(
                    "Η κράτηση δεν είναι ενεργή."
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        if (
                !reservation
                        .getStartDatetime()
                        .isAfter(
                                now
                        )
        ) {

            return fail(
                    "Δεν μπορείτε να ακυρώσετε κράτηση που έχει ήδη ξεκινήσει."
            );
        }

        boolean paid =
                "PAID".equalsIgnoreCase(
                        reservation.getPaymentStatus()
                );

        LocalDateTime refundDeadline =
                reservation
                        .getStartDatetime()
                        .minusHours(
                                1
                        );

        boolean qualifiesForRefund =
                paid
                        &&
                !now.isAfter(
                        refundDeadline
                );

        boolean cancelled;

        if (
                qualifiesForRefund
        ) {

            cancelled =
                    reservationDAO
                            .cancelReservationWithRefund(
                                    reservationId,
                                    driverId
                            );

        } else {

            cancelled =
                    reservationDAO
                            .cancelReservation(
                                    reservationId,
                                    driverId
                            );
        }

        if (
                !cancelled
        ) {

            return fail(
                    "Η ακύρωση της κράτησης απέτυχε."
            );
        }

        ParkingSpot spot =
                parkingSpotDAO.findById(
                        reservation.getSpotId()
                );

        if (
                qualifiesForRefund
        ) {

            lastCancellationRefunded =
                    true;

            lastRefundAmount =
                    reservation.getCalculatedPrice();

            notificationService
                    .createNotification(
                            driverId,
                            "Η κράτησή σας ακυρώθηκε και επιστράφηκαν "
                                    +
                            pricingService.formatPrice(
                                    reservation.getCalculatedPrice()
                            )
                                    +
                            "."
                    );

        } else if (
                paid
        ) {

            lastCancellationPaidWithoutRefund =
                    true;

            notificationService
                    .createNotification(
                            driverId,
                            "Η κράτησή σας ακυρώθηκε χωρίς επιστροφή χρημάτων, επειδή η ακύρωση έγινε λιγότερο από 1 ώρα πριν την έναρξη."
                    );

        } else {

            notificationService
                    .createNotification(
                            driverId,
                            "Η κράτησή σας ακυρώθηκε."
                    );
        }

        if (
                spot != null
        ) {

            notificationService
                    .createNotification(
                            spot.getOwnerId(),
                            "Ακυρώθηκε κράτηση για τη θέση σας: "
                                    +
                            spot.getAddress()
                    );
        }

        return true;
    }

    public boolean hasOverlappingReservation(
            int spotId,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime
    ) {

        refreshExpiredReservations();

        if (
                spotId <= 0
                        ||
                startDatetime == null
                        ||
                endDatetime == null
                        ||
                !endDatetime.isAfter(
                        startDatetime
                )
        ) {

            return false;
        }

        return reservationDAO
                .hasOverlappingReservation(
                        spotId,
                        startDatetime,
                        endDatetime
                );
    }
}