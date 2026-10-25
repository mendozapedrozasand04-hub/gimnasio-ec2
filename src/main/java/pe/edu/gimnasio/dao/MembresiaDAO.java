package pe.edu.gimnasio.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import pe.edu.gimnasio.config.Conexion;
import pe.edu.gimnasio.modelo.Membresia;

public class MembresiaDAO {
  public List<Membresia> listar(String q) throws SQLException {
    List<Membresia> lista = new ArrayList<>();
    String sql =
        "SELECT m.*,CONCAT(c.nombres,' ',c.apellidos) cliente,p.nombre plan,pg.id_pago FROM"
            + " membresia m JOIN cliente c ON m.id_cliente=c.id_cliente JOIN plan p ON"
            + " m.id_plan=p.id_plan LEFT JOIN pago pg ON pg.id_membresia=m.id_membresia WHERE"
            + " CONCAT(c.nombres,' ',c.apellidos,' ',c.documento) LIKE ? ORDER BY m.id_membresia"
            + " DESC";
    try (Connection c = Conexion.abrir();
        PreparedStatement p = c.prepareStatement(sql)) {
      p.setString(1, "%" + q + "%");
      try (ResultSet r = p.executeQuery()) {
        while (r.next()) {
          Membresia x = new Membresia();
          x.setId(r.getInt("id_membresia"));
          x.setIdCliente(r.getInt("id_cliente"));
          x.setIdPlan(r.getInt("id_plan"));
          x.setCliente(r.getString("cliente"));
          x.setPlan(r.getString("plan"));
          x.setFechaInicio(r.getDate("fecha_inicio").toLocalDate());
          x.setFechaFin(r.getDate("fecha_fin").toLocalDate());
          x.setPrecioPactado(r.getBigDecimal("precio_pactado"));
          x.setEstado(r.getString("estado"));
          x.setPagada(r.getObject("id_pago") != null);
          lista.add(x);
        }
      }
    }
    return lista;
  }

  public void registrar(int cliente, int plan, LocalDate inicio) throws SQLException {
    try (Connection c = Conexion.abrir()) {
      c.setAutoCommit(false);
      try {
        // Bloquear al cliente evita contratos superpuestos registrados a la vez.
        try (PreparedStatement p =
            c.prepareStatement("SELECT estado FROM cliente WHERE id_cliente=? FOR UPDATE")) {
          p.setInt(1, cliente);
          try (ResultSet r = p.executeQuery()) {
            if (!r.next() || !"ACTIVO".equals(r.getString(1)))
              throw new IllegalArgumentException("Seleccione un cliente activo.");
          }
        }
        int dias;
        java.math.BigDecimal precio;
        try (PreparedStatement p =
            c.prepareStatement(
                "SELECT duracion_dias,precio,estado FROM plan WHERE id_plan=? FOR UPDATE")) {
          p.setInt(1, plan);
          try (ResultSet r = p.executeQuery()) {
            if (!r.next() || !"ACTIVO".equals(r.getString("estado")))
              throw new IllegalArgumentException("Seleccione un plan activo.");
            dias = r.getInt("duracion_dias");
            precio = r.getBigDecimal("precio");
          }
        }
        LocalDate fin = inicio.plusDays(dias - 1);
        try (PreparedStatement p =
            c.prepareStatement(
                "SELECT id_membresia FROM membresia WHERE id_cliente=? AND estado<>'CANCELADA' AND"
                    + " fecha_inicio<=? AND fecha_fin>=?")) {
          p.setInt(1, cliente);
          p.setDate(2, java.sql.Date.valueOf(fin));
          p.setDate(3, java.sql.Date.valueOf(inicio));
          try (ResultSet r = p.executeQuery()) {
            if (r.next())
              throw new IllegalArgumentException(
                  "El cliente ya tiene una membresia en ese periodo. La renovacion debe comenzar"
                      + " despues del vencimiento.");
          }
        }
        try (PreparedStatement p =
            c.prepareStatement(
                "INSERT INTO"
                    + " membresia(id_cliente,id_plan,fecha_inicio,fecha_fin,precio_pactado,estado)"
                    + " VALUES(?,?,?,?,?,'REGISTRADA')")) {
          p.setInt(1, cliente);
          p.setInt(2, plan);
          p.setDate(3, java.sql.Date.valueOf(inicio));
          p.setDate(4, java.sql.Date.valueOf(fin));
          p.setBigDecimal(5, precio);
          p.executeUpdate();
        }
        c.commit();
      } catch (SQLException | RuntimeException e) {
        c.rollback();
        throw e;
      }
    }
  }

  public void cancelar(int id) throws SQLException {
    try (Connection c = Conexion.abrir();
        PreparedStatement p =
            c.prepareStatement("UPDATE membresia SET estado='CANCELADA' WHERE id_membresia=?")) {
      p.setInt(1, id);
      p.executeUpdate();
    }
  }
}
