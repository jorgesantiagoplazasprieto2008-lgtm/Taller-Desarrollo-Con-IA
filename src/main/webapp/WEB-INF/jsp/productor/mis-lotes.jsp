<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Control de Lotes y Trazabilidad - Panel del Productor" scope="request" />
<jsp:include page="../common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h1>Control de Lotes y Trazabilidad</h1>
        <p style="color: var(--color-text-muted);">
            Historial de lotes, cálculo analítico de mermas y códigos de trazabilidad emitidos.
        </p>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center;">
        <a href="${pageContext.request.contextPath}/panel/financiero" class="btn-outline">
            📊 Dashboard Financiero
        </a>
        <a href="${pageContext.request.contextPath}/panel/productor/lotes/nuevo" class="btn-noble">
            + Registrar Nuevo Lote
        </a>
    </div>
</div>

<c:choose>
    <c:when test="${not empty lotes}">
        <div class="admin-table-container">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Código Trazabilidad</th>
                        <th>Bebida Artesanal</th>
                        <th>Fecha Producción</th>
                        <th>Volumen / Merma</th>
                        <th>Parámetros Clave</th>
                        <th>Costo Unitario</th>
                        <th>Certificado</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="lote" items="${lotes}">
                        <tr>
                            <td>
                                <strong style="font-family: monospace; color: var(--color-primary);">${lote.codigoTrazabilidad}</strong>
                                <button type="button" onclick="copiarCodigo('${lote.codigoTrazabilidad}')" style="background: none; border: none; cursor: pointer; margin-left: 0.25rem;" title="Copiar código">
                                    📋
                                </button>
                            </td>
                            <td>
                                <strong>${lote.productoNombre}</strong><br>
                                <span class="product-badge-type" style="margin: 0;">${lote.tipoBebida}</span>
                            </td>
                            <td>${lote.fechaProduccion}</td>
                            <td>
                                ${lote.volumenLitros} L total<br>
                                <small style="color: var(--color-secondary); font-weight: 600;">
                                    Merma: ${lote.mermaLitros} L (${lote.porcentajeMerma}%)
                                </small>
                            </td>
                            <td>
                                <small>
                                    pH: <strong>${lote.parametrosAnaliticos.ph}</strong> | 
                                    ABV: <strong>${lote.parametrosAnaliticos.graduacionAlcoholica}%</strong><br>
                                    Acidez: ${lote.parametrosAnaliticos.acidezTotalGl} g/L
                                </small>
                            </td>
                            <td>
                                <strong>$<fmt:formatNumber value="${lote.costoPorLitro}" type="number" minFractionDigits="2" maxFractionDigits="2" /></strong>/L<br>
                                <small style="color: var(--color-text-muted);">
                                    Total: $<fmt:formatNumber value="${lote.costoTotal}" type="number" minFractionDigits="0" />
                                </small>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/trazabilidad/${lote.codigoTrazabilidad}" target="_blank" class="btn-outline" style="padding: 0.35rem 0.75rem; font-size: 0.85rem;">
                                    Ver Ficha ↗
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:when>
    <c:otherwise>
        <div class="empty-state">
            <h3>No tienes lotes productivos registrados</h3>
            <p>Registra tu primer lote de producción artesanal para obtener el código algorítmico y emitir el certificado oficial.</p>
            <a href="${pageContext.request.contextPath}/panel/productor/lotes/nuevo" class="btn-noble">
                + Registrar Primer Lote
            </a>
        </div>
    </c:otherwise>
</c:choose>

<script>
function copiarCodigo(codigo) {
    navigator.clipboard.writeText(codigo).then(() => {
        alert("Código de trazabilidad copiado al portapapeles: " + codigo);
    });
}
</script>

<jsp:include page="../common/footer.jsp" />
