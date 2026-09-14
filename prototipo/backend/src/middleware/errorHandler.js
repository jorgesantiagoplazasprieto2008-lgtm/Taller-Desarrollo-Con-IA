import { ENV } from '../config/env.js';

export function errorHandler(err, req, res, next) {
  // Log estructurado en consola del servidor (para auditoría interna)
  const timestamp = new Date().toISOString();
  console.error(`[${timestamp}] [ERROR] ${req.method} ${req.originalUrl}:`, {
    message: err.message,
    status: err.status || 500,
    stack: ENV.NODE_ENV === 'development' ? err.stack : undefined
  });

  const statusCode = err.status || 500;
  
  // Enmascaramiento de errores conforme a OWASP Top 10 y rules.md
  const clientMessage = statusCode === 500 && ENV.NODE_ENV === 'production'
    ? 'Ocurrió un error interno en el servidor. Por favor intenta más tarde.'
    : (err.message || 'Error en la solicitud.');

  res.status(statusCode).json({
    success: false,
    error: clientMessage,
    timestamp: new Date().toISOString()
  });
}
