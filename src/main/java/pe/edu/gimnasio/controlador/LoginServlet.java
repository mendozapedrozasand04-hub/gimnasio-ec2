package pe.edu.gimnasio.controlador;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import pe.edu.gimnasio.dao.AdministradorDAO;
import pe.edu.gimnasio.util.Web;

public class LoginServlet extends HttpServlet {
  protected void doGet(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    if (r.getSession().getAttribute("administrador") != null) {
      s.sendRedirect(r.getContextPath() + "/inicio");
      return;
    }
    r.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(r, s);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    try {
      String nombre =
          new AdministradorDAO()
              .autenticar(Web.texto(r, "correo", 150), Web.texto(r, "clave", 100));
      if (nombre == null) throw new IllegalArgumentException("Correo o contrasena incorrectos.");
      r.changeSessionId();
      r.getSession().setAttribute("administrador", nombre);
      s.sendRedirect(r.getContextPath() + "/inicio");
    } catch (Exception e) {
      r.setAttribute("error", Web.error(e));
      r.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(r, s);
    }
  }
}
