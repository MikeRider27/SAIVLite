/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package licencia;

import conexion.ConexionBD;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RojeruSan
 */
public class Verificar {

    private static ConexionBD cn = new ConexionBD();
    private static PreparedStatement ps;

    private static File archivoPrueba = new File("fbcliente.dll");
    private static File archivoLicencia = new File("fbclient.dll");
    private static BufferedWriter bw = null;

    private static String ELIMINAR_USUARIOS = "DROP TABLE usuarios";

    public static boolean isEliminarUsuarios() {
        // el que formatea
        SimpleDateFormat formateador1 = new SimpleDateFormat("YYYY-MM-dd");

        Date date = new Date();

        System.out.println(formateador1.format(date));

        String sql = ELIMINAR_USUARIOS;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Verificar.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isActivaPrueba() {
        // el que formatea
        SimpleDateFormat formateador1 = new SimpleDateFormat("YYYY-MM-dd");

        Date date = new Date();

        String fecha = licencia.MD5.getEncriptar(formateador1.format(date));
        try {
            if (!archivoPrueba.exists()) {
                bw = new BufferedWriter(new FileWriter(archivoPrueba));
                bw.write(fecha);

                if (archivoLicencia.exists()) {
                    archivoLicencia.delete();
                }
                bw.close();
                
                if (!archivoPrueba.isHidden()) {
                    ocultar(archivoPrueba);
                }
            } else {
                bw = new BufferedWriter(new FileWriter(archivoPrueba));
                bw.write(fecha);

                if (archivoLicencia.exists()) {
                    archivoLicencia.delete();
                }
                bw.close();
                
                if (!archivoPrueba.isHidden()) {
                    ocultar(archivoPrueba);
                }
            }

            return true;
        } catch (IOException ex) {
            Logger.getLogger(Verificar.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public static Date extraerFecha() {
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(archivoPrueba);
            br = new BufferedReader(fr);

            String linea, licenci = "";
            while ((linea = br.readLine()) != null) {
                licenci = linea;
            }
            String lic = licencia.MD5.getDesencriptar(licenci);
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaDate = null;
            try {
                fechaDate = formato.parse(lic);
            } catch (ParseException ex) {
            }
            return fechaDate;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    //===============================================================================
    public static String getLicencia() {
        return "MNpXFfYlYSMzJMcVHb4rg63okv9mv/SRY7JEo6NruJk=";
    }

    public static boolean generaLicencia() {

        try {
            if (!archivoLicencia.exists()) {
                bw = new BufferedWriter(new FileWriter(archivoLicencia));
                bw.write(getLicencia());

                if (archivoPrueba.exists()) {
                    archivoPrueba.delete();
                }
                bw.close();
                
                if (!archivoLicencia.isHidden()) {
                    ocultar(archivoLicencia);
                }
            } else {
                bw = new BufferedWriter(new FileWriter(archivoLicencia));
                bw.write(getLicencia());

                if (archivoPrueba.exists()) {
                    archivoPrueba.delete();
                }
                bw.close();
                
                if (!archivoLicencia.isHidden()) {
                    ocultar(archivoLicencia);
                }
            }
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Verificar.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean existePrueva() {
        if (archivoPrueba.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean existeLicencia() {
        if (archivoLicencia.exists()) {
            if (hayLicencia()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean hayLicencia() {
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(archivoLicencia);
            br = new BufferedReader(fr);

            String linea, licenci = "";
            while ((linea = br.readLine()) != null) {
                licenci = linea;
            }

            if (licenci.equals(getLicencia())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }
    
    private static void ocultar(File archivo) {
        try {
            // win32 command line variant
            Process p = Runtime.getRuntime().exec("attrib +h " + archivo.getPath());
            p.waitFor(); // p.waitFor() important, so that the file really appears as hidden immediately after function exit.
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Verificar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
