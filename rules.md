# REGLAS Y ESTÁNDARES IMPERATIVOS DE CÓDIGO Y SEGURIDAD - ENTRE COPAS - PLATAFORMA DE GESTIÓN, TRAZABILIDAD Y COMERCIALIZACIÓN DE BEBIDAS ARTESANALES
> Documento generado automáticamente por VibeRules Builder & Launch Auditor (Security Edition)
> Fecha de generación: 2026-09-07 13:40:00 | Nivel de Blindaje Activo: 68/68 (100% - Blindaje Máximo / Nivel Óptimo)

---

## 1. ARQUITECTURA Y CONTEXTO

### 1.1. Contexto de Agentes y Roles (agents.md)
```markdown
# Entre Copas - Protocolo Operativo y Arquitectura de Agentes
- Misión: Plataforma web modular de gestión de producción, trazabilidad analítica y comercialización de bebidas artesanales (vinos, cervezas, hidromiel).
- Arquitectura: Monolito Modular por Dominio (Package by Feature) bajo src/main/java/com/entrecopas/ (usuario, productor, producto, lote, establecimiento, disponibilidad, financiero, config, exception).
- Roles del Sistema (RBAC):
  1. ROLE_ADMIN / ROLE_PRODUCTOR: Gestión completa de productos propios, lotes, analítica, mermas, costos y finanzas.
  2. ROLE_HOSTELERIA: Gestión de disponibilidad de productos en bares y restaurantes.
  3. ROLE_CONSUMIDOR: Consulta de catálogo, visualización de precios y trazabilidad pública.
  4. Visitante Anónimo: Consulta pública de catálogo y trazabilidad mediante código único.
- Aislamiento Multi-Tenant Lógico: Toda consulta o mutación de productor DEBE filtrar obligatoriamente por el productor_id autenticado en sesión (SecurityContextHolder). Prohibido confiar en identificadores recibidos del cliente.
- Zonas Protegidas / Inmutables:
  - Archivos de migración Flyway (src/main/resources/db/migration/V*__*.sql) son ESTRICTAMENTE INMUTABLES una vez versionados.
  - Archivo pom.xml: Prohibido incorporar dependencias externas (Redis, Mongo, pasarelas) sin autorización explícita.
  - SecurityConfig.java: Prohibido exponer rutas administrativas (/panel/** o /api/v1/financiero/**) sin autenticación y rol.
- Puertas de Calidad (Validation Gates): G0 (Checkstyle Google Java Style + Prettier) -> G1 (mvn clean test) -> G2 (Integración) -> G3 (Maker-Checker humano obligatorio).
- Protocolo de Persistencia Agéntica: Registro de estado en .agents/session_state.json y límite anti-loop de 3 reintentos antes de detención obligatoria.
```

### 1.2. Sistema de Diseño y Tokens (design.md)
```markdown
# Sistema de Diseño - Entre Copas
- Vibe y Modelo Emocional: Artesanal, noble, cálido y moderno. Transmite el orgullo de la tierra y la maestría del productor tradicional, complementado con la precisión y confianza técnica de la trazabilidad.
- Paleta de Color Curada:
  - Primario (Vino Noble / Borgoña): #58111A (Navbar, botones primarios, títulos H1-H2).
  - Secundario / Acento (Ámbar Dorado / Cerveza): #D97706 (Badges de trazabilidad, destacados analíticos).
  - Fondo Base de Aplicación: #FDFBF7 (Pergamino Cálido / Marfil).
  - Superficies de Tarjetas y Modales: #FFFFFF (Blanco Puro).
  - Bordes Suaves: #E7E2DA (Delimitadores tenues).
  - Texto Principal: #1F2937 (Gris Pizarra Oscuro).
  - Texto Secundario: #6B7280.
- Tipografía:
  - Títulos y Encabezados (H1, H2, H3): Playfair Display / Cinzel (Serif elegante tradicional).
  - Tablas, Datos Analíticos, Formularios y Badges: Inter (Sans-serif técnica y legible).
- Dimensionamiento y Ergonomía:
  - Área táctil mínima: 44px × 44px en todos los botones, controles, switches de disponibilidad y enlaces.
  - Radios de borde: 8px (inputs y botones), 12px (tarjetas y paneles), 9999px (badges de trazabilidad).
  - Erradicación de Lorem Ipsum: Todo texto debe reflejar datos realistas de bodegas, productos y lotes.
```

---

## 2. REGLAS TÉCNICAS Y DE SEGURIDAD (68/68 ACTIVADAS)

### Categoría 1 - Ciberseguridad y Bastionado (OWASP Top 10) (10 activadas)
- [x] **`INPUT-VAL/SANITIZE`** | **Sanitización e inyección estricta:** Validación de inputs en servidor con esquemas fuertemente tipados (Jakarta Validation `@NotNull`, `@NotBlank`, `@Positive`, `@Email` en DTOs). Prevención total contra SQLi, XSS, NoSQLi y Command Injection mediante JPA parametrizado y codificación contextual en Thymeleaf.
- [x] **`ENV-VARS/SECRETS`** | **Variables de entorno y secretos:** Prohibido hardcodear credenciales, API keys o contraseñas de base de datos en código. Uso estricto de variables de entorno `.env` en el servidor consumidas vía `${VAR_NAME}` en `application.properties`. Registro obligatorio de `.env` en `.gitignore`.
- [x] **`HASH-CRYPT/ARGON2`** | **Cifrado y hashing de contraseñas:** Uso obligatorio de BCrypt con salt individual (factor de costo ≥ 12) o Argon2id para almacenamiento de contraseñas (`BCryptPasswordEncoder`). Cifrado AES-256-GCM para datos sensibles en reposo. Prohibido estrictamente MD5 o SHA1.
- [x] **`AUTH-JWT/RBAC`** | **Autenticación segura y RBAC:** Sesiones de usuario gestionadas de forma segura con cookies HttpOnly, Secure y SameSite=Strict. Control de acceso basado en roles (`ROLE_ADMIN`, `ROLE_PRODUCTOR`, `ROLE_HOSTELERIA`, `ROLE_CONSUMIDOR`) con verificación obligatoria en Spring Security y capa Service.
- [x] **`CSP-HEADERS/HELMET`** | **Cabeceras HTTP de seguridad:** Configurar cabeceras de seguridad estrictas en Spring Security: Content Security Policy (CSP), HSTS con preloading (`max-age=31536000; includeSubDomains`), X-Frame-Options (SAMEORIGIN/DENY) y X-Content-Type-Options (nosniff).
- [x] **`RATE-LIMIT/DDOS`** | **Rate limiting y Anti-Fuerza bruta:** Limitar peticiones por IP y por usuario en endpoints sensibles (`/login`, `/registro`, `/api/v1/**`) para mitigar ataques de denegación de servicio (DoS), scraping no autorizado y ataques de fuerza bruta.
- [x] **`CORS-POL/ORIGIN`** | **Política CORS restrictiva:** Rechazar comodines (`*`) en producción. Whitelist explícita de dominios autorizados y métodos HTTP permitidos en la configuración de CORS del backend.
- [x] **`FILE-SEC/UPLOAD-VAL`** | **Carga segura de archivos:** Validación estricta de tipo MIME real por magic bytes y restricción de tamaño en subida de ficheros. Renombrar archivos mediante hash SHA-256 aleatorio y almacenarlos fuera del web root público de ejecución.
- [x] **`ERR-MASK/LOG-SEC`** | **Enmascaramiento de errores y logs:** Ocultar stack traces, consultas SQL, nombres de tablas y excepciones internas en producción mediante `GlobalExceptionHandler`. Registrar exclusivamente logs estructurados y auditables en el servidor.
- [x] **`CSRF-PROT`** | **Protección Anti-CSRF:** Tokens anti-falsificación (CSRF tokens de Spring Security) obligatorios en todas las mutaciones de estado (POST, PUT, DELETE) combinados con cookies `SameSite=Strict`.

### Categoría 2 - Páginas y Estructura Web (12 activadas)
- [x] **`GDPR-PRIVACY`** | **Política de Privacidad (`/privacy`):** Página legal de protección de datos personales (Habeas Data Ley 1581 de 2012 de Colombia / GDPR) con identificación del responsable del tratamiento, finalidades y canales de ejercicio de derechos ARCO.
- [x] **`TERMS-COND`** | **Términos y Condiciones (`/terms`):** Página legal detallando condiciones de uso de la plataforma para productores, establecimientos y consumidores, responsabilidades de comercialización y derechos de propiedad intelectual.
- [x] **`HTTP-404-CUSTOM`** | **Error 404 Personalizado:** Vista 404 brandeada con la identidad visual de Entre Copas, buscador de bebidas y enlaces directos al inicio (`/`) y al catálogo (`/catalogo`) para evitar el abandono de navegación.
- [x] **`HTTP-500-CUSTOM`** | **Error 500 / 5XX Seguro:** Página de error de servidor sobria, elegante y comprensible para el usuario, sin exponer stack traces ni detalles técnicos de arquitectura.
- [x] **`PAGE-THANK-YOU`** | **Página de Gracias (`/thank-you` / Confirmación):** Pantallas de confirmación de registro de usuario y confirmación de lote creado con código de trazabilidad único, felicitación y opciones de continuidad operativa.
- [x] **`PAGE-CASE-STUDIES`** | **Historias de Maestros Productores (`/about` / `/productores`):** Sección dedicada a visibilizar la tradición, terroir y maestría de los productores artesanales afiliados, respaldada con métricas reales de trazabilidad.
- [x] **`PAGE-FAQ`** | **Preguntas Frecuentes (`/faq`):** Acordeones accesibles de FAQ respondiendo dudas sobre registro de lotes, interpretación de parámetros analíticos (pH, acidez, ABV), consulta de trazabilidad y gestión de disponibilidad.
- [x] **`PAGE-ABOUT-US`** | **Acerca de Nosotros (`/about`):** Página institucional que expone la misión de dignificar y formalizar las bebidas artesanales colombianas, el equipo y la ubicación.
- [x] **`NAV-BREADCRUMBS`** | **Migas de Pan (Breadcrumbs):** Navegación jerárquica con marcado semántico estructurado (`Inicio > Catálogo > Vino Tinto Malbec`) para orientar al usuario en fichas de producto y trazabilidad.
- [x] **`NAV-STICKY`** | **Navegación Superior Sticky:** Barra de navegación pegajosa en la parte superior con desenfoque de fondo sutil al hacer scroll para garantizar acceso permanente al menú y sesión.
- [x] **`LINKS-INTERNAL`** | **Arquitectura de Enlaces Internos:** Enlazado coherente sin callejones sin salida; interconexión fluida entre landing page, catálogo, detalle del producto, establecimientos y trazabilidad de lotes.
- [x] **`LINKS-FOOTER`** | **Pie de Página Completo:** Footer corporativo con enlaces a catálogo, trazabilidad, panel, políticas legales, advertencia legal de consumo responsable de alcohol para mayores de 18 años y copyright dinámico.

### Categoría 3 - SEO e Indexación (7 activadas)
- [x] **`SEO-SITEMAP-XML`** | **Sitemap.xml Dinámico:** Generación automatizada de `sitemap.xml` accesible en la raíz del dominio, indexando exclusivamente rutas públicas (`/`, `/catalogo`, `/producto/**`, `/trazabilidad/**`) con prioridades y frecuencias de actualización.
- [x] **`SEO-ROBOTS-TXT`** | **Robots.txt Configurado:** Archivo `robots.txt` bloqueando el rastreo de paneles privados (`/panel/**`), APIs internas (`/api/v1/**`) y formularios de sesión (`/login`, `/registro`), e indicando la ubicación del sitemap.
- [x] **`SEO-TITLE-UNIQUE`** | **Title Tag Único por Vista:** Etiqueta `<title>` única, no duplicada y persuasiva (50-60 caracteres) en cada plantilla Thymeleaf (ej. *"Vino Tinto Malbec Gran Reserva 2024 | Trazabilidad Entre Copas"*).
- [x] **`SEO-META-DESC`** | **Meta Descripción Contextual:** Meta descripción individualizada (<160 caracteres) con términos clave naturales sobre bebidas artesanales, origen y trazabilidad certificada.
- [x] **`SEO-IMG-ALT`** | **Atributos ALT en 100% de Imágenes:** Textos alternativos descriptivos en todas las imágenes de botellas, barricas, logotipos e iconos para accesibilidad (WCAG AA) y posicionamiento orgánico.
- [x] **`SEO-JSON-LD`** | **Marcado Semántico JSON-LD:** Esquemas Schema.org de tipo `Organization`, `Product` y `WebSite` embebidos en el `<head>` de las páginas públicas para habilitar fragmentos enriquecidos en Google.
- [x] **`SEO-OG-IMAGE`** | **Open Graph Image (1200x630):** Metadatos `og:image` y `twitter:image` con imágenes optimizadas en alta resolución para previsualizaciones elegantes al compartir fichas en redes y mensajería.

### Categoría 4 - Formularios y Conversión (11 activadas)
- [x] **`FORM-NAP-CONTACT`** | **Datos NAP Canónicos Visibles:** Información canónica de contacto visible en el pie de página y página de contacto: Nombre comercial institucional, Dirección física de operaciones y Teléfono oficial.
- [x] **`FORM-HONEYPOT`** | **Honeypot Anti-Spam:** Campo oculto por CSS invisible para humanos en formularios de registro y contacto que bloquea envíos automatizados de bots sin requerir captchas invasivos.
- [x] **`FORM-LIVE-VALIDATION`** | **Validación en Tiempo Real:** Retroalimentación inline inmediata por campo indicando errores de formato antes del envío (volumen cero, merma superior a volumen, contraseñas débiles, email inválido).
- [x] **`FORM-SLA-RESPONSE`** | **SLA de Respuesta Explícito:** Mensaje informativo tras envíos de contacto indicando el tiempo máximo de atención (ej. *"Atendemos solicitudes de productores en menos de 24 horas hábiles"*).
- [x] **`FORM-COOKIE-CONSENT`** | **Banner de Cookies Granular:** Aviso de cookies discreto con opciones de aceptar, rechazar o configurar categorías de almacenamiento (necesarias, analíticas y de preferencias).
- [x] **`CONV-HERO-CTA`** | **CTA Principal en Hero:** Botón de acción principal de alto contraste visible en el primer frame de la landing page sin necesidad de scroll (*"Explorar Catálogo"* o *"Verificar Código de Lote"*).
- [x] **`CONV-REPEAT-CTA`** | **CTA Repetido Estratégico:** Reiteración del llamado a la acción al final del catálogo público y en fichas individuales de producto para promover la consulta de trazabilidad.
- [x] **`CONV-STICKY-MOBILE-CTA`** | **CTA Flotante en Móviles:** Botón sticky optimizado para la zona inferior del pulgar en pantallas móviles para consultar trazabilidad o contactar a la bodega.
- [x] **`CONV-NEWSLETTER-OPTIN`** | **Boletín / Suscripción Voluntaria:** Formulario de suscripción con doble opt-in para recibir noticias sobre nuevas cosechas, lotes de edición limitada y eventos del sector artesanal.
- [x] **`CONV-LIVE-CHAT-CSAT`** | **Atención o Micro-Encuesta CSAT:** Mecanismo de retroalimentación rápida post-registro de lote o botón de asistencia para resolver dudas técnicas del productor.
- [x] **`CONV-TRUST-BADGES`** | **Insignias de Confianza y Calidad:** Sellos visuales de trazabilidad certificada, verificación analítica de laboratorio y cumplimiento normativo sanitario para afianzar credibilidad.

### Categoría 5 - Diseño y Adaptabilidad (12 activadas)
- [x] **`DESIGN-RESPONSIVE-BREAKPOINTS`** | **Breakpoints Responsivos:** Diseño adaptable testeado sin rupturas visuales ni superposiciones en 320px, 768px, 1024px y 1440px.
- [x] **`DESIGN-MOBILE-NAV`** | **Menú Móvil Accesible:** Menú hamburguesa colapsable con atrapamiento de foco (trap focus), soporte para tecla Escape y cierre automático al interactuar con un enlace o backdrop.
- [x] **`DESIGN-FAVICON-SUITE`** | **Suite de Favicons Completa:** Favicon corporativo en formatos SVG vectorial, PNG 32x32, PNG 180x180 (Apple Touch Icon) y archivo `site.webmanifest`.
- [x] **`DESIGN-BACK-TO-TOP`** | **Botón 'Volver Arriba':** Botón flotante accesible activado automáticamente tras 400px de desplazamiento vertical en listados de catálogo y tablas de lotes.
- [x] **`DESIGN-DARK-MODE-NATIVE`** | **Modo Oscuro Nativo:** Soporte para tema oscuro respetando `prefers-color-scheme` con paleta carbón/borgoña profundo y persistencia en `localStorage`.
- [x] **`DESIGN-I18N-LANGUAGE`** | **Estructura para i18n:** Arquitectura de plantillas preparada para internacionalización (Español base / Inglés para consulta de exportación) mediante archivos de propiedades de mensajes de Spring Boot (`messages.properties`).
- [x] **`DESIGN-PROGRESS-BAR`** | **Barra de Progreso de Lectura:** Indicador sutil de progreso en la parte superior para fichas extensas de trazabilidad o documentos legales.
- [x] **`DESIGN-CLICKABLE-LOGO`** | **Logo Clicable al Home:** El logotipo "Entre Copas" redirige siempre a la página de inicio pública (`/`) o al panel (`/panel`) según el estado de sesión, con atributo `aria-label="Ir a inicio"`.
- [x] **`DESIGN-CLICKABLE-TEL`** | **Teléfono Interactivo (`tel:`):** Todos los números telefónicos mostrados son enlaces activos para llamada directa desde dispositivos móviles.
- [x] **`DESIGN-CLICKABLE-MAILTO`** | **Correo Interactivo (`mailto:`):** Direcciones de correo electrónico configuradas con enlaces directos para abrir el cliente de mensajería del usuario.
- [x] **`DESIGN-NO-HORIZONTAL-OVERFLOW`** | **Cero Desbordamiento Horizontal:** Inspección estricta de elementos y contenedores; prohibido el scroll horizontal involuntario en cualquier ancho de pantalla.
- [x] **`DESIGN-CLEAN-LOREM-IPSUM`** | **Erradicación Total de Lorem Ipsum:** Prohibido el texto simulado, datos de relleno genéricos o enlaces vacíos (`href='#'`) en cualquier entorno; empleo exclusivo de datos realistas de bebidas artesanales.

### Categoría 6 - Rendimiento, Animaciones y Redes (16 activadas)
- [x] **`PERF-WEBP-AVIF-LAZY`** | **Imágenes WebP/AVIF con Lazy Load:** Compresión moderna en formatos WebP/AVIF con carga diferida (`loading='lazy'`) y especificación explícita de `width` y `height` para prevenir saltos de maquetación (CLS).
- [x] **`PERF-CORE-WEB-VITALS-90`** | **Core Web Vitals > 90:** Certificación de métricas LCP < 2.5s, INP < 200ms y CLS < 0.1 en auditorías de Google Lighthouse y PageSpeed Insights.
- [x] **`SEC-SSL-HTTPS-FORCE`** | **HTTPS Forzado y HSTS:** Redirección 301 obligatoria de HTTP a HTTPS, soporte TLS 1.3 y cabecera HTTP Strict Transport Security configurada en el proxy inverso Nginx.
- [x] **`UI-SMOOTH-SCROLL`** | **Desplazamiento Suave:** Comportamiento `scroll-behavior: smooth` respetando la directiva de accesibilidad `prefers-reduced-motion: reduce`.
- [x] **`UI-MICROINTERACTIONS`** | **Microinteracciones en Botones:** Retroalimentación háptica y visual inmediata (hover, active, focus visible) en todos los controles interactivos y botones de al menos 44px de altura.
- [x] **`UI-HOVER-STATES`** | **Estados Hover Coherentes:** Transiciones suaves de elevación, fondo y borde con acentos en Ámbar `#D97706` y Borgoña `#58111A`.
- [x] **`UI-SKELETON-SCREENS`** | **Skeleton Screens de Carga:** Estructuras esqueléticas animadas para estados de carga asíncrona en catálogo, panel y dashboard financiero en lugar de spinners genéricos o pantallas en blanco.
- [x] **`UI-TRANSITIONS-200MS`** | **Transiciones Rápidas (0.2s):** Curvas de animación estandarizadas a 200ms (`transition: all 0.2s ease-in-out`) para generar sensación de fluidez y respuesta inmediata.
- [x] **`UI-HERO-ANIMATION`** | **Animación de Entrada en Hero:** Transición sutil de entrada (fade-in / slide-up no intrusivo) al renderizar la landing page principal.
- [x] **`SOCIAL-LINKS-SECURE`** | **Enlaces a Redes Seguros:** Enlaces a perfiles externos con atributos de protección `rel='noopener noreferrer'` y apertura en nueva pestaña `target='_blank'`.
- [x] **`SOCIAL-SHARE-BUTTON`** | **Botón de Compartir Nativo:** Integración de la API nativa Web Share con fallback automático para copiar el enlace de la ficha de trazabilidad al portapapeles.
- [x] **`MEDIA-EMBED-VIDEO-RESPONSIVE`** | **Videos Responsivos:** Contenedores de video con relación de aspecto `aspect-ratio: 16/9` y carga diferida bajo demanda para documentar procesos de elaboración artesanal.
- [x] **`UI-PRICING-TABLE-CLEAR`** | **Tabla Comparativa Clara:** Presentación estructurada de opciones de afiliación o comercialización para productores con diferenciación del nivel recomendado.
- [x] **`UI-VERSION-CONTROL-BADGE`** | **Badge de Versión y Build:** Identificador de release semántica (`v1.0.0-MVP`) visible de forma discreta en el pie de página o consola técnica.
- [x] **`UI-DYNAMIC-COPYRIGHT-YEAR`** | **Año de Copyright Dinámico:** Año de copyright generado dinámicamente mediante script o evaluación Thymeleaf (`#dates.year(#dates.createNow())`) para evitar desactualizaciones.
- [x] **`QA-BROKEN-LINKS-AUDIT`** | **Auditoría de Enlaces Rotos:** Verificación estricta de cero enlaces rotos (404), imágenes huérfanas o scripts no resueltos en todo el árbol de navegación.

---

## 3. PROHIBICIONES Y MANDATOS ABSOLUTOS

1. **Prohibido exponer API keys, connection strings o credenciales** en código fuente o archivos de cliente; usar exclusivamente variables de entorno protegidas (`.env`) en el servidor e ignoradas en Git.
2. **Todo input del usuario DEBE sanitizarse y validarse** obligatoriamente mediante esquemas tipados (Jakarta Validation `@NotNull`, `@NotBlank`, `@Positive`, etc.) antes de ser procesado o persistido en base de datos.
3. **Prohibido el uso de algoritmos de hashing obsoletos (MD5, SHA1)**; usar obligatoriamente BCrypt con salt individual (factor de costo ≥ 12) o Argon2id para contraseñas, y AES-256-GCM para datos confidenciales en reposo.
4. **Ocultar stack traces, consultas SQL, rutas internas y excepciones técnicas** en entornos de producción; registrar exclusivamente logs estructurados y auditables en el servidor mediante `GlobalExceptionHandler`.
5. **La IA NO puede dar por completada ninguna tarea** sin verificar rigurosamente cada una de las 68 reglas activadas en este documento mediante pruebas automatizadas (`mvn clean test`), validación estática de estilo (`mvn checkstyle:check`), y compilación limpia sin advertencias.
