package dao;

import config.databaseConnection;

import java.sql.*;

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
}
