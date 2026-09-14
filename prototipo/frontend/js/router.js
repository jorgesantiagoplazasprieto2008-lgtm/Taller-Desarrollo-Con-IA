/**
 * Entre Copas - Hash SPA Router
 * Dynamic route parsing, authentication guards, and view mounting
 */

import { state } from './state.js';
import { renderLanding } from './views/landingView.js';
import { renderCatalogo } from './views/catalogoView.js';
import { renderProducto } from './views/productoView.js';
import { renderTrazabilidad } from './views/trazabilidadView.js';
import { renderAuth } from './views/authView.js';
import { renderPanel } from './views/panelView.js';
import { renderFinanciero } from './views/financieroView.js';
import { renderHosteleria } from './views/hosteleriaView.js';

export class Router {
  constructor(viewportId) {
    this.viewport = document.getElementById(viewportId);
    window.addEventListener('hashchange', () => this.handleRoute());
  }

  init() {
    this.handleRoute();
  }

  async handleRoute() {
    window.scrollTo(0, 0);
    const hash = window.location.hash || '#/';
    const path = hash.slice(1); // remove '#'

    // Split route parts
    const segments = path.split('/').filter(Boolean);

    // Dynamic pattern matching
    try {
      if (path === '' || path === '/') {
        await renderLanding(this.viewport);
      } else if (path === '/catalogo') {
        await renderCatalogo(this.viewport);
      } else if (segments[0] === 'producto' && segments[1]) {
        await renderProducto(this.viewport, { id: segments[1] });
      } else if (segments[0] === 'trazabilidad') {
        const code = segments[1] ? decodeURIComponent(segments[1]) : '';
        await renderTrazabilidad(this.viewport, { code });
      } else if (path === '/login') {
        await renderAuth(this.viewport, 'login');
      } else if (path === '/registro') {
        await renderAuth(this.viewport, 'register');
      } else if (path === '/panel') {
        if (!state.isAuthenticated()) {
          state.showToast('Debes iniciar sesión para acceder al panel', 'error');
          window.location.hash = '#/login';
          return;
        }
        await renderPanel(this.viewport);
      } else if (path === '/panel/financiero') {
        if (!state.isAuthenticated()) {
          state.showToast('Debes iniciar sesión para acceder al panel', 'error');
          window.location.hash = '#/login';
          return;
        }
        await renderFinanciero(this.viewport);
      } else if (path === '/panel/hosteleria') {
        if (!state.isAuthenticated()) {
          state.showToast('Debes iniciar sesión para acceder al panel', 'error');
          window.location.hash = '#/login';
          return;
        }
        await renderHosteleria(this.viewport);
      } else {
        this.renderNotFound();
      }
    } catch (err) {
      console.error('Routing Error:', err);
      this.viewport.innerHTML = `
        <div class="container" style="padding: var(--space-2xl) 0;">
          <div class="error-banner">
            <h2>Error de navegación</h2>
            <p>${err.message}</p>
            <a href="#/" class="btn btn-primary btn-sm" style="margin-top: var(--space-sm);">Volver al Inicio</a>
          </div>
        </div>
      `;
    }
  }

  renderNotFound() {
    this.viewport.innerHTML = `
      <div class="container" style="padding: var(--space-2xl) 0; text-align: center;">
        <div class="empty-state">
          <div class="empty-state-icon">🧭</div>
          <h2>404 - Página no encontrada</h2>
          <p>La ruta que intentas visitar no existe o ha sido movida.</p>
          <a href="#/" class="btn btn-primary" style="margin-top: var(--space-md);">Volver al Inicio</a>
        </div>
      </div>
    `;
  }
}
