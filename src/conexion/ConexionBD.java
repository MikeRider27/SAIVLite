package conexion;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RojeruSan
 */
public class ConexionBD {

    public static Connection conect = null;

    public void conexion() {

        try {
            Class.forName("org.postgresql.Driver");
            conect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/punto_venta","postgres","dgtic123");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void desconectar() {
        try {
            conect.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//
//    public static void main(String[] args) {
////        new ConexionBD().conexion();
//        
//        try {
//            Scanner s = new Scanner(Runtime.getRuntime().exec("gbak -backup -v -user ROGER -password roger bd/PUNTO_VENTAS.FDB respaldos/backup.fbk").getInputStream());
//            System.out.println("Backup realizado com sucesso!");
//            System.exit(0);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
}
