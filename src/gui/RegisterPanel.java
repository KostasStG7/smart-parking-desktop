package gui;

import javax.swing.*;
import java.awt.*;
import service.AuthService;

public class RegisterPanel extends JPanel {

    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private final AuthService authService;
    private final CardLayout parentLayout;
    private final JPanel parentPanel;

    public RegisterPanel(CardLayout layout, JPanel parent) {
        this.parentLayout = layout;
        this.parentPanel = parent;
        this.authService = new AuthService();

        setLayout(new GridBagLayout());
        setBackground(UITheme.BACKGROUND);

        UITheme.RoundedPanel card = new UITheme.RoundedPanel(22, Color.WHITE);
        card.setOutlineColor(UITheme.BORDER);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(30, 42, 30, 42));
        card.setPreferredSize(new Dimension(500, 650));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(UITheme.title("Create your account"), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        card.add(UITheme.subtitle("Start sharing and reserving parking spots."), gbc);

        fullNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        UITheme.styleTextField(fullNameField);
        UITheme.styleTextField(emailField);
        UITheme.styleTextField(phoneField);
        UITheme.styleTextField(passwordField);
        UITheme.styleTextField(confirmPasswordField);

        int row = 2;
        row = addField(card, gbc, row, "Full name", fullNameField);
        row = addField(card, gbc, row, "Email", emailField);
        row = addField(card, gbc, row, "Phone", phoneField);
        row = addField(card, gbc, row, "Password", passwordField);
        row = addField(card, gbc, row, "Confirm password", confirmPasswordField);

        UITheme.ModernButton registerButton = UITheme.primaryButton("Create account");
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 10, 0);
        card.add(registerButton, gbc);

        UITheme.ModernButton backButton = UITheme.secondaryButton("Back to sign in");
        gbc.gridy = row;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(backButton, gbc);

        registerButton.addActionListener(e -> performRegistration());
        backButton.addActionListener(e -> parentLayout.show(parentPanel, "login"));

        add(card);
    }

    private int addField(JPanel card, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(UITheme.fieldLabel(label), gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 14, 0);
        card.add(field, gbc);
        return row;
    }

    private void performRegistration() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            UITheme.showWarning(this,
                    "Missing Information",
                    "Full name, email and password are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            UITheme.showError(this,
                    "Password Mismatch",
                    "Passwords do not match.");
            return;
        }

        boolean success = authService.register(fullName, email, phone, password);
        if (success) {
            UITheme.showSuccess(this,
                    "Registration Complete",
                    "Your account was created successfully.");
            clearFields();
            parentLayout.show(parentPanel, "login");
        } else {
            UITheme.showError(this,
                    "Registration Failed",
                    "Registration failed. Check your input or use a different email.");
        }
    }

    private void clearFields() {
        fullNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }
}
