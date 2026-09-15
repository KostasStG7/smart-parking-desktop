package service;

import dao.UserDAO;
import model.User;

public class ProfileService {

    private final UserDAO userDAO =
            new UserDAO();

    private String lastErrorMessage =
            "";

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    private boolean fail(
            String message
    ) {

        lastErrorMessage =
                message;

        return false;
    }

    public User getProfile(
            int userId
    ) {

        if (
                userId <= 0
        ) {

            return null;
        }

        return userDAO.findById(
                userId
        );
    }

    public boolean updateProfile(
            int userId,
            String fullName,
            String email,
            String phone
    ) {

        lastErrorMessage =
                "";

        if (
                userId <= 0
        ) {

            return fail(
                    "Invalid user."
            );
        }

        if (
                fullName == null
                        ||
                fullName.trim().isEmpty()
        ) {

            return fail(
                    "Full name is required."
            );
        }

        if (
                email == null
                        ||
                email.trim().isEmpty()
        ) {

            return fail(
                    "Email is required."
            );
        }

        String cleanEmail =
                email.trim();

        if (
                !cleanEmail.matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                )
        ) {

            return fail(
                    "Enter a valid email address."
            );
        }

        User existingWithEmail =
                userDAO.findByEmail(
                        cleanEmail
                );

        if (
                existingWithEmail != null
                        &&
                existingWithEmail.getUserId()
                        != userId
        ) {

            return fail(
                    "This email is already used by another account."
            );
        }

        User user =
                userDAO.findById(
                        userId
                );

        if (
                user == null
        ) {

            return fail(
                    "User account could not be found."
            );
        }

        user.setFullName(
                fullName.trim()
        );

        user.setEmail(
                cleanEmail
        );

        user.setPhone(
                phone == null
                        ? ""
                        : phone.trim()
        );

        if (
                !userDAO.updateProfile(
                        user
                )
        ) {

            return fail(
                    "Profile information could not be saved."
            );
        }

        return true;
    }
}