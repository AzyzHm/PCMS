package controller;

import dao.memberDAO;
import models.user;

import exceptions.AuthenticationException;
import exceptions.DatabaseException;
import exceptions.ValidationException;
import exceptions.SecurityException;

public class authController {
    private memberDAO memberDAO;

    public authController() {
        this.memberDAO = new memberDAO();
    }

    public user login(String username, String password) throws AuthenticationException, DatabaseException, ValidationException, SecurityException {
        if (username.isEmpty() || password.isEmpty()) {
            throw new AuthenticationException("Le nom d'utilisateur et le mot de passe sont requis.");
        }

        user User = memberDAO.authenticate(username, password);
        
        if (User == null) {
            throw new AuthenticationException("Identifiants incorrects. Veuillez réessayer.");
        }
        
        return User;
    }
}
