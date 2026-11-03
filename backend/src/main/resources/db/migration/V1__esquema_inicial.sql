-- SAIV Lite 2.0 - esquema inicial

CREATE TABLE usuarios (
    id            BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(120) NOT NULL,
    username      VARCHAR(60)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    rol           VARCHAR(20)  NOT NULL DEFAULT 'CAJERO',
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en     TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE usuario_permisos (
    usuario_id BIGINT      NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    permiso    VARCHAR(40) NOT NULL,
    PRIMARY KEY (usuario_id, permiso)
);

CREATE TABLE categorias (
    id     BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE productos (
    id                  BIGSERIAL PRIMARY KEY,
    codigo              VARCHAR(60)    NOT NULL UNIQUE,
    descripcion         VARCHAR(200)   NOT NULL,
    categoria_id        BIGINT         REFERENCES categorias(id) ON DELETE SET NULL,
    precio_compra       NUMERIC(14, 2) NOT NULL DEFAULT 0,
    precio_venta        NUMERIC(14, 2) NOT NULL DEFAULT 0,
    existencias         NUMERIC(14, 3) NOT NULL DEFAULT 0,
    stock_minimo        NUMERIC(14, 3) NOT NULL DEFAULT 0,
    unidad              VARCHAR(20)    NOT NULL DEFAULT 'UNIDAD',
    ubicacion           VARCHAR(80),
    controla_inventario BOOLEAN        NOT NULL DEFAULT TRUE,
    activo              BOOLEAN        NOT NULL DEFAULT TRUE,
    actualizado_en      TIMESTAMP      NOT NULL DEFAULT now()
);
CREATE INDEX idx_productos_descripcion ON productos (lower(descripcion));

CREATE TABLE movimientos_stock (
    id          BIGSERIAL PRIMARY KEY,
    producto_id BIGINT         NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
    tipo        VARCHAR(20)    NOT NULL,
    cantidad    NUMERIC(14, 3) NOT NULL,
    saldo       NUMERIC(14, 3) NOT NULL,
    motivo      VARCHAR(200),
    usuario_id  BIGINT         REFERENCES usuarios(id),
    fecha_hora  TIMESTAMP      NOT NULL DEFAULT now()
);
CREATE INDEX idx_mov_producto ON movimientos_stock (producto_id, fecha_hora DESC);

CREATE TABLE sesiones_caja (
    id               BIGSERIAL PRIMARY KEY,
    usuario_id       BIGINT         NOT NULL REFERENCES usuarios(id),
    apertura         TIMESTAMP      NOT NULL DEFAULT now(),
    monto_inicial    NUMERIC(14, 2) NOT NULL DEFAULT 0,
    cierre           TIMESTAMP,
    monto_esperado   NUMERIC(14, 2),
    monto_declarado  NUMERIC(14, 2),
    observacion      VARCHAR(300),
    estado           VARCHAR(20)    NOT NULL DEFAULT 'ABIERTA'
);
CREATE UNIQUE INDEX uq_caja_abierta_por_usuario ON sesiones_caja (usuario_id) WHERE estado = 'ABIERTA';

CREATE TABLE ventas (
    id             BIGSERIAL PRIMARY KEY,
    fecha_hora     TIMESTAMP      NOT NULL DEFAULT now(),
    usuario_id     BIGINT         NOT NULL REFERENCES usuarios(id),
    sesion_caja_id BIGINT         REFERENCES sesiones_caja(id),
    metodo_pago    VARCHAR(20)    NOT NULL DEFAULT 'EFECTIVO',
    total          NUMERIC(14, 2) NOT NULL,
    pagado         NUMERIC(14, 2) NOT NULL,
    cambio         NUMERIC(14, 2) NOT NULL
);
CREATE INDEX idx_ventas_fecha ON ventas (fecha_hora);

CREATE TABLE venta_items (
    id                BIGSERIAL PRIMARY KEY,
    venta_id          BIGINT         NOT NULL REFERENCES ventas(id) ON DELETE CASCADE,
    producto_id       BIGINT         REFERENCES productos(id) ON DELETE SET NULL,
    codigo            VARCHAR(60)    NOT NULL,
    descripcion       VARCHAR(200)   NOT NULL,
    categoria         VARCHAR(80),
    cantidad          NUMERIC(14, 3) NOT NULL,
    cantidad_devuelta NUMERIC(14, 3) NOT NULL DEFAULT 0,
    precio_unitario   NUMERIC(14, 2) NOT NULL,
    costo_unitario    NUMERIC(14, 2) NOT NULL,
    importe           NUMERIC(14, 2) NOT NULL
);
CREATE INDEX idx_items_venta ON venta_items (venta_id);

CREATE TABLE devoluciones (
    id             BIGSERIAL PRIMARY KEY,
    venta_item_id  BIGINT         NOT NULL REFERENCES venta_items(id) ON DELETE CASCADE,
    cantidad       NUMERIC(14, 3) NOT NULL,
    importe        NUMERIC(14, 2) NOT NULL,
    motivo         VARCHAR(300)   NOT NULL,
    usuario_id     BIGINT         NOT NULL REFERENCES usuarios(id),
    sesion_caja_id BIGINT         REFERENCES sesiones_caja(id),
    fecha_hora     TIMESTAMP      NOT NULL DEFAULT now()
);
CREATE INDEX idx_devoluciones_fecha ON devoluciones (fecha_hora);

CREATE TABLE negocio (
    id              SMALLINT PRIMARY KEY DEFAULT 1 CHECK (id = 1),
    nombre          VARCHAR(120) NOT NULL DEFAULT 'Mi Negocio',
    direccion       VARCHAR(200),
    telefono        VARCHAR(60),
    ruc             VARCHAR(40),
    moneda          VARCHAR(10)  NOT NULL DEFAULT 'Gs.',
    decimales       SMALLINT     NOT NULL DEFAULT 0,
    linea_extra     VARCHAR(200),
    mensaje_gracias VARCHAR(200) DEFAULT '¡Gracias por su compra!',
    mensaje_final   VARCHAR(200)
);
INSERT INTO negocio (id) VALUES (1);

INSERT INTO categorias (nombre) VALUES ('General');
