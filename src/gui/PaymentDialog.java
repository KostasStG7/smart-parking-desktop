package gui;

import javax.swing.*;
import java.awt.*;

import service.PricingService;

public final class PaymentDialog {

    private PaymentDialog() {
    }

    public static Double showPayment(
            Component parent,
            double expectedAmount
    ) {

        PricingService pricingService =
                new PricingService();

        JTextField amountField =
                new JTextField();

        JTextField holderField =
                new JTextField();

        JTextField cardField =
                new JTextField();

        JTextField expiryField =
                new JTextField();

        JPasswordField cvvField =
                new JPasswordField();

        UITheme.styleTextField(
                amountField
        );

        UITheme.styleTextField(
                holderField
        );

        UITheme.styleTextField(
                cardField
        );

        UITheme.styleTextField(
                expiryField
        );

        UITheme.styleTextField(
                cvvField
        );

        while (
                true
        ) {

            JPanel form =
                    new JPanel();

            form.setOpaque(
                    false
            );

            form.setLayout(
                    new BoxLayout(
                            form,
                            BoxLayout.Y_AXIS
                    )
            );

            JLabel amountDue =
                    new JLabel(
                            "Amount due: "
                                    +
                            pricingService.formatPrice(
                                    expectedAmount
                            )
                    );

            amountDue.setFont(
                    UITheme.SECTION
            );

            amountDue.setForeground(
                    UITheme.TEXT
            );

            amountDue.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            form.add(
                    amountDue
            );

            form.add(
                    Box.createVerticalStrut(
                            6
                    )
            );

            JLabel simulation =
                    new JLabel(
                            "Simulation payment - card details are not stored."
                    );

            simulation.setFont(
                    UITheme.SMALL
            );

            simulation.setForeground(
                    UITheme.MUTED
            );

            simulation.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            form.add(
                    simulation
            );

            form.add(
                    Box.createVerticalStrut(
                            18
                    )
            );

            addField(
                    form,
                    "Amount (€)",
                    amountField
            );

            addField(
                    form,
                    "Card holder",
                    holderField
            );

            addField(
                    form,
                    "Card number",
                    cardField
            );

            addField(
                    form,
                    "Expiry (MM/YY)",
                    expiryField
            );

            addField(
                    form,
                    "CVV",
                    cvvField
            );

            boolean confirmed =
                    UITheme.showComponentConfirmDialog(
                            parent,
                            "Payment",
                            form,
                            "Pay",
                            "Cancel"
                    );

            if (
                    !confirmed
            ) {

                return null;
            }

            String amountText =
                    amountField
                            .getText()
                            .trim()
                            .replace(
                                    ",",
                                    "."
                            );

            double amount;

            try {

                amount =
                        Double.parseDouble(
                                amountText
                        );

            } catch (
                    NumberFormatException ex
            ) {

                UITheme.showWarning(
                        parent,
                        "Invalid Amount",
                        "Enter a valid payment amount."
                );

                continue;
            }

            if (
                    Math.abs(
                            amount
                                    -
                            expectedAmount
                    )
                            > 0.001
            ) {

                UITheme.showWarning(
                        parent,
                        "Incorrect Amount",
                        "The payment amount must be exactly "
                                +
                        pricingService.formatPrice(
                                expectedAmount
                        )
                                +
                        "."
                );

                continue;
            }

            String holder =
                    holderField
                            .getText()
                            .trim();

            String cardNumber =
                    cardField
                            .getText()
                            .replaceAll(
                                    "\\s",
                                    ""
                            );

            String expiry =
                    expiryField
                            .getText()
                            .trim();

            String cvv =
                    new String(
                            cvvField.getPassword()
                    ).trim();

            if (
                    holder.isEmpty()
            ) {

                UITheme.showWarning(
                        parent,
                        "Card Holder Required",
                        "Enter the card holder name."
                );

                continue;
            }

            if (
                    !cardNumber.matches(
                            "\\d{16}"
                    )
            ) {

                UITheme.showWarning(
                        parent,
                        "Invalid Card Number",
                        "Enter a 16-digit card number."
                );

                continue;
            }

            if (
                    !isValidExpiry(
                            expiry
                    )
            ) {

                UITheme.showWarning(
                        parent,
                        "Invalid Expiry",
                        "Use expiry format MM/YY."
                );

                continue;
            }

            if (
                    !cvv.matches(
                            "\\d{3,4}"
                    )
            ) {

                UITheme.showWarning(
                        parent,
                        "Invalid CVV",
                        "CVV must contain 3 or 4 digits."
                );

                continue;
            }

            return amount;
        }
    }

    private static boolean isValidExpiry(
            String expiry
    ) {

        if (
                !expiry.matches(
                        "\\d{2}/\\d{2}"
                )
        ) {

            return false;
        }

        int month =
                Integer.parseInt(
                        expiry.substring(
                                0,
                                2
                        )
                );

        return month >= 1
                &&
                month <= 12;
    }

    private static void addField(
            JPanel panel,
            String label,
            JTextField field
    ) {

        JLabel fieldLabel =
                UITheme.fieldLabel(
                        label
                );

        fieldLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        field.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(
                fieldLabel
        );

        panel.add(
                Box.createVerticalStrut(
                        5
                )
        );

        panel.add(
                field
        );

        panel.add(
                Box.createVerticalStrut(
                        12
                )
        );
    }
}