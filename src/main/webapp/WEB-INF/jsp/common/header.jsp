<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${tituloPagina != null ? tituloPagina : 'Entre Copas - Bebidas Artesanales y Trazabilidad'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-tokens.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
    <header class="navbar-custom">
        <a href="${pageContext.request.contextPath}/catalogo" class="brand-container">
            <div class="brand-logo-icon">EC</div>
            <span class="brand-title">Entre Copas</span>
        </a>
        <nav class="nav-container">
            <ul class="nav-links">
                <!-- Vistas públicas accesibles por todos los roles -->
                <li>
                    <a href="${pageContext.request.contextPath}/catalogo" class="${paginaActiva == 'catalogo' ? 'active' : ''}">
                        Catálogo
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/trazabilidad" class="${paginaActiva == 'trazabilidad' ? 'active' : ''}">
                        Trazabilidad
                    </a>
                </li>

                <!-- Opciones exclusivas para Productor Artesanal y Administrador -->
                <c:if test="${esProductor}">
                    <li>
                        <a href="${pageContext.request.contextPath}/panel/productor/productos" class="${paginaActiva == 'mis-productos' || paginaActiva == 'nuevo-producto' ? 'active' : ''}">
                            Mis Productos
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/panel/productor/lotes" class="${paginaActiva == 'mis-lotes' ? 'active' : ''}">
                            Mis Lotes
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/panel/financiero" class="${paginaActiva == 'financiero' ? 'active' : ''}">
                            📊 Financiero
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/panel/productor/lotes/nuevo" class="btn-amber" style="padding: 0.45rem 0.9rem; font-size: 0.85rem; text-decoration: none;">
                            + Nuevo Lote
                        </a>
                    </li>
                </c:if>

                <!-- Opciones exclusivas para Hostelería y Administrador -->
                <c:if test="${esHosteleria}">
                    <li>
                        <a href="${pageContext.request.contextPath}/panel/hosteleria/disponibilidad" class="${paginaActiva == 'hosteleria' ? 'active' : ''}">
                            🍽️ Barra Hostelería
                        </a>
                    </li>
                </c:if>
            </ul>

            <!-- Sección de Usuario y Distinción Visual de Rol -->
            <div class="nav-user-section">
                <!-- Selector Visual y Conmutador de Roles para Pruebas / Demo -->
                <div class="role-dropdown" tabindex="0">
                    <button type="button" class="role-badge role-badge-${esProductor ? 'productor' : (esHosteleria ? 'hosteleria' : (esAdmin ? 'admin' : (esConsumidor ? 'consumidor' : 'visitante')))}">
                        <span class="role-badge-icon">${rolIcono}</span>
                        <div class="role-badge-info">
                            <span class="role-badge-title">${rolEtiqueta}</span>
                            <c:if test="${usuarioAutenticado}">
                                <span class="role-badge-sub">${usuarioNombre}</span>
                            </c:if>
                        </div>
                        <span class="role-badge-arrow">▾</span>
                    </button>
                    <div class="role-dropdown-menu">
                        <div class="role-dropdown-header">
                            <span>Conmutar Rol (Pruebas / Demo)</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/demo/switch-role?role=productor" class="role-dropdown-item ${usuarioRol == 'ROLE_PRODUCTOR' ? 'current' : ''}">
                            <span class="item-icon">🍷</span>
                            <div>
                                <strong>Bodega Productora</strong>
                                <small>Carlos Mendoza (San Gabriel)</small>
                            </div>
                            <c:if test="${usuarioRol == 'ROLE_PRODUCTOR'}"><span class="check-mark">✓</span></c:if>
                        </a>
                        <a href="${pageContext.request.contextPath}/demo/switch-role?role=hosteleria" class="role-dropdown-item ${usuarioRol == 'ROLE_HOSTELERIA' ? 'current' : ''}">
                            <span class="item-icon">🍽️</span>
                            <div>
                                <strong>Hostelería / Barra</strong>
                                <small>Restaurante El Rincón Gourmet</small>
                            </div>
                            <c:if test="${usuarioRol == 'ROLE_HOSTELERIA'}"><span class="check-mark">✓</span></c:if>
                        </a>
                        <a href="${pageContext.request.contextPath}/demo/switch-role?role=consumidor" class="role-dropdown-item ${usuarioRol == 'ROLE_CONSUMIDOR' ? 'current' : ''}">
                            <span class="item-icon">🥂</span>
                            <div>
                                <strong>Consumidor Final</strong>
                                <small>Juan Pérez</small>
                            </div>
                            <c:if test="${usuarioRol == 'ROLE_CONSUMIDOR'}"><span class="check-mark">✓</span></c:if>
                        </a>
                        <a href="${pageContext.request.contextPath}/demo/switch-role?role=admin" class="role-dropdown-item ${usuarioRol == 'ROLE_ADMIN' ? 'current' : ''}">
                            <span class="item-icon">🛡️</span>
                            <div>
                                <strong>Administrador Global</strong>
                                <small>Admin Principal</small>
                            </div>
                            <c:if test="${usuarioRol == 'ROLE_ADMIN'}"><span class="check-mark">✓</span></c:if>
                        </a>
                        <div class="role-dropdown-divider"></div>
                        <a href="${pageContext.request.contextPath}/demo/switch-role?role=visitante" class="role-dropdown-item ${!usuarioAutenticado ? 'current' : ''}">
                            <span class="item-icon">👤</span>
                            <div>
                                <strong>Visitante Anónimo</strong>
                                <small>Navegación pública sin sesión</small>
                            </div>
                            <c:if test="${!usuarioAutenticado}"><span class="check-mark">✓</span></c:if>
                        </a>
                    </div>
                </div>

                <!-- Botones de Acción de Autenticación -->
                <c:choose>
                    <c:when test="${usuarioAutenticado}">
                        <a href="${pageContext.request.contextPath}/logout" class="nav-btn-logout" title="Cerrar sesión">
                            <span>Salir</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="nav-btn-login ${paginaActiva == 'login' ? 'active' : ''}">
                            Acceder
                        </a>
                        <a href="${pageContext.request.contextPath}/registro" class="nav-btn-register ${paginaActiva == 'registro' ? 'active' : ''}">
                            Registrarse
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </nav>
    </header>
    <main class="main-content">
        <c:if test="${not empty mensajeExito}">
            <div class="alert alert-success">
                <span>${mensajeExito}</span>
            </div>
        </c:if>
        <c:if test="${not empty mensajeError}">
            <div class="alert alert-error">
                <span>${mensajeError}</span>
            </div>
        </c:if>
