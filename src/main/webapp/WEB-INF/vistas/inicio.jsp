<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="cabecera.jsp" %>
<h1>Panel de control</h1><p>Resumen de la actividad del gimnasio · <%=LocalDate.now(ZoneId.of("America/Lima"))%></p>
<section class="tarjetas"><%Map<String,Object> indicadores=(Map<String,Object>)request.getAttribute("indicadores");if(indicadores!=null)for(Map.Entry<String,Object> dato:indicadores.entrySet()){%><article class="tarjeta"><span><%=Web.html(dato.getKey())%></span><strong><%=Web.html(dato.getValue())%></strong></article><%}%></section>
<section class="panel"><h2>Operaciones frecuentes</h2><div class="acciones"><a class="boton" href="<%=base%>/clientes">Registrar cliente</a><a class="boton" href="<%=base%>/membresias">Asignar membresía</a><a class="boton" href="<%=base%>/asistencias">Registrar ingreso</a></div><p>Registre al cliente, asigne un plan y confirme el pago completo antes de registrar su ingreso.</p><p>La vigencia se calcula por fechas. El pago habilita el acceso. Una renovación se registra como una nueva membresía.</p></section>
<%@ include file="pie.jsp" %>
