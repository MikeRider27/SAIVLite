package configuracion;

import alertas.CargandoBackup;
import alertas.SuccessAlert;
import alertas.WarningAlert;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Rojeru San
 */
public class Backup {

    private String obtenerFecha() {
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        String Fehca = dia + "-" + (mes + 1) + "-" + año;
        return Fehca;
    }

    public void exportDB(String nombreDB) {

        String path = "respaldos/" + nombreDB + "_" + obtenerFecha() + ".FDB";
        File existe = new File(path);
        if (existe.exists()) {
            WarningAlert w = new WarningAlert(new JFrame(), true);
            w.msj1.setText("Ya existe un respaldo con ese nombre");
            w.msj2.setText("¿Deseas reemplazarlo?");
            w.msj3.setText("");
            w.setVisible(true);

            if (w.hecho) {
                generaBackup(path);
            }

        } else {
            generaBackup(path);
        }
    }

    private void generaBackup(String respaldo) {
        new CargandoBackup(new JFrame(), true).setVisible(true);
        
        File origen = new File("bd/PUNTO_VENTAS.FDB");
        File destino = new File(respaldo);

        try {
            InputStream in = new FileInputStream(origen);
            OutputStream out = new FileOutputStream(destino);

            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            SuccessAlert s = new SuccessAlert(new JFrame(), true);
            s.msj1.setText("Respaldo generado con éxito.");
            s.msj2.setText("");
            s.msj3.setText("");
            s.setVisible(true);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
