package gui;

import javax.swing.*;
import org.openstreetmap.gui.jmapviewer.*;
import model.ParkingSpot;
import java.awt.*;
import java.util.List;

public class MapPanel extends JPanel {

    private JMapViewer map;

    public MapPanel() {
        setLayout(new BorderLayout());
        initializeMap();
    }

    private void initializeMap() {
        map = new JMapViewer();
        map.setZoomControlsVisible(true);

        // Αρχικό center (π.χ. Αθήνα)
        Coordinate center = new Coordinate(37.9838, 23.7275);
        map.setDisplayPosition(center, 10);

        add(map, BorderLayout.CENTER);
    }

    public void loadParkingSpots(List<ParkingSpot> spots) {
        if (spots == null || spots.isEmpty()) return;

        for (ParkingSpot spot : spots) {
            if (spot.getLatitude() != null && spot.getLongitude() != null) {
                Coordinate coord = new Coordinate(
                        spot.getLatitude().doubleValue(),
                        spot.getLongitude().doubleValue()
                );
                MapMarkerDot marker = new MapMarkerDot(spot.getAddress(), coord);
                map.addMapMarker(marker);
            }
        }
    }

    public JMapViewer getMap() {
        return map;
    }
}