import { memoryStore } from '../../config/db.js';

export function generateTraceabilityCode(productorId, loteSeq) {
  const year = new Date().getFullYear();
  const prodPad = String(productorId).padStart(2, '0');
  const seqHex = (loteSeq + 100).toString(16).toUpperCase().padStart(4, '0');
  return `EC-${year}-PR${prodPad}-${seqHex}`;
}

export async function createLote(productorId, data) {
  const { producto_id, merma_litros, costo_total, parametros_analiticos } = data;
  const fecha_produccion = data.fecha_produccion || data.fecha_elaboracion;
  const volumen_litros = data.volumen_litros !== undefined ? data.volumen_litros : data.volumen_producido_litros;

  const prodId = parseInt(producto_id, 10);
  const producto = memoryStore.productos.find(p => p.id === prodId);

  if (!producto) {
    const error = new Error('Producto asociado no encontrado.');
    error.status = 404;
    throw error;
  }

  // Aislamiento Multi-tenant: el producto debe pertenecer al productor autenticado
  if (producto.productor_id !== productorId) {
    const error = new Error('No tienes permisos para registrar un lote en un producto ajeno.');
    error.status = 403;
    throw error;
  }

  if (!fecha_produccion) {
    const error = new Error('La fecha de producción es obligatoria.');
    error.status = 400;
    throw error;
  }

  // Validar que la fecha no sea futura
  const prodDate = new Date(fecha_produccion);
  const now = new Date();
  if (prodDate.toISOString().substring(0, 10) > now.toISOString().substring(0, 10)) {
    const error = new Error('La fecha de producción no puede ser una fecha futura.');
    error.status = 400;
    throw error;
  }

  const vol = parseFloat(volumen_litros);
  if (isNaN(vol) || vol <= 0) {
    const error = new Error('El volumen producido debe ser un número mayor a cero.');
    error.status = 400;
    throw error;
  }

  const merma = parseFloat(merma_litros || 0);
  if (isNaN(merma) || merma < 0) {
    const error = new Error('La merma no puede ser un número negativo.');
    error.status = 400;
    throw error;
  }

  if (merma > vol) {
    const error = new Error(`La merma (${merma} L) no puede superar el volumen total producido (${vol} L).`);
    error.status = 400;
    throw error;
  }

  const costo = parseFloat(costo_total || 0);
  if (isNaN(costo) || costo < 0) {
    const error = new Error('El costo total debe ser un valor monetario mayor o igual a cero.');
    error.status = 400;
    throw error;
  }

  // Validar parámetros analíticos
  const params = parametros_analiticos || {};
  const ph = parseFloat(params.ph);
  const abv = parseFloat(params.graduacion_alcoholica !== undefined ? params.graduacion_alcoholica : params.alcohol_real_pct);
  const acidez = parseFloat(params.acidez_total_gl !== undefined ? params.acidez_total_gl : params.acidez_total_g_l);
  const densidad = parseFloat(params.densidad !== undefined ? params.densidad : params.densidad_final);

  if (isNaN(ph) || isNaN(abv)) {
    const error = new Error('Los parámetros analíticos obligatorios (pH y graduación alcohólica) deben ser números válidos.');
    error.status = 400;
    throw error;
  }

  const formattedParams = {
    ph: Number(ph.toFixed(2)),
    graduacion_alcoholica: Number(abv.toFixed(1)),
    alcohol_real_pct: Number(abv.toFixed(1)),
    acidez_total_gl: isNaN(acidez) ? 5.2 : Number(acidez.toFixed(1)),
    acidez_total_g_l: isNaN(acidez) ? 5.2 : Number(acidez.toFixed(1)),
    densidad: isNaN(densidad) ? 0.994 : Number(densidad.toFixed(3)),
    densidad_final: isNaN(densidad) ? 0.994 : Number(densidad.toFixed(3)),
    crianza_meses: params.crianza_meses || 0
  };

  const newId = memoryStore.lotes.length ? Math.max(...memoryStore.lotes.map(l => l.id)) + 1 : 1;
  const codigoTrazabilidad = generateTraceabilityCode(productorId, newId);

  const newLote = {
    id: newId,
    producto_id: prodId,
    codigo_lote: codigoTrazabilidad,
    codigo_trazabilidad: codigoTrazabilidad,
    fecha_produccion,
    fecha_elaboracion: fecha_produccion,
    volumen_litros: vol,
    volumen_producido_litros: vol,
    merma_litros: merma,
    costo_total: costo,
    parametros_analiticos: formattedParams,
    created_at: new Date()
  };

  memoryStore.lotes.push(newLote);

  return {
    ...newLote,
    producto_nombre: producto.nombre,
    volumen_neto: vol - merma
  };
}

export async function getLotesByProductor(productorId) {
  const prodIds = memoryStore.productos
    .filter(p => p.productor_id === productorId)
    .map(p => p.id);

  return memoryStore.lotes
    .filter(l => prodIds.includes(l.producto_id))
    .map(l => {
      const prod = memoryStore.productos.find(p => p.id === l.producto_id);
      return {
        ...l,
        codigo_lote: l.codigo_lote || l.codigo_trazabilidad,
        volumen_producido_litros: l.volumen_producido_litros || l.volumen_litros,
        fecha_elaboracion: l.fecha_elaboracion || l.fecha_produccion,
        producto_nombre: prod ? prod.nombre : 'Desconocido',
        presentacion: prod ? prod.presentacion : '',
        merma_porcentaje: l.volumen_litros > 0 ? Number(((l.merma_litros / l.volumen_litros) * 100).toFixed(2)) : 0
      };
    })
    .sort((a, b) => new Date(b.fecha_produccion) - new Date(a.fecha_produccion));
}

export async function getTrazabilidadByCodigo(codigo) {
  if (!codigo || !codigo.trim()) {
    const error = new Error('Código de trazabilidad requerido.');
    error.status = 400;
    throw error;
  }

  const cleanCode = codigo.trim().toUpperCase();
  const lote = memoryStore.lotes.find(l => 
    (l.codigo_trazabilidad && l.codigo_trazabilidad.toUpperCase() === cleanCode) ||
    (l.codigo_lote && l.codigo_lote.toUpperCase() === cleanCode)
  );

  if (!lote) {
    const error = new Error(`No se encontró ningún lote con el código de trazabilidad '${cleanCode}'. Verifica el código en la etiqueta.`);
    error.status = 404;
    throw error;
  }

  const producto = memoryStore.productos.find(p => p.id === lote.producto_id);
  const productor = producto ? memoryStore.productores.find(pr => pr.id === producto.productor_id) : null;

  // Privacidad de negocio según PRD Sec 3.5:
  // NUNCA revelar al consumidor público costo_total ni merma_litros interna
  return {
    codigo_lote: lote.codigo_lote || lote.codigo_trazabilidad,
    codigo_trazabilidad: lote.codigo_trazabilidad || lote.codigo_lote,
    fecha_produccion: lote.fecha_produccion,
    fecha_elaboracion: lote.fecha_elaboracion || lote.fecha_produccion,
    volumen_lote_litros: lote.volumen_litros || lote.volumen_producido_litros,
    volumen_producido_litros: lote.volumen_producido_litros || lote.volumen_litros,
    parametros_analiticos: lote.parametros_analiticos,
    producto_nombre: producto ? producto.nombre : 'Bebida Artesanal',
    categoria: producto ? (producto.tipo_bebida || producto.categoria) : 'Vino Tinto',
    grado_alcoholico: producto ? (producto.grado_alcoholico || 13.5) : 13.5,
    producto: producto ? {
      id: producto.id,
      nombre: producto.nombre,
      tipo_bebida: producto.tipo_bebida,
      descripcion: producto.descripcion,
      presentacion: producto.presentacion
    } : null,
    productor_nombre: productor ? productor.nombre_comercial : 'Bodega Artesanal',
    productor_region: productor ? (productor.ubicacion_origen || productor.region) : 'Origen Certificado',
    productor: productor ? {
      nombre_comercial: productor.nombre_comercial,
      registro_sanitario: productor.registro_sanitario,
      ubicacion_origen: productor.ubicacion_origen,
      descripcion_historia: productor.descripcion_historia
    } : null,
    certificado: {
      plataforma: 'Entre Copas - Verificación Oficial',
      fecha_consulta: new Date().toISOString()
    }
  };
}
