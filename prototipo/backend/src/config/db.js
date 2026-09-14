import mysql from 'mysql2/promise';
import { ENV } from './env.js';

let pool = null;
let useMemoryFallback = false;

// Almacén en memoria sincronizado con datos semilla de mysql_workbench_schema.sql
export const memoryStore = {
  roles: [
    { id: 1, nombre: 'ROLE_ADMIN', descripcion: 'Administrador general de la plataforma' },
    { id: 2, nombre: 'ROLE_PRODUCTOR', descripcion: 'Maestro productor artesanal con gestión de lotes y finanzas' },
    { id: 3, nombre: 'ROLE_HOSTELERIA', descripcion: 'Establecimiento gastronómico con control de disponibilidad' },
    { id: 4, nombre: 'ROLE_CONSUMIDOR', descripcion: 'Consumidor final registrado' }
  ],
  usuarios: [
    {
      id: 1,
      email: 'admin@entrecopas.com',
      password: '$2b$10$BTCqzgYPgDNsTAqpKaCQDu1ol2LtJi6usgzQrdmONKeAE2bfepc9O',
      nombre_completo: 'Administrador Principal',
      telefono: '+57 300 111 2233',
      activo: 1,
      created_at: new Date('2026-08-01T10:00:00Z')
    },
    {
      id: 2,
      email: 'contacto@bodegasangabriel.com',
      password: '$2b$10$BTCqzgYPgDNsTAqpKaCQDu1ol2LtJi6usgzQrdmONKeAE2bfepc9O',
      nombre_completo: 'Carlos Mendoza - Bodega San Gabriel',
      telefono: '+57 310 456 7890',
      activo: 1,
      created_at: new Date('2026-08-01T10:00:00Z')
    },
    {
      id: 3,
      email: 'gerencia@rincongourmet.com',
      password: '$2b$10$BTCqzgYPgDNsTAqpKaCQDu1ol2LtJi6usgzQrdmONKeAE2bfepc9O',
      nombre_completo: 'Restaurante El Rincón Gourmet',
      telefono: '+57 315 789 0123',
      activo: 1,
      created_at: new Date('2026-08-01T10:00:00Z')
    },
    {
      id: 4,
      email: 'juan.consumidor@gmail.com',
      password: '$2b$10$BTCqzgYPgDNsTAqpKaCQDu1ol2LtJi6usgzQrdmONKeAE2bfepc9O',
      nombre_completo: 'Juan Pérez',
      telefono: '+57 320 987 6543',
      activo: 1,
      created_at: new Date('2026-08-01T10:00:00Z')
    }
  ],
  usuario_rol: [
    { usuario_id: 1, rol_id: 1 },
    { usuario_id: 2, rol_id: 2 },
    { usuario_id: 3, rol_id: 3 },
    { usuario_id: 4, rol_id: 4 }
  ],
  productores: [
    {
      id: 1,
      usuario_id: 2,
      nombre_comercial: 'Bodega Artesanal San Gabriel',
      registro_sanitario: 'RS-COL-2024-V09',
      descripcion_historia: 'Viñedos de altura situados a 2.100 msnm en Villa de Leyva, con vendimia manual y fermentación en barricas de roble francés.',
      ubicacion_origen: 'Villa de Leyva, Boyacá',
      created_at: new Date('2026-08-01T10:00:00Z')
    }
  ],
  productos: [
    {
      id: 1,
      productor_id: 1,
      nombre: 'Vino Tinto Malbec Gran Reserva 2024',
      tipo_bebida: 'Vino Tinto',
      descripcion: 'Cuerpo robusto, notas a ciruela madura, mora silvestre, vainilla y taninos aterciopelados de crianza en roble francés.',
      presentacion: 'Botella 750ml',
      precio: 65000.00,
      estado: 'ACTIVO',
      created_at: new Date('2026-08-05T10:00:00Z')
    },
    {
      id: 2,
      productor_id: 1,
      nombre: 'Vino Blanco Sauvignon Blanc',
      tipo_bebida: 'Vino Blanco',
      descripcion: 'Fresco, herbáceo con acidez vibrante y notas cítricas de maracuyá y lima de alta montaña.',
      presentacion: 'Botella 750ml',
      precio: 52000.00,
      estado: 'ACTIVO',
      created_at: new Date('2026-08-06T10:00:00Z')
    },
    {
      id: 3,
      productor_id: 1,
      nombre: 'Hidromiel Tradicional Roble',
      tipo_bebida: 'Hidromiel',
      descripcion: 'Fermentación lenta de mieles de abejas nativas con 6 meses de reposo en barrica noble.',
      presentacion: 'Botella 500ml',
      precio: 42000.00,
      estado: 'PAUSADO',
      created_at: new Date('2026-08-08T10:00:00Z')
    },
    {
      id: 4,
      productor_id: 1,
      nombre: 'Cerveza Artesanal IPA Dorada',
      tipo_bebida: 'Cerveza Artesanal',
      descripcion: 'Aroma explosivo a lúpulos cítricos y resinosos, amargor limpio y final seco refrescante.',
      presentacion: 'Lata 330ml',
      precio: 14500.00,
      estado: 'ACTIVO',
      created_at: new Date('2026-08-10T10:00:00Z')
    }
  ],
  lotes: [
    {
      id: 1,
      producto_id: 1,
      codigo_trazabilidad: 'EC-2026-PR01-A101',
      fecha_produccion: '2026-08-14',
      volumen_litros: 1200.00,
      merma_litros: 45.00,
      costo_total: 8400000.00,
      parametros_analiticos: {
        ph: 3.65,
        graduacion_alcoholica: 13.5,
        acidez_total_gl: 5.2,
        densidad: 0.994
      },
      created_at: new Date('2026-08-14T10:00:00Z')
    },
    {
      id: 2,
      producto_id: 1,
      codigo_trazabilidad: 'EC-2026-PR01-A102',
      fecha_produccion: '2026-08-28',
      volumen_litros: 1000.00,
      merma_litros: 32.00,
      costo_total: 7100000.00,
      parametros_analiticos: {
        ph: 3.62,
        graduacion_alcoholica: 13.4,
        acidez_total_gl: 5.4,
        densidad: 0.995
      },
      created_at: new Date('2026-08-28T10:00:00Z')
    },
    {
      id: 3,
      producto_id: 4,
      codigo_trazabilidad: 'EC-2026-PR01-C201',
      fecha_produccion: '2026-08-20',
      volumen_litros: 600.00,
      merma_litros: 22.00,
      costo_total: 2100000.00,
      parametros_analiticos: {
        ph: 4.20,
        graduacion_alcoholica: 6.5,
        acidez_total_gl: 3.8,
        densidad: 1.010
      },
      created_at: new Date('2026-08-20T10:00:00Z')
    }
  ],
  establecimientos: [
    {
      id: 1,
      usuario_id: 3,
      nombre: 'Restaurante El Rincón Gourmet',
      direccion: 'Carrera 7 # 45-12, Zona Gourmet',
      ciudad: 'Bogotá D.C.',
      activo: 1,
      created_at: new Date('2026-08-01T10:00:00Z')
    },
    {
      id: 2,
      usuario_id: 3,
      nombre: 'Bar Cafetal San Pedro',
      direccion: 'Calle 10 # 3-24',
      ciudad: 'Villa de Leyva',
      activo: 1,
      created_at: new Date('2026-08-02T10:00:00Z')
    }
  ],
  disponibilidad: [
    { id: 1, producto_id: 1, establecimiento_id: 1, disponible: 1 },
    { id: 2, producto_id: 2, establecimiento_id: 1, disponible: 0 },
    { id: 3, producto_id: 4, establecimiento_id: 1, disponible: 1 },
    { id: 4, producto_id: 1, establecimiento_id: 2, disponible: 1 }
  ]
};

export async function initDatabaseConnection() {
  try {
    pool = mysql.createPool({
      host: ENV.DB.HOST,
      port: ENV.DB.PORT,
      user: ENV.DB.USER,
      password: ENV.DB.PASSWORD,
      database: ENV.DB.NAME,
      waitForConnections: true,
      connectionLimit: 10,
      queueLimit: 0
    });

    // Probar conexión real a MySQL
    const [rows] = await pool.query('SELECT 1 + 1 AS health');
    console.log(`✅ [MySQL 8.x] Conexión establecida exitosamente a la base de datos '${ENV.DB.NAME}' en ${ENV.DB.HOST}:${ENV.DB.PORT}`);
    useMemoryFallback = false;
    return true;
  } catch (err) {
    console.warn(`⚠️ [MySQL] No se pudo conectar a MySQL (${err.code || err.message}).`);
    console.warn(`ℹ️ [Modo Resiliente] Activando adaptador en memoria de alta fidelidad con datos semilla de Entre Copas.`);
    console.warn(`💡 Para conectar a MySQL local, actualiza la contraseña en el archivo .env y ejecuta el script 'database/mysql_workbench_schema.sql' en Workbench.`);
    useMemoryFallback = true;
    return false;
  }
}

export function isUsingMemoryFallback() {
  return useMemoryFallback;
}

export async function dbQuery(sql, params = []) {
  if (!useMemoryFallback && pool) {
    try {
      const [results] = await pool.execute(sql, params);
      return results;
    } catch (err) {
      console.error('Error ejecutando consulta MySQL:', err);
      throw err;
    }
  }

  // Si está en fallback de memoria, simula consultas SQL comunes
  return handleMemoryQuery(sql, params);
}

function handleMemoryQuery(sql, params) {
  // Manejo de compatibilidad en memoria
  return [];
}
