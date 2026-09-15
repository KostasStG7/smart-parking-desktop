package service;

import dao.NotificationDAO;
import model.Notification;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private final NotificationDAO notificationDAO =
            new NotificationDAO();

    // =============================================================
    // CREATE NOTIFICATION
    // =============================================================

    public boolean createNotification(
            int userId,
            String message
    ) {

        if (
                userId <= 0
        ) {

            System.out.println(
                    "Μη έγκυρο ID χρήστη για ειδοποίηση."
            );

            return false;
        }

        if (
                message == null
                        ||
                message.trim().isEmpty()
        ) {

            System.out.println(
                    "Το μήνυμα ειδοποίησης δεν μπορεί να είναι κενό."
            );

            return false;
        }

        Notification notification =
                new Notification(
                        userId,
                        message.trim(),
                        LocalDateTime.now(),
                        false
                );

        return notificationDAO
                .insertNotification(
                        notification
                );
    }

    // =============================================================
    // GET USER NOTIFICATIONS
    // =============================================================

    public List<Notification> getNotificationsByUser(
            int userId
    ) {

        if (
                userId <= 0
        ) {

            return new ArrayList<>();
        }

        return notificationDAO
                .findByUserId(
                        userId
                );
    }

    // =============================================================
    // GET UNREAD COUNT
    // =============================================================

    public int getUnreadCount(
            int userId
    ) {

        if (
                userId <= 0
        ) {

            return 0;
        }

        return notificationDAO
                .countUnreadByUserId(
                        userId
                );
    }

    // =============================================================
    // MARK AS READ
    // =============================================================

    public boolean markNotificationAsRead(
            int notificationId
    ) {

        if (
                notificationId <= 0
        ) {

            return false;
        }

        return notificationDAO
                .markAsRead(
                        notificationId
                );
    }
}