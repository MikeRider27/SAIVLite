package excel;

import conexion.ConexionBD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data provider for excel
 *
 * @author angel
 */
public final class FakeDataProvider {

    private static ConexionBD cn = new ConexionBD();
    static PreparedStatement ps;

    /**
     * Return the columns name for the table
     */
    public static List<String> getTableHeaders() {
        List<String> tableHeader = new ArrayList<String>();
        tableHeader.add("CATEGORÍA");
        tableHeader.add("CÓDIGO");
        tableHeader.add("DESCRIPCIÓN");
        tableHeader.add("PRECIO COMPRA");
        tableHeader.add("PRECIO VENTA");
        tableHeader.add("EXISTENCIAS");
        tableHeader.add("STOCK MÍN");
        tableHeader.add("SE VENDEN EN");
        tableHeader.add("UBICACIÓN");

        return tableHeader;
    }

    /**
     * Return values for the table
     *
     * @param numberOfRows Number of rows we want to receive
     *
     * @return Values
     */
    public static List<List<String>> getTableContent(int numberOfRows) {
        try {
            if (numberOfRows <= 0) {
                throw new IllegalArgumentException("The number of rows must be a positive integer");
            }

            List<List<String>> tableContent = new ArrayList<List<String>>();

            String SQL = "SELECT * FROM almacen";
            Statement st = cn.conect.createStatement();
            ResultSet rs = st.executeQuery(SQL);

            int i = 0;
            List<String> row = null;
            while (rs.next()) {
                tableContent.add(row = new ArrayList<String>());

                row.add(rs.getString("categoria"));
                row.add(rs.getString("codigo"));
                row.add(rs.getString("descripcion"));
                row.add(rs.getString("precio"));
                row.add(rs.getString("precio_venta"));
                if (rs.getBoolean("inventario_utiliza")) {
                    row.add(rs.getString("existencias"));
                }else{
                    row.add("NO UTILIZA");
                }
                row.add(rs.getString("stock_min"));
                row.add(rs.getString("se_venden_en"));
                row.add(rs.getString("ubicacion"));

                i++;
            }
            return tableContent;
        } catch (SQLException ex) {
            Logger.getLogger(FakeDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
