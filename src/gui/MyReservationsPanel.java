package gui;

import javax.swing.*;
import java.awt.*;

import model.ParkingSpot;
import model.Reservation;

import service.ParkingSpotService;
import service.PricingService;
import service.ReservationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MyReservationsPanel extends JPanel {

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

    public MyReservationsPanel(
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
                        "My Reservations",
                        "View, pay and manage your parking reservations."
                ),
                BorderLayout.WEST
        );

        UITheme.ModernButton refreshButton =
                UITheme.secondaryButton(
                        "Refresh"
                );

        JPanel refreshPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                0,
                                4
                        )
                );

        refreshPanel.setOpaque(
                false
        );

        refreshPanel.add(
                refreshButton
        );

        header.add(
                refreshPanel,
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
                                "Active",
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

        statusFilter.setPreferredSize(
                new Dimension(
                        170,
                        40
                )
        );

        statusFilter.setSelectedItem(
                "Active"
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
                                .getReservationsByDriver(
                                        currentUserId
                                )
                );

        String selectedStatus =
                (String)
                        statusFilter
                                .getSelectedItem();

        if (
                selectedStatus != null
                        &&
                !"All".equals(
                        selectedStatus
                )
        ) {

            reservations =
                    reservations
                            .stream()
                            .filter(
                                    reservation ->
                                            matchesFilter(
                                                    reservation,
                                                    selectedStatus
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

            case "Active":

                return "PENDING PAYMENT".equals(
                        status
                )
                        ||
                "CONFIRMED".equals(
                        status
                )
                        ||
                "IN PROGRESS".equals(
                        status
                );

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
                        20,
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
                        245
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

        address.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        details.add(
                address
        );

        details.add(
                Box.createVerticalStrut(
                        5
                )
        );

        if (
                spot != null
        ) {

            JLabel spotDetails =
                    new JLabel(
                            safeText(
                                    spot.getArea()
                            )
                                    +
                            " • "
                                    +
                            safeText(
                                    spot.getVehicleType()
                            )
                    );

            spotDetails.setFont(
                    UITheme.SMALL
            );

            spotDetails.setForeground(
                    UITheme.MUTED
            );

            spotDetails.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            details.add(
                    spotDetails
            );
        }

        details.add(
                Box.createVerticalStrut(
                        10
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

        period.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        details.add(
                period
        );

        details.add(
                Box.createVerticalStrut(
                        8
                )
        );

        JLabel price =
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

        price.setFont(
                UITheme.FONT_BOLD
        );

        price.setForeground(
                UITheme.TEXT
        );

        price.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        details.add(
                price
        );

        details.add(
                Box.createVerticalStrut(
                        5
                )
        );

        JLabel payment =
                new JLabel(
                        "Payment: "
                                +
                        reservation.getPaymentStatus()
                );

        payment.setFont(
                UITheme.SMALL
        );

        if (
                "PAID".equalsIgnoreCase(
                        reservation.getPaymentStatus()
                )
        ) {

            payment.setForeground(
                    UITheme.SUCCESS
            );

        } else if (
                "REFUNDED".equalsIgnoreCase(
                        reservation.getPaymentStatus()
                )
        ) {

            payment.setForeground(
                    UITheme.PRIMARY
            );

        } else if (
                "EXPIRED".equalsIgnoreCase(
                        reservation.getPaymentStatus()
                )
        ) {

            payment.setForeground(
                    UITheme.DANGER
            );

        } else {

            payment.setForeground(
                    UITheme.MUTED
            );
        }

        payment.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        details.add(
                payment
        );

        if (
                reservation.getPaymentDeadline() != null
                        &&
                "PENDING_PAYMENT".equalsIgnoreCase(
                        reservation.getStatus()
                )
                        &&
                "UNPAID".equalsIgnoreCase(
                        reservation.getPaymentStatus()
                )
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

            deadline.setAlignmentX(
                    Component.LEFT_ALIGNMENT
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

        JPanel actions =
                new JPanel();

        actions.setOpaque(
                false
        );

        actions.setLayout(
                new BoxLayout(
                        actions,
                        BoxLayout.Y_AXIS
                )
        );

        boolean awaitingPayment =
                "PENDING_PAYMENT".equalsIgnoreCase(
                        reservation.getStatus()
                )
                        &&
                "UNPAID".equalsIgnoreCase(
                        reservation.getPaymentStatus()
                );

        if (
                awaitingPayment
        ) {

            UITheme.ModernButton payButton =
                    UITheme.primaryButton(
                            "Pay now"
                    );

            payButton.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            payButton.addActionListener(
                    e ->
                            payReservation(
                                    reservation
                            )
            );

            actions.add(
                    payButton
            );

            actions.add(
                    Box.createVerticalStrut(
                            10
                    )
            );
        }

        boolean canCancel =
                (
                        "CONFIRMED".equalsIgnoreCase(
                                reservation.getStatus()
                        )
                                ||
                        "PENDING_PAYMENT".equalsIgnoreCase(
                                reservation.getStatus()
                        )
                )
                        &&
                reservation
                        .getStartDatetime()
                        .isAfter(
                                LocalDateTime.now()
                        );

        if (
                canCancel
        ) {

            UITheme.ModernButton cancelButton =
                    UITheme.secondaryButton(
                            "Cancel reservation"
                    );

            cancelButton.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            cancelButton.addActionListener(
                    e ->
                            cancelReservation(
                                    reservation
                            )
            );

            actions.add(
                    cancelButton
            );
        }

        card.add(
                details,
                BorderLayout.CENTER
        );

        card.add(
                actions,
                BorderLayout.EAST
        );

        return card;
    }

    private void payReservation(
            Reservation reservation
    ) {

        Double amount =
                PaymentDialog.showPayment(
                        this,
                        reservation.getCalculatedPrice()
                );

        if (
                amount == null
        ) {

            return;
        }

        boolean success =
                reservationService
                        .payReservation(
                                currentUserId,
                                reservation.getReservationId(),
                                amount
                        );

        if (
                success
        ) {

            UITheme.showSuccess(
                    this,
                    "Payment Completed",
                    "Payment completed successfully. Your reservation is now confirmed."
            );

            loadReservations();

        } else {

            UITheme.showError(
                    this,
                    "Payment Failed",
                    reservationService
                            .getLastErrorMessage()
            );

            loadReservations();
        }
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

    private void cancelReservation(
            Reservation reservation
    ) {

        String message;

        if (
                "PAID".equalsIgnoreCase(
                        reservation.getPaymentStatus()
                )
        ) {

            LocalDateTime refundDeadline =
                    reservation
                            .getStartDatetime()
                            .minusHours(
                                    1
                            );

            if (
                    !LocalDateTime
                            .now()
                            .isAfter(
                                    refundDeadline
                            )
            ) {

                message =
                        "This reservation has already been paid. "
                                +
                        "Because you are cancelling at least 1 hour before the start time, "
                                +
                        "the full payment will be refunded.";

            } else {

                message =
                        "This reservation has already been paid. "
                                +
                        "Because less than 1 hour remains before the start time, "
                                +
                        "the payment will not be refunded.";
            }

        } else {

            message =
                    "Are you sure you want to cancel this reservation?";
        }

        boolean confirmed =
                UITheme.showConfirm(
                        this,
                        "Cancel Reservation",
                        message,
                        "Cancel reservation"
                );

        if (
                !confirmed
        ) {

            return;
        }

        boolean success =
                reservationService
                        .cancelReservation(
                                currentUserId,
                                reservation.getReservationId()
                        );

        if (
                success
        ) {

            if (
                    reservationService
                            .wasLastCancellationRefunded()
            ) {

                UITheme.showSuccess(
                        this,
                        "Reservation Cancelled",
                        "Reservation cancelled successfully. "
                                +
                        pricingService.formatPrice(
                                reservationService
                                        .getLastRefundAmount()
                        )
                                +
                        " has been refunded."
                );

            } else if (
                    reservationService
                            .wasLastCancellationPaidWithoutRefund()
            ) {

                UITheme.showWarning(
                        this,
                        "Reservation Cancelled",
                        "The reservation was cancelled, but no refund was issued because it was cancelled less than 1 hour before the start time."
                );

            } else {

                UITheme.showSuccess(
                        this,
                        "Reservation Cancelled",
                        "Reservation cancelled successfully."
                );
            }

            loadReservations();

        } else {

            UITheme.showError(
                    this,
                    "Cancellation Failed",
                    reservationService
                            .getLastErrorMessage()
            );
        }
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