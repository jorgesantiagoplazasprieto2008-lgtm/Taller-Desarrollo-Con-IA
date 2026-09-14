# PRD - Product Requirements Document
## Entre Copas - Plataforma de gestión, trazabilidad y comercialización de bebidas artesanales

---

## 1. El Problema y los Objetivos de Éxito

### 1.1 Problema
Los productores artesanales presentan dificultades para llevar un control centralizado y trazable de sus productos y lotes de producción. La información relacionada con origen, características analíticas, parámetros físicos, costos, mermas y disponibilidad se encuentra dispersa en registros manuales o herramientas independientes, dificultando:

- Conocer el estado real de cada lote.
- Mantener información actualizada de los productos.
- Tomar decisiones comerciales basadas en datos.

### 1.2 Propuesta de valor
**Entre Copas** centraliza la gestión, trazabilidad y comercialización de bebidas artesanales en una sola plataforma, permitiendo a productores controlar sus lotes y productos, mientras consumidores y establecimientos pueden consultar información y disponibilidad de manera sencilla y transparente.

### 1.3 KPIs de éxito
| KPI | Objetivo |
|-----|----------|
| % de lotes registrados correctamente | ≥ 95% |
| Tiempo promedio de registro de un lote | ≤ 3 minutos (reducción ≥ 70% frente al proceso manual en papel/hojas de cálculo) |
| % de registros con información completa | ≥ 95% |
| Tasa de interacción con el catálogo y trazabilidad (MVP) | ≥ 60% de visitantes únicos consultan la ficha técnica o trazabilidad de al menos un producto |

### 1.4 Modelo Emocional y Vibe de la Plataforma
- **Vibe y Personalidad:** Artesanal, auténtico, cálido, confiable y moderno. Debe transmitir el orgullo del trabajo de la tierra y la maestría del productor artesanal, combinándolo con la precisión técnica de la trazabilidad.
- **Paleta de Colores Curada:**
  - Color Primario: Borgoña / Vino Noble (`#58111A`).
  - Color Secundario / Acento: Ámbar Dorado / Cerveza Artesanal (`#D97706`).
  - Fondos y Superficies: Marfil Cálido / Pergamino (`#FDFBF7`) con superficies de tarjetas en Blanco Puro (`#FFFFFF`) y bordes suaves (`#E7E2DA`).
  - Tipografía y Contrastes: Gris Pizarra Oscuro (`#1F2937`) para máxima legibilidad.
- **Tipografía:** Encabezados en Serif refinada (*Playfair Display* o *Cinzel*) que evocan etiquetas de botellas tradicionales, combinados con una Sans-Serif geométrica y legible (*Inter*) para datos analíticos, tablas y formularios.
- **Tono de Comunicación:** Cercano, respetuoso, libre de jerga burocrática; celebra el esfuerzo del productor ("¡Lote registrado con éxito! Tu código de trazabilidad está listo para compartir").

---

## 2. Usuarios, Roles y Permisos (RBAC)

### 2.1 Usuarios finales

| Perfil | Descripción |
|--------|-------------|
| **Administrador / Productor artesanal** | Persona o empresa encargada de producir y comercializar las bebidas. Gestiona productos, lotes, información de producción, inventario y resultados financieros. |
| **Establecimiento de Hostelería** | Bares, restaurantes, hoteles u otros establecimientos que comercializan o sirven los productos. Consulta productos disponibles y gestiona su disponibilidad dentro del establecimiento. |
| **Consumidor Final** | Persona que consulta las bebidas, conoce su información y disponibilidad y, posteriormente, puede realizar compras mediante el e-commerce. |
| **Usuario Anónimo / Visitante** | Persona que accede a la plataforma sin iniciar sesión. Puede consultar el catálogo público, pero no realizar operaciones que requieran autenticación. |

### 2.2 Roles y permisos (RBAC)

| Rol | Permisos principales |
|-----|----------------------|
| **Administrador / Productor** | `producto:crear`, `producto:consultar`, `producto:editar`, `producto:activar`, `producto:pausar`, `producto:retirar`, `lote:crear`, `lote:consultar`, `lote:editar`, `trazabilidad:consultar`, `finanzas:consultar` |
| **Hostelería** | `producto:consultar`, `trazabilidad:consultar`, `disponibilidad:gestionar` |
| **Consumidor** | `producto:consultar`, `trazabilidad:consultar`, `establecimiento:consultar`, `carrito:gestionar`, `pedido:crear`, `pedido:consultar` |
| **Visitante** | `catalogo:consultar`, `producto:consultar` |

---

## 3. Funcionalidades y Criterios de Aceptación

### 3.1 Registro y autenticación de usuarios
**Criterios de aceptación:**
- El usuario puede registrarse proporcionando los datos requeridos.
- El sistema valida que el correo no esté registrado previamente.
- El usuario puede iniciar sesión con sus credenciales.
- El sistema identifica el rol del usuario después de autenticarse.
- Un usuario no autenticado no puede acceder a funcionalidades privadas.
- Ante credenciales incorrectas, se muestra un mensaje de error sin iniciar sesión.

### 3.2 Gestión de productos
**Criterios de aceptación:**
- El Administrador puede registrar un producto indicando como mínimo nombre, tipo de bebida, descripción, presentación, precio e información necesaria para su comercialización.
- Los campos obligatorios deben estar identificados.
- Si falta información obligatoria, el sistema impide guardar y muestra el error junto al campo correspondiente.
- El Administrador puede editar la información de sus productos.
- El Administrador puede activar, pausar o retirar un producto del catálogo.
- Los cambios realizados se reflejan inmediatamente en el catálogo correspondiente.

### 3.3 Registro de lotes y trazabilidad
**Criterios de aceptación:**
- El productor puede ingresar nombre o identificador del lote, fecha de producción, volumen (litros), tipo de bebida, parámetros analíticos (pH, graduación alcohólica, acidez total, densidad), mermas (litros) y costos (costo total en moneda local).
- Los campos obligatorios deben estar identificados.
- Si falta información obligatoria o los datos no cumplen las validaciones establecidas, el sistema impide guardar y muestra errores específicos.
- Al guardar correctamente, el lote queda asociado a un producto perteneciente al productor autenticado.
- El sistema genera un código único de trazabilidad para cada lote con formato estandarizado `EC-[AÑO]-[ID_PRODUCTOR]-[ID_LOTE_HEX]` (ej. `EC-2026-PR04-A109`).
- El lote aparece inmediatamente en el listado correspondiente al productor.
- El sistema permite consultar posteriormente la información asociada al lote.

### 3.4 Consulta del catálogo
**Criterios de aceptación:**
- Los usuarios pueden consultar el catálogo de productos disponibles.
- Cada producto muestra como mínimo nombre, tipo de bebida, descripción, presentación y precio cuando corresponda.
- El usuario puede buscar productos por nombre.
- El usuario puede filtrar productos por categorías o tipo de bebida.
- Los productos pausados o retirados no aparecen como disponibles para compra.
- El catálogo es accesible para usuarios anónimos.

### 3.5 Consulta de trazabilidad del producto
**Criterios de aceptación:**
- El usuario puede acceder a la información de trazabilidad de un producto.
- El sistema muestra el lote asociado y la información de producción que sea pública.
- La información se presenta de forma organizada y comprensible.
- El sistema permite identificar el origen del producto mediante su código de trazabilidad.
- La información privada o administrativa del productor no se muestra al consumidor.

### 3.6 Gestión de disponibilidad en establecimientos
**Criterios de aceptación:**
- El consumidor puede consultar en qué establecimientos de hostelería está disponible un producto.
- El sistema muestra únicamente establecimientos con disponibilidad activa.
- El establecimiento de hostelería puede gestionar los productos que tiene disponibles.
- Al cambiar la disponibilidad, el catálogo refleja el nuevo estado.
- Un producto marcado como no disponible no aparece como disponible para el consumidor.

### 3.7 Carrito de compras *(Post-MVP)*
**Criterios de aceptación:**
- El consumidor autenticado puede agregar productos disponibles al carrito.
- Puede modificar la cantidad de productos.
- Puede eliminar productos del carrito.
- El sistema calcula automáticamente subtotal y total.
- El sistema valida que exista disponibilidad antes de iniciar el proceso de compra.
- El carrito conserva sus productos cuando ocurre un error durante el pago.

### 3.8 Gestión de pedidos y pagos *(Post-MVP)*
**Criterios de aceptación:**
- El consumidor puede revisar su pedido antes de confirmar la compra.
- El sistema muestra productos, cantidades, precios y total a pagar.
- El sistema permite iniciar el proceso de pago mediante PayU.
- Si el pago es exitoso, el pedido queda registrado.
- Si el pago falla, el pedido no se marca como completado.
- Ante un fallo de pago, el carrito se conserva.
- Después de una compra exitosa, se muestra una pantalla de confirmación.

### 3.9 Dashboard de gestión y análisis financiero
**Criterios de aceptación:**
- El Administrador puede consultar información financiera de sus productos y lotes.
- El sistema permite visualizar como mínimo costos, mermas y márgenes.
- La información puede consultarse diferenciando producto y/o lote.
- Los datos mostrados corresponden únicamente a los registros pertenecientes al Administrador autenticado.
- Mientras se cargan los datos, el sistema muestra un indicador de carga.
- Si no existen registros, se muestra un estado vacío con una opción para crear el primer registro.

### 3.10 Priorización MVP

| Prioridad | Funcionalidad | MVP |
|-----------|---------------|-----|
| 1 | Registro y autenticación | ✅ Sí |
| 2 | Gestión de productos | ✅ Sí |
| 3 | Registro de lotes y trazabilidad | ✅ Sí (núcleo) |
| 4 | Catálogo | ✅ Sí |
| 5 | Consulta de trazabilidad | ✅ Sí |
| 6 | Disponibilidad en establecimientos | ✅ Sí |
| 7 | Dashboard financiero | ✅ Sí |
| 8 | Carrito | ❌ Post-MVP |
| 9 | Pagos y pedidos | ❌ Post-MVP |

---

## 4. Límites, Restricciones y Exclusiones

### 4.1 Restricciones técnicas y no funcionales
- La plataforma será una **aplicación web responsive** en español.
- Arquitectura **modular**.
- Base de datos **relacional (MySQL)**.
- Autenticación y autorización mediante **RBAC**.
- Validaciones de datos en frontend y backend.
- Identificadores únicos de trazabilidad.
- Manejo de estados de **carga, vacío y error**.
- Protección de datos y almacenamiento seguro de contraseñas.
- Separación de información según el rol del usuario.
- Aislamiento de los datos de cada productor.
- Las consultas habituales deberán responder en **≤ 2 segundos** en condiciones normales.
- La futura funcionalidad de e-commerce deberá contemplar **normativas colombianas** aplicables a la venta de bebidas alcohólicas, protección de datos y validación de edad.

### 4.2 Componentes prohibidos de modificar
**No aplica.** El proyecto se desarrolla desde cero. No existen componentes o archivos heredados que estén prohibidos de modificar.

---

## 5. Expectativas de Pruebas y Comportamientos Críticos

### 5.1 Flujos críticos

| # | Flujo crítico | Validación esperada |
|---|---------------|---------------------|
| 1 | Registro de usuario | El sistema debe registrar correctamente al usuario y rechazar el registro si el correo ya existe. |
| 2 | Autenticación | Un usuario con credenciales incorrectas no debe acceder al sistema y debe recibir un mensaje de error. |
| 3 | Autorización por roles (RBAC) | Cada usuario solo puede acceder a las funcionalidades correspondientes a su rol. Un consumidor no puede acceder al panel administrativo. |
| 4 | Registro de producto | El Administrador puede crear un producto con información válida y este debe aparecer correctamente en su listado y, si está activo, en el catálogo. |
| 5 | Registro de lote | Un lote con todos los datos obligatorios válidos debe guardarse correctamente, quedar asociado al producto correspondiente y recibir un código único de trazabilidad. |
| 6 | Consulta de trazabilidad | Al consultar un producto, el sistema debe mostrar correctamente la información pública de su lote y permitir identificar su origen mediante el código de trazabilidad. |
| 7 | Gestión de disponibilidad | Cuando un producto es pausado o retirado por el Administrador, debe dejar de mostrarse como disponible para los consumidores. |
| 8 | Compra *(Post-MVP)* | Una compra aprobada debe generar correctamente el pedido y mostrar una confirmación al consumidor. |
| 9 | Fallo de pago *(Post-MVP)* | Si el pago falla, el pedido no debe marcarse como completado y el carrito debe conservar los productos. |

### 5.2 Casos límite

| # | Caso límite | Comportamiento esperado |
|---|-------------|--------------------------|
| 1 | Volumen negativo o igual a cero | El sistema debe rechazar el registro del lote y mostrar un mensaje indicando que el volumen debe ser mayor que cero. |
| 2 | Costos o precios negativos | El sistema debe impedir valores negativos en campos monetarios. |
| 3 | Fecha de producción futura | El sistema debe rechazar la fecha si no corresponde a una producción permitida según las reglas del negocio. |
| 4 | Datos analíticos incompletos | El sistema debe impedir guardar el lote y señalar específicamente los campos faltantes. |
| 5 | Código de trazabilidad duplicado | El sistema debe garantizar que cada lote tenga un identificador único y rechazar cualquier duplicación. |
| 6 | Productor intenta modificar un lote ajeno | La operación debe ser denegada y el sistema no debe revelar ni modificar información del otro productor. |
| 7 | Productor intenta consultar información financiera de otro productor | El acceso debe ser rechazado. |
| 8 | Búsqueda con caracteres especiales | La búsqueda debe procesarse correctamente sin generar errores ni permitir manipulación de consultas. |
| 9 | Producto sin disponibilidad | El sistema debe indicar que el producto no está disponible y no debe permitir iniciar una compra. |
| 10 | Pérdida de conexión durante el registro | La operación no debe generar un registro incompleto o duplicado; al reintentar, el formulario debe mostrarse vacío. |

### 5.3 Validaciones obligatorias

| Campo / Regla | Validación |
|---------------|------------|
| Correo electrónico | Formato válido y único dentro del sistema. |
| Contraseña | Debe cumplir requisitos mínimos de seguridad y almacenarse de forma segura. |
| Campos obligatorios | No pueden enviarse vacíos; mostrar error junto al campo correspondiente. |
| Volumen | Únicamente valores numéricos mayores que cero. |
| Precio y costos | Valores numéricos válidos y no negativos. |
| Merma | Valor válido dentro de los límites establecidos; no negativa ni superior al volumen producido. |
| Fechas | Formato válido y respetar reglas temporales definidas para producción y lotes. |
| Rol del usuario | Determina qué operaciones puede ejecutar; no puede ser utilizado para obtener permisos superiores sin autorización. |
| Identificador de trazabilidad | Único para cada lote. |
| Propiedad de los datos | Un productor solo puede modificar, consultar y administrar los registros que le pertenecen. |
| Estado del producto | Un producto pausado o retirado no debe estar disponible para una operación de compra. |
| Carrito | No debe permitir agregar productos no disponibles o cantidades superiores a las permitidas. |
| Pago | Una transacción fallida nunca debe generar un pedido como completado. |

### 5.4 Criterio general para darlo por terminado

El sistema se considerará listo cuando:

1. Todos los **flujos críticos** hayan sido ejecutados exitosamente.
2. Las **validaciones obligatorias** impidan datos inválidos.
3. Los casos de **acceso no autorizado** sean rechazados correctamente.
4. Ninguna operación fallida genere información **incompleta, duplicada o inconsistente** en la base de datos.

---

## 6. Anexos

### 6.1 Evolución post-MVP (Fase 2 y 3)

| Fase | Funcionalidades |
|------|-----------------|
| **Fase 2** | Carrito de compras, gestión de pedidos, integración con PayU, historial y seguimiento de pedidos, validación de disponibilidad en compra. |
| **Fase 3** | Reportes y analytics avanzados, integración con mapas, notificaciones por correo/push, integración con servicios de envío, funcionalidades comerciales avanzadas (promociones, recomendaciones, favoritos). |

---

*Documento generado a partir de la entrevista de descubrimiento de producto.*
*Fecha: 2026-09-02*