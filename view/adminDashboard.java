package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static utils.guiHelper.*;

import exceptions.powerHouseException;

import controllers.adminController;

public class adminDashboard extends JFrame{

    private final adminController adminController = new adminController();

    private final DefaultTableModel activitiesTableModel = createReadOnlyTableModel("ID", "Nom", "Description", "Capacité", "Horaires", "Inscrits");
    private final JTable     activitiesTable       = new JTable(activitiesTableModel);
    private final JTextField activityNameField     = new JTextField();
    private final JTextField activityDescField     = new JTextField();
    private final JTextField activityCapacityField = new JTextField(5);
    private final JTextField activityScheduleField = new JTextField();
    private int selectedActivityId = -1;

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

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Activités", buildActivitiesTab());
        
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
    }

    private void refreshActivitiesTable() {
        try {
            activitiesTableModel.setRowCount(0);
            adminController.listActivitiesWithCount().forEach(row -> activitiesTableModel.addRow(row));
        } catch (powerHouseException ex) { showError(this, ex.getMessage()); }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new adminDashboard().setVisible(true));
    }
}
