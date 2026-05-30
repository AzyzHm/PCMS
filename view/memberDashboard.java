package view;

import controllers.adminController;
import controllers.memberController;

import exceptions.ActivityFullException;
import exceptions.AlreadyEnrolledException;
import exceptions.powerHouseException;

import models.member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static utils.guiHelper.*;

public class memberDashboard extends JFrame {

    private final member          loggedInMember;
    private final memberController memberController = new memberController();
    private final adminController  adminController  = new adminController();

    private final DefaultTableModel availableActivitiesModel = createReadOnlyTableModel(
            "ID", "Activité", "Description", "Capacité", "Horaires", "Inscrits");
    private final JTable availableActivitiesTable = new JTable(availableActivitiesModel);

    private final DefaultTableModel myEnrollmentsModel = createReadOnlyTableModel(
            "Inscription ID", "Activité ID", "Activité", "Horaires", "Statut");
    private final JTable myEnrollmentsTable = new JTable(myEnrollmentsModel);

    public memberDashboard(member member) {
        // CORRECTION : On initialise la variable globale immédiatement pour éviter le NullPointerException
        this.loggedInMember = member;

        setTitle("PowerHouse – Tableau de bord - Tableau de bord de " + member.getNom());
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        ImageIcon icon = new ImageIcon("icons/Dashboard.png");
        setIconImage(icon.getImage());

        applyTableStyle(availableActivitiesTable);
        applyTableStyle(myEnrollmentsTable);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Activités disponibles", buildAvailableActivitiesTab());
        tabbedPane.addTab("Mes inscriptions",      buildMyEnrollmentsTab());

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(buildHeaderBar(), BorderLayout.NORTH);
        rootPanel.add(tabbedPane,       BorderLayout.CENTER);
        setContentPane(rootPanel);

        refreshAll();

        if (member.isFirstLogin())
            SwingUtilities.invokeLater(() -> new ChangePasswordView(this, member).setVisible(true));
    }

    // ── HEADER BAR ────────────────────────────────────────────────────────

    private JPanel buildHeaderBar() {
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(COLOUR_DARK);
        headerBar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        // Utilise maintenant loggedInMember initialisé en toute sécurité
        JLabel titleLabel = new JLabel("⚡ POWERHOUSE  —  " + loggedInMember.getLogin().toUpperCase());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        JButton logoutButton = createButton("Déconnexion", COLOUR_DANGER);
        logoutButton.setPreferredSize(new Dimension(150, 28));
        logoutButton.addActionListener(e -> { new loginView().setVisible(true); dispose(); });

        headerBar.add(titleLabel,   BorderLayout.WEST);
        headerBar.add(logoutButton, BorderLayout.EAST);
        return headerBar;
    }

    // ── AVAILABLE ACTIVITIES TAB ───────────────────────────────────────────

    private JPanel buildAvailableActivitiesTab() {
        JButton enrollButton  = createButton("S'inscrire",  COLOUR_SUCCESS);
        JButton refreshButton = createButton("Actualiser",  COLOUR_ACCENT);

        enrollButton.addActionListener(e -> handleEnrollment());
        refreshButton.addActionListener(e -> refreshAvailableActivities());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        for (JButton button : new JButton[]{enrollButton, refreshButton}) { 
            button.setPreferredSize(new Dimension(130, 32)); 
            buttonPanel.add(button); 
        }

        JPanel tabPanel = new JPanel(new BorderLayout(0, 8));
        tabPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabPanel.add(new JScrollPane(availableActivitiesTable), BorderLayout.CENTER);
        tabPanel.add(buttonPanel, BorderLayout.SOUTH);
        return tabPanel;
    }

    private void handleEnrollment() {
        int selectedRow = availableActivitiesTable.getSelectedRow();
        if (selectedRow < 0) { showWarning(this, "Veuillez sélectionner une activité."); return; }
        int    activityId   = (int) availableActivitiesModel.getValueAt(selectedRow, 0);
        String activityName = nullToEmpty(availableActivitiesModel.getValueAt(selectedRow, 1));
        try {
            memberController.enrollInActivity(loggedInMember, activityId);
            showSuccess(this ,"Votre demande d'inscription à « " + activityName + " » a bien été envoyée.\n"
                      + "Elle est en attente de validation par l'administrateur.");
            refreshAll();
        } catch (ActivityFullException ex)      { showWarning(this, ex.getMessage()); }
          catch (AlreadyEnrolledException ex)   { showWarning(this, ex.getMessage()); }
          catch (powerHouseException ex)        { showError(this, ex.getMessage()); }
    }

    // ── MY ENROLLMENTS TAB ────────────────────────────────────────────────

    private JPanel buildMyEnrollmentsTab() {
        JButton cancelButton  = createButton("Annuler l'inscription", COLOUR_DANGER);
        JButton refreshButton = createButton("Actualiser",            COLOUR_ACCENT);
        JButton pdfButton     = createButton("Ma carte PDF",          COLOUR_MUTED);

        cancelButton.addActionListener(e  -> handleCancelEnrollment());
        refreshButton.addActionListener(e -> refreshMyEnrollments());
        pdfButton.addActionListener(e     -> handlePdfCardDownload());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        for (JButton button : new JButton[]{cancelButton, refreshButton, pdfButton}) { 
            button.setPreferredSize(new Dimension(175, 32)); 
            buttonPanel.add(button); 
        }

        JPanel tabPanel = new JPanel(new BorderLayout(0, 8));
        tabPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabPanel.add(new JScrollPane(myEnrollmentsTable), BorderLayout.CENTER);
        tabPanel.add(buttonPanel, BorderLayout.SOUTH);
        return tabPanel;
    }

    private void handleCancelEnrollment() {
        int selectedRow = myEnrollmentsTable.getSelectedRow();
        if (selectedRow < 0) { showWarning(this, "Veuillez sélectionner une inscription à annuler."); return; }
        String enrollmentStatus = nullToEmpty(myEnrollmentsModel.getValueAt(selectedRow, 4));
        if ("ACCEPTEE".equals(enrollmentStatus)
                && !askConfirmation(this, "Cette inscription a déjà été acceptée par l'administrateur.\nConfirmer l'annulation ?"))
            return;
        int activityId = (int) myEnrollmentsModel.getValueAt(selectedRow, 1);
        try {
            memberController.cancelEnrollment(loggedInMember, activityId);
            showSuccess(this, "Votre inscription a été annulée.");
            refreshAll();
        } catch (powerHouseException ex) { showError(this, ex.getMessage()); }
    }

    private void handlePdfCardDownload() {
        try {
            memberController.downloadMemberCard(loggedInMember);
            showSuccess(this, "Votre carte membre a été générée dans le dossier de l'application.");
        } catch (Exception ex) { showError(this, ex.getMessage()); }
    }


    private void refreshAll() { 
        refreshAvailableActivities();
        refreshMyEnrollments(); }

    private void refreshAvailableActivities() {
        try {
            availableActivitiesModel.setRowCount(0);
            adminController.listActivitiesWithCount().forEach(row -> availableActivitiesModel.addRow(row));
        } catch (powerHouseException ex) { showError(this, ex.getMessage()); }
    }

    private void refreshMyEnrollments() {
        try {
            myEnrollmentsModel.setRowCount(0);
            memberController.getMyEnrollments(loggedInMember).forEach(row -> myEnrollmentsModel.addRow(row));
        } catch (powerHouseException ex) { showError(this, ex.getMessage()); }
    }
}