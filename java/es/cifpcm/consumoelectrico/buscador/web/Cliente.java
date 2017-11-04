package es.cifpcm.consumoelectrico.buscador.web;

/**
 *
 * @author mothcrown
 */
public class Cliente {
    private int id = 0;
    private String nombre = null;
    private String apellido = null;
    private String nombreCalle = null;
    private String numero = null;
    private int codPostal = 0;
    private String poblacion = null;
    private String provincia = null;
    
    public Cliente() {}
    
    public Cliente(int id, String nombre, String apellido) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
    }
    
    public Cliente (int id, String nombre, String apellido, String nombreCalle, 
            String numero, int codPostal, String poblacion, String provincia) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreCalle = nombreCalle;
        this.numero = numero;
        this.codPostal = codPostal;
        this.poblacion = poblacion;
        this.provincia = provincia;
    }
    
    public int getId() {
        return id;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setNombreCalle(String nombreCalle) {
        this.nombreCalle = nombreCalle;
    }
    
    public String getNombreCalle() {
        return nombreCalle;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setCodPostal(int codPostal) {
        this.codPostal = codPostal;
    }
    
    public int getCodPostal() {
        return codPostal;
    }
    
    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }
    
    public String getPoblacion() {
        return poblacion;
    }
    
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    
    public String getProvincia() {
        return provincia;
    }

}
