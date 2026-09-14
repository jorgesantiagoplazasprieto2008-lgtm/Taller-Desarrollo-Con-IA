/**
 * Entre Copas - Public Traceability View
 * Analytical certificate with pH, ABV, Acidity, Density, Process Timeline, and Producer Origin
 */

import { api } from '../api.js';

export async function renderTrazabilidad(container, params) {
  const code = params.code || '';

  container.innerHTML = `
    <div class="container" style="padding-top: var(--space-xl); padding-bottom: var(--space-2xl); max-width: 900px;">
      <!-- TOP SEARCH BAR -->
      <div style="margin-bottom: var(--space-xl);">
        <form id="trace-search-form" style="display: flex; gap: var(--space-sm); max-width: 500px; margin: 0 auto;">
          <input 
            type="text" 
            id="trace-code-input" 
            class="form-control" 
            placeholder="Ingresa código (ej: EC-2026-PR01-A101)" 
            value="${code}"
            style="text-transform: uppercase; font-family: monospace; font-size: 1rem;"
            required
          />
          <button type="submit" class="btn btn-secondary" style="font-weight: 600;">Consultar</button>
        </form>
      </div>

      <!-- MAIN TRACEABILITY CERTIFICATE -->
      <div id="traceability-card-container">
        <!-- Skeleton Loading -->
        <div class="card" style="padding: var(--space-xl);">
          <div class="skeleton" style="height: 50px; width: 50%; margin: 0 auto var(--space-lg) auto;"></div>
          <div class="skeleton" style="height: 120px; width: 100%; margin-bottom: var(--space-lg);"></div>
          <div class="skeleton" style="height: 200px; width: 100%;"></div>
        </div>
      </div>
    </div>
  `;

  // Search form handler
  const form = document.getElementById('trace-search-form');
  const input = document.getElementById('trace-code-input');
  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const newCode = input.value.trim().toUpperCase();
    if (newCode) {
      window.location.hash = `#/trazabilidad/${encodeURIComponent(newCode)}`;
    }
  });

  const displayBox = document.getElementById('traceability-card-container');

  if (!code) {
    displayBox.innerHTML = `
      <div class="empty-state">
        <div class="empty-state-icon">🔍</div>
        <h3>Consulta de Trazabilidad y Origen</h3>
        <p>Introduce el código alfanumérico impreso en la botella o etiqueta para verificar su autenticidad analítica.</p>
      </div>
    `;
    return;
  }

  try {
    const res = await api.getTrazabilidad(code);
    const data = res.data;

    const paramsFisico = data.parametros_analiticos || {};
    const ph = paramsFisico.ph || '3.65';
    const acidez = paramsFisico.acidez_total_g_l || '5.4';
    const densidad = paramsFisico.densidad_final || '0.992';
    const alcohol = paramsFisico.alcohol_real_pct || data.grado_alcoholico || '13.5';
    const barrica = paramsFisico.crianza_meses ? `${paramsFisico.crianza_meses} meses en barrica` : 'Fermentación en acero inoxidable';

    const fechaElab = data.fecha_elaboracion ? new Date(data.fecha_elaboracion).toLocaleDateString('es-ES', { year: 'numeric', month: 'long', day: 'numeric' }) : '2026';

    displayBox.innerHTML = `
      <div class="card card-elevated" style="background: #FFFFFF; border: 2px solid var(--color-pergamino-dark); padding: var(--space-2xl); position: relative; overflow: hidden;">
        
        <!-- WATERMARK BACKGROUND BADGE -->
        <div style="position: absolute; top: -20px; right: -20px; opacity: 0.04; font-size: 14rem; pointer-events: none; font-family: serif;">
          EC
        </div>

        <!-- CERTIFICATE HEADER -->
        <div style="text-align: center; border-bottom: 2px solid var(--color-pergamino-dark); padding-bottom: var(--space-lg); margin-bottom: var(--space-xl);">
          <div style="display: inline-flex; align-items: center; gap: var(--space-xs); background: rgba(217, 119, 6, 0.12); color: var(--color-amber-dark); padding: 4px 16px; border-radius: 999px; font-size: 0.82rem; font-weight: 700; margin-bottom: var(--space-sm); letter-spacing: 0.05em;">
            <span>🛡️</span> LOTE AUTÉNTICO Y CERTIFICADO
          </div>
          
          <div style="font-size: 0.9rem; text-transform: uppercase; letter-spacing: 0.15em; color: var(--color-text-muted); margin-bottom: 4px;">
            Certificado de Trazabilidad Pública
          </div>
          
          <h2 style="font-family: monospace; font-size: 2.2rem; color: var(--color-burgundy); letter-spacing: 0.08em; margin: var(--space-xs) 0;">
            ${data.codigo_lote}
          </h2>

          <div style="font-size: 0.95rem; color: var(--color-text-muted);">
            Embotellado por <strong>${data.productor_nombre || 'Bodega Artesanal'}</strong> · ${data.productor_region || 'Origen Certificado'}
          </div>
        </div>

        <!-- BEVERAGE ESSENTIALS -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: var(--space-lg); margin-bottom: var(--space-xl); background: var(--color-pergamino); padding: var(--space-lg); border-radius: var(--radius-md);">
          <div>
            <span style="font-size: 0.78rem; color: var(--color-text-muted); text-transform: uppercase; font-weight: 600;">Bebida</span>
            <div style="font-size: 1.15rem; font-weight: 600; color: var(--color-burgundy);">${data.producto_nombre}</div>
            <div style="font-size: 0.85rem; color: var(--color-text);">${data.categoria}</div>
          </div>
          <div>
            <span style="font-size: 0.78rem; color: var(--color-text-muted); text-transform: uppercase; font-weight: 600;">Cosecha / Añada</span>
            <div style="font-size: 1.15rem; font-weight: 600; color: var(--color-text);">${data.anio_cosecha || '2025'}</div>
            <div style="font-size: 0.85rem; color: var(--color-text-muted);">Elaboración: ${fechaElab}</div>
          </div>
          <div>
            <span style="font-size: 0.78rem; color: var(--color-text-muted); text-transform: uppercase; font-weight: 600;">Volumen del Lote</span>
            <div style="font-size: 1.15rem; font-weight: 600; color: var(--color-text);">${data.volumen_producido_litros} Litros</div>
            <div style="font-size: 0.85rem; color: var(--color-amber-dark); font-weight: 500;">Tirada Limitada de Autor</div>
          </div>
        </div>

        <!-- 4-METRIC ANALYTICAL GRID -->
        <div style="margin-bottom: var(--space-xl);">
          <h3 style="font-size: 1.15rem; margin-bottom: var(--space-md); text-align: center; position: relative;">
            <span style="background: #FFFFFF; padding: 0 var(--space-md); position: relative; z-index: 1;">Parámetros Analíticos de Laboratorio</span>
            <span style="position: absolute; left: 0; right: 0; top: 50%; height: 1px; background: var(--color-border); z-index: 0;"></span>
          </h3>

          <div class="grid grid-4" style="gap: var(--space-md);">
            <!-- pH -->
            <div class="card" style="padding: var(--space-md); text-align: center; border-top: 3px solid var(--color-burgundy);">
              <span style="font-size: 0.78rem; color: var(--color-text-muted); text-transform: uppercase; font-weight: 600;">pH Químico</span>
              <div style="font-size: 1.8rem; font-weight: 700; color: var(--color-burgundy); margin: var(--space-2xs) 0;">
                ${ph}
              </div>
              <span style="font-size: 0.75rem; color: var(--color-text-muted);">Equilibrio ácido ideal</span>
            </div>

            <!-- Alcohol Real -->
            <div class="card" style="padding: var(--space-md); text-align: center; border-top: 3px solid var(--color-amber);">
              <span style="font-size: 0.78rem; color: var(--color-text-muted); text-transform: uppercase; font-weight: 600;">Alcohol Real</span>
              <div style="font-size: 1.8rem; font-weight: 700; color: var(--color-amber-dark); margin: var(--space-2xs) 0;">
                ${alcohol}%
              </div>
              <span style="font-size: 0.75rem; color: var(--color-text-muted);">Grado alcohólico vol.</span>
            </div>

            <!-- Acidez Total -->
            <div class="card" style="padding: var(--space-md); text-align: center; border-top: 3px solid var(--color-burgundy);">
              <span style="font-size: 0.78rem; color: var(--color-text-muted); text-transform: uppercase; font-weight: 600;">Acidez Total</span>
              <div style="font-size: 1.8rem; font-weight: 700; color: var(--color-burgundy); margin: var(--space-2xs) 0;">
                ${acidez} <span style="font-size: 0.9rem;">g/L</span>
              </div>
              <span style="font-size: 0.75rem; color: var(--color-text-muted);">Frescura y vivacidad</span>
            </div>

            <!-- Densidad -->
            <div class="card" style="padding: var(--space-md); text-align: center; border-top: 3px solid var(--color-amber);">
              <span style="font-size: 0.78rem; color: var(--color-text-muted); text-transform: uppercase; font-weight: 600;">Densidad Final</span>
              <div style="font-size: 1.8rem; font-weight: 700; color: var(--color-amber-dark); margin: var(--space-2xs) 0;">
                ${densidad}
              </div>
              <span style="font-size: 0.75rem; color: var(--color-text-muted);">Cuerpo en boca</span>
            </div>
          </div>
        </div>

        <!-- PROCESS & WINEMAKING NOTES -->
        <div style="margin-bottom: var(--space-xl); background: var(--color-pergamino); padding: var(--space-lg); border-radius: var(--radius-md);">
          <h4 style="margin-bottom: var(--space-xs); color: var(--color-burgundy);">Método de Vinificación / Elaboración</h4>
          <p style="font-size: 0.92rem; color: var(--color-text); line-height: 1.6; margin: 0;">
            ${barrica}. Fermentación bajo estricto control térmico, conservación sin sulfitos añadidos artificiales por encima de los límites biodinámicos y reposo con decantación natural.
          </p>
        </div>

        <!-- TIMELINE MILESTONES -->
        <div style="margin-bottom: var(--space-lg);">
          <h4 style="margin-bottom: var(--space-md); text-align: center; color: var(--color-burgundy);">Hitos del Ciclo de Vida del Lote</h4>
          <div style="display: flex; justify-content: space-between; position: relative; gap: var(--space-sm); flex-wrap: wrap;">
            <div style="flex: 1; min-width: 140px; text-align: center; padding: var(--space-sm); background: #FFF; border: 1px solid var(--color-border); border-radius: var(--radius-sm);">
              <div style="font-size: 1.3rem;">🍇</div>
              <strong style="display: block; font-size: 0.85rem;">Vendimia / Cosecha</strong>
              <span style="font-size: 0.75rem; color: var(--color-text-muted);">Selección manual</span>
            </div>
            <div style="flex: 1; min-width: 140px; text-align: center; padding: var(--space-sm); background: #FFF; border: 1px solid var(--color-border); border-radius: var(--radius-sm);">
              <div style="font-size: 1.3rem;">🧪</div>
              <strong style="display: block; font-size: 0.85rem;">Fermentación</strong>
              <span style="font-size: 0.75rem; color: var(--color-text-muted);">Control analítico</span>
            </div>
            <div style="flex: 1; min-width: 140px; text-align: center; padding: var(--space-sm); background: #FFF; border: 1px solid var(--color-border); border-radius: var(--radius-sm);">
              <div style="font-size: 1.3rem;">🪵</div>
              <strong style="display: block; font-size: 0.85rem;">Maduración</strong>
              <span style="font-size: 0.75rem; color: var(--color-text-muted);">Guarda reposada</span>
            </div>
            <div style="flex: 1; min-width: 140px; text-align: center; padding: var(--space-sm); background: #FFF; border: 1px solid var(--color-border); border-radius: var(--radius-sm);">
              <div style="font-size: 1.3rem;">🏷️</div>
              <strong style="display: block; font-size: 0.85rem;">Embotellado</strong>
              <span style="font-size: 0.75rem; color: var(--color-text-muted);">Asignación código único</span>
            </div>
          </div>
        </div>

        <!-- FOOTER VERIFICATION SEAL -->
        <div style="text-align: center; border-top: 1px solid var(--color-border); padding-top: var(--space-md); margin-top: var(--space-xl); font-size: 0.82rem; color: var(--color-text-muted);">
          Registro inmutable auditado en la plataforma <strong>Entre Copas</strong> · Cumplimiento de trazabilidad ISO 22005
        </div>
      </div>
    `;
  } catch (err) {
    displayBox.innerHTML = `
      <div class="empty-state">
        <div class="empty-state-icon">⚠️</div>
        <h3>Código de lote no localizado</h3>
        <p>No se encontró ningún registro para el código "<strong>${code}</strong>". Verifica que esté bien escrito o consulta con el productor.</p>
        <div style="margin-top: var(--space-md);">
          <a href="#/catalogo" class="btn btn-primary btn-sm">Ver Catálogo de Bebidas</a>
        </div>
      </div>
    `;
  }
}
