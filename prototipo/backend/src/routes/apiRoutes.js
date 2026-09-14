import express from 'express';
import * as usuarioController from '../modules/usuario/usuarioController.js';
import * as productoController from '../modules/producto/productoController.js';
import * as loteController from '../modules/lote/loteController.js';
import * as hosteleriaController from '../modules/hosteleria/hosteleriaController.js';
import * as financieroController from '../modules/financiero/financieroController.js';
import { authenticate, requireRole, optionalAuthenticate } from '../middleware/authMiddleware.js';

const router = express.Router();

// Endpoint de salud
router.get('/health', (req, res) => {
  res.json({
    status: 'UP',
    app: 'Entre Copas API',
    timestamp: new Date().toISOString()
  });
});

// ==========================================
// 1. AUTENTICACIÓN Y USUARIOS
// ==========================================
router.post('/auth/login', usuarioController.login);
router.post('/auth/register', usuarioController.register);
router.post('/auth/registro', usuarioController.register); // Alias en español
router.get('/auth/me', authenticate, usuarioController.me);

// ==========================================
// 2. CATÁLOGO PÚBLICO Y PRODUCTOS
// ==========================================
router.get('/productos', optionalAuthenticate, productoController.listPublic);
router.get('/productos/:id', optionalAuthenticate, productoController.getById);

// Endpoints protegidos para Productor (compatibles con /panel y /productor)
router.get('/panel/productos', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_PRODUCTOR']), productoController.listProductor);
router.get('/productor/productos', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_PRODUCTOR']), productoController.listProductor);

router.post('/panel/productos', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_PRODUCTOR']), productoController.create);
router.post('/productor/productos', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_PRODUCTOR']), productoController.create);

router.put('/panel/productos/:id', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_PRODUCTOR']), productoController.update);
router.put('/productor/productos/:id', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_PRODUCTOR']), productoController.update);

router.patch('/panel/productos/:id/estado', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_PRODUCTOR']), productoController.changeStatus);
router.patch('/productor/productos/:id/estado', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_PRODUCTOR']), productoController.changeStatus);

// ==========================================
// 3. NÚCLEO DE LOTES Y TRAZABILIDAD
// ==========================================
// Endpoint 100% público para verificar origen y analítica de un lote
router.get('/trazabilidad/:codigo', loteController.getTrazabilidad);

// Endpoints de Lotes para el Productor
router.get('/panel/lotes', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_PRODUCTOR']), loteController.listByProductor);
router.get('/productor/lotes', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_PRODUCTOR']), loteController.listByProductor);

router.post('/panel/lotes', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_PRODUCTOR']), loteController.create);
router.post('/productor/lotes', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_PRODUCTOR']), loteController.create);

// ==========================================
// 4. HOSTELERÍA Y DISPONIBILIDAD
// ==========================================
router.get('/hosteleria/disponibilidad', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_HOSTELERIA']), hosteleriaController.listDisponibilidad);
router.get('/hosteleria/catalogo', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_HOSTELERIA']), hosteleriaController.listDisponibilidad);

router.post('/hosteleria/disponibilidad', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_HOSTELERIA']), hosteleriaController.updateDisponibilidad);
router.post('/hosteleria/disponibilidad/toggle', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_HOSTELERIA']), hosteleriaController.updateDisponibilidad);

// ==========================================
// 5. DASHBOARD FINANCIERO DEL PRODUCTOR
// ==========================================
router.get('/financiero/resumen', authenticate, requireRole(['ROLE_ADMIN', 'ROLE_PRODUCTOR']), financieroController.getResumen);

export default router;
