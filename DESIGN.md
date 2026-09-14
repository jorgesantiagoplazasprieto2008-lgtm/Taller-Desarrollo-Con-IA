# DESIGN.md - Sistema de Diseño y Tokens de Interfaz
## Entre Copas - Plataforma de gestión, trazabilidad y comercialización de bebidas artesanales

---

## 1. Identidad Visual y Modelo Emocional (Vibe)
- **Vibe:** Artesanal, noble, cálido y moderno. Transmite el orgullo de la tierra y la maestría del productor tradicional, complementado con la precisión y confianza técnica de la trazabilidad físico-química.
- **Tono de Comunicación:** Cercano, respetuoso, libre de jerga burocrática; celebra el esfuerzo del maestro productor artesanal.

---

## 2. Tokens de Color

### 2.1 Paleta Principal
- **Color Primario (Vino Noble / Borgoña):** `#58111A`
  - Uso: Barras de navegación, botones primarios (CTA), títulos principales, estados activos.
- **Color Secundario / Acento (Ámbar Dorado / Cerveza):** `#D97706`
  - Uso: Badges de trazabilidad, destacados analíticos, calificaciones visuales, micro-interacciones hover.
- **Fondo de Aplicación (Pergamino Cálido / Marfil):** `#FDFBF7`
  - Uso: Fondo base de todas las pantallas web responsive; reduce fatiga visual frente a blancos puros clínicos.
- **Superficies de Tarjetas y Modales:** `#FFFFFF` (Blanco Puro)
  - Uso: Tarjetas de producto, contenedores de formularios, paneles de datos y tablas.
- **Bordes y Delimitadores:** `#E7E2DA`
  - Uso: Bordes suaves para tarjetas, tablas analíticas y separadores de secciones.
- **Texto Principal (Gris Pizarra Oscuro):** `#1F2937`
  - Uso: Títulos H1-H3, cuerpo de texto principal, valores numéricos y tablas.
- **Texto Secundario / Muted:** `#6B7280`
  - Uso: Leyendas, metadatos de producción, notas al pie y descripciones secundarias.

### 2.2 Estados Semánticos Funcionales
- **Éxito / Trazabilidad Verificada / Lote Activo:** `#15803D` (Verde Bosque) | Fondo: `#DCFCE7`
- **Advertencia / Estado Pausado / Merma Alta:** `#B45309` (Ámbar Tostado) | Fondo: `#FEF3C7`
- **Error / Crítico / Retirado:** `#B91C1C` (Rojo Carmesí) | Fondo: `#FEE2E2`

---

## 3. Tipografía

### 3.1 Familias Tipográficas
- **Títulos y Encabezados (H1, H2, H3):** *Playfair Display* (o *Cinzel* como fallback), Serif refinada.
  - Pesos: SemiBold (600), Bold (700).
  - Evoca las etiquetas clásicas de botellas, barricas y bodegas artesanales.
- **Cuerpo, Tablas Técnicas, Formularios y Badges:** *Inter*, Sans-serif técnica y contemporánea.
  - Pesos: Regular (400), Medium (500), SemiBold (600).
  - Garantiza máxima legibilidad en métricas analíticas (pH, acidez, densidad, grados alcohólicos).

### 3.2 Escala Tipográfica
- **H1 (Títulos de página):** 32px (Mobile) / 40px (Desktop), line-height: 1.2, font-weight: 700.
- **H2 (Secciones principales):** 24px (Mobile) / 28px (Desktop), line-height: 1.3, font-weight: 600.
- **H3 (Títulos de tarjeta):** 18px / 20px, line-height: 1.4, font-weight: 600.
- **Body (Texto base e inputs):** 15px / 16px, line-height: 1.5, font-weight: 400.
- **Small / Badges / Monospace:** 12px / 13px, font-weight: 600, letter-spacing: 0.04em (Código de trazabilidad: `EC-YYYY-PRXX-XXXX`).

---

## 4. Espaciado, Radios y Elevación

### 4.1 Escala de Espaciado (Múltiplos de 8px)
- `space-xs`: 4px
- `space-sm`: 8px
- `space-md`: 16px
- `space-lg`: 24px
- `space-xl`: 32px
- `space-2xl`: 48px

### 4.2 Radios de Borde (Border Radius)
- **Inputs de formulario, selectores y botones:** `8px`
- **Tarjetas de producto, modales y paneles del dashboard:** `12px`
- **Pills de estado, badges de categoría y trazabilidad:** `9999px` (Full rounded)

### 4.3 Elevación y Sombras
- **Sombra sutil de tarjetas:** `box-shadow: 0 4px 12px rgba(88, 17, 26, 0.05)`
- **Sombra hover interactivo:** `box-shadow: 0 8px 24px rgba(88, 17, 26, 0.10)`

---

## 5. Reglas Base de UX e Interacción
1. **Área táctil mínima:** Mínimo **44px × 44px** en todos los controles interactivos, botones de acción, interruptores de disponibilidad y enlaces de navegación móvil.
2. **Diseño Web Responsive:** Mobile-first con breakpoints certificados en 320px, 768px, 1024px y 1440px sin desbordamiento horizontal.
3. **Erradicación de Lorem Ipsum:** Prohibido el uso de texto falso o simulado; todas las pantallas y prototipos deben usar datos realistas de bodegas, productos y lotes.
4. **Estados Asíncronos:** Toda llamada de red debe reflejar estados claros de **carga** (skeleton screens temáticos), **vacío** (mensajes orientadores amigables) y **error** (retroalimentación con botón de reintento).