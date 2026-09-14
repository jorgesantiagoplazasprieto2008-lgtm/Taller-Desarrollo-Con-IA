import app from './app.js';
import { ENV } from './config/env.js';
import { initDatabaseConnection } from './config/db.js';

async function bootstrap() {
  console.log('================================================================');
  console.log(`🍷 Iniciando ${ENV.APP_NAME}`);
  console.log('================================================================');

  // Inicializar y verificar conexión a la base de datos
  await initDatabaseConnection();

  app.listen(ENV.PORT, () => {
    console.log(`🚀 Servidor ejecutándose en: http://localhost:${ENV.PORT}`);
    console.log(`🌐 Catálogo público:        http://localhost:${ENV.PORT}/#/catalogo`);
    console.log(`🛡️ Panel administrativo:    http://localhost:${ENV.PORT}/#/panel`);
    console.log(`🔍 Consulta trazabilidad:   http://localhost:${ENV.PORT}/#/trazabilidad/EC-2026-PR01-A101`);
    console.log('================================================================');
  });
}

bootstrap().catch(err => {
  console.error('Error fatal al iniciar la aplicación:', err);
  process.exit(1);
});
