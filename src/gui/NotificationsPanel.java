package gui;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.Notification;
import service.NotificationService;

public class NotificationsPanel extends JPanel {

    private final JTable notificationsTable;
    private final DefaultTableModel tableModel;

    private final NotificationService notificationService;

    private final int currentUserId;

    private final JLabel statusLabel;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    public NotificationsPanel(
            int userId
    ) {

        this.currentUserId =
                userId;

        this.notificationService =
                new NotificationService();

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
                new JPanel(
                        new BorderLayout(
                                20,
                                0
                        )
                );

        north.setOpaque(
                false
        );

        north.add(
                UITheme.pageHeader(
                        "Notifications",
                        "Review reservation, payment and parking activity."
                ),
                BorderLayout.WEST
        );

        JPanel actions =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        actions.setOpaque(
                false
        );

        statusLabel =
                new JLabel(
                        "0 unread"
                );

        statusLabel.setFont(
                UITheme.FONT_BOLD
        );

        statusLabel.setForeground(
                UITheme.PRIMARY
        );

        UITheme.ModernButton refreshButton =
                UITheme.secondaryButton(
                        "Refresh"
                );

        UITheme.ModernButton markReadButton =
                UITheme.primaryButton(
                        "Mark as read"
                );

        actions.add(
                statusLabel
        );

        actions.add(
                Box.createHorizontalStrut(
                        8
                )
        );

        actions.add(
                refreshButton
        );

        actions.add(
                markReadButton
        );

        north.add(
                actions,
                BorderLayout.EAST
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

        String[] columns = {
                "ID",
                "Type",
                "Message",
                "Created At",
                "Status"
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

        notificationsTable =
                new JTable(
                        tableModel
                );

        notificationsTable.setAutoCreateRowSorter(
                true
        );

        notificationsTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        UITheme.styleTable(
                notificationsTable
        );

        notificationsTable
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(
                        45
                );

        notificationsTable
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(
                        115
                );

        notificationsTable
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(
                        500
                );

        notificationsTable
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(
                        150
                );

        notificationsTable
                .getColumnModel()
                .getColumn(4)
                .setPreferredWidth(
                        90
                );

        notificationsTable
                .getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        new TypeRenderer()
                );

        notificationsTable
                .getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new StatusRenderer()
                );

        tableCard.add(
                UITheme.tableScroll(
                        notificationsTable
                ),
                BorderLayout.CENTER
        );

        JPanel centerWrap =
                new JPanel(
                        new BorderLayout()
                );

        centerWrap.setOpaque(
                false
        );

        centerWrap.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        0,
                        0,
                        0
                )
        );

        centerWrap.add(
                tableCard,
                BorderLayout.CENTER
        );

        add(
                centerWrap,
                BorderLayout.CENTER
        );

        refreshButton.addActionListener(
                e -> loadNotifications()
        );

        markReadButton.addActionListener(
                e -> markSelectedAsRead()
        );

        loadNotifications();
    }

    private void markSelectedAsRead() {

        int selectedRow =
                notificationsTable
                        .getSelectedRow();

        if (
                selectedRow == -1
        ) {

            UITheme.showWarning(
                    this,
                    "No Selection",
                    "Select a notification first."
            );

            return;
        }

        int modelRow =
                notificationsTable
                        .convertRowIndexToModel(
                                selectedRow
                        );

        int notificationId =
                (int)
                        tableModel
                                .getValueAt(
                                        modelRow,
                                        0
                                );

        boolean success =
                notificationService
                        .markNotificationAsRead(
                                notificationId
                        );

        if (
                success
        ) {

            loadNotifications();

        } else {

            UITheme.showError(
                    this,
                    "Operation Failed",
                    "Failed to mark the notification as read."
            );
        }
    }

    public final void loadNotifications() {

        tableModel.setRowCount(
                0
        );

        List<Notification> notifications =
                notificationService
                        .getNotificationsByUser(
                                currentUserId
                        );

        int unread =
                0;

        for (
                Notification notification :
                notifications
        ) {

            if (
                    !notification.isRead()
            ) {

                unread++;
            }

            tableModel.addRow(
                    new Object[]{
                            notification.getNotificationId(),

                            getNotificationType(
                                    notification.getMessage()
                            ),

                            notification.getMessage(),

                            notification
                                    .getCreatedAt()
                                    .format(
                                            formatter
                                    ),

                            notification.isRead()
                                    ? "READ"
                                    : "UNREAD"
                    }
            );
        }

        statusLabel.setText(
                unread
                        +
                (
                        unread == 1
                                ? " unread"
                                : " unread"
                )
        );

        statusLabel.setForeground(
                unread > 0
                        ? UITheme.PRIMARY
                        : UITheme.MUTED
        );
    }

    private String getNotificationType(
            String message
    ) {

        if (
                message == null
        ) {

            return "INFO";
        }

        String text =
                message.toLowerCase();

        if (
                text.contains(
                        "πληρω"
                )
                        ||
                text.contains(
                        "paid"
                )
                        ||
                text.contains(
                        "payment"
                )
                        ||
                text.contains(
                        "εξοφ"
                )
        ) {

            return "PAYMENT";
        }

        if (
                text.contains(
                        "ακυρ"
                )
                        ||
                text.contains(
                        "cancel"
                )
        ) {

            return "CANCELLED";
        }

        if (
                text.contains(
                        "προθεσμία"
                )
                        ||
                text.contains(
                        "expired"
                )
                        ||
                text.contains(
                        "έγκαιρα"
                )
        ) {

            return "EXPIRED";
        }

        if (
                text.contains(
                        "κράτηση"
                )
                        ||
                text.contains(
                        "reservation"
                )
        ) {

            return "RESERVATION";
        }

        return "INFO";
    }

    private class TypeRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            JLabel label =
                    (JLabel)
                            super.getTableCellRendererComponent(
                                    table,
                                    value,
                                    isSelected,
                                    hasFocus,
                                    row,
                                    column
                            );

            label.setFont(
                    UITheme.FONT_BOLD
            );

            String type =
                    value == null
                            ? "INFO"
                            : value.toString();

            if (
                    !isSelected
            ) {

                switch (type) {

                    case "PAYMENT":
                        label.setForeground(
                                UITheme.SUCCESS
                        );
                        break;

                    case "CANCELLED":
                        label.setForeground(
                                UITheme.DANGER
                        );
                        break;

                    case "EXPIRED":
                        label.setForeground(
                                UITheme.WARNING
                        );
                        break;

                    case "RESERVATION":
                        label.setForeground(
                                UITheme.PRIMARY
                        );
                        break;

                    default:
                        label.setForeground(
                                UITheme.MUTED
                        );
                        break;
                }
            }

            return label;
        }
    }

    private class StatusRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            JLabel label =
                    (JLabel)
                            super.getTableCellRendererComponent(
                                    table,
                                    value,
                                    isSelected,
                                    hasFocus,
                                    row,
                                    column
                            );

            label.setFont(
                    UITheme.FONT_BOLD
            );

            String status =
                    value == null
                            ? ""
                            : value.toString();

            if (
                    !isSelected
            ) {

                if (
                        "UNREAD".equals(
                                status
                        )
                ) {

                    label.setForeground(
                            UITheme.PRIMARY
                    );

                } else {

                    label.setForeground(
                            UITheme.MUTED
                    );
                }
            }

            return label;
        }
    }
}