package privilegios;

import conexion.ConexionBD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rojeru San CL
 */
public class Operaciones {

    private static ConexionBD cn = new ConexionBD();
    static PreparedStatement ps;

    public static boolean isRegistradoAlmacen(Sentencias uc) {
        String sql = Sentencias.REGISTRAR_ALMACEN;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getId_user());
            ps.setBoolean(2, uc.isRegistrar_cat());
            ps.setBoolean(3, uc.isRegistrar_pro());
            ps.setBoolean(4, uc.isEditar_pro());
            ps.setBoolean(5, uc.isEliminar_pro());
            ps.setBoolean(6, uc.isExportar());
            ps.setBoolean(7, uc.isImportar());
            ps.setBoolean(8, uc.isImprimir());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEditadoAlmacen(Sentencias uc) {
        String sql = Sentencias.ACTUALIZAR_ALMACEN;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setBoolean(1, uc.isRegistrar_cat());
            ps.setBoolean(2, uc.isRegistrar_pro());
            ps.setBoolean(3, uc.isEditar_pro());
            ps.setBoolean(4, uc.isEliminar_pro());
            ps.setBoolean(5, uc.isExportar());
            ps.setBoolean(6, uc.isImportar());
            ps.setBoolean(7, uc.isImprimir());
            ps.setInt(8, uc.getId_user());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEliminadoAlmacen(Sentencias uc) {
        String sql = Sentencias.ELIMINAR_ALMACEN;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getId_user());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    //-----------------------------------------------------------------------------------
    public static boolean isRegistradoVentas(Sentencias uc) {
        String sql = Sentencias.REGISTRAR_VENTAS;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getId_user());
            ps.setBoolean(2, uc.isRegistrar());
            ps.setBoolean(3, uc.isReportes());
            ps.setBoolean(4, uc.isUltimas_ventas());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEditadoVentas(Sentencias uc) {
        String sql = Sentencias.ACTUALIZAR_VENTAS;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setBoolean(1, uc.isRegistrar());
            ps.setBoolean(2, uc.isReportes());
            ps.setBoolean(3, uc.isUltimas_ventas());
            ps.setInt(4, uc.getId_user());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEliminadoVentas(Sentencias uc) {
        String sql = Sentencias.ELIMINAR_VENTAS;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getId_user());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    //-----------------------------------------------------------------------------------
    public static boolean isRegistradoCajero(Sentencias uc) {
        String sql = Sentencias.REGISTRAR_CAJERO;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getId_user());
            ps.setBoolean(2, uc.isRegistrarCajero());
            ps.setBoolean(3, uc.isEditar());
            ps.setBoolean(4, uc.isEliminar());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEditadoCajero(Sentencias uc) {
        String sql = Sentencias.ACTUALIZAR_CAJERO;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setBoolean(1, uc.isRegistrarCajero());
            ps.setBoolean(2, uc.isEditar());
            ps.setBoolean(3, uc.isEliminar());
            ps.setInt(4, uc.getId_user());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEliminadoCajero(Sentencias uc) {
        String sql = Sentencias.ELIMINAR_CAJERO;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getId_user());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    //-----------------------------------------------------------------------------------
    public static boolean isRegistradoConfig(Sentencias uc) {
        String sql = Sentencias.REGISTRAR_CONFIG;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getId_user());
            ps.setBoolean(2, uc.isCrear_backup());
            ps.setBoolean(3, uc.isRestore_backup());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEditadoConfig(Sentencias uc) {
        String sql = Sentencias.ACTUALIZAR_CONFIG;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setBoolean(1, uc.isCrear_backup());
            ps.setBoolean(2, uc.isRestore_backup());
            ps.setInt(3, uc.getId_user());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEliminadoConfig(Sentencias uc) {
        String sql = Sentencias.ELIMINAR_CONFIG;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getId_user());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    // Información sobre los PRIVILEGIOS VENTAS -------------------------------------------------
    public static boolean RegistrarVentas(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_ventas WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(3);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean ReportesVentas(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_ventas WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(4);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean UltimasVentas(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_ventas WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(5);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    // Información sobre los PRIVILEGIOS ALMACEN -------------------------------------------------
    public static boolean RegistrarCategoria(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_almacen WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(3);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean RegistrarProducto(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_almacen WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(4);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean EditarProducto(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_almacen WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(5);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean EliminarProducto(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_almacen WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(6);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean ExportarAlmacen(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_almacen WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(7);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean ImportarAlmacen(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_almacen WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(8);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean ImprimirAlmacen(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_almacen WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(9);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    // Información sobre los PRIVILEGIOS CAJERO -------------------------------------------------
    public static boolean RegistrarCajero(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_cajero WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(3);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean EditarCajero(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_cajero WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(4);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean EliminarCajero(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_cajero WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(5);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    // Información sobre los PRIVILEGIOS CONFIGURACIÓN -------------------------------------------------
    public static boolean crearBackup(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_config WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(3);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean restaurarBackup(String idUser) {
        boolean privilegio  = false;
        String SQL = "SELECT * FROM priv_config WHERE id_user = " + idUser;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                privilegio = rs.getBoolean(4);
            }

            if (privilegio) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
