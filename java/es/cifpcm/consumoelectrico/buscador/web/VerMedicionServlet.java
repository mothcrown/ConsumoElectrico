package es.cifpcm.consumoelectrico.buscador.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * VerMedición: servlet cuco que idealmente pondríamos en un popup pero
 * no sé cómo va lo de mezclar JDBC con JavaScript todavía, que esto es muy 
 * sensible
 * 
 * @author mothcrown
 */
public class VerMedicionServlet extends HttpServlet {
    
    private BaseDatos db;
    
    /**
     * Método Init donde conectamos la Base de Datos!
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
     * Ya tu sabes
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
            sb.append(renderizaCabecera());
            sb.append("<div class=\"contenedor\">");
            
            String idMedicion = request.getParameter("idMedicion");
            sb.append(muestraMedicion(idMedicion));
            
            sb.append("</div>");
            sb.append("<footer><p>por mothcrown</p></footer>");
            sb.append("</body></html>");
            out.println(sb);
        }
    }
    
    /**
     * Hacemos consulta, mostramos resultado. Guay? Guay
     * 
     * @param idMedicion
     * @return 
     */
    protected StringBuffer muestraMedicion(String idMedicion) {
        StringBuffer sb = new StringBuffer();
        Connection conexion = null;
        
        try {
            db.abrir();
            conexion = db.getConexion();
            
            Statement comando = conexion.createStatement();
            //  Total de tiempo perdido aquí: 2h 21m
            //  No te olvides de lloriquearle a Inma sobre esto!
            ResultSet resultado = comando.executeQuery(
                    "select * from mediciones where idMedicion = "+ idMedicion
            );
            if (resultado.next()) {
                sb.append("<h2>");
                sb.append("En la fecha ");
                sb.append(resultado.getDate("FechaHora"));
                sb.append(" ");
                sb.append(resultado.getTime("FechaHora"));
                sb.append(" la medición al cliente ");
                sb.append(resultado.getInt("Cliente"));
                sb.append(" fue de  ");
                sb.append(resultado.getFloat("KW"));
                sb.append("</h2>");
            }
        } catch (SQLException  e) {
            Logger.getLogger(VerMedicionServlet.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                if (conexion != null) {
                    conexion.close();
                    db.cerrar();
                }
            } catch (SQLException ex) {
                Logger.getLogger(VerMedicionServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return sb;
    }
    
    /**
     * Igualico, igualico, que el difunto de su abuelico.
     * 
     * Es la una y media de la mañana.
     * 
     * @return 
     */
    protected StringBuffer renderizaCabecera() {
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE html>");
        sb.append("<html><head>");
        sb.append("<title>Ver Medición</title>");
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
