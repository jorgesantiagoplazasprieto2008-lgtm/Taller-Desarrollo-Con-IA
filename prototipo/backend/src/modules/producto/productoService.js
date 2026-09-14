import { memoryStore } from '../../config/db.js';

export async function getPublicProductos({ tipo, q }) {
  let productos = memoryStore.productos.filter(p => p.estado === 'ACTIVO');

  if (tipo && tipo !== 'Todos') {
    productos = productos.filter(p => p.tipo_bebida.toLowerCase() === tipo.toLowerCase());
  }

  if (q && q.trim()) {
    const query = q.trim().toLowerCase();
    productos = productos.filter(p => {
      const prod = memoryStore.productores.find(pr => pr.id === p.productor_id);
      const prodName = prod ? prod.nombre_comercial.toLowerCase() : '';
      return p.nombre.toLowerCase().includes(query) ||
             p.tipo_bebida.toLowerCase().includes(query) ||
             p.descripcion.toLowerCase().includes(query) ||
             prodName.includes(query);
    });
  }

  return productos.map(p => {
    const productor = memoryStore.productores.find(pr => pr.id === p.productor_id);
    const lotes = memoryStore.lotes.filter(l => l.producto_id === p.id);
    const dispCount = memoryStore.disponibilidad.filter(d => d.producto_id === p.id && d.disponible === 1).length;

    return {
      ...p,
      categoria: p.tipo_bebida || p.categoria,
      productor_nombre: productor ? productor.nombre_comercial : 'Bodega Artesanal',
      productor_region: productor ? productor.ubicacion_origen : 'Origen Certificado',
      productor: productor ? {
        id: productor.id,
        nombre_comercial: productor.nombre_comercial,
        ubicacion_origen: productor.ubicacion_origen
      } : null,
      lotes_count: lotes.length,
      establecimientos_disponibles_count: dispCount
    };
  });
}

export async function getProductoById(id) {
  const prodId = parseInt(id, 10);
  const producto = memoryStore.productos.find(p => p.id === prodId);

  if (!producto) {
    const error = new Error('Producto no encontrado o no disponible.');
    error.status = 404;
    throw error;
  }

  const productor = memoryStore.productores.find(pr => pr.id === producto.productor_id);
  const lotes = memoryStore.lotes
    .filter(l => l.producto_id === producto.id)
    .map(l => ({
      id: l.id,
      codigo_lote: l.codigo_lote || l.codigo_trazabilidad,
      codigo_trazabilidad: l.codigo_lote || l.codigo_trazabilidad,
      fecha_produccion: l.fecha_produccion,
      fecha_elaboracion: l.fecha_elaboracion || l.fecha_produccion,
      volumen_litros: l.volumen_litros,
      volumen_producido_litros: l.volumen_litros,
      parametros_analiticos: l.parametros_analiticos
    }));

  // Establecimientos con disponibilidad activa
  const establecimientos = memoryStore.disponibilidad
    .filter(d => d.producto_id === producto.id && d.disponible === 1)
    .map(d => {
      const est = memoryStore.establecimientos.find(e => e.id === d.establecimiento_id);
      return est ? {
        id: est.id,
        nombre: est.nombre,
        nombre_comercial: est.nombre,
        direccion: est.direccion,
        ciudad: est.ciudad,
        disponible: true
      } : null;
    })
    .filter(Boolean);

  return {
    ...producto,
    categoria: producto.tipo_bebida || producto.categoria,
    productor_nombre: productor ? productor.nombre_comercial : 'Bodega Artesanal',
    productor_region: productor ? productor.ubicacion_origen : 'Origen Certificado',
    productor,
    lotes,
    establecimientos
  };
}

export async function getProductorProductos(productorId) {
  const productos = memoryStore.productos.filter(p => p.productor_id === productorId);

  return productos.map(p => {
    const lotes = memoryStore.lotes.filter(l => l.producto_id === p.id);
    return {
      ...p,
      lotes_count: lotes.length,
      lotes: lotes.map(l => ({
        id: l.id,
        codigo_trazabilidad: l.codigo_trazabilidad,
        fecha_produccion: l.fecha_produccion,
        volumen_litros: l.volumen_litros,
        costo_total: l.costo_total
      }))
    };
  });
}

export async function createProducto(productorId, data) {
  const nombre = data.nombre;
  const tipo_bebida = data.tipo_bebida || data.categoria;
  const descripcion = data.descripcion || '';
  const presentacion = data.presentacion || 'Botella 750ml';
  const precio = data.precio !== undefined ? data.precio : (data.precio_sugerido || 55000);
  const estado = data.estado || 'ACTIVO';
  const grado_alcoholico = data.grado_alcoholico !== undefined ? parseFloat(data.grado_alcoholico) : 13.5;

  if (!nombre || !tipo_bebida) {
    const error = new Error('Nombre y categoría o tipo de bebida son obligatorios.');
    error.status = 400;
    throw error;
  }

  const numPrecio = parseFloat(precio);
  if (isNaN(numPrecio) || numPrecio < 0) {
    const error = new Error('El precio debe ser un número mayor o igual a cero.');
    error.status = 400;
    throw error;
  }

  const validEstados = ['ACTIVO', 'PAUSADO', 'RETIRADO'];
  const finalEstado = validEstados.includes(estado) ? estado : 'ACTIVO';

  const newId = memoryStore.productos.length ? Math.max(...memoryStore.productos.map(p => p.id)) + 1 : 1;

  const newProducto = {
    id: newId,
    productor_id: productorId,
    nombre: nombre.trim(),
    tipo_bebida: tipo_bebida.trim(),
    categoria: tipo_bebida.trim(),
    grado_alcoholico: isNaN(grado_alcoholico) ? 13.5 : grado_alcoholico,
    descripcion: descripcion ? descripcion.trim() : '',
    presentacion: presentacion.trim(),
    precio: numPrecio,
    estado: finalEstado,
    created_at: new Date(),
    updated_at: new Date()
  };

  memoryStore.productos.push(newProducto);
  return newProducto;
}

export async function updateProducto(productorId, id, data) {
  const prodId = parseInt(id, 10);
  const producto = memoryStore.productos.find(p => p.id === prodId);

  if (!producto) {
    const error = new Error('Producto no encontrado.');
    error.status = 404;
    throw error;
  }

  // Aislamiento Multi-tenant: no puede editar productos de otro productor
  if (producto.productor_id !== productorId) {
    const error = new Error('No tienes autorización para modificar este producto.');
    error.status = 403;
    throw error;
  }

  if (data.nombre) producto.nombre = data.nombre.trim();
  if (data.tipo_bebida) producto.tipo_bebida = data.tipo_bebida.trim();
  if (data.descripcion !== undefined) producto.descripcion = data.descripcion.trim();
  if (data.presentacion) producto.presentacion = data.presentacion.trim();
  if (data.precio !== undefined) {
    const numPrecio = parseFloat(data.precio);
    if (isNaN(numPrecio) || numPrecio < 0) {
      const error = new Error('El precio debe ser un número mayor o igual a cero.');
      error.status = 400;
      throw error;
    }
    producto.precio = numPrecio;
  }
  if (data.estado) {
    const validEstados = ['ACTIVO', 'PAUSADO', 'RETIRADO'];
    if (validEstados.includes(data.estado)) {
      producto.estado = data.estado;
    }
  }

  producto.updated_at = new Date();
  return producto;
}

export async function toggleEstadoProducto(productorId, id, nuevoEstado) {
  const prodId = parseInt(id, 10);
  const producto = memoryStore.productos.find(p => p.id === prodId);

  if (!producto) {
    const error = new Error('Producto no encontrado.');
    error.status = 404;
    throw error;
  }

  if (producto.productor_id !== productorId) {
    const error = new Error('No tienes autorización para modificar este producto.');
    error.status = 403;
    throw error;
  }

  const validEstados = ['ACTIVO', 'PAUSADO', 'RETIRADO'];
  if (!validEstados.includes(nuevoEstado)) {
    const error = new Error(`Estado inválido. Opciones: ${validEstados.join(', ')}`);
    error.status = 400;
    throw error;
  }

  producto.estado = nuevoEstado;
  producto.updated_at = new Date();
  return producto;
}
