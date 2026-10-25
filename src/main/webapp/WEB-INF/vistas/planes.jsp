<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="cabecera.jsp" %><%! private String campo(jakarta.servlet.http.HttpServletRequest r,String nombre,Object valor){return Web.html(Boolean.TRUE.equals(r.getAttribute("reintento"))?r.getParameter(nombre):valor);} %>
<%Plan x=(Plan)request.getAttribute("plan");if(x==null)x=new Plan();List<Plan> planes=(List<Plan>)request.getAttribute("planes");%>
<h1>Planes</h1><p>Catálogo de planes y precios del gimnasio.</p>
<section class="panel"><h2><%=x.getId()>0?"Editar plan":"Nuevo plan"%></h2><form method="post" class="rejilla"><input type="hidden" name="token" value="<%=session.getAttribute("token")%>"><input type="hidden" name="id" value="<%=campo(request,"id",x.getId()==0?"":x.getId())%>">
<label>Nombre<input name="nombre" maxlength="80" required value="<%=campo(request,"nombre",x.getNombre())%>"></label>
<label>Duración en días<input type="number" name="duracion_dias" min="1" max="3650" required value="<%=campo(request,"duracion_dias",x.getDuracionDias()==0?"":x.getDuracionDias())%>"></label>
<label>Precio (S/)<input type="number" name="precio" step="0.01" min="0.01" max="99999999.99" required value="<%=campo(request,"precio",x.getPrecio())%>"></label>
<label>Descripción<input name="descripcion" maxlength="250" value="<%=campo(request,"descripcion",x.getDescripcion())%>"></label>
<label>Estado<select name="estado"><%for(String e:List.of("ACTIVO","INACTIVO")){%><option <%=e.equals(Boolean.TRUE.equals(request.getAttribute("reintento"))?request.getParameter("estado"):x.getEstado())?"selected":""%>><%=e%></option><%}%></select></label>
<div class="acciones"><button>Guardar plan</button><a href="<%=base%>/planes">Limpiar</a></div></form></section>
<section class="panel"><h2>Planes registrados</h2><div class="tabla"><table><thead><tr><th>Plan</th><th>Días</th><th>Precio</th><th>Descripción</th><th>Estado</th><th>Acciones</th></tr></thead><tbody>
<%if(planes!=null)for(Plan p:planes){%><tr><td><%=Web.html(p.getNombre())%></td><td><%=p.getDuracionDias()%></td><td>S/ <%=p.getPrecio()%></td><td><%=Web.html(p.getDescripcion())%></td><td><%=p.getEstado()%></td><td class="acciones"><a href="<%=base%>/planes?accion=editar&id=<%=p.getId()%>">Editar</a><%if("ACTIVO".equals(p.getEstado())){%><form method="post" data-confirmar="¿Desactivar este plan?"><input type="hidden" name="token" value="<%=session.getAttribute("token")%>"><input type="hidden" name="id" value="<%=p.getId()%>"><button name="accion" value="desactivar" class="peligro">Desactivar</button></form><%}%></td></tr><%}%>
<%if(planes==null||planes.isEmpty()){%><tr><td colspan="6">No hay planes registrados.</td></tr><%}%></tbody></table></div></section><%@ include file="pie.jsp" %>
