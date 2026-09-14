-- =============================================================================
-- FLYWAY MIGRATION: V3__lote_estado_visibilidad.sql
-- Adición de ciclo de vida y control de visibilidad del lote artesanal
-- =============================================================================

ALTER TABLE lote 
ADD COLUMN estado VARCHAR(25) NOT NULL DEFAULT 'LIBERADO' 
COMMENT 'Estados posibles: EN_FERMENTACION, MADURACION, LIBERADO, AGOTADO';

ALTER TABLE lote 
ADD CONSTRAINT chk_lote_estado CHECK (estado IN ('EN_FERMENTACION', 'MADURACION', 'LIBERADO', 'AGOTADO'));

CREATE INDEX idx_lote_estado ON lote (estado);
