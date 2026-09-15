package dao;

import db.DBConnection;
import model.ParkingSpot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class ParkingSpotDAO {

    public boolean insertParkingSpot(
            ParkingSpot spot
    ) {

        String sql =
                "INSERT INTO parking_spots " +
                "(owner_id, address, area, description, vehicle_type, pricing_enabled, price_per_hour, emergency_booking_enabled, latitude, longitude, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    spot.getOwnerId()
            );

            ps.setString(
                    2,
                    spot.getAddress()
            );

            ps.setString(
                    3,
                    spot.getArea()
            );

            ps.setString(
                    4,
                    spot.getDescription()
            );

            ps.setString(
                    5,
                    spot.getVehicleType()
            );

            ps.setBoolean(
                    6,
                    spot.isPricingEnabled()
            );

            if (
                    spot.getPricePerHour() == null
            ) {

                ps.setNull(
                        7,
                        Types.DECIMAL
                );

            } else {

                ps.setDouble(
                        7,
                        spot.getPricePerHour()
                );
            }

            ps.setBoolean(
                    8,
                    spot.isEmergencyBookingEnabled()
            );

            ps.setDouble(
                    9,
                    spot.getLatitude()
            );

            ps.setDouble(
                    10,
                    spot.getLongitude()
            );

            ps.setBoolean(
                    11,
                    spot.isActive()
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    public ParkingSpot findById(
            int spotId
    ) {

        String sql =
                "SELECT * " +
                "FROM parking_spots " +
                "WHERE spot_id = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    spotId
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (
                        rs.next()
                ) {

                    return mapResultSetToParkingSpot(
                            rs
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    public List<ParkingSpot> findByOwnerId(
            int ownerId
    ) {

        List<ParkingSpot> spots =
                new ArrayList<>();

        String sql =
                "SELECT * " +
                "FROM parking_spots " +
                "WHERE owner_id = ? " +
                "ORDER BY spot_id DESC";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    ownerId
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (
                        rs.next()
                ) {

                    spots.add(
                            mapResultSetToParkingSpot(
                                    rs
                            )
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return spots;
    }

    public List<ParkingSpot> findByArea(
            String area
    ) {

        List<ParkingSpot> spots =
                new ArrayList<>();

        String sql =
                "SELECT * " +
                "FROM parking_spots " +
                "WHERE LOWER(area) LIKE LOWER(?) " +
                "AND is_active = TRUE " +
                "ORDER BY address ASC";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    "%" + area.trim() + "%"
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (
                        rs.next()
                ) {

                    spots.add(
                            mapResultSetToParkingSpot(
                                    rs
                            )
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return spots;
    }

    public List<ParkingSpot> findAllActive() {

        List<ParkingSpot> spots =
                new ArrayList<>();

        String sql =
                "SELECT * " +
                "FROM parking_spots " +
                "WHERE is_active = TRUE " +
                "ORDER BY address ASC";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (
                    rs.next()
            ) {

                spots.add(
                        mapResultSetToParkingSpot(
                                rs
                        )
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return spots;
    }

    public List<ParkingSpot> searchAvailableSpots(
            String area,
            String vehicleType,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime
    ) {

        List<ParkingSpot> spots =
                new ArrayList<>();

        String sql =
                "SELECT p.* " +
                "FROM parking_spots p " +
                "WHERE p.is_active = TRUE " +
                "AND (? = '' OR LOWER(p.area) LIKE LOWER(?)) " +
                "AND (? = 'All' OR LOWER(p.vehicle_type) = LOWER(?)) " +

                "AND EXISTS (" +
                "SELECT 1 " +
                "FROM availabilities a " +
                "WHERE a.spot_id = p.spot_id " +
                "AND a.status = 'AVAILABLE' " +
                "AND a.start_datetime <= ? " +
                "AND a.end_datetime >= ?" +
                ") " +

                "AND NOT EXISTS (" +
                "SELECT 1 " +
                "FROM reservations r " +
                "WHERE r.spot_id = p.spot_id " +
                "AND r.status IN ('CONFIRMED', 'PENDING_PAYMENT') " +
                "AND r.start_datetime < ? " +
                "AND r.end_datetime > ?" +
                ") " +

                "ORDER BY p.area ASC, p.address ASC";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            String safeArea =
                    area == null
                            ? ""
                            : area.trim();

            String safeVehicle =
                    vehicleType == null
                            ? "All"
                            : vehicleType.trim();

            ps.setString(
                    1,
                    safeArea
            );

            ps.setString(
                    2,
                    "%" + safeArea + "%"
            );

            ps.setString(
                    3,
                    safeVehicle
            );

            ps.setString(
                    4,
                    safeVehicle
            );

            ps.setTimestamp(
                    5,
                    Timestamp.valueOf(
                            startDatetime
                    )
            );

            ps.setTimestamp(
                    6,
                    Timestamp.valueOf(
                            endDatetime
                    )
            );

            ps.setTimestamp(
                    7,
                    Timestamp.valueOf(
                            endDatetime
                    )
            );

            ps.setTimestamp(
                    8,
                    Timestamp.valueOf(
                            startDatetime
                    )
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (
                        rs.next()
                ) {

                    spots.add(
                            mapResultSetToParkingSpot(
                                    rs
                            )
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return spots;
    }

    public boolean updateActiveStatus(
            int spotId,
            int ownerId,
            boolean active
    ) {

        String sql =
                "UPDATE parking_spots " +
                "SET is_active = ? " +
                "WHERE spot_id = ? " +
                "AND owner_id = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setBoolean(
                    1,
                    active
            );

            ps.setInt(
                    2,
                    spotId
            );

            ps.setInt(
                    3,
                    ownerId
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    public boolean updateParkingSpot(
            ParkingSpot spot
    ) {

        String sql =
                "UPDATE parking_spots " +
                "SET address = ?, " +
                "area = ?, " +
                "description = ?, " +
                "vehicle_type = ?, " +
                "pricing_enabled = ?, " +
                "price_per_hour = ?, " +
                "emergency_booking_enabled = ?, " +
                "latitude = ?, " +
                "longitude = ? " +
                "WHERE spot_id = ? " +
                "AND owner_id = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    spot.getAddress()
            );

            ps.setString(
                    2,
                    spot.getArea()
            );

            ps.setString(
                    3,
                    spot.getDescription()
            );

            ps.setString(
                    4,
                    spot.getVehicleType()
            );

            ps.setBoolean(
                    5,
                    spot.isPricingEnabled()
            );

            if (
                    spot.getPricePerHour() == null
            ) {

                ps.setNull(
                        6,
                        Types.DECIMAL
                );

            } else {

                ps.setDouble(
                        6,
                        spot.getPricePerHour()
                );
            }

            ps.setBoolean(
                    7,
                    spot.isEmergencyBookingEnabled()
            );

            ps.setDouble(
                    8,
                    spot.getLatitude()
            );

            ps.setDouble(
                    9,
                    spot.getLongitude()
            );

            ps.setInt(
                    10,
                    spot.getSpotId()
            );

            ps.setInt(
                    11,
                    spot.getOwnerId()
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    private ParkingSpot mapResultSetToParkingSpot(
            ResultSet rs
    ) throws Exception {

        ParkingSpot spot =
                new ParkingSpot();

        spot.setSpotId(
                rs.getInt(
                        "spot_id"
                )
        );

        spot.setOwnerId(
                rs.getInt(
                        "owner_id"
                )
        );

        spot.setAddress(
                rs.getString(
                        "address"
                )
        );

        spot.setArea(
                rs.getString(
                        "area"
                )
        );

        spot.setDescription(
                rs.getString(
                        "description"
                )
        );

        spot.setVehicleType(
                rs.getString(
                        "vehicle_type"
                )
        );

        spot.setPricingEnabled(
                rs.getBoolean(
                        "pricing_enabled"
                )
        );

        double price =
                rs.getDouble(
                        "price_per_hour"
                );

        if (
                rs.wasNull()
        ) {

            spot.setPricePerHour(
                    null
            );

        } else {

            spot.setPricePerHour(
                    price
            );
        }

        spot.setEmergencyBookingEnabled(
                rs.getBoolean(
                        "emergency_booking_enabled"
                )
        );

        spot.setLatitude(
                rs.getDouble(
                        "latitude"
                )
        );

        spot.setLongitude(
                rs.getDouble(
                        "longitude"
                )
        );

        spot.setActive(
                rs.getBoolean(
                        "is_active"
                )
        );

        return spot;
    }
}