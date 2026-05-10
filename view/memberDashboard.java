package view;

import javax.swing.*;

import models.member;

public class memberDashboard extends JFrame{
    public memberDashboard(member member) {
        setTitle("PowerHouse – Tableau de bord - Tableau de bord de " + member.getNom());
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
    }
}