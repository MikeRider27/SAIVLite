# SAIV Lite 2.0

Sistema de punto de venta e inventario para comercios pequeños: caja, ventas, devoluciones, stock, reportes y usuarios con permisos.

Es la reescritura web del SAIV Lite original, una aplicación de escritorio Java Swing que se conserva en [`legacy/`](legacy/).

| Capa | Tecnología |
|---|---|
| Backend | Java 21 · Spring Boot 3.3 · Spring Security (JWT) · JPA · Flyway |
| Frontend | Angular 18 (standalone + signals) · nginx |
| Base de datos | PostgreSQL 16 interno, sin puerto expuesto al host |
| Despliegue | Docker Compose |

## Puesta en marcha

```bash
cp .env.example .env      # ajustar claves y puerto
docker compose up -d --build
```

Abrir `http://localhost:8080`, o el puerto definido en `APP_PORT`.

**Usuario inicial:** `admin` / `admin123`. Se crea en el primer arranque si la base está vacía, con `ADMIN_USERNAME` y `ADMIN_PASSWORD`. Cambiá la contraseña desde *Configuración → Mi contraseña*.

> En producción definí `DB_PASSWORD` y `JWT_SECRET` en `.env`. Para generar el secreto: `openssl rand -base64 48`.

## Funcionalidades

- **Vender**: búsqueda por código de barras o nombre. Un multiplicador como `3*7790001` agrega tres unidades. El carrito sobrevive a recargas de la página. Acepta efectivo, tarjeta o transferencia; en efectivo calcula el vuelto con montos rápidos. Imprime el ticket de 80 mm. Atajos: `F2` cobrar, `F3` buscar.
- **Caja**: cada cajero abre su turno con un monto inicial. Al cerrar, el sistema calcula el monto esperado (inicial + ventas − devoluciones) y muestra la diferencia con lo contado. Hay historial de cierres.
- **Ventas**: historial por fechas, detalle, reimpresión y devoluciones parciales con motivo. Las devoluciones reponen el stock.
- **Productos**: venta por unidad o a granel (con decimales), stock mínimo, ubicación y productos que no controlan inventario. Incluye categorías, ajustes de stock (entrada, salida o conteo), kardex de movimientos e importación/exportación CSV.
- **Reportes**: ventas netas, tickets, ticket promedio, ganancia, ventas por día, más vendidos, ventas por cajero, categoría y método de pago, e inventario valorizado.
- **Usuarios**: rol Administrador (acceso total) o Cajero con permisos granulares: vender, devoluciones, productos, categorías, ajustar stock, reportes, usuarios y configuración.
- **Configuración**: datos del negocio y del ticket (RUC, dirección, mensajes, moneda y decimales).

## Respaldos

```bash
scripts/backup.sh                                 # crea backups/saiv-AAAAMMDD-HHMMSS.sql.gz
scripts/restore.sh backups/saiv-XXXX.sql.gz       # reemplaza todos los datos
```

## Desarrollo local

```bash
# Base de datos
docker run -d --name saiv-db -e POSTGRES_DB=saiv -e POSTGRES_USER=saiv -e POSTGRES_PASSWORD=saiv -p 5432:5432 postgres:16-alpine

# Backend (Java 21 + Maven) → http://localhost:8080
cd backend && mvn spring-boot:run

# Frontend (Node 20) → http://localhost:4200, con proxy de /api al backend
cd frontend && npm install && npm start
```

## Estructura

```
backend/    API REST (com.saiv.*: usuario, producto, caja, venta, reporte, negocio, security)
  src/main/resources/db/migration/   migraciones Flyway
frontend/   SPA Angular (core/, layout/, pages/, shared/)
scripts/    respaldo y restauración
legacy/     sistema Swing original (solo referencia)
```

## API

Todas las rutas están bajo `/api` y requieren `Authorization: Bearer <token>`, salvo `POST /api/auth/login`.

| Recurso | Endpoints |
|---|---|
| Auth | `POST /auth/login`, `GET /auth/me`, `POST /auth/cambiar-password` |
| Productos | `GET/POST /productos`, `GET/PUT/DELETE /productos/{id}`, `GET /productos/codigo/{codigo}`, `POST /productos/{id}/ajustes`, `GET /productos/{id}/movimientos`, `GET /productos/exportar`, `POST /productos/importar` |
| Categorías | `GET/POST /categorias`, `PUT/DELETE /categorias/{id}` |
| Caja | `GET /caja/actual`, `POST /caja/abrir`, `POST /caja/cerrar`, `GET /caja/sesiones` |
| Ventas | `POST /ventas`, `GET /ventas`, `GET /ventas/{id}`, `POST /ventas/{id}/devoluciones`, `GET /devoluciones` |
| Reportes | `GET /reportes/resumen?desde&hasta`, `GET /reportes/inventario` |
| Usuarios | `GET/POST /usuarios`, `PUT/DELETE /usuarios/{id}`, `POST /usuarios/{id}/reset-password` |
| Negocio | `GET/PUT /negocio` |
