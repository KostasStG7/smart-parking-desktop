package gui;

import javax.swing.*;
import java.awt.*;

import model.Availability;
import model.ParkingSpot;

import service.AvailabilityService;
import service.ParkingSpotService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MyAvailabilitiesPanel extends JPanel {

    private final int currentUserId;

    private final AvailabilityService availabilityService;
    private final ParkingSpotService parkingSpotService;

    private final JPanel availabilitiesContainer;

    private final JComboBox<String> statusFilter;
    private final JComboBox<String> sortFilter;

    private final JLabel resultCountLabel;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );


    // =============================================================
    // CONSTRUCTOR
    // =============================================================

    public MyAvailabilitiesPanel(
            int userId
    ) {

        this.currentUserId =
                userId;

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
        // NORTH
        // =========================================================

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


        // =========================================================
        // HEADER
        // =========================================================

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
                        "My Availabilities",
                        "View and manage the availability periods of your parking spots."
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


        // =========================================================
        // FILTER CARD
        // =========================================================

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


        // =========================================================
        // FILTERS
        // =========================================================

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


        // =========================================================
        // STATUS FILTER
        // =========================================================

        filters.add(
                UITheme.fieldLabel(
                        "Status"
                )
        );


        statusFilter =
                new JComboBox<>(
                        new String[]{
                                "All",
                                "Available",
                                "Expired",
                                "Cancelled"
                        }
                );

        UITheme.styleComboBox(
                statusFilter
        );

        statusFilter.setPreferredSize(
                new Dimension(
                        150,
                        40
                )
        );

        filters.add(
                statusFilter
        );


        filters.add(
                Box.createHorizontalStrut(
                        10
                )
        );


        // =========================================================
        // SORT
        // =========================================================

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

        sortFilter.setPreferredSize(
                new Dimension(
                        150,
                        40
                )
        );

        filters.add(
                sortFilter
        );


        filterCard.add(
                filters,
                BorderLayout.WEST
        );


        // =========================================================
        // COUNT
        // =========================================================

        resultCountLabel =
                new JLabel(
                        "0 periods"
                );

        resultCountLabel.setFont(
                UITheme.FONT_BOLD
        );

        resultCountLabel.setForeground(
                UITheme.PRIMARY
        );


        JPanel countPanel =
                new JPanel(
                        new GridBagLayout()
                );

        countPanel.setOpaque(
                false
        );

        countPanel.add(
                resultCountLabel
        );


        filterCard.add(
                countPanel,
                BorderLayout.EAST
        );


        north.add(
                filterCard
        );


        add(
                north,
                BorderLayout.NORTH
        );


        // =========================================================
        // CONTAINER
        // =========================================================

        availabilitiesContainer =
                new JPanel();

        availabilitiesContainer.setOpaque(
                false
        );

        availabilitiesContainer.setLayout(
                new BoxLayout(
                        availabilitiesContainer,
                        BoxLayout.Y_AXIS
                )
        );


        // =========================================================
        // SCROLL
        // =========================================================

        JScrollPane scrollPane =
                new JScrollPane(
                        availabilitiesContainer
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


        // =========================================================
        // ACTIONS
        // =========================================================

        refreshButton.addActionListener(
                e -> loadAvailabilities()
        );

        statusFilter.addActionListener(
                e -> loadAvailabilities()
        );

        sortFilter.addActionListener(
                e -> loadAvailabilities()
        );


        loadAvailabilities();
    }


    // =============================================================
    // LOAD
    // =============================================================

    public void loadAvailabilities() {

        availabilitiesContainer
                .removeAll();


        List<Availability> allAvailabilities =
                availabilityService
                        .getAvailabilitiesByOwner(
                                currentUserId
                        );


        List<Availability> availabilities =
                new ArrayList<>(
                        allAvailabilities
                );


        // =========================================================
        // FILTER
        // =========================================================

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

            availabilities =
                    availabilities
                            .stream()
                            .filter(
                                    availability ->
                                            matchesFilter(
                                                    availability,
                                                    selectedStatus
                                            )
                            )
                            .collect(
                                    Collectors.toList()
                            );
        }


        // =========================================================
        // SORT
        // =========================================================

        String selectedSort =
                (String)
                        sortFilter
                                .getSelectedItem();


        Comparator<Availability> comparator =
                Comparator.comparing(
                        Availability::getStartDatetime
                );


        if (
                "Newest first".equals(
                        selectedSort
                )
        ) {

            comparator =
                    comparator.reversed();
        }


        availabilities.sort(
                comparator
        );


        // =========================================================
        // COUNT
        // =========================================================

        resultCountLabel.setText(
                availabilities.size()
                        +
                (
                        availabilities.size() == 1
                                ? " period"
                                : " periods"
                )
        );


        // =========================================================
        // DISPLAY
        // =========================================================

        if (
                availabilities.isEmpty()
        ) {

            addEmptyState(
                    selectedStatus
            );

        } else {

            for (
                    Availability availability :
                    availabilities
            ) {

                availabilitiesContainer.add(
                        createAvailabilityCard(
                                availability
                        )
                );

                availabilitiesContainer.add(
                        Box.createVerticalStrut(
                                14
                        )
                );
            }
        }


        availabilitiesContainer
                .revalidate();

        availabilitiesContainer
                .repaint();
    }


    // =============================================================
    // FILTER MATCH
    // =============================================================

    private boolean matchesFilter(
            Availability availability,
            String selectedStatus
    ) {

        String status =
                getDisplayedStatus(
                        availability
                );


        switch (
                selectedStatus
        ) {

            case "Available":

                return "AVAILABLE"
                        .equals(
                                status
                        );


            case "Expired":

                return "EXPIRED"
                        .equals(
                                status
                        );


            case "Cancelled":

                return "CANCELLED"
                        .equals(
                                status
                        );


            default:

                return true;
        }
    }


    // =============================================================
    // CARD
    // =============================================================

    private JPanel createAvailabilityCard(
            Availability availability
    ) {

        ParkingSpot spot =
                parkingSpotService
                        .getSpotById(
                                availability.getSpotId()
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
                        175
                )
        );

        card.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        // =========================================================
        // DETAILS
        // =========================================================

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


        String address =
                spot != null
                        ? safeText(
                                spot.getAddress()
                        )
                        : "Parking Spot";


        JLabel addressLabel =
                new JLabel(
                        address
                );

        addressLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        addressLabel.setForeground(
                UITheme.TEXT
        );

        addressLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        details.add(
                addressLabel
        );

        details.add(
                Box.createVerticalStrut(
                        5
                )
        );


        if (
                spot != null
        ) {

            JLabel spotInfo =
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

            spotInfo.setFont(
                    UITheme.SMALL
            );

            spotInfo.setForeground(
                    UITheme.MUTED
            );

            spotInfo.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            details.add(
                    spotInfo
            );

            details.add(
                    Box.createVerticalStrut(
                            12
                    )
            );
        }


        String period =
                availability
                        .getStartDatetime()
                        .format(
                                formatter
                        )
                        +
                "  →  "
                        +
                availability
                        .getEndDatetime()
                        .format(
                                formatter
                        );


        JLabel periodLabel =
                new JLabel(
                        period
                );

        periodLabel.setFont(
                UITheme.FONT
        );

        periodLabel.setForeground(
                UITheme.TEXT
        );

        periodLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        details.add(
                periodLabel
        );

        details.add(
                Box.createVerticalStrut(
                        10
                )
        );


        // =========================================================
        // STATUS
        // =========================================================

        details.add(
                UITheme.statusBadge(
                        getDisplayedStatus(
                                availability
                        )
                )
        );


        // =========================================================
        // ACTION
        // =========================================================

        JPanel actions =
                new JPanel(
                        new GridBagLayout()
                );

        actions.setOpaque(
                false
        );


        boolean canCancel =
                "AVAILABLE"
                        .equalsIgnoreCase(
                                availability.getStatus()
                        )
                        &&
                availability
                        .getEndDatetime()
                        .isAfter(
                                LocalDateTime.now()
                        );


        if (
                canCancel
        ) {

            UITheme.ModernButton cancelButton =
                    UITheme.secondaryButton(
                            "Cancel availability"
                    );

            cancelButton.addActionListener(
                    e ->
                            cancelAvailability(
                                    availability
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


    // =============================================================
    // CANCEL
    // =============================================================

    private void cancelAvailability(
            Availability availability
    ) {

        boolean confirmed =
                UITheme.showConfirm(
                        this,
                        "Cancel Availability",
                        "Are you sure you want to cancel this availability period?",
                        "Cancel availability"
                );


        if (
                !confirmed
        ) {

            return;
        }


        boolean success =
                availabilityService
                        .cancelAvailability(
                                currentUserId,
                                availability
                                        .getAvailabilityId()
                        );


        if (
                success
        ) {

            UITheme.showSuccess(
                    this,
                    "Availability Cancelled",
                    "The availability period was cancelled successfully."
            );

            loadAvailabilities();

        } else {

            String error =
                    availabilityService
                            .getLastErrorMessage();


            if (
                    error == null
                            ||
                    error.isBlank()
            ) {

                error =
                        "Failed to cancel the availability period.";
            }


            UITheme.showError(
                    this,
                    "Cancellation Failed",
                    error
            );
        }
    }


    // =============================================================
    // DISPLAY STATUS
    // =============================================================

    private String getDisplayedStatus(
            Availability availability
    ) {

        if (
                "CANCELLED"
                        .equalsIgnoreCase(
                                availability.getStatus()
                        )
        ) {

            return "CANCELLED";
        }


        if (
                availability
                        .getEndDatetime()
                        .isBefore(
                                LocalDateTime.now()
                        )
        ) {

            return "EXPIRED";
        }


        return "AVAILABLE";
    }


    // =============================================================
    // EMPTY
    // =============================================================

    private void addEmptyState(
            String selectedStatus
    ) {

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


        JPanel content =
                new JPanel();

        content.setOpaque(
                false
        );

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS
                )
        );


        String titleText;
        String subtitleText;


        if (
                selectedStatus == null
                        ||
                "All".equals(
                        selectedStatus
                )
        ) {

            titleText =
                    "No availability periods yet";

            subtitleText =
                    "Availability periods you create will appear here.";

        } else {

            titleText =
                    "No "
                            +
                    selectedStatus.toLowerCase()
                            +
                    " periods";

            subtitleText =
                    "No availability periods match the selected filter.";
        }


        JLabel title =
                new JLabel(
                        titleText
                );

        title.setFont(
                UITheme.SECTION
        );

        title.setForeground(
                UITheme.TEXT
        );

        title.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );


        JLabel subtitle =
                new JLabel(
                        subtitleText
                );

        subtitle.setFont(
                UITheme.SMALL
        );

        subtitle.setForeground(
                UITheme.MUTED
        );

        subtitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );


        content.add(
                title
        );

        content.add(
                Box.createVerticalStrut(
                        7
                )
        );

        content.add(
                subtitle
        );


        card.add(
                content
        );

        availabilitiesContainer.add(
                card
        );
    }


    // =============================================================
    // SAFE TEXT
    // =============================================================

    private String safeText(
            String value
    ) {

        if (
                value == null
                        ||
                value.trim().isEmpty()
        ) {

            return "-";
        }

        return value;
    }
}