package ventas;

/**
 *
 * @author Rojeru San CL
 */
public class Sentencias {

    public static String LISTAR_VENTAS = "SELECT * FROM ventas";

    public static String LISTAR_CAJA = "SELECT * FROM cajaTemporal";

    public static String LISTAR_ALMACEN = "SELECT * FROM almacen";

    public static String REGISTRAR_VENTA = "INSERT INTO ventas("
            + "num_venta,"
            + "codigo,"
            + "cantidad,"
            + "importe,"
            + "fecha,"
            + "hora,"
            + "cajero,"
            + "categoria,"
            + "precio,"
            + "precio_venta,"
            + "descripcion,"
            + "inventario_utiliza"
            + ") "
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

    public static String REGISTRAR_CAJA = "INSERT INTO cajaTemporal("
            + "categoria,"
            + "codigo,"
            + "cantidad,"
            + "descripcion,"
            + "existencias,"
            + "stock_min,"
            + "precio,"
            + "precio_venta,"
            + "importe,"
            + "inventario_utiliza) "
            + "VALUES(?,?,?,?,?,?,?,?,?,?)";

    public static String ACTUALIZAR_CAJA = "UPDATE cajaTemporal SET "
            + "cantidad            = ?, "
            + "importe             = ? "
            + "WHERE codigo        = ?  ";

    public static String ACTUALIZAR_CANTIDAD = "UPDATE cajaTemporal SET "
            + "cantidad            = ?"
            + "WHERE codigo        = ?  ";
    
    public static String ACTUALIZAR_ALMACEN = "UPDATE almacen SET "
            + "existencias         = ? "
            + "WHERE codigo        = ?  ";

    public static String ELIMINAR_VENTA = "DELETE FROM ventas WHERE id = ?";

    public static String ELIMINAR_CAJA = "DELETE FROM cajaTemporal WHERE id = ?";

    public static String ELIMINAR_VENTAS = "TRUNCATE TABLE ventas";

    public static String VACIAR_CAJA = "DELETE FROM cajaTemporal";

    private int idCaja;
    private String codigoCaja;
    private String categoriaCaja;
    private int cantidadCaja;
    private String descripcionCaja;
    private int existenciasCaja;
    private int stockMinCaja;
    private double precioCaja;
    private double precio_ventaCaja;
    private double importeCaja;
    private boolean inventario_utiliza;

    public String getCategoriaCaja() {
        return categoriaCaja;
    }

    public void setCategoriaCaja(String categoriaCaja) {
        this.categoriaCaja = categoriaCaja;
    }
    
    public int getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(int idCaja) {
        this.idCaja = idCaja;
    }

    public String getCodigoCaja() {
        return codigoCaja;
    }

    public void setCodigoCaja(String codigoCaja) {
        this.codigoCaja = codigoCaja;
    }

    public int getCantidadCaja() {
        return cantidadCaja;
    }

    public void setCantidadCaja(int cantidadCaja) {
        this.cantidadCaja = cantidadCaja;
    }

    public String getDescripcionCaja() {
        return descripcionCaja;
    }

    public void setDescripcionCaja(String descripcionCaja) {
        this.descripcionCaja = descripcionCaja;
    }

    public int getExistenciasCaja() {
        return existenciasCaja;
    }

    public void setExistenciasCaja(int existenciasCaja) {
        this.existenciasCaja = existenciasCaja;
    }

    public double getPrecioCaja() {
        return precioCaja;
    }

    public void setPrecioCaja(double precioCaja) {
        this.precioCaja = precioCaja;
    }

    public double getPrecio_ventaCaja() {
        return precio_ventaCaja;
    }

    public void setPrecio_ventaCaja(double precio_ventaCaja) {
        this.precio_ventaCaja = precio_ventaCaja;
    }

    public double getImporteCaja() {
        return importeCaja;
    }

    public void setImporteCaja(double importeCaja) {
        this.importeCaja = importeCaja;
    }

    public boolean isInventario_utiliza() {
        return inventario_utiliza;
    }

    public void setInventario_utiliza(boolean inventario_utiliza) {
        this.inventario_utiliza = inventario_utiliza;
    }

    public int getStockMinCaja() {
        return stockMinCaja;
    }

    public void setStockMinCaja(int stockMinCaja) {
        this.stockMinCaja = stockMinCaja;
    }

    //------------------------------------------------------------------------------
    private int idVenta;
    private int numVenta;
    private String codigoVenta;
    private int cantidadVenta;
    private double importeVenta;
    private String fechaVenta;
    private String horaVenta;
    private String cajero;
    private String categoria;
    private double precio;
    private double precio_venta;
    private String descripcion;

    public String getHoraVenta() {
        return horaVenta;
    }

    public void setHoraVenta(String horaVenta) {
        this.horaVenta = horaVenta;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getNumVenta() {
        return numVenta;
    }

    public void setNumVenta(int numVenta) {
        this.numVenta = numVenta;
    }

    public String getCodigoVenta() {
        return codigoVenta;
    }

    public void setCodigoVenta(String codigoVenta) {
        this.codigoVenta = codigoVenta;
    }

    public int getCantidadVenta() {
        return cantidadVenta;
    }

    public void setCantidadVenta(int cantidadVenta) {
        this.cantidadVenta = cantidadVenta;
    }

    public double getImporteVenta() {
        return importeVenta;
    }

    public void setImporteVenta(double importeVenta) {
        this.importeVenta = importeVenta;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getCajero() {
        return cajero;
    }

    public void setCajero(String cajero) {
        this.cajero = cajero;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getPrecio_venta() {
        return precio_venta;
    }

    public void setPrecio_venta(double precio_venta) {
        this.precio_venta = precio_venta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    //------------------------------------------------------------------------------------
    
    private int existenciasAlmacen;

    public int getExistenciasAlmacen() {
        return existenciasAlmacen;
    }

    public void setExistenciasAlmacen(int existenciasAlmacen) {
        this.existenciasAlmacen = existenciasAlmacen;
    }
    
}
