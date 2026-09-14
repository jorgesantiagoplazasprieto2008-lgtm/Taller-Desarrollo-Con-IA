# Entre Copas - Prototipo Funcional Full-Stack

Este directorio contiene el prototipo web funcional completo de **Entre Copas**, plataforma para la gestión, trazabilidad físico-química y comercialización de bebidas artesanales (vinos, cervezas artesanales e hidromiel).

---

## 📁 Estructura del Prototipo

```text
prototipo/
├── backend/                   # API REST Modular en Express.js
│   └── src/
│       ├── config/            # DB (MySQL / Memoria), JWT, BCrypt, variables de entorno
│       ├── middleware/        # RBAC, autenticación y manejo seguro de errores (OWASP)
│       ├── modules/           # Dominio modular: usuario, producto, lote, hosteleria, financiero
│       ├── routes/            # Enrutador centralizado /api/v1
│       ├── scripts/           # Suite de pruebas automatizadas (testEndpoints.js)
│       ├── app.js             # Configuración de Express, Helmet, CORS y rate-limit
│       └── server.js          # Punto de entrada y bootstrap
├── frontend/                  # SPA Vanilla JS + CSS System (Sin frameworks pesados)
│   ├── css/
│   │   ├── design-tokens.css  # Paleta noble (Borgoña #58111A, Ámbar #D97706, Pergamino #FDFBF7)
│   │   ├── layout.css         # Header sticky blur, grid responsivo, footer legal
│   │   └── components.css     # Cards, badges, tablas, modals, botones, skeletons
│   ├── js/
│   │   ├── api.js             # Cliente Fetch con inyección automática de JWT
│   │   ├── state.js           # Gestor de sesión reactivo y sistema de toasts
│   │   ├── router.js          # Router SPA por hash (#/, #/catalogo, #/panel, etc.)
│   │   ├── views/             # Vistas: landing, catalogo, producto, trazabilidad, auth, panel, financiero, hosteleria
│   │   └── app.js             # Orquestador del navbar y ciclo de vida de la app
│   └── index.html             # Shell HTML5 semántico con fuentes Playfair Display & Inter
├── database/                  # Scripts de base de datos
│   └── mysql_workbench_schema.sql # DDL para MySQL 8.x con llaves foráneas, índices y datos semilla
├── .env                       # Variables de entorno locales
├── .env.example               # Plantilla de configuración
├── package.json               # Dependencias y scripts
└── README.md                  # Esta guía
```

---

## 🚀 Puesta en Marcha Rápida

### Opción A: Desde la carpeta `prototipo/`
```bash
cd prototipo
npm start
```

### Opción B: Desde la raíz del proyecto
```bash
npm start
```

El servidor iniciará en: **`http://localhost:3000`**

---

## 🧪 Pruebas Automatizadas

Para validar los 24 tests de integración (Auth, Trazabilidad, Multi-Tenant, Finanzas, Hostelería):

```bash
cd prototipo
npm test
```

---

## 🔑 Credenciales Semilla del Prototipo

| Rol | Correo Electrónico | Contraseña | Destino |
| :--- | :--- | :--- | :--- |
| **Productor** | `contacto@bodegasangabriel.com` | `Password123*` | `#/panel` (Mi Bodega & Lotes) |
| **Hostelería** | `gerencia@rincongourmet.com` | `Password123*` | `#/panel/hosteleria` (Mi Carta & Stock) |
| **Admin** | `admin@entrecopas.com` | `Password123*` | `#/panel` (Gestión Completa) |

*(Nota: En la pantalla de login puedes pulsar directamente los botones de "Acceso Rápido" sin tener que escribir las credenciales).*

---

## 🗄️ Base de Datos en MySQL Workbench

1. Abre **MySQL Workbench** y conéctate a `localhost:3306`.
2. Abre el archivo `database/mysql_workbench_schema.sql`.
3. Ejecuta todo el script con el botón del rayo ⚡ (`Ctrl + Shift + Enter`).
4. Para conectar directamente a MySQL, ajusta `DB_PASSWORD` en `prototipo/.env` con tu contraseña de MySQL.
