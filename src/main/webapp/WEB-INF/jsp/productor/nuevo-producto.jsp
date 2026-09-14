<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Registrar Nueva Bebida - Entre Copas" scope="request" />
<jsp:include page="../common/header.jsp" />

<div style="margin-bottom: 1.5rem;">
    <a href="${pageContext.request.contextPath}/panel/productor/productos" class="btn-outline">
        ← Volver a Mis Productos
    </a>
</div>

<div class="form-card">
    <div style="margin-bottom: 2rem;">
        <h1>Nueva Bebida Artesanal</h1>
        <p style="color: var(--color-text-muted);">Completa los datos técnicos y comerciales para publicarla en el catálogo.</p>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/panel/productor/productos/nuevo">
        <div class="form-group">
            <label for="nombre" class="form-label">Nombre Comercial de la Bebida *</label>
            <input type="text" id="nombre" name="nombre" class="form-control" placeholder="Ej. Reserva Malbec Gran Cosecha" required maxlength="120">
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
            <div class="form-group">
                <label for="tipoBebida" class="form-label">Tipo de Bebida *</label>
                <select id="tipoBebida" name="tipoBebida" class="form-control" required>
                    <option value="">-- Seleccionar Categoría --</option>
                    <option value="Vino">Vino Artesanal</option>
                    <option value="Cerveza">Cerveza Artesanal</option>
                    <option value="Hidromiel">Hidromiel</option>
                </select>
            </div>

            <div class="form-group">
                <label for="presentacion" class="form-label">Presentación / Formato *</label>
                <input type="text" id="presentacion" name="presentacion" class="form-control" placeholder="Ej. Botella 750ml, Lata 473ml" required maxlength="50">
            </div>
        </div>

        <div class="form-group">
            <label for="precio" class="form-label">Precio al Público ($) *</label>
            <input type="number" step="0.01" min="0.01" id="precio" name="precio" class="form-control" placeholder="Ej. 65000.00" required>
        </div>

        <div class="form-group">
            <label for="descripcion" class="form-label">Descripción y Perfil Sensorial *</label>
            <textarea id="descripcion" name="descripcion" class="form-control" rows="4" placeholder="Describe los ingredientes nobles, notas de cata, maridaje y proceso de elaboración..." required></textarea>
        </div>

        <div style="display: flex; justify-content: flex-end; gap: 1rem; margin-top: 2rem;">
            <a href="${pageContext.request.contextPath}/panel/productor/productos" class="btn-outline">
                Cancelar
            </a>
            <button type="submit" class="btn-noble">
                Guardar y Publicar Bebida
            </button>
        </div>
    </form>
</div>

<jsp:include page="../common/footer.jsp" />
