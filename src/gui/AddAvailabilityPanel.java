package gui;

import javax.swing.*;
import java.awt.*;

import service.AvailabilityService;
import service.ParkingSpotService;

import model.ParkingSpot;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddAvailabilityPanel extends JPanel {

    private final JComboBox<ParkingSpot> spotComboBox;

    private final JSpinner startDateSpinner;
    private final JSpinner startTimeSpinner;

    private final JSpinner endDateSpinner;
    private final JSpinner endTimeSpinner;

    private final AvailabilityService availabilityService;
    private final ParkingSpotService parkingSpotService;

    private final int currentUserId;

    public AddAvailabilityPanel(
            int userId,
            DashboardPanel dashboardPanel
    ) {

        this.currentUserId = userId;

        this.availabilityService =
                new AvailabilityService();

        this.parkingSpotService =
                new ParkingSpotService();

        // =========================================================
        // MAIN PANEL
        // =========================================================

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

        // =========================================================
        // HEADER
        // =========================================================

        add(
                UITheme.pageHeader(
                        "Add Availability",
                        "Choose when one of your parking spots can be reserved by other users."
                ),
                BorderLayout.NORTH
        );

        // =========================================================
        // CENTER
        // =========================================================

        JPanel center =
                new JPanel(
                        new GridBagLayout()
                );

        center.setOpaque(false);

        // =========================================================
        // CARD
        // =========================================================

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
                        470
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 1;

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        // =========================================================
        // TITLE
        // =========================================================

        JLabel heading =
                new JLabel(
                        "Availability window"
                );

        heading.setFont(
                UITheme.SECTION
        );

        heading.setForeground(
                UITheme.TEXT
        );

        gbc.gridy = 0;

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

        // =========================================================
        // HELPER
        // =========================================================

        JLabel helper =
                UITheme.subtitle(
                        "Select one of your parking spots and define when it can be reserved."
                );

        gbc.gridy = 1;

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

        // =========================================================
        // PARKING SPOT DROPDOWN
        // =========================================================

        spotComboBox =
                new JComboBox<>();

        spotComboBox.setPreferredSize(
                new Dimension(
                        100,
                        40
                )
        );

        spotComboBox.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        15
                )
        );

        // =========================================================
        // DROPDOWN DISPLAY
        // =========================================================

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
                                (JLabel) super
                                        .getListCellRendererComponent(
                                                list,
                                                value,
                                                index,
                                                isSelected,
                                                cellHasFocus
                                        );

                        if (value instanceof ParkingSpot) {

                            ParkingSpot spot =
                                    (ParkingSpot) value;

                            String text =
                                    spot.getAddress();

                            if (
                                    spot.getArea() != null
                                            &&
                                    !spot.getArea()
                                            .trim()
                                            .isEmpty()
                            ) {

                                text +=
                                        " - "
                                                + spot.getArea();
                            }

                            label.setText(
                                    text
                            );
                        }

                        return label;
                    }
                }
        );

        loadUserParkingSpots();

        // =========================================================
        // DATE / TIME SPINNERS
        // =========================================================

        Date now =
                new Date();

        Calendar endCalendar =
                Calendar.getInstance();

        endCalendar.setTime(now);

        // default end = μία ώρα αργότερα
        endCalendar.add(
                Calendar.HOUR_OF_DAY,
                1
        );

        Date oneHourLater =
                endCalendar.getTime();

        // START DATE
        startDateSpinner =
                createDateSpinner(
                        now
                );

        // START TIME
        startTimeSpinner =
                createTimeSpinner(
                        now
                );

        // END DATE
        endDateSpinner =
                createDateSpinner(
                        oneHourLater
                );

        // END TIME
        endTimeSpinner =
                createTimeSpinner(
                        oneHourLater
                );

        // =========================================================
        // FORM
        // =========================================================

        int row = 2;

        // PARKING SPOT
        gbc.gridy = row++;

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

        gbc.gridy = row++;

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

        // =========================================================
        // START
        // =========================================================

        gbc.gridy = row++;

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

        JPanel startPanel =
                createDateTimePanel(
                        startDateSpinner,
                        startTimeSpinner
                );

        gbc.gridy = row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        18,
                        0
                );

        card.add(
                startPanel,
                gbc
        );

        // =========================================================
        // END
        // =========================================================

        gbc.gridy = row++;

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

        JPanel endPanel =
                createDateTimePanel(
                        endDateSpinner,
                        endTimeSpinner
                );

        gbc.gridy = row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        22,
                        0
                );

        card.add(
                endPanel,
                gbc
        );

        // =========================================================
        // SAVE BUTTON
        // =========================================================

        UITheme.ModernButton addButton =
                UITheme.primaryButton(
                        "Save availability"
                );

        gbc.gridy = row;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        0,
                        0
                );

        card.add(
                addButton,
                gbc
        );

        addButton.addActionListener(
                e ->
                        addAvailability(
                                dashboardPanel
                        )
        );

        center.add(
                card
        );

        add(
                center,
                BorderLayout.CENTER
        );
    }

    // =============================================================
    // DATE SPINNER
    // =============================================================

    private JSpinner createDateSpinner(
            Date initialDate
    ) {

        SpinnerDateModel model =
                new SpinnerDateModel(
                        initialDate,
                        null,
                        null,
                        Calendar.DAY_OF_MONTH
                );

        JSpinner spinner =
                new JSpinner(
                        model
                );

        JSpinner.DateEditor editor =
                new JSpinner.DateEditor(
                        spinner,
                        "dd/MM/yyyy"
                );

        spinner.setEditor(
                editor
        );

        spinner.setPreferredSize(
                new Dimension(
                        220,
                        40
                )
        );

        return spinner;
    }

    // =============================================================
    // TIME SPINNER
    // =============================================================

    private JSpinner createTimeSpinner(
            Date initialDate
    ) {

        SpinnerDateModel model =
                new SpinnerDateModel(
                        initialDate,
                        null,
                        null,
                        Calendar.MINUTE
                );

        JSpinner spinner =
                new JSpinner(
                        model
                );

        JSpinner.DateEditor editor =
                new JSpinner.DateEditor(
                        spinner,
                        "HH:mm"
                );

        spinner.setEditor(
                editor
        );

        spinner.setPreferredSize(
                new Dimension(
                        150,
                        40
                )
        );

        return spinner;
    }

    // =============================================================
    // DATE + TIME PANEL
    // =============================================================

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

        panel.setOpaque(false);

        JPanel datePanel =
                new JPanel(
                        new BorderLayout(
                                0,
                                5
                        )
                );

        datePanel.setOpaque(false);

        JLabel dateLabel =
                new JLabel(
                        "Date"
                );

        dateLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        datePanel.add(
                dateLabel,
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

        timePanel.setOpaque(false);

        JLabel timeLabel =
                new JLabel(
                        "Time"
                );

        timeLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        timePanel.add(
                timeLabel,
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

    // =============================================================
    // LOAD CURRENT USER PARKING SPOTS
    // =============================================================

    private void loadUserParkingSpots() {

        spotComboBox.removeAllItems();

        List<ParkingSpot> spots =
                parkingSpotService
                        .getSpotsByOwner(
                                currentUserId
                        );

        for (
                ParkingSpot spot : spots
        ) {

            if (
                    spot.isActive()
            ) {

                spotComboBox.addItem(
                        spot
                );
            }
        }
    }

    // =============================================================
    // CONVERT DATE + TIME TO LOCALDATETIME
    // =============================================================

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

    // =============================================================
    // ADD AVAILABILITY
    // =============================================================

    private void addAvailability(
            DashboardPanel dashboardPanel
    ) {

        ParkingSpot selectedSpot =
                (ParkingSpot)
                        spotComboBox
                                .getSelectedItem();

        if (
                selectedSpot == null
        ) {

            UITheme.showWarning(
                    this,
                    "No Parking Spot",
                    "You do not have an active parking spot available."
            );

            return;
        }

        int spotId =
                selectedSpot
                        .getSpotId();

        // =========================================================
        // CREATE LOCALDATETIME
        // =========================================================

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

        // =========================================================
        // VALIDATION
        // =========================================================

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

        // =========================================================
        // SAVE
        // =========================================================

        boolean success =
                availabilityService
                        .addAvailability(
                                currentUserId,
                                spotId,
                                startDt,
                                endDt
                        );

        if (
                success
        ) {

            UITheme.showSuccess(
                    this,
                    "Availability Saved",
                    "Availability added successfully."
            );

            resetDateTimeFields();

            if (
                    dashboardPanel != null
            ) {

                dashboardPanel
                        .loadMarkersFromDatabase(
                                parkingSpotService
                        );
            }

        } else {

            UITheme.showError(
                    this,
                    "Operation Failed",
                    "Failed to add availability. The selected period may overlap with an existing availability."
            );
        }
    }

    // =============================================================
    // RESET DATE / TIME
    // =============================================================

    private void resetDateTimeFields() {

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