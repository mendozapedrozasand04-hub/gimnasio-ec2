package pe.edu.gimnasio.modelo;

public class Membresia {
  private int id;
  private int idCliente;
  private int idPlan;
  private String cliente;
  private String plan;
  private java.time.LocalDate fechaInicio;
  private java.time.LocalDate fechaFin;
  private java.math.BigDecimal precioPactado;
  private String estado;
  private boolean pagada;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getIdCliente() {
    return idCliente;
  }

  public void setIdCliente(int idCliente) {
    this.idCliente = idCliente;
  }

  public int getIdPlan() {
    return idPlan;
  }

  public void setIdPlan(int idPlan) {
    this.idPlan = idPlan;
  }

  public String getCliente() {
    return cliente;
  }

  public void setCliente(String cliente) {
    this.cliente = cliente;
  }

  public String getPlan() {
    return plan;
  }

  public void setPlan(String plan) {
    this.plan = plan;
  }

  public java.time.LocalDate getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(java.time.LocalDate fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public java.time.LocalDate getFechaFin() {
    return fechaFin;
  }

  public void setFechaFin(java.time.LocalDate fechaFin) {
    this.fechaFin = fechaFin;
  }

  public java.math.BigDecimal getPrecioPactado() {
    return precioPactado;
  }

  public void setPrecioPactado(java.math.BigDecimal precioPactado) {
    this.precioPactado = precioPactado;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public boolean getPagada() {
    return pagada;
  }

  public void setPagada(boolean pagada) {
    this.pagada = pagada;
  }

  public String getVigencia() {
    java.time.LocalDate hoy = java.time.LocalDate.now(java.time.ZoneId.of("America/Lima"));
    if ("CANCELADA".equals(estado)) return "Cancelada";
    if (hoy.isBefore(fechaInicio)) return "Programada";
    if (hoy.isAfter(fechaFin)) return "Vencida";
    if (!pagada) return "Pendiente de pago";
    if (!fechaFin.isAfter(hoy.plusDays(7))) return "Proxima a vencer";
    return "Vigente";
  }
}
