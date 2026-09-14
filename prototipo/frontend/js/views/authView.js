/**
 * Entre Copas - Authentication View (Login & Register)
 * Handles role-based registration, credential validation, one-click demo credentials, and JWT storage
 */

import { api } from '../api.js';
import { state } from '../state.js';

export async function renderAuth(container, initialMode = 'login') {
  let currentMode = initialMode; // 'login' or 'register'
  let selectedRole = 'ROLE_PRODUCTOR'; // Default registration role

  function render() {
    container.innerHTML = `
      <div class="container" style="padding-top: var(--space-2xl); padding-bottom: var(--space-2xl); max-width: 520px;">
        <div class="card card-elevated" style="padding: var(--space-xl); background: #FFFFFF;">
          
          <!-- AUTH HEADER -->
          <div style="text-align: center; margin-bottom: var(--space-lg);">
            <div style="font-size: 2.2rem; margin-bottom: var(--space-2xs);">🍷</div>
            <h2 style="font-size: 1.6rem; margin-bottom: var(--space-2xs);">
              ${currentMode === 'login' ? 'Bienvenido a Entre Copas' : 'Crear Cuenta en Entre Copas'}
            </h2>
            <p style="color: var(--color-text-muted); font-size: 0.9rem;">
              ${currentMode === 'login' ? 'Ingresa tus credenciales para acceder a tu panel de gestión.' : 'Elige tu rol para comenzar a operar en la plataforma.'}
            </p>
          </div>

          <!-- TAB SWITCHER -->
          <div style="display: flex; border-bottom: 2px solid var(--color-border); margin-bottom: var(--space-lg);">
            <button id="tab-login" class="btn" style="flex: 1; border-radius: 0; border: none; border-bottom: 3px solid ${currentMode === 'login' ? 'var(--color-burgundy)' : 'transparent'}; font-weight: ${currentMode === 'login' ? '700' : '500'}; color: ${currentMode === 'login' ? 'var(--color-burgundy)' : 'var(--color-text-muted)'}; background: transparent; padding: var(--space-sm) 0;">
              Iniciar Sesión
            </button>
            <button id="tab-register" class="btn" style="flex: 1; border-radius: 0; border: none; border-bottom: 3px solid ${currentMode === 'register' ? 'var(--color-burgundy)' : 'transparent'}; font-weight: ${currentMode === 'register' ? '700' : '500'}; color: ${currentMode === 'register' ? 'var(--color-burgundy)' : 'var(--color-text-muted)'}; background: transparent; padding: var(--space-sm) 0;">
              Registrarse
            </button>
          </div>

          <!-- FORM BODY -->
          <div id="auth-form-container">
            ${currentMode === 'login' ? renderLoginForm() : renderRegisterForm(selectedRole)}
          </div>

          <!-- DEMO PRESETS (QUICK LOGIN) -->
          <div style="margin-top: var(--space-xl); border-top: 1px dashed var(--color-border); padding-top: var(--space-md);">
            <div style="font-size: 0.78rem; text-transform: uppercase; color: var(--color-text-muted); font-weight: 600; text-align: center; margin-bottom: var(--space-xs);">
              Acceso Rápido de Demostración
            </div>
            <div style="display: flex; gap: var(--space-xs); justify-content: center; flex-wrap: wrap;">
              <button class="btn btn-sm btn-outline demo-btn" data-email="contacto@bodegasangabriel.com" data-pass="Password123*" title="Productor de vinos">
                🍇 Productor
              </button>
              <button class="btn btn-sm btn-outline demo-btn" data-email="gerencia@rincongourmet.com" data-pass="Password123*" title="Bar / Restaurante">
                🍽️ Hostelería
              </button>
              <button class="btn btn-sm btn-outline demo-btn" data-email="admin@entrecopas.com" data-pass="Password123*" title="Administrador general">
                👑 Admin
              </button>
            </div>
          </div>

        </div>
      </div>
    `;

    attachEvents();
  }

  function renderLoginForm() {
    return `
      <form id="form-login">
        <div class="form-group" style="margin-bottom: var(--space-md);">
          <label class="form-label" for="login-email">Correo Electrónico</label>
          <input type="email" id="login-email" class="form-control" placeholder="tu@bodega.com" required />
        </div>

        <div class="form-group" style="margin-bottom: var(--space-lg);">
          <label class="form-label" for="login-password">Contraseña</label>
          <input type="password" id="login-password" class="form-control" placeholder="••••••••" required />
        </div>

        <button type="submit" id="btn-submit-auth" class="btn btn-primary" style="width: 100%; height: 46px; font-size: 1rem;">
          Iniciar Sesión
        </button>
      </form>
    `;
  }

  function renderRegisterForm(role) {
    return `
      <form id="form-register">
        <!-- Role Selector Pills -->
        <div class="form-group" style="margin-bottom: var(--space-md);">
          <label class="form-label">Selecciona tu Rol</label>
          <div style="display: flex; gap: var(--space-2xs);">
            <button type="button" class="btn btn-sm role-choice-btn ${role === 'ROLE_PRODUCTOR' ? 'btn-primary' : 'btn-outline'}" data-role="ROLE_PRODUCTOR" style="flex: 1;">
              🍇 Productor
            </button>
            <button type="button" class="btn btn-sm role-choice-btn ${role === 'ROLE_HOSTELERIA' ? 'btn-primary' : 'btn-outline'}" data-role="ROLE_HOSTELERIA" style="flex: 1;">
              🍽️ Hostelería
            </button>
            <button type="button" class="btn btn-sm role-choice-btn ${role === 'ROLE_CONSUMIDOR' ? 'btn-primary' : 'btn-outline'}" data-role="ROLE_CONSUMIDOR" style="flex: 1;">
              🍷 Consumidor
            </button>
          </div>
        </div>

        <div class="form-group" style="margin-bottom: var(--space-sm);">
          <label class="form-label" for="reg-nombre">Nombre Completo / Titular</label>
          <input type="text" id="reg-nombre" class="form-control" placeholder="Ej: Gabriel Montes" required />
        </div>

        <div class="form-group" style="margin-bottom: var(--space-sm);">
          <label class="form-label" for="reg-email">Correo Electrónico</label>
          <input type="email" id="reg-email" class="form-control" placeholder="contacto@empresa.com" required />
        </div>

        <div class="form-group" style="margin-bottom: var(--space-sm);">
          <label class="form-label" for="reg-password">Contraseña (Mínimo 8 caracteres)</label>
          <input type="password" id="reg-password" class="form-control" placeholder="Mínimo 8 caracteres con letras y números" minlength="8" required />
        </div>

        <!-- Role Specific Fields -->
        ${role === 'ROLE_PRODUCTOR' ? `
          <div style="background: var(--color-pergamino); padding: var(--space-sm); border-radius: var(--radius-sm); margin-bottom: var(--space-sm);">
            <div class="form-group" style="margin-bottom: var(--space-xs);">
              <label class="form-label" for="reg-prod-nombre">Nombre de la Bodega / Elaborador</label>
              <input type="text" id="reg-prod-nombre" class="form-control" placeholder="Ej: Bodega San Gabriel" required />
            </div>
            <div class="form-group" style="margin-bottom: 0;">
              <label class="form-label" for="reg-prod-region">Región / Terroir</label>
              <input type="text" id="reg-prod-region" class="form-control" placeholder="Ej: Valle de Uco, Mendoza" required />
            </div>
          </div>
        ` : ''}

        ${role === 'ROLE_HOSTELERIA' ? `
          <div style="background: var(--color-pergamino); padding: var(--space-sm); border-radius: var(--radius-sm); margin-bottom: var(--space-sm);">
            <div class="form-group" style="margin-bottom: var(--space-xs);">
              <label class="form-label" for="reg-host-nombre">Nombre Comercial del Bar / Restaurante</label>
              <input type="text" id="reg-host-nombre" class="form-control" placeholder="Ej: Rincón Gourmet Bar" required />
            </div>
            <div class="form-group" style="margin-bottom: 0;">
              <label class="form-label" for="reg-host-dir">Dirección Física</label>
              <input type="text" id="reg-host-dir" class="form-control" placeholder="Ej: Calle Gran Vía 42" required />
            </div>
          </div>
        ` : ''}

        <button type="submit" id="btn-submit-auth" class="btn btn-primary" style="width: 100%; height: 46px; font-size: 1rem; margin-top: var(--space-xs);">
          Registrar Cuenta
        </button>
      </form>
    `;
  }

  function attachEvents() {
    // Tab switching
    document.getElementById('tab-login')?.addEventListener('click', () => {
      currentMode = 'login';
      render();
    });

    document.getElementById('tab-register')?.addEventListener('click', () => {
      currentMode = 'register';
      render();
    });

    // Role selection in register
    container.querySelectorAll('.role-choice-btn').forEach((btn) => {
      btn.addEventListener('click', (e) => {
        selectedRole = e.target.getAttribute('data-role');
        render();
      });
    });

    // Demo Preset Buttons
    container.querySelectorAll('.demo-btn').forEach((btn) => {
      btn.addEventListener('click', () => {
        if (currentMode !== 'login') {
          currentMode = 'login';
          render();
        }
        document.getElementById('login-email').value = btn.getAttribute('data-email');
        document.getElementById('login-password').value = btn.getAttribute('data-pass');
        document.getElementById('btn-submit-auth')?.click();
      });
    });

    // Form Submit: Login
    const loginForm = document.getElementById('form-login');
    if (loginForm) {
      loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = document.getElementById('btn-submit-auth');
        const email = document.getElementById('login-email').value.trim();
        const pass = document.getElementById('login-password').value;

        submitBtn.disabled = true;
        submitBtn.textContent = 'Autenticando...';

        try {
          const res = await api.login(email, pass);
          state.setSession(res.data.token, res.data.user);
          state.showToast(`¡Bienvenido de nuevo, ${res.data.user.nombre}!`, 'success');

          // Redirect according to role
          redirectByRole(res.data.user.rol);
        } catch (err) {
          state.showToast(err.message, 'error');
          submitBtn.disabled = false;
          submitBtn.textContent = 'Iniciar Sesión';
        }
      });
    }

    // Form Submit: Register
    const regForm = document.getElementById('form-register');
    if (regForm) {
      regForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = document.getElementById('btn-submit-auth');
        const nombre = document.getElementById('reg-nombre').value.trim();
        const email = document.getElementById('reg-email').value.trim();
        const pass = document.getElementById('reg-password').value;

        const payload = {
          nombre,
          email,
          password: pass,
          rol: selectedRole
        };

        if (selectedRole === 'ROLE_PRODUCTOR') {
          payload.nombre_productor = document.getElementById('reg-prod-nombre')?.value.trim();
          payload.region = document.getElementById('reg-prod-region')?.value.trim();
        } else if (selectedRole === 'ROLE_HOSTELERIA') {
          payload.nombre_comercial = document.getElementById('reg-host-nombre')?.value.trim();
          payload.direccion = document.getElementById('reg-host-dir')?.value.trim();
        }

        submitBtn.disabled = true;
        submitBtn.textContent = 'Creando cuenta...';

        try {
          const res = await api.register(payload);
          state.setSession(res.data.token, res.data.user);
          state.showToast('Cuenta creada con éxito. Bienvenido.', 'success');
          redirectByRole(res.data.user.rol);
        } catch (err) {
          state.showToast(err.message, 'error');
          submitBtn.disabled = false;
          submitBtn.textContent = 'Registrar Cuenta';
        }
      });
    }
  }

  function redirectByRole(rol) {
    if (rol === 'ROLE_PRODUCTOR' || rol === 'ROLE_ADMIN') {
      window.location.hash = '#/panel';
    } else if (rol === 'ROLE_HOSTELERIA') {
      window.location.hash = '#/panel/hosteleria';
    } else {
      window.location.hash = '#/catalogo';
    }
  }

  render();
}
