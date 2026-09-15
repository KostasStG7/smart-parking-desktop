package gui;

import javax.swing.*;
import java.awt.*;

import model.ParkingSpot;
import model.Reservation;
import model.User;

import service.ParkingSpotService;
import service.PricingService;
import service.ReservationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationsOnMySpotsPanel extends JPanel {

    private final int currentUserId;

    private final ReservationService reservationService;
    private final ParkingSpotService parkingSpotService;
    private final PricingService pricingService;

    private final JPanel reservationsContainer;

    private final JComboBox<String> statusFilter;
    private final JComboBox<String> sortFilter;

    private final JLabel resultCountLabel;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    public ReservationsOnMySpotsPanel(
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

        JPanel north =
                new JPanel();

        north.setOpaque(
                false
        );

        north.setLayout(
                new BoxLayout(
                        north,
                        BoxLayout.Y_AXIS
                )
        );

        JPanel header =
                new JPanel(
                        new BorderLayout(
                                20,
                                0
                        )
                );

        header.setOpaque(
                false
        );

        header.add(
                UITheme.pageHeader(
                        "Reservations on My Spots",
                        "See reservations, prices and payment status for your parking spots."
                ),
                BorderLayout.WEST
        );

        UITheme.ModernButton refreshButton =
                UITheme.secondaryButton(
                        "Refresh"
                );

        header.add(
                refreshButton,
                BorderLayout.EAST
        );

        north.add(
                header
        );

        north.add(
                Box.createVerticalStrut(
                        18
                )
        );

        UITheme.RoundedPanel filterCard =
                new UITheme.RoundedPanel(
                        16,
                        Color.WHITE
                );

        filterCard.setOutlineColor(
                UITheme.BORDER
        );

        filterCard.setLayout(
                new BorderLayout(
                        20,
                        0
                )
        );

        filterCard.setBorder(
                BorderFactory.createEmptyBorder(
                        14,
                        18,
                        14,
                        18
                )
        );

        JPanel filters =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        filters.setOpaque(
                false
        );

        filters.add(
                UITheme.fieldLabel(
                        "Status"
                )
        );

        statusFilter =
                new JComboBox<>(
                        new String[]{
                                "All",
                                "Awaiting Payment",
                                "Upcoming",
                                "In Progress",
                                "Completed",
                                "Cancelled"
                        }
                );

        UITheme.styleComboBox(
                statusFilter
        );

        filters.add(
                statusFilter
        );

        filters.add(
                Box.createHorizontalStrut(
                        10
                )
        );

        filters.add(
                UITheme.fieldLabel(
                        "Sort"
                )
        );

        sortFilter =
                new JComboBox<>(
                        new String[]{
                                "Newest first",
                                "Oldest first"
                        }
                );

        UITheme.styleComboBox(
                sortFilter
        );

        filters.add(
                sortFilter
        );

        filterCard.add(
                filters,
                BorderLayout.WEST
        );

        resultCountLabel =
                new JLabel(
                        "0 reservations"
                );

        resultCountLabel.setFont(
                UITheme.FONT_BOLD
        );

        resultCountLabel.setForeground(
                UITheme.PRIMARY
        );

        filterCard.add(
                resultCountLabel,
                BorderLayout.EAST
        );

        north.add(
                filterCard
        );

        add(
                north,
                BorderLayout.NORTH
        );

        reservationsContainer =
                new JPanel();

        reservationsContainer.setOpaque(
                false
        );

        reservationsContainer.setLayout(
                new BoxLayout(
                        reservationsContainer,
                        BoxLayout.Y_AXIS
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        reservationsContainer
                );

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        0,
                        0,
                        0
                )
        );

        scrollPane.setOpaque(
                false
        );

        scrollPane
                .getViewport()
                .setOpaque(
                        false
                );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane
                .getVerticalScrollBar()
                .setUnitIncrement(
                        16
                );

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        refreshButton.addActionListener(
                e -> loadReservations()
        );

        statusFilter.addActionListener(
                e -> loadReservations()
        );

        sortFilter.addActionListener(
                e -> loadReservations()
        );

        loadReservations();
    }

    public void loadReservations() {

        reservationsContainer.removeAll();

        List<Reservation> reservations =
                new ArrayList<>(
                        reservationService
                                .getReservationsForOwner(
                                        currentUserId
                                )
                );

        String filter =
                (String)
                        statusFilter
                                .getSelectedItem();

        if (
                filter != null
                        &&
                !"All".equals(
                        filter
                )
        ) {

            reservations =
                    reservations
                            .stream()
                            .filter(
                                    reservation ->
                                            matchesFilter(
                                                    reservation,
                                                    filter
                                            )
                            )
                            .collect(
                                    Collectors.toList()
                            );
        }

        Comparator<Reservation> comparator =
                Comparator.comparing(
                        Reservation::getStartDatetime
                );

        if (
                "Newest first".equals(
                        sortFilter.getSelectedItem()
                )
        ) {

            comparator =
                    comparator.reversed();
        }

        reservations.sort(
                comparator
        );

        resultCountLabel.setText(
                reservations.size()
                        +
                (
                        reservations.size() == 1
                                ? " reservation"
                                : " reservations"
                )
        );

        if (
                reservations.isEmpty()
        ) {

            addEmptyState();

        } else {

            for (
                    Reservation reservation :
                    reservations
            ) {

                reservationsContainer.add(
                        createReservationCard(
                                reservation
                        )
                );

                reservationsContainer.add(
                        Box.createVerticalStrut(
                                14
                        )
                );
            }
        }

        reservationsContainer.revalidate();
        reservationsContainer.repaint();
    }

    private boolean matchesFilter(
            Reservation reservation,
            String filter
    ) {

        String status =
                getDisplayedStatus(
                        reservation
                );

        switch (
                filter
        ) {

            case "Awaiting Payment":
                return "PENDING PAYMENT".equals(
                        status
                );

            case "Upcoming":
                return "CONFIRMED".equals(
                        status
                );

            case "In Progress":
                return "IN PROGRESS".equals(
                        status
                );

            case "Completed":
                return "COMPLETED".equals(
                        status
                );

            case "Cancelled":
                return "CANCELLED".equals(
                        status
                );

            default:
                return true;
        }
    }

    private JPanel createReservationCard(
            Reservation reservation
    ) {

        ParkingSpot spot =
                parkingSpotService
                        .getSpotById(
                                reservation.getSpotId()
                        );

        User driver =
                reservationService
                        .getDriverById(
                                reservation.getDriverId()
                        );

        UITheme.RoundedPanel card =
                new UITheme.RoundedPanel(
                        16,
                        Color.WHITE
                );

        card.setOutlineColor(
                UITheme.BORDER
        );

        card.setLayout(
                new BorderLayout(
                        24,
                        0
                )
        );

        card.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        22,
                        20,
                        22
                )
        );

        card.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        240
                )
        );

        card.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JPanel details =
                new JPanel();

        details.setOpaque(
                false
        );

        details.setLayout(
                new BoxLayout(
                        details,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel address =
                new JLabel(
                        spot == null
                                ? "Parking Spot"
                                : safeText(
                                        spot.getAddress()
                                )
                );

        address.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        address.setForeground(
                UITheme.TEXT
        );

        details.add(
                address
        );

        details.add(
                Box.createVerticalStrut(
                        8
                )
        );

        JLabel period =
                new JLabel(
                        reservation
                                .getStartDatetime()
                                .format(
                                        formatter
                                )
                                +
                        "  →  "
                                +
                        reservation
                                .getEndDatetime()
                                .format(
                                        formatter
                                )
                );

        period.setFont(
                UITheme.FONT
        );

        period.setForeground(
                UITheme.TEXT
        );

        details.add(
                period
        );

        details.add(
                Box.createVerticalStrut(
                        7
                )
        );

        JLabel total =
                new JLabel(
                        "Total: "
                                +
                        (
                                reservation.getCalculatedPrice() > 0
                                        ? pricingService
                                                .formatPrice(
                                                        reservation.getCalculatedPrice()
                                                )
                                        : "Free"
                        )
                );

        total.setFont(
                UITheme.FONT_BOLD
        );

        total.setForeground(
                UITheme.TEXT
        );

        details.add(
                total
        );

        JLabel payment =
                new JLabel(
                        "Payment: "
                                +
                        reservation.getPaymentStatus()
                );

        payment.setFont(
                UITheme.FONT_BOLD
        );

        payment.setForeground(
                "PAID".equalsIgnoreCase(
                        reservation.getPaymentStatus()
                )
                        ? UITheme.SUCCESS
                        : (
                                "UNPAID".equalsIgnoreCase(
                                        reservation.getPaymentStatus()
                                )
                                        ? UITheme.WARNING
                                        : UITheme.MUTED
                        )
        );

        details.add(
                payment
        );

        JLabel bookingType =
                new JLabel(
                        "Booking type: "
                                +
                        reservation.getBookingType()
                );

        bookingType.setFont(
                UITheme.SMALL
        );

        bookingType.setForeground(
                UITheme.MUTED
        );

        details.add(
                bookingType
        );

        if (
                reservation.getPaymentDeadline() != null
        ) {

            JLabel deadline =
                    new JLabel(
                            "Payment deadline: "
                                    +
                            reservation
                                    .getPaymentDeadline()
                                    .format(
                                            formatter
                                    )
                    );

            deadline.setFont(
                    UITheme.SMALL
            );

            deadline.setForeground(
                    UITheme.WARNING
            );

            details.add(
                    deadline
            );
        }

        details.add(
                Box.createVerticalStrut(
                        8
                )
        );

        details.add(
                UITheme.statusBadge(
                        getDisplayedStatus(
                                reservation
                        )
                )
        );

        JPanel driverPanel =
                new JPanel();

        driverPanel.setOpaque(
                false
        );

        driverPanel.setLayout(
                new BoxLayout(
                        driverPanel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel driverTitle =
                new JLabel(
                        "Driver"
                );

        driverTitle.setFont(
                UITheme.FONT_BOLD
        );

        driverTitle.setForeground(
                UITheme.MUTED
        );

        driverPanel.add(
                driverTitle
        );

        driverPanel.add(
                Box.createVerticalStrut(
                        8
                )
        );

        if (
                driver != null
        ) {

            JLabel name =
                    new JLabel(
                            safeText(
                                    driver.getFullName()
                            )
                    );

            name.setFont(
                    UITheme.FONT_BOLD
            );

            name.setForeground(
                    UITheme.TEXT
            );

            driverPanel.add(
                    name
            );

            driverPanel.add(
                    Box.createVerticalStrut(
                            5
                    )
            );

            JLabel email =
                    new JLabel(
                            safeText(
                                    driver.getEmail()
                            )
                    );

            email.setFont(
                    UITheme.SMALL
            );

            email.setForeground(
                    UITheme.MUTED
            );

            driverPanel.add(
                    email
            );

            if (
                    driver.getPhone() != null
                            &&
                    !driver
                            .getPhone()
                            .trim()
                            .isEmpty()
            ) {

                driverPanel.add(
                        Box.createVerticalStrut(
                                4
                        )
                );

                JLabel phone =
                        new JLabel(
                                driver.getPhone()
                        );

                phone.setFont(
                        UITheme.SMALL
                );

                phone.setForeground(
                        UITheme.MUTED
                );

                driverPanel.add(
                        phone
                );
            }
        }

        card.add(
                details,
                BorderLayout.CENTER
        );

        card.add(
                driverPanel,
                BorderLayout.EAST
        );

        return card;
    }

    private String getDisplayedStatus(
            Reservation reservation
    ) {

        if (
                "CANCELLED".equalsIgnoreCase(
                        reservation.getStatus()
                )
        ) {

            return "CANCELLED";
        }

        if (
                "PENDING_PAYMENT".equalsIgnoreCase(
                        reservation.getStatus()
                )
        ) {

            return "PENDING PAYMENT";
        }

        LocalDateTime now =
                LocalDateTime.now();

        if (
                reservation
                        .getEndDatetime()
                        .isBefore(
                                now
                        )
        ) {

            return "COMPLETED";
        }

        if (
                !reservation
                        .getStartDatetime()
                        .isAfter(
                                now
                        )
                        &&
                reservation
                        .getEndDatetime()
                        .isAfter(
                                now
                        )
        ) {

            return "IN PROGRESS";
        }

        return "CONFIRMED";
    }

    private void addEmptyState() {

        UITheme.RoundedPanel card =
                new UITheme.RoundedPanel(
                        16,
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
                        35,
                        25,
                        35,
                        25
                )
        );

        card.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        130
                )
        );

        card.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel label =
                new JLabel(
                        "No reservations match the selected filter."
                );

        label.setFont(
                UITheme.FONT
        );

        label.setForeground(
                UITheme.MUTED
        );

        card.add(
                label
        );

        reservationsContainer.add(
                card
        );
    }

    private String safeText(
            String value
    ) {

        return value == null
                ||
                value.trim().isEmpty()
                ? "-"
                : value;
    }
}