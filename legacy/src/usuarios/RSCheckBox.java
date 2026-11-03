/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usuarios;

import java.awt.Cursor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

/**
 *
 * @author RojeruSan
 */
public class RSCheckBox extends JCheckBox{

    public RSCheckBox() {
        this.setOpaque(false);
        this.setIcon(new ImageIcon(getClass().getResource("/img/usuarios/uncheck.png")));
        this.setSelectedIcon(new ImageIcon(getClass().getResource("/img/usuarios/check.png")));
        this.setCursor(new Cursor(12));
    }
    
}
