-- FARMAPE Auth Service - Datos base

-- Roles base
INSERT INTO roles (nombre_rol, codigo, descripcion, activo) VALUES
('Administrador',         'ADMIN',      'Acceso total al sistema',                       TRUE),
('Empleado',              'EMPLOYEE',   'Acceso basico de ventas',                       TRUE),
('Cajero',                'CASHIER',    'Gestion de caja y pagos',                       TRUE),
('Encargado de Despacho', 'DISPATCH',   'Gestion de despacho',                           TRUE),
('Encargado de Almacen',  'WAREHOUSE',  'Gestion de almacen e inventario',               TRUE),
('Quimico Farmaceutico',  'PHARMACIST', 'Gestion de recetas magistrales',                TRUE),
('Gerente',               'MANAGER',    'Acceso a reportes y auditoria',                 TRUE)
ON CONFLICT (codigo) DO NOTHING;

-- Permisos
INSERT INTO permisos (codigo, nombre, modulo, activo) VALUES
('USER_MANAGE',      'Gestionar usuarios',          'Usuarios',   TRUE),
('ROLE_READ',        'Ver roles',                   'Roles',      TRUE),
('ROLE_MANAGE',      'Gestionar roles',             'Roles',      TRUE),
('ROLE_ASSIGN',      'Asignar permisos a roles',    'Roles',      TRUE),
('PRODUCT_READ',     'Ver productos',               'Productos',  TRUE),
('PRODUCT_MANAGE',   'Gestionar productos',         'Productos',  TRUE),
('CUSTOMER_MANAGE',  'Gestionar clientes',          'Clientes',   TRUE),
('SALE_READ',        'Ver ventas',                  'Ventas',     TRUE),
('SALE_CREATE',      'Crear ventas',                'Ventas',     TRUE),
('SALE_CONFIRM',     'Confirmar ventas',            'Ventas',     TRUE),
('SALE_CANCEL',      'Anular ventas',               'Ventas',     TRUE),
('PAYMENT_READ',     'Ver pagos',                   'Caja',       TRUE),
('PAYMENT_CREATE',   'Registrar pagos',             'Caja',       TRUE),
('PURCHASE_MANAGE',  'Gestionar compras',           'Compras',    TRUE),
('INVENTORY_MANAGE', 'Gestionar inventario',        'Almacen',    TRUE),
('DISPATCH_MANAGE',  'Gestionar despacho',          'Despacho',   TRUE),
('FORMULA_MANAGE',   'Gestionar recetas',           'Formulas',   TRUE),
('REPORT_VIEW',      'Ver reportes',                'Reportes',   TRUE),
('REPORT_MANAGE',    'Gestionar reportes',          'Reportes',   TRUE),
('AUDIT_VIEW',       'Ver auditoria',               'Auditoria',  TRUE),
('AUDIT_MANAGE',     'Gestionar auditoria',         'Auditoria',  TRUE)
ON CONFLICT (codigo) DO NOTHING;

-- ADMIN: todos los permisos
INSERT INTO rol_permisos (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM roles r, permisos p
WHERE r.codigo = 'ADMIN'
ON CONFLICT DO NOTHING;

-- CASHIER
INSERT INTO rol_permisos (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso FROM roles r, permisos p
WHERE r.codigo = 'CASHIER'
  AND p.codigo IN ('PAYMENT_READ', 'PAYMENT_CREATE', 'SALE_READ')
ON CONFLICT DO NOTHING;

-- EMPLOYEE
INSERT INTO rol_permisos (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso FROM roles r, permisos p
WHERE r.codigo = 'EMPLOYEE'
  AND p.codigo IN ('SALE_CREATE', 'SALE_CONFIRM', 'SALE_CANCEL', 'PRODUCT_READ', 'CUSTOMER_MANAGE')
ON CONFLICT DO NOTHING;

-- DISPATCH
INSERT INTO rol_permisos (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso FROM roles r, permisos p
WHERE r.codigo = 'DISPATCH'
  AND p.codigo IN ('DISPATCH_MANAGE', 'INVENTORY_MANAGE')
ON CONFLICT DO NOTHING;

-- WAREHOUSE
INSERT INTO rol_permisos (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso FROM roles r, permisos p
WHERE r.codigo = 'WAREHOUSE'
  AND p.codigo IN ('INVENTORY_MANAGE', 'PRODUCT_READ')
ON CONFLICT DO NOTHING;

-- PHARMACIST
INSERT INTO rol_permisos (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso FROM roles r, permisos p
WHERE r.codigo = 'PHARMACIST'
  AND p.codigo IN ('FORMULA_MANAGE', 'PRODUCT_READ', 'CUSTOMER_MANAGE')
ON CONFLICT DO NOTHING;

-- MANAGER
INSERT INTO rol_permisos (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso FROM roles r, permisos p
WHERE r.codigo = 'MANAGER'
  AND p.codigo IN ('REPORT_VIEW', 'REPORT_MANAGE', 'AUDIT_VIEW', 'SALE_READ')
ON CONFLICT DO NOTHING;

--Trabajadores 
INSERT INTO trabajadores (id_rol, dni, nombres, apellidos, estado, fecha_registro)
SELECT r.id_rol, '00000000', 'Carlos', 'Ramirez', 'Activo', NOW()
FROM roles r WHERE r.codigo = 'ADMIN'
ON CONFLICT (dni) DO NOTHING;

INSERT INTO trabajadores (id_rol, dni, nombres, apellidos, estado, fecha_registro)
SELECT r.id_rol, '11111111', 'Maria', 'Lopez', 'Activo', NOW()
FROM roles r WHERE r.codigo = 'EMPLOYEE'
ON CONFLICT (dni) DO NOTHING;

INSERT INTO trabajadores (id_rol, dni, nombres, apellidos, estado, fecha_registro)
SELECT r.id_rol, '22222222', 'Juan', 'Torres', 'Activo', NOW()
FROM roles r WHERE r.codigo = 'CASHIER'
ON CONFLICT (dni) DO NOTHING;

INSERT INTO trabajadores (id_rol, dni, nombres, apellidos, estado, fecha_registro)
SELECT r.id_rol, '33333333', 'Ana', 'Flores', 'Activo', NOW()
FROM roles r WHERE r.codigo = 'DISPATCH'
ON CONFLICT (dni) DO NOTHING;

INSERT INTO trabajadores (id_rol, dni, nombres, apellidos, estado, fecha_registro)
SELECT r.id_rol, '44444444', 'Luis', 'Mendoza', 'Activo', NOW()
FROM roles r WHERE r.codigo = 'WAREHOUSE'
ON CONFLICT (dni) DO NOTHING;

INSERT INTO trabajadores (id_rol, dni, nombres, apellidos, estado, fecha_registro)
SELECT r.id_rol, '55555555', 'Rosa', 'Vargas', 'Activo', NOW()
FROM roles r WHERE r.codigo = 'PHARMACIST'
ON CONFLICT (dni) DO NOTHING;

INSERT INTO trabajadores (id_rol, dni, nombres, apellidos, estado, fecha_registro)
SELECT r.id_rol, '66666666', 'Pedro', 'Castillo', 'Activo', NOW()
FROM roles r WHERE r.codigo = 'MANAGER'
ON CONFLICT (dni) DO NOTHING;

-- Cuentas de usuario 
-- Todas usan la clave: 123456
INSERT INTO cuentas_usuario (id_trabajador, usuario, email, clave, estado, fecha_creacion)
SELECT t.id_trabajador, 'admin', 'admin@farmape.com',
       '$2a$10$NlUGFJdgRIFr1k8Op/BJael5M6k7RYmWXu/a.e0J1PugvhQ.5xKKm',
       'Activo', NOW()
FROM trabajadores t WHERE t.dni = '00000000'
ON CONFLICT (usuario) DO NOTHING;

INSERT INTO cuentas_usuario (id_trabajador, usuario, email, clave, estado, fecha_creacion)
SELECT t.id_trabajador, 'empleado01', 'empleado01@farmape.com',
       '$2a$10$NlUGFJdgRIFr1k8Op/BJael5M6k7RYmWXu/a.e0J1PugvhQ.5xKKm',
       'Activo', NOW()
FROM trabajadores t WHERE t.dni = '11111111'
ON CONFLICT (usuario) DO NOTHING;

INSERT INTO cuentas_usuario (id_trabajador, usuario, email, clave, estado, fecha_creacion)
SELECT t.id_trabajador, 'cajero01', 'cajero01@farmape.com',
       '$2a$10$NlUGFJdgRIFr1k8Op/BJael5M6k7RYmWXu/a.e0J1PugvhQ.5xKKm',
       'Activo', NOW()
FROM trabajadores t WHERE t.dni = '22222222'
ON CONFLICT (usuario) DO NOTHING;

INSERT INTO cuentas_usuario (id_trabajador, usuario, email, clave, estado, fecha_creacion)
SELECT t.id_trabajador, 'despacho01', 'despacho01@farmape.com',
       '$2a$10$NlUGFJdgRIFr1k8Op/BJael5M6k7RYmWXu/a.e0J1PugvhQ.5xKKm',
       'Activo', NOW()
FROM trabajadores t WHERE t.dni = '33333333'
ON CONFLICT (usuario) DO NOTHING;

INSERT INTO cuentas_usuario (id_trabajador, usuario, email, clave, estado, fecha_creacion)
SELECT t.id_trabajador, 'almacen01', 'almacen01@farmape.com',
       '$2a$10$NlUGFJdgRIFr1k8Op/BJael5M6k7RYmWXu/a.e0J1PugvhQ.5xKKm',
       'Activo', NOW()
FROM trabajadores t WHERE t.dni = '44444444'
ON CONFLICT (usuario) DO NOTHING;

INSERT INTO cuentas_usuario (id_trabajador, usuario, email, clave, estado, fecha_creacion)
SELECT t.id_trabajador, 'quimico01', 'quimico01@farmape.com',
       '$2a$10$NlUGFJdgRIFr1k8Op/BJael5M6k7RYmWXu/a.e0J1PugvhQ.5xKKm',
       'Activo', NOW()
FROM trabajadores t WHERE t.dni = '55555555'
ON CONFLICT (usuario) DO NOTHING;

INSERT INTO cuentas_usuario (id_trabajador, usuario, email, clave, estado, fecha_creacion)
SELECT t.id_trabajador, 'gerente01', 'gerente01@farmape.com',
       '$2a$10$NlUGFJdgRIFr1k8Op/BJael5M6k7RYmWXu/a.e0J1PugvhQ.5xKKm',
       'Activo', NOW()
FROM trabajadores t WHERE t.dni = '66666666'
ON CONFLICT (usuario) DO NOTHING;