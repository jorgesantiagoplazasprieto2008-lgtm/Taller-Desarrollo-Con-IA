# Entre Copas 🍷
### Plataforma de Gestión, Trazabilidad Analítica y Comercialización de Bebidas Artesanales

**Entre Copas** es una aplicación web full-stack modular desarrollada en **Java 17+ y Spring Boot 3.x con vistas JSP + JSTL**. Permite a los productores de vinos, cervezas artesanales e hidromiel centralizar el control de su producción, calcular mermas y costos, registrar parámetros físico-químicos en formato JSON y emitir certificados de trazabilidad algorítmica con sello de calidad; al tiempo que hostelería y comensales consultan stock en barra y el origen noble de cada botella.

---

## 🏛️ Arquitectura y Stack Tecnológico

- **Backend:** Java 17+ (LTS), Spring Boot 3.3.3, Spring Data JPA, Spring Security (RBAC con BCrypt).
- **Frontend / Vistas:** Spring MVC + JSP (JavaServer Pages) + JSTL, JavaScript Vanilla modular (Fetch API), Tokens CSS personalizados ([DESIGN.md](DESIGN.md)).
- **Base de Datos:** MySQL 8.x con soporte para columnas nativas `JSON` y versionado continuo con Flyway.
- **Calidad y Estilo:** Google Java Style Guide aplicado mediante Maven Checkstyle Plugin.
- **Empaquetado:** `.war` ejecutable autónomo con servidor Tomcat embebido y Jasper JSP compiler.

---

## 🚀 Instrucciones para Ejecutar la Aplicación

### Opción 1: Despliegue Inmediato con Docker Compose (Recomendado)
Levanta MySQL 8.0 y la aplicación Spring Boot en contenedores enlazados:
```bash
# 1. Clonar el repositorio y copiar el archivo de variables
cp .env.example .env

# 2. Iniciar servicios en segundo plano
docker-compose up -d --build

# 3. Acceder en el navegador
http://localhost:8080/catalogo
```

### Opción 2: Ejecución Local en Desarrollo (Maven Wrapper)
Requiere tener MySQL 8.x corriendo en el puerto 3306 (con base de datos `entrecopas_db`).
```powershell
# En Windows (PowerShell / CMD)
.\mvnw.cmd spring-boot:run

# En Linux / macOS
./mvnw spring-boot:run
```
Acceder en el navegador a: `http://localhost:8080/catalogo`

### Opción 3: Ejecución Directa del Artefacto Empaquetado
```bash
# Empaquetar y correr suite de pruebas
.\mvnw.cmd clean package -DskipTests=false

# Ejecutar el archivo WAR
java -jar target/entre-copas-app-0.0.1-SNAPSHOT.war
```

---

## 🗄️ Base de Datos y Migraciones Versionadas

El esquema relacional se gestiona automáticamente al arrancar la aplicación mediante **Flyway**:
1. `src/main/resources/db/migration/V1__init_schema.sql`: Tablas relacionales canónicas (`usuario`, `rol`, `usuario_rol`, `productor`, `producto`, `lote`, `establecimiento`, `disponibilidad`).
2. `src/main/resources/db/migration/V2__seed_demo_data.sql`: Datos semilla y usuarios demo precargados.

*(Opcional)* Si deseas inicializar manualmente la base de datos en MySQL Workbench, puedes ejecutar el script consolidado ubicado en:
`database/mysql_workbench_schema.sql`

---

## 🔑 Credenciales de Prueba y Demostración (1-Click)

En la pantalla de inicio de sesión (`/login`) se incluyen accesos rápidos de 1-clic:

| Rol | Correo Electrónico | Contraseña | Vistas y Capacidades |
| :--- | :--- | :--- | :--- |
| **Productor Artesanal** | `contacto@bodegasangabriel.com` | `Password123*` | `/panel/productor/productos`, `/panel/productor/lotes`, `/panel/financiero` |
| **Hostelería / Bar** | `gerencia@rincongourmet.com` | `Password123*` | `/panel/hosteleria/disponibilidad` (Switches de stock 44px) |
| **Administrador** | `admin@entrecopas.com` | `AdminPass123*` | Supervisión general de la plataforma |
| **Consumidor / Público** | Anónimo / Visitante | Sin credenciales | `/catalogo`, `/productos/{id}`, `/trazabilidad/{codigo}` |

---

## 🧪 Pruebas Automatizadas y Puertas de Calidad (Maker-Checker)

La suite cuenta con **34 pruebas automatizadas** que validan la lógica de negocio, los controladores REST y MVC, el aislamiento multi-tenant y el enmascaramiento de secretos industriales:

```powershell
# 1. Verificación de estilo Checkstyle (Google Java Style Guide)
.\mvnw.cmd checkstyle:check

# 2. Ejecución de suite de pruebas unitarias y de integración
.\mvnw.cmd test
```

Resultados: **34 de 34 pruebas superadas (0 fallos, 0 errores, 0 violaciones de Checkstyle)**.

---

## 📋 Hitos del MVP Implementados

- [x] **Hito 1 — Autenticación y RBAC:** Registro y control de acceso con 4 roles, password encoding BCrypt y perfiles vinculados.
- [x] **Hito 2 — Productores y Catálogo:** CRUD de bebidas artesanales con estados (`ACTIVO`, `PAUSADO`, `RETIRADO`), catálogo público y fichas técnicas.
- [x] **Hito 3 — Núcleo de Lotes y Trazabilidad:** Registro de lotes, parámetros analíticos (pH, alcohol, acidez, densidad en JSON), generador algorítmico `EC-YYYY-PRXX-XXXX`, y certificado oficial público con omisión estricta de costos y mermas.
- [x] **Hito 4 — Hostelería y Disponibilidad:** Tablero de disponibilidad para bares con interruptores táctiles de 44px, sincronización AJAX/Fetch y sección "Dónde Degustar" en la ficha del producto.
- [x] **Hito 5 — Dashboard Financiero Básico:** Cálculo agregado de costos totales, mermas ponderadas, costo promedio por litro, proyección de márgenes por formato (750ml, 330ml, 500ml) y auditoría privada por lote.
- [x] **Hito 6 — Validación y Empaquetado:** Orquestación con `docker-compose.yml`, `Dockerfile`, empaquetado `.war` y suite completa de pruebas.

---

## 🚢 ¿Qué falta para salir a Producción? (Roadmap Post-MVP)

Para la puesta en producción comercial y las siguientes fases:
1. **Infraestructura de Producción:**
   - Dominio personalizado y certificado SSL/TLS con Let's Encrypt / Certbot.
   - Nginx como proxy inverso para terminación SSL, compresión gzip y cache de estáticos.
2. **Pasarela de Pagos (Exclusivo Fase 2):**
   - Integración del carrito de compras y pasarela PayU / Wompi para pedidos B2B y directos al consumidor.
3. **Servicios de Envío y Mapas (Exclusivo Fase 3):**
   - Integración de geolocalización de tabernas y bodegas con Leaflet / OpenStreetMap.
   - Envío de notificaciones transaccionales vía correo (SendGrid / SES).
