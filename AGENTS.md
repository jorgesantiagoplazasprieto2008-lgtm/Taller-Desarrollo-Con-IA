# AGENTS.md - Protocolo Operativo para Agentes de Ingeniería de Software
## Entre Copas - Plataforma de gestión, trazabilidad y comercialización de bebidas artesanales

---

## 1. System Brief (Visión y Límites del PRD)

### 1.1 Visión del Producto
**Entre Copas** es una plataforma web modular diseñada para centralizar la gestión de producción, trazabilidad analítica y comercialización de bebidas artesanales (vinos, cervezas artesanales, hidromiel). Permite a los productores controlar de forma rigurosa sus lotes, parámetros físico-químicos, costos y mermas; mientras que ofrece a hostelería y consumidores una consulta transparente de disponibilidad y trazabilidad pública mediante códigos únicos.

### 1.2 Alcance del MVP (Fase 1 - Permitido)
1. **Autenticación y RBAC:** Registro y control de acceso para 4 roles: `ROLE_ADMIN` / `ROLE_PRODUCTOR`, `ROLE_HOSTELERIA`, `ROLE_CONSUMIDOR`, y Visitante Anónimo.
2. **Gestión de Productores y Productos:** CRUD de productos artesanales con estados (`ACTIVO`, `PAUSADO`, `RETIRADO`).
3. **Núcleo de Lotes y Trazabilidad:** Registro de lotes con parámetros analíticos (pH, alcohol, acidez, densidad), cálculo de mermas, costos y generación algorítmica del código único (`EC-YYYY-PRXX-XXXX`).
4. **Catálogo Público y Trazabilidad Pública:** Visualización pública sin login de bebidas y consulta de origen de lote.
5. **Gestión de Disponibilidad:** Hostelería gestiona disponibilidad local de productos.
6. **Dashboard Financiero Básico:** Cálculo agregado de costos, mermas e indicadores de margen para el productor autenticado.

### 1.3 Límites y Exclusiones Estrictas (Prohibido en MVP)
- ❌ **Carrito de compras y pasarela de pagos (PayU):** Exclusivo de Fase 2 (Post-MVP).
- ❌ **Gestión de pedidos y checkout:** Exclusivo de Fase 2 (Post-MVP).
- ❌ **Mapas (Google Maps / OSM) y servicios de envío:** Exclusivo de Fase 3.
- ❌ **Notificaciones push o SMS:** Exclusivo de Fase 3.
- ❌ **Bases de datos NoSQL o cachés Redis:** El MVP se sustenta 100% sobre MySQL 8.x relacional con soporte para campos nativos JSON.

### 1.4 Identidad y Experiential Vibe
- **Atmósfera:** Artesanal, noble, rústico-moderna y cálida.
- **Paleta de Colores:** Primario Borgoña (`#58111A`), Secundario Ámbar (`#D97706`), Fondo Cálido Pergamino (`#FDFBF7`), Superficies Blancas (`#FFFFFF`), Bordes Suaves (`#E7E2DA`).
- **Tipografía:** Encabezados elegantes con serif (*Playfair Display* / *Cinzel*), combinados con sans-serif técnica (*Inter*) para formularios, tablas analíticas y badges de trazabilidad.

---

## 2. Operational Rules (Convenciones, Arquitectura y Reglas del TRD)

### 2.1 Arquitectura y Organización de Paquetes
Se impone de forma estricta la arquitectura de **Monolito Modular por Dominio (Package by Feature)** bajo el paquete base `src/main/java/com/entrecopas/`:

```text
src/main/java/com/entrecopas/
├── usuario/         # controller, service, repository, model, dto, mapper
├── productor/       # controller, service, repository, model, dto, mapper
├── producto/        # controller, service, repository, model, dto, mapper
├── lote/            # controller, service, repository, model, dto, mapper
├── establecimiento/ # controller, service, repository, model, dto, mapper
├── disponibilidad/  # controller, service, repository, model, dto, mapper
├── financiero/      # controller, service, repository, model, dto, mapper
├── config/          # SecurityConfig, WebConfig, AuditConfig, PasswordEncoder
└── exception/       # GlobalExceptionHandler, CustomExceptions
```

### 2.2 Convenciones de Codificación y Stack
- **Java:** Versión 17+ (LTS).
- **Backend:** Spring Boot 3.x, Spring Data JPA, Spring Security.
- **Frontend:** Spring MVC + Thymeleaf, Bootstrap 5 y JavaScript vanilla modular con Fetch API.
- **Estándar de Código:** Google Java Style Guide aplicado mediante Checkstyle.
- **Validación:** DTOs con anotaciones Jakarta Validation (`@NotNull`, `@NotBlank`, `@Positive`, `@Email`). Validaciones de negocio complejas en la capa `@Service`.
- **Aislamiento Multi-Tenant Lógico:** Cada consulta de actualización o lectura de productor DEBE filtrar obligatoriamente por el `productor_id` del usuario autenticado en sesión (`SecurityContextHolder`). Prohibido confiar en IDs enviados desde el cliente.

### 2.3 Archivos y Zonas Protegidas (Modificación Restringida)
- ⚠️ `src/main/resources/db/migration/`: Los archivos de migración ya aplicados (`V1__...`, etc.) son **INMUTABLES**. Cualquier cambio en el esquema debe crearse como un nuevo archivo secuencial (`V2__...sql`).
- ⚠️ `pom.xml`: No agregar dependencias de terceros (como Redis, MongoDB, librerías de UI complejas o pasarelas de pago) sin aprobación explícita del validador humano.
- ⚠️ `src/main/java/com/entrecopas/config/SecurityConfig.java`: La configuración de rutas públicas vs protegidas requiere validación rigurosa; ninguna ruta administrativa (`/panel/**` o `/api/v1/financiero/**`) puede quedar expuesta como pública.

---

## 3. Harness Config (Comandos, Linters PreToolUse y Sandbox)

### 3.1 Entorno de Ejecución Sandbox
- Orquestación local: Docker Compose (`docker-compose up -d mysql`).
- Configuración de entorno: Parámetros y credenciales sensibles (`MYSQL_ROOT_PASSWORD`, `MYSQL_USER`, `MYSQL_PASSWORD`) consumidos vía variables de entorno en `.env` (ignorado en Git). Prohibido almacenar contraseñas en claro en `application.properties`.

### 3.2 Comandos Permitidos para el Agente
| Fase | Comando de Shell | Propósito |
| :--- | :--- | :--- |
| **Linter / Formato** | `mvn checkstyle:check` | Verificación estática PreToolUse antes de compilar. |
| **Build & Test** | `mvn clean test` | Ejecución de suite unitaria completa y compilación. |
| **Pruebas de Integración** | `mvn test -Dtest=*IntegrationTest` | Validación de capas con base de datos H2/MySQL de pruebas. |
| **Verificación de Migraciones** | `mvn flyway:info` / `mvn flyway:migrate` | Comprobación de estado de esquemas versionados. |
| **Empaquetado** | `mvn clean package -DskipTests=false` | Generación del artefacto `.jar` ejecutable. |

### 3.3 Presupuesto de Contexto y Delimitación de Tareas
- El agente debe operar en **tickets verticales medianos** (un solo módulo a la vez, e.g. solo `producto` o solo `lote`).
- Prohibido editar más de 4 archivos de distintas capas simultáneamente sin ejecutar `mvn checkstyle:check` o `mvn clean test`.

---

## 4. Persistence Loop (Protocolo de Estado del Agente y Anti-Loop)

### 4.1 Ciclo de Vida Operativo (Maker-Checker Loop)
```text
[Leer .agents/session_state.json] ──► [Verificar Hito y Ticket Activo]
               ▲                                      │
               │                                      ▼
     [Actualizar Estado] ◄────────────── [Generar Código / Tests]
               │                                      │
               ▼                                      ▼
     [Gate G0: Checkstyle] ─────────────► [Gate G1: mvn test]
               │                                      │
               └──────── (Si falla 3 veces) ──────────┘
                               │
                               ▼
            [STOP & Consultar Validador Humano]
```

### 4.2 Protocolo de Lectura y Escritura de Estado
1. **Paso 0 (Pre-Flight):** Antes de generar código, el agente debe leer `.agents/session_state.json`. Si no existe, debe inicializarlo.
2. **Paso 1 (Ejecución):** Modificar únicamente los archivos concernientes al ticket activo.
3. **Paso 2 (Validación PreToolUse):** Ejecutar `mvn checkstyle:check`. Si hay advertencias de estilo, corregirlas inmediatamente.
4. **Paso 3 (Validación de Comportamiento):** Ejecutar `mvn clean test`.
5. **Paso 4 (Persistencia):** Actualizar `.agents/session_state.json` con el siguiente formato exacto:
   ```json
   {
     "hito_activo": 1,
     "ticket_activo": "US-01: Registro de Usuarios y Roles",
     "status": "EN_PROGRESO",
     "archivos_modificados": [
       "src/main/java/com/entrecopas/usuario/model/Usuario.java",
       "src/main/java/com/entrecopas/usuario/repository/UsuarioRepository.java"
     ],
     "ultimos_tests_ejecutados": {
       "suite": "UsuarioServiceTest",
       "resultado": "PASSED",
       "timestamp": "2026-09-07T13:00:00Z"
     },
     "proximo_paso": "Implementar capa Service y formulario Thymeleaf"
   }
   ```
6. **Paso 5 (Condition de Parada - Límite Anti-Loop):**
   - Si un test falla persistentemente durante **3 intentos consecutivos**, el agente tiene **PROHIBIDO** continuar iterando a ciegas o reescribir código aleatoriamente.
   - Debe detenerse, registrar el stack trace completo en `scratch/agent_errors.log` y solicitar la intervención del validador humano (Maker-Checker).
