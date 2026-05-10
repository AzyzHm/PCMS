package models;

import exceptions.ValidationException;

public class activity {
    private int id;
    private String nom;
    private String description;
    private int CapaciteMax;
    private String Horaire;

    public activity() {}

    public activity(int id, String nom, String description, int CapaciteMax, String Horaire) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.CapaciteMax = CapaciteMax;
        this.Horaire = Horaire;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) throws ValidationException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new ValidationException("nom", "Le nom de l'activité ne peut pas être vide");
        }
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapaciteMax() {
        return CapaciteMax;
    }
    public void setCapaciteMax(int CapaciteMax) throws ValidationException {
        if (CapaciteMax <= 0) {
            throw new ValidationException("CapaciteMax", "La capacité maximale doit être un nombre positif");
        }
        this.CapaciteMax = CapaciteMax;
    }

    public String getHoraire() {
        return Horaire;
    }
    public void setHoraire(String Horaire) {
        this.Horaire = Horaire;
    }
}
