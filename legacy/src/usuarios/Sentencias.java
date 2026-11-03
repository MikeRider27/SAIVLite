package usuarios;

/**
 *
 * @author Rojeru San CL
 */
public class Sentencias {

    public static String LISTAR = "SELECT * FROM usuarios WHERE id != 1";

    public static String REGISTRAR = "INSERT INTO usuarios("
            + "id,"
            + "nombre,"
            + "usuario,"
            + "password) "
            + "VALUES(?,?,?,?)";

    public static String ACTUALIZAR = "UPDATE usuarios SET "
            + "nombre             = ?, "
            + "usuario            = ?, "
            + "password           = ?  "
            + "WHERE id           = ?  ";
    
    public static String ACTUALIZAR_USUARIO = "UPDATE usuarios SET "
            + "usuario            = ? "
            + "WHERE id           = ?  ";
    
    public static String ACTUALIZAR_PASSWORD = "UPDATE usuarios SET "
            + "password           = ?  "
            + "WHERE id           = ?  ";

    public static String ELIMINAR = "DELETE FROM usuarios WHERE id = ?";

    public static String ELIMINAR_TODO = "TRUNCATE TABLE usuarios";

    private int id;
    private String nombre;
    private String usuario;
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
