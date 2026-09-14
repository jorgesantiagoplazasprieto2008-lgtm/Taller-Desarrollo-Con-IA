<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Certificado de Trazabilidad - Entre Copas" scope="request" />
<jsp:include page="common/header.jsp" />

<div class="catalog-header" style="margin-bottom: 2rem;">
    <h1>Trazabilidad Analítica Certificada</h1>
    <p>Verificación transparente de origen, parámetros de laboratorio y proceso artesanal.</p>
</div>

<!-- Buscador de Código -->
<div class="search-bar-container" style="max-width: 680px; margin: 0 auto 2.5rem;">
    <form method="get" action="${pageContext.request.contextPath}/trazabilidad" style="display: flex; gap: 0.75rem; width: 100%;">
        <input type="text" name="codigo" value="${codigoBuscado}" placeholder="Ingresa el código único (Ej. EC-2026-PR01-A101)" class="search-input" required>
        <button type="submit" class="btn-noble">Verificar</button>
    </form>
</div>

<c:choose>
    <c:when test="${not empty trazabilidad}">
        <!-- Certificado Oficial -->
        <div class="form-card" style="max-width: 850px; padding: 2.5rem; border: 2px solid var(--color-border); box-shadow: var(--shadow-hover); position: relative;">
            
            <!-- Sello Oficial -->
            <div style="display: flex; justify-content: space-between; align-items: flex-start; border-bottom: 1px solid var(--color-border); padding-bottom: 1.5rem; margin-bottom: 2rem;">
                <div>
                    <span class="product-badge-type">Certificación Oficial de Trazabilidad</span>
                    <h2 style="font-size: 1.8rem; margin: 0.25rem 0;">${trazabilidad.productoNombre}</h2>
                    <p style="color: var(--color-text-muted);">${trazabilidad.tipoBebida} · ${trazabilidad.presentacion}</p>
                </div>
                <div style="text-align: right;">
                    <div style="background-color: var(--color-primary-light); color: var(--color-primary); padding: 0.5rem 1rem; border-radius: var(--radius-md); font-weight: 700; font-family: monospace; font-size: 1.1rem;">
                        ${trazabilidad.codigoTrazabilidad}
                    </div>
                    <span style="font-size: 0.8rem; color: var(--color-active); font-weight: 600; display: block; margin-top: 0.25rem;">
                        ✔ Verificado ISO 22005
                    </span>
                </div>
            </div>

            <!-- Grilla Analítica Físico-Química (4 Métricas) -->
            <div style="margin-bottom: 2.5rem;">
                <h3 style="font-size: 1.2rem; margin-bottom: 1rem; color: var(--color-primary);">
                    🔬 Parámetros Analíticos de Laboratorio
                </h3>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 1rem;">
                    <!-- Tarjeta pH -->
                    <div style="background-color: var(--color-bg-parchment); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 1.25rem; text-align: center;">
                        <span style="display: block; font-size: 0.85rem; color: var(--color-text-muted); text-transform: uppercase;">pH (Acidez Iónica)</span>
                        <strong style="font-size: 1.8rem; color: var(--color-primary); font-family: var(--font-heading);">
                            ${trazabilidad.parametrosAnaliticos.ph}
                        </strong>
                        <span style="display: block; font-size: 0.75rem; color: var(--color-text-muted); margin-top: 0.25rem;">Rango óptimo artesanal</span>
                    </div>

                    <!-- Tarjeta Alcohol ABV -->
                    <div style="background-color: var(--color-bg-parchment); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 1.25rem; text-align: center;">
                        <span style="display: block; font-size: 0.85rem; color: var(--color-text-muted); text-transform: uppercase;">Graduación Real</span>
                        <strong style="font-size: 1.8rem; color: var(--color-secondary); font-family: var(--font-heading);">
                            ${trazabilidad.parametrosAnaliticos.graduacionAlcoholica}%
                        </strong>
                        <span style="display: block; font-size: 0.75rem; color: var(--color-text-muted); margin-top: 0.25rem;">Volumen de alcohol (% ABV)</span>
                    </div>

                    <!-- Tarjeta Acidez Total -->
                    <div style="background-color: var(--color-bg-parchment); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 1.25rem; text-align: center;">
                        <span style="display: block; font-size: 0.85rem; color: var(--color-text-muted); text-transform: uppercase;">Acidez Total</span>
                        <strong style="font-size: 1.8rem; color: var(--color-primary); font-family: var(--font-heading);">
                            ${trazabilidad.parametrosAnaliticos.acidezTotalGl} g/L
                        </strong>
                        <span style="display: block; font-size: 0.75rem; color: var(--color-text-muted); margin-top: 0.25rem;">Equivalente en ácido tartárico</span>
                    </div>

                    <!-- Tarjeta Densidad -->
                    <div style="background-color: var(--color-bg-parchment); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 1.25rem; text-align: center;">
                        <span style="display: block; font-size: 0.85rem; color: var(--color-text-muted); text-transform: uppercase;">Densidad Final</span>
                        <strong style="font-size: 1.8rem; color: var(--color-primary); font-family: var(--font-heading);">
                            ${trazabilidad.parametrosAnaliticos.densidad}
                        </strong>
                        <span style="display: block; font-size: 0.75rem; color: var(--color-text-muted); margin-top: 0.25rem;">g/cm³ a 20°C</span>
                    </div>
                </div>
            </div>

            <!-- Línea de Tiempo y Origen -->
            <div style="background-color: var(--color-bg-parchment); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 1.5rem; margin-bottom: 2rem;">
                <h3 style="font-size: 1.15rem; margin-bottom: 0.75rem; color: var(--color-primary);">
                    📍 Origen y Maestro Productor
                </h3>
                <p style="font-size: 1.05rem; font-weight: 600;">${trazabilidad.productorNombre}</p>
                <p style="color: var(--color-text-muted); font-size: 0.95rem; margin-bottom: 0.5rem;">
                    Ubicación del Terroir: ${trazabilidad.productorUbicacion}
                </p>
                <c:if test="${not empty trazabilidad.registroSanitario}">
                    <p style="color: var(--color-text-muted); font-size: 0.85rem;">
                        Registro Sanitario Oficial: <code>${trazabilidad.registroSanitario}</code>
                    </p>
                </c:if>
                <p style="color: var(--color-text-muted); font-size: 0.85rem; margin-top: 0.5rem;">
                    Fecha de Elaboración y Lote: <strong>${trazabilidad.fechaProduccion}</strong>
                </p>
            </div>

            <!-- Footer del Certificado -->
            <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--color-border); padding-top: 1rem; font-size: 0.85rem; color: var(--color-text-muted);">
                <span>Certificado emitido digitalmente por la plataforma Entre Copas</span>
                <a href="${pageContext.request.contextPath}/catalogo" class="btn-outline" style="min-height: 38px;">
                    Volver al Catálogo
                </a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <c:if test="${empty mensajeError}">
            <div class="empty-state" style="max-width: 600px; margin: 0 auto;">
                <h3>Consulta la Ficha de Origen de tu Bebida</h3>
                <p>Ingresa el código único grabado en la etiqueta para visualizar el perfil analítico y origen.</p>
                <p style="font-size: 0.9rem; color: var(--color-primary); font-weight: 600;">
                    Código de muestra: <a href="${pageContext.request.contextPath}/trazabilidad/EC-2026-PR01-A101" style="color: inherit; text-decoration: underline;">EC-2026-PR01-A101</a>
                </p>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>

<jsp:include page="common/footer.jsp" />
