package pe.edu.gimnasio.config;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class Conexion {
  public static Connection abrir() throws SQLException {
    Properties p = new Properties();
    try (InputStream archivo = Conexion.class.getResourceAsStream("/db.properties")) {
      if (archivo == null) throw new Exception("No se encontro db.properties");
      p.load(archivo);
      Class.forName(System.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
    } catch (Exception e) {
      throw new SQLException("No se pudo leer la configuracion de la base de datos", e);
    }
    return DriverManager.getConnection(
        System.getProperty("db.url", p.getProperty("url")),
        System.getProperty("db.usuario", p.getProperty("usuario")),
        System.getProperty("db.clave", p.getProperty("clave")));
  }
}
