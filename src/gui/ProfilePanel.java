package gui;

import javax.swing.*;
import java.awt.*;

import model.User;
import service.ProfileService;

public class ProfilePanel extends JPanel {

    private final int currentUserId;

    private final ProfileService profileService;

    private final JTextField fullNameField;
    private final JTextField emailField;
    private final JTextField phoneField;

    private Runnable profileUpdatedListener;

    public ProfilePanel(
            int userId
    ) {

        this.currentUserId =
                userId;

        this.profileService =
                new ProfileService();

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

        add(
                UITheme.pageHeader(
                        "My Profile",
                        "View and update your personal account information."
                ),
                BorderLayout.NORTH
        );

        JPanel center =
                new JPanel(
                        new GridBagLayout()
                );

        center.setOpaque(
                false
        );

        UITheme.RoundedPanel card =
                new UITheme.RoundedPanel(
                        18,
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
                        30,
                        34,
                        30,
                        34
                )
        );

        card.setPreferredSize(
                new Dimension(
                        620,
                        470
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx =
                0;

        gbc.weightx =
                1;

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        JLabel heading =
                new JLabel(
                        "Account information"
                );

        heading.setFont(
                UITheme.SECTION
        );

        heading.setForeground(
                UITheme.TEXT
        );

        gbc.gridy =
                0;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        6,
                        0
                );

        card.add(
                heading,
                gbc
        );

        JLabel helper =
                UITheme.subtitle(
                        "Keep your contact information up to date."
                );

        gbc.gridy =
                1;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        24,
                        0
                );

        card.add(
                helper,
                gbc
        );

        fullNameField =
                new JTextField();

        emailField =
                new JTextField();

        phoneField =
                new JTextField();

        UITheme.styleTextField(
                fullNameField
        );

        UITheme.styleTextField(
                emailField
        );

        UITheme.styleTextField(
                phoneField
        );

        int row =
                2;

        row =
                addField(
                        card,
                        gbc,
                        row,
                        "Full name",
                        fullNameField
                );

        row =
                addField(
                        card,
                        gbc,
                        row,
                        "Email",
                        emailField
                );

        row =
                addField(
                        card,
                        gbc,
                        row,
                        "Phone",
                        phoneField
                );

        JLabel note =
                new JLabel(
                        "Your password is not shown here for security."
                );

        note.setFont(
                UITheme.SMALL
        );

        note.setForeground(
                UITheme.MUTED
        );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        20,
                        0
                );

        card.add(
                note,
                gbc
        );

        UITheme.ModernButton saveButton =
                UITheme.primaryButton(
                        "Save changes"
                );

        gbc.gridy =
                row;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        0,
                        0
                );

        card.add(
                saveButton,
                gbc
        );

        saveButton.addActionListener(
                e -> saveProfile()
        );

        center.add(
                card
        );

        add(
                center,
                BorderLayout.CENTER
        );

        loadProfile();
    }

    public void setProfileUpdatedListener(
            Runnable listener
    ) {

        this.profileUpdatedListener =
                listener;
    }

    private int addField(
            JPanel card,
            GridBagConstraints gbc,
            int row,
            String label,
            JTextField field
    ) {

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        6,
                        0
                );

        card.add(
                UITheme.fieldLabel(
                        label
                ),
                gbc
        );

        gbc.gridy =
                row++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        17,
                        0
                );

        card.add(
                field,
                gbc
        );

        return row;
    }

    public void loadProfile() {

        User user =
                profileService
                        .getProfile(
                                currentUserId
                        );

        if (
                user == null
        ) {

            UITheme.showError(
                    this,
                    "Profile Error",
                    "Your account information could not be loaded."
            );

            return;
        }

        fullNameField.setText(
                safeText(
                        user.getFullName()
                )
        );

        emailField.setText(
                safeText(
                        user.getEmail()
                )
        );

        phoneField.setText(
                user.getPhone() == null
                        ? ""
                        : user.getPhone()
        );
    }

    private void saveProfile() {

        String fullName =
                fullNameField
                        .getText()
                        .trim();

        String email =
                emailField
                        .getText()
                        .trim();

        String phone =
                phoneField
                        .getText()
                        .trim();

        boolean success =
                profileService
                        .updateProfile(
                                currentUserId,
                                fullName,
                                email,
                                phone
                        );

        if (
                success
        ) {

            UITheme.showSuccess(
                    this,
                    "Profile Updated",
                    "Your account information was updated successfully."
            );

            loadProfile();

            if (
                    profileUpdatedListener != null
            ) {

                profileUpdatedListener.run();
            }

        } else {

            String error =
                    profileService
                            .getLastErrorMessage();

            if (
                    error == null
                            ||
                    error.isBlank()
            ) {

                error =
                        "Failed to update your profile.";
            }

            UITheme.showError(
                    this,
                    "Update Failed",
                    error
            );
        }
    }

    private String safeText(
            String value
    ) {

        return value == null
                ? ""
                : value;
    }
}