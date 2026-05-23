package controllers;

import java.util.List;

import dao.activityDAO;

import models.activity;

import exceptions.DatabaseException;
import exceptions.ValidationException;

public class adminController {

    private activityDAO activityDAO;

    public adminController() {
        this.activityDAO = new activityDAO();
    }

    public void createActivity(String nom, String desc, int cap, String horaires)
            throws DatabaseException, ValidationException {
        activity act = new activity();
        act.setNom(nom);
        act.setDescription(desc);
        act.setCapaciteMax(cap);
        act.setHoraire(horaires);
        activityDAO.addActivity(act);
    }

    public void updateActivity(int id, String nom, String desc, int cap, String horaires)
            throws DatabaseException, ValidationException {
        activity act = new activity();
        act.setId(id);
        act.setNom(nom);
        act.setDescription(desc);
        act.setCapaciteMax(cap);
        act.setHoraire(horaires);
        activityDAO.updateActivity(act);
    }

    public void deleteActivity(int id) throws DatabaseException {
        activityDAO.deleteActivity(id);
    }

    public List<activity> listAllActivities() throws DatabaseException {
        return activityDAO.getAllActivities();
    }

    public List<Object[]> listActivitiesWithCount() throws DatabaseException {
        return activityDAO.getActivitiesWithCount();
    }

    public List<activity> listFullActivities() throws DatabaseException {
        return activityDAO.getFullActivities();
    }

    
}