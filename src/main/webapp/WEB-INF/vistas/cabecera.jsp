<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,java.time.*,pe.edu.gimnasio.modelo.*,pe.edu.gimnasio.util.Web" %>
<% String base=request.getContextPath(); %>
<!DOCTYPE html><html lang="es"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Gestión de Gimnasio</title><link rel="stylesheet" href="<%=base%>/css/estilos.css"></head><body>
<header><div class="marca">GESTIÓN DE GIMNASIO</div><div class="sesion"><span><%=Web.html(session.getAttribute("administrador"))%></span><form method="post" action="<%=base%>/salir"><input type="hidden" name="token" value="<%=session.getAttribute("token")%>"><button class="secundario">Cerrar sesión</button></form></div></header>
<nav aria-label="Menú principal"><% String[] rutas={"inicio","clientes","planes","membresias","pagos","asistencias","reportes"};String[] titulos={"Inicio","Clientes","Planes","Membresías","Pagos","Asistencias","Reportes"};for(int i=0;i<rutas.length;i++){%><a class="<%=request.getServletPath().contains(rutas[i])?"actual":""%>" href="<%=base%>/<%=rutas[i]%>"><%=titulos[i]%></a><%}%></nav><main>
<% if(request.getAttribute("error")!=null){%><div class="aviso error" role="alert"><%=Web.html(request.getAttribute("error"))%></div><%}%>
<% if(session.getAttribute("mensaje")!=null){%><div class="aviso exito" role="status"><%=Web.html(session.getAttribute("mensaje"))%></div><%session.removeAttribute("mensaje");}%>
