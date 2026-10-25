package pe.edu.gimnasio.modelo;

public class Plan {
  private int id;
  private String nombre;
  private int duracionDias;
  private java.math.BigDecimal precio;
  private String descripcion;
  private String estado;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public int getDuracionDias() {
    return duracionDias;
  }

  public void setDuracionDias(int duracionDias) {
    this.duracionDias = duracionDias;
  }

  public java.math.BigDecimal getPrecio() {
    return precio;
  }

  public void setPrecio(java.math.BigDecimal precio) {
    this.precio = precio;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }
}
