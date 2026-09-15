package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

import java.util.List;

import service.ParkingSpotService;
import service.PricingService;

import model.ParkingSpot;

public class SearchParkingSpotPanel extends JPanel {

    private final JTextField areaField;

    private final JComboBox<String> vehicleFilter;

    private final JTable resultTable;
    private final DefaultTableModel tableModel;

    private final ParkingSpotService spotService;
    private final PricingService pricingService;

    private final JLabel resultCountLabel;

    public SearchParkingSpotPanel() {

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
                UITheme.pageHeader(
                        "Search Parking Spots",
                        "Browse active parking spots and filter them by area and vehicle type."
                )
        );

        north.add(
                Box.createVerticalStrut(
                        20
                )
        );

        UITheme.RoundedPanel searchCard =
                new UITheme.RoundedPanel(
                        16,
                        Color.WHITE
                );

        searchCard.setOutlineColor(
                UITheme.BORDER
        );

        searchCard.setLayout(
                new BorderLayout(
                        14,
                        0
                )
        );

        searchCard.setBorder(
                BorderFactory.createEmptyBorder(
                        16,
                        18,
                        16,
                        18
                )
        );

        JPanel fields =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        fields.setOpaque(
                false
        );

        fields.add(
                UITheme.fieldLabel(
                        "Area"
                )
        );

        areaField =
                new JTextField();

        UITheme.styleTextField(
                areaField
        );

        areaField.setToolTipText(
                "Example: Athens"
        );

        areaField.setPreferredSize(
                new Dimension(
                        250,
                        42
                )
        );

        fields.add(
                areaField
        );

        fields.add(
                Box.createHorizontalStrut(
                        8
                )
        );

        fields.add(
                UITheme.fieldLabel(
                        "Vehicle"
                )
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

        vehicleFilter.setPreferredSize(
                new Dimension(
                        150,
                        40
                )
        );

        fields.add(
                vehicleFilter
        );

        JPanel buttons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        buttons.setOpaque(
                false
        );

        UITheme.ModernButton clearButton =
                UITheme.secondaryButton(
                        "Show all"
                );

        UITheme.ModernButton searchButton =
                UITheme.primaryButton(
                        "Search"
                );

        buttons.add(
                clearButton
        );

        buttons.add(
                searchButton
        );

        searchCard.add(
                fields,
                BorderLayout.CENTER
        );

        searchCard.add(
                buttons,
                BorderLayout.EAST
        );

        north.add(
                searchCard
        );

        north.add(
                Box.createVerticalStrut(
                        16
                )
        );

        add(
                north,
                BorderLayout.NORTH
        );

        UITheme.RoundedPanel tableCard =
                new UITheme.RoundedPanel(
                        16,
                        Color.WHITE
                );

        tableCard.setOutlineColor(
                UITheme.BORDER
        );

        tableCard.setLayout(
                new BorderLayout()
        );

        tableCard.setBorder(
                BorderFactory.createEmptyBorder(
                        16,
                        16,
                        16,
                        16
                )
        );

        JPanel tableHeader =
                new JPanel(
                        new BorderLayout()
                );

        tableHeader.setOpaque(
                false
        );

        JLabel heading =
                new JLabel(
                        "Available parking spots"
                );

        heading.setFont(
                UITheme.SECTION
        );

        heading.setForeground(
                UITheme.TEXT
        );

        resultCountLabel =
                new JLabel(
                        "0 spots"
                );

        resultCountLabel.setFont(
                UITheme.FONT_BOLD
        );

        resultCountLabel.setForeground(
                UITheme.PRIMARY
        );

        tableHeader.add(
                heading,
                BorderLayout.WEST
        );

        tableHeader.add(
                resultCountLabel,
                BorderLayout.EAST
        );

        tableHeader.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        2,
                        12,
                        2
                )
        );

        String[] columns = {
                "Spot ID",
                "Address",
                "Area",
                "Vehicle",
                "Price",
                "Emergency Booking"
        };

        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return false;
                    }
                };

        resultTable =
                new JTable(
                        tableModel
                );

        resultTable.setAutoCreateRowSorter(
                true
        );

        resultTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        UITheme.styleTable(
                resultTable
        );

        resultTable
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(
                        65
                );

        resultTable
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(
                        220
                );

        resultTable
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(
                        140
                );

        resultTable
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(
                        100
                );

        resultTable
                .getColumnModel()
                .getColumn(4)
                .setPreferredWidth(
                        110
                );

        resultTable
                .getColumnModel()
                .getColumn(5)
                .setPreferredWidth(
                        150
                );

        tableCard.add(
                tableHeader,
                BorderLayout.NORTH
        );

        tableCard.add(
                UITheme.tableScroll(
                        resultTable
                ),
                BorderLayout.CENTER
        );

        add(
                tableCard,
                BorderLayout.CENTER
        );

        searchButton.addActionListener(
                e -> performSearch()
        );

        areaField.addActionListener(
                e -> performSearch()
        );

        vehicleFilter.addActionListener(
                e -> performSearch()
        );

        clearButton.addActionListener(
                e -> {

                    areaField.setText(
                            ""
                    );

                    vehicleFilter.setSelectedItem(
                            "All"
                    );

                    refreshAllSpots();
                }
        );

        refreshAllSpots();
    }

    private void performSearch() {

        String area =
                areaField
                        .getText()
                        .trim();

        String vehicle =
                (String)
                        vehicleFilter
                                .getSelectedItem();

        List<ParkingSpot> spots =
                area.isEmpty()
                        ? spotService.getAllActiveSpots()
                        : spotService.searchByArea(
                                area
                        );

        if (
                vehicle != null
                        &&
                !"All".equals(
                        vehicle
                )
        ) {

            spots =
                    spots
                            .stream()
                            .filter(
                                    spot ->
                                            vehicle.equalsIgnoreCase(
                                                    spot.getVehicleType()
                                            )
                            )
                            .toList();
        }

        loadSpots(
                spots
        );

        if (
                spots.isEmpty()
        ) {

            UITheme.showInfo(
                    this,
                    "No Results",
                    "No active parking spots matched your search."
            );
        }
    }

    public void refreshAllSpots() {

        loadSpots(
                spotService
                        .getAllActiveSpots()
        );
    }

    private void loadSpots(
            List<ParkingSpot> spots
    ) {

        tableModel.setRowCount(
                0
        );

        for (
                ParkingSpot spot :
                spots
        ) {

            String priceText;

            if (
                    spot.isPricingEnabled()
                            &&
                    spot.getPricePerHour() != null
            ) {

                priceText =
                        pricingService
                                .formatPrice(
                                        spot.getPricePerHour()
                                )
                                +
                        "/hour";

            } else {

                priceText =
                        "Free";
            }

            String emergencyText =
                    spot.isEmergencyBookingEnabled()
                            ? "Yes"
                            : "No";

            tableModel.addRow(
                    new Object[]{
                            spot.getSpotId(),
                            safeText(
                                    spot.getAddress()
                            ),
                            safeText(
                                    spot.getArea()
                            ),
                            safeText(
                                    spot.getVehicleType()
                            ),
                            priceText,
                            emergencyText
                    }
            );
        }

        resultCountLabel.setText(
                spots.size()
                        +
                (
                        spots.size() == 1
                                ? " spot"
                                : " spots"
                )
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
}