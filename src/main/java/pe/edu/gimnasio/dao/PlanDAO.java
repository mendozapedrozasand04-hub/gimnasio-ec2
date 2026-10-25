package pe.edu.gimnasio.dao;

import java.sql.*;
import java.util.*;
import pe.edu.gimnasio.config.Conexion;
import pe.edu.gimnasio.modelo.Plan;

public class PlanDAO {
  public List<Plan> listar() throws SQLException {
    List<Plan> lista = new ArrayList<>();
    try (Connection c = Conexion.abrir();
        PreparedStatement p = c.prepareStatement("SELECT * FROM plan ORDER BY id_plan DESC");
        ResultSet r = p.executeQuery()) {
      while (r.next()) lista.add(leer(r));
    }
    return lista;
  }

  public Plan buscar(int id) throws SQLException {
    try (Connection c = Conexion.abrir();
        PreparedStatement p = c.prepareStatement("SELECT * FROM plan WHERE id_plan=?")) {
      p.setInt(1, id);
      try (ResultSet r = p.executeQuery()) {
        if (r.next()) return leer(r);
      }
    }
    throw new IllegalArgumentException("Plan no encontrado.");
  }

  public void guardar(Plan x) throws SQLException {
    String sql =
        x.getId() == 0
            ? "INSERT INTO plan(nombre,duracion_dias,precio,descripcion,estado) VALUES(?,?,?,?,?)"
            : "UPDATE plan SET nombre=?,duracion_dias=?,precio=?,descripcion=?,estado=? WHERE"
                  + " id_plan=?";
    try (Connection c = Conexion.abrir();
        PreparedStatement p = c.prepareStatement(sql)) {
      p.setString(1, x.getNombre());
      p.setInt(2, x.getDuracionDias());
      p.setBigDecimal(3, x.getPrecio());
      p.setString(4, x.getDescripcion());
      p.setString(5, x.getEstado());
      if (x.getId() > 0) p.setInt(6, x.getId());
      p.executeUpdate();
    }
  }

  public void desactivar(int id) throws SQLException {
    try (Connection c = Conexion.abrir();
        PreparedStatement p =
            c.prepareStatement("UPDATE plan SET estado='INACTIVO' WHERE id_plan=?")) {
      p.setInt(1, id);
      p.executeUpdate();
    }
  }

  private Plan leer(ResultSet r) throws SQLException {
    Plan x = new Plan();
    x.setId(r.getInt("id_plan"));
    x.setNombre(r.getString("nombre"));
    x.setDuracionDias(r.getInt("duracion_dias"));
    x.setPrecio(r.getBigDecimal("precio"));
    x.setDescripcion(r.getString("descripcion"));
    x.setEstado(r.getString("estado"));
    return x;
  }
}
