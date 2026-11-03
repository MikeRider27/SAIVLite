/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package splash;

import java.awt.Color;
import javax.swing.JProgressBar;

/**
 *
 * @author IsraelCM
 */
public class cargarSplash extends Thread {

    private JProgressBar progreso;
    private SplashScreen splash;

    public cargarSplash(JProgressBar progreso, SplashScreen splash) {
        super();
        this.progreso = progreso;
        this.splash = splash;
    }

    @Override
    public void run() {

        setProgress(0, "Cargando Componentes del Sistema", new Color(0, 145, 234));
        pausa(1000);
        setProgress(10, "Cargando Componentes del Sistema", new Color(74, 20, 140));

        pausa(2000);
        
        setProgress(20, "Conectandose a la Base de Datos...", new Color(13, 71, 161));
        pausa(2000);
        setProgress(50, "Conectandose a la Base de Datos...", new Color(0, 96, 100));
        setProgress(65, "Conectandose a la Base de Datos...", new Color(27, 94, 32));

        setProgress(70, "Cargando Modulos..", new Color(245, 127, 23));
        pausa(1000);
        setProgress(75, "Carga de Modulos Terminada", new Color(230, 81, 0));

        setProgress(80, "Cargando Interfaces..", new Color(255, 234, 0));
        pausa(1000);
        setProgress(87, "Cargando Interfaces..", new Color(0, 145, 234));
        setProgress(92, "Interfaces Cargadas", new Color(230, 81, 0));
        setProgress(100, "Cargas finalizadas", new Color(0, 230, 118));

    }

    public void pausa(int mlSeg) {
        try {
            Thread.sleep(mlSeg);
        } catch (Exception e) {

        }
    }

    public void setProgress(int valor, String información, Color color) {
        progreso.setValue(valor);
        progreso.setString(información);
        progreso.setForeground(color);


        pausa(1000);

        if (valor == 100) {
            pausa(1000);
            splash.dispose();
            new login.Login().setVisible(true);
        }
    }
}
