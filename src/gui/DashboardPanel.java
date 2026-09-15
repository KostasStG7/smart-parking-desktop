package gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.stream.Collectors;

import service.ParkingSpotService;
import service.AvailabilityService;
import service.ReservationService;
import service.NotificationService;
import service.PricingService;

import model.ParkingSpot;
import model.Availability;
import model.Reservation;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.Coordinate;

class SpotMarker extends MapMarkerDot {

    private final int spotId;
    private final int ownerId;
    private final String vehicleType;

    public SpotMarker(
            String name,
            Coordinate coord,
            int spotId,
            int ownerId,
            String vehicleType
    ) {

        super(
                name,
                coord
        );

        this.spotId =
                spotId;

        this.ownerId =
                ownerId;

        this.vehicleType =
                vehicleType;

        setBackColor(
                ownerId == DashboardPanel.currentUserIdGlobal
                        ? UITheme.SUCCESS
                        : UITheme.PRIMARY
        );

        setName(
                name
        );
    }

    public int getSpotId() {
        return spotId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getVehicleType() {
        return vehicleType;
    }
}

public class DashboardPanel extends JPanel {

    public static int currentUserIdGlobal;

    private final int currentUserId;

    private final ReservationPanel reservationPanel;

    private final Runnable loginRequiredAction;

    private final JMapViewer map;

    private List<SpotMarker> currentMarkers;

    private final ParkingSpotService parkingSpotService;
    private final AvailabilityService availabilityService;
    private final ReservationService reservationService;
    private final NotificationService notificationService;
    private final PricingService pricingService;

    private JComboBox<String> vehicleFilter;

    private JLabel visibleSpotsLabel;

    private JLabel totalSpotsValue;
    private JLabel mySpotsValue;
    private JLabel upcomingReservationsValue;
    private JLabel unreadNotificationsValue;
    private JLabel guestVisibleSpotsValue;

    private final DateTimeFormatter displayFormatter =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    public DashboardPanel(
            int userId,
            ReservationPanel reservationPanel
    ) {

        this(
                userId,
                reservationPanel,
                null
        );
    }

    public DashboardPanel(
            int userId,
            ReservationPanel reservationPanel,
            Runnable loginRequiredAction
    ) {

        this.currentUserId =
                userId;

        currentUserIdGlobal =
                userId;

        this.reservationPanel =
                reservationPanel;

        this.loginRequiredAction =
                loginRequiredAction;

        this.parkingSpotService =
                new ParkingSpotService();

        this.availabilityService =
                new AvailabilityService();

        this.reservationService =
                new ReservationService();

        this.notificationService =
                new NotificationService();

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

        JPanel top =
                new JPanel(
                        new BorderLayout(
                                20,
                                0
                        )
                );

        top.setOpaque(
                false
        );

        top.add(
                UITheme.pageHeader(
                        "Parking Dashboard",

                        currentUserId > 0
                                ? "Overview of parking spots, reservations and account activity."
                                : "Explore active parking spots and discover available parking locations."
                ),
                BorderLayout.WEST
        );

        JPanel controls =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                4
                        )
                );

        controls.setOpaque(
                false
        );

        visibleSpotsLabel =
                new JLabel(
                        "0 spots shown"
                );

        visibleSpotsLabel.setFont(
                UITheme.SMALL
        );

        visibleSpotsLabel.setForeground(
                UITheme.MUTED
        );

        controls.add(
                visibleSpotsLabel
        );

        controls.add(
                Box.createHorizontalStrut(
                        4
                )
        );

        JLabel filterLabel =
                UITheme.fieldLabel(
                        "Vehicle"
                );

        controls.add(
                filterLabel
        );

        vehicleFilter =
                new JComboBox<>(
                        new String[]{
                                "All",
                                "Car",
                                "Motorbike"
                        }
                );

        UITheme.styleComboBox(
                vehicleFilter
        );

        controls.add(
                vehicleFilter
        );

        UITheme.ModernButton rulesButton =
                UITheme.secondaryButton(
                        "Pricing & Booking Rules"
                );

        controls.add(
                rulesButton
        );

        UITheme.ModernButton refreshButton =
                UITheme.secondaryButton(
                        "Refresh"
                );

        controls.add(
                refreshButton
        );

        top.add(
                controls,
                BorderLayout.EAST
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

        north.add(
                top
        );

        north.add(
                Box.createVerticalStrut(
                        20
                )
        );

        north.add(
                buildStatsRow()
        );

        north.add(
                Box.createVerticalStrut(
                        18
                )
        );

        add(
                north,
                BorderLayout.NORTH
        );

        UITheme.RoundedPanel mapCard =
                new UITheme.RoundedPanel(
                        18,
                        Color.WHITE
                );

        mapCard.setOutlineColor(
                UITheme.BORDER
        );

        mapCard.setLayout(
                new BorderLayout()
        );

        mapCard.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        8,
                        8,
                        8
                )
        );

        map =
                new JMapViewer();

        Coordinate athens =
                new Coordinate(
                        37.9838,
                        23.7275
                );

        map.setDisplayPosition(
                athens,
                12
        );

        mapCard.add(
                map,
                BorderLayout.CENTER
        );

        add(
                mapCard,
                BorderLayout.CENTER
        );

        vehicleFilter.addActionListener(
                e ->
                        filterByVehicle(
                                (String)
                                        vehicleFilter
                                                .getSelectedItem()
                        )
        );

        refreshButton.addActionListener(
                e -> {

                    vehicleFilter.setSelectedItem(
                            "All"
                    );

                    loadMarkersFromDatabase(
                            parkingSpotService
                    );
                }
        );

        rulesButton.addActionListener(
                e -> showPricingRulesDialog()
        );

        map.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e
                    ) {

                        if (
                                !SwingUtilities
                                        .isLeftMouseButton(
                                                e
                                        )
                        ) {

                            return;
                        }

                        ICoordinate clickedCoord =
                                map.getPosition(
                                        e.getX(),
                                        e.getY()
                                );

                        if (
                                clickedCoord == null
                        ) {

                            return;
                        }

                        SpotMarker nearest =
                                findNearestMarker(
                                        clickedCoord
                                );

                        if (
                                nearest != null
                        ) {

                            showMarkerInfo(
                                    nearest
                            );
                        }
                    }
                }
        );

        map.addMouseMotionListener(
                new MouseMotionAdapter() {

                    @Override
                    public void mouseMoved(
                            MouseEvent e
                    ) {

                        ICoordinate coord =
                                map.getPosition(
                                        e.getX(),
                                        e.getY()
                                );

                        if (
                                coord == null
                        ) {

                            map.setToolTipText(
                                    null
                            );

                            return;
                        }

                        SpotMarker nearest =
                                findNearestMarker(
                                        coord
                                );

                        if (
                                nearest != null
                        ) {

                            map.setToolTipText(
                                    nearest.getName()
                                            +
                                    " • "
                                            +
                                    nearest.getVehicleType()
                                            +
                                    " • Click for details"
                            );

                        } else {

                            map.setToolTipText(
                                    null
                            );
                        }
                    }
                }
        );
    }

    private JPanel buildStatsRow() {

        if (
                currentUserId <= 0
        ) {

            JPanel row =
                    new JPanel(
                            new GridLayout(
                                    1,
                                    2,
                                    14,
                                    0
                            )
                    );

            row.setOpaque(
                    false
            );

            totalSpotsValue =
                    new JLabel(
                            "0"
                    );

            guestVisibleSpotsValue =
                    new JLabel(
                            "0"
                    );

            row.add(
                    statCard(
                            "Active spots",
                            totalSpotsValue,
                            "All active parking spots"
                    )
            );

            row.add(
                    statCard(
                            "Visible on map",
                            guestVisibleSpotsValue,
                            "After the current vehicle filter"
                    )
            );

            return row;
        }

        JPanel row =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                14,
                                0
                        )
                );

        row.setOpaque(
                false
        );

        totalSpotsValue =
                new JLabel(
                        "0"
                );

        mySpotsValue =
                new JLabel(
                        "0"
                );

        upcomingReservationsValue =
                new JLabel(
                        "0"
                );

        unreadNotificationsValue =
                new JLabel(
                        "0"
                );

        row.add(
                statCard(
                        "Active spots",
                        totalSpotsValue,
                        "Available in the system"
                )
        );

        row.add(
                statCard(
                        "My active spots",
                        mySpotsValue,
                        "Parking spots you currently offer"
                )
        );

        row.add(
                statCard(
                        "Upcoming reservations",
                        upcomingReservationsValue,
                        "Future confirmed bookings"
                )
        );

        row.add(
                statCard(
                        "Unread notifications",
                        unreadNotificationsValue,
                        "Updates waiting for you"
                )
        );

        return row;
    }

    private JPanel statCard(
            String title,
            JLabel valueLabel,
            String caption
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
                new BorderLayout()
        );

        card.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        18,
                        15,
                        18
                )
        );

        JLabel titleLabel =
                new JLabel(
                        title
                );

        titleLabel.setFont(
                UITheme.FONT_BOLD
        );

        titleLabel.setForeground(
                UITheme.MUTED
        );

        valueLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        26
                )
        );

        valueLabel.setForeground(
                UITheme.TEXT
        );

        JLabel captionLabel =
                new JLabel(
                        caption
                );

        captionLabel.setFont(
                UITheme.SMALL
        );

        captionLabel.setForeground(
                UITheme.MUTED
        );

        card.add(
                titleLabel,
                BorderLayout.NORTH
        );

        card.add(
                valueLabel,
                BorderLayout.CENTER
        );

        card.add(
                captionLabel,
                BorderLayout.SOUTH
        );

        return card;
    }

    public void refreshDashboardStats() {

        List<ParkingSpot> allActive =
                parkingSpotService
                        .getAllActiveSpots();

        if (
                totalSpotsValue != null
        ) {

            totalSpotsValue.setText(
                    String.valueOf(
                            allActive.size()
                    )
            );
        }

        if (
                currentUserId <= 0
        ) {

            return;
        }

        List<ParkingSpot> mySpots =
                parkingSpotService
                        .getSpotsByOwner(
                                currentUserId
                        );

        long activeMine =
                mySpots
                        .stream()
                        .filter(
                                ParkingSpot::isActive
                        )
                        .count();

        if (
                mySpotsValue != null
        ) {

            mySpotsValue.setText(
                    String.valueOf(
                            activeMine
                    )
            );
        }

        List<Reservation> reservations =
                reservationService
                        .getReservationsByDriver(
                                currentUserId
                        );

        LocalDateTime now =
                LocalDateTime.now();

        long upcoming =
                reservations
                        .stream()
                        .filter(
                                reservation ->
                                        "CONFIRMED"
                                                .equalsIgnoreCase(
                                                        reservation
                                                                .getStatus()
                                                )
                        )
                        .filter(
                                reservation ->
                                        reservation
                                                .getStartDatetime()
                                                .isAfter(
                                                        now
                                                )
                        )
                        .count();

        if (
                upcomingReservationsValue != null
        ) {

            upcomingReservationsValue.setText(
                    String.valueOf(
                            upcoming
                    )
            );
        }

        int unread =
                notificationService
                        .getUnreadCount(
                                currentUserId
                        );

        if (
                unreadNotificationsValue != null
        ) {

            unreadNotificationsValue.setText(
                    String.valueOf(
                            unread
                    )
            );

            unreadNotificationsValue.setForeground(
                    unread > 0
                            ? UITheme.PRIMARY
                            : UITheme.TEXT
            );
        }
    }

    private void filterByVehicle(
            String vehicleType
    ) {

        if (
                currentMarkers == null
        ) {

            return;
        }

        map.removeAllMapMarkers();

        List<SpotMarker> filtered =
                currentMarkers
                        .stream()
                        .filter(
                                marker ->
                                        "All".equals(
                                                vehicleType
                                        )
                                                ||
                                        marker
                                                .getVehicleType()
                                                .equalsIgnoreCase(
                                                        vehicleType
                                                )
                        )
                        .collect(
                                Collectors.toList()
                        );

        filtered.forEach(
                map::addMapMarker
        );

        updateVisibleCount(
                filtered.size()
        );

        map.repaint();
    }

    private void updateVisibleCount(
            int count
    ) {

        if (
                visibleSpotsLabel != null
        ) {

            visibleSpotsLabel.setText(
                    count
                            +
                    (
                            count == 1
                                    ? " spot shown"
                                    : " spots shown"
                    )
            );
        }

        if (
                guestVisibleSpotsValue != null
        ) {

            guestVisibleSpotsValue.setText(
                    String.valueOf(
                            count
                    )
            );
        }
    }

    public void loadMarkersFromDatabase(
            ParkingSpotService spotService
    ) {

        map.removeAllMapMarkers();

        List<ParkingSpot> spots =
                spotService
                        .getAllActiveSpots();

        addMarkers(
                spots
        );

        refreshDashboardStats();
    }

    public void loadMarkersFromList(
            List<ParkingSpot> spots
    ) {

        map.removeAllMapMarkers();

        addMarkers(
                spots
        );

        refreshDashboardStats();
    }

    private void addMarkers(
            List<ParkingSpot> spots
    ) {

        currentMarkers =
                spots
                        .stream()
                        .filter(
                                spot ->
                                        spot.getLatitude()
                                                != null
                                                &&
                                        spot.getLongitude()
                                                != null
                        )
                        .map(
                                spot ->
                                        new SpotMarker(
                                                spot.getAddress(),

                                                new Coordinate(
                                                        spot.getLatitude(),
                                                        spot.getLongitude()
                                                ),

                                                spot.getSpotId(),

                                                spot.getOwnerId(),

                                                spot.getVehicleType()
                                        )
                        )
                        .collect(
                                Collectors.toList()
                        );

        currentMarkers.forEach(
                map::addMapMarker
        );

        updateVisibleCount(
                currentMarkers.size()
        );

        if (
                vehicleFilter != null
                        &&
                !"All".equals(
                        vehicleFilter
                                .getSelectedItem()
                )
        ) {

            filterByVehicle(
                    (String)
                            vehicleFilter
                                    .getSelectedItem()
            );
        }

        map.repaint();
    }

    private SpotMarker findNearestMarker(
            ICoordinate clickedCoord
    ) {

        SpotMarker nearest =
                null;

        double minDist =
                Double.MAX_VALUE;

        if (
                currentMarkers == null
        ) {

            return null;
        }

        for (
                SpotMarker marker :
                currentMarkers
        ) {

            double dist =
                    distance(
                            clickedCoord.getLat(),
                            clickedCoord.getLon(),

                            marker
                                    .getCoordinate()
                                    .getLat(),

                            marker
                                    .getCoordinate()
                                    .getLon()
                    );

            if (
                    dist < 0.0008
                            &&
                    dist < minDist
            ) {

                minDist =
                        dist;

                nearest =
                        marker;
            }
        }

        return nearest;
    }

    private double distance(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ) {

        double dx =
                lat1 - lat2;

        double dy =
                lon1 - lon2;

        return Math.sqrt(
                dx * dx
                        +
                dy * dy
        );
    }

    private void showMarkerInfo(
            SpotMarker marker
    ) {

        ParkingSpot spot =
                parkingSpotService
                        .getSpotById(
                                marker.getSpotId()
                        );

        if (
                spot == null
        ) {

            UITheme.showError(
                    this,
                    "Parking Spot",
                    "Parking spot details could not be loaded."
            );

            return;
        }

        List<Availability> allPeriods =
                availabilityService
                        .getAvailablePeriodsBySpot(
                                spot.getSpotId()
                        );

        List<Availability> futurePeriods =
                allPeriods
                        .stream()
                        .filter(
                                availability ->
                                        availability
                                                .getEndDatetime()
                                                .isAfter(
                                                        LocalDateTime.now()
                                                )
                        )
                        .sorted(
                                (
                                        a,
                                        b
                                ) ->
                                        a
                                                .getStartDatetime()
                                                .compareTo(
                                                        b.getStartDatetime()
                                                )
                        )
                        .collect(
                                Collectors.toList()
                        );

        JDialog dialog =
                new JDialog(
                        SwingUtilities
                                .getWindowAncestor(
                                        this
                                ),

                        "Parking Spot Details",

                        Dialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );

        JPanel wrapper =
                new JPanel(
                        new BorderLayout()
                );

        wrapper.setBackground(
                UITheme.BACKGROUND
        );

        wrapper.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        18,
                        18,
                        18
                )
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
                new BorderLayout(
                        0,
                        18
                )
        );

        card.setBorder(
                BorderFactory.createEmptyBorder(
                        22,
                        24,
                        22,
                        24
                )
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

        JLabel heading =
                new JLabel(
                        spot.getAddress()
                );

        heading.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        heading.setForeground(
                UITheme.TEXT
        );

        heading.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        content.add(
                heading
        );

        content.add(
                Box.createVerticalStrut(
                        18
                )
        );

        content.add(
                detailRow(
                        "Area",
                        safeText(
                                spot.getArea()
                        )
                )
        );

        content.add(
                Box.createVerticalStrut(
                        9
                )
        );

        content.add(
                detailRow(
                        "Vehicle",
                        safeText(
                                spot.getVehicleType()
                        )
                )
        );

        content.add(
                Box.createVerticalStrut(
                        9
                )
        );

        content.add(
        detailRow(
                "Description",
                        safeText(
                                spot.getDescription()
                        )
                )
        );

        content.add(
                Box.createVerticalStrut(
                 9
                )
        );

        String priceText;

        if (
                spot.isPricingEnabled()
                        &&
                spot.getPricePerHour() != null
        ) {

                priceText =
                pricingService.formatPrice(
                    spot.getPricePerHour()
                )
                    +
                " / hour";

        } else {

        priceText =
            "Free";
        }

        content.add(
                detailRow(
                        "Price",
                        priceText
                )
        );

        content.add(
                Box.createVerticalStrut(
                        9
                )
        );

        content.add(
                detailRow(
                        "Emergency booking",
                        spot.isEmergencyBookingEnabled()
                                ? "Enabled"
                                : "Disabled"
                )
        );

        content.add(
                Box.createVerticalStrut(
                20
                )
        );

        JLabel availabilityTitle =
                new JLabel(
                        "Available periods"
                );

        availabilityTitle.setFont(
                UITheme.FONT_BOLD
        );

        availabilityTitle.setForeground(
                UITheme.TEXT
        );

        availabilityTitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        content.add(
                availabilityTitle
        );

        content.add(
                Box.createVerticalStrut(
                        10
                )
        );

        JPanel periodsPanel =
                new JPanel();

        periodsPanel.setOpaque(
                false
        );

        periodsPanel.setLayout(
                new BoxLayout(
                        periodsPanel,
                        BoxLayout.Y_AXIS
                )
        );

        if (
                futurePeriods.isEmpty()
        ) {

            JLabel noAvailability =
                    new JLabel(
                            "No upcoming availability."
                    );

            noAvailability.setFont(
                    UITheme.FONT
            );

            noAvailability.setForeground(
                    UITheme.MUTED
            );

            periodsPanel.add(
                    noAvailability
            );

        } else {

            for (
                    Availability availability :
                    futurePeriods
            ) {

                String periodText =
                        availability
                                .getStartDatetime()
                                .format(
                                        displayFormatter
                                )
                                +
                        "  →  "
                                +
                        availability
                                .getEndDatetime()
                                .format(
                                        displayFormatter
                                );

                JLabel periodLabel =
                        new JLabel(
                                periodText
                        );

                periodLabel.setFont(
                        UITheme.FONT
                );

                periodLabel.setForeground(
                        UITheme.TEXT
                );

                periodsPanel.add(
                        periodLabel
                );

                periodsPanel.add(
                        Box.createVerticalStrut(
                                7
                        )
                );
            }
        }

        JScrollPane periodsScroll =
                new JScrollPane(
                        periodsPanel
                );

        periodsScroll.setBorder(
                BorderFactory.createEmptyBorder()
        );

        periodsScroll.setOpaque(
                false
        );

        periodsScroll
                .getViewport()
                .setOpaque(
                        false
                );

        periodsScroll.setPreferredSize(
                new Dimension(
                        400,
                        110
                )
        );

        periodsScroll
                .getVerticalScrollBar()
                .setUnitIncrement(
                        12
                );

        content.add(
                periodsScroll
        );

        UITheme.ModernButton reserveButton =
                UITheme.primaryButton(
                        "Reserve this spot"
                );

        boolean ownSpot =
                currentUserId > 0
                        &&
                spot.getOwnerId()
                        == currentUserId;

        if (
                ownSpot
        ) {

            reserveButton.setText(
                    "This is your parking spot"
            );

            reserveButton.setEnabled(
                    false
            );

        } else if (
                futurePeriods.isEmpty()
        ) {

            reserveButton.setText(
                    "No availability"
            );

            reserveButton.setEnabled(
                    false
            );
        }

        reserveButton.addActionListener(
                e -> {

                    if (
                            currentUserId <= 0
                    ) {

                        dialog.dispose();

                        if (
                                loginRequiredAction != null
                        ) {

                            loginRequiredAction.run();

                        } else {

                            UITheme.showInfo(
                                    this,
                                    "Login Required",
                                    "You need to sign in or create an account to reserve a parking spot."
                            );
                        }

                        return;
                    }

                    if (
                            reservationPanel == null
                    ) {

                        return;
                    }

                    reservationPanel
                            .prepareForReservation(
                                    currentUserId,
                                    spot.getSpotId()
                            );

                    Container parent =
                            this.getParent();

                    if (
                            parent != null
                                    &&
                            parent.getLayout()
                                    instanceof CardLayout
                    ) {

                        CardLayout layout =
                                (CardLayout)
                                        parent.getLayout();

                        layout.show(
                                parent,
                                "reservation"
                        );
                    }

                    dialog.dispose();
                }
        );

        card.add(
                content,
                BorderLayout.CENTER
        );

        card.add(
                reserveButton,
                BorderLayout.SOUTH
        );

        wrapper.add(
                card,
                BorderLayout.CENTER
        );

        dialog.setContentPane(
                wrapper
        );

        dialog.setSize(
                520,
                560
        );

        dialog.setLocationRelativeTo(
                this
        );

        dialog.setVisible(
                true
        );
    }

    private void showPricingRulesDialog() {

    JDialog dialog =
            new JDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Pricing & Booking Rules",
                    Dialog.ModalityType.APPLICATION_MODAL
            );

    dialog.setDefaultCloseOperation(
            WindowConstants.DISPOSE_ON_CLOSE
    );

    dialog.setUndecorated(true);

    JPanel wrapper =
            new JPanel(
                    new BorderLayout()
            );

    wrapper.setOpaque(false);

    wrapper.setBorder(
            BorderFactory.createEmptyBorder(
                    6,
                    6,
                    6,
                    6
            )
    );

    UITheme.RoundedPanel card =
            new UITheme.RoundedPanel(
                    22,
                    Color.WHITE
            );

    card.setOutlineColor(
            UITheme.BORDER
    );

    card.setLayout(
            new BorderLayout(
                    0,
                    20
            )
    );

    card.setBorder(
            BorderFactory.createEmptyBorder(
                    28,
                    30,
                    24,
                    30
            )
    );

    JPanel content =
            new JPanel();

    content.setOpaque(false);

    content.setLayout(
            new BoxLayout(
                    content,
                    BoxLayout.Y_AXIS
            )
    );

    JLabel title =
            new JLabel(
                    "Pricing & Booking Information"
            );

    title.setFont(
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    22
            )
    );

    title.setForeground(
            UITheme.TEXT
    );

    title.setAlignmentX(
            Component.LEFT_ALIGNMENT
    );

    content.add(title);

    content.add(
            Box.createVerticalStrut(6)
    );

    JLabel subtitle =
            new JLabel(
                    "Please review these rules before creating a paid reservation."
            );

    subtitle.setFont(
            UITheme.FONT
    );

    subtitle.setForeground(
            UITheme.MUTED
    );

    subtitle.setAlignmentX(
            Component.LEFT_ALIGNMENT
    );

    content.add(subtitle);

    content.add(
            Box.createVerticalStrut(22)
    );

    content.add(
            ruleItem(
                    "Optional pricing",
                    "Parking spot owners may choose whether their spot is free or paid."
            )
    );

    content.add(
            Box.createVerticalStrut(13)
    );

    content.add(
            ruleItem(
                    "Reservations under 1 hour",
                    "A reservation lasting less than one hour is not charged."
            )
    );

    content.add(
            Box.createVerticalStrut(13)
    );

    content.add(
            ruleItem(
                    "Extra minutes",
                    "Up to 30 additional minutes do not add another charged hour."
            )
    );

    content.add(
            Box.createVerticalStrut(13)
    );

    content.add(
            ruleItem(
                    "More than 30 extra minutes",
                    "If the remaining time exceeds 30 minutes, one additional hour is charged."
            )
    );

    content.add(
            Box.createVerticalStrut(13)
    );

    content.add(
            ruleItem(
                    "Payment deadline",
                    "Paid normal reservations must be fully paid at least 30 minutes before they begin."
            )
    );

    content.add(
            Box.createVerticalStrut(13)
    );

    content.add(
            ruleItem(
                    "Unpaid reservations",
                    "If the payment deadline passes, the reservation is cancelled when the system is refreshed or reopened."
            )
    );

    content.add(
            Box.createVerticalStrut(13)
    );

    content.add(
            ruleItem(
                    "Emergency booking",
                    "A reservation starting within 30 minutes is allowed only when the owner has enabled emergency booking."
            )
    );

    content.add(
            Box.createVerticalStrut(13)
    );

    content.add(
            ruleItem(
                    "Emergency payment",
                    "A paid emergency booking must be paid immediately in order to be confirmed."
            )
    );

    content.add(
            Box.createVerticalStrut(13)
    );

    content.add(
            ruleItem(
                    "Reservation cancellation",
                    "A confirmed reservation can be cancelled before it begins."
            )
    );

    content.add(
            Box.createVerticalStrut(13)
    );

    content.add(
            ruleItem(
                    "Refund policy",
                    "If a paid reservation is cancelled at least 1 hour before it begins, the payment is refunded. Later cancellations are not refunded."
            )
    );

    content.add(
            Box.createVerticalStrut(20)
    );

    UITheme.RoundedPanel note =
            new UITheme.RoundedPanel(
                    14,
                    UITheme.PRIMARY_SOFT
            );

    note.setLayout(
            new BorderLayout()
    );

    note.setBorder(
            BorderFactory.createEmptyBorder(
                    13,
                    15,
                    13,
                    15
            )
    );

    JLabel noteText =
            new JLabel(
                    "<html>"
                            + "Before a paid reservation is created, the final price is shown "
                            + "and the user must explicitly accept the pricing policy."
                            + "</html>"
            );

    noteText.setFont(
            UITheme.FONT
    );

    noteText.setForeground(
            UITheme.PRIMARY
    );

    note.add(
            noteText,
            BorderLayout.CENTER
    );

    note.setAlignmentX(
            Component.LEFT_ALIGNMENT
    );

    content.add(note);

    JScrollPane scrollPane =
            new JScrollPane(
                    content
            );

    scrollPane.setBorder(null);

    scrollPane.setOpaque(false);

    scrollPane
            .getViewport()
            .setOpaque(false);

    scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );

    scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
    );

    scrollPane
            .getVerticalScrollBar()
            .setUnitIncrement(14);

    card.add(
            scrollPane,
            BorderLayout.CENTER
    );

    JPanel buttons =
            new JPanel(
                    new FlowLayout(
                            FlowLayout.RIGHT,
                            0,
                            0
                    )
            );

    buttons.setOpaque(false);

    UITheme.ModernButton closeButton =
            UITheme.primaryButton(
                    "Got it"
            );

    closeButton.addActionListener(
            e -> dialog.dispose()
    );

    buttons.add(closeButton);

    card.add(
            buttons,
            BorderLayout.SOUTH
    );

    wrapper.add(
            card,
            BorderLayout.CENTER
    );

    dialog.setContentPane(
            wrapper
    );

    dialog.setBackground(
            new Color(
                    0,
                    0,
                    0,
                    0
            )
    );

    dialog.setSize(
            700,
            720
    );

    dialog.setLocationRelativeTo(
            this
    );

    dialog.setVisible(
            true
    );
}

    private JPanel ruleItem(
            String title,
            String description
    ) {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                14,
                                0
                        )
                );

        panel.setOpaque(
                false
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        65
                )
        );

        panel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel icon =
                new JLabel(
                        "✓",
                        SwingConstants.CENTER
                );

        icon.setOpaque(
                true
        );

        icon.setBackground(
                UITheme.SUCCESS_SOFT
        );

        icon.setForeground(
                UITheme.SUCCESS
        );

        icon.setFont(
                UITheme.FONT_BOLD
        );

        icon.setPreferredSize(
                new Dimension(
                        36,
                        36
                )
        );

        JPanel textPanel =
                new JPanel();

        textPanel.setOpaque(
                false
        );

        textPanel.setLayout(
                new BoxLayout(
                        textPanel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titleLabel =
                new JLabel(
                        title
                );

        titleLabel.setFont(
                UITheme.FONT_BOLD
        );

        titleLabel.setForeground(
                UITheme.TEXT
        );

        JLabel descriptionLabel =
                new JLabel(
                        "<html><body style='width:480px'>"
                                +
                        description
                                +
                        "</body></html>"
                );

        descriptionLabel.setFont(
                UITheme.SMALL
        );

        descriptionLabel.setForeground(
                UITheme.MUTED
        );

        textPanel.add(
                titleLabel
        );

        textPanel.add(
                Box.createVerticalStrut(
                        3
                )
        );

        textPanel.add(
                descriptionLabel
        );

        panel.add(
                icon,
                BorderLayout.WEST
        );

        panel.add(
                textPanel,
                BorderLayout.CENTER
        );

        return panel;
    }

    private String safeText(
            String text
    ) {

        if (
                text == null
                        ||
                text.trim().isEmpty()
        ) {

            return "-";
        }

        return text;
    }

    private JPanel detailRow(
            String label,
            String value
    ) {

        JPanel row =
                new JPanel(
                        new BorderLayout(
                                15,
                                0
                        )
                );

        row.setOpaque(
                false
        );

        row.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        26
                )
        );

        row.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel left =
                new JLabel(
                        label
                );

        left.setFont(
                UITheme.FONT_BOLD
        );

        left.setForeground(
                UITheme.MUTED
        );

        JLabel right =
                new JLabel(
                        value
                );

        right.setFont(
                UITheme.FONT
        );

        right.setForeground(
                UITheme.TEXT
        );

        row.add(
                left,
                BorderLayout.WEST
        );

        row.add(
                right,
                BorderLayout.EAST
        );

        return row;
    }

    public JMapViewer getMap() {

        return map;
    }
}