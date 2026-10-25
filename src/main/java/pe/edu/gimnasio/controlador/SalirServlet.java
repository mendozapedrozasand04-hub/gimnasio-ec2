package pe.edu.gimnasio.controlador;

import jakarta.servlet.http.*;
import java.io.IOException;

public class SalirServlet extends HttpServlet {
  protected void doPost(HttpServletRequest r, HttpServletResponse s) throws IOException {
    r.getSession().invalidate();
    s.sendRedirect(r.getContextPath() + "/login");
  }
}
