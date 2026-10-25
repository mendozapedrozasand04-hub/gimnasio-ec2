package pe.edu.gimnasio.controlador;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import pe.edu.gimnasio.dao.*;
import pe.edu.gimnasio.modelo.Pago;
import pe.edu.gimnasio.util.Web;

public class ReporteServlet extends HttpServlet {
  public static String param(HttpServletRequest r, String nombre) {
    String s = r.getParameter(nombre);
    return s == null ? "" : s.trim();
  }

  public static void fechas(HttpServletRequest r) {
    LocalDate hoy = LocalDate.now(ZoneId.of("America/Lima"));
    try {
      LocalDate desde =
          param(r, "desde").isEmpty() ? hoy.withDayOfMonth(1) : LocalDate.parse(param(r, "desde"));
      LocalDate hasta = param(r, "hasta").isEmpty() ? hoy : LocalDate.parse(param(r, "hasta"));
      if (desde.isAfter(hasta)) throw new Exception();
      r.setAttribute("desde", desde.toString());
      r.setAttribute("hasta", hasta.toString());
    } catch (Exception e) {
      r.setAttribute("desde", hoy.withDayOfMonth(1).toString());
      r.setAttribute("hasta", hoy.toString());
      throw new IllegalArgumentException(
          "Revise el periodo: la fecha inicial no puede ser posterior a la final.");
    }
  }

  private String csv(String s) {
    if (s.matches("^[=+@\\-].*")) s = "'" + s;
    return "\"" + s.replace("\"", "\"\"") + "\"";
  }

  protected void doGet(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    try {
      fechas(r);
      String desde = r.getAttribute("desde").toString(), hasta = r.getAttribute("hasta").toString();
      List<Pago> pagos = new PagoDAO().listar(param(r, "q"), desde, hasta, param(r, "metodo"));
      BigDecimal total = BigDecimal.ZERO;
      for (Pago p : pagos) total = total.add(p.getMonto());
      List<String[]> planes = new ReporteDAO().planes(desde, hasta);
      if ("csv".equals(param(r, "formato"))) {
        s.setContentType("text/csv;charset=UTF-8");
        s.setHeader("Content-Disposition", "attachment; filename=ingresos.csv");
        PrintWriter w = s.getWriter();
        w.print('\ufeff');
        w.println("Cliente;Membresia;Fecha;Metodo;Monto");
        for (Pago p : pagos)
          w.println(
              csv(p.getCliente())
                  + ";"
                  + p.getIdMembresia()
                  + ";"
                  + csv(p.getFecha())
                  + ";"
                  + csv(p.getMetodo())
                  + ";"
                  + p.getMonto());
        w.println("Total;;;;" + total);
        return;
      }
      r.setAttribute("pagos", pagos);
      r.setAttribute("total", total);
      r.setAttribute("planes", planes);
    } catch (Exception e) {
      r.setAttribute("error", Web.error(e));
    }
    r.getRequestDispatcher("/WEB-INF/vistas/reportes.jsp").forward(r, s);
  }
}
