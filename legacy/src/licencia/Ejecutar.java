
package licencia;

import conexion.ConexionBD;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import principal.Principal;

/**
 *
 * @author RojeruSan
 */
public class Ejecutar {

    public static String diasRestantes = "";

    private boolean Trial() {
        try {
//            String fechaExpiracion = "20/11/2017";
            Date fechaActual = new Date();
            SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
            String fechaSistema = formateador.format(fechaActual);

            Date fechaSystem = formateador.parse(fechaSistema);
            Date fechaPrueba = Verificar.extraerFecha();

            final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al día 
            long diferencia = (fechaSystem.getTime() - fechaPrueba.getTime()) / MILLSECS_PER_DAY;
//            System.out.println("Días de prueba: 15");
//            System.out.println("Días restantes: "+(15-diferencia));

            diasRestantes = String.valueOf((15 - diferencia));

            if (diferencia > 15) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException ex) {
            Logger.getLogger(Ejecutar.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void verificaLicencia() {

        if (!Verificar.existePrueva() && !Verificar.existeLicencia()) {
            new TipoLicencia().setVisible(true);
        } else {

            if (Verificar.existePrueva()) {
                if (!new Ejecutar().Trial()) {
                    ContinuarPrueba c = new ContinuarPrueba();
                    c.lblDias.setText(diasRestantes + " días");
                    c.setVisible(true);
                } else {
                    new Expiroprueba().setVisible(true);
                }
            } else {
                new splash.SplashScreen().setVisible(true);
            }
        }
    }
    
    public String verificaLicencia1() {

        if (!new Ejecutar().Trial()) {
            return String.valueOf(diasRestantes);
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (new control.Control().comprobar()) {
            new ConexionBD().conexion();
            new Ejecutar().verificaLicencia();
        } else {
            System.exit(0);
        }
    }
}
