<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Gestión de Productos - Panel del Productor" scope="request" />
<jsp:include page="../common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h1>Gestión de Productos Artesanales</h1>
        <p style="color: var(--color-text-muted);">Administra tu catálogo, presentaciones y disponibilidad comercial.</p>
    </div>
    <a href="${pageContext.request.contextPath}/panel/productor/productos/nuevo" class="btn-noble">
        + Registrar Nuevo Producto
    </a>
</div>

<c:choose>
    <c:when test="${not empty productos}">
        <div class="admin-table-container">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Producto</th>
                        <th>Tipo / Formato</th>
                        <th>Precio</th>
                        <th>Estado Actual</th>
                        <th>Acciones de Estado</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="prod" items="${productos}">
                        <tr>
                            <td>
                                <strong>${prod.nombre}</strong><br>
                                <small style="color: var(--color-text-muted);">${prod.descripcion}</small>
                            </td>
                            <td>
                                <span class="product-badge-type">${prod.tipoBebida}</span><br>
                                <small>${prod.presentacion}</small>
                            </td>
                            <td>
                                <strong>$<fmt:formatNumber value="${prod.precio}" type="number" minFractionDigits="2" maxFractionDigits="2" /></strong>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${prod.estado == 'ACTIVO'}">
                                        <span class="badge-status badge-activo">● ACTIVO</span>
                                    </c:when>
                                    <c:when test="${prod.estado == 'PAUSADO'}">
                                        <span class="badge-status badge-pausado">❚❚ PAUSADO</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge-status badge-retirado">✖ RETIRADO</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/panel/productor/productos/${prod.id}/estado" style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                                    <c:if test="${prod.estado != 'ACTIVO'}">
                                        <button type="submit" name="nuevoEstado" value="ACTIVO" class="btn-outline" style="padding: 0.4rem 0.8rem; font-size: 0.85rem; color: var(--color-active); border-color: var(--color-active);">
                                            Activar
                                        </button>
                                    </c:if>
                                    <c:if test="${prod.estado == 'ACTIVO'}">
                                        <button type="submit" name="nuevoEstado" value="PAUSADO" class="btn-outline" style="padding: 0.4rem 0.8rem; font-size: 0.85rem; color: var(--color-paused); border-color: var(--color-paused);">
                                            Pausar
                                        </button>
                                    </c:if>
                                    <c:if test="${prod.estado != 'RETIRADO'}">
                                        <button type="submit" name="nuevoEstado" value="RETIRADO" class="btn-outline" style="padding: 0.4rem 0.8rem; font-size: 0.85rem; color: var(--color-retired); border-color: var(--color-retired);">
                                            Retirar
                                        </button>
                                    </c:if>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:when>
    <c:otherwise>
        <div class="empty-state">
            <h3>No tienes productos registrados</h3>
            <p>Comienza a comercializar tus bebidas artesanales registrando tu primer lote y presentación.</p>
            <a href="${pageContext.request.contextPath}/panel/productor/productos/nuevo" class="btn-noble">
                + Crear Primer Producto
            </a>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="../common/footer.jsp" />
