USE farmape_inventario;

CREATE TABLE IF NOT EXISTS verificaciones_almacen (
  id_verificacion INT NOT NULL AUTO_INCREMENT,
  id_pedido_compra INT DEFAULT NULL,
  id_producto INT NOT NULL,
  cantidad_pedida INT NOT NULL,
  cantidad_recibida INT NOT NULL,
  estado ENUM('CONFORME','OBSERVADO') NOT NULL DEFAULT 'CONFORME',
  observacion TEXT DEFAULT NULL,
  fecha_verificacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_verificacion),
  KEY idx_verificaciones_producto (id_producto),
  KEY idx_verificaciones_estado (estado),
  CONSTRAINT fk_verificaciones_producto
    FOREIGN KEY (id_producto) REFERENCES productos (id_producto)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO verificaciones_almacen
  (id_verificacion, id_pedido_compra, id_producto, cantidad_pedida, cantidad_recibida, estado, observacion, fecha_verificacion)
VALUES
  (1, 10, 1, 50, 50, 'CONFORME', 'Recepcion conforme', '2026-02-13 10:00:00'),
  (2, 10, 12, 48, 45, 'OBSERVADO', 'Diferencia en cantidad recibida', '2026-02-13 10:05:00'),
  (3, 20, 22, 35, 35, 'CONFORME', 'Recepcion conforme', '2026-03-24 09:30:00'),
  (4, 20, 43, 25, 22, 'OBSERVADO', 'Faltan unidades por entregar', '2026-03-24 09:35:00'),
  (5, 30, 77, 28, 28, 'CONFORME', 'Recepcion conforme', '2026-05-04 11:20:00'),
  (6, 30, 78, 33, 30, 'OBSERVADO', 'Producto recibido incompleto', '2026-05-04 11:25:00');
