package controllers;

import java.util.List;

import dao.activityDAO;
import dao.memberDAO;

import models.activity;
import models.member;

import exceptions.DatabaseException;
import exceptions.ValidationException;
import exceptions.SecurityException;

public class adminController {

    private activityDAO activityDAO;
    private memberDAO memberDAO;

    public adminController() {
        this.activityDAO = new activityDAO();
        this.memberDAO = new memberDAO();
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

    public void registerMember(member member) throws DatabaseException, ValidationException, SecurityException {
        memberDAO.addMember(member);
    }

    public void updateMember(member member) throws DatabaseException {
        memberDAO.updateMember(member);
    }

    public void deleteMember(int id) throws DatabaseException {
        memberDAO.deleteMember(id);
    }

    public List<member> listAllMembers() throws DatabaseException {
        return memberDAO.getAllMembers();
    }
}