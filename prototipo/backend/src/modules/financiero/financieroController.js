import { getDashboardFinanciero } from './financieroService.js';

export async function getResumen(req, res, next) {
  try {
    const productorId = req.productorId;
    if (!productorId) {
      return res.status(403).json({
        success: false,
        error: 'El usuario autenticado no tiene un perfil de productor asociado.'
      });
    }

    const dashboard = await getDashboardFinanciero(productorId);
    res.json({
      success: true,
      data: dashboard
    });
  } catch (err) {
    next(err);
  }
}
