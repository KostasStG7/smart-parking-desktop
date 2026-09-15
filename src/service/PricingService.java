package service;

import java.time.Duration;
import java.time.LocalDateTime;

public class PricingService {


    public static final double CAR_MIN_PRICE = 1.00;
    public static final double CAR_MAX_PRICE = 8.00;

    public static final double MOTORBIKE_MIN_PRICE = 0.50;
    public static final double MOTORBIKE_MAX_PRICE = 4.00;


    public boolean isValidPrice(
            String vehicleType,
            double pricePerHour
    ) {

        if (
                vehicleType == null
                        ||
                vehicleType.trim().isEmpty()
        ) {

            return false;
        }


        String vehicle =
                vehicleType
                        .trim()
                        .toUpperCase();


        if (
                "CAR".equals(
                        vehicle
                )
        ) {

            return pricePerHour >= CAR_MIN_PRICE
                    &&
                    pricePerHour <= CAR_MAX_PRICE;
        }


        if (
                "MOTORBIKE".equals(
                        vehicle
                )
        ) {

            return pricePerHour >= MOTORBIKE_MIN_PRICE
                    &&
                    pricePerHour <= MOTORBIKE_MAX_PRICE;
        }


        return false;
    }



    public String getPriceRuleText(
            String vehicleType
    ) {

        if (
                vehicleType == null
        ) {

            return "";
        }


        String vehicle =
                vehicleType
                        .trim()
                        .toUpperCase();


        if (
                "CAR".equals(
                        vehicle
                )
        ) {

            return String.format(
                    "Allowed price: %.2f € - %.2f € per hour",
                    CAR_MIN_PRICE,
                    CAR_MAX_PRICE
            );
        }


        if (
                "MOTORBIKE".equals(
                        vehicle
                )
        ) {

            return String.format(
                    "Allowed price: %.2f € - %.2f € per hour",
                    MOTORBIKE_MIN_PRICE,
                    MOTORBIKE_MAX_PRICE
            );
        }


        return "";
    }


    public long calculateTotalMinutes(
            LocalDateTime start,
            LocalDateTime end
    ) {

        if (
                start == null
                        ||
                end == null
                        ||
                !end.isAfter(
                        start
                )
        ) {

            return 0;
        }


        return Duration
                .between(
                        start,
                        end
                )
                .toMinutes();
    }


    public int calculateChargeableHours(
            LocalDateTime start,
            LocalDateTime end
    ) {

        long totalMinutes =
                calculateTotalMinutes(
                        start,
                        end
                );


        if (
                totalMinutes < 60
        ) {

            return 0;
        }



        long fullHours =
                totalMinutes / 60;



        long remainingMinutes =
                totalMinutes % 60;


        /*
         * RULE:
         *
         * 1:00 -> 1 hour
         * 1:30 -> 1 hour
         * 1:31 -> 2 hours
         *
         * 2:00 -> 2 hours
         * 2:30 -> 2 hours
         * 2:31 -> 3 hours
         */


        if (
                remainingMinutes > 30
        ) {

            fullHours++;
        }


        return (int)
                fullHours;
    }

    public double calculatePrice(
            boolean pricingEnabled,
            double pricePerHour,
            LocalDateTime start,
            LocalDateTime end
    ) {

        if (
                !pricingEnabled
        ) {

            return 0.00;
        }


        int chargeableHours =
                calculateChargeableHours(
                        start,
                        end
                );


        if (
                chargeableHours <= 0
        ) {

            return 0.00;
        }


        double total =
                chargeableHours
                        *
                pricePerHour;


        return roundMoney(
                total
        );
    }



    public boolean isPaymentRequired(
            boolean pricingEnabled,
            double calculatedPrice
    ) {

        return pricingEnabled
                &&
                calculatedPrice > 0;
    }

    public LocalDateTime calculatePaymentDeadline(
            LocalDateTime reservationStart
    ) {

        if (
                reservationStart == null
        ) {

            return null;
        }


        return reservationStart
                .minusMinutes(
                        30
                );
    }



    public boolean canCreateNormalBooking(
            LocalDateTime reservationStart
    ) {

        if (
                reservationStart == null
        ) {

            return false;
        }


        LocalDateTime deadline =
                calculatePaymentDeadline(
                        reservationStart
                );


        return LocalDateTime
                .now()
                .isBefore(
                        deadline
                );
    }

    public boolean isEmergencyWindow(
            LocalDateTime reservationStart
    ) {

        if (
                reservationStart == null
        ) {

            return false;
        }


        LocalDateTime now =
                LocalDateTime.now();

        LocalDateTime normalDeadline =
                calculatePaymentDeadline(
                        reservationStart
                );


        /*
         * Emergency booking:
         *
         * - reservation start is still in the future
         * - but we are already inside the final 30 minutes
         */


        return reservationStart
                .isAfter(
                        now
                )
                &&
                !now.isBefore(
                        normalDeadline
                );
    }


    public String formatPrice(
            double price
    ) {

        return String.format(
                "%.2f €",
                roundMoney(
                        price
                )
        );
    }

    private double roundMoney(
            double value
    ) {

        return Math.round(
                value * 100.0
        ) / 100.0;
    }
}