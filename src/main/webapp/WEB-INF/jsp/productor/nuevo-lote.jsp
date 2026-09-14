<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Registrar Nuevo Lote - Entre Copas" scope="request" />
<jsp:include page="../common/header.jsp" />

<div style="margin-bottom: 1.5rem;">
    <a href="${pageContext.request.contextPath}/panel/productor/lotes" class="btn-outline">
        ← Volver a Mis Lotes
    </a>
</div>

<div class="form-card" style="max-width: 760px;">
    <div style="margin-bottom: 2rem;">
        <h1>Registrar Nuevo Lote de Producción</h1>
        <p style="color: var(--color-text-muted);">
            Registra los volúmenes, costos y parámetros de laboratorio para emitir el certificado de trazabilidad.
        </p>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/panel/productor/lotes/nuevo">
        <!-- 1. Producto y Fecha -->
        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1rem;">
            <div class="form-group">
                <label for="productoId" class="form-label">Producto Artesanal *</label>
                <select id="productoId" name="productoId" class="form-control" required>
                    <option value="">-- Seleccionar Bebida del Catálogo --</option>
                    <c:forEach var="p" items="${productos}">
                        <option value="${p.id}">${p.nombre} (${p.tipoBebida} · ${p.presentacion})</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="fechaProduccion" class="form-label">Fecha de Embotellado *</label>
                <input type="date" id="fechaProduccion" name="fechaProduccion" class="form-control" required>
            </div>
        </div>

        <!-- 2. Volúmenes y Costos con cálculo de merma -->
        <div style="background-color: var(--color-bg-parchment); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 1.5rem;">
            <h4 style="margin-bottom: 1rem; color: var(--color-primary);">Volumen, Mermas y Costos Productivos</h4>
            <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 1rem;">
                <div class="form-group">
                    <label for="volumenLitros" class="form-label">Volumen Total (L) *</label>
                    <input type="number" step="0.01" min="1" id="volumenLitros" name="volumenLitros" class="form-control" placeholder="Ej. 500.00" oninput="calcularMerma()" required>
                </div>

                <div class="form-group">
                    <label for="mermaLitros" class="form-label">Merma en Proceso (L) *</label>
                    <input type="number" step="0.01" min="0" id="mermaLitros" name="mermaLitros" class="form-control" placeholder="Ej. 25.00" oninput="calcularMerma()" required>
                </div>

                <div class="form-group">
                    <label for="costoTotal" class="form-label">Costo Total ($) *</label>
                    <input type="number" step="0.01" min="0" id="costoTotal" name="costoTotal" class="form-control" placeholder="Ej. 4500000.00" required>
                </div>
            </div>

            <div id="resumenCalculo" style="font-size: 0.9rem; color: var(--color-text-muted); margin-top: 0.5rem;">
                <!-- Cálculo dinámico -->
                <span>Porcentaje de Merma Estimado: <strong id="lblPorcentajeMerma" style="color: var(--color-secondary);">0.00%</strong></span>
            </div>
        </div>

        <!-- 3. Parámetros Analíticos de Laboratorio -->
        <div style="background-color: var(--color-surface); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 1.5rem;">
            <h4 style="margin-bottom: 1rem; color: var(--color-primary);">🔬 Parámetros Analíticos Físico-Químicos</h4>
            <div style="display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; gap: 1rem;">
                <div class="form-group">
                    <label for="ph" class="form-label">pH *</label>
                    <input type="number" step="0.01" min="2.0" max="6.0" id="ph" name="ph" class="form-control" placeholder="Ej. 3.65" required>
                </div>

                <div class="form-group">
                    <label for="graduacionAlcoholica" class="form-label">% Alcohol (ABV) *</label>
                    <input type="number" step="0.1" min="0" max="30.0" id="graduacionAlcoholica" name="graduacionAlcoholica" class="form-control" placeholder="Ej. 13.5" required>
                </div>

                <div class="form-group">
                    <label for="acidezTotalGl" class="form-label">Acidez (g/L) *</label>
                    <input type="number" step="0.01" min="0.1" id="acidezTotalGl" name="acidezTotalGl" class="form-control" placeholder="Ej. 5.80" required>
                </div>

                <div class="form-group">
                    <label for="densidad" class="form-label">Densidad (g/cm³) *</label>
                    <input type="number" step="0.001" min="0.8" max="1.5" id="densidad" name="densidad" class="form-control" placeholder="Ej. 0.994" required>
                </div>
            </div>
        </div>

        <div style="display: flex; justify-content: flex-end; gap: 1rem; margin-top: 2rem;">
            <a href="${pageContext.request.contextPath}/panel/productor/lotes" class="btn-outline">
                Cancelar
            </a>
            <button type="submit" class="btn-noble">
                Generar Código y Guardar Lote
            </button>
        </div>
    </form>
</div>

<script>
document.getElementById('fechaProduccion').valueAsDate = new Date();

function calcularMerma() {
    const vol = parseFloat(document.getElementById('volumenLitros').value) || 0;
    const mer = parseFloat(document.getElementById('mermaLitros').value) || 0;
    const lbl = document.getElementById('lblPorcentajeMerma');

    if (vol > 0) {
        const pct = ((mer / vol) * 100).toFixed(2);
        lbl.innerText = pct + '%';
        if (mer > vol) {
            lbl.style.color = 'var(--color-retired)';
            lbl.innerText += ' (¡ADVERTENCIA: La merma excede el volumen total!)';
        } else {
            lbl.style.color = 'var(--color-secondary)';
        }
    } else {
        lbl.innerText = '0.00%';
    }
}
</script>

<jsp:include page="../common/footer.jsp" />
