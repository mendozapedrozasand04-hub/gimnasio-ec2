package pe.edu.gimnasio.controlador;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

public class SeguridadFiltro implements Filter {
  public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena)
      throws IOException, ServletException {
    HttpServletRequest r = (HttpServletRequest) solicitud;
    HttpServletResponse s = (HttpServletResponse) respuesta;
    r.setCharacterEncoding("UTF-8");
    s.setCharacterEncoding("UTF-8");
    String ruta = r.getServletPath();
    s.setHeader("X-Content-Type-Options", "nosniff");
    s.setHeader("X-Frame-Options", "DENY");
    if (ruta.startsWith("/css/") || ruta.startsWith("/js/")) {
      cadena.doFilter(r, s);
      return;
    }
    s.setHeader("Cache-Control", "no-store");
    HttpSession sesion = r.getSession();
    if (sesion.getAttribute("token") == null)
      sesion.setAttribute("token", UUID.randomUUID().toString());
    if (!ruta.equals("/login") && sesion.getAttribute("administrador") == null) {
      s.sendRedirect(r.getContextPath() + "/login");
      return;
    }
    if ("POST".equals(r.getMethod())
        && !sesion.getAttribute("token").equals(r.getParameter("token"))) {
      s.sendError(403, "El formulario vencio. Recargue la pagina.");
      return;
    }
    cadena.doFilter(r, s);
  }
}
