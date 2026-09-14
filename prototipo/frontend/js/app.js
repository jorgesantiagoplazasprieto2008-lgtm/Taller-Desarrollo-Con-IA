/**
 * Entre Copas - Main Application Orchestrator
 * Bootstraps router, responsive navbar state, dynamic role navigation, and session listener
 */

import { state } from './state.js';
import { Router } from './router.js';

document.addEventListener('DOMContentLoaded', () => {
  // Initialize Router
  const router = new Router('app-viewport');

  // Navbar Elements
  const navLinksContainer = document.getElementById('navbar-links-container');
  const navAuthContainer = document.getElementById('navbar-auth-container');
  const mobileToggle = document.getElementById('mobile-menu-toggle');
  const navbarCollapse = document.getElementById('navbar-collapse');

  // Update Navigation based on session
  function updateNav() {
    const user = state.getUser();
    const isAuth = state.isAuthenticated();

    let linksHtml = `
      <a href="#/" class="nav-link">Inicio</a>
      <a href="#/catalogo" class="nav-link">Catálogo</a>
      <a href="#/trazabilidad" class="nav-link">Trazabilidad</a>
    `;

    if (isAuth) {
      if (state.isProductor() || state.isAdmin()) {
        linksHtml += `
          <a href="#/panel" class="nav-link">🍇 Mi Bodega</a>
          <a href="#/panel/financiero" class="nav-link">📊 Financiero</a>
        `;
      }
      if (state.isHosteleria() || state.isAdmin()) {
        linksHtml += `
          <a href="#/panel/hosteleria" class="nav-link">🍽️ Mi Carta</a>
        `;
      }
    }

    if (navLinksContainer) {
      navLinksContainer.innerHTML = linksHtml;
    }

    let authHtml = '';
    if (isAuth && user) {
      authHtml = `
        <div style="display: flex; align-items: center; gap: var(--space-sm);">
          <span style="font-size: 0.85rem; color: var(--color-text); font-weight: 500;">
            ${escapeHtml(user.nombre)}
          </span>
          <button id="btn-logout" class="btn btn-outline btn-sm" style="font-size: 0.8rem; padding: 4px 10px;">
            Salir
          </button>
        </div>
      `;
    } else {
      authHtml = `
        <a href="#/login" class="btn btn-outline btn-sm">Iniciar Sesión</a>
        <a href="#/registro" class="btn btn-primary btn-sm">Registrarse</a>
      `;
    }

    if (navAuthContainer) {
      navAuthContainer.innerHTML = authHtml;
      document.getElementById('btn-logout')?.addEventListener('click', () => {
        state.clearSession();
        state.showToast('Has cerrado sesión correctamente', 'info');
        window.location.hash = '#/';
      });
    }

    // Highlight active link
    highlightActiveLink();
  }

  function highlightActiveLink() {
    const currentHash = window.location.hash || '#/';
    document.querySelectorAll('.nav-link').forEach((link) => {
      if (link.getAttribute('href') === currentHash) {
        link.classList.add('active');
      } else {
        link.classList.remove('active');
      }
    });
  }

  window.addEventListener('hashchange', highlightActiveLink);

  // Mobile Menu Toggle
  if (mobileToggle && navbarCollapse) {
    mobileToggle.addEventListener('click', () => {
      navbarCollapse.classList.toggle('open');
    });

    // Close mobile menu on link click
    navbarCollapse.addEventListener('click', (e) => {
      if (e.target.tagName === 'A' || e.target.tagName === 'BUTTON') {
        navbarCollapse.classList.remove('open');
      }
    });
  }

  // Subscribe to state changes (login, logout)
  state.subscribe(() => {
    updateNav();
  });

  // Initial nav render & start router
  updateNav();
  router.init();
});

function escapeHtml(str) {
  if (!str) return '';
  const div = document.createElement('div');
  div.innerText = str;
  return div.innerHTML;
}
