package gui;

import javax.swing.*;
import java.awt.*;

import service.PricingService;
import service.ReservationService;
import service.ParkingSpotService;

import model.ParkingSpot;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReservationPanel extends JPanel {

    private final JComboBox<ParkingSpot> spotComboBox;

    private final JSpinner startDateSpinner;
    private final JSpinner startTimeSpinner;

    private final JSpinner endDateSpinner;
    private final JSpinner endTimeSpinner;

    private final ReservationService reservationService;
    private final ParkingSpotService parkingSpotService;
    private final PricingService pricingService;

    private int currentUserId;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    public ReservationPanel(
            int userId
    ) {

        this.currentUserId =
                userId;

        this.reservationService =
                new ReservationService();

        this.parkingSpotService =
                new ParkingSpotService();

        this.pricingService =
                new PricingService();

        setLayout(
                new BorderLayout()
        );

        setBackground(
                UITheme.BACKGROUND
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        24,
                        26,
                        26,
                        26
                )
        );

        add(
                UITheme.pageHeader(
                        "Create Reservation",
                        "Reserve an available parking spot for a specific date and time range."
                ),
                BorderLayout.NORTH
        );

        JPanel center =
                new JPanel(
                        new GridBagLayout()
                );

        center.setOpaque(
                false
        );

        UITheme.RoundedPanel card =
                new UITheme.RoundedPanel(
                        18,
                        Color.WHITE
                );

        card.setOutlineColor(
                UITheme.BORDER
        );

        card.setLayout(
                new GridBagLayout()
        );

        card.setBorder(
                BorderFactory.createEmptyBorder(
                        28,
                        32,
                        28,
                        32
                )
        );

        card.setPreferredSize(
                new Dimension(
                        650,
                        480
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx =
                0;

        gbc.weightx =
                1;

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        int row =
                0;

        JLabel heading =
                new JLabel(
                        "Reservation details"
                );

        heading.setFont(
                UITheme.SECTION
        );

        heading.setForeground(
                UITheme.TEXT
        );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        6,
                        0
                );

        card.add(
                heading,
                gbc
        );

        JLabel helper =
                UITheme.subtitle(
                        "Choose a parking spot and a time period within its availability."
                );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        22,
                        0
                );

        card.add(
                helper,
                gbc
        );

        spotComboBox =
                new JComboBox<>();

        spotComboBox.setFont(
                UITheme.FONT
        );

        spotComboBox.setPreferredSize(
                new Dimension(
                        100,
                        40
                )
        );

        spotComboBox.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {

                        JLabel label =
                                (JLabel)
                                        super.getListCellRendererComponent(
                                                list,
                                                value,
                                                index,
                                                isSelected,
                                                cellHasFocus
                                        );

                        if (
                                value instanceof ParkingSpot
                        ) {

                            ParkingSpot spot =
                                    (ParkingSpot)
                                            value;

                            String text =
                                    spot.getAddress()
                                            +
                                    " - "
                                            +
                                    spot.getArea();

                            if (
                                    spot.isPricingEnabled()
                                            &&
                                    spot.getPricePerHour() != null
                            ) {

                                text +=
                                        " • "
                                                +
                                        pricingService
                                                .formatPrice(
                                                        spot.getPricePerHour()
                                                )
                                                +
                                        "/h";

                            } else {

                                text +=
                                        " • Free";
                            }

                            label.setText(
                                    text
                            );
                        }

                        return label;
                    }
                }
        );

        loadParkingSpots();

        Date now =
                new Date();

        Calendar calendar =
                Calendar.getInstance();

        calendar.setTime(
                now
        );

        calendar.add(
                Calendar.HOUR_OF_DAY,
                1
        );

        Date oneHourLater =
                calendar.getTime();

        startDateSpinner =
                createDateSpinner(
                        now
                );

        startTimeSpinner =
                createTimeSpinner(
                        now
                );

        endDateSpinner =
                createDateSpinner(
                        oneHourLater
                );

        endTimeSpinner =
                createTimeSpinner(
                        oneHourLater
                );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        6,
                        0
                );

        card.add(
                UITheme.fieldLabel(
                        "Parking spot"
                ),
                gbc
        );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        18,
                        0
                );

        card.add(
                spotComboBox,
                gbc
        );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        6,
                        0
                );

        card.add(
                UITheme.fieldLabel(
                        "Start"
                ),
                gbc
        );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        18,
                        0
                );

        card.add(
                createDateTimePanel(
                        startDateSpinner,
                        startTimeSpinner
                ),
                gbc
        );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        6,
                        0
                );

        card.add(
                UITheme.fieldLabel(
                        "End"
                ),
                gbc
        );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        22,
                        0
                );

        card.add(
                createDateTimePanel(
                        endDateSpinner,
                        endTimeSpinner
                ),
                gbc
        );

        UITheme.ModernButton reserveButton =
                UITheme.primaryButton(
                        "Continue"
                );

        gbc.gridy =
                row;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        0,
                        0
                );

        card.add(
                reserveButton,
                gbc
        );

        reserveButton.addActionListener(
                e -> performReservation()
        );

        center.add(
                card
        );

        add(
                center,
                BorderLayout.CENTER
        );
    }

    private void loadParkingSpots() {

        spotComboBox.removeAllItems();

        List<ParkingSpot> spots =
                parkingSpotService
                        .getAllActiveSpots();

        for (
                ParkingSpot spot :
                spots
        ) {

            if (
                    spot.getOwnerId()
                            != currentUserId
            ) {

                spotComboBox.addItem(
                        spot
                );
            }
        }
    }

    private JSpinner createDateSpinner(
            Date initialDate
    ) {

        JSpinner spinner =
                new JSpinner(
                        new SpinnerDateModel(
                                initialDate,
                                null,
                                null,
                                Calendar.DAY_OF_MONTH
                        )
                );

        spinner.setEditor(
                new JSpinner.DateEditor(
                        spinner,
                        "dd/MM/yyyy"
                )
        );

        spinner.setPreferredSize(
                new Dimension(
                        220,
                        40
                )
        );

        return spinner;
    }

    private JSpinner createTimeSpinner(
            Date initialDate
    ) {

        JSpinner spinner =
                new JSpinner(
                        new SpinnerDateModel(
                                initialDate,
                                null,
                                null,
                                Calendar.MINUTE
                        )
                );

        spinner.setEditor(
                new JSpinner.DateEditor(
                        spinner,
                        "HH:mm"
                )
        );

        spinner.setPreferredSize(
                new Dimension(
                        150,
                        40
                )
        );

        return spinner;
    }

    private JPanel createDateTimePanel(
            JSpinner dateSpinner,
            JSpinner timeSpinner
    ) {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                12,
                                0
                        )
                );

        panel.setOpaque(
                false
        );

        JPanel datePanel =
                new JPanel(
                        new BorderLayout(
                                0,
                                5
                        )
                );

        datePanel.setOpaque(
                false
        );

        datePanel.add(
                new JLabel(
                        "Date"
                ),
                BorderLayout.NORTH
        );

        datePanel.add(
                dateSpinner,
                BorderLayout.CENTER
        );

        JPanel timePanel =
                new JPanel(
                        new BorderLayout(
                                0,
                                5
                        )
                );

        timePanel.setOpaque(
                false
        );

        timePanel.add(
                new JLabel(
                        "Time"
                ),
                BorderLayout.NORTH
        );

        timePanel.add(
                timeSpinner,
                BorderLayout.CENTER
        );

        panel.add(
                datePanel
        );

        panel.add(
                timePanel
        );

        return panel;
    }

    private LocalDateTime combineDateAndTime(
            JSpinner dateSpinner,
            JSpinner timeSpinner
    ) {

        Date selectedDate =
                (Date)
                        dateSpinner.getValue();

        Date selectedTime =
                (Date)
                        timeSpinner.getValue();

        Calendar dateCalendar =
                Calendar.getInstance();

        dateCalendar.setTime(
                selectedDate
        );

        Calendar timeCalendar =
                Calendar.getInstance();

        timeCalendar.setTime(
                selectedTime
        );

        dateCalendar.set(
                Calendar.HOUR_OF_DAY,
                timeCalendar.get(
                        Calendar.HOUR_OF_DAY
                )
        );

        dateCalendar.set(
                Calendar.MINUTE,
                timeCalendar.get(
                        Calendar.MINUTE
                )
        );

        dateCalendar.set(
                Calendar.SECOND,
                0
        );

        dateCalendar.set(
                Calendar.MILLISECOND,
                0
        );

        return dateCalendar
                .getTime()
                .toInstant()
                .atZone(
                        ZoneId.systemDefault()
                )
                .toLocalDateTime();
    }

    private void performReservation() {

        ParkingSpot selectedSpot =
                (ParkingSpot)
                        spotComboBox
                                .getSelectedItem();

        if (
                selectedSpot == null
        ) {

            UITheme.showWarning(
                    this,
                    "Parking Spot Required",
                    "Please select a parking spot."
            );

            return;
        }

        LocalDateTime startDt =
                combineDateAndTime(
                        startDateSpinner,
                        startTimeSpinner
                );

        LocalDateTime endDt =
                combineDateAndTime(
                        endDateSpinner,
                        endTimeSpinner
                );

        if (
                !endDt.isAfter(
                        startDt
                )
        ) {

            UITheme.showWarning(
                    this,
                    "Invalid Time Range",
                    "End date/time must be after the start date/time."
            );

            return;
        }

        double totalPrice =
                reservationService
                        .calculateReservationPrice(
                                selectedSpot.getSpotId(),
                                startDt,
                                endDt
                        );

        String bookingType =
                reservationService
                        .getBookingType(
                                selectedSpot.getSpotId(),
                                startDt
                        );

        if (
                bookingType == null
        ) {

            UITheme.showWarning(
                    this,
                    "Reservation Not Available",
                    "This reservation starts within 30 minutes and the owner has not enabled emergency booking."
            );

            return;
        }

        boolean pricingAccepted =
                true;

        if (
                totalPrice > 0
        ) {

            pricingAccepted =
                    showPricingPolicy(
                            selectedSpot,
                            startDt,
                            endDt,
                            totalPrice,
                            bookingType
                    );

            if (
                    !pricingAccepted
            ) {

                return;
            }
        }

        boolean emergencyPaymentCompleted =
                false;

        if (
                totalPrice > 0
                        &&
                "EMERGENCY".equals(
                        bookingType
                )
        ) {

            Double amount =
                    PaymentDialog.showPayment(
                            this,
                            totalPrice
                    );

            if (
                    amount == null
            ) {

                return;
            }

            emergencyPaymentCompleted =
                    true;
        }

        boolean success =
                reservationService
                        .createReservation(
                                currentUserId,
                                selectedSpot.getSpotId(),
                                startDt,
                                endDt,
                                pricingAccepted,
                                emergencyPaymentCompleted
                        );

        if (
                success
        ) {

            if (
                    totalPrice <= 0
            ) {

                UITheme.showSuccess(
                        this,
                        "Reservation Confirmed",
                        "Reservation created successfully. No payment is required."
                );

            } else if (
                    "EMERGENCY".equals(
                            bookingType
                    )
            ) {

                UITheme.showSuccess(
                        this,
                        "Reservation Confirmed",
                        "Payment completed and the emergency reservation is confirmed."
                );

            } else {

                LocalDateTime deadline =
                        startDt.minusMinutes(
                                30
                        );

                UITheme.showSuccess(
                        this,
                        "Reservation Created",
                        "Your reservation is awaiting payment. Pay it from My Reservations before "
                                +
                        deadline.format(
                                formatter
                        )
                                +
                        "."
                );
            }

            resetDateTime();

        } else {

            String error =
                    reservationService
                            .getLastErrorMessage();

            if (
                    error == null
                            ||
                    error.isBlank()
            ) {

                error =
                        "Failed to create the reservation.";
            }

            UITheme.showError(
                    this,
                    "Reservation Failed",
                    error
            );
        }
    }

    private boolean showPricingPolicy(
            ParkingSpot spot,
            LocalDateTime start,
            LocalDateTime end,
            double totalPrice,
            String bookingType
    ) {

        JPanel panel =
                new JPanel();

        panel.setOpaque(
                false
        );

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        int chargeableHours =
                pricingService
                        .calculateChargeableHours(
                                start,
                                end
                        );

        addPolicyLine(
                panel,
                "Price per hour",
                pricingService.formatPrice(
                        spot.getPricePerHour()
                )
        );

        addPolicyLine(
                panel,
                "Charged hours",
                String.valueOf(
                        chargeableHours
                )
        );

        addPolicyLine(
                panel,
                "Total",
                pricingService.formatPrice(
                        totalPrice
                )
        );

        addPolicyLine(
                panel,
                "Booking type",
                bookingType
        );

        panel.add(
                Box.createVerticalStrut(
                        15
                )
        );

        JLabel rules =
                new JLabel(
                        "<html>"
                                +
                        "Pricing rules:<br>"
                                +
                        "• Reservations under 1 hour are free.<br>"
                                +
                        "• Up to 30 extra minutes do not add another hour.<br>"
                                +
                        "• More than 30 extra minutes count as one additional hour.<br>"
                                +
                        ("EMERGENCY".equals(bookingType)
                                ? "• Emergency bookings must be paid immediately."
                                : "• Payment must be completed at least 30 minutes before the reservation.")
                                +
                        "</html>"
                );

        rules.setFont(
                UITheme.FONT
        );

        rules.setForeground(
                UITheme.MUTED
        );

        rules.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(
                rules
        );

        panel.add(
                Box.createVerticalStrut(
                        18
                )
        );

        JCheckBox acceptCheckBox =
                new JCheckBox(
                        "I understand and accept the pricing policy."
                );

        acceptCheckBox.setOpaque(
                false
        );

        acceptCheckBox.setFont(
                UITheme.FONT_BOLD
        );

        acceptCheckBox.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(
                acceptCheckBox
        );

        boolean confirmed =
                UITheme.showComponentConfirmDialog(
                        this,
                        "Pricing Policy",
                        panel,
                        "Continue",
                        "Cancel"
                );

        if (
                !confirmed
        ) {

            return false;
        }

        if (
                !acceptCheckBox.isSelected()
        ) {

            UITheme.showWarning(
                    this,
                    "Acceptance Required",
                    "You must accept the pricing policy before creating the reservation."
            );

            return false;
        }

        return true;
    }

    private void addPolicyLine(
            JPanel panel,
            String title,
            String value
    ) {

        JLabel label =
                new JLabel(
                        title
                                +
                        ": "
                                +
                        value
                );

        label.setFont(
                UITheme.FONT_BOLD
        );

        label.setForeground(
                UITheme.TEXT
        );

        label.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(
                label
        );

        panel.add(
                Box.createVerticalStrut(
                        7
                )
        );
    }

    public void prepareForReservation(
            int userId,
            int spotId
    ) {

        this.currentUserId =
                userId;

        loadParkingSpots();

        for (
                int i = 0;
                i < spotComboBox.getItemCount();
                i++
        ) {

            ParkingSpot spot =
                    spotComboBox.getItemAt(
                            i
                    );

            if (
                    spot.getSpotId()
                            == spotId
            ) {

                spotComboBox.setSelectedIndex(
                        i
                );

                break;
            }
        }

        startDateSpinner.requestFocusInWindow();
    }

    private void resetDateTime() {

        Date now =
                new Date();

        Calendar calendar =
                Calendar.getInstance();

        calendar.setTime(
                now
        );

        calendar.add(
                Calendar.HOUR_OF_DAY,
                1
        );

        Date oneHourLater =
                calendar.getTime();

        startDateSpinner.setValue(
                now
        );

        startTimeSpinner.setValue(
                now
        );

        endDateSpinner.setValue(
                oneHourLater
        );

        endTimeSpinner.setValue(
                oneHourLater
        );
    }
}