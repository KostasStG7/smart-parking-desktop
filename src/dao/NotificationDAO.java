package dao;

import db.DBConnection;
import model.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // =============================================================
    // INSERT NOTIFICATION
    // =============================================================

    public boolean insertNotification(
            Notification notification
    ) {

        String sql =
                "INSERT INTO notifications " +
                "(user_id, message, created_at, is_read) " +
                "VALUES (?, ?, ?, ?)";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    notification.getUserId()
            );

            ps.setString(
                    2,
                    notification.getMessage()
            );

            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            notification.getCreatedAt()
                    )
            );

            ps.setBoolean(
                    4,
                    notification.isRead()
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    // =============================================================
    // FIND BY USER
    // =============================================================

    public List<Notification> findByUserId(
            int userId
    ) {

        List<Notification> notifications =
                new ArrayList<>();

        String sql =
                "SELECT * FROM notifications " +
                "WHERE user_id = ? " +
                "ORDER BY created_at DESC";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    userId
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (
                        rs.next()
                ) {

                    Notification notification =
                            new Notification(
                                    rs.getInt(
                                            "notification_id"
                                    ),

                                    rs.getInt(
                                            "user_id"
                                    ),

                                    rs.getString(
                                            "message"
                                    ),

                                    rs.getTimestamp(
                                            "created_at"
                                    ).toLocalDateTime(),

                                    rs.getBoolean(
                                            "is_read"
                                    )
                            );

                    notifications.add(
                            notification
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return notifications;
    }

    // =============================================================
    // COUNT UNREAD
    // =============================================================

    public int countUnreadByUserId(
            int userId
    ) {

        String sql =
                "SELECT COUNT(*) AS unread_count " +
                "FROM notifications " +
                "WHERE user_id = ? " +
                "AND is_read = FALSE";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    userId
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (
                        rs.next()
                ) {

                    return rs.getInt(
                            "unread_count"
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 0;
    }

    // =============================================================
    // MARK AS READ
    // =============================================================

    public boolean markAsRead(
            int notificationId
    ) {

        String sql =
                "UPDATE notifications " +
                "SET is_read = TRUE " +
                "WHERE notification_id = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    notificationId
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }
}