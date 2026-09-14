import { memoryStore } from '../../config/db.js';

export async function getProductosConDisponibilidad(establecimientoId) {
  const estId = parseInt(establecimientoId, 10);
  const establecimiento = memoryStore.establecimientos.find(e => e.id === estId);

  if (!establecimiento) {
    const error = new Error('Establecimiento de hostelería no encontrado.');
    error.status = 404;
    throw error;
  }

  // Todos los productos activos en catálogo
  const productosActivos = memoryStore.productos.filter(p => p.estado === 'ACTIVO');

  return productosActivos.map(p => {
    const productor = memoryStore.productores.find(pr => pr.id === p.productor_id);
    const dispRecord = memoryStore.disponibilidad.find(
      d => d.producto_id === p.id && d.establecimiento_id === estId
    );

    return {
      producto_id: p.id,
      nombre: p.nombre,
      tipo_bebida: p.tipo_bebida,
      categoria: p.tipo_bebida || p.categoria || 'Vino Artesanal',
      grado_alcoholico: p.grado_alcoholico || 13.5,
      presentacion: p.presentacion,
      precio: p.precio,
      productor_nombre: productor ? productor.nombre_comercial : 'Bodega Artesanal',
      productor_region: productor ? productor.ubicacion_origen : 'Origen Noble',
      disponible: dispRecord ? Boolean(dispRecord.disponible) : false,
      precio_copa: dispRecord && dispRecord.precio_copa ? dispRecord.precio_copa : Math.round(p.precio * 0.25),
      precio_botella: dispRecord && dispRecord.precio_botella ? dispRecord.precio_botella : p.precio
    };
  });
}

export async function toggleDisponibilidad(establecimientoId, productoId, disponible) {
  const estId = parseInt(establecimientoId, 10);
  const prodId = parseInt(productoId, 10);

  const producto = memoryStore.productos.find(p => p.id === prodId);
  if (!producto) {
    const error = new Error('Producto no encontrado.');
    error.status = 404;
    throw error;
  }

  let dispRecord = memoryStore.disponibilidad.find(
    d => d.producto_id === prodId && d.establecimiento_id === estId
  );

  const newStatus = disponible === true || disponible === 1 || disponible === 'true' ? 1 : 0;

  if (dispRecord) {
    dispRecord.disponible = newStatus;
  } else {
    const newId = memoryStore.disponibilidad.length ? Math.max(...memoryStore.disponibilidad.map(d => d.id)) + 1 : 1;
    dispRecord = {
      id: newId,
      producto_id: prodId,
      establecimiento_id: estId,
      disponible: newStatus
    };
    memoryStore.disponibilidad.push(dispRecord);
  }

  return {
    producto_id: prodId,
    establecimiento_id: estId,
    disponible: Boolean(newStatus),
    mensaje: `Disponibilidad de '${producto.nombre}' actualizada a ${newStatus ? 'DISPONIBLE' : 'NO DISPONIBLE'}.`
  };
}
