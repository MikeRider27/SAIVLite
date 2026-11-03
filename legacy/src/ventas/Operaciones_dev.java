/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventas;

import conexion.ConexionBD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author RojeruSan
 */
public class Operaciones_dev {
    private static ConexionBD cn = new ConexionBD();
    static PreparedStatement ps;
    
    public static boolean isRegistrado(Sentencias_Dev uc) {
        String sql = Sentencias_Dev.REGISTRAR;
        try {
            ps = cn.conect.prepareStatement(sql);
//            ps.setInt(1, uc.getIdVenta());
            ps.setInt(1, uc.getNumVenta());
            ps.setString(2, uc.getCodigoVenta());
            ps.setInt(3, uc.getCantidadVenta());
            ps.setDouble(4, uc.getImporteVenta());
            ps.setString(5, uc.getFechaVenta());
            ps.setString(6, uc.getHoraVenta());
            ps.setString(7, uc.getCajero());
            ps.setString(8, uc.getCategoria());
            ps.setDouble(9, uc.getPrecio());
            ps.setDouble(10, uc.getPrecio_venta());
            ps.setString(11, uc.getDescripcion());
            ps.setString(12, uc.getMotivo());
            ps.setBoolean(13, uc.isInventario_utiliza());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones_dev.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isVaciado() {
        int rsu = 0;
        String sql = Sentencias_Dev.VACIAR;

        try {
            ps = cn.conect.prepareStatement(sql);
            rsu = ps.executeUpdate();
            rsu++;
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println(sql);
        return false;
    }
    
    public static boolean isEliminado(int id) {
        int rsu = 0;
        String sql = Sentencias_Dev.ELIMINAR;

        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, id);
            rsu = ps.executeUpdate();
            rsu++;
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println(sql);
        return false;
    }
    
    public static boolean isEditadoVentas(Sentencias_Dev uc) {
        String sql = Sentencias_Dev.ACTUALIZAR_VENTAS;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getCantidadVenta());
            ps.setDouble(2, uc.getImporteVenta());
            ps.setInt(3, uc.getIdVenta());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
        }
        System.out.println(sql);
        return false;
    }
    
    public static boolean isEditadoDevoluciones(Sentencias_Dev uc) {
        String sql = Sentencias_Dev.ACTUALIZAR_DEVOLUCIONES;
        try {
             ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getCantidadVenta());
            ps.setDouble(2, uc.getImporteVenta());
            ps.setString(3, uc.getMotivo());
            ps.setInt(4, uc.getIdVenta());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
        }
        System.out.println(sql);
        return false;
    }

    public static void setListar(String busca) {
        DefaultTableModel modelo = (DefaultTableModel) pnlDevoluciones.tabla.getModel();

        while (modelo.getRowCount() > 0) {
            modelo.removeRow(0);
        }
        String sql = "";
        if (busca.equals("")) {
            sql = Sentencias_Dev.LISTAR;
        } else {
            sql = "SELECT * FROM devoluciones WHERE (fecha LIKE'" + busca + "%')";
        }
        String datos[] = new String[9];
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString("id");
                datos[1] = rs.getString("num_venta");
                datos[2] = rs.getString("cantidad");
                datos[3] = rs.getString("descripcion");
                datos[4] = rs.getString("precio_venta");
                datos[5] = rs.getString("importe");
                datos[6] = rs.getString("hora");
                datos[7] = rs.getString("cajero");
                datos[8] = rs.getString("inventario_utiliza");
                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones_dev.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean isExiste(int idP) {
        int i = 0;
        String SQL = "SELECT * FROM devoluciones WHERE id = " + idP;
        System.out.println(SQL);
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                i = rs.getInt(1);
            }
            
            System.out.println(i);
            
            if(i != 0){
                return true;
            }else{
                return false;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones_dev.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static int getCantidad(int idP) {
        int stock = 0;
        String SQL = "SELECT cantidad FROM devoluciones WHERE id = " + idP;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                stock = rs.getInt(1);
            }
            return stock;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return stock;
    }
    
    public static String getMotivo(int idP) {
        String motivo = null;
        String SQL = "SELECT motivo FROM devoluciones WHERE id = " + idP;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                motivo = rs.getString(1);
            }
            return motivo;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return motivo;
    }
}
