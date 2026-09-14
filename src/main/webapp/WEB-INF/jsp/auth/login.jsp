<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Iniciar Sesión - Entre Copas" scope="request" />
<jsp:include page="../common/header.jsp" />

<div class="form-card" style="max-width: 480px; margin: 2rem auto;">
    <div style="text-align: center; margin-bottom: 2rem;">
        <div class="brand-logo-icon" style="width: 50px; height: 50px; font-size: 1.5rem; margin: 0 auto 1rem;">EC</div>
        <h1>Bienvenido de Vuelta</h1>
        <p style="color: var(--color-text-muted);">Accede a la plataforma de gestión y trazabilidad artesanal</p>
    </div>

    <!-- Botones de Demostración Rápida -->
    <div style="background-color: var(--color-bg-parchment); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: 1rem; margin-bottom: 1.5rem;">
        <span style="font-size: 0.85rem; font-weight: 600; color: var(--color-primary); display: block; margin-bottom: 0.5rem;">
            ⚡ Accesos Rápidos de Prueba:
        </span>
        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
            <button type="button" class="btn-outline" style="padding: 0.35rem 0.75rem; font-size: 0.8rem;" onclick="setDemo('contacto@bodegasangabriel.com', 'Password123*')">
                🍷 Productor
            </button>
            <button type="button" class="btn-outline" style="padding: 0.35rem 0.75rem; font-size: 0.8rem;" onclick="setDemo('gerencia@rincongourmet.com', 'Password123*')">
                🍽️ Hostelería
            </button>
            <button type="button" class="btn-outline" style="padding: 0.35rem 0.75rem; font-size: 0.8rem;" onclick="setDemo('admin@entrecopas.com', 'AdminPass123*')">
                🛡️ Admin
            </button>
        </div>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="form-group">
            <label for="username" class="form-label">Correo Electrónico</label>
            <input type="email" id="username" name="username" class="form-control" placeholder="nombre@bodega.com" required>
        </div>

        <div class="form-group">
            <label for="password" class="form-label">Contraseña</label>
            <input type="password" id="password" name="password" class="form-control" placeholder="••••••••" required>
        </div>

        <button type="submit" class="btn-noble" style="width: 100%; margin-top: 1rem;">
            Iniciar Sesión
        </button>
    </form>

    <div style="text-align: center; margin-top: 1.5rem; font-size: 0.9rem;">
        <span style="color: var(--color-text-muted);">¿Aún no tienes cuenta?</span>
        <a href="${pageContext.request.contextPath}/registro" style="color: var(--color-primary); font-weight: 600; text-decoration: none; margin-left: 0.25rem;">
            Regístrate aquí
        </a>
    </div>
</div>

<script>
function setDemo(email, pass) {
    document.getElementById('username').value = email;
    document.getElementById('password').value = pass;
}
</script>

<jsp:include page="../common/footer.jsp" />
