package pe.edu.gimnasio.controlador;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import pe.edu.gimnasio.dao.*;
import pe.edu.gimnasio.util.Web;

public class PagoServlet extends HttpServlet {
  protected void doGet(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    try {
      ReporteServlet.fechas(r);
      r.setAttribute(
          "pagos",
          new PagoDAO()
              .listar(
                  ReporteServlet.param(r, "q"),
                  r.getAttribute("desde").toString(),
                  r.getAttribute("hasta").toString(),
                  ReporteServlet.param(r, "metodo")));
      r.setAttribute("membresias", new MembresiaDAO().listar(""));
    } catch (Exception e) {
      r.setAttribute("error", Web.error(e));
    }
    r.getRequestDispatcher("/WEB-INF/vistas/pagos.jsp").forward(r, s);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    try {
      new PagoDAO()
          .registrar(
              Web.numero(r, "id_membresia"),
              Web.monto(r, "monto"),
              Web.texto(r, "metodo_pago", 25));
      Web.mensaje(r, "Pago completo registrado correctamente.");
      s.sendRedirect(r.getContextPath() + "/pagos");
    } catch (Exception e) {
      r.setAttribute("error", Web.error(e));
      doGet(r, s);
    }
  }
}
