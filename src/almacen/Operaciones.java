package almacen;

import conexion.ConexionBD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
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
            ps.setString(2, uc.getCategoria());
            ps.setString(3, uc.getCodigo());
            ps.setString(4, uc.getDescripcion());
            ps.setDouble(5, uc.getPrecio());
            ps.setDouble(6, uc.getPrecio_venta());
            ps.setInt(7, uc.getExistencias());
            ps.setInt(8, uc.getStock_min());
            ps.setString(9, uc.getSe_venden_en());
            ps.setString(10, uc.getUbicacion());
            ps.setBoolean(11, uc.isInventario_utiliza());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isRegistradoCat(Sentencias uc) {
        String sql = Sentencias.REGISTRAR_CAT;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getIdCategoria());
            ps.setString(2, uc.getCategoria());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEditado(Sentencias uc) {
        String sql = Sentencias.ACTUALIZAR;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setString(1, uc.getCategoria());
            ps.setString(2, uc.getCodigo());
            ps.setString(3, uc.getDescripcion());
            ps.setDouble(4, uc.getPrecio());
            ps.setDouble(5, uc.getPrecio_venta());
            ps.setInt(6, uc.getExistencias());
            ps.setInt(7, uc.getStock_min());
            ps.setString(8, uc.getSe_venden_en());
            ps.setString(9, uc.getUbicacion());
            ps.setBoolean(10, uc.isInventario_utiliza());
            ps.setInt(11, uc.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
        }
        System.out.println(sql);
        return false;
    }
    
    public static boolean isEditadoAlmacen(Sentencias uc) {
        String sql = Sentencias.ACTUALIZAR_ALMACEN;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getExistencias());
            ps.setString(2, uc.getCodigo());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEditadoCat(Sentencias uc) {
        String sql = Sentencias.ACTUALIZAR_CAT;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setString(1, uc.getCategoria());
            ps.setInt(2, uc.getIdCategoria());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
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
            ex.printStackTrace();
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEliminadoCat(Sentencias uc) {
        String sql = Sentencias.ELIMINAR_CAT;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getIdCategoria());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
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
            ex.printStackTrace();
        }
        System.out.println(sql);
        return false;
    }

    public static int extraerIDMax() {
        int id = 0;
        String SQL = "SELECT MAX(id) FROM almacen";
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
            System.out.println(ex);
        }
        return id;
    }

    public static int extraerIDMaxCat() {
        int id = 0;
        String SQL = "SELECT MAX(id) FROM categorias";
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
            System.out.println(ex);
        }
        return id;
    }

    public static int getStockMin(int idP) {
        int stock = 0;
        String SQL = "SELECT stock_min FROM almacen WHERE id = " + idP;
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
    
    public static int getExistencias(String codigo) {
        int stock = 0;
        String SQL = "SELECT existencias FROM almacen WHERE codigo = " + codigo;
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

    public static String getStockMinVentas(String idP) {
        String stock = null;
        String SQL = "SELECT stock_min FROM almacen WHERE codigo = " + idP;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                stock = rs.getString(1);
            }
            return stock;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return stock;
    }

    public static void setListar(String busca) {
        DefaultTableModel modelo = (DefaultTableModel) pnlAlmacen.tabla.getModel();

        while (modelo.getRowCount() > 0) {
            modelo.removeRow(0);
        }
        String sql = "";
        if (busca.equals("")) {
            sql = Sentencias.LISTAR;
        } else {
            sql = "SELECT * FROM almacen WHERE ("
                    + "categoria LIKE'" + busca + "%' OR "
                    + "codigo LIKE'" + busca + "%' OR "
                    + "descripcion LIKE'" + busca + "%'"
                    + ")";
        }
        String datos[] = new String[8];
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString("id");
                datos[1] = rs.getString("categoria");
                datos[2] = rs.getString("codigo");
                datos[3] = rs.getString("descripcion");
                datos[4] = rs.getString("precio");
                datos[5] = rs.getString("precio_venta");

                if (rs.getBoolean("inventario_utiliza")) {
                    datos[6] = rs.getString("existencias");
                } else {
                    datos[6] = "Ilimitadas";
                }

                datos[7] = rs.getString("ubicacion");
                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setListarCat(String busca) {
        DefaultTableModel modelo = (DefaultTableModel) Categorias.tabla.getModel();

        while (modelo.getRowCount() > 0) {
            modelo.removeRow(0);
        }
        String sql = "";
        if (busca.equals("")) {
            sql = Sentencias.LISTAR_CAT;
        } else {
            sql = "SELECT * FROM categorias WHERE ("
                    + "categorias LIKE'" + busca + "%'"
                    + ")";
        }
        String datos[] = new String[2];
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString("id");
                datos[1] = rs.getString("categorias");
                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void selecionaFila(String id) {
        for (int i = 0; i < pnlAlmacen.tabla.getRowCount(); i++) {
            if (id.equals(pnlAlmacen.tabla.getValueAt(i, 0).toString())) {
                pnlAlmacen.tabla.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    public static void selecionaFilaCat(String id) {
        for (int i = 0; i < Categorias.tabla.getRowCount(); i++) {
            if (id.equals(Categorias.tabla.getValueAt(i, 0).toString())) {
                Categorias.tabla.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    public static boolean isExiste(String codigo) {
        String existe = "";
        String SQL = "SELECT codigo FROM almacen WHERE codigo = '" + codigo + "'";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                existe = rs.getString(1);
            }

            if (existe.equals(codigo)) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }

    public static boolean isExisteCat(String categoria) {
        String existe = "";
        String SQL = "SELECT categorias FROM categorias WHERE categorias = '" + categoria + "'";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                existe = rs.getString(1);
            }

            if (existe.equals(categoria)) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }

    public static void extraerDatos(ModalEditar datos, String id) {

        Operaciones.getCategorias("editar");
        String SQL = "SELECT * FROM almacen WHERE id = " + id;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                datos.id = rs.getInt(1);
                datos.comboCategorias.setSelectedItem(rs.getString(2));
                datos.txtCodigo.setText(rs.getString(3));
                datos.txtDescripcion.setText(rs.getString(4));
                datos.txtPrecio.setText(rs.getString(5));
                datos.txtPrecioVenta.setText(rs.getString(6));

                if (rs.getBoolean(11)) {
                    datos.txtExistencias.setText(rs.getString(7));
                    datos.txtStockMin.setText(rs.getString(8));
                } else {
                    datos.txtExistencias.setText("");
                    datos.txtStockMin.setText("");
                    
                    datos.txtExistencias.setEditable(false);
                    datos.txtStockMin.setEditable(false);
                    datos.txtMas.setEditable(false);
                }

                if (rs.getString(9).isEmpty()) {
                    datos.comboCantidades.setSelectedIndex(0);
                } else {
                    datos.comboCantidades.setSelectedItem(rs.getString(9));
                }
                datos.txtUbicacion.setText(rs.getString(10));
//                datos.checkUtiliza.setSelected(rs.getBoolean(11));
                datos.codigoTemp = rs.getString(3);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void extraerDatosCat(Categorias datos, String id) {

        String SQL = "SELECT * FROM categorias WHERE id = " + id;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                datos.txtNombre.setText(rs.getString(2));
                datos.btnRegistrar.setIcon(new ImageIcon(datos.getClass().getResource("/img/usuarios/btnGuardar.png")));
                datos.btnRegistrar.setText("GUARDAR CAMBIOS");
                datos.btnCancelar.setVisible(true);
                datos.isRegistrar = false;
                datos.catTemp = rs.getString(2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getCategorias(String accion) {
        DefaultComboBoxModel modelo = null;
        if (accion.equals("editar")) {
            modelo = (DefaultComboBoxModel) ModalEditar.comboCategorias.getModel();
            modelo.removeAllElements();
            modelo.addElement("-Sin categoría-");
        }
        if (accion.equals("registrar")) {
            modelo = (DefaultComboBoxModel) ModalRegistrar.comboCategorias.getModel();
            modelo.removeAllElements();
            modelo.addElement("-Sin categoría-");
        }
        if (accion.equals("imprimir")) {
            modelo = (DefaultComboBoxModel) Impresion.inventario.getModel();
            modelo.removeAllElements();
            modelo.addElement("IMPRIMIR TODO");
            modelo.addElement("-Sin categoría-");
        }

        String sql = "SELECT * FROM categorias";

        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                modelo.addElement(rs.getString("categorias"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
