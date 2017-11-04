package es.cifpcm.consumoelectrico.buscador.web;

import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mothcrown
 */
public class BaseDatos {
    private Connection conexion;
    private String path;
    private String usuario;
    private String password;
    private int tamannoPagina;
    private int tamPaginaEstandar = 10;
    private String driverClassName;
    
    public BaseDatos(ResourceBundle rb) {
        this.path = rb.getString("database.url");
        this.usuario = rb.getString("database.user");
        this.password = rb.getString("database.password");
        this.tamannoPagina = rb.getString("database.pageSize") == null ? tamPaginaEstandar : Integer.parseInt(rb.getString("database.pageSize"));
        this.driverClassName = rb.getString("database.driver");
    }
    
    public BaseDatos(String path, String usuario, String password) {
        conexion = null;
        this.path = path;
        this.usuario = usuario;
        this.password = password;
    }
    
    public void abrir() {
        try {
            Class.forName(driverClassName);
            conexion = DriverManager.getConnection(path, usuario, password);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e);
            //Logger.getLogger(BuscadorConsumoElectricoServlet.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void cerrar() {
        try {
            if (conexion != null)
                conexion.close();
        } catch (SQLException e) {
            Logger.getLogger(BuscadorConsumoElectricoServlet.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String nPath) {
        this.path = nPath;
    }
    
    public String getUsuario() {
        return usuario;
    }
    
    public void setUsuario(String nUsuario) {
        this.usuario = nUsuario;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String nPassword) {
        this.password = nPassword;
    }
    
    public int getTamannoPagina(){
        return tamannoPagina;
    }
    
    public void setTamannoPagina(int nTamannoPagina) {
        this.tamannoPagina = nTamannoPagina;
    }
    
    public int getTamPaginaEstandar(){
        return tamPaginaEstandar;
    }
    
    public void setTamPaginaEstandar(int nTamPaginaEstandar){
        this.tamPaginaEstandar = nTamPaginaEstandar;
    }
    
    public Connection getConexion() {
        return conexion;
    }
    
    public void setConexion(Connection nConexion) {
        this.conexion = nConexion;
    }
    
}
