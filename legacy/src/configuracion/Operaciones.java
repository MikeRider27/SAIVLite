package configuracion;

import privilegios.*;
import conexion.ConexionBD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Rojeru San CL
 */
public class Operaciones {

    private static ConexionBD cn = new ConexionBD();
    static PreparedStatement ps;

    public static boolean isEditadoTicket(
            String negocio, String direccion, String telefono, String rfc,
            String otroMensaje, String gracias, String msjFinal
    ) {

        String sql = "UPDATE ticket SET "
                + "nogocio           = ?, "
                + "direccion         = ?, "
                + "telefono          = ?, "
                + "rfc               = ?, "
                + "otroTXT           = ?, "
                + "gracias           = ?, "
                + "msjFinal          = ? "
                + "WHERE id          = ?  ";

        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setString(1, negocio);
            ps.setString(2, direccion);
            ps.setString(3, telefono);
            ps.setString(4, rfc);
            ps.setString(5, otroMensaje);
            ps.setString(6, gracias);
            ps.setString(7, msjFinal);
            ps.setInt(8, 1);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
        }
        System.out.println(sql);
        return false;
    }
    
    public static boolean isVaciarDevoluciones() {
        String sql = "DELETE FROM devoluciones";

        try {
            ps = cn.conect.prepareStatement(sql);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
        }
        System.out.println(sql);
        return false;
    }
    
    public static boolean isVaciarVentas() {

        String sql = "DELETE FROM ventas";

        try {
            ps = cn.conect.prepareStatement(sql);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
        }
        System.out.println(sql);
        return false;
    }
    
    public static boolean isVaciarCaja() {

        String sql = "DELETE FROM caja";

        try {
            ps = cn.conect.prepareStatement(sql);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
        }
        System.out.println(sql);
        return false;
    }
    
    public static boolean isVaciarCajaTemp() {

        String sql = "DELETE FROM cajaTemporal";

        try {
            ps = cn.conect.prepareStatement(sql);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
        }
        System.out.println(sql);
        return false;
    }

    public static void getTicket() {
        String SQL = "SELECT * FROM ticket WHERE id = 1";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                Ticket.txtNegocio.setText(rs.getString(2));
                Ticket.txtDireccion.setText(rs.getString(3));
                Ticket.txtTelefono.setText(rs.getString(4));
                Ticket.txtRFC.setText(rs.getString(5));
                Ticket.txtOtraLinea.setText(rs.getString(6));
                Ticket.txtGracias.setText(rs.getString(7));
                Ticket.txtWWW.setText(rs.getString(8));
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public static String getNegocio() {
        String dato = "";
        String SQL = "SELECT * FROM ticket WHERE id = 1";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                dato = rs.getString(2);
            }

            return dato;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return dato;
    }
    
    public static String getDireccion() {
        String dato = "";
        String SQL = "SELECT * FROM ticket WHERE id = 1";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                dato = rs.getString(3);
            }

            return dato;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return dato;
    }
    
    public static String getTelefono() {
        String dato = "";
        String SQL = "SELECT * FROM ticket WHERE id = 1";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                dato = rs.getString(4);
            }

            return dato;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return dato;
    }
    
    public static String getRFC() {
        String dato = "";
        String SQL = "SELECT * FROM ticket WHERE id = 1";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                dato = rs.getString(5);
            }

            return dato;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return dato;
    }
    
    public static String getOtraLinea() {
        String dato = "";
        String SQL = "SELECT * FROM ticket WHERE id = 1";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                dato = rs.getString(6);
            }

            return dato;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return dato;
    }
    
    public static String getGracias() {
        String dato = "";
        String SQL = "SELECT * FROM ticket WHERE id = 1";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                dato = rs.getString(7);
            }

            return dato;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return dato;
    }
    
    public static String getWWW() {
        String dato = "";
        String SQL = "SELECT * FROM ticket WHERE id = 1";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                dato = rs.getString(8);
            }

            return dato;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return dato;
    }
}
