package pe.edu.gimnasio.controlador;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import pe.edu.gimnasio.dao.ReporteDAO;
import pe.edu.gimnasio.util.Web;

public class InicioServlet extends HttpServlet {
  protected void doGet(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    try {
      r.setAttribute("indicadores", new ReporteDAO().indicadores());
    } catch (Exception e) {
      r.setAttribute("error", Web.error(e));
    }
    r.getRequestDispatcher("/WEB-INF/vistas/inicio.jsp").forward(r, s);
  }
}
