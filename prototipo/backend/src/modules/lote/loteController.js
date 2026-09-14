import {
  createLote,
  getLotesByProductor,
  getTrazabilidadByCodigo
} from './loteService.js';

export async function create(req, res, next) {
  try {
    const productorId = req.productorId;
    if (!productorId) {
      return res.status(403).json({
        success: false,
        error: 'El usuario autenticado no tiene un perfil de productor asociado.'
      });
    }

    const newLote = await createLote(productorId, req.body);
    res.status(201).json({
      success: true,
      message: 'Lote registrado correctamente con código de trazabilidad generado.',
      data: newLote
    });
  } catch (err) {
    next(err);
  }
}

export async function listByProductor(req, res, next) {
  try {
    const productorId = req.productorId;
    if (!productorId) {
      return res.status(403).json({
        success: false,
        error: 'El usuario autenticado no tiene un perfil de productor asociado.'
      });
    }

    const lotes = await getLotesByProductor(productorId);
    res.json({
      success: true,
      data: lotes
    });
  } catch (err) {
    next(err);
  }
}

// Endpoint PÚBLICO: Consulta de trazabilidad por código
export async function getTrazabilidad(req, res, next) {
  try {
    const { codigo } = req.params;
    const trazabilidad = await getTrazabilidadByCodigo(codigo);
    res.json({
      success: true,
      data: trazabilidad
    });
  } catch (err) {
    next(err);
  }
}
