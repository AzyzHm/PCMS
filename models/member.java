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

    public String getBirthday() {
        return DateNaissance;
        }
    public void setBirthday(String dateNaissance) {
        this.DateNaissance = dateNaissance;
        }

    public String getAdresse() {
        return adresse;
        }
    public void setAdresse(String adresse) {
        this.adresse = adresse;
        }

    public String getPhone() {
        return Telephone;
        }
    public void setPhone(String telephone) {
        this.Telephone = telephone;
        }

    public double getWeight() {
        return Poids;
        }
    public void setWeight(double weight) throws ValidationException{
        if (weight <= 0) {
            throw new ValidationException("Poids", "Poids doit être un nombre positif");
        }
        this.Poids = weight;
        }

    public boolean isFirstLogin() {
        return PremierLogin;
        }
    public void setFirstLogin(boolean PremierLogin) {
        this.PremierLogin = PremierLogin;
        }
}