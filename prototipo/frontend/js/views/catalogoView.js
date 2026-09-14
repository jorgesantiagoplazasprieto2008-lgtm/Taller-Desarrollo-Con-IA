/**
 * Entre Copas - Public Catalog View
 * Live search, category filters, responsive product grid, loading/empty/error states
 */

import { api } from '../api.js';

export async function renderCatalogo(container) {
  let activeCategory = '';
  let searchQuery = '';

  container.innerHTML = `
    <div class="container" style="padding-top: var(--space-xl); padding-bottom: var(--space-2xl);">
      <!-- HEADER -->
      <div style="margin-bottom: var(--space-xl); text-align: center;">
        <span class="badge badge-burgundy" style="margin-bottom: var(--space-xs);">CATÁLOGO PÚBLICO</span>
        <h1 style="margin-bottom: var(--space-xs);">Bebidas de Elaboración Artesanal</h1>
        <p style="color: var(--color-text-muted); max-width: 600px; margin: 0 auto;">
          Explora la selección certificada de vinos, cervezas e hidromieles de pequeños y medianos productores independientes.
        </p>
      </div>

      <!-- FILTER CONTROLS & SEARCH -->
      <div class="card" style="padding: var(--space-lg); margin-bottom: var(--space-xl); background: #FFFFFF;">
        <div style="display: flex; gap: var(--space-md); flex-wrap: wrap; justify-content: space-between; align-items: center;">
          
          <!-- Category Pills -->
          <div style="display: flex; gap: var(--space-xs); flex-wrap: wrap;" id="category-pills">
            <button class="btn btn-sm btn-primary filter-pill" data-cat="">Todos</button>
            <button class="btn btn-sm btn-outline filter-pill" data-cat="Vino Tinto">Vinos Tintos</button>
            <button class="btn btn-sm btn-outline filter-pill" data-cat="Vino Blanco">Vinos Blancos</button>
            <button class="btn btn-sm btn-outline filter-pill" data-cat="Cerveza Artesanal">Cervezas Artesanales</button>
            <button class="btn btn-sm btn-outline filter-pill" data-cat="Hidromiel">Hidromieles</button>
          </div>

          <!-- Search Input -->
          <div style="flex: 1; max-width: 320px; min-width: 220px; position: relative;">
            <input 
              type="text" 
              id="catalog-search-input" 
              class="form-control" 
              placeholder="Buscar por nombre, uva o estilo..." 
              style="height: 42px; font-size: 0.9rem;"
            />
          </div>
        </div>

        <div style="margin-top: var(--space-sm); font-size: 0.85rem; color: var(--color-text-muted); display: flex; justify-content: space-between;">
          <span id="catalog-count-label">Cargando bebidas...</span>
        </div>
      </div>

      <!-- PRODUCTS GRID CONTAINER -->
      <div id="catalog-products-grid" class="grid grid-3">
        ${renderSkeletons(6)}
      </div>
    </div>
  `;

  const grid = document.getElementById('catalog-products-grid');
  const countLabel = document.getElementById('catalog-count-label');
  const searchInput = document.getElementById('catalog-search-input');
  const pillsContainer = document.getElementById('category-pills');

  // Load products function
  async function loadProducts() {
    grid.innerHTML = renderSkeletons(6);
    countLabel.textContent = 'Actualizando catálogo...';

    try {
      const res = await api.getPublicProducts({
        categoria: activeCategory,
        q: searchQuery
      });
      const products = res.data || [];

      countLabel.textContent = `Mostrando ${products.length} bebida${products.length === 1 ? '' : 's'} artesanal${products.length === 1 ? '' : 'es'}`;

      if (products.length === 0) {
        grid.innerHTML = `
          <div class="empty-state" style="grid-column: 1 / -1;">
            <div class="empty-state-icon">🔍</div>
            <h3>No encontramos bebidas</h3>
            <p>No hay resultados que coincidan con "${searchQuery || activeCategory}". Intenta con otros términos o filtros.</p>
            <button id="btn-reset-filters" class="btn btn-outline btn-sm" style="margin-top: var(--space-md);">Limpiar Filtros</button>
          </div>
        `;
        document.getElementById('btn-reset-filters')?.addEventListener('click', () => {
          activeCategory = '';
          searchQuery = '';
          searchInput.value = '';
          updatePillStyles();
          loadProducts();
        });
        return;
      }

      grid.innerHTML = products.map((p) => renderProductCard(p)).join('');
    } catch (err) {
      grid.innerHTML = `
        <div class="error-banner" style="grid-column: 1 / -1;">
          <p>Error al cargar el catálogo de bebidas: ${err.message}</p>
          <button id="btn-retry-catalog" class="btn btn-sm btn-outline" style="margin-top: var(--space-sm);">Reintentar</button>
        </div>
      `;
      document.getElementById('btn-retry-catalog')?.addEventListener('click', loadProducts);
    }
  }

  function updatePillStyles() {
    const buttons = pillsContainer.querySelectorAll('.filter-pill');
    buttons.forEach((btn) => {
      const cat = btn.getAttribute('data-cat');
      if (cat === activeCategory) {
        btn.className = 'btn btn-sm btn-primary filter-pill';
      } else {
        btn.className = 'btn btn-sm btn-outline filter-pill';
      }
    });
  }

  // Category pill click handler
  pillsContainer.addEventListener('click', (e) => {
    const target = e.target.closest('.filter-pill');
    if (!target) return;
    activeCategory = target.getAttribute('data-cat') || '';
    updatePillStyles();
    loadProducts();
  });

  // Debounced search input handler
  let debounceTimeout = null;
  searchInput.addEventListener('input', (e) => {
    clearTimeout(debounceTimeout);
    debounceTimeout = setTimeout(() => {
      searchQuery = e.target.value.trim();
      loadProducts();
    }, 350);
  });

  // Initial load
  loadProducts();
}

function renderSkeletons(count) {
  let html = '';
  for (let i = 0; i < count; i++) {
    html += `<div class="skeleton" style="height: 360px; border-radius: var(--radius-md);"></div>`;
  }
  return html;
}

function renderProductCard(p) {
  const cat = p.categoria || p.tipo_bebida || 'Bebida Artesanal';
  const categoryBadge = cat.toLowerCase().includes('tinto') ? 'badge-burgundy' : 'badge-amber';
  const prodName = p.productor_nombre || (p.productor ? p.productor.nombre_comercial : 'Bodega Artesanal');
  const abv = p.grado_alcoholico || (p.parametros_analiticos ? p.parametros_analiticos.graduacion_alcoholica : '13.5');
  const precio = p.precio ? `$${Number(p.precio).toLocaleString('es-CO')}` : null;

  return `
    <div class="card card-elevated" style="display: flex; flex-direction: column; overflow: hidden; background: #FFFFFF; transition: transform var(--transition-fast);">
      <div style="background: var(--color-pergamino-dark); height: 180px; display: flex; align-items: center; justify-content: center; position: relative;">
        <span style="font-size: 3.5rem;">🍾</span>
        <span class="badge ${categoryBadge}" style="position: absolute; top: var(--space-sm); right: var(--space-sm);">
          ${cat}
        </span>
        ${p.presentacion ? `<span style="position: absolute; bottom: var(--space-xs); left: var(--space-sm); font-size: 0.75rem; color: var(--color-text-muted); background: rgba(255,255,255,0.7); padding: 2px 6px; border-radius: 4px;">${p.presentacion}</span>` : ''}
      </div>
      <div style="padding: var(--space-md); display: flex; flex-direction: column; flex: 1;">
        <div style="font-size: 0.82rem; color: var(--color-text-muted); font-weight: 500; margin-bottom: 2px;">
          ${prodName}
        </div>
        <h3 style="font-size: 1.15rem; margin-bottom: var(--space-xs);">
          <a href="#/producto/${p.id}" style="color: var(--color-burgundy);">${p.nombre}</a>
        </h3>
        <p style="font-size: 0.88rem; color: var(--color-text-muted); line-height: 1.4; margin-bottom: var(--space-md); flex: 1;">
          ${p.descripcion ? p.descripcion.substring(0, 100) + '...' : 'Bebida artesanal de alta calidad elaborada con métodos tradicionales.'}
        </p>
        <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--color-border); padding-top: var(--space-sm); margin-top: auto;">
          <div>
            ${precio ? `<div style="font-size: 0.95rem; font-weight: 700; color: var(--color-burgundy);">${precio}</div>` : ''}
            <span style="font-size: 0.75rem; color: var(--color-text-muted);">${abv}% ABV</span>
          </div>
          <a href="#/producto/${p.id}" class="btn btn-primary btn-sm">Ver Ficha →</a>
        </div>
      </div>
    </div>
  `;
}
