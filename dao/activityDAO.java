package dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.databaseConnection;

import models.activity;

import java.util.ArrayList;
import java.util.List;

import exceptions.DatabaseException;

public class activityDAO {

    public void addActivity(activity a) throws DatabaseException {
        String sql = "INSERT INTO activities (nom, description, capacite_max, horaires) VALUES (?,?,?,?)";
        try (Connection conn = databaseConnection.getConnection();

             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getNom());
            ps.setString(2, a.getDescription());
            ps.setInt(3, a.getCapaciteMax());
            ps.setString(4, a.getHoraire());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Impossible de créer l'activité", e);
        }
    }

    public void updateActivity(activity a) throws DatabaseException {
        String sql = "UPDATE activities SET nom=?, description=?, capacite_max=?, horaires=? WHERE id=?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getNom());
            ps.setString(2, a.getDescription());
            ps.setInt(3, a.getCapaciteMax());
            ps.setString(4, a.getHoraire());
            ps.setInt(5, a.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Impossible de modifier l'activité", e);
        }
    }

    public void deleteActivity(int id) throws DatabaseException {
        String sql = "DELETE FROM activities WHERE id=?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Impossible de supprimer l'activité", e);
        }
    }

    public List<activity> getAllActivities() throws DatabaseException {
        List<activity> list = new ArrayList<>();
        String sql = "SELECT * FROM activities";
        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des activités", e);
        }
        return list;
    }

    public List<activity> getFullActivities() throws DatabaseException {
        List<activity> list = new ArrayList<>();
        String sql = "SELECT a.*, COUNT(e.id) as enrolled " +
                     "FROM activities a LEFT JOIN enrollments e ON a.id = e.activity_id AND e.status = 'ACCEPTEE' " +
                     "GROUP BY a.id HAVING enrolled >= a.capacite_max";
        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des activités complètes", e);
        }
        return list;
    }

    public List<Object[]> getActivitiesWithCount() throws DatabaseException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT a.id, a.nom, a.description, a.capacite_max, a.horaires, " +
                     "COUNT(e.id) as enrolled " +
                     "FROM activities a LEFT JOIN enrollments e ON a.id = e.activity_id AND e.status != 'REFUSEE' " +
                     "GROUP BY a.id";
        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id"), rs.getString("nom"), rs.getString("description"),
                    rs.getInt("capacite_max"), rs.getString("horaires"), rs.getInt("enrolled")
                });
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du comptage des participants", e);
        }
        return list;
    }

    private activity mapRow(ResultSet rs) throws SQLException {
        return new activity(rs.getInt("id"), rs.getString("nom"),
                rs.getString("description"), rs.getInt("capacite_max"), rs.getString("horaires"));
    }

}
