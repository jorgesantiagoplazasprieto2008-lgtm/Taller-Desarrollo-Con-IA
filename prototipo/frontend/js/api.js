/**
 * Entre Copas - API Client
 * Centralized Fetch wrapper with automatic JWT injection, error normalization, and response parsing
 */

import { state } from './state.js';

const API_BASE = '/api/v1';

class ApiClient {
  async request(endpoint, options = {}) {
    const url = `${API_BASE}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers
    };

    const token = state.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
      ...options,
      headers
    };

    if (config.body && typeof config.body === 'object') {
      config.body = JSON.stringify(config.body);
    }

    try {
      const response = await fetch(url, config);

      if (response.status === 401) {
        // If unauthorized while having a token, token has expired
        if (state.getToken()) {
          state.clearSession();
          state.showToast('Tu sesión ha expirado. Por favor inicia sesión nuevamente.', 'error');
          window.location.hash = '#/login';
        }
      }

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        const errorMessage = data.message || data.error || `Error en la solicitud HTTP (${response.status})`;
        throw new Error(errorMessage);
      }

      return data;
    } catch (err) {
      console.error(`API Error [${options.method || 'GET'} ${endpoint}]:`, err.message);
      throw err;
    }
  }

  // --- Auth Endpoints ---
  async login(email, password) {
    return this.request('/auth/login', {
      method: 'POST',
      body: { email, password }
    });
  }

  async register(userData) {
    return this.request('/auth/registro', {
      method: 'POST',
      body: userData
    });
  }

  async getMe() {
    return this.request('/auth/me');
  }

  // --- Public Endpoints ---
  async getPublicProducts(params = {}) {
    const query = new URLSearchParams();
    if (params.categoria) query.append('categoria', params.categoria);
    if (params.q) query.append('q', params.q);
    const qs = query.toString() ? `?${query.toString()}` : '';
    return this.request(`/productos${qs}`);
  }

  async getProductById(id) {
    return this.request(`/productos/${id}`);
  }

  async getTrazabilidad(codigoLote) {
    return this.request(`/trazabilidad/${encodeURIComponent(codigoLote)}`);
  }

  // --- Producer Endpoints (Secured) ---
  async getProducerProducts() {
    return this.request('/productor/productos');
  }

  async createProduct(productData) {
    return this.request('/productor/productos', {
      method: 'POST',
      body: productData
    });
  }

  async updateProductStatus(id, estado) {
    return this.request(`/productor/productos/${id}/estado`, {
      method: 'PATCH',
      body: { estado }
    });
  }

  async getProducerLots() {
    return this.request('/productor/lotes');
  }

  async createLot(lotData) {
    return this.request('/productor/lotes', {
      method: 'POST',
      body: lotData
    });
  }

  // --- Financial Dashboard (Secured) ---
  async getFinancialSummary() {
    return this.request('/financiero/resumen');
  }

  // --- Hosteleria Endpoints (Secured) ---
  async getHosteleriaCatalog() {
    return this.request('/hosteleria/catalogo');
  }

  async toggleDisponibilidad(productoId, disponible, precioCopa = null, precioBotella = null) {
    return this.request('/hosteleria/disponibilidad', {
      method: 'POST',
      body: {
        producto_id: productoId,
        disponible: Boolean(disponible),
        precio_copa: precioCopa,
        precio_botella: precioBotella
      }
    });
  }
}

export const api = new ApiClient();
