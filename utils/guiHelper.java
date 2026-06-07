package utils;

import java.awt.*;
import javax.swing.*;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class guiHelper{

    public static final Color COLOUR_BACKGROUND = new Color(245, 247 , 250);
    public static final Color COLOUR_ACCENT     = new Color(30,  90,  200);
    public static final Color COLOUR_DANGER     = new Color(190, 35,  35);
    public static final Color COLOUR_SUCCESS    = new Color(30,  140, 70);
    public static final Color COLOUR_DARK       = new Color(40,  40,  55);
    public static final Color COLOUR_MUTED      = new Color(100, 100, 120);

    public static JButton createButton(String label, Color backgroundColour) {
        JButton button = new JButton(label);
        button.setBackground(backgroundColour);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void applyTableStyle(JTable table) {
        table.setRowHeight(24);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(180, 210, 255));
        table.setSelectionForeground(COLOUR_DARK);
        table.setGridColor(new Color(220, 220, 230));
        table.setFillsViewportHeight(true);
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            { setHorizontalAlignment(SwingConstants.LEFT); }
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focused, row, col);
                setText(value == null ? "" : value.toString());
                setBackground(new Color(210, 215, 230));
                setForeground(COLOUR_DARK);
                setFont(new Font("SansSerif", Font.BOLD, 12));
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(170, 175, 195)),
                    BorderFactory.createEmptyBorder(3, 6, 3, 4)));
                setOpaque(true);
                return this;
            }
        });
    }

    public static DefaultTableModel createReadOnlyTableModel(String... columnNames) {
        return new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
    }



    public static String nullToEmpty(Object value) { 
        return value == null ? "" : value.toString(); 
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Succès", JOptionPane.INFORMATION_MESSAGE); 
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Attention", JOptionPane.WARNING_MESSAGE); 
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Erreur", JOptionPane.ERROR_MESSAGE); 
    }

    public static boolean askConfirmation(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
