/**
 * Entre Copas - Hospitality & Bar Management View
 * Real-time stock availability toggle switches (44px touch target) and by-the-glass / bottle pricing
 */

import { api } from '../api.js';
import { state } from '../state.js';

export async function renderHosteleria(container) {
  const user = state.getUser();
  if (!user || (!state.isHosteleria() && !state.isAdmin())) {
    window.location.hash = '#/login';
    return;
  }

  container.innerHTML = `
    <div class="container" style="padding-top: var(--space-xl); padding-bottom: var(--space-2xl);">
      <!-- HEADER -->
      <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: var(--space-xl); flex-wrap: wrap; gap: var(--space-md);">
        <div>
          <span class="badge badge-amber" style="margin-bottom: var(--space-xs);">PANEL DE HOSTELERÍA Y RESTAURACIÓN</span>
          <h1 style="margin: 0; font-size: 2rem;">Carta y Disponibilidad en Barra</h1>
          <p style="color: var(--color-text-muted); margin-top: 4px;">
            Gestiona qué bebidas artesanales tienes en stock para informar a los consumidores en tiempo real.
          </p>
        </div>
        <div style="background: var(--color-pergamino-dark); padding: var(--space-xs) var(--space-md); border-radius: var(--radius-sm); font-size: 0.88rem;">
          🍽️ Local: <strong>${user.nombre}</strong>
        </div>
      </div>

      <!-- INSTRUCTION BANNER -->
      <div style="background: #FFFFFF; border-left: 4px solid var(--color-burgundy); padding: var(--space-md); border-radius: var(--radius-sm); margin-bottom: var(--space-xl); box-shadow: var(--shadow-sm);">
        <h4 style="margin: 0 0 4px 0; color: var(--color-burgundy); font-size: 0.95rem;">¿Cómo funciona la disponibilidad pública?</h4>
        <p style="margin: 0; font-size: 0.88rem; color: var(--color-text-muted);">
          Al activar el interruptor de una bebida e indicar tus precios de servicio (por copa o botella), tu local aparecerá automáticamente en la ficha pública del producto para que los amantes de las bebidas artesanales puedan visitarte.
        </p>
      </div>

      <!-- CATALOG AVAILABILITY CONTAINER -->
      <div id="hosteleria-items-container">
        <div class="skeleton" style="height: 350px; border-radius: var(--radius-md);"></div>
      </div>
    </div>
  `;

  const itemsContainer = document.getElementById('hosteleria-items-container');

  async function loadCatalog() {
    try {
      const res = await api.getHosteleriaCatalog();
      const items = res.data || [];

      if (items.length === 0) {
        itemsContainer.innerHTML = `
          <div class="empty-state card" style="padding: var(--space-2xl);">
            <div class="empty-state-icon">🍷</div>
            <h3>No hay productos artesanales disponibles en la plataforma</h3>
            <p>Los productores aún no han publicado bebidas activas en el catálogo general.</p>
          </div>
        `;
        return;
      }

      itemsContainer.innerHTML = `
        <div class="card" style="padding: 0; overflow-x: auto; background: #FFFFFF;">
          <table class="table" style="margin: 0; width: 100%;">
            <thead>
              <tr>
                <th style="min-width: 200px;">Bebida Artesanal</th>
                <th>Bodega / Elaborador</th>
                <th>En Carta (Activo)</th>
                <th>Precio Copa ($)</th>
                <th>Precio Botella ($)</th>
                <th>Acción</th>
              </tr>
            </thead>
            <tbody>
              ${items.map((item) => {
                const isDispo = Boolean(item.disponible);
                const cat = item.categoria || item.tipo_bebida || 'Bebida Artesanal';
                const catBadge = cat.toLowerCase().includes('tinto') ? 'badge-burgundy' : 'badge-amber';
                return `
                  <tr id="row-prod-${item.producto_id}">
                    <td>
                      <strong>${item.nombre}</strong>
                      <div style="font-size: 0.78rem;">
                        <span class="badge ${catBadge}">${cat}</span>
                        <span style="color: var(--color-text-muted); margin-left: 4px;">${item.grado_alcoholico || '13.5'}% ABV</span>
                      </div>
                    </td>
                    <td>
                      <span style="font-weight: 500;">${item.productor_nombre || 'Bodega'}</span>
                      <div style="font-size: 0.75rem; color: var(--color-text-muted);">${item.productor_region || 'Origen Certificado'}</div>
                    </td>
                    <td>
                      <label class="switch-container" style="display: flex; align-items: center; gap: var(--space-xs); cursor: pointer;">
                        <input 
                          type="checkbox" 
                          class="stock-toggle-input" 
                          data-id="${item.producto_id}" 
                          ${isDispo ? 'checked' : ''} 
                          style="width: 24px; height: 24px; cursor: pointer;"
                        />
                        <span class="stock-status-label" style="font-size: 0.85rem; font-weight: 600; color: ${isDispo ? '#15803D' : 'var(--color-text-muted)'};">
                          ${isDispo ? 'Disponible' : 'Agotado / No'}
                        </span>
                      </label>
                    </td>
                    <td>
                      <input 
                        type="number" 
                        id="precio-copa-${item.producto_id}" 
                        class="form-control" 
                        placeholder="Ej: 14000" 
                        value="${item.precio_copa || ''}" 
                        min="0" 
                        step="500" 
                        style="width: 120px; height: 36px; font-size: 0.88rem;"
                      />
                    </td>
                    <td>
                      <input 
                        type="number" 
                        id="precio-botella-${item.producto_id}" 
                        class="form-control" 
                        placeholder="Ej: 65000" 
                        value="${item.precio_botella || ''}" 
                        min="0" 
                        step="500" 
                        style="width: 120px; height: 36px; font-size: 0.88rem;"
                      />
                    </td>
                    <td>
                      <button class="btn btn-sm btn-outline btn-save-stock" data-id="${item.producto_id}" style="font-weight: 600;">
                        Guardar
                      </button>
                    </td>
                  </tr>
                `;
              }).join('')}
            </tbody>
          </table>
        </div>
      `;

      attachRowEvents();
    } catch (err) {
      itemsContainer.innerHTML = `
        <div class="error-banner">
          <h3>Error al cargar carta de hostelería</h3>
          <p>${err.message}</p>
          <button id="btn-retry-hosteleria" class="btn btn-outline btn-sm" style="margin-top: var(--space-sm);">Reintentar</button>
        </div>
      `;
      document.getElementById('btn-retry-hosteleria')?.addEventListener('click', loadCatalog);
    }
  }

  function attachRowEvents() {
    // Toggle checkbox instant change
    itemsContainer.querySelectorAll('.stock-toggle-input').forEach((chk) => {
      chk.addEventListener('change', (e) => {
        const row = e.target.closest('tr');
        const label = row.querySelector('.stock-status-label');
        if (e.target.checked) {
          label.textContent = 'Disponible';
          label.style.color = '#15803D';
        } else {
          label.textContent = 'Agotado / No';
          label.style.color = 'var(--color-text-muted)';
        }
      });
    });

    // Save button click
    itemsContainer.querySelectorAll('.btn-save-stock').forEach((btn) => {
      btn.addEventListener('click', async (e) => {
        const prodId = e.target.getAttribute('data-id');
        const row = document.getElementById(`row-prod-${prodId}`);
        const isChecked = row.querySelector('.stock-toggle-input').checked;
        const copaInput = document.getElementById(`precio-copa-${prodId}`);
        const botellaInput = document.getElementById(`precio-botella-${prodId}`);

        const precioCopa = copaInput.value ? parseFloat(copaInput.value) : null;
        const precioBotella = botellaInput.value ? parseFloat(botellaInput.value) : null;

        btn.disabled = true;
        btn.textContent = 'Guardando...';

        try {
          await api.toggleDisponibilidad(prodId, isChecked, precioCopa, precioBotella);
          state.showToast('Disponibilidad actualizada en tu carta pública', 'success');
          btn.disabled = false;
          btn.textContent = '✓ Guardado';
          setTimeout(() => { btn.textContent = 'Guardar'; }, 2000);
        } catch (err) {
          state.showToast(err.message, 'error');
          btn.disabled = false;
          btn.textContent = 'Guardar';
        }
      });
    });
  }

  loadCatalog();
}
