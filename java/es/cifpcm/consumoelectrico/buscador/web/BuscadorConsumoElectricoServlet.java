package es.cifpcm.consumoelectrico.buscador.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Buscador Consumo Eléctrico: práctica de acceso a DB desde Java!
 * 
 * @author mothcrown
 */
public class BuscadorConsumoElectricoServlet extends HttpServlet {
    
    /**
     *  Atributos: base de datos y lista de clientes
     */
    private BaseDatos db;
    private ArrayList<Cliente> clientes;
    
    /**
     * Método INIT. Esto se dispara cuando cargamos el server, pilla la info
     * de consumoelectrico.properties (aka app.config) y la manda al constructor
     * de BaseDatos donde resolvemos el ResourceBundle
     * 
     * @param config
     * @throws ServletException 
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        String configBundleName = config.getInitParameter("app.config");
        ResourceBundle rb = ResourceBundle.getBundle(configBundleName);
        db = new BaseDatos(rb);
    }
    
    /**
     * Cositas de HTTP con <code>GET</code> y <code>POST</code> y demás 
     * zarandajas. También renderizamos la web aquí, que es lo importante!
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            StringBuffer sb = new StringBuffer();
            //  Renderizamos cabecera
            sb.append(renderizaCabecera());
            sb.append("<div class=\"contenedor\">");
            
            //  Renderizamos la tabla con los 10 clientes!
            sb.append(renderizaTablaClientes());
            
            //  Cogemos el nombre del cliente, lo ponemos presentable con un
            //  trimeito y lo convertimos en array en caso de que el usuario
            //  haya metido nombre y apellido. Guay, eh?
            String nombreTmp = request.getParameter("cajaCliente");
            nombreTmp = nombreTmp.trim();
            String[] nombre = nombreTmp.split(" ");
            sb.append(consultaCliente(nombre));
            
            sb.append("</div>");
            sb.append("<footer><p>por mothcrown</p></footer>");
            sb.append("</body></html>");
            out.println(sb);
        }
    }
    /**
     * Esto contiene el header de la página web. No hace falta más!
     * 
     * @return 
     */
    protected StringBuffer renderizaCabecera() {
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE html>");
        sb.append("<html><head>");
        sb.append("<title>Consulta clientes</title>");
        sb.append("<meta http-equiv=\"Content-Type\" content=\text/html; charset=UTF-8\">");
        sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />");
        sb.append("<link rel=\"stylesheet\" href=\"css/style.css\">");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<nav>");
        sb.append("<h1>Base de datos de consumo eléctrico</h1>");
        sb.append("</nav>");
        
        return sb;
    }
    
    /**
     * Hacemos la consulta según el nombre que nos pasa el usuario, o no! Porque
     * lo mismo no encontramos el nombre del cliente y eso hay que decirlo 
     * también!
     * 
     * @param nombre String[]
     * @return 
     */
    protected StringBuffer consultaCliente(String[] nombre) {
        StringBuffer sb = new StringBuffer();
        String[] campos = new String[] { "Id Medición", "Fecha/Hora", "Medición de KW" };
        Connection conexion = null;
        String nomCliente = nombre[0];
        //  Resolvemos el apellido del cliente
        String apellCliente = (nombre.length > 1) ? nombre[1] : "";

        try {
            db.abrir();
            conexion = db.getConexion();
            
            Statement comando = conexion.createStatement();
            //  Join muy rico con la opción de buscar con el apellido también
            //  en caso de que hubiese dos clientes con el mismo nombre
            // (que no es el caso, pero porsiaca)
            ResultSet resultado = comando.executeQuery(
                    "select * from mediciones as m "
                            + "join misclientes as c "
                            + "on c.id = m.cliente "
                            + "where c.nombre='" + nomCliente +
                            ((!apellCliente.equals("")) ? "' AND c.apellido='" + nombre[1] + "'" : "'")
            );
            if (resultado.next()) {
                //  Debería sacar esto de aquí, verdad? Debería sacar esto de aquí
                sb.append("<h2>");
                sb.append("Estas son las mediciones para ");
                sb.append(nombre[0]);
                if (nombre.length > 1) {
                    sb.append(" ");
                    sb.append(nombre[1]);
                }
                sb.append("</h2>");
                
                sb.append("<table>");
                sb.append(renderizaCabeceraTabla(campos));
                
                do {
                    sb.append("<tr>");
                    
                    sb.append("<td>");
                    sb.append(resultado.getInt("idMedicion"));
                    sb.append("</td>");
                    
                    sb.append("<td>");
                    // Hay una forma mejor de hacer esto?! Esto es horrible
                    // PREGUNTA A INMA!
                    sb.append(resultado.getDate("FechaHora"));
                    sb.append(" ");
                    sb.append(resultado.getTime("FechaHora"));
                    sb.append("</td>");
                    
                    //  Creamos el link a la página esa chupi donde vemos
                    //  la medición!
                    sb.append("<td class=\"link\">");
                    sb.append("<a href=\"vermedicion?idMedicion=");
                    sb.append(resultado.getInt("idMedicion"));
                    sb.append("\" target=\"_blank\">Ver</a>");
                    sb.append("</td>");
                    
                    sb.append("</tr>");
                    
                } while (resultado.next());
                sb.append("</table>");
                //  Lo que pone en la lata
                sb.append(muestraConsumoTotal(nombre));
            }
            else if (!resultado.next()) {
                //  Control de errores en la consulta
                sb.append("<h2>");
                sb.append("Error: no se ha encontrado a ningún cliente con el nombre ");
                sb.append(nombre[0]);
                if (nombre.length > 1) {
                    sb.append(" ");
                    sb.append(nombre[1]);
                }
                sb.append("</h2>");
            }
        } catch (SQLException  e) {
            Logger.getLogger(BuscadorConsumoElectricoServlet.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                if (conexion != null) {
                    //  En teoría esto debería funcionar sin uno de los dos.
                    //  PERO NO ME ATREVO, INMA, NO ME ATREVO :S
                    conexion.close();
                    db.cerrar();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BuscadorConsumoElectricoServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return sb;
    }
    
    /**
     * Muestra el consumo total de kw realizado por el cliente.
     * 
     * @param nombre String[]
     * @return 
     */
    protected StringBuffer muestraConsumoTotal(String[] nombre) {
        StringBuffer sb = new StringBuffer();
        Connection conexion = null;
        String nomCliente = nombre[0];
        String apellCliente = (nombre.length > 1) ? nombre[1] : "";
        
        try {
            db.abrir();
            conexion = db.getConexion();
            
            Statement comando = conexion.createStatement();
            //  sum(kw) group by codcliente
            ResultSet resultado = comando.executeQuery(
                    "select sum(KW) from mediciones as m "
                            + "join misclientes as c "
                            + "on c.id = m.cliente "
                            + "where c.nombre='" + nomCliente +
                            ((!apellCliente.equals("")) ? "' AND c.apellido='" + nombre[1] + "' " : "' ") +
                            "group by m.cliente"
            );
            if (resultado.next()) {
                sb.append("<h2>");
                sb.append("El consumo total de ");
                sb.append(nombre[0]);
                if (nombre.length > 1) {
                    sb.append(" ");
                    sb.append(nombre[1]);
                }
                sb.append(" es ");
                sb.append(resultado.getFloat(1));
                sb.append("</h2>");
            }
        } catch (SQLException  e) {
            Logger.getLogger(BuscadorConsumoElectricoServlet.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                if (conexion != null) {
                    conexion.close();
                    db.cerrar();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BuscadorConsumoElectricoServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return sb;
    }
    
    /**
     * Una muy simpática función para meter th en table. Paso array con lista
     * de campos y me devuelve una tr muy mona
     * 
     * @param campos String[]
     * @return 
     */
    protected StringBuffer renderizaCabeceraTabla(String[] campos) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<tr>");
        for (String campo : campos) {
            sb.append("<th>");
            sb.append(campo);
            sb.append("</th>");
        }
        sb.append("</tr>");
        
        return sb;
    }
    
    /**
     * Pillamos los 10 primeros clientes de la BD, los formateamos en clase
     * Cliente, los metemos en atributo ArrayList<Cliente> clientes y 
     * renderizamos tabla
     * 
     * @return 
     */
    protected StringBuffer renderizaTablaClientes() {
        StringBuffer sb = new StringBuffer();
        String[] campos = new String[] {
            "Id", "Nombre", "Apellido", "Calle", "Número", "Código Postal",
            "Población", "Provincia"
        };
        
        //  this is where all the magic happens! Weeee :D
        cargaClientes();
        
        sb.append("<h2>Lista de Clientes</h2>");
        sb.append("<table>");
        sb.append(renderizaCabeceraTabla(campos));
        
        for (Cliente cliente : clientes) {
            sb.append("<tr>");
            
            sb.append("<td>");
            sb.append(cliente.getId());
            sb.append("</td>");
            
            sb.append("<td>");
            sb.append(cliente.getNombre());
            sb.append("</td>");
            
            sb.append("<td>");
            sb.append(cliente.getApellido());
            sb.append("</td>");
            
            sb.append("<td>");
            sb.append(cliente.getNombreCalle());
            sb.append("</td>");
            
            sb.append("<td>");
            sb.append(cliente.getNumero());
            sb.append("</td>");
            
            sb.append("<td>");
            sb.append(cliente.getCodPostal());
            sb.append("</td>");
            
            sb.append("<td>");
            sb.append(cliente.getPoblacion());
            sb.append("</td>");
            
            sb.append("<td>");
            sb.append(cliente.getProvincia());
            sb.append("</td>");
            
            sb.append("</tr>");
        }
        sb.append("</table>");
                
        return sb;
    }
    
    /**
     * Hacemos consulta, pillamos 10 clientes usando setMaxRows(10),
     * formateamos y metemos en clientes. Simple!
     */
    protected void cargaClientes() {
        clientes = new ArrayList<>();
        Connection conexion = null;

        try {
            db.abrir();
            conexion = db.getConexion();
            
            Statement comando = conexion.createStatement();
            comando.setMaxRows(10);
            ResultSet resultado = comando.executeQuery("select * from misclientes"); // limit 10
            while (resultado.next()) {
                clientes.add(new Cliente(
                        resultado.getInt("id"), 
                        resultado.getString("nombre"),
                        resultado.getString("apellido"),
                        resultado.getString("nombreCalle"),
                        resultado.getString("numero"),
                        resultado.getInt("codPostal"),
                        resultado.getString("poblacion"),
                        resultado.getString("provincia")
                ));
            }
        } catch (SQLException  e) {
            Logger.getLogger(BuscadorConsumoElectricoServlet.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                if (conexion != null) {
                    conexion.close();
                    db.cerrar();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BuscadorConsumoElectricoServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
