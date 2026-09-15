package dao;

import db.DBConnection;
import model.Availability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class AvailabilityDAO {

    // =============================================================
    // INSERT AVAILABILITY
    // =============================================================

    public boolean insertAvailability(
            Availability availability
    ) {

        String sql =
                "INSERT INTO availabilities " +
                "(spot_id, start_datetime, end_datetime, status) " +
                "VALUES (?, ?, ?, ?)";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    availability.getSpotId()
            );

            ps.setTimestamp(
                    2,
                    Timestamp.valueOf(
                            availability.getStartDatetime()
                    )
            );

            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            availability.getEndDatetime()
                    )
            );

            ps.setString(
                    4,
                    availability.getStatus()
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    // =============================================================
    // FIND BY ID
    // =============================================================

    public Availability findById(
            int availabilityId
    ) {

        String sql =
                "SELECT * FROM availabilities " +
                "WHERE availability_id = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    availabilityId
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return mapAvailability(
                            rs
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    // =============================================================
    // FIND BY SPOT
    // =============================================================

    public List<Availability> findBySpotId(
            int spotId
    ) {

        List<Availability> availabilities =
                new ArrayList<>();

        String sql =
                "SELECT * FROM availabilities " +
                "WHERE spot_id = ? " +
                "ORDER BY start_datetime DESC";

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

                while (rs.next()) {

                    availabilities.add(
                            mapAvailability(
                                    rs
                            )
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return availabilities;
    }

    // =============================================================
    // FIND ALL AVAILABILITIES OF OWNER
    // =============================================================

    public List<Availability> findByOwnerId(
            int ownerId
    ) {

        List<Availability> availabilities =
                new ArrayList<>();

        String sql =
                "SELECT a.* " +
                "FROM availabilities a " +
                "INNER JOIN parking_spots p " +
                "ON a.spot_id = p.spot_id " +
                "WHERE p.owner_id = ? " +
                "ORDER BY a.start_datetime DESC";

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

                while (rs.next()) {

                    availabilities.add(
                            mapAvailability(
                                    rs
                            )
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return availabilities;
    }

    // =============================================================
    // FIND AVAILABLE BY SPOT
    // =============================================================

    public List<Availability> findAvailableBySpotId(
            int spotId
    ) {

        List<Availability> availabilities =
                new ArrayList<>();

        String sql =
                "SELECT * FROM availabilities " +
                "WHERE spot_id = ? " +
                "AND status = 'AVAILABLE' " +
                "ORDER BY start_datetime ASC";

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

                while (rs.next()) {

                    availabilities.add(
                            mapAvailability(
                                    rs
                            )
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return availabilities;
    }

    // =============================================================
    // CANCEL AVAILABILITY
    // =============================================================

    public boolean cancelAvailability(
            int availabilityId
    ) {

        String sql =
                "UPDATE availabilities " +
                "SET status = 'CANCELLED' " +
                "WHERE availability_id = ? " +
                "AND status = 'AVAILABLE'";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    availabilityId
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    // =============================================================
    // CHECK OVERLAPPING AVAILABILITY
    // =============================================================

    public boolean hasOverlappingAvailability(
            int spotId,
            java.time.LocalDateTime start,
            java.time.LocalDateTime end
    ) {

        String sql =
                "SELECT availability_id " +
                "FROM availabilities " +
                "WHERE spot_id = ? " +
                "AND status = 'AVAILABLE' " +
                "AND start_datetime < ? " +
                "AND end_datetime > ?";

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

            ps.setTimestamp(
                    2,
                    Timestamp.valueOf(
                            end
                    )
            );

            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            start
                    )
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                return rs.next();
            }

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    // =============================================================
    // CHECK IF SPOT AVAILABLE FOR PERIOD
    // =============================================================

    public boolean isSpotAvailableForPeriod(
            int spotId,
            java.time.LocalDateTime start,
            java.time.LocalDateTime end
    ) {

        String sql =
                "SELECT availability_id " +
                "FROM availabilities " +
                "WHERE spot_id = ? " +
                "AND status = 'AVAILABLE' " +
                "AND start_datetime <= ? " +
                "AND end_datetime >= ?";

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

            ps.setTimestamp(
                    2,
                    Timestamp.valueOf(
                            start
                    )
            );

            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            end
                    )
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                return rs.next();
            }

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    // =============================================================
    // RESULTSET -> AVAILABILITY
    // =============================================================

    private Availability mapAvailability(
            ResultSet rs
    ) throws Exception {

        return new Availability(
                rs.getInt(
                        "availability_id"
                ),

                rs.getInt(
                        "spot_id"
                ),

                rs.getTimestamp(
                        "start_datetime"
                ).toLocalDateTime(),

                rs.getTimestamp(
                        "end_datetime"
                ).toLocalDateTime(),

                rs.getString(
                        "status"
                )
        );
    }
}