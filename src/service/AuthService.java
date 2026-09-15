package service;

import dao.UserDAO;
import model.User;

public class AuthService {

    private UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {

        if (email == null || email.trim().isEmpty()) {
            System.out.println("Το email δεν μπορεί να είναι κενό.");
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            System.out.println("Ο κωδικός δεν μπορεί να είναι κενός.");
            return null;
        }

        return userDAO.findByEmailAndPassword(email, password);
    }

    public boolean register(String fullName, String email, String phone, String password) {

        if (fullName == null || fullName.trim().isEmpty()) {
            System.out.println("Το ονοματεπώνυμο δεν μπορεί να είναι κενό.");
            return false;
        }

        if (email == null || email.trim().isEmpty()) {
            System.out.println("Το email δεν μπορεί να είναι κενό.");
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            System.out.println("Ο κωδικός δεν μπορεί να είναι κενός.");
            return false;
        }

        if (userDAO.emailExists(email)) {
            System.out.println("Υπάρχει ήδη χρήστης με αυτό το email.");
            return false;
        }

        User user = new User(
                fullName,
                email,
                phone,
                password,
                "USER"
        );

        return userDAO.insertUser(user);
    }
}