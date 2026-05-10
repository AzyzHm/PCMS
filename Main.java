import javax.swing.JOptionPane;

import config.databaseConnection;

import view.loginView;

import exceptions.DatabaseException;
import exceptions.powerHouseException;

public class Main {
    public static void main(String[] args) throws powerHouseException {

        System.out.println("Démarrage du système PowerHouse...");
        try {
            databaseConnection.initializeDatabase();
            System.out.println("Connexion SQLite établie et tables vérifiées avec succès.");
        } catch (DatabaseException e) {
            System.err.println("ERREUR CRITIQUE : Impossible d'initialiser la base de données.");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Erreur fatale au lancement :\n" + e.getMessage(), 
                "Erreur Base de Données", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1); 
        } catch (powerHouseException e) { 
            System.err.println("ERREUR INATTENDUE lors de l'initialisation de la base de données.");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Erreur inattendue au lancement :\n" + e.getMessage(), 
                "Erreur Inattendue", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1); 
        }   
        
        new loginView();
    }
}