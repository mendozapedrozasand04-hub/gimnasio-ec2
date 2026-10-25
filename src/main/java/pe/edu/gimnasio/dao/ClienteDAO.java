package pe.edu.gimnasio.dao;

import java.sql.*;
import java.util.*;
import pe.edu.gimnasio.config.Conexion;
import pe.edu.gimnasio.modelo.Cliente;

public class ClienteDAO {
  public List<Cliente> listar(String busqueda, String estado) throws SQLException {
    List<Cliente> lista = new ArrayList<>();
    String sql =
        "SELECT * FROM cliente WHERE CONCAT(nombres,' ',apellidos,' ',documento) LIKE ? AND (?=''"
            + " OR estado=?) ORDER BY id_cliente DESC";
    try (Connection c = Conexion.abrir();
        PreparedStatement p = c.prepareStatement(sql)) {
      p.setString(1, "%" + busqueda + "%");
      p.setString(2, estado);
      p.setString(3, estado);
      try (ResultSet r = p.executeQuery()) {
        while (r.next()) lista.add(leer(r));
      }
    }
    return lista;
  }

  public Cliente buscar(int id) throws SQLException {
    try (Connection c = Conexion.abrir();
        PreparedStatement p = c.prepareStatement("SELECT * FROM cliente WHERE id_cliente=?")) {
      p.setInt(1, id);
      try (ResultSet r = p.executeQuery()) {
        if (r.next()) return leer(r);
      }
    }
    throw new IllegalArgumentException("Cliente no encontrado.");
  }

  public void guardar(Cliente x) throws SQLException {
    String sql =
        x.getId() == 0
            ? "INSERT INTO"
                  + " cliente(tipo_documento,documento,nombres,apellidos,telefono,correo,estado)"
                  + " VALUES(?,?,?,?,?,?,?)"
            : "UPDATE cliente SET"
                  + " tipo_documento=?,documento=?,nombres=?,apellidos=?,telefono=?,correo=?,estado=?"
                  + " WHERE id_cliente=?";
    try (Connection c = Conexion.abrir();
        PreparedStatement p = c.prepareStatement(sql)) {
      p.setString(1, x.getTipoDocumento());
      p.setString(2, x.getDocumento());
      p.setString(3, x.getNombres());
      p.setString(4, x.getApellidos());
      p.setString(5, x.getTelefono());
      p.setString(6, x.getCorreo());
      p.setString(7, x.getEstado());
      if (x.getId() > 0) p.setInt(8, x.getId());
      p.executeUpdate();
    }
  }

  public void desactivar(int id) throws SQLException {
    try (Connection c = Conexion.abrir();
        PreparedStatement p =
            c.prepareStatement("UPDATE cliente SET estado='INACTIVO' WHERE id_cliente=?")) {
      p.setInt(1, id);
      p.executeUpdate();
    }
  }

  private Cliente leer(ResultSet r) throws SQLException {
    Cliente x = new Cliente();
    x.setId(r.getInt("id_cliente"));
    x.setTipoDocumento(r.getString("tipo_documento"));
    x.setDocumento(r.getString("documento"));
    x.setNombres(r.getString("nombres"));
    x.setApellidos(r.getString("apellidos"));
    x.setTelefono(r.getString("telefono"));
    x.setCorreo(r.getString("correo"));
    x.setEstado(r.getString("estado"));
    return x;
  }
}
