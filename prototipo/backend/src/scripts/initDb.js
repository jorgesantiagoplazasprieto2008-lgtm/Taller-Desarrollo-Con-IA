import fs from 'fs';
import path from 'path';
import mysql from 'mysql2/promise';
import { fileURLToPath } from 'url';
import { ENV } from '../config/env.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

async function runInit() {
  console.log(`🔌 Conectando a MySQL en ${ENV.DB.HOST}:${ENV.DB.PORT}...`);
  
  try {
    const connection = await mysql.createConnection({
      host: ENV.DB.HOST,
      port: ENV.DB.PORT,
      user: ENV.DB.USER,
      password: ENV.DB.PASSWORD,
      multipleStatements: true
    });

    console.log('✅ Conexión establecida con éxito.');

    const sqlPath = path.resolve(__dirname, '../../../database/mysql_workbench_schema.sql');
    console.log(`📖 Leyendo script DDL desde: ${sqlPath}`);
    const sqlContent = fs.readFileSync(sqlPath, 'utf-8');

    console.log('🚀 Ejecutando script de inicialización de esquema y datos semilla...');
    await connection.query(sqlContent);

    console.log('✨ [ÉXITO] Base de datos entrecopas_db inicializada y sembrada correctamente con el esquema del TRD.');
    await connection.end();
    process.exit(0);
  } catch (err) {
    console.error('❌ Error al inicializar MySQL:', err.message);
    console.error('💡 Verifica que el servicio MySQL esté activo y las credenciales en el archivo .env sean correctas.');
    console.error('Alternativamente, puedes abrir y ejecutar el archivo database/mysql_workbench_schema.sql directamente en MySQL Workbench.');
    process.exit(1);
  }
}

runInit();
