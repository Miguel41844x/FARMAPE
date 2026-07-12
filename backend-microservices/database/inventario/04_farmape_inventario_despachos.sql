USE farmape_inventario;

CREATE TABLE IF NOT EXISTS despachos_operativos (
  id_despacho INT NOT NULL AUTO_INCREMENT,
  id_orden_venta INT NOT NULL,
  cliente VARCHAR(180) NOT NULL,
  fecha_orden DATETIME NOT NULL,
  total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  tipo_despacho ENUM('LOCAL','DOMICILIO') NOT NULL,
  direccion VARCHAR(180) DEFAULT NULL,
  repartidor VARCHAR(120) DEFAULT NULL,
  estado VARCHAR(20) NOT NULL,
  fecha_entrega DATETIME DEFAULT NULL,
  PRIMARY KEY (id_despacho),
  UNIQUE KEY uq_despachos_orden_tipo (id_orden_venta, tipo_despacho),
  KEY idx_despachos_tipo_estado (tipo_despacho, estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO despachos_operativos
  (id_despacho, id_orden_venta, cliente, fecha_orden, total, tipo_despacho, direccion, repartidor, estado, fecha_entrega)
VALUES
  (1, 101, 'Ana Torres', '2026-07-08 10:15:00', 84.50, 'LOCAL', NULL, NULL, 'PAGADA', NULL),
  (2, 102, 'Carlos Medina', '2026-07-08 11:40:00', 42.80, 'LOCAL', NULL, NULL, 'ENTREGADA', '2026-07-08 12:05:00'),
  (3, 103, 'Rosa Salazar', '2026-07-09 09:20:00', 126.30, 'LOCAL', NULL, NULL, 'PAGADA', NULL),
  (4, 104, 'Luis Perez', '2026-07-09 15:00:00', 33.20, 'LOCAL', NULL, NULL, 'ENTREGADA', '2026-07-09 15:30:00'),
  (5, 105, 'Maria Quispe', '2026-07-10 08:35:00', 61.90, 'LOCAL', NULL, NULL, 'PAGADA', NULL),
  (6, 201, 'Jorge Ramirez', '2026-07-08 13:10:00', 97.40, 'DOMICILIO', 'Av. Arequipa 1200, Lince', 'Pedro Diaz', 'PENDIENTE', NULL),
  (7, 202, 'Lucia Herrera', '2026-07-08 16:45:00', 58.70, 'DOMICILIO', 'Jr. Los Olivos 430, San Miguel', 'Carmen Ruiz', 'EN_RUTA', NULL),
  (8, 203, 'Miguel Castro', '2026-07-09 10:05:00', 143.00, 'DOMICILIO', 'Calle Las Flores 220, Surco', 'Pedro Diaz', 'ENTREGADO', '2026-07-09 11:10:00'),
  (9, 204, 'Patricia Nunez', '2026-07-10 12:25:00', 39.50, 'DOMICILIO', 'Av. Brasil 880, Pueblo Libre', NULL, 'PENDIENTE', NULL),
  (10, 205, 'Elena Vargas', '2026-07-10 17:30:00', 72.60, 'DOMICILIO', 'Av. Colonial 1440, Callao', 'Carmen Ruiz', 'EN_RUTA', NULL);
