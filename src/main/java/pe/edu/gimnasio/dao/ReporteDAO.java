package pe.edu.gimnasio.dao;

import java.sql.*;
import java.time.*;
import java.util.*;
import pe.edu.gimnasio.config.Conexion;

public class ReporteDAO {
  public Map<String, Object> indicadores() throws SQLException {
    Map<String, Object> datos = new LinkedHashMap<>();
    String hoy = LocalDate.now(ZoneId.of("America/Lima")).toString();
    try (Connection c = Conexion.abrir()) {
      datos.put("Clientes activos", valor(c, "SELECT COUNT(*) FROM cliente WHERE estado='ACTIVO'"));
      datos.put(
          "Membresias vigentes",
          valor(
              c,
              "SELECT COUNT(*) FROM membresia WHERE estado<>'CANCELADA' AND fecha_inicio<='"
                  + hoy
                  + "' AND fecha_fin>='"
                  + hoy
                  + "'"));
      datos.put(
          "Proximas a vencer",
          valor(
              c,
              "SELECT COUNT(*) FROM membresia WHERE estado<>'CANCELADA' AND fecha_inicio<='"
                  + hoy
                  + "' AND fecha_fin BETWEEN '"
                  + hoy
                  + "' AND '"
                  + LocalDate.parse(hoy).plusDays(7)
                  + "'"));
      datos.put(
          "Membresias vencidas",
          valor(
              c,
              "SELECT COUNT(*) FROM membresia WHERE estado<>'CANCELADA' AND fecha_fin<'"
                  + hoy
                  + "'"));
      datos.put(
          "Ingresos del mes (S/)",
          valor(
              c,
              "SELECT COALESCE(SUM(monto),0) FROM pago WHERE fecha_pago>='"
                  + LocalDate.parse(hoy).withDayOfMonth(1)
                  + "' AND fecha_pago<'"
                  + LocalDate.parse(hoy).withDayOfMonth(1).plusMonths(1)
                  + "'"));
      datos.put(
          "Asistencias de hoy",
          valor(c, "SELECT COUNT(*) FROM asistencia WHERE fecha='" + hoy + "'"));
    }
    return datos;
  }

  private Object valor(Connection c, String sql) throws SQLException {
    try (PreparedStatement p = c.prepareStatement(sql);
        ResultSet r = p.executeQuery()) {
      r.next();
      return r.getObject(1);
    }
  }

  public List<String[]> planes(String desde, String hasta) throws SQLException {
    List<String[]> lista = new ArrayList<>();
    String sql =
        "SELECT p.nombre,COUNT(m.id_membresia) total FROM plan p LEFT JOIN membresia m ON"
            + " p.id_plan=m.id_plan AND m.estado<>'CANCELADA' AND m.fecha_inicio BETWEEN ? AND ?"
            + " GROUP BY p.id_plan,p.nombre ORDER BY total DESC";
    try (Connection c = Conexion.abrir();
        PreparedStatement p = c.prepareStatement(sql)) {
      p.setDate(1, java.sql.Date.valueOf(desde));
      p.setDate(2, java.sql.Date.valueOf(hasta));
      try (ResultSet r = p.executeQuery()) {
        while (r.next()) lista.add(new String[] {r.getString(1), r.getString(2)});
      }
    }
    return lista;
  }
}
