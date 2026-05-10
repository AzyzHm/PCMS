package models;

import exceptions.ValidationException;

public class member extends user{
        private String DateNaissance;
        private String adresse;
        private String Telephone;
        private double Poids;
        private boolean PremierLogin;

    public member(){
        this.setRole("MEMBER");
        this.PremierLogin = true;
        }

    public String getDateNaissance() {
        return DateNaissance;
        }
    public void setDateNaissance(String dateNaissance) {
        this.DateNaissance = dateNaissance;
        }

    public String getAdresse() {
        return adresse;
        }
    public void setAdresse(String adresse) {
        this.adresse = adresse;
        }

    public String getTelephone() {
        return Telephone;
        }
    public void setTelephone(String telephone) {
        this.Telephone = telephone;
        }

    public double getPoids() {
        return Poids;
        }
    public void setPoids(double poids) throws ValidationException{
        if (poids <= 0) {
            throw new ValidationException("Poids", "Poids doit être un nombre positif");
        }
        this.Poids = poids;
        }

    public boolean isFirstLogin() {
        return PremierLogin;
        }
    public void setFirstLogin(boolean PremierLogin) {
        this.PremierLogin = PremierLogin;
        }
}