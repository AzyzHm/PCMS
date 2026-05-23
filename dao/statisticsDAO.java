package dao;

import config.databaseConnection;
import exceptions.DatabaseException;
import java.sql.*;

public class statisticsDAO {

    public String getMostPopularActivity() throws DatabaseException {
        String sql = "SELECT a.nom, COUNT(e.id) as total " +
                     "FROM activities a " +
                     "LEFT JOIN enrollments e ON a.id = e.activity_id " +
                     "GROUP BY a.id ORDER BY total DESC LIMIT 1";
        
        try (Connection conn = databaseConnection.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int count = rs.getInt("total");
                return rs.getString("nom") + " (" + count + " membre" + (count > 1 ? "s" : "") + ")";
            }
            return "Aucune donnée disponible";
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du calcul des statistiques", e);
        }
    }
}