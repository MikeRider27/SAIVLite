package ventas;

import usuarios.*;
import conexion.ConexionBD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import principal.pnlVentas;

/**
 *
 * @author Rojeru San CL
 */
public class Operaciones {

    private static ConexionBD cn = new ConexionBD();
    static PreparedStatement ps;

    public static boolean isRegistradoCaja(Sentencias uc) {
        String sql = Sentencias.REGISTRAR_CAJA;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setString(1, uc.getCategoriaCaja());
            ps.setString(2, uc.getCodigoCaja());
            ps.setInt(3, uc.getCantidadCaja());
            ps.setString(4, uc.getDescripcionCaja());
            ps.setInt(5, uc.getExistenciasCaja());
            ps.setInt(6, uc.getStockMinCaja());
            ps.setDouble(7, uc.getPrecioCaja());
            ps.setDouble(8, uc.getPrecio_ventaCaja());
            ps.setDouble(9, uc.getImporteCaja());
            ps.setBoolean(10, uc.isInventario_utiliza());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEditadoCaja(Sentencias uc) {
        String sql = Sentencias.ACTUALIZAR_CAJA;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getCantidadCaja());
            ps.setDouble(2, uc.getImporteCaja());
            ps.setString(3, uc.getCodigoCaja());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEliminadoCaja(Sentencias uc) {
        String sql = Sentencias.ELIMINAR_CAJA;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getIdCaja());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isVaciadoCaja() {
        int rsu = 0;
        String sql = Sentencias.VACIAR_CAJA;

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

    public static void setListarCaja() {
        DefaultTableModel modelo = (DefaultTableModel) principal.pnlVentas.tabla.getModel();

        while (modelo.getRowCount() > 0) {
            modelo.removeRow(0);
        }
        String sql = "SELECT * FROM cajaTemporal";

        String datos[] = new String[10];
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString("id");
                datos[1] = rs.getString("codigo");
                datos[2] = rs.getString("cantidad");
                datos[3] = rs.getString("descripcion");

                if (rs.getBoolean("inventario_utiliza")) {
                    datos[9] = "true";
                    datos[4] = rs.getString("existencias");
                } else {
                    datos[9] = "false";
                    datos[4] = "Ilimitadas";
                }

                datos[5] = rs.getString("precio_venta");
                datos[6] = rs.getString("importe");
                datos[7] = rs.getString("categoria");
                datos[8] = rs.getString("precio");
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

    public static boolean isRDatosCaja(String id, int cantidad, boolean existencias) {

        String categoriaCaja = null;
        String codigoCaja = null;
        int cantidadCaja = cantidad;
        String descripcionCaja = null;
        int existenciasCaja = 0;
        int stockMinCaja = 0;
        double precioCaja = 0;
        double precio_ventaCaja = 0;
        double importeCaja;

        String SQL = "SELECT * FROM almacen WHERE id = " + id;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                categoriaCaja = rs.getString("categoria");
                codigoCaja = rs.getString("codigo");
                descripcionCaja = rs.getString("descripcion");
                existenciasCaja = rs.getInt("existencias");
                stockMinCaja = rs.getInt("stock_min");
                precio_ventaCaja = rs.getDouble("precio_venta");
                precioCaja = rs.getDouble("precio");
            }

            importeCaja = cantidad * precio_ventaCaja;

            Sentencias s = new Sentencias();
            s.setCategoriaCaja(categoriaCaja);
            s.setCodigoCaja(codigoCaja);
            s.setCantidadCaja(cantidadCaja);
            s.setDescripcionCaja(descripcionCaja);
            s.setExistenciasCaja(existenciasCaja);
            s.setPrecioCaja(precioCaja);
            s.setPrecio_ventaCaja(precio_ventaCaja);
            s.setImporteCaja(importeCaja);
            s.setInventario_utiliza(existencias);
            s.setStockMinCaja(stockMinCaja);

            if (isExiste(codigoCaja)) {

                int CantidadTotal = extraerCantidad(codigoCaja) + cantidadCaja;
                double importeTotal = extraerImporte(codigoCaja) + importeCaja;

                System.out.println(importeTotal);

                s.setCantidadCaja(CantidadTotal);
                s.setImporteCaja(importeTotal);

                if (isEditadoCaja(s)) {
                    setListarCaja();
                    totalCaja();
                    selecionaFilaVentas(codigoCaja);
                    return true;
                } else {
                    return false;
                }
            } else {

                if (isRegistradoCaja(s)) {
                    setListarCaja();
                    totalCaja();
                    selecionaFilaVentas(codigoCaja);
                    return true;
                } else {
                    return false;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static int extraerCantidad(String codigo) {

        int cantidad = 0;

        String SQL = "SELECT cantidad FROM cajaTemporal WHERE codigo = " + codigo;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                cantidad = rs.getInt(1);
            }

            return cantidad;

        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cantidad;
    }

    public static boolean isInventaro_utiliza(String codigo) {

        boolean verdadero = false;

        String SQL = "SELECT inventario_utiliza FROM almacen WHERE codigo = " + codigo;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                verdadero = rs.getBoolean(1);
            }

            return verdadero;

        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return verdadero;
    }

    public static String extraerCodigo(String id) {

        String cantidad = "0";

        String SQL = "SELECT codigo FROM almacen WHERE id = " + id;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                cantidad = rs.getString(1);
            }

            return cantidad;

        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cantidad;
    }

    public static int extraerExistencias(String codigo) {

        int cantidad = 0;

        String SQL = "SELECT existencias FROM almacen WHERE codigo = " + codigo;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                cantidad = rs.getInt(1);
            }

            return cantidad;

        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cantidad;
    }

    public static double extraerImporte(String codigo) {

        double importe = 0;

        String SQL = "SELECT importe FROM cajaTemporal WHERE codigo = " + codigo;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                importe = rs.getDouble(1);
            }

            return importe;

        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return importe;
    }

    public static boolean isExiste(String codigo) {

        boolean existe = false;

        String SQL = "SELECT codigo FROM cajaTemporal WHERE codigo = " + codigo;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                existe = true;
            } else {
                existe = false;
            }

            return existe;

        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return existe;
    }

    public static boolean isExisteEnAlmacen(String codigo) {

        boolean existe = false;

        String SQL = "SELECT codigo FROM almacen WHERE codigo = " + codigo;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                existe = true;
            } else {
                existe = false;
            }

            return existe;

        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return existe;
    }

    public static void totalCaja() {

        String SQL = "SELECT SUM(importe) FROM cajaTemporal";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                if (rs.getString(1) == null) {
                    principal.pnlVentas.lblTotal.setText("0.00");
                } else {
                    principal.pnlVentas.lblTotal.setText(rs.getString(1));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static int totalCantidad() {

        int cantidad = 0;

        String SQL = "SELECT SUM(cantidad) FROM cajaTemporal";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                cantidad = rs.getInt(1);
            }

            return cantidad;
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cantidad;
    }

    public static int extraerIDVenta() {

        int cantidad = 0;

        String SQL = "SELECT MAX(num_venta) FROM ventas";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                cantidad = rs.getInt(1);
            }

            if (cantidad == 0) {
                cantidad = 1;
            } else {
                cantidad = cantidad + 1;
            }
            return cantidad;

        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cantidad;
    }

//------------------------------------------------------------------------------------
    public static void setListarAlmacen(String busca) {
        DefaultTableModel modelo = (DefaultTableModel) Productos.tabla.getModel();

        while (modelo.getRowCount() > 0) {
            modelo.removeRow(0);
        }
        String sql = "";
        if (busca.equals("")) {
            sql = Sentencias.LISTAR_ALMACEN;
        } else {
            sql = "SELECT * FROM almacen WHERE ("
                    + "categoria LIKE'" + busca + "%' OR "
                    + "codigo LIKE'" + busca + "%' OR "
                    + "descripcion LIKE'" + busca + "%'"
                    + ")";
        }
        String datos[] = new String[10];
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString("id");
                datos[1] = rs.getString("codigo");
                datos[2] = rs.getString("descripcion");
                datos[3] = rs.getString("precio");
                datos[4] = rs.getString("precio_venta");

                if (rs.getBoolean("inventario_utiliza")) {
                    datos[6] = rs.getString("existencias");
                } else {
                    datos[6] = "Ilimitadas";
                }

                datos[7] = rs.getString("ubicacion");
                datos[8] = rs.getString("inventario_utiliza");
                datos[9] = rs.getString("stock_min");
                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //-----------------------------------------------------------------------------------
    public static boolean isRegistradoVenta(Sentencias uc) {
        String sql = Sentencias.REGISTRAR_VENTA;
        try {
            ps = cn.conect.prepareStatement(sql);
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
            ps.setBoolean(12, uc.isInventario_utiliza());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        System.out.println(sql);
        return false;
    }

    public static boolean isEditadoExistencias(Sentencias uc) {
        String sql = Sentencias.ACTUALIZAR_ALMACEN;
        try {
            ps = cn.conect.prepareStatement(sql);
            ps.setInt(1, uc.getExistenciasAlmacen());
            ps.setString(2, uc.getCodigoCaja());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
        }
        System.out.println(sql);
        return false;
    }

    //------------------------------------------------------------------------------------
    public static void selecionaFilaVentas(String id) {
        for (int i = 0; i < pnlVentas.tabla.getRowCount(); i++) {
            if (id.equals(pnlVentas.tabla.getValueAt(i, 1).toString())) {
                pnlVentas.tabla.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    public static boolean isRCaja(String codigo, int cantidad, boolean existencias) {

        String categoriaCaja = null;
        String codigoCaja = null;
        int cantidadCaja = cantidad;
        String descripcionCaja = null;
        int existenciasCaja = 0;
        int stockMinCaja = 0;
        double precioCaja = 0;
        double precio_ventaCaja = 0;
        double importeCaja;

        String SQL = "SELECT * FROM almacen WHERE codigo = " + codigo;
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                categoriaCaja = rs.getString("categoria");
                codigoCaja = rs.getString("codigo");
                descripcionCaja = rs.getString("descripcion");
                existenciasCaja = rs.getInt("existencias");
                stockMinCaja = rs.getInt("stock_min");
                precio_ventaCaja = rs.getDouble("precio_venta");
                precioCaja = rs.getDouble("precio");
            }

            importeCaja = cantidad * precio_ventaCaja;

            Sentencias s = new Sentencias();
            s.setCategoriaCaja(categoriaCaja);
            s.setCodigoCaja(codigoCaja);
            s.setCantidadCaja(cantidadCaja);
            s.setDescripcionCaja(descripcionCaja);
            s.setExistenciasCaja(existenciasCaja);
            s.setPrecioCaja(precioCaja);
            s.setPrecio_ventaCaja(precio_ventaCaja);
            s.setImporteCaja(importeCaja);
            s.setInventario_utiliza(existencias);
            s.setStockMinCaja(stockMinCaja);

            if (isExiste(codigoCaja)) {

                int CantidadTotal = extraerCantidad(codigoCaja) + cantidadCaja;
                double importeTotal = extraerImporte(codigoCaja) + importeCaja;

                System.out.println(importeTotal);

                s.setCantidadCaja(CantidadTotal);
                s.setImporteCaja(importeTotal);

                if (isEditadoCaja(s)) {
                    setListarCaja();
                    totalCaja();
                    selecionaFilaVentas(codigoCaja);
                    return true;
                } else {
                    return false;
                }
            } else {

                if (isRegistradoCaja(s)) {
                    setListarCaja();
                    totalCaja();
                    selecionaFilaVentas(codigoCaja);
                    return true;
                } else {
                    return false;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------
    public static void setListarVentas(String busca) {
        DefaultTableModel modelo = (DefaultTableModel) pnlVentasToDay.tabla.getModel();

        while (modelo.getRowCount() > 0) {
            modelo.removeRow(0);
        }
        String sql = "";
        if (busca.equals("")) {
            sql = Sentencias.LISTAR_VENTAS;
        } else {
            sql = "SELECT * FROM ventas WHERE (fecha LIKE'" + busca + "%')";
        }
        String datos[] = new String[13];
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString("id");
                datos[1] = rs.getString("num_venta");
                datos[2] = rs.getString("cantidad");
                datos[3] = rs.getString("codigo");
                datos[4] = rs.getString("descripcion");
                datos[5] = rs.getString("precio_venta");
                datos[6] = rs.getString("importe");
                datos[7] = rs.getString("hora");
                datos[8] = rs.getString("cajero");
                datos[9] = rs.getString("inventario_utiliza");
                datos[10] = rs.getString("fecha");
                datos[11] = rs.getString("categoria");
                datos[12] = rs.getString("precio");
                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //------------------------------------------------------------------------------------
    // REPORTE DE LAS VENTAS
    //---------------------------------------------------------------------------------------
    public static void reporteVentas(pnlReportes datos, String fecha1, String fecha2) {

        boolean siHayDatos = false;

        String SQL = "SELECT "
                + "ventas.fecha, "
                + "ventas.cantidad, "
                + "ventas.descripcion, "
                + "ventas.precio_venta, "
                + "ventas.importe, "
                + "tabla4.cantidad AS cantidad1, "
                + "tabla4.ganancias3, "
                + "tabla4.total, "
                + "tabla4.capital2, "
                + "tabla4.dinCaja, "
                + "(tabla4.dinCaja + tabla4.total) AS totalCaja "
                + "FROM ventas, "
                + "(SELECT dinCaja, ganancias3, ganancias2 AS total, capital2, cantidad FROM "
                + "(SELECT dinCaja, ganancias2-capital2 AS ganancias3, ganancias2, capital2, cantidad FROM  "
                + "(SELECT dinCaja, SUM(capital1) AS capital2, SUM(ganancias1) AS ganancias2, SUM(cantidad) AS cantidad FROM  "
                + "(SELECT dinCaja, ventas.precio*ventas.cantidad AS capital1, ventas.precio_venta*ventas.cantidad AS ganancias1, ventas.cantidad FROM  "
                + "ventas, (SELECT SUM(dineroInicial) AS dinCaja FROM caja WHERE (caja.fecha >= '" + fecha1 + "' AND caja.fecha <= '" + fecha2 + "')) AS tablaCaja  "
                + "WHERE (ventas.fecha >= '" + fecha1 + "' AND ventas.fecha <= '" + fecha2 + "')) AS tabla1 GROUP BY dinCaja) AS tabla2) AS tabla3) AS tabla4  "
                + "WHERE (ventas.fecha >= '" + fecha1 + "' AND ventas.fecha <= '" + fecha2 + "')";
        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            if (rs.next()) {
                siHayDatos = true;
                datos.lblCaja.setText(rs.getString(10));
                datos.lblTotal.setText(rs.getString(8));
                datos.lblCantidad.setText(rs.getString(6));
                datos.lblCapital.setText(rs.getString(9));
                datos.lblGanancias.setText(rs.getString(7));
                datos.lblCajaTotal.setText(rs.getString(11));
            }

            if (siHayDatos) {
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formato1 = new SimpleDateFormat("dd MMMM yyyy");
                Date f1 = null;
                Date f2 = null;
                try {
                    f1 = formato.parse(fecha1);
                    f2 = formato.parse(fecha2);
                } catch (ParseException ex) {
                    Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
                }

                datos.lblFechas.setText("RESULTADOS DESDE " + formato1.format(f1) + " HASTA " + formato1.format(f2));
            } else {
                datos.lblFechas.setText("NO SE ENCONTRARÓN RESULTADOS");

                datos.lblCaja.setText("0.00");
                datos.lblTotal.setText("0.00");
                datos.lblCantidad.setText("0");
                datos.lblCapital.setText("0.00");
                datos.lblGanancias.setText("0.00");
                datos.lblCajaTotal.setText("0.00");
            }

        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //-----------------------------------------------------------------------------------
    // LISTAR CAJEROS EN COMBOBOX
    //-----------------------------------------------------------------------------------
    public static void getCajeros() {
        DefaultComboBoxModel modelo = (DefaultComboBoxModel) ventas.Impresion.comboCajeros.getModel();
        modelo.removeAllElements();
        modelo.addElement("IMPRIMIR TODO");

        String sql = "SELECT * FROM usuarios";

        try {
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                modelo.addElement(rs.getString("nombre"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Operaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
