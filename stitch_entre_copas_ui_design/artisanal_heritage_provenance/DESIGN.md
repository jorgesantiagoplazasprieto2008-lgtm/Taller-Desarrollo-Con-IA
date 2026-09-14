---
name: Artisanal Heritage & Provenance
colors:
  surface: '#f8f9ff'
  surface-dim: '#d0dbed'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e6eeff'
  surface-container-high: '#dee9fc'
  surface-container-highest: '#d9e3f6'
  on-surface: '#121c2a'
  on-surface-variant: '#554242'
  inverse-surface: '#27313f'
  inverse-on-surface: '#eaf1ff'
  outline: '#877272'
  outline-variant: '#dac0c0'
  surface-tint: '#994348'
  primary: '#390009'
  on-primary: '#ffffff'
  primary-container: '#58111a'
  on-primary-container: '#db767b'
  inverse-primary: '#ffb3b4'
  secondary: '#904d00'
  on-secondary: '#ffffff'
  secondary-container: '#fe932c'
  on-secondary-container: '#663500'
  tertiary: '#001e08'
  on-tertiary: '#ffffff'
  tertiary-container: '#003514'
  on-tertiary-container: '#45a75f'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdada'
  primary-fixed-dim: '#ffb3b4'
  on-primary-fixed: '#40000b'
  on-primary-fixed-variant: '#7b2c32'
  secondary-fixed: '#ffdcc3'
  secondary-fixed-dim: '#ffb77d'
  on-secondary-fixed: '#2f1500'
  on-secondary-fixed-variant: '#6e3900'
  tertiary-fixed: '#95f8a7'
  tertiary-fixed-dim: '#79db8d'
  on-tertiary-fixed: '#00210a'
  on-tertiary-fixed-variant: '#005323'
  background: '#f8f9ff'
  on-background: '#121c2a'
  surface-variant: '#d9e3f6'
typography:
  display-lg:
    fontFamily: Playfair Display
    fontSize: 48px
    fontWeight: '600'
    lineHeight: 56px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Playfair Display
    fontSize: 36px
    fontWeight: '600'
    lineHeight: 44px
    letterSpacing: -0.01em
  headline-lg:
    fontFamily: Playfair Display
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Playfair Display
    fontSize: 26px
    fontWeight: '600'
    lineHeight: 34px
  headline-md:
    fontFamily: Playfair Display
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Playfair Display
    fontSize: 20px
    fontWeight: '500'
    lineHeight: 28px
  title-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.04em
  code-batch:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '500'
    lineHeight: 18px
    letterSpacing: 0.06em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  space-05: 4px
  space-1: 8px
  space-2: 16px
  space-3: 24px
  space-4: 32px
  space-6: 48px
  touch-min: 44px
  gutter-mobile: 16px
  gutter-desktop: 24px
  max-width: 1280px
---

## Brand & Style

This design system expresses a fusion of artisanal cellar tradition and modern agricultural technology. The aesthetic evokes the warmth of wine vaults, hand-labeled bottles, and brass tasting rooms, balanced with the clarity required for lot traceability and commercial operations.

The visual style blends refined editorial minimalism with tactile physical cues. Clean, generous margins and warm parchment backdrops replace stark, clinical tech interfaces. The UI should convey pedigree, meticulous craftsmanship, transparency of origin, and certified trust.

## Colors

The palette grounds modern digital commerce in viticultural and brewing heritage:

- **Primary (`#58111A` - Deep Burgundy / Noble Wine):** Commands authority, quality, and legacy. Used for prominent brand moments, primary CTAs, active states, and dominant headlines.
- **Secondary (`#D97706` - Warm Amber Gold):** Highlights artisanal value, harvest authenticity, and active focal elements. Used for secondary accents, key highlights, and active metadata chips.
- **Surface & Canvas:**
  - Application Canvas: `#FDFBF7` (Warm Parchment / Ivory) ensures low-glare comfort and warmth.
  - Card & Container Surface: `#FFFFFF` (Pure Cellar Paper) provides crisp elevation against the warm parchment canvas.
  - Borders & Dividers: `#E7E2DA` delivers subtle, tactile definition without stark digital contrast.
- **Typography & Neutrals:**
  - Primary Text: `#1F2937` (Dark Slate Charcoal) ensures high-contrast readability across long data tables and technical descriptions.
  - Secondary / Subdued Text: `#6B7280` handles tertiary metadata, timestamps, and lot identifiers.
- **Functional Semantics:**
  - Verified Provenance / Success: `#15803D` (Forest Green).
  - Paused / Pending Warning: `#B45309` (Toasted Amber).
  - Batch Fault / Critical Error: `#B91C1C` (Crimson Red).

## Typography

The typographic hierarchy pairs high-contrast editorial elegance with utilitarian precision. 

- **Display & Headlines (`Playfair Display`):** Reserved for product names, cellar titles, appellation highlights, and major screen headings. Conveys human craft and vintage heritage.
- **Body, Form Fields, Tables & Analytics (`Inter`):** Delivers clean optical rhythm, high x-height readability, and structured numeric alignment for ABV percentages, specific gravities, batch numbers, and sensory tasting scales.
- **Uppercase Tracking:** Used exclusively for micro-labels, certificates, and lot numbers (`label-md` and `code-batch`) with wide letter spacing to mimic embossed stamp labels.

## Layout & Spacing

The layout is built upon a rigid 8px baseline rhythm (with 4px sub-increments for compact tags and icons).

- **Fluid Grid Architecture:**
  - **Desktop (>= 1024px):** 12 columns, 24px gutters, maximum container width of 1280px, centered with dynamic auto-margins.
  - **Tablet (768px - 1023px):** 8 columns, 20px gutters, 24px outer margins.
  - **Mobile (< 768px):** 4 columns, 16px gutters, 16px outer margins.
- **Physical Touch Target:** All interactive controls (buttons, selectors, toggles, badge filters) enforce an absolute minimum hitbox of 44px on mobile viewports to ensure seamless cellar and warehouse field usage.
- **Rhythm Rules:** Vertical grouping between headings and direct body copy uses 8px (`space-1`), form field spacing uses 16px (`space-2`), component cards use 24px padding (`space-3`), and macro-section stacking uses 48px (`space-6`).

## Elevation & Depth

To sustain an organic, artisanal look, the system eliminates heavy, cold black shadows in favor of warm, low-contrast physical tiers:

- **Flat Canvas (`#FDFBF7`):** Base grounding tone with no shadow.
- **Level 1 (Cards, Product Tiles):** Solid `#FFFFFF` surface framed by a 1px border in `#E7E2DA`. Ambient shadow tinted with warm umber: `0 1px 3px rgba(88, 17, 26, 0.04), 0 1px 2px rgba(31, 41, 55, 0.03)`.
- **Level 2 (Flyouts, Traceability Drawers, Popovers):** Solid `#FFFFFF` framed by a 1px border in `#E7E2DA`. Shadow: `0 10px 15px -3px rgba(88, 17, 26, 0.06), 0 4px 6px -2px rgba(31, 41, 55, 0.03)`.
- **Level 3 (Modals, Certificate Overlays):** `0 20px 25px -5px rgba(88, 17, 26, 0.1), 0 10px 10px -5px rgba(31, 41, 55, 0.04)`. Backdrops feature a warm scrim overlay of `#1F2937` at 40% opacity with a subtle 2px blur.

## Shapes

The geometry reflects tailored, artisanal framing with purposeful exceptions:

- **Form Controls & Buttons:** 8px (`0.5rem`) corner radius. Balances modern software ergonomics with structured, firm edges.
- **Cards, Panels & Containers:** 12px (`0.75rem`) corner radius. Produces soft framing around editorial product photos, technical notes, and analytics cards.
- **Traceability Badges & Status Chips:** 9999px (Fully Pill-Shaped). Encapsulates official verification stamps, harvest years, barrel types, and ABV indicators as distinct seal-like elements.
- **Borders:** Thin, crisp 1px strokes in `#E7E2DA` provide structured boundaries throughout all elevated surfaces.

## Components

### Buttons
- **Primary:** Background `#58111A`, text `#FFFFFF`, border-radius 8px, minimum height 44px. Hover state deepens to `#420D13` with subtle transition. Active state shifts to `#310A0E`.
- **Secondary / Craft Accent:** Background `#FFFFFF`, border 1.5px solid `#D97706`, text `#D97706`, font-weight 600. Hover applies an 8% tint of `#D97706` as a background fill.
- **Tertiary / Ghost:** Text `#1F2937`, transparent background. Hover displays an overlay of `#E7E2DA` at 40% opacity.

### Chips & Provenance Badges
- **Pill Shape (`9999px`):** Heights of 24px (compact) and 32px (default). Horizontal padding 12px.
- **Verified Traceability Chip:** Background `#15803D` at 10% opacity, border 1px solid `#15803D` at 30% opacity, text `#15803D`, accompanied by a small verification seal icon.
- **Vintage / Lot Identifier:** Background `#FDFBF7`, border 1px solid `#E7E2DA`, text `#1F2937`, monospace-styled tracking (`code-batch`).

### Cards & Cellar Tiles
- Background `#FFFFFF`, 1px border in `#E7E2DA`, 12px radius, 24px internal padding.
- **Media Header:** Edge-to-edge top image container with a subtle warm fade overlay to showcase bottle labels and barrel finishes.
- **Footer Section:** Border-top 1px solid `#E7E2DA`, separating sensory notes from commercial pricing and traceability links.

### Input Fields & Selectors
- Background `#FFFFFF`, border 1px solid `#E7E2DA`, radius 8px, height 44px, padding 0 16px.
- **Text:** `#1F2937` font size 14px; placeholder text in `#6B7280`.
- **Focus State:** 2px ring in `#58111A` with zero offset; border color shifts to `#58111A`.

### Checkboxes & Radio Controls
- Minimum interactive footprint of 44px × 44px centered over an 18px visual control.
- Visual frame: 1.5px solid `#E7E2DA`, radius 4px (checkbox) or 9999px (radio).
- **Checked State:** Fill `#58111A` with crisp white checkmark or center pip.

### Lists & Batch Tables
- Alternating subtle rows using `#FFFFFF` and `#FDFBF7`.
- Table header: Text in `#6B7280`, uppercase `label-md`, border-bottom 2px solid `#E7E2DA`.
- Cell dividers: 1px border `#E7E2DA`. Numeric values (e.g., ABV, stock units, lot ID) aligned right using tabular figures.

### Product-Specific Component: Traceability Timeline
- Vertical or horizontal flow nodes linked by 2px solid `#E7E2DA` line tracks.
- Completed stages (Harvest, Fermentation, Barrel Aging, Bottling) display a 20px `#15803D` icon circle; in-progress stages pulse with `#D97706`.