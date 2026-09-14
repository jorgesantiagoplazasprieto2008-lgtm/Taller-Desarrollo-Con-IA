<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Catálogo Artesanal - Entre Copas" scope="request" />
<jsp:include page="common/header.jsp" />

<div class="catalog-header">
    <h1>Catálogo de Bebidas Artesanales</h1>
    <p>Descubre creaciones nobles con trazabilidad analítica certificada de origen.</p>
</div>

<!-- Barra de Filtros y Búsqueda -->
<div class="search-bar-container">
    <div class="filter-pills">
        <a href="${pageContext.request.contextPath}/catalogo" class="filter-pill ${empty tipoActual ? 'active' : ''}">
            Todos
        </a>
        <a href="${pageContext.request.contextPath}/catalogo?tipo=Vino" class="filter-pill ${tipoActual == 'Vino' ? 'active' : ''}">
            Vinos
        </a>
        <a href="${pageContext.request.contextPath}/catalogo?tipo=Cerveza" class="filter-pill ${tipoActual == 'Cerveza' ? 'active' : ''}">
            Cervezas
        </a>
        <a href="${pageContext.request.contextPath}/catalogo?tipo=Hidromiel" class="filter-pill ${tipoActual == 'Hidromiel' ? 'active' : ''}">
            Hidromiel
        </a>
    </div>

    <form method="get" action="${pageContext.request.contextPath}/catalogo" class="search-input-group">
        <c:if test="${not empty tipoActual}">
            <input type="hidden" name="tipo" value="${tipoActual}">
        </c:if>
        <input type="text" name="q" value="${busquedaActual}" placeholder="Buscar por nombre, nota o estilo..." class="search-input">
        <button type="submit" class="btn-noble">Buscar</button>
    </form>
</div>

<!-- Grilla de Productos -->
<c:choose>
    <c:when test="${not empty productos}">
        <div class="product-grid">
            <c:forEach var="prod" items="${productos}">
                <article class="product-card">
                    <div>
                        <span class="product-badge-type">${prod.tipoBebida} · ${prod.presentacion}</span>
                        <h2 class="product-name">${prod.nombre}</h2>
                        <p class="product-origin">
                            📍 <strong>${prod.productorNombre}</strong> (${prod.productorUbicacion})
                        </p>
                        <p class="product-desc">${prod.descripcion}</p>
                    </div>
                    <div class="product-card-footer">
                        <span class="product-price">
                            $<fmt:formatNumber value="${prod.precio}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                        </span>
                        <a href="${pageContext.request.contextPath}/productos/${prod.id}" class="btn-outline">
                            Ver Ficha
                        </a>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        <div class="empty-state">
            <h3>No se encontraron bebidas disponibles</h3>
            <p>No hay productos activos que coincidan con los criterios seleccionados.</p>
            <a href="${pageContext.request.contextPath}/catalogo" class="btn-noble">Ver Todo el Catálogo</a>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="common/footer.jsp" />
