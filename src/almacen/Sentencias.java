package almacen;

/**
 *
 * @author Rojeru San CL
 */
public class Sentencias {

    public static String LISTAR = "SELECT * FROM almacen";
    public static String LISTAR_CAT = "SELECT * FROM categorias";

    public static String REGISTRAR = "INSERT INTO almacen("
            + "id,"
            + "categoria,"
            + "codigo,"
            + "descripcion,"
            + "precio,"
            + "precio_venta,"
            + "existencias,"
            + "stock_min,"
            + "se_venden_en,"
            + "ubicacion,"
            + "inventario_utiliza) "
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    
     public static String REGISTRAR_CAT = "INSERT INTO categorias("
            + "id,"
            + "categorias) "
            + "VALUES(?,?)";

    public static String ACTUALIZAR = "UPDATE almacen SET "
            + "categoria           = ?, "
            + "codigo              = ?, "
            + "descripcion         = ?, "
            + "precio              = ?, "
            + "precio_venta        = ?, "
            + "existencias         = ?, "
            + "stock_min           = ?, "
            + "se_venden_en        = ?, "
            + "ubicacion           = ?, "
            + "inventario_utiliza  = ?  "
            + "WHERE id            = ?  ";
    
    public static String ACTUALIZAR_ALMACEN = "UPDATE almacen SET "
            + "existencias         = ? "
            + "WHERE codigo        = ? ";
    
    public static String ACTUALIZAR_CAT = "UPDATE categorias SET "
            + "categorias             = ? "
            + "WHERE id            = ?  ";

    public static String ELIMINAR = "DELETE FROM almacen WHERE id = ?";
    
    public static String ELIMINAR_CAT = "DELETE FROM categorias WHERE id = ?";

    public static String ELIMINAR_TODO = "TRUNCATE TABLE almacen";

    private int id;
    private String codigo;
    private String descripcion;
    private double precio;
    private double precio_venta;
    private int stock_min;
    private int existencias;
    private String se_venden_en;
    private String ubicacion;
    private boolean inventario_utiliza;
    
    private int idCategoria;
    private String Categoria;

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String Categoria) {
        this.Categoria = Categoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public int getStock_min() {
        return stock_min;
    }

    public void setStock_min(int stock_min) {
        this.stock_min = stock_min;
    }

    public int getExistencias() {
        return existencias;
    }

    public void setExistencias(int existencias) {
        this.existencias = existencias;
    }

    public String getSe_venden_en() {
        return se_venden_en;
    }

    public void setSe_venden_en(String se_venden_en) {
        this.se_venden_en = se_venden_en;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isInventario_utiliza() {
        return inventario_utiliza;
    }

    public void setInventario_utiliza(boolean inventario_utiliza) {
        this.inventario_utiliza = inventario_utiliza;
    }
    
}
