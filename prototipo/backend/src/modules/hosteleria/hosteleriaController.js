import {
  getProductosConDisponibilidad,
  toggleDisponibilidad
} from './hosteleriaService.js';

export async function listDisponibilidad(req, res, next) {
  try {
    const establecimientoId = req.user.establecimientoId;
    if (!establecimientoId) {
      return res.status(403).json({
        success: false,
        error: 'El usuario no tiene un establecimiento de hostelería asociado.'
      });
    }

    const productos = await getProductosConDisponibilidad(establecimientoId);
    res.json({
      success: true,
      data: productos
    });
  } catch (err) {
    next(err);
  }
}

export async function updateDisponibilidad(req, res, next) {
  try {
    const establecimientoId = req.user.establecimientoId;
    if (!establecimientoId) {
      return res.status(403).json({
        success: false,
        error: 'El usuario no tiene un establecimiento de hostelería asociado.'
      });
    }

    const { producto_id, disponible } = req.body;
    const result = await toggleDisponibilidad(establecimientoId, producto_id, disponible);
    res.json({
      success: true,
      message: result.mensaje,
      data: result
    });
  } catch (err) {
    next(err);
  }
}
