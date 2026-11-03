package usuarios;

import conexion.ConexionBD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Rojeru San CL
 */
public class Operaciones {

    private static ConexionBD cn = new ConexionBD();
    static PreparedStatement ps;

    public static boolean isRegistrado(Sentencias uc) {
        String sql = Sentencias.REGISTRAR;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getId());
            ps.setString(2, uc.getNombre());
            ps.setString(3, uc.getUsuario());
            ps.setString(4, uc.getPassword());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEditado(Sentencias uc) {
        String sql = Sentencias.ACTUALIZAR;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setString(1, uc.getNombre());
            ps.setString(2, uc.getUsuario());
            ps.setString(3, uc.getPassword());
            ps.setInt(4, uc.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isUsuarioCambiado(Sentencias uc) {
        String sql = Sentencias.ACTUALIZAR_USUARIO;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setString(1, uc.getUsuario());
            ps.setInt(2, uc.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isPassCambiado(Sentencias uc) {
        String sql = Sentencias.ACTUALIZAR_PASSWORD;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setString(1, uc.getPassword());
            ps.setInt(2, uc.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEliminado(Sentencias uc) {
        String sql = Sentencias.ELIMINAR;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEliminadoAll() {
        int rsu = 0;
        String sql = Sentencias.ELIMINAR_TODO;

        try {
            ps = cn.conect.prepareStatement(sql);
            rsu = ps.executeUpdate();
            rsu++;
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static String verificaUsuario(String usuario) {
        String existe = "";
        String SQL = "SELECT usuario FROM usuarios WHERE usuario = '" + usuario + "'";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                existe = rs.getString(1);
            }
        } catch (SQLException ex) {
           Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return existe;
    }

    public static String verificaPassword(String id) {
        String existe = "";
        String SQL = "SELECT password FROM usuarios WHERE id = " + id;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                existe = rs.getString(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return existe;
    }

    public static boolean isExiste(String usuario) {
        String existe = "";
        String SQL = "SELECT usuario FROM usuarios WHERE usuario = '" + usuario +"'";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                existe = rs.getString(1);
            }

            if (existe.equals(usuario)) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static String extraerNombre(String id) {
        String nombre = "";
        String SQL = "SELECT nombre FROM usuarios WHERE id = " + id;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                nombre = rs.getString(1);
            }
            return nombre;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nombre;
    }

    public static String extraerPassword(String id) {
        String password = "";
        String SQL = "SELECT password FROM usuarios WHERE id = '" + id + "'";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                password = rs.getString(1);
            }
            return password;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return password;
    }

    public static String extraerPass(String usuario) {
        String password = "";
        String SQL = "SELECT password FROM usuarios WHERE usuario = '" + usuario + "'";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                password = rs.getString(1);
            }
            return password;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return password;
    }

    public static String extraerID(String usuario) {
        String password = "";
        String SQL = "SELECT id FROM usuarios WHERE usuario = '" + usuario + "'";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                password = rs.getString(1);
            }
            return password;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return password;
    }

    public static int extraerIDMax() {
        int id = 0;
        String SQL = "SELECT MAX(id) FROM usuarios";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                id = rs.getInt(1);
            }

            if (id == 0) {
                id = 1;
            } else {
                id = id + 1;
            }
            return id;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    public static void setListar(String busca) {
        DefaultTableModel modelo = (DefaultTableModel) pnlUsuarios.tabla.getModel();

        while (modelo.getRowCount() > 0) {
            modelo.removeRow(0);
        }
        String sql = "";
        if (busca.equals("")) {
            sql = Sentencias.LISTAR;
        } else {
            sql = "SELECT * FROM usuarios WHERE ("
                    + "nombre LIKE'" + busca + "%' OR "
                    + "usuario LIKE'" + busca + "%') "
                    + "AND id != 1";
        }
        String datos[] = new String[4];
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString("id");
                datos[1] = rs.getString("nombre");
                datos[2] = rs.getString("usuario");
                datos[3] = rs.getString("password");
                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void selecionaFila(String id) {
        for (int i = 0; i < pnlUsuarios.tabla.getRowCount(); i++) {
            if (id.equals(pnlUsuarios.tabla.getValueAt(i, 0).toString())) {
                pnlUsuarios.tabla.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    public static void extraerDatos(pnlUsuarios datos, String id) {

        String SQL = "SELECT * FROM usuarios WHERE id = " + id;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                datos.txtNombre.setText(rs.getString(2));
                datos.txtUsuario.setText(rs.getString(3));
                datos.txtPass.setText(rs.getString(4));
                datos.btnRegistrar.setIcon(new ImageIcon(datos.getClass().getResource("/img/usuarios/btnGuardar.png")));
                datos.btnRegistrar.setText("GUARDAR CAMBIOS");
                datos.btnCancelar.setVisible(true);
                datos.isRegistrar = false;
                datos.userTemp = rs.getString(3);

                extraeraAlmacen(datos, id);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // DATOS PRIVILEGIOS ----------------------------------------------------------------
    public static void extraeraAlmacen(pnlUsuarios datos, String id) {

        int contarTodos = 0;

        String SQLAlmacen = "SELECT * FROM priv_almacen WHERE id_user = " + id;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQLAlmacen);
            if (rs.next()) {

                if (rs.getBoolean(3)) {
                    contarTodos++;
                }
                datos.registrarCategoria.setSelected(rs.getBoolean(3));
                if (rs.getBoolean(4)) {
                    contarTodos++;
                }
                datos.registrarAlmacen.setSelected(rs.getBoolean(4));
                if (rs.getBoolean(5)) {
                    contarTodos++;
                }
                datos.editarAlmacen.setSelected(rs.getBoolean(5));
                if (rs.getBoolean(6)) {
                    contarTodos++;
                }
                datos.borrarAlmacen.setSelected(rs.getBoolean(6));
                if (rs.getBoolean(7)) {
                    contarTodos++;
                }
                datos.exportarAlmacen.setSelected(rs.getBoolean(7));
                if (rs.getBoolean(8)) {
                    contarTodos++;
                }
                datos.importarAlmacen.setSelected(rs.getBoolean(8));
                if (rs.getBoolean(9)) {
                    contarTodos++;
                }
                datos.imprimirAlmacen.setSelected(rs.getBoolean(9));

                if (contarTodos == 7) {
                    datos.todoAlmacen.setSelected(true);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }

        //--------------------------------------VENTAS ----------------------------------
        contarTodos = 0;
        String SQLVentas = "SELECT * FROM priv_ventas WHERE id_user = " + id;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQLVentas);
            if (rs.next()) {

                if (rs.getBoolean(3)) {
                    contarTodos++;
                }
                datos.registrarVentas.setSelected(rs.getBoolean(3));
                if (rs.getBoolean(4)) {
                    contarTodos++;
                }
                datos.reportesVentas.setSelected(rs.getBoolean(4));
                if (rs.getBoolean(5)) {
                    contarTodos++;
                }
                datos.ultimasVentas.setSelected(rs.getBoolean(5));

                if (contarTodos == 3) {
                    datos.todoVentas.setSelected(true);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }

        //--------------------------------------CAJEROS ----------------------------------
        contarTodos = 0;
        String SQLCajero = "SELECT * FROM priv_cajero WHERE id_user = " + id;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQLCajero);
            if (rs.next()) {

                if (rs.getBoolean(3)) {
                    contarTodos++;
                }
                datos.registrarCajero.setSelected(rs.getBoolean(3));
                if (rs.getBoolean(4)) {
                    contarTodos++;
                }
                datos.editarCajero.setSelected(rs.getBoolean(4));
                if (rs.getBoolean(5)) {
                    contarTodos++;
                }
                datos.borrarCajero.setSelected(rs.getBoolean(5));

                if (contarTodos == 3) {
                    datos.todoCajeros.setSelected(true);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }

        //--------------------------------------CONFIGURACIÓN ----------------------------------
        contarTodos = 0;
        String SQLConfig = "SELECT * FROM priv_config WHERE id_user = " + id;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQLConfig);
            if (rs.next()) {

                if (rs.getBoolean(3)) {
                    contarTodos++;
                }
                datos.crearConfig.setSelected(rs.getBoolean(3));
                if (rs.getBoolean(4)) {
                    contarTodos++;
                }
                datos.restoreConfig.setSelected(rs.getBoolean(4));

                if (contarTodos == 2) {
                    datos.todoConfig.setSelected(true);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //-----------------------------------------------------------------------------------
    //
    public static boolean isRegistradoCaja(String dineroInicial, String fecha, String cajero) {
        String sql = "INSERT INTO caja(dineroInicial, fecha, cajero) VALUES(?,?,?)";
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setDouble(1, Double.parseDouble(dineroInicial));
            ps.setString(2, fecha);
            ps.setString(3, cajero);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        System.out.println(sql);
        return false;
    }
}
