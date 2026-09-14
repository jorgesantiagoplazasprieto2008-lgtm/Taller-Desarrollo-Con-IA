import {
  getPublicProductos,
  getProductoById,
  getProductorProductos,
  createProducto,
  updateProducto,
  toggleEstadoProducto
} from './productoService.js';

export async function listPublic(req, res, next) {
  try {
    const { tipo, q } = req.query;
    const productos = await getPublicProductos({ tipo, q });
    res.json({
      success: true,
      data: productos
    });
  } catch (err) {
    next(err);
  }
}

export async function getById(req, res, next) {
  try {
    const producto = await getProductoById(req.params.id);
    res.json({
      success: true,
      data: producto
    });
  } catch (err) {
    next(err);
  }
}

export async function listProductor(req, res, next) {
  try {
    const productorId = req.productorId;
    if (!productorId) {
      return res.status(403).json({
        success: false,
        error: 'El usuario autenticado no tiene un perfil de productor asociado.'
      });
    }

    const productos = await getProductorProductos(productorId);
    res.json({
      success: true,
      data: productos
    });
  } catch (err) {
    next(err);
  }
}

export async function create(req, res, next) {
  try {
    const productorId = req.productorId;
    if (!productorId) {
      return res.status(403).json({
        success: false,
        error: 'El usuario autenticado no tiene un perfil de productor asociado.'
      });
    }

    const newProd = await createProducto(productorId, req.body);
    res.status(201).json({
      success: true,
      message: 'Producto creado exitosamente.',
      data: newProd
    });
  } catch (err) {
    next(err);
  }
}

export async function update(req, res, next) {
  try {
    const productorId = req.productorId;
    const updated = await updateProducto(productorId, req.params.id, req.body);
    res.json({
      success: true,
      message: 'Producto actualizado exitosamente.',
      data: updated
    });
  } catch (err) {
    next(err);
  }
}

export async function changeStatus(req, res, next) {
  try {
    const productorId = req.productorId;
    const { estado } = req.body;
    const updated = await toggleEstadoProducto(productorId, req.params.id, estado);
    res.json({
      success: true,
      message: `Estado de producto actualizado a ${estado}.`,
      data: updated
    });
  } catch (err) {
    next(err);
  }
}
