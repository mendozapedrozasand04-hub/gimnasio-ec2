package pe.edu.gimnasio;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDate;
import org.junit.jupiter.api.*;
import pe.edu.gimnasio.config.Conexion;
import pe.edu.gimnasio.dao.*;
import pe.edu.gimnasio.modelo.*;

public class FlujoGimnasioTest {
  @BeforeEach
  void preparar() throws Exception {
    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("America/Lima"));
    System.setProperty("db.driver", "org.h2.Driver");
    System.setProperty(
        "db.url", "jdbc:h2:mem:gym;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
    System.setProperty("db.usuario", "sa");
    System.setProperty("db.clave", "");
    try (Connection c = Conexion.abrir();
        Statement s = c.createStatement()) {
      s.execute("DROP ALL OBJECTS");
      String sql = Files.readString(Path.of("base_datos/01_crear_base.sql"));
      sql = sql.replaceAll("(?m)^CREATE DATABASE[^;]+;", "").replace("USE gimnasio_db;", "");
      for (String parte : sql.split(";")) if (!parte.isBlank()) s.execute(parte);
      String semilla = Files.readString(Path.of("base_datos/02_datos_ejemplo.sql"));
      int inicio = semilla.indexOf("INSERT INTO administrador");
      s.execute(semilla.substring(inicio, semilla.indexOf(';', inicio)));
    }
    Cliente x = new Cliente();
    x.setTipoDocumento("DNI");
    x.setDocumento("12345678");
    x.setNombres("Ana");
    x.setApellidos("Torres");
    x.setTelefono("987654321");
    x.setCorreo("");
    x.setEstado("ACTIVO");
    new ClienteDAO().guardar(x);
    Plan p = new Plan();
    p.setNombre("Mensual");
    p.setDuracionDias(30);
    p.setPrecio(new BigDecimal("90.00"));
    p.setDescripcion("30 dias");
    p.setEstado("ACTIVO");
    new PlanDAO().guardar(p);
  }

  @Test
  void pagoCompletoHabilitaIngresoYConservaHistorial() throws Exception {
    MembresiaDAO m = new MembresiaDAO();
    m.registrar(1, 1, LocalDate.now());
    assertThrows(IllegalArgumentException.class, () -> new AsistenciaDAO().registrar(1));
    assertThrows(
        IllegalArgumentException.class,
        () -> new PagoDAO().registrar(1, new BigDecimal("45.00"), "Efectivo"));
    new PagoDAO().registrar(1, new BigDecimal("90.00"), "Efectivo");
    assertThrows(
        SQLException.class, () -> new PagoDAO().registrar(1, new BigDecimal("90.00"), "Efectivo"));
    new AsistenciaDAO().registrar(1);
    assertThrows(SQLException.class, () -> new AsistenciaDAO().registrar(1));
    assertEquals(1, new AsistenciaDAO().listar(LocalDate.now().toString(), "").size());
    m.cancelar(1);
    assertThrows(IllegalArgumentException.class, () -> new AsistenciaDAO().registrar(1));
    assertEquals(
        1,
        new PagoDAO()
            .listar("", LocalDate.now().toString(), LocalDate.now().toString(), "")
            .size());
  }

  @Test
  void superposicionRenovacionYPrecioHistorico() throws Exception {
    MembresiaDAO m = new MembresiaDAO();
    LocalDate hoy = LocalDate.now();
    m.registrar(1, 1, hoy);
    assertThrows(IllegalArgumentException.class, () -> m.registrar(1, 1, hoy.plusDays(29)));
    m.registrar(1, 1, hoy.plusDays(30));
    assertEquals(2, m.listar("").size());
    Plan p = new PlanDAO().buscar(1);
    p.setPrecio(new BigDecimal("120.00"));
    new PlanDAO().guardar(p);
    assertEquals(new BigDecimal("90.00"), m.listar("").get(0).getPrecioPactado());
    assertEquals(hoy.plusDays(29), m.listar("").get(1).getFechaFin());
  }

  @Test
  void bajasLogicasAutenticacionYFiltros() throws Exception {
    assertEquals(
        "Administrador", new AdministradorDAO().autenticar("admin@gimnasio.com", "Admin123!"));
    assertNull(new AdministradorDAO().autenticar("admin@gimnasio.com", "incorrecta"));
    new ClienteDAO().desactivar(1);
    assertEquals(1, new ClienteDAO().listar("Torres", "INACTIVO").size());
    assertThrows(
        IllegalArgumentException.class, () -> new MembresiaDAO().registrar(1, 1, LocalDate.now()));
    Cliente x = new ClienteDAO().buscar(1);
    x.setEstado("ACTIVO");
    new ClienteDAO().guardar(x);
    new PlanDAO().desactivar(1);
    assertThrows(
        IllegalArgumentException.class, () -> new MembresiaDAO().registrar(1, 1, LocalDate.now()));
  }
}
