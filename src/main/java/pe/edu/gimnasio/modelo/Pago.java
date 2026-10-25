package pe.edu.gimnasio.modelo;

public class Pago {
  private int id;
  private int idMembresia;
  private String cliente;
  private java.math.BigDecimal monto;
  private String metodo;
  private String fecha;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getIdMembresia() {
    return idMembresia;
  }

  public void setIdMembresia(int idMembresia) {
    this.idMembresia = idMembresia;
  }

  public String getCliente() {
    return cliente;
  }

  public void setCliente(String cliente) {
    this.cliente = cliente;
  }

  public java.math.BigDecimal getMonto() {
    return monto;
  }

  public void setMonto(java.math.BigDecimal monto) {
    this.monto = monto;
  }

  public String getMetodo() {
    return metodo;
  }

  public void setMetodo(String metodo) {
    this.metodo = metodo;
  }

  public String getFecha() {
    return fecha;
  }

  public void setFecha(String fecha) {
    this.fecha = fecha;
  }
}
