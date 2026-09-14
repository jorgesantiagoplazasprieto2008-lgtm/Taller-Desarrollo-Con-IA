<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Dashboard Financiero y Mermas - Entre Copas" scope="request" />
<jsp:include page="../common/header.jsp" />

<div style="margin-bottom: 2rem;">
    <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem;">
        <div>
            <span class="product-badge-type" style="background-color: #fef3c7; color: #b45309; margin-bottom: 0.5rem; display: inline-block;">
                MÓDULO DE COSTOS Y MERMAS
            </span>
            <h1 style="margin: 0; font-size: 2.2rem;">Dashboard Financiero de Producción</h1>
            <p style="color: var(--color-text-muted); margin-top: 0.25rem;">
                Auditoría interna de rendimientos, volumen y costos unitarios
                <c:if test="${not empty productor}">
                    para <strong>${productor.nombreComercial}</strong> (${productor.ubicacionOrigen})
                </c:if>
            </p>
        </div>
        <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
            <button type="button" onclick="window.print()" class="btn-outline" title="Exportar o imprimir informe ejecutivo">
                🖨️ Imprimir Reporte
            </button>
            <a href="${pageContext.request.contextPath}/panel/productor/lotes" class="btn-outline">
                ← Ver Mis Lotes
            </a>
            <a href="${pageContext.request.contextPath}/panel/productor/lotes/nuevo" class="btn-amber">
                + Nuevo Lote
            </a>
        </div>
    </div>
</div>

<!-- BARRA DE FILTRADO DINÁMICO POR PRODUCTO Y PERÍODO -->
<div style="background-color: #FFFFFF; border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 1.25rem 1.5rem; margin-bottom: 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
    <form method="GET" action="${pageContext.request.contextPath}/panel/financiero" style="display: flex; align-items: center; flex-wrap: wrap; gap: 1rem;">
        <div style="display: flex; align-items: center; gap: 0.5rem;">
            <span style="font-size: 1.1rem;">🔍</span>
            <strong style="font-size: 0.95rem; color: var(--color-text-main);">Filtrar Análisis:</strong>
        </div>

        <!-- Selector de Producto -->
        <div style="flex: 1; min-width: 200px;">
            <select name="productoId" class="form-control" style="width: 100%; padding: 0.5rem 0.75rem; border: 1px solid var(--color-border); border-radius: var(--radius-sm); background-color: var(--color-bg-parchment);">
                <option value="">-- Todas las Bebidas Artesanales --</option>
                <c:forEach var="p" items="${productos}">
                    <option value="${p.id}" ${p.id eq selectedProductoId ? 'selected' : ''}>
                        ${p.nombre} (${p.tipoBebida})
                    </option>
                </c:forEach>
            </select>
        </div>

        <!-- Selector de Período Temporal -->
        <div style="min-width: 170px;">
            <select name="periodo" class="form-control" style="width: 100%; padding: 0.5rem 0.75rem; border: 1px solid var(--color-border); border-radius: var(--radius-sm); background-color: var(--color-bg-parchment);">
                <option value="" ${empty selectedPeriodo ? 'selected' : ''}>Histórico Completo</option>
                <option value="1m" ${selectedPeriodo eq '1m' ? 'selected' : ''}>Último Mes (30 días)</option>
                <option value="3m" ${selectedPeriodo eq '3m' ? 'selected' : ''}>Último Trimestre (90 días)</option>
                <option value="1y" ${selectedPeriodo eq '1y' ? 'selected' : ''}>Último Año</option>
            </select>
        </div>

        <!-- Botones de Acción -->
        <div style="display: flex; gap: 0.5rem;">
            <button type="submit" class="btn-noble" style="padding: 0.5rem 1.25rem;">
                Aplicar Filtro
            </button>
            <c:if test="${not empty selectedProductoId or not empty selectedPeriodo}">
                <a href="${pageContext.request.contextPath}/panel/financiero" class="btn-outline" style="padding: 0.5rem 1rem; color: #dc2626; border-color: #fca5a5;" title="Quitar filtros y mostrar todo">
                    Limpiar ✕
                </a>
            </c:if>
        </div>
    </form>
</div>

<!-- 4 TARJETAS CLAVE DE RESUMEN FINANCIERO (KPIs) -->
<div class="kpi-grid" style="margin-bottom: 2rem;">
    <!-- Inversión Total -->
    <div class="kpi-card" style="border-left: 4px solid var(--color-primary);">
        <span class="kpi-title">Inversión Total Acumulada</span>
        <span class="kpi-value" style="color: var(--color-primary);">
            $<fmt:formatNumber value="${dashboard.kpis.costoTotalAcumulado}" type="number" minFractionDigits="2" maxFractionDigits="2" />
        </span>
        <span style="display: block; font-size: 0.8rem; color: var(--color-text-muted); margin-top: 0.35rem;">
            En ${dashboard.kpis.lotesContabilizados} lote(s) contabilizado(s)
        </span>
    </div>

    <!-- Volumen Neto Producido -->
    <div class="kpi-card" style="border-left: 4px solid var(--color-secondary);">
        <span class="kpi-title">Volumen Neto Embotellado</span>
        <span class="kpi-value" style="color: var(--color-secondary);">
            <fmt:formatNumber value="${dashboard.kpis.volumenNetoLitros}" type="number" minFractionDigits="1" maxFractionDigits="1" /> <small style="font-size: 1.1rem;">L</small>
        </span>
        <span style="display: block; font-size: 0.8rem; color: var(--color-text-muted); margin-top: 0.35rem;">
            De <fmt:formatNumber value="${dashboard.kpis.volumenTotalLitros}" type="number" minFractionDigits="1" maxFractionDigits="1" /> L iniciales elaborados
        </span>
    </div>

    <!-- Merma Total en Elaboración -->
    <div class="kpi-card" style="border-left: 4px solid ${dashboard.kpis.porcentajeMermaGlobal > 10.0 ? '#dc2626' : (dashboard.kpis.porcentajeMermaGlobal > 5.0 ? '#d97706' : '#16a34a')};">
        <span class="kpi-title">Merma Total Acumulada</span>
        <span class="kpi-value" style="color: ${dashboard.kpis.porcentajeMermaGlobal > 10.0 ? '#dc2626' : (dashboard.kpis.porcentajeMermaGlobal > 5.0 ? '#d97706' : '#16a34a')};">
            <fmt:formatNumber value="${dashboard.kpis.mermaTotalLitros}" type="number" minFractionDigits="1" maxFractionDigits="1" /> <small style="font-size: 1.1rem;">L</small>
        </span>
        <span style="display: block; font-size: 0.8rem; color: ${dashboard.kpis.porcentajeMermaGlobal > 10.0 ? '#dc2626' : (dashboard.kpis.porcentajeMermaGlobal > 5.0 ? '#d97706' : '#16a34a')}; font-weight: 600; margin-top: 0.35rem;">
            Tasa global de merma: ${dashboard.kpis.porcentajeMermaGlobal}%
            <c:choose>
                <c:when test="${dashboard.kpis.porcentajeMermaGlobal > 10.0}">(⚠️ Crítica)</c:when>
                <c:when test="${dashboard.kpis.porcentajeMermaGlobal > 5.0}">(Moderada)</c:when>
                <c:otherwise>(Óptima)</c:otherwise>
            </c:choose>
        </span>
    </div>

    <!-- Costo Promedio por Litro -->
    <div class="kpi-card" style="border-left: 4px solid var(--color-primary);">
        <span class="kpi-title">Costo Promedio / Litro</span>
        <span class="kpi-value" style="color: var(--color-primary);">
            $<fmt:formatNumber value="${dashboard.kpis.costoPromedioPorLitro}" type="number" minFractionDigits="2" maxFractionDigits="2" />
        </span>
        <span style="display: block; font-size: 0.8rem; color: var(--color-text-muted); margin-top: 0.35rem;">
            Base de fijación de precios y márgenes
        </span>
    </div>
</div>

<!-- TABLA DE DESGLOSE Y AUDITORÍA POR LOTE -->
<div class="table-container" style="margin-bottom: 2.5rem;">
    <div style="padding: 1.25rem 1.5rem; background-color: var(--color-bg-parchment); border-bottom: 1px solid var(--color-border); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.5rem;">
        <div>
            <h3 style="margin: 0; font-size: 1.25rem;">Auditoría de Costos y Rendimiento por Lote</h3>
            <p style="margin: 0; font-size: 0.85rem; color: var(--color-text-muted);">
                Desglose analítico de unidades estimadas y márgenes de contribución
            </p>
        </div>
        <span class="badge-status badge-activo" style="font-size: 0.75rem; text-transform: uppercase;">
            🔒 Datos Confidenciales del Productor
        </span>
    </div>

    <c:choose>
        <c:when test="${not empty dashboard.desglosePorLote}">
            <table class="custom-table">
                <thead>
                    <tr>
                        <th>Código Lote</th>
                        <th>Producto / Formato</th>
                        <th>Fecha</th>
                        <th>Vol. Neto</th>
                        <th>Merma (%)</th>
                        <th>Costo Total</th>
                        <th>Costo / L</th>
                        <th>Unidades Est.</th>
                        <th>Margen Bruto</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="lote" items="${dashboard.desglosePorLote}">
                        <tr>
                            <td>
                                <strong style="font-family: monospace; font-size: 0.95rem; color: var(--color-primary); display: block;">
                                    ${lote.codigoTrazabilidad}
                                </strong>
                                <a href="${pageContext.request.contextPath}/trazabilidad/${lote.codigoTrazabilidad}" target="_blank" style="font-size: 0.75rem; color: var(--color-text-muted); text-decoration: none;">
                                    Certificado ISO ↗
                                </a>
                            </td>
                            <td>
                                <strong style="color: var(--color-text-main);">${lote.productoNombre}</strong>
                                <span style="display: block; font-size: 0.8rem; color: var(--color-text-muted);">
                                    ${lote.presentacion}
                                </span>
                            </td>
                            <td style="font-size: 0.9rem;">${lote.fechaProduccion}</td>
                            <td>
                                <strong><fmt:formatNumber value="${lote.volumenNeto}" type="number" minFractionDigits="1" maxFractionDigits="1" /> L</strong>
                                <span style="display: block; font-size: 0.75rem; color: var(--color-text-muted);">
                                    de <fmt:formatNumber value="${lote.volumenLitros}" type="number" minFractionDigits="1" maxFractionDigits="1" /> L
                                </span>
                            </td>
                            <td>
                                <span style="font-weight: 500;">
                                    <fmt:formatNumber value="${lote.mermaLitros}" type="number" minFractionDigits="1" maxFractionDigits="1" /> L
                                </span>
                                <c:choose>
                                    <c:when test="${lote.mermaPorcentaje > 10.0}">
                                        <span class="badge-status badge-retirado" style="font-size: 0.7rem; padding: 0.15rem 0.4rem; display: block; width: fit-content; margin-top: 0.2rem;">
                                            ⚠️ ${lote.mermaPorcentaje}% (Crítica)
                                        </span>
                                    </c:when>
                                    <c:when test="${lote.mermaPorcentaje > 5.0}">
                                        <span class="badge-status badge-pausado" style="font-size: 0.7rem; padding: 0.15rem 0.4rem; display: block; width: fit-content; margin-top: 0.2rem;">
                                            ${lote.mermaPorcentaje}% (Moderada)
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge-status badge-activo" style="font-size: 0.7rem; padding: 0.15rem 0.4rem; display: block; width: fit-content; margin-top: 0.2rem;">
                                            ✓ ${lote.mermaPorcentaje}% (Óptima)
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <strong style="color: var(--color-primary);">
                                    $<fmt:formatNumber value="${lote.costoTotal}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                </strong>
                            </td>
                            <td style="font-weight: 600;">
                                $<fmt:formatNumber value="${lote.costoPorLitro}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                            </td>
                            <td>
                                <span style="font-weight: 700;">${lote.unidadesEstimadas}</span>
                                <span style="display: block; font-size: 0.75rem; color: var(--color-text-muted);">
                                    @ $<fmt:formatNumber value="${lote.costoPorUnidad}" type="number" minFractionDigits="2" maxFractionDigits="2" /> /u
                                </span>
                            </td>
                            <td>
                                <span class="badge-status badge-activo" style="font-size: 0.85rem; font-weight: 700;">
                                    ${lote.margenBrutoPorcentaje}%
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div style="text-align: center; padding: 3rem; background-color: #FFFFFF;">
                <div style="font-size: 3rem; margin-bottom: 0.75rem;">📊</div>
                <h3 style="margin-bottom: 0.5rem;">No se encontraron lotes para este criterio de búsqueda</h3>
                <p style="color: var(--color-text-muted); max-width: 480px; margin: 0 auto 1.5rem;">
                    Ajusta los filtros temporales o por producto, o da de alta un nuevo lote de producción artesanal.
                </p>
                <div style="display: flex; justify-content: center; gap: 0.75rem;">
                    <c:if test="${not empty selectedProductoId or not empty selectedPeriodo}">
                        <a href="${pageContext.request.contextPath}/panel/financiero" class="btn-outline">
                            Limpiar Filtros
                        </a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/panel/productor/lotes/nuevo" class="btn-noble">
                        + Registrar Nuevo Lote
                    </a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- TABLA DE RENTABILIDAD CONSOLIDADA POR LÍNEA DE PRODUCTO -->
<c:if test="${not empty dashboard.desgloseProductos}">
    <div class="table-container" style="margin-bottom: 2rem;">
        <div style="padding: 1.25rem 1.5rem; background-color: var(--color-bg-parchment); border-bottom: 1px solid var(--color-border);">
            <h3 style="margin: 0; font-size: 1.25rem;">Rendimiento Comercial por Línea de Producto</h3>
            <p style="margin: 0; font-size: 0.85rem; color: var(--color-text-muted);">
                Consolidación de inversión, volumen producido y margen promedio proyectado
            </p>
        </div>
        <table class="custom-table">
            <thead>
                <tr>
                    <th>Bebida Artesanal</th>
                    <th>Categoría</th>
                    <th>Precio Venta Sugerido</th>
                    <th>Lotes Producidos</th>
                    <th>Costo Acumulado</th>
                    <th>Volumen Neto Total</th>
                    <th>Unidades Totales</th>
                    <th>Margen Promedio</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="prod" items="${dashboard.desgloseProductos}">
                    <tr>
                        <td>
                            <strong style="font-size: 1.05rem; color: var(--color-primary);">${prod.nombre}</strong>
                        </td>
                        <td>
                            <span class="product-badge-type">${prod.tipoBebida}</span>
                        </td>
                        <td>
                            <strong style="color: var(--color-text-main);">
                                $<fmt:formatNumber value="${prod.precio}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                            </strong>
                        </td>
                        <td>
                            <span style="font-weight: 600;">${prod.lotesCount}</span>
                        </td>
                        <td>
                            $<fmt:formatNumber value="${prod.costoAcumulado}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                        </td>
                        <td>
                            <fmt:formatNumber value="${prod.volumenNetoTotal}" type="number" minFractionDigits="1" maxFractionDigits="1" /> L
                        </td>
                        <td>
                            <strong style="color: var(--color-secondary);">${prod.unidadesEstimadasTotal}</strong>
                        </td>
                        <td>
                            <span class="badge-status badge-activo" style="font-weight: 700;">
                                ${prod.margenPromedioPorcentaje}%
                            </span>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

<!-- BANNER DE RECOMENDACIÓN ENOLÓGICA Y FINANCIERA -->
<div style="background-color: #fdfbf7; border: 1px solid var(--color-border); border-left: 4px solid var(--color-primary); border-radius: var(--radius-md); padding: 1.25rem; font-size: 0.9rem; line-height: 1.6; margin-bottom: 2rem;">
    💡 <strong>Recomendación Enológica & de Fijación de Precios:</strong>
    Una tasa de merma superior al <strong>6.0%</strong> en fermentación indica necesidad de calibración en prensas o evaporación en barricas. Mantener el costo unitario por litro permanentemente actualizado te permite negociar precios mayoristas sostenibles con la hostelería preservando márgenes saludables (rango recomendado: 45% - 60%).
</div>

<!-- ESTILOS EXCLUSIVOS PARA IMPRESIÓN LIMPIA DE REPORTE -->
<style>
@media print {
    header, footer, nav, .btn-outline, .btn-noble, .btn-amber, form, .product-badge-type, .role-dropdown {
        display: none !important;
    }
    body {
        background-color: #FFFFFF !important;
        color: #000000 !important;
        font-size: 10pt !important;
    }
    .kpi-card, .table-container {
        box-shadow: none !important;
        border: 1px solid #CCC !important;
        page-break-inside: avoid;
    }
    .kpi-grid {
        display: grid !important;
        grid-template-columns: repeat(4, 1fr) !important;
    }
}
</style>

<jsp:include page="../common/footer.jsp" />
