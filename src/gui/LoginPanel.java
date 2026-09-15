package gui;

import javax.swing.*;
import java.awt.*;

import service.AuthService;
import model.User;

public class LoginPanel extends JPanel {

    private JTextField emailField;
    private JPasswordField passwordField;

    private final AuthService authService;

    private final CardLayout parentLayout;
    private final JPanel parentPanel;

    // =============================================================
    // LOGIN LISTENER
    // =============================================================

    public interface LoginListener {
        void onLoginSuccess(int userId);
    }

    private LoginListener loginListener;

    public void setLoginListener(LoginListener listener) {
        this.loginListener = listener;
    }

    // =============================================================
    // GUEST LISTENER
    // =============================================================

    public interface GuestListener {
        void onContinueAsGuest();
    }

    private GuestListener guestListener;

    public void setGuestListener(GuestListener listener) {
        this.guestListener = listener;
    }

    // =============================================================
    // CONSTRUCTOR
    // =============================================================

    public LoginPanel(
            CardLayout layout,
            JPanel parent
    ) {

        this.parentLayout = layout;
        this.parentPanel = parent;

        this.authService =
                new AuthService();

        setLayout(
                new GridLayout(
                        1,
                        2
                )
        );

        setBackground(
                UITheme.BACKGROUND
        );

        add(
                buildBrandPanel()
        );

        add(
                buildLoginSide()
        );
    }

    // =============================================================
    // BRAND PANEL
    // =============================================================

    private JPanel buildBrandPanel() {

        JPanel brand =
                new JPanel(
                        new GridBagLayout()
                ) {

                    @Override
                    protected void paintComponent(
                            Graphics g
                    ) {

                        Graphics2D g2 =
                                (Graphics2D)
                                        g.create();

                        g2.setRenderingHint(
                                RenderingHints.KEY_RENDERING,
                                RenderingHints.VALUE_RENDER_QUALITY
                        );

                        GradientPaint gp =
                                new GradientPaint(
                                        0,
                                        0,
                                        new Color(
                                                15,
                                                23,
                                                42
                                        ),

                                        getWidth(),
                                        getHeight(),

                                        new Color(
                                                30,
                                                64,
                                                175
                                        )
                                );

                        g2.setPaint(
                                gp
                        );

                        g2.fillRect(
                                0,
                                0,
                                getWidth(),
                                getHeight()
                        );

                        g2.dispose();
                    }
                };

        JPanel content =
                new JPanel();

        content.setOpaque(
                false
        );

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS
                )
        );

        content.setPreferredSize(
                new Dimension(
                        420,
                        360
                )
        );

        // =========================================================
        // LOGO
        // =========================================================

        JLabel logo =
                new JLabel(
                        "P",
                        SwingConstants.CENTER
                );

        logo.setOpaque(
                true
        );

        logo.setBackground(
                Color.WHITE
        );

        logo.setForeground(
                UITheme.PRIMARY
        );

        logo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        34
                )
        );

        logo.setMaximumSize(
                new Dimension(
                        64,
                        64
                )
        );

        logo.setPreferredSize(
                new Dimension(
                        64,
                        64
                )
        );

        logo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        // =========================================================
        // TITLE
        // =========================================================

        JLabel title =
                new JLabel(
                        "Smart Parking"
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        38
                )
        );

        title.setForeground(
                Color.WHITE
        );

        title.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        // =========================================================
        // SUBTITLE
        // =========================================================

        JLabel subtitle =
                new JLabel(
                        "Find, share and reserve parking with ease."
                );

        subtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        17
                )
        );

        subtitle.setForeground(
                new Color(
                        219,
                        234,
                        254
                )
        );

        subtitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        // =========================================================
        // CONTENT
        // =========================================================

        content.add(
                logo
        );

        content.add(
                Box.createVerticalStrut(
                        28
                )
        );

        content.add(
                title
        );

        content.add(
                Box.createVerticalStrut(
                        12
                )
        );

        content.add(
                subtitle
        );

        content.add(
                Box.createVerticalStrut(
                        36
                )
        );

        content.add(
                feature(
                        "Live map of active parking spots"
                )
        );

        content.add(
                Box.createVerticalStrut(
                        14
                )
        );

        content.add(
                feature(
                        "Simple reservation workflow"
                )
        );

        content.add(
                Box.createVerticalStrut(
                        14
                )
        );

        content.add(
                feature(
                        "Availability and notification management"
                )
        );

        brand.add(
                content
        );

        return brand;
    }

    // =============================================================
    // FEATURE
    // =============================================================

    private JLabel feature(
            String text
    ) {

        JLabel label =
                new JLabel(
                        "✓  " + text
                );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        15
                )
        );

        label.setForeground(
                new Color(
                        226,
                        232,
                        240
                )
        );

        label.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return label;
    }

    // =============================================================
    // LOGIN SIDE
    // =============================================================

    private JPanel buildLoginSide() {

        JPanel side =
                new JPanel(
                        new GridBagLayout()
                );

        side.setBackground(
                UITheme.BACKGROUND
        );

        UITheme.RoundedPanel card =
                new UITheme.RoundedPanel(
                        22,
                        Color.WHITE
                );

        card.setOutlineColor(
                UITheme.BORDER
        );

        card.setLayout(
                new GridBagLayout()
        );

        card.setBorder(
                BorderFactory.createEmptyBorder(
                        34,
                        38,
                        34,
                        38
                )
        );

        card.setPreferredSize(
                new Dimension(
                        430,
                        535
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        // =========================================================
        // TITLE
        // =========================================================

        JLabel title =
                UITheme.title(
                        "Welcome back"
                );

        gbc.gridy = 0;

        card.add(
                title,
                gbc
        );

        JLabel subtitle =
                UITheme.subtitle(
                        "Sign in to manage parking spots and reservations."
                );

        gbc.gridy = 1;

        gbc.insets =
                new Insets(
                        5,
                        0,
                        28,
                        0
                );

        card.add(
                subtitle,
                gbc
        );

        // =========================================================
        // EMAIL
        // =========================================================

        gbc.gridy = 2;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        7,
                        0
                );

        card.add(
                UITheme.fieldLabel(
                        "Email"
                ),
                gbc
        );

        emailField =
                new JTextField();

        UITheme.styleTextField(
                emailField
        );

        gbc.gridy = 3;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        18,
                        0
                );

        card.add(
                emailField,
                gbc
        );

        // =========================================================
        // PASSWORD
        // =========================================================

        gbc.gridy = 4;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        7,
                        0
                );

        card.add(
                UITheme.fieldLabel(
                        "Password"
                ),
                gbc
        );

        passwordField =
                new JPasswordField();

        UITheme.styleTextField(
                passwordField
        );

        gbc.gridy = 5;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        24,
                        0
                );

        card.add(
                passwordField,
                gbc
        );

        // =========================================================
        // SIGN IN
        // =========================================================

        UITheme.ModernButton loginButton =
                UITheme.primaryButton(
                        "Sign in"
                );

        gbc.gridy = 6;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        12,
                        0
                );

        card.add(
                loginButton,
                gbc
        );

        // =========================================================
        // CREATE ACCOUNT
        // =========================================================

        UITheme.ModernButton registerButton =
                UITheme.secondaryButton(
                        "Create an account"
                );

        gbc.gridy = 7;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        14,
                        0
                );

        card.add(
                registerButton,
                gbc
        );

        // =========================================================
        // OR
        // =========================================================

        JLabel orLabel =
                new JLabel(
                        "or",
                        SwingConstants.CENTER
                );

        orLabel.setFont(
                UITheme.SMALL
        );

        orLabel.setForeground(
                UITheme.MUTED
        );

        gbc.gridy = 8;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        12,
                        0
                );

        card.add(
                orLabel,
                gbc
        );

        // =========================================================
        // GUEST BUTTON
        // =========================================================

        UITheme.ModernButton guestButton =
                UITheme.secondaryButton(
                        "Continue as guest"
                );

        gbc.gridy = 9;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        0,
                        0
                );

        card.add(
                guestButton,
                gbc
        );

        // =========================================================
        // ACTIONS
        // =========================================================

        loginButton.addActionListener(
                e -> performLogin()
        );

        passwordField.addActionListener(
                e -> performLogin()
        );

        registerButton.addActionListener(
                e ->
                        parentLayout.show(
                                parentPanel,
                                "register"
                        )
        );

        guestButton.addActionListener(
                e -> {

                    if (
                            guestListener != null
                    ) {

                        guestListener
                                .onContinueAsGuest();
                    }
                }
        );

        side.add(
                card
        );

        return side;
    }

    // =============================================================
    // LOGIN
    // =============================================================

    private void performLogin() {

        String email =
                emailField
                        .getText()
                        .trim();

        String password =
                new String(
                        passwordField
                                .getPassword()
                );

        if (
                email.isEmpty()
                        ||
                password.isEmpty()
        ) {

            UITheme.showWarning(
                    this,
                    "Missing Information",
                    "Please enter both email and password."
            );

            return;
        }

        User user =
                authService.login(
                        email,
                        password
                );

        if (
                user != null
        ) {

            passwordField.setText(
                    ""
            );

            if (
                    loginListener != null
            ) {

                loginListener
                        .onLoginSuccess(
                                user.getUserId()
                        );
            }

        } else {

            UITheme.showError(
                    this,
                    "Login Failed",
                    "Invalid email or password."
            );
        }
    }

        // =============================================================
        // RESET LOGIN FORM
        // =============================================================

        public void resetFields() {

        emailField.setText(
            ""
        );

        passwordField.setText(
            ""
        );

         emailField.requestFocusInWindow();
        }
}