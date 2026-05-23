package dao;

import config.databaseConnection;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import models.member;
import models.user;
import models.admin;

import utils.passwordHasher;

import exceptions.DatabaseException;
import exceptions.ValidationException;
import exceptions.SecurityException;

public class memberDAO {

    public user authenticate(String login, String password) throws DatabaseException, ValidationException, SecurityException {
        String sql = "SELECT * FROM users WHERE login = ? AND password = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, passwordHasher.hashPassword(password));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                if ("ADMIN".equals(role)) {
                    admin admin = new admin();
                    fillUserFields(admin, rs);
                    return admin;
                } else {
                    return mapMember(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Erreur d'authentification", e);
        }
    }

    private member mapMember(ResultSet rs) throws SQLException {
        member member = new member();
        try {
            fillUserFields(member, rs);
        } catch (ValidationException e) {
            throw new SQLException("Données utilisateur invalides dans la base de données", e);
        }
        member.setDateNaissance(rs.getString("date_naissance"));
        member.setAdresse(rs.getString("adresse"));
        member.setTelephone(rs.getString("telephone"));
        
        try{
            member.setPoids(rs.getDouble("poids"));
        } catch (ValidationException e) {
            throw new SQLException("Données de poids invalides dans la base de données", e);
        }

        member.setFirstLogin(rs.getInt("first_login") == 1);
        return member;
    }

    private void fillUserFields(user u, ResultSet rs) throws SQLException, ValidationException {
        u.setId(rs.getInt("id"));
        u.setLogin(rs.getString("login"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
    }


    public void addMember(member m) throws DatabaseException, SecurityException {
        String sql = "INSERT INTO users (role, login, password, nom, prenom, date_naissance, adresse, telephone, email, poids) " +
                     "VALUES ('MEMBER',?,?,?,?,?,?,?,?,?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getLogin());
            ps.setString(2, passwordHasher.hashPassword(m.getPassword()));
            ps.setString(3, m.getNom());
            ps.setString(4, m.getPrenom());
            ps.setString(5, m.getDateNaissance());
            ps.setString(6, m.getAdresse());
            ps.setString(7, m.getTelephone());
            ps.setString(8, m.getEmail());
            ps.setDouble(9, m.getPoids());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                throw new DatabaseException("Ce login est déjà utilisé. Veuillez en choisir un autre.", e);
            }
            throw new DatabaseException("Erreur lors de l'ajout du membre", e);
        }
    }

    public void updateMember(member m) throws DatabaseException {
        String sql = "UPDATE users SET nom=?, prenom=?, date_naissance=?, adresse=?, telephone=?, email=?, poids=? WHERE id=?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getPrenom());
            ps.setString(3, m.getDateNaissance());
            ps.setString(4, m.getAdresse());
            ps.setString(5, m.getTelephone());
            ps.setString(6, m.getEmail());
            ps.setDouble(7, m.getPoids());
            ps.setInt(8, m.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la modification du membre", e);
        }
    }

    public void deleteMember(int id) throws DatabaseException {
        String sql = "DELETE FROM users WHERE id=? AND role='MEMBER'";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression du membre", e);
        }
    }

    public List<member> getAllMembers() throws DatabaseException {
        List<member> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='MEMBER'";
        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapMember(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération des membres", e);
        }
        return list;
    }
}
