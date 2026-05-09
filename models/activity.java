package models;

public class activity {
    private int id;
    private String nom;
    private String description;
    private String maxCapacity;
    private String Time;

    public activity() {}

    public activity(int id, String nom, String description, String maxCapacity, String Time) {
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
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getMaxCapacity() {
        return maxCapacity;
    }
    public void setMaxCapacity(String maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getTime() {
        return Time;
    }
    public void setTime(String Time) {
        this.Time = Time;
    }
}
