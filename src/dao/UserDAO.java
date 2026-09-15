package dao;

import db.DBConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    // =============================================================
    // FIND BY EMAIL AND PASSWORD
    // =============================================================

    public User findByEmailAndPassword(
            String email,
            String password
    ) {

        String sql =
                "SELECT * FROM users " +
                "WHERE email = ? " +
                "AND password = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    email
            );

            ps.setString(
                    2,
                    password
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (
                        rs.next()
                ) {

                    return mapUser(
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
    // FIND BY EMAIL
    // =============================================================

    public User findByEmail(
            String email
    ) {

        String sql =
                "SELECT * FROM users " +
                "WHERE email = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    email
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (
                        rs.next()
                ) {

                    return mapUser(
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
    // FIND BY ID
    // =============================================================

    public User findById(
            int userId
    ) {

        String sql =
                "SELECT * FROM users " +
                "WHERE user_id = ?";

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

                    return mapUser(
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
    // EMAIL EXISTS
    // =============================================================

    public boolean emailExists(
            String email
    ) {

        String sql =
                "SELECT user_id FROM users " +
                "WHERE email = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    email
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                return rs.next();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    // =============================================================
    // CHECK EMAIL USED BY ANOTHER USER
    // =============================================================

    public boolean emailExistsForOtherUser(
            String email,
            int userId
    ) {

        String sql =
                "SELECT user_id FROM users " +
                "WHERE email = ? " +
                "AND user_id <> ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    email
            );

            ps.setInt(
                    2,
                    userId
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                return rs.next();
            }

        } catch (Exception e) {

            e.printStackTrace();

            return true;
        }
    }

    // =============================================================
    // INSERT
    // =============================================================

    public boolean insertUser(
            User user
    ) {

        String sql =
                "INSERT INTO users " +
                "(full_name, email, phone, password, role) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    user.getFullName()
            );

            ps.setString(
                    2,
                    user.getEmail()
            );

            ps.setString(
                    3,
                    user.getPhone()
            );

            ps.setString(
                    4,
                    user.getPassword()
            );

            ps.setString(
                    5,
                    user.getRole()
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    // =============================================================
    // UPDATE PROFILE
    // =============================================================

    public boolean updateProfile(
            int userId,
            String fullName,
            String email,
            String phone
    ) {

        String sql =
                "UPDATE users " +
                "SET full_name = ?, " +
                "email = ?, " +
                "phone = ? " +
                "WHERE user_id = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    fullName
            );

            ps.setString(
                    2,
                    email
            );

            ps.setString(
                    3,
                    phone
            );

            ps.setInt(
                    4,
                    userId
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    // =============================================================
    // RESULTSET -> USER
    // =============================================================

    private User mapUser(
            ResultSet rs
    ) throws Exception {

        return new User(
                rs.getInt(
                        "user_id"
                ),

                rs.getString(
                        "full_name"
                ),

                rs.getString(
                        "email"
                ),

                rs.getString(
                        "phone"
                ),

                rs.getString(
                        "password"
                ),

                rs.getString(
                        "role"
                )
        );
    }

    public boolean updateProfile(
        User user
) {

    String sql =
            "UPDATE users " +
            "SET full_name = ?, email = ?, phone = ? " +
            "WHERE user_id = ?";

    try (
            Connection conn =
                    DBConnection.getConnection();

            PreparedStatement ps =
                    conn.prepareStatement(sql)
    ) {

        ps.setString(
                1,
                user.getFullName()
        );

        ps.setString(
                2,
                user.getEmail()
        );

        ps.setString(
                3,
                user.getPhone()
        );

        ps.setInt(
                4,
                user.getUserId()
        );

        return ps.executeUpdate() > 0;

    } catch (Exception e) {

        e.printStackTrace();

        return false;
    }
}
}