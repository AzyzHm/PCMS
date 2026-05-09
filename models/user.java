package models;
import exceptions.ValidationException;

public class user {
    protected int id;
    protected String nom;
    protected String prenom;
    protected String role;
    protected String email;
    protected String login; // comme username
    protected String password;

    public user(){}

    public user(int id, String nom, String prenom, String role, String email, String login, String password) {
        this.id = id;
        this.role = role;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.login = login;
        this.password = password;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws ValidationException {
         if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("email", "Invalid email format");
        }
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) throws ValidationException {
        if (login == null || login.length() < 6) {
            throw new ValidationException("login", "Login must be at least 6 characters long");
        }
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws ValidationException {
        if (password == null || password.length() < 8) {
            throw new ValidationException("password", "Password must be at least 8 characters long");
        }
        this.password = password;
    }
}
