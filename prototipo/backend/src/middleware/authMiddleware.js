import { verifyToken } from '../config/security.js';
import { memoryStore, isUsingMemoryFallback } from '../config/db.js';
import mysql from 'mysql2/promise';

export function authenticate(req, res, next) {
  const authHeader = req.headers.authorization;
  let token = null;

  if (authHeader && authHeader.startsWith('Bearer ')) {
    token = authHeader.substring(7);
  } else if (req.cookies && req.cookies.token) {
    token = req.cookies.token;
  }

  if (!token) {
    return res.status(401).json({
      success: false,
      error: 'No autenticado. Por favor inicia sesión para acceder.'
    });
  }

  const decoded = verifyToken(token);
  if (!decoded) {
    return res.status(401).json({
      success: false,
      error: 'Token inválido o expirado. Inicia sesión nuevamente.'
    });
  }

  req.user = decoded;

  // Aislamiento Multi-tenant: Si es productor, inyectar el productor_id correspondiente
  if (req.user.roles && req.user.roles.includes('ROLE_PRODUCTOR')) {
    const productor = memoryStore.productores.find(p => p.usuario_id === req.user.id);
    req.productorId = productor ? productor.id : (req.user.productorId || null);
    req.user.productorId = req.productorId;
  }

  // Establecimiento para Hostelería
  if (req.user.roles && req.user.roles.includes('ROLE_HOSTELERIA')) {
    const establecimiento = memoryStore.establecimientos.find(e => e.usuario_id === req.user.id);
    req.establecimientoId = establecimiento ? establecimiento.id : (req.user.establecimientoId || null);
    req.user.establecimientoId = req.establecimientoId;
  }

  // Si es ADMIN, permitir acceso por defecto a productor 1 y establecimiento 1 si no los tiene
  if (req.user.roles && req.user.roles.includes('ROLE_ADMIN')) {
    if (!req.productorId) req.productorId = 1;
    if (!req.user.productorId) req.user.productorId = 1;
    if (!req.user.establecimientoId) req.user.establecimientoId = 1;
  }

  next();
}

export function optionalAuthenticate(req, res, next) {
  const authHeader = req.headers.authorization;
  if (authHeader && authHeader.startsWith('Bearer ')) {
    const token = authHeader.substring(7);
    const decoded = verifyToken(token);
    if (decoded) {
      req.user = decoded;
      if (req.user.roles && req.user.roles.includes('ROLE_PRODUCTOR')) {
        const productor = memoryStore.productores.find(p => p.usuario_id === req.user.id);
        if (productor) req.productorId = productor.id;
      }
    }
  }
  next();
}

export function requireRole(allowedRoles = []) {
  return (req, res, next) => {
    if (!req.user) {
      return res.status(401).json({
        success: false,
        error: 'Autenticación requerida para acceder a este recurso.'
      });
    }

    const userRoles = req.user.roles || [];
    const hasRole = allowedRoles.some(r => userRoles.includes(r));

    if (!hasRole) {
      return res.status(403).json({
        success: false,
        error: 'Acceso denegado: No tienes permisos suficientes para realizar esta acción.'
      });
    }

    next();
  };
}
