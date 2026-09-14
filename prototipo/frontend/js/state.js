/**
 * Entre Copas - Global State & Session Store
 * Manages user tokens, roles, and UI feedback (toasts)
 */

class StateManager {
  constructor() {
    this.tokenKey = 'entrecopas_token';
    this.userKey = 'entrecopas_user';
    this.listeners = [];
  }

  getToken() {
    return localStorage.getItem(this.tokenKey);
  }

  getUser() {
    const data = localStorage.getItem(this.userKey);
    try {
      return data ? JSON.parse(data) : null;
    } catch (e) {
      return null;
    }
  }

  isAuthenticated() {
    return !!this.getToken();
  }

  hasRole(role) {
    const user = this.getUser();
    return user && user.rol === role;
  }

  isProductor() {
    return this.hasRole('ROLE_PRODUCTOR') || this.hasRole('ROLE_ADMIN');
  }

  isHosteleria() {
    return this.hasRole('ROLE_HOSTELERIA') || this.hasRole('ROLE_ADMIN');
  }

  isAdmin() {
    return this.hasRole('ROLE_ADMIN');
  }

  setSession(token, user) {
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem(this.userKey, JSON.stringify(user));
    this.notify();
  }

  clearSession() {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.notify();
  }

  subscribe(callback) {
    this.listeners.push(callback);
  }

  notify() {
    this.listeners.forEach((fn) => fn(this.getUser()));
  }

  showToast(message, type = 'info', duration = 4000) {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    let icon = 'ℹ️';
    if (type === 'success') icon = '✓';
    if (type === 'error') icon = '⚠️';

    toast.innerHTML = `
      <span class="toast-icon">${icon}</span>
      <div class="toast-content">
        <p class="toast-message">${this.escapeHtml(message)}</p>
      </div>
    `;

    container.appendChild(toast);

    setTimeout(() => {
      toast.style.animation = 'fadeOut 0.3s forwards';
      setTimeout(() => toast.remove(), 300);
    }, duration);
  }

  escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.innerText = str;
    return div.innerHTML;
  }
}

export const state = new StateManager();
