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
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            conect = DriverManager.getConnection("jdbc:firebirdsql:embedded:./bd/PUNTO_VENTAS.FDB",
                    "roger", "roger");
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
