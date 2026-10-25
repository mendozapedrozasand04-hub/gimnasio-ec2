package pe.edu.gimnasio.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.*;
import pe.edu.gimnasio.config.Conexion;
import pe.edu.gimnasio.modelo.Pago;

public class PagoDAO {
  public List<Pago> listar(String q, String desde, String hasta, String metodo)
      throws SQLException {
    List<Pago> lista = new ArrayList<>();
    String sql =
        "SELECT pg.*,CONCAT(c.nombres,' ',c.apellidos) cliente FROM pago pg JOIN membresia m ON"
            + " pg.id_membresia=m.id_membresia JOIN cliente c ON m.id_cliente=c.id_cliente WHERE"
            + " CONCAT(c.nombres,' ',c.apellidos,' ',c.documento) LIKE ? AND fecha_pago>=? AND"
            + " fecha_pago<? AND (?='' OR metodo_pago=?) ORDER BY pg.id_pago DESC";
    try (Connection c = Conexion.abrir();
        PreparedStatement p = c.prepareStatement(sql)) {
      p.setString(1, "%" + q + "%");
      p.setTimestamp(2, Timestamp.valueOf(LocalDate.parse(desde).atStartOfDay()));
      p.setTimestamp(3, Timestamp.valueOf(LocalDate.parse(hasta).plusDays(1).atStartOfDay()));
      p.setString(4, metodo);
      p.setString(5, metodo);
      try (ResultSet r = p.executeQuery()) {
        while (r.next()) {
          Pago x = new Pago();
          x.setId(r.getInt("id_pago"));
          x.setIdMembresia(r.getInt("id_membresia"));
          x.setCliente(r.getString("cliente"));
          x.setMonto(r.getBigDecimal("monto"));
          x.setMetodo(r.getString("metodo_pago"));
          x.setFecha(r.getString("fecha_pago"));
          lista.add(x);
        }
      }
    }
    return lista;
  }

  public void registrar(int membresia, BigDecimal monto, String metodo) throws SQLException {
    if (!List.of("Efectivo", "Transferencia", "Tarjeta", "Billetera digital").contains(metodo))
      throw new IllegalArgumentException("Metodo de pago no valido.");
    try (Connection c = Conexion.abrir()) {
      c.setAutoCommit(false);
      try {
        try (PreparedStatement p =
            c.prepareStatement(
                "SELECT precio_pactado,estado FROM membresia WHERE id_membresia=? FOR UPDATE")) {
          p.setInt(1, membresia);
          try (ResultSet r = p.executeQuery()) {
            if (!r.next() || "CANCELADA".equals(r.getString("estado")))
              throw new IllegalArgumentException("La membresia no existe o esta cancelada.");
            if (monto.signum() <= 0 || monto.compareTo(r.getBigDecimal("precio_pactado")) != 0)
              throw new IllegalArgumentException(
                  "El pago debe ser igual al precio pactado. No se permiten abonos.");
          }
        }
        try (PreparedStatement p =
            c.prepareStatement(
                "INSERT INTO pago(id_membresia,monto,fecha_pago,metodo_pago) VALUES(?,?,?,?)")) {
          p.setInt(1, membresia);
          p.setBigDecimal(2, monto);
          p.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Lima"))));
          p.setString(4, metodo);
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
