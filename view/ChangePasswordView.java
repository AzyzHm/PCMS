package view;

import controllers.memberController;
import exceptions.powerHouseException;
import models.member;
import javax.swing.*;
import java.awt.*;
import static utils.guiHelper.*;


public class ChangePasswordView extends JDialog {

    public ChangePasswordView(Frame parentFrame, member member) {
        super(parentFrame, "Changement de mot de passe — Premier accès", true);
        setSize(480, 320);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        memberController memberController = new memberController();
        JPasswordField newPasswordField    = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JButton        validateButton       = createButton("Valider", COLOUR_SUCCESS);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 6, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        formPanel.add(new JLabel("Nouveau mot de passe :"));  formPanel.add(newPasswordField);
        formPanel.add(new JLabel("Confirmer :"));             formPanel.add(confirmPasswordField);

        JLabel hintLabel = new JLabel(
            "<html><div style='text-align:center'>Minimum 4 caractères.<br>"
            + "Ce changement est obligatoire lors du premier accès.</div></html>",
            SwingConstants.CENTER);
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hintLabel.setForeground(COLOUR_MUTED);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        validateButton.setPreferredSize(new Dimension(160, 34));
        buttonPanel.add(validateButton);

        JPanel rootPanel = new JPanel(new BorderLayout(0, 4));
        rootPanel.add(formPanel,   BorderLayout.NORTH);
        rootPanel.add(hintLabel,   BorderLayout.CENTER);
        rootPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(rootPanel);

        validateButton.addActionListener(e -> {
            String newPassword     = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                    "Les deux mots de passe ne correspondent pas. Veuillez réessayer.",
                    "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                memberController.changePassword(member, newPassword);
                JOptionPane.showMessageDialog(this,
                    "Votre mot de passe a été mis à jour avec succès.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (powerHouseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
