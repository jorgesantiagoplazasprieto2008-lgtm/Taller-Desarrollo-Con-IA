# PLAN - Roadmap de Desarrollo
## Entre Copas - Plataforma de gestión, trazabilidad y comercialización de bebidas artesanales

---

## 1. MVP vs. Iteraciones Futuras (priorización)

### 1.1 MVP (Fase 1) - Funcionalidades incluidas

| # | Funcionalidad | Estado |
|---|---------------|--------|
| 1 | Registro y autenticación de usuarios | ✅ MVP |
| 2 | Gestión de roles y permisos (RBAC) | ✅ MVP |
| 3 | Gestión de productores | ✅ MVP |
| 4 | Gestión de productos (CRUD) | ✅ MVP |
| 5 | Registro y gestión de lotes | ✅ MVP (núcleo) |
| 6 | Trazabilidad mediante código único por lote | ✅ MVP |
| 7 | Consulta del catálogo público | ✅ MVP |
| 8 | Consulta pública de información de trazabilidad | ✅ MVP |
| 9 | Gestión de disponibilidad en establecimientos | ✅ MVP |
| 10 | Dashboard financiero básico | ✅ MVP |

**Nota:** El Dashboard financiero estará limitado a información básica de gestión (costos, mermas e indicadores básicos de rentabilidad). Los reportes avanzados y análisis detallados se incorporarán posteriormente.

### 1.2 Fase 2 — E-commerce y comercialización

| Prioridad | Funcionalidad | Descripción |
|-----------|---------------|-------------|
| 1 | Carrito de compras | Agregar, modificar y eliminar productos, controlar cantidades y calcular totales. |
| 2 | Gestión de pedidos | Creación, almacenamiento y consulta de pedidos y sus estados. |
| 3 | Integración con PayU | Pasarela de pagos para completar las compras. |
| 4 | Historial y seguimiento de pedidos | Consulta de compras realizadas y estado de los pedidos. |
| 5 | Validación de disponibilidad | Comprobación de disponibilidad antes y durante la confirmación de la compra. |

### 1.3 Fase 3 — Analítica y servicios complementarios

| Prioridad | Funcionalidad | Descripción |
|-----------|---------------|-------------|
| 1 | Reportes y analytics avanzados | Estadísticas de ventas, productos, lotes, rentabilidad y comportamiento de usuarios. |
| 2 | Integración con mapas | Ubicación de establecimientos donde se encuentran disponibles los productos. |
| 3 | Notificaciones por correo y push | Comunicación de eventos importantes: pedidos, pagos, cambios de estado. |
| 4 | Integración con servicios de envío | Cálculo de costos, gestión y seguimiento de envíos. |
| 5 | Funcionalidades comerciales avanzadas | Promociones, recomendaciones, favoritos y otras según necesidades identificadas post-MVP. |

---

## 2. Estructura de Hitos Secuenciales (milestones)

**Enfoque elegido: Por características completas (verticales).**

Cada hito desarrolla una funcionalidad de principio a fin, incluyendo backend, persistencia, lógica de negocio, seguridad cuando corresponda y las respectivas vistas de usuario.

### Hito 0 — Infraestructura y arquitectura base

**Entregable:**
- Proyecto Spring Boot configurado.
- Maven configurado.
- Conexión con MySQL.
- MySQL Workbench para diseño y administración de la BD.
- Flyway configurado para migraciones.
- Docker Compose configurado.
- Estructura modular de paquetes.
- Configuración inicial de manejo de excepciones y respuestas.
- Configuración base de Spring Security.
- Repositorio Git configurado.

**Criterios de aceptación:**
- El proyecto compila correctamente (`mvn clean install`).
- La conexión a MySQL es exitosa.
- Flyway ejecuta las migraciones iniciales sin errores.
- Docker Compose levanta todos los servicios correctamente.

---

### Hito 1 — Usuarios y seguridad

**Entregable:**
- Entidad Usuario y Rol.
- Registro de usuarios.
- Inicio y cierre de sesión.
- Spring Security configurado.
- Control de acceso basado en roles (RBAC).
- Protección de rutas y funcionalidades.
- Vistas de registro y autenticación (HTML + Bootstrap).
- Validaciones correspondientes.

**Criterios de aceptación:**
- Un usuario puede registrarse con datos válidos.
- Un usuario puede iniciar sesión con credenciales correctas.
- Credenciales incorrectas muestran error sin revelar información sensible.
- Un usuario autenticado no puede acceder a rutas de otro rol.
- Las contraseñas se almacenan de forma segura (hash).

---

### Hito 2 — Gestión de productos

**Entregable:**
- Creación de productos por parte del productor.
- Consulta, edición y actualización de productos.
- Activación, pausa y retiro de productos.
- Validaciones de información (campos obligatorios, precios válidos).
- Catálogo público de productos activos.
- Vistas HTML correspondientes (listado, formulario, detalle).
- Pruebas unitarias e integración de la funcionalidad.

**Criterios de aceptación:**
- Un productor puede crear un producto con información válida.
- El producto aparece en su listado y en el catálogo público si está activo.
- Un producto pausado o retirado no aparece en el catálogo.
- Un consumidor no puede crear, editar o eliminar productos.
- Las validaciones impiden datos inválidos.

---

### Hito 3 — Gestión de lotes y trazabilidad

**Entregable:**
- Registro y gestión de lotes.
- Asociación entre productos y lotes.
- Registro de información de producción (fecha, volumen, parámetros analíticos).
- Registro de costos y mermas.
- Generación de código único de trazabilidad.
- Consulta pública de trazabilidad.
- Validación de permisos sobre los lotes.
- Vistas HTML correspondientes.
- Pruebas unitarias e integración de la funcionalidad.

**Criterios de aceptación:**
- Un productor puede registrar un lote asociado a un producto propio.
- El sistema genera un código de trazabilidad único para cada lote.
- El lote aparece en el listado del productor.
- Un visitante puede consultar la trazabilidad pública de un lote.
- Un productor no puede modificar lotes de otro productor.
- Las validaciones impiden datos inválidos (volumen negativo, fechas futuras, etc.).

---

### Hito 4 — Establecimientos y disponibilidad

**Entregable:**
- Registro y gestión de establecimientos.
- Asociación de establecimientos con usuarios de hostelería.
- Gestión de disponibilidad de productos.
- Consulta de establecimientos donde un producto está disponible.
- Actualización del catálogo según la disponibilidad.
- Control de permisos.
- Vistas HTML correspondientes.
- Pruebas unitarias e integración de la funcionalidad.

**Criterios de aceptación:**
- Un usuario de hostelería puede gestionar la disponibilidad de productos en su establecimiento.
- Un consumidor puede consultar en qué establecimientos está disponible un producto.
- Un producto marcado como no disponible no aparece como disponible para el consumidor.
- Un productor no puede gestionar la disponibilidad de otro productor.
- La disponibilidad se refleja inmediatamente en el catálogo.

---

### Hito 5 — Dashboard financiero básico

**Entregable:**
- Consulta de costos de producción.
- Consulta de mermas.
- Indicadores básicos de rentabilidad.
- Información organizada por producto y/o lote.
- Restricción de acceso a la información financiera del productor.
- Estados vacíos y manejo de errores.
- Vistas HTML correspondientes.
- Pruebas unitarias e integración de la funcionalidad.

**Criterios de aceptación:**
- Un productor puede consultar costos, mermas y márgenes de sus productos y lotes.
- Los datos mostrados corresponden únicamente a los registros del productor autenticado.
- Un productor no puede consultar información financiera de otro productor.
- Los estados de carga y vacío se manejan correctamente.
- Los datos se actualizan correctamente cuando se registran nuevos lotes.

---

### Hito 6 — Validación integral y despliegue del MVP

**Entregable:**
- Pruebas E2E de los flujos críticos.
- Validación de permisos y roles (RBAC).
- Corrección de errores encontrados.
- Validación de persistencia y consistencia de datos.
- Configuración del entorno de producción.
- Despliegue mediante Docker Compose en VPS.
- Configuración de Nginx (proxy inverso).
- Validación final del MVP con stakeholders.

**Criterios de aceptación:**
- Todos los flujos críticos funcionan correctamente.
- Las validaciones de seguridad (RBAC) se aplican consistentemente.
- No hay regresiones en funcionalidades de hitos anteriores.
- El despliegue en producción es exitoso y accesible.
- Los stakeholders piloto pueden probar la aplicación.

---

## 3. Criterios de Parada y Puertas de Calidad (validation gates)

Cada hito deberá superar las siguientes puertas de calidad antes de considerarse terminado:

| Gate | Criterio | Descripción |
|------|----------|-------------|
| **G0** | Linter y formato estático (PreToolUse) | `mvn checkstyle:check` y validación de sintaxis de plantillas/JS sin advertencias de estilo o violaciones de convención. |
| **G1** | Código compila y pruebas unitarias pasan | `mvn clean test` debe pasar sin errores. Todas las pruebas unitarias del hito deben estar en verde. |
| **G2** | Pruebas de integración pasan | La interacción entre capas (Controller → Service → Repository) y la persistencia en BD debe funcionar correctamente. |
| **G3** | Validación manual | La funcionalidad será revisada en un entorno local o de pruebas para comprobar que cumple los criterios de aceptación definidos en el PRD. |
| **G4** | No existen regresiones | Las funcionalidades desarrolladas en hitos anteriores deben continuar funcionando correctamente (suite completa). |
| **G5** | Migraciones Flyway correctas | Cuando se modifique el esquema de BD, las migraciones deben ejecutarse sin errores y mantener el esquema versionado. |
| **G6** | Documentación mínima | El código y las funcionalidades críticas deberán contar con la documentación necesaria (JavaDoc o comentarios) para facilitar su comprensión y mantenimiento. |
| **G7** | Seguridad y permisos | Las funcionalidades deberán respetar los roles, permisos y restricciones de acceso definidos en el RBAC. |

**Obligatorios para cerrar un hito:** G0, G1, G2, G3, G4, G5 (cuando corresponda) y G7 (cuando exista control de acceso). G6 se mantiene durante el desarrollo y se completa antes de la entrega.

### Intervención del validador humano

El validador humano (stakeholder/product owner) intervendrá **al finalizar cada hito**, realizando una revisión funcional antes de autorizar el inicio del siguiente.

**Durante la revisión se comprobará:**
- Que la funcionalidad cumple los criterios de aceptación.
- Que el flujo de usuario funciona correctamente.
- Que las validaciones y mensajes de error son adecuados.
- Que los permisos corresponden al rol del usuario.
- Que no se hayan afectado funcionalidades anteriores.
- Que los datos almacenados sean correctos.

Una vez superada la revisión, se dará el **visto bueno para avanzar al siguiente hito**.

Adicionalmente, al finalizar el Hito 6 se realizará una **revisión integral del MVP** antes de considerarlo listo para su despliegue definitivo.

---

## 4. Tamaño de Tareas y Modularidad (presupuesto de contexto)

### 4.1 Granularidad de las tareas

Se utilizará una **granularidad intermedia entre micro-tareas y bloques medianos**.

**Principios:**
- Cada ticket representará una **funcionalidad concreta y comprobable**.
- Incluirá todos los componentes necesarios para completarla (entidad, repositorio, servicio, controlador, validaciones, persistencia, vista).
- Se evitará dividir excesivamente una funcionalidad en tareas individuales por cada archivo o método.
- También se evitarán tickets demasiado grandes que involucren módulos completos sin una funcionalidad específica.

**Ejemplo de ticket bien dimensionado:**
> "Implementar registro de usuarios" → incluye entidad Usuario, repositorio, servicio, controlador, validaciones, persistencia y vista de registro.

### 4.2 Estructura de paquetes y modularidad

El código se organizará principalmente por **módulos funcionales del negocio**, manteniendo dentro de cada módulo sus respectivas capas.

**Módulos principales:**
src/main/java/com/entrecopas/
│
├── usuario/ # Gestión de usuarios y autenticación
│ ├── controller/
│ ├── service/
│ ├── repository/
│ ├── model/
│ ├── dto/
│ └── mapper/
│
├── productor/ # Gestión de productores
│ └── (estructura similar)
│
├── producto/ # Gestión de productos y catálogo
│ └── (estructura similar)
│
├── lote/ # Gestión de lotes y trazabilidad
│ └── (estructura similar)
│
├── establecimiento/ # Gestión de establecimientos
│ └── (estructura similar)
│
├── disponibilidad/ # Gestión de disponibilidad
│ └── (estructura similar)
│
├── financiero/ # Dashboard financiero
│ └── (estructura similar)
│
├── config/ # Configuración transversal
│ ├── SecurityConfig.java
│ ├── WebConfig.java
│ └── ExceptionHandler.java
│
└── exception/ # Excepciones personalizadas


**Ventajas de esta estructura:**
- Código organizado y fácil de navegar.
- Facilita el mantenimiento y la evolución del proyecto.
- Evita que el crecimiento del proyecto genere una estructura difícil de manejar.
- Permite trabajar en módulos de forma aislada sin afectar a otros.

### 4.3 Protocolo de Persistencia Agéntica y Control de Estado

Para garantizar la continuidad del desarrollo mediante agentes autónomos y evitar el *Understanding Rot* o la pérdida de contexto entre turnos de trabajo, se establece el siguiente protocolo estricto:

1. **Archivo de Estado de Sesión Agéntica (`.agents/session_state.json`):**
   - El agente debe leer este archivo antes de iniciar cualquier tarea y actualizarlo inmediatamente al completar una operación o superar una compuerta.
   - Esquema formal del estado:
     ```json
     {
       "hito_activo": 1,
       "ticket_activo": "US-01: Registro de Usuarios y Roles",
       "status": "EN_PROGRESO", // [PENDIENTE, EN_PROGRESO, EN_REVISION, COMPLETADO]
       "archivos_modificados": ["usuario/model/Usuario.java", "usuario/service/UsuarioService.java"],
       "ultimos_tests_ejecutados": {
         "suite": "UsuarioServiceTest",
         "resultado": "PASSED",
         "cobertura": "92%"
       },
       "proximo_paso": "Implementar UsuarioController y validación de formulario"
     }
     ```
2. **Protocolo de Checkpoints y Reintentos (Anti-Loop):**
   - Ante fallos de compilación o pruebas, el agente tiene un límite estricto de **3 reintentos autónomos**. Si al tercer intento la prueba no pasa, el agente debe detenerse, registrar el diagnóstico en `scratch/agent_errors.log` y consultar al usuario.
   - Prohibido modificar archivos fuera del dominio del ticket activo sin previa justificación explícita.
   - Cada hito debe consolidarse con un commit semántico en Git tras la validación humana del Gate G3.

---

## 5. Despliegue y Feedback Externo (vibe deploying)

### 5.1 Estrategia de despliegue en vivo

**Estrategia seleccionada: Despliegues periódicos cada dos hitos (Opción 3)**

| Momento | Acción | Propósito |
|---------|--------|-----------|
| **Hitos 0–1** | Desarrollo y pruebas locales | Establecer base sólida sin exposición externa. |
| **Después del Hito 2** | Primer despliegue de prueba | Validar gestión de productos y autenticación con stakeholders. |
| **Después del Hito 4** | Segundo despliegue de prueba | Validar funcionalidades integradas (lotes + disponibilidad). |
| **Después del Hito 5** | Versión candidata del MVP | Validación completa antes del despliegue final. |
| **Hito 6** | Despliegue y validación final | MVP listo para producción. |

### 5.2 Primeros stakeholders en probar

| Orden | Perfil | Propósito |
|-------|--------|-----------|
| 1 | **Equipo interno** | Validación técnica y funcional inicial. |
| 2 | **Productor artesanal piloto** | Validación de gestión de productos, lotes y dashboard financiero. |
| 3 | **Usuario de hostelería piloto** | Validación de gestión de disponibilidad. |
| 4 | **Grupo reducido de consumidores de prueba** | Validación de catálogo, trazabilidad y experiencia de usuario. |

### 5.3 Mecanismo de feedback

El feedback obtenido en cada despliegue de prueba se utilizará para:

1. Identificar **errores** no detectados en pruebas internas.
2. Detectar **problemas de usabilidad** en flujos reales.
3. Ajustar **prioridades** para las siguientes fases.
4. Validar que la solución resuelve el problema real del negocio.

### 5.4 Infraestructura de despliegue

**Entorno de desarrollo/pruebas:** Local con Docker Compose.

**Entorno de staging/pruebas externas:** VPS (ej. DigitalOcean, Linode) con Docker Compose + Nginx.

**Entorno de producción:** Misma configuración que staging, con variables de entorno para credenciales reales y configuración optimizada.

---

## 6. Resumen del Roadmap

| Fase | Hitos | Entregable principal |
|------|-------|----------------------|
| **Base** | Hito 0 | Infraestructura y arquitectura. |
| **Núcleo** | Hito 1 | Usuarios y seguridad. |
| **Núcleo** | Hito 2 | Gestión de productos y catálogo. |
| **Núcleo** | Hito 3 | Gestión de lotes y trazabilidad. |
| **Núcleo** | Hito 4 | Establecimientos y disponibilidad. |
| **Núcleo** | Hito 5 | Dashboard financiero básico. |
| **Cierre** | Hito 6 | Validación integral y despliegue del MVP. |

---

*Documento generado a partir de la entrevista de descubrimiento de producto.*
*Fecha: 2026-09-02*