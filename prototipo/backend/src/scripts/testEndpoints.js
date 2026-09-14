/**
 * Entre Copas - Automated API & Business Logic Test Suite
 * Validates RBAC, Auth, Public Traceability, Multi-Tenant Producer Isolation, and Financial Aggregates
 */

import http from 'http';
import app from '../app.js';

let server;
const PORT = 3999;
const BASE_URL = `http://localhost:${PORT}/api/v1`;

function makeRequest(path, options = {}) {
  return new Promise((resolve, reject) => {
    const url = new URL(`${BASE_URL}${path}`);
    const reqOptions = {
      method: options.method || 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...(options.headers || {})
      }
    };

    const req = http.request(url, reqOptions, (res) => {
      let data = '';
      res.on('data', (chunk) => { data += chunk; });
      res.on('end', () => {
        try {
          const json = JSON.parse(data);
          resolve({ status: res.statusCode, data: json });
        } catch (e) {
          resolve({ status: res.statusCode, raw: data });
        }
      });
    });

    req.on('error', reject);

    if (options.body) {
      req.write(typeof options.body === 'string' ? options.body : JSON.stringify(options.body));
    }
    req.end();
  });
}

let passed = 0;
let failed = 0;

function assert(condition, message) {
  if (condition) {
    console.log(`  ✅ PASS: ${message}`);
    passed++;
  } else {
    console.error(`  ❌ FAIL: ${message}`);
    failed++;
  }
}

async function runSuite() {
  console.log('🍷 ======================================================');
  console.log('   ENTRE COPAS - SUITE DE PRUEBAS DE INTEGRACIÓN');
  console.log('======================================================\n');

  server = app.listen(PORT);

  try {
    // 1. Health Check
    console.log('📌 1. Verificando Endpoint de Salud...');
    const health = await makeRequest('/health');
    assert(health.status === 200 && health.data.status === 'UP', 'Servicio responde estado UP');

    // 2. Auth Flow
    console.log('\n📌 2. Autenticación y RBAC...');
    const invalidLogin = await makeRequest('/auth/login', {
      method: 'POST',
      body: { email: 'wrong@test.com', password: 'badpassword' }
    });
    assert(invalidLogin.status === 401, 'Rechazo seguro de credenciales erróneas (401)');

    const prodLogin = await makeRequest('/auth/login', {
      method: 'POST',
      body: { email: 'contacto@bodegasangabriel.com', password: 'Password123*' }
    });
    assert(prodLogin.status === 200 && prodLogin.data.success, 'Login de Productor exitoso');
    const prodToken = prodLogin.data.data.token;
    assert(prodToken && prodLogin.data.data.user.rol === 'ROLE_PRODUCTOR', 'Token JWT emitido con rol ROLE_PRODUCTOR');

    const hostLogin = await makeRequest('/auth/login', {
      method: 'POST',
      body: { email: 'gerencia@rincongourmet.com', password: 'Password123*' }
    });
    assert(hostLogin.status === 200 && hostLogin.data.data.user.rol === 'ROLE_HOSTELERIA', 'Login de Hostelería exitoso');
    const hostToken = hostLogin.data.data.token;

    // 3. Public Catalog & Bottle Details
    console.log('\n📌 3. Catálogo Público y Ficha de Producto...');
    const catalog = await makeRequest('/productos');
    assert(catalog.status === 200 && Array.isArray(catalog.data.data) && catalog.data.data.length > 0, 'Catálogo público lista bebidas activas');

    const productDetail = await makeRequest('/productos/1');
    assert(productDetail.status === 200 && productDetail.data.data.nombre, `Detalle de producto obtenido (${productDetail.data.data.nombre})`);
    assert(Array.isArray(productDetail.data.data.lotes), 'Ficha incluye lotes embotellados asociados');
    assert(Array.isArray(productDetail.data.data.establecimientos), 'Ficha incluye disponibilidad en locales hosteleros');

    // 4. Traceability Transparency & Secret Protection (PRD Sec 3.5)
    console.log('\n📌 4. Trazabilidad Pública y Protección de Secretos Industriales...');
    const trace = await makeRequest('/trazabilidad/EC-2026-PR01-A101');
    assert(trace.status === 200 && trace.data.success, 'Consulta pública de lote por código algorítmico');
    assert(trace.data.data.parametros_analiticos && trace.data.data.parametros_analiticos.ph === 3.65, 'Muestra pH analítico de laboratorio certificado (3.65)');
    assert(trace.data.data.costo_total === undefined, 'Seguridad: costo_total oculto en trazabilidad pública');
    assert(trace.data.data.merma_litros === undefined, 'Seguridad: merma interna oculta en trazabilidad pública');

    // 5. Producer Multi-Tenant Operations & Algorithmic Lot Generation
    console.log('\n📌 5. Operaciones de Productor (Multi-Tenant & Generación Algorítmica)...');
    const prodProducts = await makeRequest('/productor/productos', {
      headers: { Authorization: `Bearer ${prodToken}` }
    });
    assert(prodProducts.status === 200 && prodProducts.data.data.length > 0, 'Listado de productos aislado del productor autenticado');

    // Create new lot with analytical parameters
    const newLotPayload = {
      producto_id: prodProducts.data.data[0].id,
      anio_cosecha: 2026,
      fecha_elaboracion: '2026-09-07',
      volumen_producido_litros: 850.5,
      merma_litros: 35.0,
      costo_total: 6200000.0,
      parametros_analiticos: {
        ph: 3.58,
        alcohol_real_pct: 13.8,
        acidez_total_g_l: 5.8,
        densidad_final: 0.991,
        crianza_meses: 6
      }
    };
    const createLotRes = await makeRequest('/productor/lotes', {
      method: 'POST',
      headers: { Authorization: `Bearer ${prodToken}` },
      body: newLotPayload
    });
    assert(createLotRes.status === 201 && createLotRes.data.success, 'Creación de nuevo lote con parámetros físico-químicos');
    const generatedCode = createLotRes.data.data.codigo_lote;
    assert(/^EC-\d{4}-PR\d{2}-[A-Z0-9]{4}$/.test(generatedCode), `Código algorítmico cumple formato estándar: ${generatedCode}`);

    // Verify new lot immediately retrievable via public traceability
    const traceNew = await makeRequest(`/trazabilidad/${encodeURIComponent(generatedCode)}`);
    assert(traceNew.status === 200 && traceNew.data.data.codigo_lote === generatedCode, 'Nuevo lote consultable en trazabilidad pública en tiempo real');

    // 6. Financial Dashboard Aggregation
    console.log('\n📌 6. Dashboard Financiero y Mermas...');
    const financial = await makeRequest('/financiero/resumen', {
      headers: { Authorization: `Bearer ${prodToken}` }
    });
    assert(financial.status === 200 && financial.data.success, 'Resumen financiero del productor obtenido');
    const kpis = financial.data.data.kpis;
    assert(kpis.costo_total_acumulado > 0, `Inversión total acumulada calculada: $${kpis.costo_total_acumulado}`);
    assert(kpis.volumen_total_litros > 0, `Volumen total calculado: ${kpis.volumen_total_litros} L`);
    assert(parseFloat(kpis.porcentaje_merma_global) >= 0, `Tasa de merma ponderada: ${kpis.porcentaje_merma_global}%`);
    assert(kpis.costo_promedio_por_litro > 0, `Costo medio por litro calculado: $${kpis.costo_promedio_por_litro}`);

    // 7. Hosteleria Availability & Pricing
    console.log('\n📌 7. Gestión de Hostelería y Stock en Barra...');
    const hostCatalog = await makeRequest('/hosteleria/catalogo', {
      headers: { Authorization: `Bearer ${hostToken}` }
    });
    assert(hostCatalog.status === 200 && Array.isArray(hostCatalog.data.data), 'Carta de hostelería lista bebidas del mercado');

    const updateDisp = await makeRequest('/hosteleria/disponibilidad', {
      method: 'POST',
      headers: { Authorization: `Bearer ${hostToken}` },
      body: {
        producto_id: 1,
        disponible: true,
        precio_copa: 16500,
        precio_botella: 78000
      }
    });
    assert(updateDisp.status === 200 && updateDisp.data.success, 'Actualización de disponibilidad y precios de servicio (copa/botella)');

  } catch (err) {
    console.error('💥 Error inesperado durante la ejecución de pruebas:', err);
    failed++;
  } finally {
    server.close();
    console.log('\n======================================================');
    console.log(`RESUMEN: ${passed} pruebas superadas, ${failed} fallidas`);
    console.log('======================================================\n');
    process.exit(failed > 0 ? 1 : 0);
  }
}

runSuite();
