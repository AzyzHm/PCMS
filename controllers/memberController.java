package controllers;

import dao.enrollmentDAO;
import dao.memberDAO;
import exceptions.ActivityFullException;
import exceptions.AlreadyEnrolledException;
import exceptions.DatabaseException;
import exceptions.ExportException;
import exceptions.SecurityException;
import models.member;
import utils.PDFGenerator;
import java.util.List;

public class memberController {
    private enrollmentDAO enrollmentDAO;
    private memberDAO memberDAO;

    public memberController() {
        this.enrollmentDAO = new enrollmentDAO();
        this.memberDAO = new memberDAO();
    }

    public void enrollInActivity(member member, int activityId)
            throws DatabaseException, ActivityFullException, AlreadyEnrolledException {
        enrollmentDAO.enroll(member.getId(), activityId);
    }

    public void cancelEnrollment(member member, int activityId) throws DatabaseException {
        enrollmentDAO.cancelEnrollment(member.getId(), activityId);
    }

    // Retourne une liste d'objets contenant les détails de l'inscription (activité, statut, etc.)
    public List<Object[]> getMyEnrollments(member member) throws DatabaseException {
        return enrollmentDAO.getEnrollmentsByUser(member.getId());
    }

    public void changePassword(member member, String newPassword)
            throws DatabaseException, SecurityException {
        if (newPassword == null || newPassword.length() < 8) {
            throw new SecurityException("Le mot de passe doit contenir au moins 8 caractères", null);
        }
        memberDAO.updatePassword(member.getId(), newPassword);
        member.setFirstLogin(false);
    }

    public void downloadMemberCard(member member) throws ExportException {
        PDFGenerator.generateMemberCard(member);
    }
}