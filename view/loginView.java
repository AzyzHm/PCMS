package view;

import javax.swing.*;
import java.awt.*;

import models.user;
import models.member;
import models.admin;

import static utils.guiHelper.*;

import controllers.authController;
import exceptions.powerHouseException;

public class loginView extends JFrame {

    private final authController authController = new authController();
    private JTextField loginField;
    private JPasswordField passwordField;

    public loginView() {
        setTitle("PowerHouse Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(COLOUR_BACKGROUND);

        ImageIcon icon = new ImageIcon("icons/Login.png");
        setIconImage(icon.getImage());

        JLabel titleLabel = new JLabel("Veuillez vous connecter");
        titleLabel.setForeground(COLOUR_SUCCESS);
        titleLabel.setBounds(220, 60, 500, 50);
        titleLabel.setFont(new Font("Dubai", Font.BOLD, 30));
        add(titleLabel);

        JLabel usernameLabel = new JLabel("Nom d'utilisateur:");
        usernameLabel.setBounds(100, 150, 250, 50);
        usernameLabel.setForeground(COLOUR_DARK);
        usernameLabel.setFont(new Font("Dubai", Font.BOLD, 25));
        add(usernameLabel);

        JLabel passwordLabel = new JLabel("Mot de passe:");
        passwordLabel.setBounds(100, 250, 250, 50);
        passwordLabel.setForeground(COLOUR_DARK);
        passwordLabel.setFont(new Font("Dubai", Font.BOLD, 25));
        add(passwordLabel);

        loginField = new JTextField();
        loginField.setBounds(350, 150, 300, 50);
        loginField.setFont(new Font("Dubai", Font.PLAIN, 23));
        add(loginField);

        passwordField = new JPasswordField();
        passwordField.setBounds(350, 250, 300, 50);
        passwordField.setFont(new Font("Dubai", Font.PLAIN, 23));
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(300, 370, 150, 50);
        loginButton.setBackground(COLOUR_SUCCESS);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Dubai", Font.PLAIN, 30));
        add(loginButton);

        loginButton.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String username = loginField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError(this, "Veuillez remplir tous les champs.");
        }

        if (username.length() < 6 || password.length() < 8) {
            showError(this, "Le nom d'utilisateur doit comporter au moins 6 caractères et le mot de passe au moins 8 caractères.");
            return;
        }

        try {
            user authenticatedUser = authController.login(username, password);
            if (authenticatedUser instanceof admin) {
                showSuccess(this, "Connexion réussie en tant qu'administrateur !");
                new adminDashboard().setVisible(true);
            }
            else {
                showSuccess(this, "Connexion réussie en tant que membre !");
                new memberDashboard((member) authenticatedUser).setVisible(true);
            }
            dispose();
        } catch (powerHouseException exception) {
            showError(this, exception.getMessage());
            passwordField.setText("");
        }
    }

    // pour les tests
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new loginView().setVisible(true));
    }
}