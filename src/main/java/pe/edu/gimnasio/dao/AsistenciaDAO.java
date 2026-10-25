package pe.edu.gimnasio.dao;

import java.sql.*;
import java.time.*;
import java.util.*;
import pe.edu.gimnasio.config.Conexion;
import pe.edu.gimnasio.modelo.Asistencia;

public class AsistenciaDAO {
  public List<Asistencia> listar(String fecha, String q) throws SQLException {
    List<Asistencia> lista = new ArrayList<>();
    String sql =
        "SELECT a.*,CONCAT(c.nombres,' ',c.apellidos) cliente,c.documento FROM asistencia a JOIN"
            + " cliente c ON a.id_cliente=c.id_cliente WHERE a.fecha=? AND CONCAT(c.nombres,'"
            + " ',c.apellidos,' ',c.documento) LIKE ? ORDER BY a.hora_ingreso DESC";
    try (Connection c = Conexion.abrir();
        PreparedStatement p = c.prepareStatement(sql)) {
      p.setDate(1, java.sql.Date.valueOf(fecha));
      p.setString(2, "%" + q + "%");
      try (ResultSet r = p.executeQuery()) {
        while (r.next()) {
          Asistencia x = new Asistencia();
          x.setId(r.getInt("id_asistencia"));
          x.setCliente(r.getString("cliente"));
          x.setDocumento(r.getString("documento"));
          x.setFecha(r.getString("fecha"));
          x.setHora(r.getString("hora_ingreso"));
          lista.add(x);
        }
      }
    }
    return lista;
  }

  public void registrar(int cliente) throws SQLException {
    LocalDateTime ahora = LocalDateTime.now(ZoneId.of("America/Lima"));
    try (Connection c = Conexion.abrir()) {
      c.setAutoCommit(false);
      try {
        try (PreparedStatement p =
            c.prepareStatement("SELECT estado FROM cliente WHERE id_cliente=? FOR UPDATE")) {
          p.setInt(1, cliente);
          try (ResultSet r = p.executeQuery()) {
            if (!r.next() || !"ACTIVO".equals(r.getString(1)))
              throw new IllegalArgumentException("El cliente debe estar activo.");
          }
        }
        try (PreparedStatement p =
            c.prepareStatement(
                "SELECT m.id_membresia FROM membresia m JOIN pago pg ON"
                    + " pg.id_membresia=m.id_membresia WHERE m.id_cliente=? AND"
                    + " m.estado<>'CANCELADA' AND m.fecha_inicio<=? AND m.fecha_fin>=? FOR"
                    + " UPDATE")) {
          p.setInt(1, cliente);
          p.setDate(2, java.sql.Date.valueOf(ahora.toLocalDate()));
          p.setDate(3, java.sql.Date.valueOf(ahora.toLocalDate()));
          try (ResultSet r = p.executeQuery()) {
            if (!r.next())
              throw new IllegalArgumentException(
                  "Ingreso rechazado: el cliente no tiene una membresia vigente y pagada.");
          }
        }
        try (PreparedStatement p =
            c.prepareStatement(
                "INSERT INTO asistencia(id_cliente,fecha,hora_ingreso) VALUES(?,?,?)")) {
          p.setInt(1, cliente);
          p.setDate(2, java.sql.Date.valueOf(ahora.toLocalDate()));
          p.setTime(3, Time.valueOf(ahora.toLocalTime()));
          p.executeUpdate();
        }
        c.commit();
      } catch (SQLException | RuntimeException e) {
        c.rollback();
        throw e;
      }
    }
  }
}
