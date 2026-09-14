# User Flow - Flujo de Navegación
## Entre Copas - Plataforma de gestión, trazabilidad y comercialización de bebidas artesanales

---

## 1. Punto de Partida y Estado Inicial (entry point)

### 1.1 Punto de entrada

El usuario llega a **Entre Copas** mediante una **landing page pública con acceso al catálogo**, sin necesidad de autenticarse inicialmente.

**Estructura de la landing page:**
- Menú de navegación con acceso a:
  - **Inicio** (`/`)
  - **Catálogo** (`/catalogo`)
  - **Consulta de Trazabilidad** (`/trazabilidad/{codigo}`)
  - **Iniciar sesión** (`/login`)
  - **Registrarse** (`/registro`)

**Comportamiento:**
- El visitante puede explorar los productos disponibles y consultar información pública de trazabilidad sin autenticarse.
- Cuando intenta acceder a funcionalidades privadas o de gestión, el sistema solicita autenticación.

### 1.2 Diferencias entre usuario anónimo y autenticado

| Estado | Acceso | Funcionalidades disponibles |
|--------|--------|-----------------------------|
| **Visitante / Anónimo** | Sin autenticación | Landing page, catálogo público, consulta pública de trazabilidad (`/trazabilidad/{codigo}`). |
| **Productor autenticado** | Login con credenciales | Panel de gestión: administración de productos y lotes, dashboard financiero. |
| **Hostelería autenticada** | Login con credenciales | Gestión de disponibilidad de productos en su establecimiento. |
| **Consumidor autenticado** | Login con credenciales | Catálogo con precios, carrito de compras (Fase 2), historial de pedidos (Fase 2). |

**Principio de seguridad:** El sistema aplica control de acceso basado en roles (RBAC), evitando que un usuario pueda consultar o modificar información privada que no le corresponda.

---

## 2. El "Happy Path" o Camino Crítico Paso a Paso

### 2.1 Happy Path A — Productor: registro de producto y lote

| Paso | Acción | Pantalla | Comportamiento del sistema |
|------|--------|----------|----------------------------|
| 1 | El productor ingresa a la landing pública. | Landing | Muestra información general y menú de navegación. |
| 2 | Selecciona **"Iniciar sesión"** e ingresa credenciales. | `/login` | Valida credenciales y autentica al usuario. |
| 3 | Es redirigido a su panel de gestión. | `/panel` | Muestra listado de productos, lotes y acceso al dashboard financiero. |
| 4 | Selecciona **"Nuevo Producto"**. | Formulario de producto | Muestra formulario con campos: nombre, tipo, descripción, presentación, precio. |
| 5 | Completa los datos requeridos y selecciona **"Guardar"**. | Formulario de producto | Valida campos obligatorios y formato. |
| 6 | El sistema registra el producto. | Listado de productos | Muestra el producto en el listado del productor. |
| 7 | Desde el producto, selecciona **"Registrar Lote"**. | Formulario de lote | Muestra formulario con campos: fecha, volumen, parámetros (pH, alcohol, acidez, densidad), costos, mermas. |
| 8 | Completa los datos del lote y selecciona **"Guardar"**. | Formulario de lote | Valida datos (volumen > 0, costos válidos, etc.). |
| 9 | El sistema crea el lote y genera código de trazabilidad único (`EC-YYYY-PRXX-XXXX`). | Confirmación de lote | Muestra mensaje de éxito y el código de trazabilidad generado. |
| 10 | El productor visualiza el lote en su listado. | Listado de lotes | Muestra el lote asociado al producto con su código de trazabilidad. |

**Resultado esperado:** El productor ha registrado correctamente un producto y su respectivo lote, quedando ambos almacenados y relacionados dentro del sistema, con un código único que permite consultar posteriormente la trazabilidad pública del lote.

---

### 2.2 Happy Path B — Visitante/Consumidor: consulta de catálogo y trazabilidad

| Paso | Acción | Pantalla | Comportamiento del sistema |
|------|--------|----------|----------------------------|
| 1 | El visitante ingresa a la landing pública. | Landing | Muestra información general y menú de navegación. |
| 2 | Selecciona **"Catálogo"** o accede a `/catalogo`. | `/catalogo` | Muestra lista de productos activos con información básica. |
| 3 | (Opcional) Utiliza buscador o filtros. | `/catalogo` | Filtra productos por nombre o tipo de bebida. |
| 4 | Selecciona un producto de su interés. | Detalle de producto (`/producto/{id}`) | Muestra información detallada del producto y sección de trazabilidad. |
| 5 | Selecciona **"Ver trazabilidad"**. | Ficha de trazabilidad (`/trazabilidad/{codigo}`) | Muestra información pública del lote: código, fecha, parámetros analíticos públicos. |
| 6 | Consulta la información de origen del producto. | Ficha de trazabilidad (`/trazabilidad/{codigo}`) | Visualiza los datos sin necesidad de autenticación. |
| 7 | (Opcional) Regresa al catálogo o al detalle del producto. | `/catalogo` o `/producto/{id}` | Navegación fluida sin pérdida de contexto. |

**Resultado esperado:** El visitante o consumidor puede localizar un producto en el catálogo y consultar su información de trazabilidad de forma pública, sencilla y sin acceder a información privada del productor.

---

## 3. Bifurcaciones y Escenarios Alternativos (edge cases)

### 3.1 Autenticación y registro

| Escenario | Bifurcación / Falla | Comportamiento esperado |
|-----------|---------------------|--------------------------|
| Inicio de sesión | Credenciales incorrectas | Mostrar mensaje de error, no permitir acceso, mantener formulario sin contraseña. |
| Inicio de sesión | Usuario inexistente | Mostrar mensaje de error sin revelar información sensible. |
| Inicio de sesión | Cuenta desactivada | Informar que la cuenta no está activa y evitar el acceso. |
| Registro | Correo ya registrado | Rechazar el registro y mostrar mensaje de correo ya asociado. |
| Registro | Campos obligatorios vacíos | Resaltar campos faltantes y mostrar errores específicos. |
| Registro | Formato de correo inválido | Mostrar error de validación y solicitar correo válido. |
| Registro | Contraseña que no cumple requisitos | Informar requisitos y evitar el registro. |
| Sesión | Sesión expirada | Redirigir a login e informar que debe autenticarse nuevamente. |
| Navegación | Autenticado intenta volver a pantalla pública | Permitir navegación pública sin perder sesión. |

### 3.2 Gestión de productos y lotes

| Escenario | Bifurcación / Falla | Comportamiento esperado |
|-----------|---------------------|--------------------------|
| Registro de producto | Campos obligatorios vacíos | Resaltar campos, mostrar errores y no guardar. |
| Registro de producto | Precio inválido o negativo | Rechazar valor y mostrar mensaje de error. |
| Registro de producto | Error durante el guardado | Mostrar error y evitar registro incompleto. |
| Edición de producto | Producto inexistente | Mostrar mensaje y regresar al listado. |
| Producto | Producto pausado o retirado | No mostrarlo como disponible en catálogo. |
| Registro de lote | Campos obligatorios incompletos | Resaltar campos faltantes y evitar registro. |
| Registro de lote | Volumen cero o negativo | Rechazar valor y mostrar error en el campo. |
| Registro de lote | Costos o valores negativos | Rechazar valores e indicar cuáles corregir. |
| Registro de lote | Fecha de producción inválida | Rechazar fecha según reglas de negocio. |
| Registro de lote | Parámetros analíticos incompletos | Informar datos faltantes y evitar guardado. |
| Registro de lote | Código de trazabilidad duplicado | Impedir duplicado y generar código único. |
| Lote | Error al guardar información | No crear registros incompletos y permitir reintento. |
| Lote | Productor intenta modificar lote ajeno | Denegar operación y mostrar mensaje de falta de permisos. |

### 3.3 Catálogo y trazabilidad

| Escenario | Bifurcación / Falla | Comportamiento esperado |
|-----------|---------------------|--------------------------|
| Catálogo | No existen productos activos | Mostrar estado vacío: "No hay productos disponibles en este momento." |
| Catálogo | Búsqueda sin resultados | Mostrar mensaje de no coincidencia. |
| Catálogo | Producto pausado o retirado | No presentarlo como disponible. |
| Catálogo | Error al cargar productos | Mostrar mensaje de error y opción de reintentar. |
| Carga de datos | Tiempo de espera prolongado | Mostrar indicador de carga y evitar acciones duplicadas. |
| Detalle de producto | Producto no encontrado | Mostrar mensaje de recurso no disponible y ofrecer regresar al catálogo. |
| Trazabilidad | Lote sin información pública | Mostrar: "Este lote no tiene información de trazabilidad pública disponible." |
| Trazabilidad | Código de trazabilidad inexistente | Informar que no se encontró ningún lote asociado. |
| Trazabilidad | Información de trazabilidad incompleta | Mostrar únicamente información pública disponible. |
| Trazabilidad | Error al consultar el lote | Mostrar mensaje de error y permitir reintentar. |
| Privacidad | Visitante intenta consultar información privada | Mostrar únicamente datos públicos, sin exponer costos ni información financiera. |

### 3.4 Accesos no autorizados y RBAC

| Escenario | Bifurcación / Falla | Comportamiento esperado |
|-----------|---------------------|--------------------------|
| Acceso directo | Visitante intenta acceder al panel de gestión | Redirigir a login o mostrar mensaje de autenticación requerida. |
| Permisos | Consumidor intenta registrar un producto | Denegar operación: "No tiene permisos para realizar esta acción." |
| Permisos | Consumidor intenta registrar un lote | Denegar operación y no modificar información. |
| Permisos | Hostelería intenta modificar producto de productor | Denegar operación y mantener datos sin cambios. |
| Permisos | Hostelería intenta acceder a información financiera | Denegar acceso (información privada del productor). |
| Propiedad | Productor intenta acceder a información de otro productor | Denegar consulta aunque conozca la URL o ID. |
| Privilegios | Usuario intenta modificar su rol mediante petición manipulada | Rechazar operación y conservar rol asignado. |
| Sesión | Usuario sin autenticación intenta acceder a ruta protegida | Bloquear acceso y redirigir a login. |
| Seguridad | Usuario intenta acceder a URL no permitida para su rol | Backend debe validar permisos y devolver acceso denegado. |

### 3.5 Comportamiento general ante errores

- Ante cualquier bifurcación o falla, el sistema debe evitar **estados inconsistentes**.
- Una operación que no haya sido completada correctamente no debe presentarse como exitosa ni generar registros incompletos o duplicados.
- Los mensajes deben ser **claros para el usuario**, pero sin revelar información sensible del sistema.
- Cuando sea posible, la interfaz debe permitir **corregir datos, reintentar la operación o regresar** a la pantalla anterior.
- Las restricciones de permisos deben validarse en el **backend** mediante Spring Security y la lógica de negocio, no únicamente ocultando botones o elementos de la interfaz.

---

## 4. Estados de la Interfaz: Carga, Vacío y Errores (UI states)

### 4.1 Estados por pantalla clave

| Pantalla | Estado de carga | Estado vacío | Estado de error |
|----------|----------------|--------------|-----------------|
| **Catálogo público** | Indicador de carga centrado mientras se consultan productos. | "No hay productos disponibles en este momento" con ilustración o sugerencia de filtros. | "No fue posible cargar los productos" con botón **"Reintentar"**. |
| **Detalle de producto / Trazabilidad** | Indicador de carga mientras se obtiene información del producto o lote. | "No hay información de trazabilidad pública disponible." | Mensaje de recurso no disponible con opción **"Volver al catálogo"** o **"Reintentar"**. |
| **Panel del productor** | Indicadores de carga en listados de productos y lotes. Botones de acciones deshabilitados temporalmente. | "Aún no has registrado productos" con botón **"Crear producto"**. "Este producto aún no tiene lotes registrados" con botón **"Registrar lote"**. | "No fue posible cargar la información" con opción **"Reintentar"** sin abandonar el panel. |
| **Formulario de registro de usuario** | Botón deshabilitado con texto **"Registrando..."** para evitar duplicados. | No aplica. | Errores de validación junto a cada campo (correo inválido, contraseña débil, correo ya registrado). |
| **Formulario de producto** | Botón deshabilitado con texto **"Guardando..."**. | No aplica. | Errores de validación debajo de los campos correspondientes y mensaje general de error de guardado. |
| **Formulario de lote** | Botón deshabilitado con texto **"Guardando..."**. | No aplica. | Errores inline para datos inválidos: volumen cero/negativo, costos inválidos, fecha incorrecta, información obligatoria faltante. |
| **Dashboard financiero** | Indicadores o skeletons de carga mientras se calculan datos. | "Aún no hay información financiera disponible para mostrar." | "No fue posible cargar la información financiera" con opción **"Reintentar"**. No mostrar cifras incompletas como definitivas. |

### 4.2 Reglas generales de los estados de interfaz

| Regla | Descripción |
|-------|-------------|
| **Carga** | Debe existir una señal visual clara de que el sistema está procesando o consultando información. |
| **Vacío** | Debe explicar que no existen datos, diferenciándolo de un error de carga. |
| **Error** | Debe explicar de forma comprensible qué ocurrió y, cuando sea posible, ofrecer una acción para solucionarlo. |
| **Validación** | Los errores deben mostrarse preferentemente junto al campo que necesita corrección. |
| **Acciones durante carga** | Los botones de envío deben deshabilitarse temporalmente para evitar operaciones duplicadas. |
| **Persistencia del formulario** | Cuando ocurra un error del servidor, se deben conservar los datos ingresados siempre que sea seguro hacerlo, evitando que el usuario tenga que diligenciar nuevamente todo el formulario. |
| **Mensajes** | Deben ser claros para el usuario y no exponer información técnica innecesaria, como excepciones, consultas SQL o detalles internos del servidor. |

---

## 5. Puntos de Salida y Feedback de Cierre (success & loopback)

### 5.1 Confirmación y redirección por acción

| Acción exitosa | Pantalla / feedback de confirmación | Redirección o siguiente acción |
|----------------|-------------------------------------|--------------------------------|
| **Registro de usuario** | "Cuenta creada exitosamente." Si se requiere confirmación de correo, indicar que debe revisar su correo para activar la cuenta. | Redirigir a **Iniciar sesión (`/login`)**. |
| **Login** | No requiere pantalla de confirmación independiente. | Redirigir automáticamente al panel correspondiente según el rol del usuario. |
| **Registro de producto** | "Producto creado exitosamente" mediante notificación o mensaje de confirmación. | Redirigir al listado o detalle del producto, ofreciendo **"Registrar lote"** como acción principal. |
| **Registro de lote** | "Lote registrado correctamente" junto con el **código de trazabilidad único generado**. | Mostrar acciones: **"Ver lote"**, **"Volver al listado"** y opción de registrar otro lote para el mismo producto. |
| **Consulta de trazabilidad** | No requiere confirmación de éxito (acción de consulta). | Mantener al visitante en la pantalla de información del lote, mostrando datos públicos de trazabilidad. Desde allí puede regresar al detalle del producto o al catálogo. |

### 5.2 Flujo de cierre principal del Productor

El flujo de registro del Productor debe facilitar la continuidad de las operaciones:
Login → Panel → Nuevo producto → Producto creado → Registrar lote → Lote creado →
Código de trazabilidad → Ver lote / Volver al listado


**Principio:** El registro del producto no se considera completamente terminado desde la perspectiva del flujo de producción hasta que el usuario pueda continuar fácilmente con el registro de su lote.

### 5.3 Flujo de cierre del Visitante/Consumidor

El flujo público mantiene una navegación sencilla y sin fricción:
Landing → Catálogo → Producto → Trazabilidad → Consulta de información pública →
Volver al producto o catálogo


**Nota:** La consulta de trazabilidad no requiere autenticación ni una pantalla de confirmación, debido a que constituye una funcionalidad pública y de consulta.

---

## 6. Resumen del User Flow

| Área | Decisión |
|------|----------|
| **Punto de entrada** | Landing pública con catálogo accesible sin autenticación. |
| **Diferencias anónimo/autenticado** | Anónimo: solo consulta pública. Autenticado: acceso a funcionalidades según rol (productor, hostelería, consumidor). |
| **Happy Path Productor** | Login → Panel → Crear producto → Registrar lote → Código de trazabilidad. |
| **Happy Path Visitante** | Landing → Catálogo → Detalle de producto → Trazabilidad pública. |
| **Bifurcaciones** | Cubiertas para autenticación, gestión de productos/lotes, catálogo, trazabilidad y accesos no autorizados. |
| **Estados de UI** | Carga, vacío y error definidos para todas las pantallas clave. |
| **Feedback de cierre** | Confirmaciones claras y redirecciones lógicas para cada acción exitosa. |

---

*Documento generado a partir de la entrevista de descubrimiento de producto.*
*Fecha: 2026-09-02*