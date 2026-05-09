package models;

import exceptions.ValidationException;

public class activity {
    private int id;
    private String nom;
    private String description;
    private int maxCapacity;
    private String Time;

    public activity() {}

    public activity(int id, String nom, String description, int maxCapacity, String Time) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.maxCapacity = maxCapacity;
        this.Time = Time;
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
            throw new ValidationException("nom", "Name cannot be null or empty");
        }
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
    public void setMaxCapacity(int maxCapacity) throws ValidationException {
        if (maxCapacity <= 0) {
            throw new ValidationException("maxCapacity", "Maximum capacity must be a positive number");
        }
        this.maxCapacity = maxCapacity;
    }

    public String getTime() {
        return Time;
    }
    public void setTime(String Time) {
        this.Time = Time;
    }
}
