package pe.edu.gimnasio.controlador;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.*;
import pe.edu.gimnasio.dao.*;
import pe.edu.gimnasio.util.Web;

public class AsistenciaServlet extends HttpServlet {
  protected void doGet(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    try {
      String fecha = r.getParameter("fecha");
      if (fecha == null || fecha.isBlank())
        fecha = LocalDate.now(ZoneId.of("America/Lima")).toString();
      try {
        LocalDate.parse(fecha);
      } catch (Exception e) {
        throw new IllegalArgumentException("Fecha no valida.");
      }
      r.setAttribute("fecha", fecha);
      r.setAttribute(
          "asistencias", new AsistenciaDAO().listar(fecha, ReporteServlet.param(r, "q")));
      r.setAttribute("clientes", new ClienteDAO().listar("", "ACTIVO"));
    } catch (Exception e) {
      r.setAttribute("error", Web.error(e));
    }
    r.getRequestDispatcher("/WEB-INF/vistas/asistencias.jsp").forward(r, s);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    try {
      new AsistenciaDAO().registrar(Web.numero(r, "id_cliente"));
      Web.mensaje(r, "Ingreso registrado correctamente.");
      s.sendRedirect(r.getContextPath() + "/asistencias");
    } catch (Exception e) {
      r.setAttribute("error", Web.error(e));
      doGet(r, s);
    }
  }
}
