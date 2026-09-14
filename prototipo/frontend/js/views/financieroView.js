/**
 * Entre Copas - Financial & Waste Analytics View
 * Aggregated cost intelligence, production volume, shrinkage/merma analysis, and unit economics
 */

import { api } from '../api.js';
import { state } from '../state.js';

export async function renderFinanciero(container) {
  const user = state.getUser();
  if (!user || (!state.isProductor() && !state.isAdmin())) {
    window.location.hash = '#/login';
    return;
  }

  container.innerHTML = `
    <div class="container" style="padding-top: var(--space-xl); padding-bottom: var(--space-2xl);">
      <!-- HEADER -->
      <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: var(--space-xl); flex-wrap: wrap; gap: var(--space-md);">
        <div>
          <span class="badge badge-amber" style="margin-bottom: var(--space-xs);">MÓDULO DE COSTOS Y MERMAS</span>
          <h1 style="margin: 0; font-size: 2rem;">Dashboard Financiero de Producción</h1>
          <p style="color: var(--color-text-muted); margin-top: 4px;">
            Análisis de costos agregados, mermas y rendimientos operativos para <strong>${user.nombre}</strong>
          </p>
        </div>
        <div>
          <a href="#/panel" class="btn btn-outline">← Volver a Gestión de Lotes</a>
        </div>
      </div>

      <!-- VIEWPORT FOR FINANCIAL METRICS -->
      <div id="financial-content">
        <!-- Skeleton Loading -->
        <div class="grid grid-4" style="margin-bottom: var(--space-xl);">
          <div class="skeleton" style="height: 120px; border-radius: var(--radius-md);"></div>
          <div class="skeleton" style="height: 120px; border-radius: var(--radius-md);"></div>
          <div class="skeleton" style="height: 120px; border-radius: var(--radius-md);"></div>
          <div class="skeleton" style="height: 120px; border-radius: var(--radius-md);"></div>
        </div>
        <div class="skeleton" style="height: 320px; border-radius: var(--radius-md);"></div>
      </div>
    </div>
  `;

  const contentBox = document.getElementById('financial-content');

  try {
    const res = await api.getFinancialSummary();
    const data = res.data;

    const kpis = data.kpis;
    const desglose = data.desglose_por_lote || [];

    contentBox.innerHTML = `
      <!-- 4 KPI METRICS CARDS -->
      <div class="grid grid-4" style="gap: var(--space-md); margin-bottom: var(--space-xl);">
        <!-- Total Inversión -->
        <div class="card card-elevated" style="padding: var(--space-lg); border-left: 4px solid var(--color-burgundy); background: #FFFFFF;">
          <div style="font-size: 0.78rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 600;">Inversión Total en Lotes</div>
          <div style="font-size: 1.8rem; font-weight: 700; color: var(--color-burgundy); margin: var(--space-2xs) 0;">
            $${Number(kpis.costo_total_acumulado).toLocaleString('es-CO')}
          </div>
          <div style="font-size: 0.78rem; color: var(--color-text-muted);">Acumulado en ${desglose.length} lotes</div>
        </div>

        <!-- Volumen Total -->
        <div class="card card-elevated" style="padding: var(--space-lg); border-left: 4px solid var(--color-amber); background: #FFFFFF;">
          <div style="font-size: 0.78rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 600;">Volumen Producido</div>
          <div style="font-size: 1.8rem; font-weight: 700; color: var(--color-amber-dark); margin: var(--space-2xs) 0;">
            ${Number(kpis.volumen_total_litros).toLocaleString()} <span style="font-size: 1rem;">L</span>
          </div>
          <div style="font-size: 0.78rem; color: var(--color-text-muted);">Bebida embotellada lista</div>
        </div>

        <!-- Merma Total -->
        <div class="card card-elevated" style="padding: var(--space-lg); border-left: 4px solid #DC2626; background: #FFFFFF;">
          <div style="font-size: 0.78rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 600;">Merma en Producción</div>
          <div style="font-size: 1.8rem; font-weight: 700; color: #DC2626; margin: var(--space-2xs) 0;">
            ${Number(kpis.merma_total_litros).toLocaleString()} <span style="font-size: 1rem;">L</span>
          </div>
          <div style="font-size: 0.78rem; color: #DC2626; font-weight: 500;">
            Tasa de merma: ${kpis.porcentaje_merma_global}%
          </div>
        </div>

        <!-- Costo por Litro -->
        <div class="card card-elevated" style="padding: var(--space-lg); border-left: 4px solid var(--color-burgundy); background: #FFFFFF;">
          <div style="font-size: 0.78rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 600;">Costo Promedio / Litro</div>
          <div style="font-size: 1.8rem; font-weight: 700; color: var(--color-burgundy); margin: var(--space-2xs) 0;">
            $${Number(kpis.costo_promedio_por_litro).toLocaleString('es-CO')}
          </div>
          <div style="font-size: 0.78rem; color: var(--color-text-muted);">Base para fijación de precios</div>
        </div>
      </div>

      <!-- DETAILED BREAKDOWN TABLE -->
      <div class="card" style="padding: var(--space-lg); background: #FFFFFF; margin-bottom: var(--space-xl);">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-md); flex-wrap: wrap; gap: var(--space-sm);">
          <div>
            <h3 style="margin: 0; font-size: 1.25rem;">Desglose de Costos y Mermas por Lote</h3>
            <p style="margin: 0; font-size: 0.85rem; color: var(--color-text-muted);">Auditoría interna de rendimientos de elaboración</p>
          </div>
          <span class="badge badge-burgundy">Aislamiento Multi-Tenant Certificado</span>
        </div>

        ${desglose.length === 0 ? `
          <div class="empty-state">
            <p>No se registran lotes en el período seleccionado.</p>
          </div>
        ` : `
          <div style="overflow-x: auto;">
            <table class="table" style="width: 100%; margin: 0;">
              <thead>
                <tr>
                  <th>Código Lote</th>
                  <th>Producto</th>
                  <th>Volumen</th>
                  <th>Merma</th>
                  <th>% Merma</th>
                  <th>Costo Total</th>
                  <th>Costo / L</th>
                </tr>
              </thead>
              <tbody>
                ${desglose.map((l) => {
                  const pct = l.porcentaje_merma || '0.00';
                  const unitCost = l.volumen_producido_litros > 0 ? (l.costo_total / l.volumen_producido_litros).toFixed(2) : '0.00';
                  return `
                    <tr>
                      <td>
                        <span class="badge badge-burgundy" style="font-family: monospace; font-size: 0.85rem;">
                          ${l.codigo_lote}
                        </span>
                      </td>
                      <td><strong>${l.producto_nombre || 'Vino Artesanal'}</strong></td>
                      <td>${Number(l.volumen_producido_litros).toLocaleString()} L</td>
                      <td style="color: #DC2626;">${Number(l.merma_litros).toLocaleString()} L</td>
                      <td>
                        <span class="badge ${parseFloat(pct) > 5 ? 'badge-amber' : 'badge-success'}" style="font-size: 0.75rem;">
                          ${pct}%
                        </span>
                      </td>
                      <td><strong>$${Number(l.costo_total).toLocaleString('es-CO')}</strong></td>
                      <td style="color: var(--color-burgundy); font-weight: 600;">$${Number(unitCost).toLocaleString('es-CO')}</td>
                    </tr>
                  `;
                }).join('')}
              </tbody>
            </table>
          </div>
        `}
      </div>

      <!-- ADVISORY BANNER -->
      <div style="background: rgba(88, 17, 26, 0.05); border: 1px solid rgba(88, 17, 26, 0.15); border-radius: var(--radius-md); padding: var(--space-md); font-size: 0.85rem; color: var(--color-text);">
        💡 <strong>Recomendación Enológica & Financiera:</strong> Una tasa de merma superior al 6% en fermentación indica necesidad de ajuste en prensas o evaporación de barrica. Mantener el costo unitario por litro actualizado te permite negociar márgenes saludables con hostelería (márgenes sugeridos: 45%-60%).
      </div>
    `;
  } catch (err) {
    contentBox.innerHTML = `
      <div class="error-banner">
        <h3>Error al obtener métricas financieras</h3>
        <p>${err.message}</p>
        <button id="btn-retry-financial" class="btn btn-outline btn-sm" style="margin-top: var(--space-sm);">Reintentar</button>
      </div>
    `;
    document.getElementById('btn-retry-financial')?.addEventListener('click', () => renderFinanciero(container));
  }
}
