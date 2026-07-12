CREATE DATABASE IF NOT EXISTS farmape_inventario
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE farmape_inventario;

CREATE TABLE IF NOT EXISTS categorias (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT DEFAULT NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id_categoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS productos (
  id_producto INT NOT NULL AUTO_INCREMENT,
  id_categoria INT NOT NULL,
  sku VARCHAR(50) DEFAULT NULL,
  nombre VARCHAR(150) NOT NULL,
  descripcion TEXT DEFAULT NULL,
  laboratorio VARCHAR(100) DEFAULT NULL,
  precio_compra DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  precio_venta DECIMAL(12,2) NOT NULL,
  stock_actual INT NOT NULL DEFAULT 0,
  stock_minimo INT NOT NULL DEFAULT 5,
  fecha_vencimiento DATE DEFAULT NULL,
  requiere_receta TINYINT(1) NOT NULL DEFAULT 0,
  estado ENUM('Activo','Inactivo') DEFAULT 'Activo',
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_producto),
  UNIQUE KEY uq_productos_sku (sku),
  KEY idx_productos_categoria (id_categoria),
  KEY idx_productos_estado_nombre (estado, nombre),
  KEY idx_productos_stock_minimo (stock_actual, stock_minimo),
  CONSTRAINT fk_productos_categoria
    FOREIGN KEY (id_categoria) REFERENCES categorias (id_categoria)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS lotes_producto (
  id_lote INT NOT NULL AUTO_INCREMENT,
  id_producto INT NOT NULL,
  numero_lote VARCHAR(80) NOT NULL,
  fecha_vencimiento DATE NOT NULL,
  costo_unitario DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  stock_disponible INT NOT NULL DEFAULT 0,
  estado VARCHAR(20) NOT NULL DEFAULT 'Disponible',
  fecha_ingreso DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_lote),
  UNIQUE KEY uq_lotes_producto_numero (id_producto, numero_lote),
  KEY idx_lotes_producto_fefo (id_producto, estado, fecha_vencimiento),
  CONSTRAINT fk_lotes_producto
    FOREIGN KEY (id_producto) REFERENCES productos (id_producto)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS movimientos_almacen (
  id_movimiento INT NOT NULL AUTO_INCREMENT,
  id_producto INT NOT NULL,
  id_lote INT DEFAULT NULL,
  id_trabajador INT NOT NULL,
  tipo_movimiento ENUM('Entrada','Salida','Ajuste') NOT NULL,
  motivo ENUM('Compra','Venta','Devolucion','Vencimiento','Ajuste') NOT NULL,
  cantidad INT NOT NULL,
  referencia_tipo VARCHAR(30) DEFAULT NULL,
  referencia_id INT DEFAULT NULL,
  fecha_movimiento DATETIME DEFAULT CURRENT_TIMESTAMP,
  observacion TEXT DEFAULT NULL,
  PRIMARY KEY (id_movimiento),
  KEY idx_movimientos_producto (id_producto),
  KEY idx_movimientos_lote (id_lote),
  KEY idx_movimientos_trabajador (id_trabajador),
  KEY idx_movimientos_fecha (fecha_movimiento),
  CONSTRAINT fk_movimientos_producto
    FOREIGN KEY (id_producto) REFERENCES productos (id_producto)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_movimientos_lote
    FOREIGN KEY (id_lote) REFERENCES lotes_producto (id_lote)
    ON UPDATE CASCADE
    ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
