package pe.edu.gimnasio;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.junit.jupiter.api.Test;

public class PantallasUtf8Test {
  @Test
  void todasLasPantallasConservanTildes() throws Exception {
    new FlujoGimnasioTest().preparar();
    Tomcat servidor = new Tomcat();
    servidor.setBaseDir("target/tomcat-pruebas");
    servidor.setPort(0);
    servidor.getConnector().setProperty("address", "127.0.0.1");
    Context contexto = servidor.addWebapp("", new File("src/main/webapp").getAbsolutePath());
    contexto.setParentClassLoader(getClass().getClassLoader());
    ((StandardJarScanner) contexto.getJarScanner()).setScanClassPath(false);
    StandardRoot recursos = new StandardRoot(contexto);
    recursos.addPreResources(new DirResourceSet(recursos, "/WEB-INF/classes",
        new File("target/classes").getAbsolutePath(), "/"));
    contexto.setResources(recursos);
    try {
      servidor.start();
      String base = "http://127.0.0.1:" + servidor.getConnector().getLocalPort();
      CookieManager cookies = new CookieManager();
      HttpClient cliente = HttpClient.newBuilder().cookieHandler(cookies)
          .followRedirects(HttpClient.Redirect.NORMAL).build();
      HttpResponse<String> acceso = cliente.send(HttpRequest.newBuilder(URI.create(base + "/login"))
          .GET().build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      var token = Pattern.compile("name=\"token\" value=\"([^\"]+)\"").matcher(acceso.body());
      assertTrue(token.find());
      String datos = "correo=admin%40gimnasio.com&clave=Admin123%21&token=" + token.group(1);
      var login = cliente.send(HttpRequest.newBuilder(URI.create(base + "/login"))
          .header("Content-Type", "application/x-www-form-urlencoded")
          .POST(HttpRequest.BodyPublishers.ofString(datos)).build(),
          HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      assertEquals(200, login.statusCode());
      assertTrue(login.body().contains("Panel de control"));
      Map<String,String> pantallas = Map.of(
          "inicio", "Asignar membresía",
          "clientes", "Teléfono",
          "planes", "Duración en días",
          "membresias", "Historial de membresías",
          "pagos", "Método de pago",
          "asistencias", "automáticamente",
          "reportes", "Planes más contratados");
      for (var pantalla : pantallas.entrySet()) {
        var respuesta = cliente.send(HttpRequest.newBuilder(URI.create(base + "/" + pantalla.getKey()))
            .GET().build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, respuesta.statusCode(), pantalla.getKey());
        String html = respuesta.body();
        assertTrue(html.contains(pantalla.getValue()), pantalla.getKey() + ": " + pantalla.getValue());
        assertTrue(html.contains("Gestión administrativa de clientes, membresías y asistencias"), pantalla.getKey() + ": pie");
        assertFalse(html.contains("Ã"), pantalla.getKey() + ": texto mal codificado");
        assertFalse(html.contains("Â"), pantalla.getKey() + ": texto mal codificado");
        assertFalse(html.contains("�"), pantalla.getKey() + ": caracter de reemplazo");
      }
    } finally {
      servidor.stop();
      servidor.destroy();
    }
  }
}
