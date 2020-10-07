package privilegios;

/**
 *
 * @author Rojeru San CL
 */
public class Sentencias {

    public static String REGISTRAR_ALMACEN = "INSERT INTO priv_almacen("
            + "id_user,"
            + "registrar_cat,"
            + "registrar_pro,"
            + "editar_pro,"
            + "eliminar_pro,"
            + "exportar,"
            + "importar,"
            + "imprimir) "
            + "VALUES(?,?,?,?,?,?,?,?)";

    public static String ACTUALIZAR_ALMACEN = "UPDATE priv_almacen SET "
            + "registrar_cat             = ?, "
            + "registrar_pro             = ?, "
            + "editar_pro                = ?, "
            + "eliminar_pro              = ?, "
            + "exportar                  = ?, "
            + "importar                  = ?, "
            + "imprimir                  = ?  "
            + "WHERE id_user             = ?  ";

    public static String ELIMINAR_ALMACEN = "DELETE FROM priv_almacen WHERE id_user = ?";


    private int id_user;
    private boolean registrar_cat;
    private boolean registrar_pro;
    private boolean editar_pro;
    private boolean eliminar_pro;
    private boolean exportar;
    private boolean importar;
    private boolean imprimir;

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public boolean isRegistrar_cat() {
        return registrar_cat;
    }

    public void setRegistrar_cat(boolean registrar_cat) {
        this.registrar_cat = registrar_cat;
    }

    public boolean isRegistrar_pro() {
        return registrar_pro;
    }

    public void setRegistrar_pro(boolean registrar_pro) {
        this.registrar_pro = registrar_pro;
    }

    public boolean isEditar_pro() {
        return editar_pro;
    }

    public void setEditar_pro(boolean editar_pro) {
        this.editar_pro = editar_pro;
    }

    public boolean isEliminar_pro() {
        return eliminar_pro;
    }

    public void setEliminar_pro(boolean eliminar_pro) {
        this.eliminar_pro = eliminar_pro;
    }

    public boolean isExportar() {
        return exportar;
    }

    public void setExportar(boolean exportar) {
        this.exportar = exportar;
    }

    public boolean isImportar() {
        return importar;
    }

    public void setImportar(boolean importar) {
        this.importar = importar;
    }

    public boolean isImprimir() {
        return imprimir;
    }

    public void setImprimir(boolean imprimir) {
        this.imprimir = imprimir;
    }
    
    
    //---------------------------------------------------------------------------------
    
    public static String REGISTRAR_VENTAS = "INSERT INTO priv_ventas("
            + "id_user,"
            + "registrar,"
            + "reportes,"
            + "ultimas_ventas) "
            + "VALUES(?,?,?,?)";

    public static String ACTUALIZAR_VENTAS = "UPDATE priv_ventas SET "
            + "registrar            = ?, "
            + "reportes             = ?, "
            + "ultimas_ventas       = ?  "
            + "WHERE id_user        = ?  ";

    public static String ELIMINAR_VENTAS = "DELETE FROM priv_ventas WHERE id_user = ?";

    private boolean registrar;
    private boolean reportes;
    private boolean ultimas_ventas;

    public boolean isRegistrar() {
        return registrar;
    }

    public void setRegistrar(boolean registrar) {
        this.registrar = registrar;
    }

    public boolean isReportes() {
        return reportes;
    }

    public void setReportes(boolean reportes) {
        this.reportes = reportes;
    }

    public boolean isUltimas_ventas() {
        return ultimas_ventas;
    }

    public void setUltimas_ventas(boolean ultimas_ventas) {
        this.ultimas_ventas = ultimas_ventas;
    }

    //---------------------------------------------------------------------------------
    
    public static String REGISTRAR_CAJERO = "INSERT INTO priv_cajero("
            + "id_user,"
            + "registrar,"
            + "editar,"
            + "eliminar) "
            + "VALUES(?,?,?,?)";

    public static String ACTUALIZAR_CAJERO = "UPDATE priv_cajero SET "
            + "registrar          = ?, "
            + "editar             = ?, "
            + "eliminar           = ?  "
            + "WHERE id_user      = ?  ";

    public static String ELIMINAR_CAJERO = "DELETE FROM priv_cajero WHERE id_user = ?";

    private boolean registrarCajero;
    private boolean editar;
    private boolean eliminar;

    public boolean isRegistrarCajero() {
        return registrarCajero;
    }

    public void setRegistrarCajero(boolean registrarCajero) {
        this.registrarCajero = registrarCajero;
    }

    public boolean isEditar() {
        return editar;
    }

    public void setEditar(boolean editar) {
        this.editar = editar;
    }

    public boolean isEliminar() {
        return eliminar;
    }

    public void setEliminar(boolean eliminar) {
        this.eliminar = eliminar;
    }
    
    //---------------------------------------------------------------------------------
    
    public static String REGISTRAR_CONFIG = "INSERT INTO priv_config("
            + "id_user,"
            + "crear_backup,"
            + "restore_backup) "
            + "VALUES(?,?,?)";

    public static String ACTUALIZAR_CONFIG = "UPDATE priv_config SET "
            + "crear_backup       = ?, "
            + "restore_backup     = ?  "
            + "WHERE id_user      = ?  ";

    public static String ELIMINAR_CONFIG = "DELETE FROM priv_config WHERE id_user = ?";

    private boolean crear_backup;
    private boolean restore_backup;

    public boolean isCrear_backup() {
        return crear_backup;
    }

    public void setCrear_backup(boolean crear_backup) {
        this.crear_backup = crear_backup;
    }

    public boolean isRestore_backup() {
        return restore_backup;
    }

    public void setRestore_backup(boolean restore_backup) {
        this.restore_backup = restore_backup;
    }

}
