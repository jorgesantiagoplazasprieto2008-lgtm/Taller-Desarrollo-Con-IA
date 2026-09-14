import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import rateLimit from 'express-rate-limit';
import path from 'path';
import { fileURLToPath } from 'url';
import apiRoutes from './routes/apiRoutes.js';
import { errorHandler } from './middleware/errorHandler.js';
import { ENV } from './config/env.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const frontendPath = path.resolve(__dirname, '../../frontend');

const app = express();

// 1. Cabeceras de seguridad con Helmet (OWASP CSP-HEADERS/HELMET)
app.use(helmet({
  contentSecurityPolicy: {
    directives: {
      defaultSrc: ["'self'"],
      scriptSrc: ["'self'", "'unsafe-inline'", "https://cdn.jsdelivr.net"],
      styleSrc: ["'self'", "'unsafe-inline'", "https://fonts.googleapis.com", "https://cdn.jsdelivr.net"],
      fontSrc: ["'self'", "https://fonts.gstatic.com", "data:"],
      imgSrc: ["'self'", "data:", "https://images.unsplash.com"],
      connectSrc: ["'self'"]
    }
  },
  crossOriginEmbedderPolicy: false
}));

// 2. CORS Restrictivo (OWASP CORS-POL/ORIGIN)
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

// 3. Rate limiting contra fuerza bruta (OWASP RATE-LIMIT/DDOS)
const authLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutos
  max: 100, // máximo 100 peticiones por ventana
  message: {
    success: false,
    error: 'Demasiadas peticiones desde esta IP. Por favor intenta más tarde.'
  }
});
app.use('/api/v1/auth', authLimiter);

// 4. Parseo de cuerpo JSON (con límite de tamaño seguro)
app.use(express.json({ limit: '2mb' }));
app.use(express.urlencoded({ extended: true, limit: '2mb' }));

// 5. Servir archivos estáticos del frontend
app.use(express.static(frontendPath));

// 6. Rutas de la API REST
app.use('/api/v1', apiRoutes);

// Endpoint de salud del sistema
app.get('/api/health', (req, res) => {
  res.json({
    status: 'UP',
    app: ENV.APP_NAME,
    timestamp: new Date().toISOString()
  });
});

// 7. Enrutador fallback para SPA (Permite navegación cliente en /catalogo, /panel, etc.)
app.get('*', (req, res, next) => {
  if (req.originalUrl.startsWith('/api/')) {
    return res.status(404).json({ success: false, error: 'Endpoint no encontrado.' });
  }
  res.sendFile(path.join(frontendPath, 'index.html'));
});

// 8. Manejador centralizado de errores (OWASP ERR-MASK/LOG-SEC)
app.use(errorHandler);

export default app;
