<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="${producto.nombre} - Entre Copas" scope="request" />
<jsp:include page="common/header.jsp" />

<div style="margin-bottom: 1.5rem;">
    <a href="${pageContext.request.contextPath}/catalogo" class="btn-outline">
        ← Volver al Catálogo
    </a>
</div>

<div class="form-card" style="max-width: 800px; padding: 2.5rem;">
    <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1rem;">
        <div>
            <span class="product-badge-type">${producto.tipoBebida} · ${producto.presentacion}</span>
            <h1 style="font-size: 2.2rem; margin-top: 0.25rem;">${producto.nombre}</h1>
        </div>
        <span class="badge-status badge-activo">Disponible</span>
    </div>

    <div style="background-color: var(--color-bg-parchment); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 2rem;">
        <h4 style="margin-bottom: 0.25rem;">Origen y Productor Artesanal</h4>
        <p style="font-size: 1.05rem; font-weight: 600; color: var(--color-primary);">${producto.productorNombre}</p>
        <p style="color: var(--color-text-muted); font-size: 0.95rem;">📍 ${producto.productorUbicacion}</p>
    </div>

    <div style="margin-bottom: 2rem;">
        <h3 style="font-size: 1.25rem; margin-bottom: 0.75rem;">Notas de Cata y Descripción</h3>
        <p style="font-size: 1.05rem; line-height: 1.8; color: var(--color-text-main);">
            ${producto.descripcion}
        </p>
    </div>

    <!-- Sección de Disponibilidad en Hostelería Local -->
    <div style="margin-bottom: 2rem; border-top: 1px solid var(--color-border); padding-top: 1.5rem;">
        <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1rem;">
            <h3 style="font-size: 1.25rem; margin: 0; display: flex; align-items: center; gap: 0.5rem;">
                <span>🍽️</span> Dónde Degustar (Locales con Stock Activo)
            </h3>
            <span class="product-badge-type" style="font-size: 0.8rem;">
                ${localesDisponibles != null ? localesDisponibles.size() : 0} locales disponibles
            </span>
        </div>

        <c:choose>
            <c:when test="${not empty localesDisponibles}">
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 1rem;">
                    <c:forEach var="local" items="${localesDisponibles}">
                        <div style="background-color: var(--color-bg-parchment); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 1rem; position: relative;">
                            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.35rem;">
                                <strong style="font-size: 1.05rem; color: var(--color-primary);">${local.nombre}</strong>
                                <span class="badge-status badge-activo" style="font-size: 0.75rem; padding: 0.2rem 0.5rem;">En Barra</span>
                            </div>
                            <p style="margin: 0; font-size: 0.9rem; color: var(--color-text-main);">📍 ${local.direccion}</p>
                            <span style="display: inline-block; font-size: 0.8rem; color: var(--color-text-muted); margin-top: 0.25rem;">
                                Ciudad: ${local.ciudad}
                            </span>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div style="background-color: #fcf8f3; border: 1px dashed var(--color-border); border-radius: var(--radius-md); padding: 1.5rem; text-align: center;">
                    <p style="margin: 0; color: var(--color-text-muted); font-size: 0.95rem;">
                        Actualmente ningún bar o restaurante asociado ha marcado stock activo de este producto en barra.
                    </p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--color-border); padding-top: 1.5rem;">
        <div>
            <span style="display: block; font-size: 0.85rem; color: var(--color-text-muted);">Precio sugerido</span>
            <span style="font-size: 2rem; font-weight: 700; color: var(--color-primary);">
                $<fmt:formatNumber value="${producto.precio}" type="number" minFractionDigits="2" maxFractionDigits="2" />
            </span>
        </div>
        <a href="${pageContext.request.contextPath}/catalogo" class="btn-noble">
            Explorar más de ${producto.productorNombre}
        </a>
    </div>
</div>

<jsp:include page="common/footer.jsp" />
