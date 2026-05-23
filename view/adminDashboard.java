package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import controllers.adminController;

import models.member;

import static utils.guiHelper.*;

import exceptions.powerHouseException;

public class adminDashboard extends JFrame{

    private final adminController adminController = new adminController();

    // Table des activités
    private final DefaultTableModel activitiesTableModel = createReadOnlyTableModel("ID", "Nom", "Description", "Capacité", "Horaires", "Inscrits");
    private final JTable     activitiesTable       = new JTable(activitiesTableModel);
    private final JTextField activityNameField     = new JTextField();
    private final JTextField activityDescField     = new JTextField();
    private final JTextField activityCapacityField = new JTextField(5);
    private final JTextField activityScheduleField = new JTextField();
    private int selectedActivityId = -1;

    // table des membres
    private final DefaultTableModel membersTableModel   = createReadOnlyTableModel( "ID", "Nom", "Prénom", "Login", "Email", "Téléphone", "Date naiss.", "Poids");
    private final JTable       membersTable         = new JTable(membersTableModel);
    private final JTextField   memberLastNameField  = new JTextField();
    private final JTextField   memberFirstNameField = new JTextField();
    private final JTextField   memberLoginField     = new JTextField();
    private final JPasswordField memberPasswordField= new JPasswordField();
    private final JTextField   memberEmailField     = new JTextField();
    private final JTextField   memberPhoneField     = new JTextField();
    private final JTextField   memberBirthDateField = new JTextField();
    private final JTextField   memberWeightField    = new JTextField(5);
    private final JTextField   memberAddressField   = new JTextField();
    private int selectedMemberId = -1;

    // table des inscriptions
    private final DefaultTableModel enrollmentsTableModel = createReadOnlyTableModel("ID", "Membre", "Activité", "Statut");
    private final JTable enrollmentsTable = new JTable(enrollmentsTableModel);

    public adminDashboard() {
        setTitle("PowerHouse : Tableau de bord d'administration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(0x1B1931));

        ImageIcon icon = new ImageIcon("icons/AdminSpace.png");
        setIconImage(icon.getImage());

        applyTableStyle(activitiesTable);
        applyTableStyle(membersTable);
        applyTableStyle(enrollmentsTable);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Activités", buildActivitiesTab());
        tabbedPane.addTab("Membres",      buildMembersTab());
        tabbedPane.addTab("Inscriptions", buildEnrollmentsTab());


        JPanel headerBar = buildHeaderBar();
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(COLOUR_BACKGROUND);

        rootPanel.add(headerBar, BorderLayout.NORTH);
        rootPanel.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(rootPanel);
        refreshAll();
    }


    private JPanel buildHeaderBar() {
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        headerBar.setBackground(COLOUR_BACKGROUND);

        JLabel titleLabel = new JLabel("POWERHOUSE --- Administration");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        JButton logoutButton = createButton("Déconnexion", COLOUR_DANGER);
        logoutButton.setPreferredSize(new Dimension(150, 28));
        logoutButton.addActionListener(e -> { new loginView().setVisible(true); dispose(); });

        headerBar.add(titleLabel,   BorderLayout.WEST);
        headerBar.add(logoutButton, BorderLayout.EAST);
        return headerBar;
    }

    private JPanel buildActivitiesTab() {
        JPanel formPanel = createFormPanel("Activité");
        formPanel.add(rightAlignedLabel("Nom :"));         formPanel.add(activityNameField);
        formPanel.add(rightAlignedLabel("Description :")); formPanel.add(activityDescField);
        formPanel.add(rightAlignedLabel("Capacité :"));    formPanel.add(activityCapacityField);
        formPanel.add(rightAlignedLabel("Horaires :"));    formPanel.add(activityScheduleField);

        JButton addButton    = createButton("Ajouter",    COLOUR_SUCCESS);
        JButton updateButton = createButton("Modifier",   COLOUR_ACCENT);
        JButton deleteButton = createButton("Supprimer",  COLOUR_DANGER);
        JButton clearButton  = createButton("Effacer",    COLOUR_MUTED);

        addButton.addActionListener(e -> {
            try {
                adminController.createActivity(
                    activityNameField.getText().trim(),
                    activityDescField.getText().trim(),
                    Integer.parseInt(activityCapacityField.getText().trim()),
                    activityScheduleField.getText().trim());
                showSuccess(this, "Activité créée avec succès.");
                clearActivityForm();
                refreshActivitiesTable();
            } catch (NumberFormatException ex) { showError(this, "La capacité doit être un nombre entier."); }
              catch (powerHouseException ex)   { showError(this, ex.getMessage()); }
        });

        updateButton.addActionListener(e -> {
            if (selectedActivityId < 0) { showWarning(this, "Veuillez sélectionner une activité dans le tableau."); return; }
            try {
                adminController.updateActivity(
                    selectedActivityId,
                    activityNameField.getText().trim(),
                    activityDescField.getText().trim(),
                    Integer.parseInt(activityCapacityField.getText().trim()),
                    activityScheduleField.getText().trim());
                showSuccess(this, "Activité modifiée avec succès.");
                clearActivityForm();
                refreshActivitiesTable();
            } catch (NumberFormatException ex) { showError(this, "La capacité doit être un nombre entier."); }
              catch (powerHouseException ex)   { showError(this, ex.getMessage()); }
        });

        deleteButton.addActionListener(e -> {
            if (selectedActivityId < 0) { showWarning(this, "Veuillez sélectionner une activité dans le tableau."); return; }
            if (!askConfirmation(this, "Supprimer cette activité ? Toutes les inscriptions associées seront également supprimées.")) return;
            try {
                adminController.deleteActivity(selectedActivityId);
                showSuccess(this, "Activité supprimée.");
                clearActivityForm();
                refreshAll();
            } catch (powerHouseException ex) { showError(this, ex.getMessage()); }
        });

        clearButton.addActionListener(e -> clearActivityForm());

        activitiesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && activitiesTable.getSelectedRow() >= 0) {
                int row = activitiesTable.getSelectedRow();
                selectedActivityId = (int) activitiesTableModel.getValueAt(row, 0);
                activityNameField.setText(nullToEmpty(activitiesTableModel.getValueAt(row, 1)));
                activityDescField.setText(nullToEmpty(activitiesTableModel.getValueAt(row, 2)));
                activityCapacityField.setText(nullToEmpty(activitiesTableModel.getValueAt(row, 3)));
                activityScheduleField.setText(nullToEmpty(activitiesTableModel.getValueAt(row, 4)));
            }
        });

        return buildTabPanel(formPanel, new JButton[]{addButton, updateButton, deleteButton, clearButton}, activitiesTable);
    }

    private void clearActivityForm() {
        activityNameField.setText(""); activityDescField.setText("");
        activityCapacityField.setText(""); activityScheduleField.setText("");
        selectedActivityId = -1;
        activitiesTable.clearSelection();
    }

    private JPanel createFormPanel(String title) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(title),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return panel;
    }

    private JPanel buildMembersTab() {
        JPanel formPanel = createFormPanel("Membre");
        formPanel.add(rightAlignedLabel("Nom :"));              formPanel.add(memberLastNameField);
        formPanel.add(rightAlignedLabel("Prénom :"));           formPanel.add(memberFirstNameField);
        formPanel.add(rightAlignedLabel("Login :"));            formPanel.add(memberLoginField);
        formPanel.add(rightAlignedLabel("Mot de passe :"));     formPanel.add(memberPasswordField);
        formPanel.add(rightAlignedLabel("Email :"));            formPanel.add(memberEmailField);
        formPanel.add(rightAlignedLabel("Téléphone :"));        formPanel.add(memberPhoneField);
        formPanel.add(rightAlignedLabel("Date de naissance :")); formPanel.add(memberBirthDateField);
        formPanel.add(rightAlignedLabel("Poids (kg) :"));       formPanel.add(memberWeightField);
        formPanel.add(rightAlignedLabel("Adresse :"));          formPanel.add(memberAddressField);

        JButton addButton    = createButton("Ajouter",   COLOUR_SUCCESS);
        JButton updateButton = createButton("Modifier",  COLOUR_ACCENT);
        JButton deleteButton = createButton("Supprimer", COLOUR_DANGER);
        JButton clearButton  = createButton("Effacer",   COLOUR_MUTED);

        addButton.addActionListener(e -> {
            try {
                adminController.registerMember(buildMemberFromForm(0));
                showSuccess(this,"Membre ajouté avec succès.");
                clearMemberForm();
                refreshMembersTable();
            } catch (NumberFormatException ex) { showError(this, "Le poids doit être un nombre valide."); }
              catch (powerHouseException ex)   { showError(this, ex.getMessage()); }
        });

        updateButton.addActionListener(e -> {
            if (selectedMemberId < 0) { showWarning(this, "Veuillez sélectionner un membre dans le tableau."); return; }
            try {
                adminController.updateMember(buildMemberFromForm(selectedMemberId));
                showSuccess(this, "Membre modifié avec succès.");
                clearMemberForm();
                refreshMembersTable();
            } catch (NumberFormatException ex) { showError(this, "Le poids doit être un nombre valide."); }
              catch (powerHouseException ex)   { showError(this, ex.getMessage()); }
        });

        deleteButton.addActionListener(e -> {
            if (selectedMemberId < 0) { showWarning(this, "Veuillez sélectionner un membre dans le tableau."); return; }
            if (!askConfirmation(this, "Supprimer ce membre ? Toutes ses inscriptions seront également supprimées.")) return;
            try {
                adminController.deleteMember(selectedMemberId);
                showSuccess(this, "Membre supprimé.");
                clearMemberForm();
                refreshAll();
            } catch (powerHouseException ex) { showError(this, ex.getMessage()); }
        });

        clearButton.addActionListener(e -> clearMemberForm());

        membersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && membersTable.getSelectedRow() >= 0) {
                int row = membersTable.getSelectedRow();
                selectedMemberId = (int) membersTableModel.getValueAt(row, 0);
                memberLastNameField.setText(nullToEmpty(membersTableModel.getValueAt(row, 1)));
                memberFirstNameField.setText(nullToEmpty(membersTableModel.getValueAt(row, 2)));
                memberLoginField.setText(nullToEmpty(membersTableModel.getValueAt(row, 3)));
                memberEmailField.setText(nullToEmpty(membersTableModel.getValueAt(row, 4)));
                memberPhoneField.setText(nullToEmpty(membersTableModel.getValueAt(row, 5)));
                memberBirthDateField.setText(nullToEmpty(membersTableModel.getValueAt(row, 6)));
                memberWeightField.setText(nullToEmpty(membersTableModel.getValueAt(row, 7)));
                memberPasswordField.setText("");
                memberAddressField.setText("");
            }
        });

        return buildTabPanel(formPanel, new JButton[]{addButton, updateButton, deleteButton, clearButton}, membersTable);
    }

    private member buildMemberFromForm(int memberId) throws powerHouseException {
        member member = new member();
        if (memberId > 0) member.setId(memberId);
        member.setNom(memberLastNameField.getText().trim());
        member.setPrenom(memberFirstNameField.getText().trim());
        member.setLogin(memberLoginField.getText().trim());
        member.setPassword(new String(memberPasswordField.getPassword()));
        member.setEmail(memberEmailField.getText().trim());
        member.setTelephone(memberPhoneField.getText().trim());
        member.setDateNaissance(memberBirthDateField.getText().trim());
        member.setAdresse(memberAddressField.getText().trim());
        if (!memberWeightField.getText().trim().isEmpty())
            member.setPoids(Double.parseDouble(memberWeightField.getText().trim()));
        return member;
    }

    private void clearMemberForm() {
        for (JTextField field : new JTextField[]{memberLastNameField, memberFirstNameField, memberLoginField,
                memberEmailField, memberPhoneField, memberBirthDateField, memberWeightField, memberAddressField})
            field.setText("");
        memberPasswordField.setText("");
        selectedMemberId = -1;
        membersTable.clearSelection();
    }

    private JPanel buildEnrollmentsTab() {
        JButton validateButton = createButton("✔ Valider",   COLOUR_SUCCESS);
        JButton refuseButton   = createButton("✕ Refuser",   COLOUR_DANGER);
        JButton deleteButton   = createButton("Supprimer",   COLOUR_MUTED);
        JButton refreshButton  = createButton("Actualiser",  COLOUR_ACCENT);

        validateButton.addActionListener(e -> applyEnrollmentAction(id -> adminController.validateEnrollment(id)));
        refuseButton.addActionListener(e   -> applyEnrollmentAction(id -> adminController.refuseEnrollment(id)));
        deleteButton.addActionListener(e   -> {
            if (enrollmentsTable.getSelectedRow() < 0) { showWarning(this ,"Veuillez sélectionner une inscription."); return; }
            if (!askConfirmation(this, "Supprimer cette inscription définitivement ?")) return;
            applyEnrollmentAction(id -> adminController.deleteEnrollment(id));
        });
        refreshButton.addActionListener(e -> refreshEnrollmentsTable());

        applyTableStyle(enrollmentsTable);
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JScrollPane(enrollmentsTable), BorderLayout.CENTER);
        panel.add(buildButtonPanel(validateButton, refuseButton, deleteButton, refreshButton), BorderLayout.SOUTH);
        return panel;
    }

    @FunctionalInterface
    interface EnrollmentOperation { void execute(int enrollmentId) throws powerHouseException; }

    private void applyEnrollmentAction(EnrollmentOperation operation) {
        int selectedRow = enrollmentsTable.getSelectedRow();
        if (selectedRow < 0) { showWarning(this, "Veuillez sélectionner une inscription."); return; }
        try {
            operation.execute((int) enrollmentsTableModel.getValueAt(selectedRow, 0));
            refreshEnrollmentsTable();
        } catch (powerHouseException ex) { showError(this, ex.getMessage()); }
    }
    

    private JPanel buildTabPanel(JPanel formPanel, JButton[] actionButtons, JTable dataTable) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, new JScrollPane(dataTable));
        splitPane.setDividerLocation(0.38);
        splitPane.setResizeWeight(0.38);
        splitPane.setBorder(null);

        JPanel tabPanel = new JPanel(new BorderLayout(0, 8));
        tabPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabPanel.add(splitPane, BorderLayout.CENTER);
        tabPanel.add(buildButtonPanel(actionButtons), BorderLayout.SOUTH);
        return tabPanel;
    }

    private JPanel buildButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        for (JButton button : buttons) {
            if (button.getPreferredSize().width < 120) button.setPreferredSize(new Dimension(120, 32));
            panel.add(button);
        }
        return panel;
    }

    private JLabel rightAlignedLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return label;
    }

    private void refreshAll() {
        refreshActivitiesTable();
        refreshMembersTable();
        refreshEnrollmentsTable();
    }

    private void refreshActivitiesTable() {
        try {
            activitiesTableModel.setRowCount(0);
            adminController.listActivitiesWithCount().forEach(row -> activitiesTableModel.addRow(row));
        } catch (powerHouseException ex) { showError(this, ex.getMessage()); }
    }

    private void refreshMembersTable() {
        try {
            membersTableModel.setRowCount(0);
            adminController.listAllMembers().forEach(member -> membersTableModel.addRow(new Object[]{
                member.getId(), member.getNom(), member.getPrenom(), member.getLogin(),
                member.getEmail(), member.getTelephone(), member.getDateNaissance(), member.getPoids()
            }));
        } catch (powerHouseException ex) { showError(this, ex.getMessage()); }
    }

    private void refreshEnrollmentsTable() {
        try {
            enrollmentsTableModel.setRowCount(0);
            adminController.listAllEnrollments().forEach(row -> enrollmentsTableModel.addRow(
                new Object[]{row[0], row[2] + " " + row[3], row[5], row[6]}));
        } catch (powerHouseException ex) { showError(this, ex.getMessage()); }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new adminDashboard().setVisible(true));
    }
}
