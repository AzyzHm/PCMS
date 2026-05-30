package view;

import controllers.memberController;
import exceptions.powerHouseException;
import models.member;
import javax.swing.*;
import java.awt.*;


import static utils.guiHelper.*;


public class ChangePasswordView extends JDialog {

    private final memberController memberController = new memberController();
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton validateButton;

    public ChangePasswordView(Frame parentFrame, member member) {
        super(parentFrame, "Changement de mot de passe - Premier accès", true);
        setSize(480, 320);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        JLabel titleLabel = new JLabel("Veuillez redéfinir votre mot de passe");
        titleLabel.setBounds(80, 20, 420, 30);
        titleLabel.setFont(new Font("Dubai", Font.BOLD, 18));
        titleLabel.setForeground(COLOUR_ACCENT);

        JLabel newPasswordLabel = new JLabel("Nouveau mot de passe :");
        newPasswordLabel.setBounds(30, 80, 200, 30);
        newPasswordLabel.setFont(new Font("Dubai", Font.PLAIN, 14));
        newPasswordLabel.setForeground(COLOUR_ACCENT);

        newPasswordField    = new JPasswordField();
        newPasswordField.setBounds(230, 80, 200, 30);
        newPasswordField.setFont(new Font("Dubai", Font.PLAIN, 14));


        JLabel confirmPasswordLabel = new JLabel("Confirmer votre mot de passe :");
        confirmPasswordLabel.setBounds(30, 135, 200, 30);
        confirmPasswordLabel.setFont(new Font("Dubai", Font.PLAIN, 14));
        confirmPasswordLabel.setForeground(COLOUR_ACCENT);


        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(230, 135, 200, 30);
        confirmPasswordField.setFont(new Font("Dubai", Font.PLAIN, 14));

        validateButton = createButton("Valider", COLOUR_SUCCESS);
        validateButton.setFont(new Font("Dubai", Font.BOLD, 16));
        validateButton.setBounds(150, 200, 160, 34);

        JPanel rootPanel = new JPanel(null);
        rootPanel.add(titleLabel);
        rootPanel.add(newPasswordLabel);
        rootPanel.add(newPasswordField);
        rootPanel.add(confirmPasswordLabel);
        rootPanel.add(confirmPasswordField);
        rootPanel.add(validateButton);

        setContentPane(rootPanel);

        validateButton.addActionListener(e -> changePassword(member));
    }

    private void changePassword(member member){
        String newPassword     = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            if (!newPassword.equals(confirmPassword)) {
                showError(this, "Les deux mots de passe ne correspondent pas. Veuillez réessayer.");
                return;
            }
            try {
                memberController.changePassword(member, newPassword);
                showSuccess(this, "Votre mot de passe a été changé avec succès.");
                dispose();
            } catch (powerHouseException ex) {
                showError(this, ex.getMessage());
            }
    }
}
