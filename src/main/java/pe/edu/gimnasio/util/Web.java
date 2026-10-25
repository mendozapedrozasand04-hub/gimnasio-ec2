package pe.edu.gimnasio.util;

import jakarta.servlet.http.*;
import java.math.BigDecimal;

public class Web {
  public static String html(Object valor) {
    if (valor == null) return "";
    return valor
        .toString()
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }

  public static String texto(HttpServletRequest r, String campo, int maximo) {
    String valor = r.getParameter(campo);
    if (valor == null || valor.trim().isEmpty())
      throw new IllegalArgumentException("Complete el campo " + campo + ".");
    valor = valor.trim();
    if (valor.length() > maximo)
      throw new IllegalArgumentException("El campo " + campo + " es demasiado largo.");
    return valor;
  }

  public static int numero(HttpServletRequest r, String campo) {
    try {
      int n = Integer.parseInt(r.getParameter(campo));
      if (n <= 0) throw new Exception();
      return n;
    } catch (Exception e) {
      throw new IllegalArgumentException("Seleccione un valor valido para " + campo + ".");
    }
  }

  public static BigDecimal monto(HttpServletRequest r, String campo) {
    try {
      BigDecimal m = new BigDecimal(r.getParameter(campo));
      if (m.signum() <= 0 || m.scale() > 2 || m.compareTo(new BigDecimal("99999999.99")) > 0)
        throw new Exception();
      return m;
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "El importe debe ser positivo y tener hasta dos decimales.");
    }
  }

  public static void mensaje(HttpServletRequest r, String texto) {
    r.getSession().setAttribute("mensaje", texto);
  }

  public static String error(Exception e) {
    if (e instanceof IllegalArgumentException) return e.getMessage();
    if (e instanceof java.sql.SQLException
        && (((java.sql.SQLException) e).getErrorCode() == 1062
            || "23505".equals(((java.sql.SQLException) e).getSQLState())))
      return "Ese registro ya existe. Verifique el documento, pago o asistencia.";
    e.printStackTrace();
    return "No se pudo completar la operacion. Verifique MySQL en el puerto 3307 y la importacion"
               + " del archivo SQL.";
  }
}
