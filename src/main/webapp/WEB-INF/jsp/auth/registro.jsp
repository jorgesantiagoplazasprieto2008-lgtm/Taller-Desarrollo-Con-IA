<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Crear Cuenta - Entre Copas" scope="request" />
<jsp:include page="../common/header.jsp" />

<div class="form-card" style="max-width: 600px; margin: 2rem auto;">
    <div style="text-align: center; margin-bottom: 2rem;">
        <h1>Crear Cuenta en Entre Copas</h1>
        <p style="color: var(--color-text-muted);">Únete a la comunidad de productores y distribuidores artesanales</p>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/registro">
        <div class="form-group">
            <label for="nombreCompleto" class="form-label">Nombre Completo o Razón Social *</label>
            <input type="text" id="nombreCompleto" name="nombreCompleto" class="form-control" placeholder="Ej. Juan Manuel Pérez" required maxlength="120">
        </div>

        <div class="form-group">
            <label for="email" class="form-label">Correo Electrónico *</label>
            <input type="email" id="email" name="email" class="form-control" placeholder="contacto@bodega.com" required maxlength="150">
        </div>

        <div class="form-group">
            <label for="password" class="form-label">Contraseña *</label>
            <input type="password" id="password" name="password" class="form-control" placeholder="Mínimo 6 caracteres" required minlength="6">
        </div>

        <div class="form-group">
            <label for="telefono" class="form-label">Teléfono de Contacto</label>
            <input type="tel" id="telefono" name="telefono" class="form-control" placeholder="+57 300 123 4567" maxlength="20">
        </div>

        <div class="form-group">
            <label class="form-label">Tipo de Usuario / Rol en la Plataforma *</label>
            <select id="rol" name="rol" class="form-control" onchange="toggleProductorCampos(this.value)" required>
                <option value="ROLE_PRODUCTOR">🍷 Productor Artesanal (Gestión de Lotes y Fichas)</option>
                <option value="ROLE_HOSTELERIA">🍽️ Hostelería / Bar / Restaurante (Disponibilidad en Barra)</option>
                <option value="ROLE_CONSUMIDOR">🥂 Consumidor Final (Exploración y Trazabilidad)</option>
            </select>
        </div>

        <!-- Campos adicionales específicos para el productor -->
        <div id="seccionProductor" style="background-color: var(--color-bg-parchment); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 1.5rem;">
            <h4 style="margin-bottom: 1rem; color: var(--color-primary);">Datos de la Bodega o Taller Artesanal</h4>
            <div class="form-group">
                <label for="nombreComercial" class="form-label">Nombre Comercial de la Marca</label>
                <input type="text" id="nombreComercial" name="nombreComercial" class="form-control" placeholder="Ej. Bodega San Gabriel">
            </div>
            <div class="form-group">
                <label for="ubicacionOrigen" class="form-label">Ubicación / Terroir de Origen</label>
                <input type="text" id="ubicacionOrigen" name="ubicacionOrigen" class="form-control" placeholder="Ej. Villa de Leyva, Boyacá">
            </div>
            <div class="form-group">
                <label for="registroSanitario" class="form-label">Registro Sanitario / Certificación (Opcional)</label>
                <input type="text" id="registroSanitario" name="registroSanitario" class="form-control" placeholder="Ej. RSA-0012948-2024">
            </div>
        </div>

        <!-- Campos adicionales específicos para hostelería / bar -->
        <div id="seccionHosteleria" style="display: none; background-color: var(--color-bg-parchment); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 1.5rem;">
            <h4 style="margin-bottom: 1rem; color: var(--color-primary);">Datos del Local Gastronómico o Bar</h4>
            <div class="form-group">
                <label for="nombreEstablecimiento" class="form-label">Nombre del Restaurante / Bar / Taberna</label>
                <input type="text" id="nombreEstablecimiento" name="nombreEstablecimiento" class="form-control" placeholder="Ej. Rincón Gourmet Bar">
            </div>
            <div class="form-group">
                <label for="direccionEstablecimiento" class="form-label">Dirección Física del Local</label>
                <input type="text" id="direccionEstablecimiento" name="direccionEstablecimiento" class="form-control" placeholder="Ej. Cra 7 # 45-20">
            </div>
            <div class="form-group">
                <label for="ciudadEstablecimiento" class="form-label">Ciudad</label>
                <input type="text" id="ciudadEstablecimiento" name="ciudadEstablecimiento" class="form-control" placeholder="Ej. Bogotá">
            </div>
        </div>

        <button type="submit" class="btn-noble" style="width: 100%; margin-top: 1rem;">
            Completar Registro
        </button>
    </form>

    <div style="text-align: center; margin-top: 1.5rem; font-size: 0.9rem;">
        <span style="color: var(--color-text-muted);">¿Ya tienes una cuenta?</span>
        <a href="${pageContext.request.contextPath}/login" style="color: var(--color-primary); font-weight: 600; text-decoration: none; margin-left: 0.25rem;">
            Inicia sesión
        </a>
    </div>
</div>

<script>
function toggleProductorCampos(rol) {
    const secProd = document.getElementById('seccionProductor');
    const secHost = document.getElementById('seccionHosteleria');
    secProd.style.display = (rol === 'ROLE_PRODUCTOR') ? 'block' : 'none';
    secHost.style.display = (rol === 'ROLE_HOSTELERIA') ? 'block' : 'none';
}
</script>

<jsp:include page="../common/footer.jsp" />
