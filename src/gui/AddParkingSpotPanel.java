package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;

import service.ParkingSpotService;
import service.PricingService;

public class AddParkingSpotPanel extends JPanel {

    private final JTextField addressField;
    private final JTextField areaField;
    private final JTextField descriptionField;

    private final JComboBox<String> vehicleTypeComboBox;

    private final JCheckBox pricingEnabledCheckBox;
    private final JTextField pricePerHourField;
    private final JLabel priceRuleLabel;

    private final JCheckBox emergencyBookingCheckBox;

    private final JTextField latitudeField;
    private final JTextField longitudeField;

    private final ParkingSpotService spotService;
    private final PricingService pricingService;

    private final int currentUserId;

    private final JMapViewer map;

    private MapMarkerDot selectedMarker;

    public AddParkingSpotPanel(
            int userId,
            DashboardPanel dashboardPanel
    ) {

        this.currentUserId =
                userId;

        this.spotService =
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
                        "Add Parking Spot",
                        "Publish a new parking location so other users can discover it on the map."
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
                        26,
                        30,
                        26,
                        30
                )
        );

        card.setPreferredSize(
                new Dimension(
                        800,
                        1080
                )
        );

        GridBagConstraints gbc =
                formConstraints();

        int row = 0;

        JLabel formTitle =
                new JLabel(
                        "Spot details"
                );

        formTitle.setFont(
                UITheme.SECTION
        );

        formTitle.setForeground(
                UITheme.TEXT
        );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        20,
                        0
                );

        card.add(
                formTitle,
                gbc
        );

        addressField =
                new JTextField();

        areaField =
                new JTextField();

        descriptionField =
                new JTextField();

        latitudeField =
                new JTextField();

        longitudeField =
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

        row =
                addField(
                        card,
                        gbc,
                        row,
                        "Address",
                        addressField
                );

        row =
                addField(
                        card,
                        gbc,
                        row,
                        "Area",
                        areaField
                );

        row =
                addField(
                        card,
                        gbc,
                        row,
                        "Description",
                        descriptionField
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
                        "Vehicle type"
                ),
                gbc
        );

        vehicleTypeComboBox =
                new JComboBox<>(
                        new String[]{
                                "Car",
                                "Motorbike"
                        }
                );

        UITheme.styleComboBox(
                vehicleTypeComboBox
        );

        vehicleTypeComboBox.setPreferredSize(
                new Dimension(
                        280,
                        42
                )
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
                vehicleTypeComboBox,
                gbc
        );

        UITheme.RoundedPanel pricingPanel =
                new UITheme.RoundedPanel(
                        14,
                        UITheme.PRIMARY_SOFT
                );

        pricingPanel.setLayout(
                new GridBagLayout()
        );

        pricingPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        16,
                        18,
                        16,
                        18
                )
        );

        GridBagConstraints pricingGbc =
                new GridBagConstraints();

        pricingGbc.gridx =
                0;

        pricingGbc.weightx =
                1;

        pricingGbc.fill =
                GridBagConstraints.HORIZONTAL;

        pricingGbc.anchor =
                GridBagConstraints.WEST;

        pricingEnabledCheckBox =
                new JCheckBox(
                        "Paid parking"
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

        pricingGbc.gridy =
                0;

        pricingGbc.insets =
                new Insets(
                        0,
                        0,
                        12,
                        0
                );

        pricingPanel.add(
                pricingEnabledCheckBox,
                pricingGbc
        );

        JLabel priceLabel =
                UITheme.fieldLabel(
                        "Price per hour"
                );

        pricingGbc.gridy =
                1;

        pricingGbc.insets =
                new Insets(
                        0,
                        0,
                        6,
                        0
                );

        pricingPanel.add(
                priceLabel,
                pricingGbc
        );

        pricePerHourField =
                new JTextField();

        UITheme.styleTextField(
                pricePerHourField
        );

        pricingGbc.gridy =
                2;

        pricingGbc.insets =
                new Insets(
                        0,
                        0,
                        6,
                        0
                );

        pricingPanel.add(
                pricePerHourField,
                pricingGbc
        );

        priceRuleLabel =
                new JLabel();

        priceRuleLabel.setFont(
                UITheme.SMALL
        );

        priceRuleLabel.setForeground(
                UITheme.MUTED
        );

        pricingGbc.gridy =
                3;

        pricingGbc.insets =
                new Insets(
                        0,
                        0,
                        0,
                        0
                );

        pricingPanel.add(
                priceRuleLabel,
                pricingGbc
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
                pricingPanel,
                gbc
        );

        emergencyBookingCheckBox =
                new JCheckBox(
                        "Allow emergency booking"
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

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        5,
                        0
                );

        card.add(
                emergencyBookingCheckBox,
                gbc
        );

        JLabel emergencyHint =
                new JLabel(
                        "Allows users to book shortly before the reservation start time."
                );

        emergencyHint.setFont(
                UITheme.SMALL
        );

        emergencyHint.setForeground(
                UITheme.MUTED
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
                emergencyHint,
                gbc
        );

        JLabel mapLabel =
                UITheme.fieldLabel(
                        "Select location on map"
                );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        2,
                        0,
                        6,
                        0
                );

        card.add(
                mapLabel,
                gbc
        );

        map =
                new JMapViewer();

        map.setPreferredSize(
                new Dimension(
                        720,
                        260
                )
        );

        map.setDisplayPosition(
                new Coordinate(
                        37.9838,
                        23.7275
                ),
                12
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

                        ICoordinate coordinate =
                                map.getPosition(
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
                                        java.util.Locale.US,
                                        "%.6f",
                                        latitude
                                )
                        );

                        longitudeField.setText(
                                String.format(
                                        java.util.Locale.US,
                                        "%.6f",
                                        longitude
                                )
                        );

                        if (
                                selectedMarker != null
                        ) {

                            map.removeMapMarker(
                                    selectedMarker
                            );
                        }

                        selectedMarker =
                                new MapMarkerDot(
                                        latitude,
                                        longitude
                                );

                        map.addMapMarker(
                                selectedMarker
                        );

                        map.repaint();

                        addressField.setText(
                                "Loading..."
                        );

                        areaField.setText(
                                "Loading..."
                        );

                        reverseGeocode(
                                latitude,
                                longitude
                        );
                    }
                }
        );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        16,
                        0
                );

        card.add(
                map,
                gbc
        );

        JPanel coords =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                12,
                                0
                        )
                );

        coords.setOpaque(
                false
        );

        coords.add(
                labeledField(
                        "Latitude",
                        latitudeField
                )
        );

        coords.add(
                labeledField(
                        "Longitude",
                        longitudeField
                )
        );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        10,
                        0
                );

        card.add(
                coords,
                gbc
        );

        JLabel hint =
                new JLabel(
                        "Click on the map to automatically select the location, address and area."
                );

        hint.setFont(
                UITheme.SMALL
        );

        hint.setForeground(
                UITheme.MUTED
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
                hint,
                gbc
        );

        UITheme.ModernButton addButton =
                UITheme.primaryButton(
                        "Add parking spot"
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
                addButton,
                gbc
        );

        addButton.addActionListener(
                e ->
                        addSpot(
                                dashboardPanel
                        )
        );

        pricingEnabledCheckBox.addActionListener(
                e ->
                        updatePricingControls()
        );

        vehicleTypeComboBox.addActionListener(
                e ->
                        updatePricingControls()
        );

        updatePricingControls();

        center.add(
                card
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        center
                );

        scrollPane.setBorder(
                null
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

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
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
    }

    private GridBagConstraints formConstraints() {

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx =
                0;

        gbc.weightx =
                1;

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        return gbc;
    }

    private int addField(
            JPanel card,
            GridBagConstraints gbc,
            int row,
            String label,
            JTextField field
    ) {

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
                        label
                ),
                gbc
        );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        14,
                        0
                );

        card.add(
                field,
                gbc
        );

        return row;
    }

    private JPanel labeledField(
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

        return panel;
    }

    private void updatePricingControls() {

        boolean enabled =
                pricingEnabledCheckBox
                        .isSelected();

        pricePerHourField.setEnabled(
                enabled
        );

        String vehicle =
                (String)
                        vehicleTypeComboBox
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

            pricePerHourField.setText(
                    ""
            );

            priceRuleLabel.setText(
                    "Leave disabled to offer this parking spot for free."
            );
        }
    }

    private void reverseGeocode(
            double latitude,
            double longitude
    ) {

        new Thread(
                () -> {

                    try {

                        String url =
                                "https://nominatim.openstreetmap.org/reverse"
                                        + "?format=jsonv2"
                                        + "&lat=" + latitude
                                        + "&lon=" + longitude
                                        + "&addressdetails=1"
                                        + "&accept-language=el";

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

    private void addSpot(
            DashboardPanel dashboardPanel
    ) {

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
                        vehicleTypeComboBox
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
                address.equals(
                        "Loading..."
                )
                        ||
                area.equals(
                        "Loading..."
                )
        ) {

            UITheme.showWarning(
                    this,
                    "Missing Information",
                    "Address, area and vehicle type are required."
            );

            return;
        }

        if (
                latitudeField
                        .getText()
                        .trim()
                        .isEmpty()
                        ||
                longitudeField
                        .getText()
                        .trim()
                        .isEmpty()
        ) {

            UITheme.showWarning(
                    this,
                    "Location Required",
                    "Please select the parking location on the map."
            );

            return;
        }

        double lat;
        double lon;

        try {

            lat =
                    Double.parseDouble(
                            latitudeField
                                    .getText()
                                    .trim()
                    );

            lon =
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
                    "Invalid map coordinates."
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
                spotService
                        .addParkingSpot(
                                currentUserId,
                                address,
                                area,
                                description,
                                vehicle,
                                pricingEnabled,
                                pricePerHour,
                                emergencyBookingEnabled,
                                lat,
                                lon
                        );

        if (
                success
        ) {

            String pricingText =
                    pricingEnabled
                            ? "Price: "
                                    +
                            pricingService
                                    .formatPrice(
                                            pricePerHour
                                    )
                                    +
                            " per hour."
                            : "This parking spot is free.";

            String emergencyText =
                    emergencyBookingEnabled
                            ? " Emergency booking is enabled."
                            : "";

            UITheme.showSuccess(
                    this,
                    "Spot Added",
                    "Parking spot added successfully. "
                            +
                    pricingText
                            +
                    emergencyText
            );

            clearFields();

            if (
                    dashboardPanel != null
            ) {

                dashboardPanel
                        .loadMarkersFromDatabase(
                                spotService
                        );
            }

        } else {

            String error =
                    spotService
                            .getLastErrorMessage();

            if (
                    error == null
                            ||
                    error.isBlank()
            ) {

                error =
                        "Failed to add the parking spot.";
            }

            UITheme.showError(
                    this,
                    "Operation Failed",
                    error
            );
        }
    }

    private void clearFields() {

        addressField.setText(
                ""
        );

        areaField.setText(
                ""
        );

        descriptionField.setText(
                ""
        );

        vehicleTypeComboBox.setSelectedItem(
                "Car"
        );

        pricingEnabledCheckBox.setSelected(
                false
        );

        pricePerHourField.setText(
                ""
        );

        emergencyBookingCheckBox.setSelected(
                false
        );

        latitudeField.setText(
                ""
        );

        longitudeField.setText(
                ""
        );

        updatePricingControls();

        if (
                selectedMarker != null
        ) {

            map.removeMapMarker(
                    selectedMarker
            );

            selectedMarker =
                    null;

            map.repaint();
        }
    }
}