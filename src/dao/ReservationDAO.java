package dao;

import db.DBConnection;
import model.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public boolean insertReservation(
            Reservation reservation
    ) {

        String sql =
                "INSERT INTO reservations " +
                "(spot_id, driver_id, start_datetime, end_datetime, status, " +
                "calculated_price, payment_status, payment_deadline, pricing_accepted, booking_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    reservation.getSpotId()
            );

            ps.setInt(
                    2,
                    reservation.getDriverId()
            );

            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            reservation.getStartDatetime()
                    )
            );

            ps.setTimestamp(
                    4,
                    Timestamp.valueOf(
                            reservation.getEndDatetime()
                    )
            );

            ps.setString(
                    5,
                    reservation.getStatus()
            );

            ps.setDouble(
                    6,
                    reservation.getCalculatedPrice()
            );

            ps.setString(
                    7,
                    reservation.getPaymentStatus()
            );

            if (
                    reservation.getPaymentDeadline() == null
            ) {

                ps.setNull(
                        8,
                        Types.TIMESTAMP
                );

            } else {

                ps.setTimestamp(
                        8,
                        Timestamp.valueOf(
                                reservation.getPaymentDeadline()
                        )
                );
            }

            ps.setBoolean(
                    9,
                    reservation.isPricingAccepted()
            );

            ps.setString(
                    10,
                    reservation.getBookingType()
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    public List<Reservation> findByDriverId(
            int driverId
    ) {

        List<Reservation> reservations =
                new ArrayList<>();

        String sql =
                "SELECT * FROM reservations " +
                "WHERE driver_id = ? " +
                "ORDER BY start_datetime DESC";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    driverId
            );

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                while (
                        rs.next()
                ) {

                    reservations.add(
                            mapReservation(
                                    rs
                            )
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return reservations;
    }

    public List<Reservation> findBySpotId(
            int spotId
    ) {

        List<Reservation> reservations =
                new ArrayList<>();

        String sql =
                "SELECT * FROM reservations " +
                "WHERE spot_id = ? " +
                "ORDER BY start_datetime DESC";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    spotId
            );

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                while (
                        rs.next()
                ) {

                    reservations.add(
                            mapReservation(
                                    rs
                            )
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return reservations;
    }

    public Reservation findById(
            int reservationId
    ) {

        String sql =
                "SELECT * FROM reservations " +
                "WHERE reservation_id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    reservationId
            );

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                if (
                        rs.next()
                ) {

                    return mapReservation(
                            rs
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    public boolean cancelReservation(
            int reservationId,
            int driverId
    ) {

        String sql =
                "UPDATE reservations " +
                "SET status = 'CANCELLED' " +
                "WHERE reservation_id = ? " +
                "AND driver_id = ? " +
                "AND status IN ('CONFIRMED', 'PENDING_PAYMENT')";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    reservationId
            );

            ps.setInt(
                    2,
                    driverId
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    public boolean cancelReservationWithRefund(
            int reservationId,
            int driverId
    ) {

        String sql =
                "UPDATE reservations " +
                "SET status = 'CANCELLED', " +
                "payment_status = 'REFUNDED' " +
                "WHERE reservation_id = ? " +
                "AND driver_id = ? " +
                "AND status = 'CONFIRMED' " +
                "AND payment_status = 'PAID'";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    reservationId
            );

            ps.setInt(
                    2,
                    driverId
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    public boolean hasOverlappingReservation(
            int spotId,
            LocalDateTime start,
            LocalDateTime end
    ) {

        String sql =
                "SELECT reservation_id " +
                "FROM reservations " +
                "WHERE spot_id = ? " +
                "AND status IN ('CONFIRMED', 'PENDING_PAYMENT') " +
                "AND start_datetime < ? " +
                "AND end_datetime > ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
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
                    ResultSet rs = ps.executeQuery()
            ) {

                return rs.next();
            }

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    public boolean markAsPaid(
            int reservationId,
            int driverId
    ) {

        String sql =
                "UPDATE reservations " +
                "SET payment_status = 'PAID', " +
                "status = 'CONFIRMED' " +
                "WHERE reservation_id = ? " +
                "AND driver_id = ? " +
                "AND status = 'PENDING_PAYMENT' " +
                "AND payment_status = 'UNPAID'";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    reservationId
            );

            ps.setInt(
                    2,
                    driverId
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    public List<Reservation> findExpiredUnpaidReservations(
            LocalDateTime now
    ) {

        List<Reservation> reservations =
                new ArrayList<>();

        String sql =
                "SELECT * FROM reservations " +
                "WHERE status = 'PENDING_PAYMENT' " +
                "AND payment_status = 'UNPAID' " +
                "AND payment_deadline IS NOT NULL " +
                "AND payment_deadline <= ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setTimestamp(
                    1,
                    Timestamp.valueOf(
                            now
                    )
            );

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                while (
                        rs.next()
                ) {

                    reservations.add(
                            mapReservation(
                                    rs
                            )
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return reservations;
    }

    public boolean expireReservation(
            int reservationId
    ) {

        String sql =
                "UPDATE reservations " +
                "SET status = 'CANCELLED', " +
                "payment_status = 'EXPIRED' " +
                "WHERE reservation_id = ? " +
                "AND status = 'PENDING_PAYMENT' " +
                "AND payment_status = 'UNPAID'";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    reservationId
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    private Reservation mapReservation(
            ResultSet rs
    ) throws Exception {

        Timestamp deadline =
                rs.getTimestamp(
                        "payment_deadline"
                );

        return new Reservation(
                rs.getInt(
                        "reservation_id"
                ),

                rs.getInt(
                        "spot_id"
                ),

                rs.getInt(
                        "driver_id"
                ),

                rs.getTimestamp(
                        "start_datetime"
                ).toLocalDateTime(),

                rs.getTimestamp(
                        "end_datetime"
                ).toLocalDateTime(),

                rs.getString(
                        "status"
                ),

                rs.getDouble(
                        "calculated_price"
                ),

                rs.getString(
                        "payment_status"
                ),

                deadline == null
                        ? null
                        : deadline.toLocalDateTime(),

                rs.getBoolean(
                        "pricing_accepted"
                ),

                rs.getString(
                        "booking_type"
                )
        );
    }
}