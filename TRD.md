# TRD - Technical Requirements Document
## Entre Copas - Plataforma de gestión, trazabilidad y comercialización de bebidas artesanales

---

## 1. Stack Tecnológico y Arquitectura de Software

### 1.1 Stack tecnológico

| Componente | Tecnología |
|------------|------------|
| **Lenguaje** | Java 17+ (LTS) |
| **Backend** | Spring Boot 3.x (Spring MVC, Spring Data JPA, Spring Security) |
| **Gestión del proyecto** | Maven |
| **Base de datos** | MySQL 8.x |
| **Frontend / Vistas** | Spring Boot MVC + Thymeleaf, HTML5, CSS3, JavaScript (Fetch API para llamadas dinámicas) |
| **Framework de estilos** | Bootstrap 5 |
| **Persistencia** | Spring Data JPA / Hibernate |
| **Migraciones de BD** | Flyway |
| **Autenticación y permisos** | Spring Security + RBAC (sesiones de usuario y protección de endpoints) |
| **Calidad de código (PreToolUse)** | Checkstyle (Google Java Style) + SpotBugs + Prettier (HTML/JS/CSS) |
| **APIs externas** | No contempladas en el MVP (PayU y Maps reservadas para Fase 2 y 3) |

### 1.2 Arquitectura de software

**Arquitectura elegida: Monolito modular empaquetado por dominio (Package by Feature)**

El proyecto se desarrollará bajo una arquitectura de **monolito modular** en Spring Boot, estructurado por dominios de negocio funcionales. Cada módulo agrupa verticalmente sus controladores, servicios, repositorios, entidades de modelo, DTOs y mappers.

**Estructura unificada de paquetes (`src/main/java/com/entrecopas/`):**
```text
src/main/java/com/entrecopas/
├── usuario/         # Autenticación, registro, usuarios y roles (controller, service, repository, model, dto)
├── productor/       # Perfil del productor artesanal y vinculación con usuario
├── producto/        # Catálogo público, CRUD de productos y estados (ACTIVO, PAUSADO, RETIRADO)
├── lote/            # Registro de lotes, costos, mermas, analítica y generación de código de trazabilidad
├── establecimiento/ # Registro de hostelería, bares, restaurantes y vinculación con usuario
├── disponibilidad/  # Relación N:M producto-establecimiento y consulta de disponibilidad
├── financiero/      # Métricas de rentabilidad, costos agregados, mermas por lote y producto
├── config/          # SecurityConfig, WebConfig, PasswordEncoder, AuditConfig
└── exception/       # GlobalExceptionHandler, ResourceNotFoundException, BusinessRuleException
```

**Ventajas de esta arquitectura:**
- Aislamiento de contexto por dominio, minimizando el acoplamiento y previniendo el *Understanding Rot*.
- Trazabilidad directa entre los requisitos funcionales del PRD y los paquetes de código.
- Facilita la transición futura a microservicios si el volumen de negocio lo demanda.

---

## 2. Modelo de Datos y Persistencia

### 2.1 Motor de base de datos

**Motor seleccionado: MySQL 8.x**
- Almacenamiento transaccional ACID con soporte para campos nativos `JSON`.
- Motor de almacenamiento: **InnoDB** con codificación `utf8mb4`.
- Migraciones automatizadas mediante **Flyway** (`src/main/resources/db/migration/V1__init_schema.sql`).
- Auditoría automática mediante Spring Data JPA (`@CreatedDate`, `@LastModifiedDate`).

### 2.2 Diagrama Entidad-Relación y Esquema DDL Estricto

**Relaciones de Dominio:**
```text
Usuario (1) ────── (1) Productor (1) ────── (N) Producto (1) ────── (N) Lote
   │                                               │
   │ (1:1..N)                                      │ (N)
   ▼                                               ▼
Establecimiento (1) ────────────────────── (N) Disponibilidad
   │
   └── Usuario ─── (N:M via usuario_rol) ─── Rol
```

**Especificación DDL Estricta (Esquema Físico Flyway V1):**

1. **`rol`**:
   - `id` INT AUTO_INCREMENT PRIMARY KEY
   - `nombre` VARCHAR(30) NOT NULL UNIQUE (Valores: `'ROLE_ADMIN'`, `'ROLE_PRODUCTOR'`, `'ROLE_HOSTELERIA'`, `'ROLE_CONSUMIDOR'`)

2. **`usuario`**:
   - `id` BIGINT AUTO_INCREMENT PRIMARY KEY
   - `email` VARCHAR(150) NOT NULL UNIQUE
   - `password` VARCHAR(255) NOT NULL (Hash BCrypt)
   - `nombre_completo` VARCHAR(120) NOT NULL
   - `telefono` VARCHAR(20) NULL
   - `activo` BOOLEAN NOT NULL DEFAULT TRUE
   - `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
   - `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

3. **`usuario_rol`**:
   - `usuario_id` BIGINT NOT NULL, `rol_id` INT NOT NULL
   - PRIMARY KEY (`usuario_id`, `rol_id`)
   - FK `fk_ur_usuario` REFERENCES `usuario`(`id`) ON DELETE CASCADE
   - FK `fk_ur_rol` REFERENCES `rol`(`id`) ON DELETE RESTRICT

4. **`productor`**:
   - `id` BIGINT AUTO_INCREMENT PRIMARY KEY
   - `usuario_id` BIGINT NOT NULL UNIQUE
   - `nombre_comercial` VARCHAR(150) NOT NULL
   - `registro_sanitario` VARCHAR(60) NULL
   - `descripcion_historia` TEXT NULL
   - `ubicacion_origen` VARCHAR(200) NOT NULL
   - `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
   - FK `fk_prod_usuario` REFERENCES `usuario`(`id`) ON DELETE RESTRICT

5. **`producto`**:
   - `id` BIGINT AUTO_INCREMENT PRIMARY KEY
   - `productor_id` BIGINT NOT NULL
   - `nombre` VARCHAR(120) NOT NULL
   - `tipo_bebida` VARCHAR(50) NOT NULL (ej. 'Vino Tinto', 'Cerveza Artesanal', 'Hidromiel')
   - `descripcion` TEXT NOT NULL
   - `presentacion` VARCHAR(50) NOT NULL (ej. 'Botella 750ml', 'Lata 330ml')
   - `precio` DECIMAL(10,2) NOT NULL CHECK (`precio` >= 0)
   - `estado` VARCHAR(20) NOT NULL DEFAULT 'ACTIVO' (Valores: `'ACTIVO'`, `'PAUSADO'`, `'RETIRADO'`)
   - `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
   - `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
   - INDEX `idx_prod_productor` (`productor_id`), INDEX `idx_prod_estado` (`estado`)
   - FK `fk_prod_productor` REFERENCES `productor`(`id`) ON DELETE RESTRICT

6. **`lote`**:
   - `id` BIGINT AUTO_INCREMENT PRIMARY KEY
   - `producto_id` BIGINT NOT NULL
   - `codigo_trazabilidad` VARCHAR(35) NOT NULL UNIQUE (Formato: `EC-YYYY-PRXX-XXXX`)
   - `fecha_produccion` DATE NOT NULL
   - `volumen_litros` DECIMAL(10,2) NOT NULL CHECK (`volumen_litros` > 0)
   - `merma_litros` DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (`merma_litros` >= 0 AND `merma_litros` <= `volumen_litros`)
   - `costo_total` DECIMAL(12,2) NOT NULL CHECK (`costo_total` >= 0)
   - `parametros_analiticos` JSON NOT NULL
   - `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
   - INDEX `idx_lote_trazabilidad` (`codigo_trazabilidad`)
   - FK `fk_lote_producto` REFERENCES `producto`(`id`) ON DELETE RESTRICT

   *Esquema formal del campo JSON `parametros_analiticos`:*
   ```json
   {
     "ph": 3.65,
     "graduacion_alcoholica": 12.8,
     "acidez_total_gl": 5.4,
     "densidad": 0.994
   }
   ```

7. **`establecimiento`**:
   - `id` BIGINT AUTO_INCREMENT PRIMARY KEY
   - `usuario_id` BIGINT NOT NULL
   - `nombre` VARCHAR(150) NOT NULL
   - `direccion` VARCHAR(255) NOT NULL
   - `ciudad` VARCHAR(80) NOT NULL
   - `activo` BOOLEAN NOT NULL DEFAULT TRUE
   - FK `fk_est_usuario` REFERENCES `usuario`(`id`) ON DELETE RESTRICT

8. **`disponibilidad`**:
   - `id` BIGINT AUTO_INCREMENT PRIMARY KEY
   - `producto_id` BIGINT NOT NULL
   - `establecimiento_id` BIGINT NOT NULL
   - `disponible` BOOLEAN NOT NULL DEFAULT TRUE
   - `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
   - UNIQUE KEY `uk_prod_est` (`producto_id`, `establecimiento_id`)
   - FK `fk_disp_producto` REFERENCES `producto`(`id`) ON DELETE CASCADE
   - FK `fk_disp_establecimiento` REFERENCES `establecimiento`(`id`) ON DELETE CASCADE

---

## 3. Integración de APIs, Rutas Web y Seguridad

### 3.1 Catálogo de Rutas Web (Spring MVC + Thymeleaf) y Endpoints REST

| Ruta / Endpoint | Método | Acceso / Rol | Tipo | Propósito |
|-----------------|--------|--------------|------|-----------|
| `/` | GET | Público | MVC | Landing page con propuesta de valor y acceso rápido. |
| `/catalogo` | GET | Público | MVC | Catálogo público con filtros y buscador de bebidas activas. |
| `/producto/{id}` | GET | Público | MVC | Detalle del producto y lista pública de lotes. |
| `/trazabilidad/{codigo}` | GET | Público | MVC | Ficha pública de trazabilidad y origen del lote por código único. |
| `/login` | GET | Anónimo | MVC | Formulario de inicio de sesión. |
| `/registro` | GET / POST | Anónimo | MVC | Registro de nuevos usuarios con rol seleccionado. |
| `/panel` | GET | Productor / Hostelería | MVC | Dashboard principal según rol de usuario. |
| `/panel/productos/**` | GET / POST | `ROLE_PRODUCTOR` | MVC | Gestión CRUD de productos (crear, editar, pausar). |
| `/panel/lotes/**` | GET / POST | `ROLE_PRODUCTOR` | MVC | Registro y consulta de lotes de producción y mermas. |
| `/panel/hosteleria/**` | GET / POST | `ROLE_HOSTELERIA` | MVC | Gestión de disponibilidad en establecimientos. |
| `/panel/financiero` | GET | `ROLE_PRODUCTOR` | MVC | Visualización de indicadores financieros y márgenes. |
| `/api/v1/trazabilidad/{codigo}` | GET | Público | REST | Retorna DTO JSON con ficha técnica pública del lote (`TrazabilidadResponseDTO`). |
| `/api/v1/disponibilidad/toggle` | POST | `ROLE_HOSTELERIA` | REST | Actualización asíncrona de disponibilidad (`DisponibilidadToggleDTO`). |
| `/api/v1/financiero/metricas` | GET | `ROLE_PRODUCTOR` | REST | Datos JSON agregados de costos y mermas para gráficos interactivos. |


**Estrategia principal: Variables de entorno en el servidor (Opción C)**

- Las credenciales (ej. `PAYU_API_KEY`, `PAYU_API_LOGIN`) se almacenan como variables de entorno en el servidor.
- Spring Boot las recibe mediante `application.properties` o `application.yml` con placeholders `${VAR_NAME}`.
- Las credenciales **nunca** se incluyen en el código fuente ni en el repositorio Git.
- Los archivos de configuración con información sensible no se suben al repositorio.

**Estrategia complementaria para producción: Secret Manager (Opción B)**
- En un entorno de producción más avanzado, se podrá utilizar un gestor de secretos (ej. AWS Secrets Manager, GCP Secret Manager) para almacenar y administrar las credenciales de forma centralizada.

**Medidas de seguridad adicionales:**
- Las claves privadas nunca estarán incluidas en el código fuente.
- El cliente no tendrá acceso a API Keys privadas.
- Las comunicaciones con servicios externos utilizarán **HTTPS**.
- Las credenciales de pasarelas de pago serán utilizadas únicamente por el backend.

---

## 4. Estrategia de Pruebas y Robustez

### 4.1 Cobertura de pruebas

**Estrategia por capas: Opciones 1 + 3 + 4 + 5**

| Opción | Tipo de prueba | Herramienta | Propósito |
|--------|----------------|-------------|-----------|
| **1** | **Unitarias (backend)** | JUnit 5 + Mockito | Verificar lógica de negocio de servicios y componentes de forma aislada. |
| **3** | **Pruebas de integración** | Spring Boot Test | Comprobar interacción entre Controller, Service y Repository con persistencia real. |
| **4** | **Pruebas E2E** | Selenium / Playwright | Validar flujos críticos completos desde la interfaz de usuario (registro, autenticación, creación de productos, registro de lotes). |
| **5** | **Pruebas de API** | RestAssured | Verificar endpoints, respuestas, validaciones, errores y control de acceso según el rol del usuario. |

**Prioridades:**
- Análisis estático previo (PreToolUse): **Checkstyle** (Google Java Style) y **Prettier** obligatorios antes de pruebas.
- Pruebas unitarias e integración: **prioridad alta** durante el desarrollo del MVP.
- Pruebas E2E: enfocadas en los **flujos críticos** del sistema.
- Pruebas de API: útiles para validar contratos y seguridad.

**Nota:** No se prioriza Jest porque el frontend se renderiza con Spring Boot MVC + Thymeleaf y JavaScript vanilla modular.

### 4.2 Esquemas de validación de datos y análisis estático

**Estrategia combinada: Opciones A + C + E + Linters**

| Opción | Esquema | Descripción |
|--------|---------|-------------|
| **Linter** | **Checkstyle + Prettier** | Validación estática de estilo, formato y convenciones de código previo a la compilación (`mvn checkstyle:check`). |
| **A** | **Jakarta Validation (Bean Validation)** | Validación principal mediante anotaciones en DTOs: `@NotNull`, `@NotBlank`, `@Email`, `@Positive`, etc. |
| **C** | **Validación manual en Service** | Reglas de negocio complejas: comprobar que una merma no supere el volumen producido, que un productor no modifique lotes ajenos, etc. |
| **E** | **Validator personalizado** | Para reglas complejas o reutilizables que requieran una lógica específica y separada. |

**Principios de validación:**
- La validación se realiza tanto en la **entrada de datos** como antes de ejecutar operaciones críticas.
- Se evitan registros incompletos, inconsistentes o no autorizados.
- No se utiliza JSON Schema inicialmente para evitar complejidad innecesaria.

---

## 5. Despliegue, DevOps y Observabilidad

### 5.1 Despliegue en producción y entornos de prueba

**Plataforma elegida: VPS con Docker Compose**

| Componente | Propósito |
|------------|-----------|
| **Spring Boot** | Aplicación principal de Entre Copas. |
| **MySQL** | Almacenamiento de la información del sistema. |
| **Docker Compose** | Administración y ejecución de los diferentes servicios. |
| **Nginx** | Proxy inverso y gestión de solicitudes hacia la aplicación. |

**Estrategia de despliegue:**
- Durante el desarrollo: ejecución local con Docker Compose.
- En producción: misma configuración en un VPS (ej. DigitalOcean, Linode).
- Infraestructura sencilla, controlable y compatible con el stack seleccionado.
- No se incorporan servicios cloud adicionales innecesarios para el MVP.

### 5.2 Telemetría, observabilidad y operaciones

**Estrategia combinada: Opciones A + B + C + E**

| Opción | Herramienta / Práctica | Propósito |
|--------|------------------------|-----------|
| **A** | **Logs estructurados** | Spring Boot con sistema de logging para registrar errores, eventos y operaciones importantes. |
| **B** | **Métricas (Micrometer + Prometheus + Grafana)** | Monitoreo de tiempos de respuesta, tasa de errores, consumo de recursos (principalmente en producción). |
| **C** | **Alertas** | Notificaciones ante eventos críticos: indisponibilidad de BD, errores recurrentes del servidor, etc. |
| **E** | **Tareas programadas (Spring Scheduled)** | Procesos automáticos de mantenimiento y tareas periódicas necesarias para la operación del sistema. |

**Nota:** No se incorpora Redis inicialmente. MySQL será suficiente para el MVP. La necesidad de una solución de caché se evaluará posteriormente según el rendimiento de la aplicación.

---

## 6. Resumen Ejecutivo del TRD

| Área | Decisión |
|------|----------|
| **Stack** | Java 17 + Spring Boot 3.x (MVC + Thymeleaf) + Maven + MySQL 8 + Bootstrap 5 + JS |
| **Arquitectura** | Monolito modular empaquetado por dominio (Package by Feature) |
| **Persistencia** | Spring Data JPA / Hibernate con MySQL 8 (soporte JSON) |
| **Seguridad** | Spring Security + RBAC + filtrado en capa Service |
| **Migraciones** | Flyway (V1__init_schema.sql con DDL estricto) |
| **Auditoría** | Automática para entidades críticas (@CreatedDate, @LastModifiedDate) |
| **Pruebas y Calidad** | Checkstyle + Prettier + JUnit 5 + Mockito + Spring Boot Test + RestAssured + Selenium |
| **Validación de datos** | Jakarta Validation (DTOs) + Reglas en Service + Validators personalizados |
| **Despliegue** | VPS con Docker Compose + Nginx proxy |
| **Observabilidad** | Logs estructurados + Micrometer (Prometheus/Grafana) + Spring Scheduled |

---

*Documento generado a partir de la entrevista de descubrimiento de producto.*
*Fecha: 2026-09-02*