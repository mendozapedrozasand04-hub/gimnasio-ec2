package pe.edu.gimnasio;

import java.io.File;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.scan.StandardJarScanner;

public class IniciarGimnasio {
  public static void main(String[] args) throws Exception {
    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("America/Lima"));
    Tomcat tomcat = new Tomcat();
    tomcat.setBaseDir("target/tomcat");
    tomcat.setHostname("localhost");
    tomcat.setPort(Integer.getInteger("puerto", 8081));
    tomcat.getConnector().setProperty("address", "127.0.0.1");
    Context contexto = tomcat.addWebapp("", new File("src/main/webapp").getAbsolutePath());
    contexto.setParentClassLoader(IniciarGimnasio.class.getClassLoader());
    ((StandardJarScanner) contexto.getJarScanner()).setScanClassPath(false);
    StandardRoot recursos = new StandardRoot(contexto);
    recursos.addPreResources(
        new DirResourceSet(
            recursos, "/WEB-INF/classes", new File("target/classes").getAbsolutePath(), "/"));
    contexto.setResources(recursos);
    tomcat.start();
    System.out.println(
        "Gimnasio disponible en http://localhost:" + tomcat.getConnector().getPort());
    tomcat.getServer().await();
  }
}
