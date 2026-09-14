/**
 * Entre Copas - Product Detail View
 * Complete bottle profile, producer info, batch links, and real-time establishment availability
 */

import { api } from '../api.js';

export async function renderProducto(container, params) {
  const productId = params.id;

  container.innerHTML = `
    <div class="container" style="padding-top: var(--space-xl); padding-bottom: var(--space-2xl);">
      <div id="product-detail-container">
        <!-- Skeleton Loading -->
        <div style="display: grid; grid-template-columns: 1fr 2fr; gap: var(--space-xl);">
          <div class="skeleton" style="height: 480px; border-radius: var(--radius-md);"></div>
          <div>
            <div class="skeleton" style="height: 40px; width: 60%; margin-bottom: var(--space-md);"></div>
            <div class="skeleton" style="height: 20px; width: 40%; margin-bottom: var(--space-lg);"></div>
            <div class="skeleton" style="height: 120px; width: 100%; margin-bottom: var(--space-lg);"></div>
            <div class="skeleton" style="height: 160px; width: 100%;"></div>
          </div>
        </div>
      </div>
    </div>
  `;

  const detailBox = document.getElementById('product-detail-container');

  try {
    const res = await api.getProductById(productId);
    const p = res.data;

    if (!p) {
      detailBox.innerHTML = `
        <div class="empty-state">
          <div class="empty-state-icon">🍾</div>
          <h2>Bebida no encontrada</h2>
          <p>El producto solicitado no existe o fue retirado del catálogo activo.</p>
          <a href="#/catalogo" class="btn btn-primary" style="margin-top: var(--space-md);">Volver al Catálogo</a>
        </div>
      `;
      return;
    }

    const categoryBadge = p.categoria === 'Vino Tinto' ? 'badge-burgundy' : 'badge-amber';

    // Render Establishments List
    const establecimientos = p.establecimientos || [];
    let establecimientosHtml = '';
    if (establecimientos.length === 0) {
      establecimientosHtml = `
        <div class="empty-state" style="padding: var(--space-lg); border: 1px dashed var(--color-border);">
          <p style="margin: 0; color: var(--color-text-muted);">
            Esta bebida no cuenta con locales gastronómicos asociados activos en este momento. Pregunta directamente en bodega.
          </p>
        </div>
      `;
    } else {
      establecimientosHtml = `
        <div style="display: flex; flex-direction: column; gap: var(--space-sm);">
          ${establecimientos.map((e) => `
            <div class="card" style="padding: var(--space-md); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: var(--space-sm);">
              <div>
                <div style="font-weight: 600; font-size: 1.05rem; color: var(--color-burgundy);">${e.nombre_comercial}</div>
                <div style="font-size: 0.85rem; color: var(--color-text-muted);">${e.direccion || 'Ubicación céntrica'} · ${e.telefono || 'Sin teléfono'}</div>
              </div>
              <div style="text-align: right;">
                ${e.precio_copa ? `<span style="font-size: 0.9rem; font-weight: 600; display: block; color: var(--color-burgundy);">Copa: $${Number(e.precio_copa).toLocaleString()}</span>` : ''}
                ${e.precio_botella ? `<span style="font-size: 0.9rem; font-weight: 600; display: block; color: var(--color-text);">Botella: $${Number(e.precio_botella).toLocaleString()}</span>` : ''}
                <span class="badge badge-success" style="font-size: 0.72rem; margin-top: 4px;">En Carta</span>
              </div>
            </div>
          `).join('')}
        </div>
      `;
    }

    // Render Lots List
    const lotes = p.lotes || [];
    let lotesHtml = '';
    if (lotes.length === 0) {
      lotesHtml = `
        <div class="empty-state" style="padding: var(--space-md);">
          <p style="margin: 0; color: var(--color-text-muted);">No hay lotes embotellados registrados para esta referencia.</p>
        </div>
      `;
    } else {
      lotesHtml = `
        <div style="display: flex; flex-direction: column; gap: var(--space-sm);">
          ${lotes.map((lote) => `
            <div class="card" style="padding: var(--space-md); border-left: 4px solid var(--color-burgundy); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: var(--space-md);">
              <div>
                <div style="display: flex; align-items: center; gap: var(--space-xs); margin-bottom: 4px;">
                  <span class="badge badge-burgundy" style="font-family: monospace; font-size: 0.85rem;">${lote.codigo_lote}</span>
                  <span style="font-size: 0.82rem; color: var(--color-text-muted);">Añada ${lote.anio_cosecha || new Date(lote.fecha_elaboracion).getFullYear()}</span>
                </div>
                <div style="font-size: 0.88rem; color: var(--color-text);">
                  Volumen embotellado: <strong>${lote.volumen_producido_litros} L</strong>
                </div>
              </div>
              <div>
                <a href="#/trazabilidad/${encodeURIComponent(lote.codigo_lote)}" class="btn btn-secondary btn-sm" style="font-weight: 600;">
                  🔍 Ver Trazabilidad
                </a>
              </div>
            </div>
          `).join('')}
        </div>
      `;
    }

    detailBox.innerHTML = `
      <div style="margin-bottom: var(--space-lg);">
        <a href="#/catalogo" style="color: var(--color-burgundy); font-weight: 500; text-decoration: none;">← Volver al Catálogo</a>
      </div>

      <div style="display: grid; grid-template-columns: 1fr 2fr; gap: var(--space-xl); margin-bottom: var(--space-2xl);" class="product-layout-grid">
        <!-- Visual Column -->
        <div>
          <div class="card" style="background: var(--color-pergamino-dark); height: 420px; display: flex; flex-direction: column; align-items: center; justify-content: center; position: relative; border: 1px solid var(--color-border);">
            <span style="font-size: 7rem; margin-bottom: var(--space-md);">🍾</span>
            <span class="badge ${categoryBadge}" style="font-size: 0.9rem; padding: 6px 14px;">${p.categoria}</span>
            <div style="margin-top: var(--space-md); font-size: 0.85rem; color: var(--color-text-muted);">Lote Artesanal Certificado</div>
          </div>

          <!-- Producer Card -->
          <div class="card" style="margin-top: var(--space-lg); padding: var(--space-md);">
            <div style="font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.05em; color: var(--color-text-muted); font-weight: 600;">Elaborador / Bodega</div>
            <h4 style="margin: var(--space-2xs) 0; font-size: 1.1rem; color: var(--color-burgundy);">${p.productor_nombre || 'Productor Artesanal'}</h4>
            <p style="font-size: 0.85rem; color: var(--color-text-muted); margin-bottom: var(--space-xs);">
              ${p.productor_region ? `📍 ${p.productor_region}` : 'Origen Noble'}
            </p>
            ${p.productor_bio ? `<p style="font-size: 0.85rem; line-height: 1.4; color: var(--color-text);">${p.productor_bio}</p>` : ''}
          </div>
        </div>

        <!-- Info Column -->
        <div>
          <span class="badge ${categoryBadge}" style="margin-bottom: var(--space-xs);">${p.categoria}</span>
          <h1 style="font-size: 2.2rem; margin-bottom: var(--space-xs);">${p.nombre}</h1>
          <div style="font-size: 1.05rem; color: var(--color-text-muted); margin-bottom: var(--space-lg);">
            Graduación Alcohólica: <strong style="color: var(--color-text);">${p.grado_alcoholico || '13.5'}% ABV</strong>
          </div>

          <!-- Description -->
          <div style="margin-bottom: var(--space-xl);">
            <h3 style="font-size: 1.15rem; margin-bottom: var(--space-xs); border-bottom: 1px solid var(--color-border); padding-bottom: 4px;">Notas de Cata y Proceso</h3>
            <p style="color: var(--color-text); line-height: 1.6; font-size: 0.98rem;">
              ${p.descripcion || 'Esta bebida artesanal ha sido concebida con materias primas seleccionadas a mano, fermentación controlada y reposo natural sin aditivos químicos artificiales.'}
            </p>
          </div>

          <!-- Lotes Registrados -->
          <div style="margin-bottom: var(--space-xl);">
            <h3 style="font-size: 1.15rem; margin-bottom: var(--space-xs); border-bottom: 1px solid var(--color-border); padding-bottom: 4px;">Lotes Registrados y Trazabilidad</h3>
            <p style="font-size: 0.85rem; color: var(--color-text-muted); margin-bottom: var(--space-sm);">Cada lote cuenta con su certificado analítico público verificable.</p>
            ${lotesHtml}
          </div>

          <!-- Dónde Degustar (Hostelería) -->
          <div>
            <h3 style="font-size: 1.15rem; margin-bottom: var(--space-xs); border-bottom: 1px solid var(--color-border); padding-bottom: 4px;">Dónde Degustar (Hostelería y Bares)</h3>
            <p style="font-size: 0.85rem; color: var(--color-text-muted); margin-bottom: var(--space-sm);">Locales que ofrecen esta referencia en su carta actualmente.</p>
            ${establecimientosHtml}
          </div>
        </div>
      </div>
    `;
  } catch (err) {
    detailBox.innerHTML = `
      <div class="error-banner">
        <h3>Error al consultar la ficha del producto</h3>
        <p>${err.message}</p>
        <a href="#/catalogo" class="btn btn-outline btn-sm" style="margin-top: var(--space-sm);">Volver al Catálogo</a>
      </div>
    `;
  }
}
