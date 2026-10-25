package pe.edu.gimnasio.dao;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
import pe.edu.gimnasio.config.Conexion;

public class AdministradorDAO {
  public String autenticar(String correo, String clave) throws SQLException {
    try (Connection c = Conexion.abrir();
        PreparedStatement p =
            c.prepareStatement(
                "SELECT nombres,password_hash FROM administrador WHERE correo=? AND"
                    + " estado='ACTIVO'")) {
      p.setString(1, correo);
      try (ResultSet r = p.executeQuery()) {
        if (r.next() && BCrypt.checkpw(clave, r.getString("password_hash")))
          return r.getString("nombres");
      }
    }
    return null;
  }
}
