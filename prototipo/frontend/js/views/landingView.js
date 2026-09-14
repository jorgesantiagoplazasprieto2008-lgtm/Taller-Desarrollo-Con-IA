/**
 * Entre Copas - Landing Page View
 * Luxury craft hero, quick lot traceability search, value pillars, and featured bottles
 */

import { api } from '../api.js';
import { state } from '../state.js';

export async function renderLanding(container) {
  container.innerHTML = `
    <!-- HERO SECTION -->
    <section class="hero-section" style="background: linear-gradient(180deg, rgba(88, 17, 26, 0.06) 0%, rgba(253, 251, 247, 0) 100%); padding: var(--space-2xl) 0; border-bottom: 1px solid var(--color-border);">
      <div class="container" style="text-align: center; max-width: 860px;">
        <span class="badge badge-amber" style="margin-bottom: var(--space-md); font-weight: 600;">PLATAFORMA ARTESANAL DE ALTA GAMA</span>
        <h1 style="font-size: 2.75rem; margin-bottom: var(--space-md); line-height: 1.2;">
          Bebidas Nobles con <span style="color: var(--color-burgundy);">Trazabilidad y Origen</span> Certificado
        </h1>
        <p style="font-size: 1.15rem; color: var(--color-text-muted); margin-bottom: var(--space-xl); line-height: 1.6;">
          Descubre vinos de altura, cervezas de autor e hidromieles singulares. Conoce cada detalle analítico de su producción, desde la fermentación hasta tu copa.
        </p>

        <!-- QUICK TRACEABILITY SEARCH -->
        <div class="card card-elevated" style="padding: var(--space-md); max-width: 580px; margin: 0 auto var(--space-xl) auto; background: #FFFFFF;">
          <form id="hero-traceability-form" style="display: flex; gap: var(--space-sm); flex-wrap: wrap;">
            <div style="flex: 1; min-width: 220px; position: relative;">
              <input 
                type="text" 
                id="hero-traceability-input" 
                class="form-control" 
                placeholder="Ej: EC-2026-PR01-A101" 
                style="height: 48px; text-transform: uppercase; font-family: monospace; font-size: 1rem;"
                required
              />
            </div>
            <button type="submit" class="btn btn-secondary" style="height: 48px; padding: 0 var(--space-lg); font-weight: 600;">
              🔍 Verificar Lote
            </button>
          </form>
          <div style="display: flex; justify-content: center; gap: var(--space-xs); margin-top: var(--space-xs); font-size: 0.82rem; color: var(--color-text-muted);">
            <span>Prueba rápida:</span>
            <a href="#/trazabilidad/EC-2026-PR01-A101" style="color: var(--color-burgundy); font-weight: 600; text-decoration: underline;">EC-2026-PR01-A101</a>
            <span>o</span>
            <a href="#/trazabilidad/EC-2026-PR01-B202" style="color: var(--color-burgundy); font-weight: 600; text-decoration: underline;">EC-2026-PR01-B202</a>
          </div>
        </div>

        <div style="display: flex; justify-content: center; gap: var(--space-md); flex-wrap: wrap;">
          <a href="#/catalogo" class="btn btn-primary" style="height: 48px; padding: 0 var(--space-xl); font-size: 1rem;">
            Explorar Catálogo
          </a>
          <a href="#/login" class="btn btn-outline" style="height: 48px; padding: 0 var(--space-lg); font-size: 1rem;">
            Acceso a Productores
          </a>
        </div>
      </div>
    </section>

    <!-- CRAFT PILLARS -->
    <section style="padding: var(--space-2xl) 0;">
      <div class="container">
        <div style="text-align: center; margin-bottom: var(--space-xl);">
          <span class="badge badge-burgundy" style="margin-bottom: var(--space-xs);">NUESTRO COMPROMISO</span>
          <h2>El estándar de transparencia para el bebedor consciente</h2>
        </div>

        <div class="grid grid-3">
          <div class="card" style="padding: var(--space-xl); text-align: left;">
            <div style="font-size: 2.2rem; margin-bottom: var(--space-sm);">🔬</div>
            <h3 style="font-size: 1.25rem; margin-bottom: var(--space-xs);">Parámetros Físico-Químicos</h3>
            <p style="color: var(--color-text-muted); font-size: 0.95rem; line-height: 1.5;">
              No más etiquetas genéricas. Conoce el pH exacto, acidez volátil, densidad y grado alcohólico real certificados por el laboratorio del maestro elaborador.
            </p>
          </div>

          <div class="card" style="padding: var(--space-xl); text-align: left;">
            <div style="font-size: 2.2rem; margin-bottom: var(--space-sm);">🍇</div>
            <h3 style="font-size: 1.25rem; margin-bottom: var(--space-xs);">Directo del Productor</h3>
            <p style="color: var(--color-text-muted); font-size: 0.95rem; line-height: 1.5;">
              Cada botella cuenta la historia de su terroir o lúpulo, su añada y su método de maceración o fermentación, garantizando autenticidad genuina.
            </p>
          </div>

          <div class="card" style="padding: var(--space-xl); text-align: left;">
            <div style="font-size: 2.2rem; margin-bottom: var(--space-sm);">🍷</div>
            <h3 style="font-size: 1.25rem; margin-bottom: var(--space-xs);">Disponibilidad en Hostelería</h3>
            <p style="color: var(--color-text-muted); font-size: 0.95rem; line-height: 1.5;">
              Descubre qué bares y restaurantes gastronómicos disponen actualmente de botellas o copas servidas frescas con precio transparente.
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- FEATURED BOTTLES -->
    <section style="padding: var(--space-2xl) 0; background-color: rgba(0,0,0,0.015); border-top: 1px solid var(--color-border);">
      <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: var(--space-lg); flex-wrap: wrap; gap: var(--space-md);">
          <div>
            <span class="badge badge-amber">DESTACADOS</span>
            <h2 style="margin-top: var(--space-xs);">Colección de Bebidas Artesanales</h2>
          </div>
          <a href="#/catalogo" class="btn btn-outline btn-sm">Ver todas las bebidas →</a>
        </div>

        <div id="landing-featured-grid" class="grid grid-3">
          <!-- Loading skeleton -->
          <div class="skeleton" style="height: 380px; border-radius: var(--radius-md);"></div>
          <div class="skeleton" style="height: 380px; border-radius: var(--radius-md);"></div>
          <div class="skeleton" style="height: 380px; border-radius: var(--radius-md);"></div>
        </div>
      </div>
    </section>
  `;

  // Attach search form event
  const form = document.getElementById('hero-traceability-form');
  const input = document.getElementById('hero-traceability-input');
  if (form && input) {
    form.addEventListener('submit', (e) => {
      e.preventDefault();
      const code = input.value.trim().toUpperCase();
      if (code) {
        window.location.hash = `#/trazabilidad/${encodeURIComponent(code)}`;
      }
    });
  }

  // Load featured bottles
  const grid = document.getElementById('landing-featured-grid');
  try {
    const res = await api.getPublicProducts();
    const products = res.data || [];

    if (products.length === 0) {
      grid.innerHTML = `
        <div class="empty-state" style="grid-column: 1 / -1;">
          <div class="empty-state-icon">🍷</div>
          <h3>Aún no hay productos en exhibición</h3>
          <p>Los maestros elaboradores están preparando sus mejores añadas.</p>
        </div>
      `;
      return;
    }

    const featured = products.slice(0, 3);
    grid.innerHTML = featured.map((p) => renderProductCard(p)).join('');
  } catch (err) {
    grid.innerHTML = `
      <div class="error-banner" style="grid-column: 1 / -1;">
        <p>No fue posible conectar con el catálogo en este momento. (${err.message})</p>
      </div>
    `;
  }
}

function renderProductCard(p) {
  const cat = p.categoria || p.tipo_bebida || 'Bebida Artesanal';
  const categoryBadge = cat.toLowerCase().includes('tinto') ? 'badge-burgundy' : 'badge-amber';
  const prodName = p.productor_nombre || (p.productor ? p.productor.nombre_comercial : 'Bodega Artesanal');
  const abv = p.grado_alcoholico || (p.parametros_analiticos ? p.parametros_analiticos.graduacion_alcoholica : '13.5');
  const precio = p.precio ? `$${Number(p.precio).toLocaleString('es-CO')}` : null;

  return `
    <div class="card card-elevated" style="display: flex; flex-direction: column; overflow: hidden; background: #FFFFFF; transition: transform var(--transition-fast), box-shadow var(--transition-fast);">
      <div style="background: var(--color-pergamino-dark); height: 180px; display: flex; align-items: center; justify-content: center; position: relative;">
        <span style="font-size: 4rem;">🍾</span>
        <span class="badge ${categoryBadge}" style="position: absolute; top: var(--space-sm); right: var(--space-sm);">
          ${cat}
        </span>
      </div>
      <div style="padding: var(--space-md); display: flex; flex-direction: column; flex: 1;">
        <div style="font-size: 0.85rem; color: var(--color-text-muted); font-weight: 500; margin-bottom: 2px;">
          ${prodName}
        </div>
        <h3 style="font-size: 1.15rem; margin-bottom: var(--space-xs);">
          <a href="#/producto/${p.id}" style="color: var(--color-burgundy);">${p.nombre}</a>
        </h3>
        <p style="font-size: 0.88rem; color: var(--color-text-muted); line-height: 1.4; margin-bottom: var(--space-md); flex: 1;">
          ${p.descripcion ? p.descripcion.substring(0, 110) + '...' : 'Bebida de calidad superior elaborada bajo estrictos estándares artesanales.'}
        </p>
        <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--color-border); padding-top: var(--space-sm); margin-top: auto;">
          <div>
            ${precio ? `<div style="font-size: 0.95rem; font-weight: 700; color: var(--color-burgundy);">${precio}</div>` : ''}
            <span style="font-size: 0.75rem; color: var(--color-text-muted);">${abv}% ABV</span>
          </div>
          <a href="#/producto/${p.id}" class="btn btn-outline btn-sm">Ver Detalle</a>
        </div>
      </div>
    </div>
  `;
}
