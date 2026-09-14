/**
 * Entre Copas - Producer Operations Dashboard
 * Product catalog management, batch registration with analytical parameters, and unique code generation
 */

import { api } from '../api.js';
import { state } from '../state.js';

export async function renderPanel(container) {
  const user = state.getUser();
  if (!user || (!state.isProductor() && !state.isAdmin())) {
    window.location.hash = '#/login';
    return;
  }

  let products = [];
  let lots = [];
  let activeTab = 'lotes'; // 'lotes' or 'productos'

  container.innerHTML = `
    <div class="container" style="padding-top: var(--space-xl); padding-bottom: var(--space-2xl);">
      <!-- DASHBOARD HEADER -->
      <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: var(--space-xl); flex-wrap: wrap; gap: var(--space-md);">
        <div>
          <span class="badge badge-burgundy" style="margin-bottom: var(--space-xs);">PANEL DE CONTROL DEL PRODUCTOR</span>
          <h1 style="margin: 0; font-size: 2rem;">Gestión de Bodega y Trazabilidad</h1>
          <p style="color: var(--color-text-muted); margin-top: 4px;">
            Operando como <strong>${user.nombre}</strong> (${user.email})
          </p>
        </div>
        <div style="display: flex; gap: var(--space-sm); flex-wrap: wrap;">
          <a href="#/panel/financiero" class="btn btn-outline" style="font-weight: 600;">
            📊 Dashboard Financiero
          </a>
          <button id="btn-open-modal-lote" class="btn btn-secondary" style="font-weight: 600;">
            + Registrar Lote
          </button>
          <button id="btn-open-modal-producto" class="btn btn-primary">
            + Nuevo Producto
          </button>
        </div>
      </div>

      <!-- NAVIGATION TABS -->
      <div style="display: flex; gap: var(--space-sm); border-bottom: 2px solid var(--color-border); margin-bottom: var(--space-lg);">
        <button id="tab-btn-lotes" class="btn" style="border: none; border-bottom: 3px solid var(--color-burgundy); border-radius: 0; font-weight: 700; color: var(--color-burgundy); background: transparent; padding: var(--space-sm) var(--space-md);">
          🏷️ Lotes Embotellados (${lots.length})
        </button>
        <button id="tab-btn-productos" class="btn" style="border: none; border-bottom: 3px solid transparent; border-radius: 0; font-weight: 500; color: var(--color-text-muted); background: transparent; padding: var(--space-sm) var(--space-md);">
          🍷 Catálogo de Productos (${products.length})
        </button>
      </div>

      <!-- MAIN CONTENT VIEWPORT -->
      <div id="panel-tab-content">
        <div class="skeleton" style="height: 300px; border-radius: var(--radius-md);"></div>
      </div>
    </div>

    <!-- MODAL CONTAINER -->
    <div id="panel-modal-layer"></div>
  `;

  const tabContent = document.getElementById('panel-tab-content');
  const modalLayer = document.getElementById('panel-modal-layer');

  async function loadData() {
    try {
      const [prodRes, lotsRes] = await Promise.all([
        api.getProducerProducts(),
        api.getProducerLots()
      ]);
      products = prodRes.data || [];
      lots = lotsRes.data || [];

      // Update tab badge numbers
      document.getElementById('tab-btn-lotes').textContent = `🏷️ Lotes Embotellados (${lots.length})`;
      document.getElementById('tab-btn-productos').textContent = `🍷 Catálogo de Productos (${products.length})`;

      renderCurrentTab();
    } catch (err) {
      tabContent.innerHTML = `
        <div class="error-banner">
          <h3>Error al sincronizar datos del productor</h3>
          <p>${err.message}</p>
          <button id="btn-retry-panel" class="btn btn-sm btn-outline" style="margin-top: var(--space-sm);">Reintentar</button>
        </div>
      `;
      document.getElementById('btn-retry-panel')?.addEventListener('click', loadData);
    }
  }

  function renderCurrentTab() {
    if (activeTab === 'lotes') {
      renderLotsTab();
    } else {
      renderProductsTab();
    }
  }

  function renderLotsTab() {
    if (lots.length === 0) {
      tabContent.innerHTML = `
        <div class="empty-state card" style="padding: var(--space-2xl);">
          <div class="empty-state-icon">🏷️</div>
          <h3>No tienes lotes registrados aún</h3>
          <p>Registra tu primer lote con sus parámetros físico-químicos para generar su código algorítmico único.</p>
          <button id="btn-empty-lote" class="btn btn-secondary" style="margin-top: var(--space-md);">
            + Registrar Primer Lote
          </button>
        </div>
      `;
      document.getElementById('btn-empty-lote')?.addEventListener('click', openNewLotModal);
      return;
    }

    tabContent.innerHTML = `
      <div class="card" style="padding: 0; overflow-x: auto; background: #FFFFFF;">
        <table class="table" style="margin: 0; width: 100%;">
          <thead>
            <tr>
              <th>Código Único</th>
              <th>Producto</th>
              <th>Añada / Fecha</th>
              <th>Volumen</th>
              <th>pH / Alcohol</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            ${lots.map((l) => {
              const p = l.parametros_analiticos || {};
              return `
                <tr>
                  <td>
                    <span class="badge badge-burgundy" style="font-family: monospace; font-size: 0.85rem;">
                      ${l.codigo_lote}
                    </span>
                  </td>
                  <td><strong>${l.producto_nombre || 'Vino Artesanal'}</strong></td>
                  <td>${l.anio_cosecha || '2025'} <span style="font-size: 0.78rem; color: var(--color-text-muted);">(${l.fecha_elaboracion ? l.fecha_elaboracion.substring(0, 10) : ''})</span></td>
                  <td><strong>${l.volumen_producido_litros} L</strong></td>
                  <td>
                    <span style="font-size: 0.85rem;">pH ${p.ph || '3.65'} · ${p.alcohol_real_pct || '13.5'}%</span>
                  </td>
                  <td>
                    <a href="#/trazabilidad/${encodeURIComponent(l.codigo_lote)}" target="_blank" class="btn btn-outline btn-sm" style="font-size: 0.8rem;">
                      Certificado Público ↗
                    </a>
                  </td>
                </tr>
              `;
            }).join('')}
          </tbody>
        </table>
      </div>
    `;
  }

  function renderProductsTab() {
    if (products.length === 0) {
      tabContent.innerHTML = `
        <div class="empty-state card" style="padding: var(--space-2xl);">
          <div class="empty-state-icon">🍷</div>
          <h3>Aún no has registrado productos</h3>
          <p>Crea tu catálogo de bebidas artesanales para asociarles lotes de producción.</p>
          <button id="btn-empty-prod" class="btn btn-primary" style="margin-top: var(--space-md);">
            + Crear Primer Producto
          </button>
        </div>
      `;
      document.getElementById('btn-empty-prod')?.addEventListener('click', openNewProductModal);
      return;
    }

    tabContent.innerHTML = `
      <div class="card" style="padding: 0; overflow-x: auto; background: #FFFFFF;">
        <table class="table" style="margin: 0; width: 100%;">
          <thead>
            <tr>
              <th>Nombre de Bebida</th>
              <th>Categoría</th>
              <th>Graduación</th>
              <th>Estado</th>
              <th>Cambiar Estado</th>
            </tr>
          </thead>
          <tbody>
            ${products.map((p) => `
              <tr>
                <td>
                  <strong>${p.nombre}</strong>
                  <div style="font-size: 0.8rem; color: var(--color-text-muted);">${p.descripcion ? p.descripcion.substring(0, 60) + '...' : ''}</div>
                </td>
                <td><span class="badge ${p.categoria === 'Vino Tinto' ? 'badge-burgundy' : 'badge-amber'}">${p.categoria}</span></td>
                <td><strong>${p.grado_alcoholico || '13.5'}%</strong></td>
                <td>
                  <span class="badge ${p.estado === 'ACTIVO' ? 'badge-success' : (p.estado === 'PAUSADO' ? 'badge-amber' : 'badge-danger')}">
                    ${p.estado}
                  </span>
                </td>
                <td>
                  <select class="form-control change-status-select" data-id="${p.id}" style="height: 34px; font-size: 0.85rem; width: 120px;">
                    <option value="ACTIVO" ${p.estado === 'ACTIVO' ? 'selected' : ''}>ACTIVO</option>
                    <option value="PAUSADO" ${p.estado === 'PAUSADO' ? 'selected' : ''}>PAUSADO</option>
                    <option value="RETIRADO" ${p.estado === 'RETIRADO' ? 'selected' : ''}>RETIRADO</option>
                  </select>
                </td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;

    // Attach status change events
    tabContent.querySelectorAll('.change-status-select').forEach((select) => {
      select.addEventListener('change', async (e) => {
        const prodId = e.target.getAttribute('data-id');
        const newStatus = e.target.value;
        try {
          await api.updateProductStatus(prodId, newStatus);
          state.showToast(`Estado actualizado a ${newStatus}`, 'success');
          loadData();
        } catch (err) {
          state.showToast(err.message, 'error');
          loadData();
        }
      });
    });
  }

  // Tab switching UI
  document.getElementById('tab-btn-lotes')?.addEventListener('click', () => {
    activeTab = 'lotes';
    document.getElementById('tab-btn-lotes').style.borderBottom = '3px solid var(--color-burgundy)';
    document.getElementById('tab-btn-lotes').style.color = 'var(--color-burgundy)';
    document.getElementById('tab-btn-productos').style.borderBottom = '3px solid transparent';
    document.getElementById('tab-btn-productos').style.color = 'var(--color-text-muted)';
    renderCurrentTab();
  });

  document.getElementById('tab-btn-productos')?.addEventListener('click', () => {
    activeTab = 'productos';
    document.getElementById('tab-btn-productos').style.borderBottom = '3px solid var(--color-burgundy)';
    document.getElementById('tab-btn-productos').style.color = 'var(--color-burgundy)';
    document.getElementById('tab-btn-lotes').style.borderBottom = '3px solid transparent';
    document.getElementById('tab-btn-lotes').style.color = 'var(--color-text-muted)';
    renderCurrentTab();
  });

  // Modal Triggers
  document.getElementById('btn-open-modal-producto')?.addEventListener('click', openNewProductModal);
  document.getElementById('btn-open-modal-lote')?.addEventListener('click', openNewLotModal);

  // --- NEW PRODUCT MODAL ---
  function openNewProductModal() {
    modalLayer.innerHTML = `
      <div class="modal-backdrop" id="modal-product-backdrop">
        <div class="modal-dialog card" style="max-width: 500px; padding: var(--space-xl); background: #FFFFFF;">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-md);">
            <h3 style="margin: 0; font-size: 1.3rem;">Nuevo Producto Artesanal</h3>
            <button id="btn-close-modal-prod" style="background: none; border: none; font-size: 1.5rem; cursor: pointer; color: var(--color-text-muted);">×</button>
          </div>
          
          <form id="form-new-product">
            <div class="form-group" style="margin-bottom: var(--space-sm);">
              <label class="form-label">Nombre del Producto</label>
              <input type="text" id="m-prod-nombre" class="form-control" placeholder="Ej: Syrah Reserva de Montaña" required />
            </div>

            <div class="form-group" style="margin-bottom: var(--space-sm);">
              <label class="form-label">Categoría</label>
              <select id="m-prod-categoria" class="form-control" required>
                <option value="Vino Tinto">Vino Tinto</option>
                <option value="Vino Blanco">Vino Blanco</option>
                <option value="Cerveza Artesanal">Cerveza Artesanal</option>
                <option value="Hidromiel">Hidromiel</option>
              </select>
            </div>

            <div class="form-group" style="margin-bottom: var(--space-sm);">
              <label class="form-label">Graduación Alcohólica (% ABV)</label>
              <input type="number" id="m-prod-alcohol" class="form-control" step="0.1" min="0" max="100" placeholder="13.5" required />
            </div>

            <div class="form-group" style="margin-bottom: var(--space-md);">
              <label class="form-label">Notas de Cata y Descripción</label>
              <textarea id="m-prod-desc" class="form-control" rows="3" placeholder="Descripción sensorial, terroir, uvas o lúpulos..." required></textarea>
            </div>

            <div style="display: flex; justify-content: flex-end; gap: var(--space-sm);">
              <button type="button" id="btn-cancel-modal-prod" class="btn btn-outline">Cancelar</button>
              <button type="submit" id="btn-save-prod" class="btn btn-primary">Guardar Producto</button>
            </div>
          </form>
        </div>
      </div>
    `;

    const close = () => { modalLayer.innerHTML = ''; };
    document.getElementById('btn-close-modal-prod')?.addEventListener('click', close);
    document.getElementById('btn-cancel-modal-prod')?.addEventListener('click', close);

    document.getElementById('form-new-product')?.addEventListener('submit', async (e) => {
      e.preventDefault();
      const saveBtn = document.getElementById('btn-save-prod');
      saveBtn.disabled = true;
      saveBtn.textContent = 'Guardando...';

      try {
        await api.createProduct({
          nombre: document.getElementById('m-prod-nombre').value.trim(),
          categoria: document.getElementById('m-prod-categoria').value,
          grado_alcoholico: parseFloat(document.getElementById('m-prod-alcohol').value),
          descripcion: document.getElementById('m-prod-desc').value.trim()
        });

        state.showToast('Producto creado con éxito', 'success');
        close();
        activeTab = 'productos';
        loadData();
      } catch (err) {
        state.showToast(err.message, 'error');
        saveBtn.disabled = false;
        saveBtn.textContent = 'Guardar Producto';
      }
    });
  }

  // --- NEW LOT MODAL ---
  function openNewLotModal() {
    if (products.length === 0) {
      state.showToast('Primero debes registrar al menos un producto', 'error');
      openNewProductModal();
      return;
    }

    modalLayer.innerHTML = `
      <div class="modal-backdrop" id="modal-lot-backdrop">
        <div class="modal-dialog card" style="max-width: 650px; max-height: 90vh; overflow-y: auto; padding: var(--space-xl); background: #FFFFFF;">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-md);">
            <div>
              <span class="badge badge-amber" style="margin-bottom: 2px;">ALGORITMO DE TRAZABILIDAD</span>
              <h3 style="margin: 0; font-size: 1.3rem;">Registrar Lote con Parámetros Analíticos</h3>
            </div>
            <button id="btn-close-modal-lot" style="background: none; border: none; font-size: 1.5rem; cursor: pointer; color: var(--color-text-muted);">×</button>
          </div>

          <form id="form-new-lot">
            <!-- Basic Batch Info -->
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-sm); margin-bottom: var(--space-sm);">
              <div class="form-group" style="grid-column: 1 / -1;">
                <label class="form-label">Producto Asociado</label>
                <select id="m-lot-producto" class="form-control" required>
                  ${products.map((p) => `<option value="${p.id}">${p.nombre} (${p.categoria})</option>`).join('')}
                </select>
              </div>

              <div class="form-group">
                <label class="form-label">Año Cosecha / Vendimia</label>
                <input type="number" id="m-lot-anio" class="form-control" value="2025" min="2000" max="2035" required />
              </div>

              <div class="form-group">
                <label class="form-label">Fecha de Embotellado</label>
                <input type="date" id="m-lot-fecha" class="form-control" value="${new Date().toISOString().substring(0, 10)}" required />
              </div>

              <div class="form-group">
                <label class="form-label">Volumen Producido (Litros)</label>
                <input type="number" id="m-lot-volumen" class="form-control" placeholder="Ej: 1200" min="1" step="0.1" required />
              </div>

              <div class="form-group">
                <label class="form-label">Merma en Producción (Litros)</label>
                <input type="number" id="m-lot-merma" class="form-control" placeholder="Ej: 45" min="0" step="0.1" value="0" required />
              </div>

              <div class="form-group" style="grid-column: 1 / -1;">
                <label class="form-label">Costo Total de Producción del Lote ($ COP/USD)</label>
                <input type="number" id="m-lot-costo" class="form-control" placeholder="Ej: 8500000" min="0" step="100" required />
                <span style="font-size: 0.75rem; color: var(--color-text-muted);">Confidencial: este valor jamás se expone en la trazabilidad pública.</span>
              </div>
            </div>

            <!-- Analytical Chemical Parameters (JSON) -->
            <div style="background: var(--color-pergamino); padding: var(--space-md); border-radius: var(--radius-sm); margin-bottom: var(--space-md); border: 1px solid var(--color-border);">
              <h4 style="margin-top: 0; margin-bottom: var(--space-xs); color: var(--color-burgundy); font-size: 0.95rem;">
                🔬 Certificación Analítica (Laboratorio)
              </h4>
              <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: var(--space-xs);">
                <div class="form-group">
                  <label class="form-label" style="font-size: 0.78rem;">pH (ej: 3.55 - 3.80)</label>
                  <input type="number" id="m-lot-ph" class="form-control" step="0.01" value="3.65" required />
                </div>
                <div class="form-group">
                  <label class="form-label" style="font-size: 0.78rem;">Alcohol Real (% ABV)</label>
                  <input type="number" id="m-lot-alcohol-real" class="form-control" step="0.1" value="13.5" required />
                </div>
                <div class="form-group">
                  <label class="form-label" style="font-size: 0.78rem;">Acidez Total (g/L ác. tartárico)</label>
                  <input type="number" id="m-lot-acidez" class="form-control" step="0.1" value="5.4" required />
                </div>
                <div class="form-group">
                  <label class="form-label" style="font-size: 0.78rem;">Densidad Final (g/ml)</label>
                  <input type="number" id="m-lot-densidad" class="form-control" step="0.001" value="0.992" required />
                </div>
                <div class="form-group" style="grid-column: 1 / -1;">
                  <label class="form-label" style="font-size: 0.78rem;">Crianza en Barrica (Meses - Opcional)</label>
                  <input type="number" id="m-lot-crianza" class="form-control" placeholder="Ej: 12" min="0" value="0" />
                </div>
              </div>
            </div>

            <div style="display: flex; justify-content: flex-end; gap: var(--space-sm);">
              <button type="button" id="btn-cancel-modal-lot" class="btn btn-outline">Cancelar</button>
              <button type="submit" id="btn-save-lot" class="btn btn-secondary" style="font-weight: 600;">
                Generar Código y Guardar
              </button>
            </div>
          </form>
        </div>
      </div>
    `;

    const close = () => { modalLayer.innerHTML = ''; };
    document.getElementById('btn-close-modal-lot')?.addEventListener('click', close);
    document.getElementById('btn-cancel-modal-lot')?.addEventListener('click', close);

    document.getElementById('form-new-lot')?.addEventListener('submit', async (e) => {
      e.preventDefault();
      const saveBtn = document.getElementById('btn-save-lot');
      saveBtn.disabled = true;
      saveBtn.textContent = 'Calculando código...';

      try {
        const payload = {
          producto_id: parseInt(document.getElementById('m-lot-producto').value),
          anio_cosecha: parseInt(document.getElementById('m-lot-anio').value),
          fecha_elaboracion: document.getElementById('m-lot-fecha').value,
          volumen_producido_litros: parseFloat(document.getElementById('m-lot-volumen').value),
          merma_litros: parseFloat(document.getElementById('m-lot-merma').value || 0),
          costo_total: parseFloat(document.getElementById('m-lot-costo').value),
          parametros_analiticos: {
            ph: parseFloat(document.getElementById('m-lot-ph').value),
            alcohol_real_pct: parseFloat(document.getElementById('m-lot-alcohol-real').value),
            acidez_total_g_l: parseFloat(document.getElementById('m-lot-acidez').value),
            densidad_final: parseFloat(document.getElementById('m-lot-densidad').value),
            crianza_meses: parseInt(document.getElementById('m-lot-crianza').value || 0)
          }
        };

        const res = await api.createLot(payload);
        const newLot = res.data;

        // Display celebration code modal!
        showCelebrationModal(newLot);
        activeTab = 'lotes';
        loadData();
      } catch (err) {
        state.showToast(err.message, 'error');
        saveBtn.disabled = false;
        saveBtn.textContent = 'Generar Código y Guardar';
      }
    });
  }

  function showCelebrationModal(lot) {
    modalLayer.innerHTML = `
      <div class="modal-backdrop">
        <div class="modal-dialog card" style="max-width: 500px; text-align: center; padding: var(--space-2xl); background: #FFFFFF;">
          <div style="font-size: 3rem; margin-bottom: var(--space-xs);">🎉</div>
          <span class="badge badge-success" style="margin-bottom: var(--space-xs);">LOTE GENERADO EXITOSAMENTE</span>
          <h3 style="margin-top: 0; font-size: 1.4rem;">Código de Trazabilidad Asignado</h3>
          <p style="color: var(--color-text-muted); font-size: 0.9rem;">
            El algoritmo ha emitido un código criptográfico único según el estándar ISO 22005.
          </p>

          <div style="background: var(--color-pergamino); border: 2px dashed var(--color-burgundy); padding: var(--space-md); border-radius: var(--radius-sm); margin: var(--space-lg) 0;">
            <div style="font-size: 0.8rem; text-transform: uppercase; color: var(--color-text-muted); margin-bottom: 4px;">Código de Lote</div>
            <div style="font-family: monospace; font-size: 1.8rem; font-weight: 700; color: var(--color-burgundy); letter-spacing: 0.08em;">
              ${lot.codigo_lote}
            </div>
          </div>

          <div style="display: flex; gap: var(--space-sm); justify-content: center;">
            <a href="#/trazabilidad/${encodeURIComponent(lot.codigo_lote)}" target="_blank" class="btn btn-secondary" style="font-weight: 600;">
              🔍 Ver Certificado Público
            </a>
            <button id="btn-done-celebration" class="btn btn-primary">
              Cerrar y Volver
            </button>
          </div>
        </div>
      </div>
    `;

    document.getElementById('btn-done-celebration')?.addEventListener('click', () => {
      modalLayer.innerHTML = '';
    });
  }

  // Initial load
  loadData();
}
