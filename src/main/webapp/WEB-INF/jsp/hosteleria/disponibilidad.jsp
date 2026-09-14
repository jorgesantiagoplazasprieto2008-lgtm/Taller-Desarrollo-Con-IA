<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Gestión de Disponibilidad - Entre Copas" scope="request" />
<jsp:include page="../common/header.jsp" />

<div style="margin-bottom: 2rem;">
    <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
        <div>
            <h1 style="margin: 0; font-size: 2.2rem;">Disponibilidad en Barra</h1>
            <p style="color: var(--color-text-muted); margin-top: 0.25rem;">
                Controla en tiempo real qué bebidas artesanales tienes disponibles para tus comensales
            </p>
        </div>
        <c:if test="${not empty establecimiento}">
            <div style="background-color: var(--color-bg-parchment); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 0.75rem 1.25rem; text-align: right;">
                <span style="font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.05em; color: var(--color-primary); font-weight: 700; display: block;">
                    🍽️ Local Vinculado
                </span>
                <strong style="font-size: 1.1rem; color: var(--color-text-main);">${establecimiento.nombre}</strong>
                <span style="display: block; font-size: 0.85rem; color: var(--color-text-muted);">
                    📍 ${establecimiento.direccion} (${establecimiento.ciudad})
                </span>
            </div>
        </c:if>
    </div>
</div>

<c:if test="${not empty advertencia}">
    <div class="alert alert-warning" style="background-color: #fffbeb; border: 1px solid #fef3c7; color: #b45309; padding: 1rem; border-radius: var(--radius-md); margin-bottom: 1.5rem;">
        <span>⚠️ ${advertencia}</span>
    </div>
</c:if>

<!-- Métricas Resumen -->
<c:set var="totalCount" value="${not empty productos ? productos.size() : 0}" />
<c:set var="enBarraCount" value="0" />
<c:forEach var="p" items="${productos}">
    <c:if test="${p.disponible}">
        <c:set var="enBarraCount" value="${enBarraCount + 1}" />
    </c:if>
</c:forEach>
<c:set var="sinStockCount" value="${totalCount - enBarraCount}" />

<div class="kpi-grid" style="margin-bottom: 2rem;">
    <div class="kpi-card">
        <span class="kpi-title">Bebidas Artesanales Totales</span>
        <span class="kpi-value" id="kpi-total">${totalCount}</span>
    </div>
    <div class="kpi-card">
        <span class="kpi-title">Disponibles en Tu Barra</span>
        <span class="kpi-value" style="color: #15803d;" id="kpi-en-barra">${enBarraCount}</span>
    </div>
    <div class="kpi-card">
        <span class="kpi-title">Sin Stock / No Servido</span>
        <span class="kpi-value" style="color: var(--color-primary);" id="kpi-sin-stock">${sinStockCount}</span>
    </div>
</div>

<!-- Filtros Rápidos -->
<div style="display: flex; gap: 0.5rem; margin-bottom: 1.5rem; flex-wrap: wrap;">
    <button type="button" class="btn-noble" id="filtroTodos" onclick="filtrarTabla('todos')" style="padding: 0.4rem 1rem; font-size: 0.85rem;">
        Todos (<span id="count-todos">${totalCount}</span>)
    </button>
    <button type="button" class="btn-outline" id="filtroEnBarra" onclick="filtrarTabla('enBarra')" style="padding: 0.4rem 1rem; font-size: 0.85rem;">
        En Barra (<span id="count-en-barra">${enBarraCount}</span>)
    </button>
    <button type="button" class="btn-outline" id="filtroSinStock" onclick="filtrarTabla('sinStock')" style="padding: 0.4rem 1rem; font-size: 0.85rem;">
        Sin Stock (<span id="count-sin-stock">${sinStockCount}</span>)
    </button>
</div>

<!-- Tabla de Disponibilidad -->
<div class="table-container">
    <table class="custom-table" id="tablaDisponibilidad">
        <thead>
            <tr>
                <th>Bebida Artesanal</th>
                <th>Tipo / Formato</th>
                <th>Bodega Productora</th>
                <th>Precio Sugerido</th>
                <th>Estado Actual</th>
                <th style="text-align: center;">Disponibilidad en Barra</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${not empty productos}">
                    <c:forEach var="prod" items="${productos}">
                        <tr data-disponible="${prod.disponible ? 'true' : 'false'}" id="fila-producto-${prod.productoId}">
                            <td>
                                <strong style="font-size: 1.05rem; color: var(--color-primary); display: block;">
                                    ${prod.productoNombre}
                                </strong>
                                <a href="${pageContext.request.contextPath}/productos/${prod.productoId}" target="_blank" style="font-size: 0.8rem; color: var(--color-text-muted); text-decoration: none;">
                                    Ver Ficha Pública ↗
                                </a>
                            </td>
                            <td>
                                <span class="product-badge-type">${prod.tipoBebida}</span>
                                <span style="display: block; font-size: 0.8rem; color: var(--color-text-muted); margin-top: 0.25rem;">
                                    ${prod.presentacion}
                                </span>
                            </td>
                            <td>
                                <span style="font-weight: 500;">${prod.productorNombre}</span>
                            </td>
                            <td>
                                <strong style="color: var(--color-primary);">
                                    $<fmt:formatNumber value="${prod.precioSugerido}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                </strong>
                            </td>
                            <td>
                                <span id="badge-estado-${prod.productoId}" class="badge-status ${prod.disponible ? 'badge-activo' : 'badge-retirado'}">
                                    ${prod.disponible ? 'En Barra' : 'Sin Stock'}
                                </span>
                            </td>
                            <td style="text-align: center;">
                                <div class="switch-container">
                                    <label class="switch" for="switch-${prod.productoId}">
                                        <input type="checkbox" 
                                               id="switch-${prod.productoId}" 
                                               <c:if test="${prod.disponible}">checked="checked"</c:if> 
                                               onchange="toggleDisponibilidadAjax(${prod.productoId}, this.checked)">
                                        <span class="slider" id="slider-${prod.productoId}"></span>
                                    </label>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="6" style="text-align: center; padding: 2.5rem; color: var(--color-text-muted);">
                            No hay bebidas disponibles en el catálogo para configurar en este momento.
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<script>
function toggleDisponibilidadAjax(productoId, isChecked) {
    const badge = document.getElementById('badge-estado-' + productoId);
    const fila = document.getElementById('fila-producto-' + productoId);

    // Animación visual inmediata (optimistic UI)
    if (badge) {
        badge.className = 'badge-status ' + (isChecked ? 'badge-activo' : 'badge-retirado');
        badge.innerText = isChecked ? 'En Barra' : 'Sin Stock';
    }
    if (fila) {
        fila.setAttribute('data-disponible', isChecked ? 'true' : 'false');
    }

    // Petición REST
    actualizarContadores();

    fetch('${pageContext.request.contextPath}/api/v1/hosteleria/disponibilidad/toggle', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify({
            productoId: productoId,
            disponible: isChecked
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error al actualizar disponibilidad');
        }
        return response.json();
    })
    .then(data => {
        // Confirmado exitosamente
    })
    .catch(err => {
        alert('No se pudo actualizar el estado de disponibilidad: ' + err.message);
        // Revertir estado
        const chk = document.getElementById('switch-' + productoId);
        if (chk) chk.checked = !isChecked;
        if (badge) {
            badge.className = 'badge-status ' + (!isChecked ? 'badge-activo' : 'badge-retirado');
            badge.innerText = !isChecked ? 'En Barra' : 'Sin Stock';
        }
        if (fila) {
            fila.setAttribute('data-disponible', !isChecked ? 'true' : 'false');
        }
        actualizarContadores();
    });
}

function actualizarContadores() {
    const filas = document.querySelectorAll('#tablaDisponibilidad tbody tr[data-disponible]');
    let enBarra = 0;
    filas.forEach(f => {
        if (f.getAttribute('data-disponible') === 'true') {
            enBarra++;
        }
    });
    const total = filas.length;
    const sinStock = total - enBarra;

    const kpiTotal = document.getElementById('kpi-total');
    const kpiEnBarra = document.getElementById('kpi-en-barra');
    const kpiSinStock = document.getElementById('kpi-sin-stock');
    const countTodos = document.getElementById('count-todos');
    const countEnBarra = document.getElementById('count-en-barra');
    const countSinStock = document.getElementById('count-sin-stock');

    if (kpiTotal) kpiTotal.innerText = total;
    if (kpiEnBarra) kpiEnBarra.innerText = enBarra;
    if (kpiSinStock) kpiSinStock.innerText = sinStock;
    if (countTodos) countTodos.innerText = total;
    if (countEnBarra) countEnBarra.innerText = enBarra;
    if (countSinStock) countSinStock.innerText = sinStock;
}

function filtrarTabla(tipo) {
    const filas = document.querySelectorAll('#tablaDisponibilidad tbody tr[data-disponible]');
    filas.forEach(f => {
        const esDisp = f.getAttribute('data-disponible') === 'true';
        if (tipo === 'todos') {
            f.style.display = '';
        } else if (tipo === 'enBarra') {
            f.style.display = esDisp ? '' : 'none';
        } else if (tipo === 'sinStock') {
            f.style.display = !esDisp ? '' : 'none';
        }
    });

    const bTodos = document.getElementById('filtroTodos');
    const bBarra = document.getElementById('filtroEnBarra');
    const bStock = document.getElementById('filtroSinStock');

    bTodos.className = (tipo === 'todos') ? 'btn-noble' : 'btn-outline';
    bBarra.className = (tipo === 'enBarra') ? 'btn-noble' : 'btn-outline';
    bStock.className = (tipo === 'sinStock') ? 'btn-noble' : 'btn-outline';
}
</script>

<jsp:include page="../common/footer.jsp" />
