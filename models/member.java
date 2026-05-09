package models;

import exceptions.ValidationException;

public class member extends user{
        private String Birthday;
        private String adresse;
        private String phone;
        private double Weight;
        private boolean firstLogin;

    public member(){
        this.setRole("MEMBER");
        this.firstLogin = true;
        }

    public String getBirthday() {
        return Birthday;
        }
    public void setBirthday(String birthday) {
        this.Birthday = birthday;
        }

    public String getAdresse() {
        return adresse;
        }
    public void setAdresse(String adresse) {
        this.adresse = adresse;
        }

    public String getPhone() {
        return phone;
        }
    public void setPhone(String phone) {
        this.phone = phone;
        }

    public double getWeight() {
        return Weight;
        }
    public void setWeight(double weight) throws ValidationException{
        if (weight <= 0) {
            throw new ValidationException("weight", "Weight must be a positive number");
        }
        this.Weight = weight;
        }

    public boolean isFirstLogin() {
        return firstLogin;
        }
    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
        }
}