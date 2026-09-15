package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public final class UITheme {

    private UITheme() {}

    public static final Color BACKGROUND = new Color(246, 248, 252);
    public static final Color CARD = Color.WHITE;
    public static final Color SIDEBAR = new Color(15, 23, 42);
    public static final Color SIDEBAR_ACTIVE = new Color(30, 41, 59);
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    public static final Color PRIMARY_SOFT = new Color(239, 246, 255);
    public static final Color TEXT = new Color(17, 24, 39);
    public static final Color MUTED = new Color(100, 116, 139);
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color SUCCESS_SOFT = new Color(240, 253, 244);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color DANGER_SOFT = new Color(254, 242, 242);
    public static final Color WARNING = new Color(217, 119, 6);
    public static final Color WARNING_SOFT = new Color(255, 251, 235);

    public static final Font FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SECTION = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    public static void applyGlobalStyle() {
        UIManager.put("Label.font", FONT);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("TextField.font", FONT);
        UIManager.put("PasswordField.font", FONT);
        UIManager.put("ComboBox.font", FONT);
        UIManager.put("Table.font", FONT);
        UIManager.put("TableHeader.font", FONT_BOLD);
        UIManager.put("OptionPane.messageFont", FONT);
        UIManager.put("OptionPane.buttonFont", FONT_BOLD);
        UIManager.put("ToolTip.font", SMALL);
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE);
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel subtitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUBTITLE);
        label.setForeground(MUTED);
        return label;
    }

    public static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BOLD);
        label.setForeground(TEXT);
        return label;
    }

    public static JPanel pageHeader(String title, String subtitle) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel titleLabel = title(title);
        JLabel subtitleLabel = subtitle(subtitle);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitleLabel);
        return panel;
    }

    public static void styleTextField(JTextField field) {
        field.setFont(FONT);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(280, 42));
        field.setMinimumSize(new Dimension(180, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)
        ));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT);
        comboBox.setPreferredSize(new Dimension(170, 40));
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT);
        table.setForeground(TEXT);
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(241, 245, 249));
        table.setRowHeight(42);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(PRIMARY_SOFT);
        table.setSelectionForeground(TEXT);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setForeground(new Color(71, 85, 105));
        header.setBackground(new Color(248, 250, 252));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 42));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
    }

    public static JScrollPane tableScroll(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    public static ModernButton primaryButton(String text) {
        return new ModernButton(text, PRIMARY, PRIMARY_HOVER, Color.WHITE);
    }

    public static ModernButton secondaryButton(String text) {
        ModernButton button = new ModernButton(text, Color.WHITE, new Color(248, 250, 252), TEXT);
        button.setOutline(BORDER);
        return button;
    }

    public static ModernButton dangerButton(String text) {
        return new ModernButton(text, DANGER, new Color(185, 28, 28), Color.WHITE);
    }

    public static JButton sidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(new Color(203, 213, 225));
        button.setBackground(SIDEBAR);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 12));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setPreferredSize(new Dimension(240, 50));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void setSidebarActive(JButton button, boolean active) {
        button.setBackground(active ? SIDEBAR_ACTIVE : SIDEBAR);
        button.setForeground(active ? Color.WHITE : new Color(203, 213, 225));
    }

    public static JPanel infoChip(String text, Color foreground, Color background) {
        JPanel panel = new RoundedPanel(14, background);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel label = new JLabel(text);
        label.setFont(SMALL);
        label.setForeground(foreground);
        panel.add(label);
        return panel;
    }

    public static void showInfo(Component parent, String title, String message) {
        showMessageDialog(parent, title, message, "i", PRIMARY, PRIMARY_SOFT);
    }

    public static void showSuccess(Component parent, String title, String message) {
        showMessageDialog(parent, title, message, "✓", SUCCESS, SUCCESS_SOFT);
    }

    public static void showWarning(Component parent, String title, String message) {
        showMessageDialog(parent, title, message, "!", WARNING, WARNING_SOFT);
    }

    public static void showError(Component parent, String title, String message) {
        showMessageDialog(parent, title, message, "!", DANGER, DANGER_SOFT);
    }

    private static void showMessageDialog(
            Component parent,
            String title,
            String message,
            String iconText,
            Color iconColor,
            Color iconBackground
    ) {
        JDialog dialog = createDialog(parent, title);

        RoundedPanel card = dialogCard();
        card.setLayout(new BorderLayout(20, 22));

        card.add(messageContent(title, message, iconText, iconColor, iconBackground), BorderLayout.CENTER);

        JPanel buttons = buttonRow();
        ModernButton okButton = primaryButton("OK");
        okButton.addActionListener(e -> dialog.dispose());
        buttons.add(okButton);
        card.add(buttons, BorderLayout.SOUTH);

        finishDialog(dialog, card, parent, 480, 225);
    }

    public static int showChoiceDialog(
            Component parent,
            String title,
            String message,
            String primaryText,
            String secondaryText,
            String cancelText
    ) {
        final int[] result = {-1};
        JDialog dialog = createDialog(parent, title);

        RoundedPanel card = dialogCard();
        card.setLayout(new BorderLayout(20, 22));
        card.add(messageContent(title, message, "i", PRIMARY, PRIMARY_SOFT), BorderLayout.CENTER);

        JPanel buttons = buttonRow();
        ModernButton cancelButton = secondaryButton(cancelText);
        ModernButton secondaryAction = secondaryButton(secondaryText);
        ModernButton primaryAction = primaryButton(primaryText);

        primaryAction.addActionListener(e -> { result[0] = 0; dialog.dispose(); });
        secondaryAction.addActionListener(e -> { result[0] = 1; dialog.dispose(); });
        cancelButton.addActionListener(e -> { result[0] = 2; dialog.dispose(); });

        buttons.add(cancelButton);
        buttons.add(secondaryAction);
        buttons.add(primaryAction);
        card.add(buttons, BorderLayout.SOUTH);

        finishDialog(dialog, card, parent, 575, 235);
        return result[0];
    }

    public static boolean showConfirm(
            Component parent,
            String title,
            String message,
            String confirmText
    ) {
        final boolean[] confirmed = {false};
        JDialog dialog = createDialog(parent, title);

        RoundedPanel card = dialogCard();
        card.setLayout(new BorderLayout(20, 22));
        card.add(messageContent(title, message, "?", PRIMARY, PRIMARY_SOFT), BorderLayout.CENTER);

        JPanel buttons = buttonRow();
        ModernButton cancelButton = secondaryButton("Cancel");
        ModernButton confirmButton = primaryButton(confirmText);

        cancelButton.addActionListener(e -> dialog.dispose());
        confirmButton.addActionListener(e -> {
            confirmed[0] = true;
            dialog.dispose();
        });

        buttons.add(cancelButton);
        buttons.add(confirmButton);
        card.add(buttons, BorderLayout.SOUTH);

        finishDialog(dialog, card, parent, 520, 225);
        return confirmed[0];
    }

    public static boolean showComponentConfirmDialog(
        Component parent,
        String title,
        Component content,
        String confirmText,
        String cancelText
) {

    final boolean[] confirmed = {false};

    JDialog dialog =
            createDialog(
                    parent,
                    title
            );

    RoundedPanel card =
            dialogCard();

    card.setLayout(
            new BorderLayout(
                    0,
                    18
            )
    );

    JLabel titleLabel =
            new JLabel(
                    title
            );

    titleLabel.setFont(
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    20
            )
    );

    titleLabel.setForeground(
            TEXT
    );

    titleLabel.setBorder(
            BorderFactory.createEmptyBorder(
                    0,
                    0,
                    4,
                    0
            )
    );

    card.add(
            titleLabel,
            BorderLayout.NORTH
    );


    JPanel contentWrapper =
            new JPanel(
                    new BorderLayout()
            );

    contentWrapper.setOpaque(
            false
    );

    contentWrapper.add(
            content,
            BorderLayout.NORTH
    );

    JScrollPane scrollPane =
            new JScrollPane(
                    contentWrapper
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

    card.add(
            scrollPane,
            BorderLayout.CENTER
    );


    JPanel buttons =
            buttonRow();

    ModernButton cancelButton =
            secondaryButton(
                    cancelText
            );

    ModernButton confirmButton =
            primaryButton(
                    confirmText
            );


    cancelButton.addActionListener(
            e -> dialog.dispose()
    );

    confirmButton.addActionListener(
            e -> {

                confirmed[0] =
                        true;

                dialog.dispose();
            }
    );


    buttons.add(
            cancelButton
    );

    buttons.add(
            confirmButton
    );

    card.add(
            buttons,
            BorderLayout.SOUTH
    );


    Dimension preferred =
            content.getPreferredSize();

    int width =
            Math.max(
                    620,
                    Math.min(
                            820,
                            preferred.width + 70
                    )
            );

    int height =
            Math.max(
                    560,
                    Math.min(
                            850,
                            preferred.height + 190
                    )
            );


    Dimension screenSize =
            Toolkit
                    .getDefaultToolkit()
                    .getScreenSize();

    height =
            Math.min(
                    height,
                    screenSize.height - 80
            );


    finishDialog(
            dialog,
            card,
            parent,
            width,
            height
    );

    return confirmed[0];
}

    private static JDialog createDialog(Component parent, String title) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setBackground(new Color(0, 0, 0, 0));
        return dialog;
    }

    // =============================================================
// STATUS BADGE
// =============================================================

public static JPanel statusBadge(
        String status
) {

    String normalized =
            status == null
                    ? ""
                    : status.trim().toUpperCase();

    Color foreground;
    Color background;

    switch (normalized) {

        // =========================================================
        // GREEN
        // =========================================================

        case "ACTIVE":
        case "AVAILABLE":
        case "CONFIRMED":

            foreground =
                    new Color(
                            22,
                            101,
                            52
                    );

            background =
                    new Color(
                            220,
                            252,
                            231
                    );

            break;


        // =========================================================
        // BLUE
        // =========================================================

        case "IN PROGRESS":

            foreground =
                    new Color(
                            29,
                            78,
                            216
                    );

            background =
                    new Color(
                            219,
                            234,
                            254
                    );

            break;


        // =========================================================
        // GRAY
        // =========================================================

        case "COMPLETED":
        case "EXPIRED":

            foreground =
                    new Color(
                            71,
                            85,
                            105
                    );

            background =
                    new Color(
                            241,
                            245,
                            249
                    );

            break;


        // =========================================================
        // RED
        // =========================================================

        case "CANCELLED":
        case "INACTIVE":

            foreground =
                    new Color(
                            185,
                            28,
                            28
                    );

            background =
                    new Color(
                            254,
                            226,
                            226
                    );

            break;


        // =========================================================
        // ORANGE
        // =========================================================

        case "PENDING":

            foreground =
                    new Color(
                            146,
                            64,
                            14
                    );

            background =
                    new Color(
                            254,
                            243,
                            199
                    );

            break;


        // =========================================================
        // DEFAULT
        // =========================================================

        default:

            foreground =
                    MUTED;

            background =
                    new Color(
                            241,
                            245,
                            249
                    );

            break;
    }


    RoundedPanel badge =
            new RoundedPanel(
                    16,
                    background
            );

    badge.setLayout(
            new FlowLayout(
                    FlowLayout.CENTER,
                    10,
                    4
            )
    );

    JLabel label =
            new JLabel(
                    normalized
            );

    label.setFont(
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    11
            )
    );

    label.setForeground(
            foreground
    );

    badge.add(
            label
    );

    badge.setMaximumSize(
            badge.getPreferredSize()
    );

    badge.setAlignmentX(
            Component.LEFT_ALIGNMENT
    );

    return badge;
}

    private static RoundedPanel dialogCard() {
        RoundedPanel card = new RoundedPanel(22, Color.WHITE);
        card.setOutlineColor(BORDER);
        card.setBorder(BorderFactory.createEmptyBorder(26, 28, 24, 28));
        return card;
    }

    private static JPanel messageContent(
            String title,
            String message,
            String iconText,
            Color iconColor,
            Color iconBackground
    ) {
        JPanel content = new JPanel(new BorderLayout(18, 0));
        content.setOpaque(false);
        content.add(createDialogIcon(iconText, iconColor, iconBackground), BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        titleLabel.setForeground(TEXT);

        JLabel messageLabel = new JLabel(
                "<html><body style='width:320px'>" + escapeHtml(message) + "</body></html>"
        );
        messageLabel.setFont(FONT);
        messageLabel.setForeground(MUTED);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(7));
        textPanel.add(messageLabel);
        content.add(textPanel, BorderLayout.CENTER);
        return content;
    }

    private static JPanel buttonRow() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        return buttons;
    }

    private static void finishDialog(
        JDialog dialog,
        JComponent card,
        Component parent,
        int width,
        int height
) {

    JPanel outer =
            new JPanel(
                    new BorderLayout()
            );

    outer.setOpaque(
            false
    );

    outer.setBorder(
            BorderFactory.createEmptyBorder(
                    6,
                    6,
                    6,
                    6
            )
    );

    outer.add(
            card,
            BorderLayout.CENTER
    );

    dialog.setContentPane(
            outer
    );

    dialog.setSize(
            width,
            height
    );

    dialog.setLocationRelativeTo(
            parent
    );

    dialog.setVisible(
            true
    );
}

    private static JLabel createDialogIcon(String text, Color foreground, Color background) {
        JLabel icon = new JLabel(text, SwingConstants.CENTER);
        icon.setOpaque(true);
        icon.setBackground(background);
        icon.setForeground(foreground);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 20));
        icon.setPreferredSize(new Dimension(48, 48));
        icon.setMinimumSize(new Dimension(48, 48));
        icon.setBorder(BorderFactory.createLineBorder(background));
        return icon;
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
    }

    public static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fillColor;
        private Color outlineColor;

        public RoundedPanel(int radius, Color fillColor) {
            this.radius = radius;
            this.fillColor = fillColor;
            setOpaque(false);
        }

        public void setOutlineColor(Color outlineColor) {
            this.outlineColor = outlineColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            if (outlineColor != null) {
                g2.setColor(outlineColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class ModernButton extends JButton {
        private final Color normal;
        private final Color hover;
        private final Color foreground;
        private Color outline;
        private boolean hovered;

        public ModernButton(String text, Color normal, Color hover, Color foreground) {
            super(text);
            this.normal = normal;
            this.hover = hover;
            this.foreground = foreground;

            setFont(FONT_BOLD);
            setForeground(foreground);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
            setPreferredSize(new Dimension(Math.max(120, getPreferredSize().width + 18), 42));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        public void setOutline(Color outline) {
            this.outline = outline;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = isEnabled()
                    ? (hovered ? hover : normal)
                    : new Color(203, 213, 225);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            if (outline != null) {
                g2.setColor(outline);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            g2.dispose();
            setForeground(isEnabled() ? foreground : new Color(100, 116, 139));
            super.paintComponent(g);
        }
    }

    public static DefaultTableCellRenderer statusRenderer() {

    return new DefaultTableCellRenderer() {

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
                    (JLabel) super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            String status =
                    value == null
                            ? ""
                            : value.toString().trim().toUpperCase();

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setFont(
                    FONT_BOLD
            );

            label.setOpaque(
                    true
            );

            label.setBorder(
                    BorderFactory.createEmptyBorder(
                            5,
                            10,
                            5,
                            10
                    )
            );

            // Αν η γραμμή είναι selected,
            // αφήνουμε τα χρώματα επιλογής του JTable.
            if (isSelected) {

                label.setBackground(
                        table.getSelectionBackground()
                );

                label.setForeground(
                        table.getSelectionForeground()
                );

                return label;
            }

            switch (status) {

                // =============================================
                // GREEN
                // =============================================

                case "ACTIVE":
                case "AVAILABLE":
                case "CONFIRMED":
                case "COMPLETED":

                    label.setBackground(
                            new Color(
                                    220,
                                    252,
                                    231
                            )
                    );

                    label.setForeground(
                            new Color(
                                    22,
                                    101,
                                    52
                            )
                    );

                    break;


                // =============================================
                // RED
                // =============================================

                case "CANCELLED":
                case "INACTIVE":

                    label.setBackground(
                            new Color(
                                    254,
                                    226,
                                    226
                            )
                    );

                    label.setForeground(
                            new Color(
                                    185,
                                    28,
                                    28
                            )
                    );

                    break;


                // =============================================
                // ORANGE
                // =============================================

                case "IN PROGRESS":
                case "PENDING":

                    label.setBackground(
                            new Color(
                                    254,
                                    243,
                                    199
                            )
                    );

                    label.setForeground(
                            new Color(
                                    146,
                                    64,
                                    14
                            )
                    );

                    break;


                // =============================================
                // GRAY
                // =============================================

                case "EXPIRED":

                    label.setBackground(
                            new Color(
                                    241,
                                    245,
                                    249
                            )
                    );

                    label.setForeground(
                            new Color(
                                    100,
                                    116,
                                    139
                            )
                    );

                    break;


                // =============================================
                // UNKNOWN STATUS
                // =============================================

                default:

                    label.setBackground(
                            Color.WHITE
                    );

                    label.setForeground(
                            TEXT
                    );

                    break;
            }

            return label;
        }
    };
}
}
