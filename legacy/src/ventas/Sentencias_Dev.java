/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventas;

/**
 *
 * @author RojeruSan
 */
public class Sentencias_Dev {

    public static String LISTAR = "SELECT * FROM devoluciones";

    public static String REGISTRAR = "INSERT INTO devoluciones("
//            + "id,"
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
            + "motivo,"
            + "inventario_utiliza"
            + ") "
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
    
    public static String ACTUALIZAR_DEVOLUCIONES = "UPDATE devoluciones SET "
            + "cantidad            = ?, "
            + "importe             = ?, "
            + "motivo              = ?  "
            + "WHERE id            = ?  ";
    
    public static String ACTUALIZAR_VENTAS = "UPDATE ventas SET "
            + "cantidad            = ?, "
            + "importe             = ?  "
            + "WHERE id            = ?  ";

    public static String ELIMINAR = "DELETE FROM ventas WHERE id = ?";
    
    public static String VACIAR = "DELETE FROM devoluciones";

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
    private String motivo;
    private boolean inventario_utiliza;

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

    public String getHoraVenta() {
        return horaVenta;
    }

    public void setHoraVenta(String horaVenta) {
        this.horaVenta = horaVenta;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    
    public boolean isInventario_utiliza() {
        return inventario_utiliza;
    }

    public void setInventario_utiliza(boolean inventario_utiliza) {
        this.inventario_utiliza = inventario_utiliza;
    }

    
}
