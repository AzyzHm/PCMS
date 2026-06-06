package dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import java.util.ArrayList;
import java.util.List;

import config.databaseConnection;

import exceptions.DatabaseException;
import exceptions.*;

public class enrollmentDAO {


    public void enroll(int userId, int activityId) throws DatabaseException, ActivityFullException, AlreadyEnrolledException {
        try (Connection conn = databaseConnection.getConnection()) {
            // Check already enrolled
            String dupSql = "SELECT COUNT(*) FROM enrollments WHERE user_id=? AND activity_id=?";
            try (PreparedStatement ps = conn.prepareStatement(dupSql)) {
                ps.setInt(1, userId);
                ps.setInt(2, activityId);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new AlreadyEnrolledException("Ce membre", "cette activité");
                }
            }

            // verfier la capacité de l'activité
            String capSql = "SELECT a.capacite_max, " +
                            "(SELECT COUNT(*) FROM enrollments WHERE activity_id=? AND status IN ('ACCEPTEE','EN_ATTENTE')) as taken " +
                            "FROM activities a WHERE a.id=?";
            try (PreparedStatement ps = conn.prepareStatement(capSql)) {
                ps.setInt(1, activityId);
                ps.setInt(2, activityId);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt("taken") >= rs.getInt("capacite_max")) {
                    throw new ActivityFullException("Activité ID " + activityId);
                }
            }

            String sql = "INSERT INTO enrollments (user_id, activity_id, status) VALUES (?,?,'EN_ATTENTE')";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setInt(2, activityId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur d'inscription", e);
        }
    }

    public void cancelEnrollment(int userId, int activityId) throws DatabaseException {
        String sql = "DELETE FROM enrollments WHERE user_id=? AND activity_id=?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, activityId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de l'annulation de l'inscription", e);
        }
    }
            
    public void deleteEnrollment(int enrollmentId) throws DatabaseException {
        String sql = "DELETE FROM enrollments WHERE id=?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression de l'inscription", e);
        }
    }

    public void updateStatus(int enrollmentId, String status) throws DatabaseException {
        String sql = "UPDATE enrollments SET status=? WHERE id=?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, enrollmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise à jour du statut", e);
        }
    }

    public List<Object[]> getAllEnrollments() throws DatabaseException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT e.id, u.id as uid, u.nom, u.prenom, a.id as aid, a.nom as anom, e.status " +
                     "FROM enrollments e " +
                     "JOIN users u ON e.user_id = u.id " +
                     "JOIN activities a ON e.activity_id = a.id " +
                     "ORDER BY e.id DESC";
        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id"), rs.getInt("uid"),
                    rs.getString("nom"), rs.getString("prenom"),
                    rs.getInt("aid"), rs.getString("anom"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des inscriptions", e);
        }
        return list;
    }

    public List<Object[]> getEnrollmentsByUser(int userId) throws DatabaseException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT e.id, a.id as aid, a.nom, a.horaires, e.status " +
                     "FROM enrollments e JOIN activities a ON e.activity_id = a.id " +
                     "WHERE e.user_id=? ORDER BY e.id DESC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id"), rs.getInt("aid"),
                    rs.getString("nom"), rs.getString("horaires"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération de vos inscriptions", e);
        }
        return list;
    }

    public List<Object[]> getMostActiveMembers() throws DatabaseException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT u.id, u.nom, u.prenom, u.login, COUNT(e.id) as total " +
                     "FROM users u LEFT JOIN enrollments e ON u.id = e.user_id AND e.status='ACCEPTEE' " +
                     "WHERE u.role='MEMBER' " +
                     "GROUP BY u.id ORDER BY total DESC LIMIT 10";
        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id"), rs.getString("nom"),
                    rs.getString("prenom"), rs.getString("login"),
                    rs.getInt("total")
                });
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des membres actifs", e);
        }
        return list;
    }
}
