package pe.edu.gimnasio.controlador;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import pe.edu.gimnasio.dao.ClienteDAO;
import pe.edu.gimnasio.modelo.Cliente;
import pe.edu.gimnasio.util.Web;

public class ClienteServlet extends HttpServlet {
  private final ClienteDAO dao = new ClienteDAO();

  protected void doGet(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    try {
      r.setAttribute(
          "clientes",
          dao.listar(
              r.getParameter("q") == null ? "" : r.getParameter("q"),
              r.getParameter("estado") == null ? "" : r.getParameter("estado")));
      if ("editar".equals(r.getParameter("accion")))
        r.setAttribute("cliente", dao.buscar(Web.numero(r, "id")));
    } catch (Exception e) {
      r.setAttribute("error", Web.error(e));
    }
    r.getRequestDispatcher("/WEB-INF/vistas/clientes.jsp").forward(r, s);
  }

  protected void doPost(HttpServletRequest r, HttpServletResponse s)
      throws ServletException, IOException {
    try {
      if ("desactivar".equals(r.getParameter("accion"))) {
        dao.desactivar(Web.numero(r, "id"));
      } else {
        Cliente x = new Cliente();
        String id = r.getParameter("id");
        x.setId(id == null || id.isBlank() ? 0 : Web.numero(r, "id"));
        x.setTipoDocumento(Web.texto(r, "tipo_documento", 10));
        x.setDocumento(Web.texto(r, "documento", 20));
        if (!java.util.List.of("DNI", "CE", "PASAPORTE").contains(x.getTipoDocumento()))
          throw new IllegalArgumentException("Tipo de documento no valido.");
        if ("DNI".equals(x.getTipoDocumento()) && !x.getDocumento().matches("[0-9]{8}"))
          throw new IllegalArgumentException("El DNI debe tener 8 digitos.");
        x.setNombres(Web.texto(r, "nombres", 100));
        x.setApellidos(Web.texto(r, "apellidos", 100));
        x.setTelefono(Web.texto(r, "telefono", 20));
        String correo = r.getParameter("correo");
        correo = correo == null ? "" : correo.trim();
        if (correo.length() > 150
            || (!correo.isEmpty() && !correo.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")))
          throw new IllegalArgumentException("Ingrese un correo valido.");
        x.setCorreo(correo);
        x.setEstado("INACTIVO".equals(r.getParameter("estado")) ? "INACTIVO" : "ACTIVO");
        dao.guardar(x);
      }
      Web.mensaje(r, "Cliente guardado correctamente.");
      s.sendRedirect(r.getContextPath() + "/clientes");
    } catch (Exception e) {
      r.setAttribute("error", Web.error(e));
      r.setAttribute("reintento", true);
      doGet(r, s);
    }
  }
}
