package pe.edu.gimnasio.controlador;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import pe.edu.gimnasio.dao.*;
import pe.edu.gimnasio.util.Web;

public class MembresiaServlet extends HttpServlet {
  protected void doGet(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    try {
      r.setAttribute(
          "membresias",
          new MembresiaDAO().listar(r.getParameter("q") == null ? "" : r.getParameter("q")));
      r.setAttribute("clientes", new ClienteDAO().listar("", "ACTIVO"));
      r.setAttribute("planes", new PlanDAO().listar());
    } catch (Exception e) {
      r.setAttribute("error", Web.error(e));
    }
    r.getRequestDispatcher("/WEB-INF/vistas/membresias.jsp").forward(r, s);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    try {
      if ("cancelar".equals(r.getParameter("accion")))
        new MembresiaDAO().cancelar(Web.numero(r, "id"));
      else {
        LocalDate fecha;
        try {
          fecha = LocalDate.parse(r.getParameter("fecha_inicio"));
        } catch (Exception e) {
          throw new IllegalArgumentException("Seleccione una fecha de inicio valida.");
        }
        if (fecha.getYear() < 2000 || fecha.getYear() > 2100)
          throw new IllegalArgumentException("La fecha debe estar entre 2000 y 2100.");
        new MembresiaDAO().registrar(Web.numero(r, "id_cliente"), Web.numero(r, "id_plan"), fecha);
      }
      Web.mensaje(r, "Membresia guardada correctamente.");
      s.sendRedirect(r.getContextPath() + "/membresias");
    } catch (Exception e) {
      r.setAttribute("error", Web.error(e));
      doGet(r, s);
    }
  }
}
