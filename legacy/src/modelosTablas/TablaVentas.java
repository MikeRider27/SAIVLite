/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelosTablas;

import almacen.Operaciones;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author RojeruSan
 */
public class TablaVentas extends DefaultTableCellRenderer {

    private Component componenete;
    private Color colorBackgound1 = new Color(255, 255, 255);
    private Color colorForeground1 = new Color(0, 0, 0);

    private Color colorBackgound2 = new Color(240, 240, 240);
    private Color colorForeground2 = new Color(0, 0, 0);

    private Color colorSelBackgound = new Color(69, 87, 252);
    private Color colorSelForeground = new Color(255, 255, 255);

    private int grosorBorde = 1;
    private Color colorBorde = new Color(0, 0, 0);

    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
        componenete = super.getTableCellRendererComponent(table, value, selected, focused, row, column);
        this.setHorizontalAlignment(0);

        if (row % 2 == 0 && column != 4) {
            componenete.setForeground(colorForeground1);
            componenete.setBackground(colorBackgound1);
        } else {
            componenete.setForeground(colorForeground2);
            componenete.setBackground(colorBackgound2);
        }

        if (column == 1) {
            componenete.setBackground(new Color(255, 233, 56));
            componenete.setForeground(Color.BLACK);
        }

        String id = table.getValueAt(row, 1).toString();

        String ilimitados = table.getValueAt(row, 4).toString();
        int existencias = 0;

        if (!ilimitados.equals("Ilimitadas")) {
            existencias = Integer.valueOf(table.getValueAt(row, 4).toString());
        }

        String stockMin = Operaciones.getStockMinVentas(id);

        if (column == 4) {

            int stock = Integer.parseInt(stockMin);

            componenete.setBackground(new Color(0, 204, 102));
            componenete.setForeground(Color.WHITE);

            if (!ilimitados.equals("Ilimitadas")) {
                if (existencias <= (stock + (stock / 2)) && column == 4) {
                    componenete.setBackground(new Color(255, 198, 0));
                    componenete.setForeground(Color.WHITE);
                }

                if (existencias <= stock && column == 4) {
                    componenete.setBackground(new Color(243, 66, 53));
                    componenete.setForeground(Color.WHITE);
                }
            }
        }

        if (selected) {
            componenete.setForeground(colorSelForeground);
            componenete.setBackground(colorSelBackgound);
        }
        return componenete;
    }
}
