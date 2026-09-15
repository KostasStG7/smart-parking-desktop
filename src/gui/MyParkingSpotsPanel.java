package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import model.ParkingSpot;

import service.ParkingSpotService;
import service.PricingService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Locale;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;

public class MyParkingSpotsPanel extends JPanel {

    private final int currentUserId;

    private final ParkingSpotService parkingSpotService;
    private final PricingService pricingService;

    private final JPanel spotsContainer;

    private final DashboardPanel dashboardPanel;

    public MyParkingSpotsPanel(
            int userId,
            DashboardPanel dashboardPanel
    ) {

        this.currentUserId =
                userId;

        this.dashboardPanel =
                dashboardPanel;

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
                        "My Parking Spots",
                        "Manage the parking spots you have published."
                ),
                BorderLayout.WEST
        );

        UITheme.ModernButton refreshButton =
                UITheme.secondaryButton(
                        "Refresh"
                );

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                0,
                                4
                        )
                );

        buttonPanel.setOpaque(
                false
        );

        buttonPanel.add(
                refreshButton
        );

        header.add(
                buttonPanel,
                BorderLayout.EAST
        );

        add(
                header,
                BorderLayout.NORTH
        );

        spotsContainer =
                new JPanel();

        spotsContainer.setOpaque(
                false
        );

        spotsContainer.setLayout(
                new BoxLayout(
                        spotsContainer,
                        BoxLayout.Y_AXIS
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        spotsContainer
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
                e -> loadParkingSpots()
        );

        loadParkingSpots();
    }

    public void loadParkingSpots() {

        spotsContainer.removeAll();

        List<ParkingSpot> spots =
                parkingSpotService
                        .getSpotsByOwner(
                                currentUserId
                        );

        if (
                spots.isEmpty()
        ) {

            addEmptyState();

        } else {

            for (
                    ParkingSpot spot :
                    spots
            ) {

                spotsContainer.add(
                        createSpotCard(
                                spot
                        )
                );

                spotsContainer.add(
                        Box.createVerticalStrut(
                                14
                        )
                );
            }
        }

        spotsContainer.revalidate();
        spotsContainer.repaint();
    }

    private JPanel createSpotCard(
            ParkingSpot spot
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
                        205
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
                        safeText(
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
                        6
                )
        );

        JLabel info =
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

        info.setFont(
                UITheme.SMALL
        );

        info.setForeground(
                UITheme.MUTED
        );

        info.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        details.add(
                info
        );

        details.add(
                Box.createVerticalStrut(
                        10
                )
        );

        JLabel description =
                new JLabel(
                        "Description: "
                                +
                        safeText(
                                spot.getDescription()
                        )
                );

        description.setFont(
                UITheme.FONT
        );

        description.setForeground(
                UITheme.TEXT
        );

        description.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        details.add(
                description
        );

        details.add(
                Box.createVerticalStrut(
                        8
                )
        );

        String priceText;

        if (
                spot.isPricingEnabled()
                        &&
                spot.getPricePerHour() != null
        ) {

            priceText =
                    "Price: "
                            +
                    pricingService.formatPrice(
                            spot.getPricePerHour()
                    )
                            +
                    " / hour";

        } else {

            priceText =
                    "Price: Free";
        }

        JLabel pricingLabel =
                new JLabel(
                        priceText
                );

        pricingLabel.setFont(
                UITheme.FONT
        );

        pricingLabel.setForeground(
                spot.isPricingEnabled()
                        ? UITheme.PRIMARY
                        : UITheme.SUCCESS
        );

        pricingLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        details.add(
                pricingLabel
        );

        details.add(
                Box.createVerticalStrut(
                        6
                )
        );

        JLabel emergencyLabel =
                new JLabel(
                        spot.isEmergencyBookingEnabled()
                                ? "Emergency booking: Enabled"
                                : "Emergency booking: Disabled"
                );

        emergencyLabel.setFont(
                UITheme.SMALL
        );

        emergencyLabel.setForeground(
                UITheme.MUTED
        );

        emergencyLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        details.add(
                emergencyLabel
        );

        details.add(
                Box.createVerticalStrut(
                        10
                )
        );

        details.add(
                UITheme.statusBadge(
                        spot.isActive()
                                ? "ACTIVE"
                                : "INACTIVE"
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

        UITheme.ModernButton editButton =
                UITheme.secondaryButton(
                        "Edit"
                );

        UITheme.ModernButton activeButton;

        if (
                spot.isActive()
        ) {

            activeButton =
                    UITheme.secondaryButton(
                            "Deactivate"
                    );

        } else {

            activeButton =
                    UITheme.primaryButton(
                            "Activate"
                    );
        }

        editButton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        activeButton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        actions.add(
                editButton
        );

        actions.add(
                Box.createVerticalStrut(
                        10
                )
        );

        actions.add(
                activeButton
        );

        editButton.addActionListener(
                e ->
                        showEditDialog(
                                spot
                        )
        );

        activeButton.addActionListener(
                e ->
                        toggleActive(
                                spot
                        )
        );

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

    private void toggleActive(
            ParkingSpot spot
    ) {

        boolean newStatus =
                !spot.isActive();

        String action =
                newStatus
                        ? "activate"
                        : "deactivate";

        boolean confirmed =
                UITheme.showConfirm(
                        this,
                        "Parking Spot",
                        "Do you want to "
                                +
                        action
                                +
                        " this parking spot?",
                        newStatus
                                ? "Activate"
                                : "Deactivate"
                );

        if (
                !confirmed
        ) {

            return;
        }

        boolean success =
                parkingSpotService
                        .setSpotActive(
                                currentUserId,
                                spot.getSpotId(),
                                newStatus
                        );

        if (
                success
        ) {

            UITheme.showSuccess(
                    this,
                    "Parking Spot Updated",
                    newStatus
                            ? "Parking spot activated successfully."
                            : "Parking spot deactivated successfully."
            );

            refreshEverything();

        } else {

            String error =
                    parkingSpotService
                            .getLastErrorMessage();

            if (
                    error == null
                            ||
                    error.isBlank()
            ) {

                error =
                        "Failed to update the parking spot.";
            }

            UITheme.showError(
                    this,
                    "Operation Failed",
                    error
            );
        }
    }

    private void showEditDialog(
            ParkingSpot spot
    ) {

        JTextField addressField =
                new JTextField(
                        safeTextForField(
                                spot.getAddress()
                        )
                );

        JTextField areaField =
                new JTextField(
                        safeTextForField(
                                spot.getArea()
                        )
                );

        JTextField descriptionField =
                new JTextField(
                        safeTextForField(
                                spot.getDescription()
                        )
                );

        JComboBox<String> vehicleComboBox =
                new JComboBox<>(
                        new String[]{
                                "Car",
                                "Motorbike"
                        }
                );

        vehicleComboBox.setSelectedItem(
                spot.getVehicleType()
        );

        JTextField pricePerHourField =
                new JTextField();

        JCheckBox pricingEnabledCheckBox =
                new JCheckBox(
                        "Paid parking"
                );

        JCheckBox emergencyBookingCheckBox =
                new JCheckBox(
                        "Allow emergency booking"
                );

        JLabel priceRuleLabel =
                new JLabel();

        JTextField latitudeField =
                new JTextField();

        JTextField longitudeField =
                new JTextField();

        UITheme.styleTextField(
                addressField
        );

        UITheme.styleTextField(
                areaField
        );

        UITheme.styleTextField(
                descriptionField
        );

        UITheme.styleComboBox(
                vehicleComboBox
        );

        UITheme.styleTextField(
                pricePerHourField
        );

        UITheme.styleTextField(
                latitudeField
        );

        UITheme.styleTextField(
                longitudeField
        );

        latitudeField.setEditable(
                false
        );

        longitudeField.setEditable(
                false
        );

        pricingEnabledCheckBox.setOpaque(
                false
        );

        pricingEnabledCheckBox.setFont(
                UITheme.FONT_BOLD
        );

        pricingEnabledCheckBox.setForeground(
                UITheme.TEXT
        );

        emergencyBookingCheckBox.setOpaque(
                false
        );

        emergencyBookingCheckBox.setFont(
                UITheme.FONT_BOLD
        );

        emergencyBookingCheckBox.setForeground(
                UITheme.TEXT
        );

        priceRuleLabel.setFont(
                UITheme.SMALL
        );

        priceRuleLabel.setForeground(
                UITheme.MUTED
        );

        pricingEnabledCheckBox.setSelected(
                spot.isPricingEnabled()
        );

        emergencyBookingCheckBox.setSelected(
                spot.isEmergencyBookingEnabled()
        );

        if (
                spot.getPricePerHour() != null
        ) {

            pricePerHourField.setText(
                    String.format(
                            Locale.US,
                            "%.2f",
                            spot.getPricePerHour()
                    )
            );
        }

        latitudeField.setText(
                String.format(
                        Locale.US,
                        "%.6f",
                        spot.getLatitude()
                )
        );

        longitudeField.setText(
                String.format(
                        Locale.US,
                        "%.6f",
                        spot.getLongitude()
                )
        );

        Runnable updatePricingControls =
                () -> {

                    boolean enabled =
                            pricingEnabledCheckBox
                                    .isSelected();

                    pricePerHourField.setEnabled(
                            enabled
                    );

                    String vehicle =
                            (String)
                                    vehicleComboBox
                                            .getSelectedItem();

                    if (
                            enabled
                    ) {

                        priceRuleLabel.setText(
                                pricingService
                                        .getPriceRuleText(
                                                vehicle
                                        )
                        );

                    } else {

                        priceRuleLabel.setText(
                                "Η θέση θα παραμείνει δωρεάν."
                        );
                    }
                };

        pricingEnabledCheckBox.addActionListener(
                e ->
                        updatePricingControls.run()
        );

        vehicleComboBox.addActionListener(
                e ->
                        updatePricingControls.run()
        );

        updatePricingControls.run();

        JMapViewer editMap =
                new JMapViewer();

        editMap.setPreferredSize(
                new Dimension(
                        620,
                        280
                )
        );

        Coordinate currentCoordinate =
                new Coordinate(
                        spot.getLatitude(),
                        spot.getLongitude()
                );

        editMap.setDisplayPosition(
                currentCoordinate,
                16
        );

        MapMarkerDot[] selectedMarker =
                new MapMarkerDot[1];

        selectedMarker[0] =
                new MapMarkerDot(
                        spot.getLatitude(),
                        spot.getLongitude()
                );

        editMap.addMapMarker(
                selectedMarker[0]
        );

        editMap.addMouseListener(
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

                        ICoordinate coordinate =
                                editMap.getPosition(
                                        e.getX(),
                                        e.getY()
                                );

                        if (
                                coordinate == null
                        ) {

                            return;
                        }

                        double latitude =
                                coordinate.getLat();

                        double longitude =
                                coordinate.getLon();

                        latitudeField.setText(
                                String.format(
                                        Locale.US,
                                        "%.6f",
                                        latitude
                                )
                        );

                        longitudeField.setText(
                                String.format(
                                        Locale.US,
                                        "%.6f",
                                        longitude
                                )
                        );

                        if (
                                selectedMarker[0] != null
                        ) {

                            editMap.removeMapMarker(
                                    selectedMarker[0]
                            );
                        }

                        selectedMarker[0] =
                                new MapMarkerDot(
                                        latitude,
                                        longitude
                                );

                        editMap.addMapMarker(
                                selectedMarker[0]
                        );

                        editMap.repaint();

                        addressField.setText(
                                "Loading..."
                        );

                        areaField.setText(
                                "Loading..."
                        );

                        reverseGeocodeForEdit(
                                latitude,
                                longitude,
                                addressField,
                                areaField
                        );
                    }
                }
        );

        JPanel form =
                new JPanel();

        form.setLayout(
                new BoxLayout(
                        form,
                        BoxLayout.Y_AXIS
                )
        );

        form.setBackground(
                Color.WHITE
        );

        form.add(
                createEditField(
                        "Address",
                        addressField
                )
        );

        form.add(
                Box.createVerticalStrut(
                        10
                )
        );

        form.add(
                createEditField(
                        "Area",
                        areaField
                )
        );

        form.add(
                Box.createVerticalStrut(
                        10
                )
        );

        form.add(
                createEditField(
                        "Description",
                        descriptionField
                )
        );

        form.add(
                Box.createVerticalStrut(
                        10
                )
        );

        form.add(
                createEditComboField(
                        "Vehicle type",
                        vehicleComboBox
                )
        );

        form.add(
                Box.createVerticalStrut(
                        16
                )
        );

        UITheme.RoundedPanel pricingPanel =
                new UITheme.RoundedPanel(
                        14,
                        UITheme.PRIMARY_SOFT
                );

        pricingPanel.setLayout(
                new BoxLayout(
                        pricingPanel,
                        BoxLayout.Y_AXIS
                )
        );

        pricingPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        16,
                        18,
                        16,
                        18
                )
        );

        pricingPanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        pricingEnabledCheckBox.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        pricingPanel.add(
                pricingEnabledCheckBox
        );

        pricingPanel.add(
                Box.createVerticalStrut(
                        10
                )
        );

        JPanel priceFieldPanel =
                createEditField(
                        "Price per hour",
                        pricePerHourField
                );

        priceFieldPanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        pricingPanel.add(
                priceFieldPanel
        );

        pricingPanel.add(
                Box.createVerticalStrut(
                        5
                )
        );

        priceRuleLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        pricingPanel.add(
                priceRuleLabel
        );

        form.add(
                pricingPanel
        );

        form.add(
                Box.createVerticalStrut(
                        14
                )
        );

        emergencyBookingCheckBox.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        form.add(
                emergencyBookingCheckBox
        );

        form.add(
                Box.createVerticalStrut(
                        5
                )
        );

        JLabel emergencyHint =
                new JLabel(
                        "Επιτρέπει κρατήσεις μέσα στο τελευταίο 30λεπτο πριν την έναρξη."
                );

        emergencyHint.setFont(
                UITheme.SMALL
        );

        emergencyHint.setForeground(
                UITheme.MUTED
        );

        emergencyHint.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        form.add(
                emergencyHint
        );

        form.add(
                Box.createVerticalStrut(
                        18
                )
        );

        JLabel mapTitle =
                new JLabel(
                        "Location"
                );

        mapTitle.setFont(
                UITheme.FONT_BOLD
        );

        mapTitle.setForeground(
                UITheme.TEXT
        );

        mapTitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        form.add(
                mapTitle
        );

        form.add(
                Box.createVerticalStrut(
                        6
                )
        );

        editMap.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        form.add(
                editMap
        );

        form.add(
                Box.createVerticalStrut(
                        10
                )
        );

        JLabel mapHint =
                new JLabel(
                        "Click on the map to change the parking location."
                );

        mapHint.setFont(
                UITheme.SMALL
        );

        mapHint.setForeground(
                UITheme.MUTED
        );

        mapHint.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        form.add(
                mapHint
        );

        form.add(
                Box.createVerticalStrut(
                        12
                )
        );

        JPanel coordsPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                12,
                                0
                        )
                );

        coordsPanel.setOpaque(
                false
        );

        coordsPanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        coordsPanel.add(
                createEditField(
                        "Latitude",
                        latitudeField
                )
        );

        coordsPanel.add(
                createEditField(
                        "Longitude",
                        longitudeField
                )
        );

        form.add(
                coordsPanel
        );

        form.setPreferredSize(
        new Dimension(
                700,
                900
        )
);

boolean saveChanges =
        UITheme.showComponentConfirmDialog(
                this,
                "Edit Parking Spot",
                form,
                "Save changes",
                "Cancel"
        );

        if (
                !saveChanges
        ) {

            return;
        }

        String address =
                addressField
                        .getText()
                        .trim();

        String area =
                areaField
                        .getText()
                        .trim();

        String description =
                descriptionField
                        .getText()
                        .trim();

        String vehicle =
                (String)
                        vehicleComboBox
                                .getSelectedItem();

        if (
                address.isEmpty()
                        ||
                area.isEmpty()
                        ||
                vehicle == null
                        ||
                vehicle.trim().isEmpty()
                        ||
                "Loading...".equals(
                        address
                )
                        ||
                "Loading...".equals(
                        area
                )
        ) {

            UITheme.showWarning(
                    this,
                    "Missing Information",
                    "Address, area and vehicle type are required."
            );

            return;
        }

        double latitude;
        double longitude;

        try {

            latitude =
                    Double.parseDouble(
                            latitudeField
                                    .getText()
                                    .trim()
                    );

            longitude =
                    Double.parseDouble(
                            longitudeField
                                    .getText()
                                    .trim()
                    );

        } catch (
                NumberFormatException ex
        ) {

            UITheme.showError(
                    this,
                    "Invalid Coordinates",
                    "Invalid parking coordinates."
            );

            return;
        }

        boolean pricingEnabled =
                pricingEnabledCheckBox
                        .isSelected();

        Double pricePerHour =
                null;

        if (
                pricingEnabled
        ) {

            String priceText =
                    pricePerHourField
                            .getText()
                            .trim()
                            .replace(
                                    ",",
                                    "."
                            );

            if (
                    priceText.isEmpty()
            ) {

                UITheme.showWarning(
                        this,
                        "Price Required",
                        "Enter a price per hour for this paid parking spot."
                );

                return;
            }

            try {

                pricePerHour =
                        Double.parseDouble(
                                priceText
                        );

            } catch (
                    NumberFormatException ex
            ) {

                UITheme.showWarning(
                        this,
                        "Invalid Price",
                        "Price per hour must be a valid number."
                );

                return;
            }

            if (
                    !pricingService
                            .isValidPrice(
                                    vehicle,
                                    pricePerHour
                            )
            ) {

                UITheme.showWarning(
                        this,
                        "Invalid Price",
                        pricingService
                                .getPriceRuleText(
                                        vehicle
                                )
                );

                return;
            }
        }

        boolean emergencyBookingEnabled =
                emergencyBookingCheckBox
                        .isSelected();

        boolean success =
                parkingSpotService
                        .updateParkingSpot(
                                currentUserId,
                                spot.getSpotId(),
                                address,
                                area,
                                description,
                                vehicle,
                                pricingEnabled,
                                pricePerHour,
                                emergencyBookingEnabled,
                                latitude,
                                longitude
                        );

        if (
                success
        ) {

            UITheme.showSuccess(
                    this,
                    "Parking Spot Updated",
                    "Parking spot updated successfully."
            );

            refreshEverything();

        } else {

            String error =
                    parkingSpotService
                            .getLastErrorMessage();

            if (
                    error == null
                            ||
                    error.isBlank()
            ) {

                error =
                        "Failed to update the parking spot.";
            }

            UITheme.showError(
                    this,
                    "Operation Failed",
                    error
            );
        }
    }

    private JPanel createEditField(
            String label,
            JTextField field
    ) {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                6
                        )
                );

        panel.setOpaque(
                false
        );

        panel.add(
                UITheme.fieldLabel(
                        label
                ),
                BorderLayout.NORTH
        );

        panel.add(
                field,
                BorderLayout.CENTER
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        70
                )
        );

        panel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return panel;
    }

    private JPanel createEditComboField(
            String label,
            JComboBox<String> comboBox
    ) {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                6
                        )
                );

        panel.setOpaque(
                false
        );

        panel.add(
                UITheme.fieldLabel(
                        label
                ),
                BorderLayout.NORTH
        );

        panel.add(
                comboBox,
                BorderLayout.CENTER
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        70
                )
        );

        panel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return panel;
    }

    private void reverseGeocodeForEdit(
            double latitude,
            double longitude,
            JTextField addressField,
            JTextField areaField
    ) {

        new Thread(
                () -> {

                    try {

                        String url =
                                "https://nominatim.openstreetmap.org/reverse"
                                        +
                                "?format=jsonv2"
                                        +
                                "&lat="
                                        +
                                latitude
                                        +
                                "&lon="
                                        +
                                longitude
                                        +
                                "&addressdetails=1"
                                        +
                                "&accept-language=el";

                        HttpClient client =
                                HttpClient.newHttpClient();

                        HttpRequest request =
                                HttpRequest
                                        .newBuilder()
                                        .uri(
                                                URI.create(
                                                        url
                                                )
                                        )
                                        .header(
                                                "User-Agent",
                                                "SmartParking-UniversityProject/1.0"
                                        )
                                        .GET()
                                        .build();

                        HttpResponse<String> response =
                                client.send(
                                        request,
                                        HttpResponse
                                                .BodyHandlers
                                                .ofString(
                                                        StandardCharsets.UTF_8
                                                )
                                );

                        if (
                                response.statusCode()
                                        != 200
                        ) {

                            SwingUtilities.invokeLater(
                                    () -> {

                                        addressField.setText(
                                                ""
                                        );

                                        areaField.setText(
                                                ""
                                        );
                                    }
                            );

                            return;
                        }

                        String json =
                                response.body();

                        String road =
                                extractJsonValue(
                                        json,
                                        "road"
                                );

                        if (
                                road.isEmpty()
                        ) {

                            road =
                                    extractJsonValue(
                                            json,
                                            "pedestrian"
                                    );
                        }

                        String houseNumber =
                                extractJsonValue(
                                        json,
                                        "house_number"
                                );

                        String neighbourhood =
                                extractJsonValue(
                                        json,
                                        "neighbourhood"
                                );

                        String suburb =
                                extractJsonValue(
                                        json,
                                        "suburb"
                                );

                        String quarter =
                                extractJsonValue(
                                        json,
                                        "quarter"
                                );

                        String city =
                                extractJsonValue(
                                        json,
                                        "city"
                                );

                        String town =
                                extractJsonValue(
                                        json,
                                        "town"
                                );

                        String village =
                                extractJsonValue(
                                        json,
                                        "village"
                                );

                        String municipality =
                                extractJsonValue(
                                        json,
                                        "municipality"
                                );

                        String address =
                                road;

                        if (
                                !houseNumber.isEmpty()
                        ) {

                            if (
                                    !address.isEmpty()
                            ) {

                                address +=
                                        " ";
                            }

                            address +=
                                    houseNumber;
                        }

                        String area =
                                "";

                        if (
                                !neighbourhood.isEmpty()
                        ) {

                            area =
                                    neighbourhood;

                        } else if (
                                !suburb.isEmpty()
                        ) {

                            area =
                                    suburb;

                        } else if (
                                !quarter.isEmpty()
                        ) {

                            area =
                                    quarter;

                        } else if (
                                !city.isEmpty()
                        ) {

                            area =
                                    city;

                        } else if (
                                !town.isEmpty()
                        ) {

                            area =
                                    town;

                        } else if (
                                !village.isEmpty()
                        ) {

                            area =
                                    village;

                        } else if (
                                !municipality.isEmpty()
                        ) {

                            area =
                                    municipality;
                        }

                        String finalAddress =
                                address;

                        String finalArea =
                                area;

                        SwingUtilities.invokeLater(
                                () -> {

                                    addressField.setText(
                                            finalAddress
                                    );

                                    areaField.setText(
                                            finalArea
                                    );
                                }
                        );

                    } catch (
                            Exception ex
                    ) {

                        ex.printStackTrace();

                        SwingUtilities.invokeLater(
                                () -> {

                                    addressField.setText(
                                            ""
                                    );

                                    areaField.setText(
                                            ""
                                    );
                                }
                        );
                    }
                }
        ).start();
    }

    private String extractJsonValue(
            String json,
            String key
    ) {

        String search =
                "\""
                        +
                key
                        +
                "\":\"";

        int start =
                json.indexOf(
                        search
                );

        if (
                start == -1
        ) {

            return "";
        }

        start +=
                search.length();

        StringBuilder result =
                new StringBuilder();

        boolean escaped =
                false;

        for (
                int i = start;
                i < json.length();
                i++
        ) {

            char c =
                    json.charAt(
                            i
                    );

            if (
                    escaped
            ) {

                switch (
                        c
                ) {

                    case '"':
                        result.append(
                                '"'
                        );
                        break;

                    case '\\':
                        result.append(
                                '\\'
                        );
                        break;

                    case '/':
                        result.append(
                                '/'
                        );
                        break;

                    case 'b':
                        result.append(
                                '\b'
                        );
                        break;

                    case 'f':
                        result.append(
                                '\f'
                        );
                        break;

                    case 'n':
                        result.append(
                                '\n'
                        );
                        break;

                    case 'r':
                        result.append(
                                '\r'
                        );
                        break;

                    case 't':
                        result.append(
                                '\t'
                        );
                        break;

                    default:
                        result.append(
                                c
                        );
                        break;
                }

                escaped =
                        false;

            } else {

                if (
                        c == '\\'
                ) {

                    escaped =
                            true;

                } else if (
                        c == '"'
                ) {

                    break;

                } else {

                    result.append(
                            c
                    );
                }
            }
        }

        return result
                .toString()
                .trim();
    }

    private void refreshEverything() {

        loadParkingSpots();

        if (
                dashboardPanel != null
        ) {

            dashboardPanel
                    .loadMarkersFromDatabase(
                            parkingSpotService
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

        JLabel title =
                new JLabel(
                        "No parking spots yet"
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
                        "Parking spots you publish will appear here."
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

        spotsContainer.add(
                card
        );
    }

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

    private String safeTextForField(
            String value
    ) {

        return value == null
                ? ""
                : value;
    }
}