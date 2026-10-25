package pe.edu.gimnasio.controlador;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import pe.edu.gimnasio.dao.PlanDAO;
import pe.edu.gimnasio.modelo.Plan;
import pe.edu.gimnasio.util.Web;

public class PlanServlet extends HttpServlet {
  private final PlanDAO dao = new PlanDAO();

  protected void doGet(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    try {
      r.setAttribute("planes", dao.listar());
      if ("editar".equals(r.getParameter("accion")))
        r.setAttribute("plan", dao.buscar(Web.numero(r, "id")));
    } catch (Exception e) {
      r.setAttribute("error", Web.error(e));
    }
    r.getRequestDispatcher("/WEB-INF/vistas/planes.jsp").forward(r, s);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    try {
      if ("desactivar".equals(r.getParameter("accion"))) dao.desactivar(Web.numero(r, "id"));
      else {
        Plan x = new Plan();
        String id = r.getParameter("id");
        x.setId(id == null || id.isBlank() ? 0 : Web.numero(r, "id"));
        x.setNombre(Web.texto(r, "nombre", 80));
        x.setDuracionDias(Web.numero(r, "duracion_dias"));
        if (x.getDuracionDias() > 3650)
          throw new IllegalArgumentException("La duracion maxima es de 3650 dias.");
        x.setPrecio(Web.monto(r, "precio"));
        String d = r.getParameter("descripcion");
        if (d != null && d.length() > 250)
          throw new IllegalArgumentException("La descripcion admite hasta 250 caracteres.");
        x.setDescripcion(d);
        x.setEstado("INACTIVO".equals(r.getParameter("estado")) ? "INACTIVO" : "ACTIVO");
        dao.guardar(x);
      }
      Web.mensaje(r, "Plan guardado correctamente.");
      s.sendRedirect(r.getContextPath() + "/planes");
    } catch (Exception e) {
      r.setAttribute("error", Web.error(e));
      r.setAttribute("reintento", true);
      doGet(r, s);
    }
  }
}
