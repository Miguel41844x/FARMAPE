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

-- Trabajador administrador
INSERT INTO trabajadores (id_rol, dni, nombres, apellidos, estado, fecha_registro)
SELECT r.id_rol, '00000000', 'Administrador', 'FARMAPE', 'Activo', NOW()
FROM roles r WHERE r.codigo = 'ADMIN'
ON CONFLICT (dni) DO NOTHING;

-- Cuenta administrador — clave: Admin123456!
INSERT INTO cuentas_usuario (id_trabajador, usuario, email, clave, estado, fecha_creacion)
SELECT t.id_trabajador,
       'admin',
       'admin@farmape.com',
       '$2a$10$Mlt/BgVz4LrznrosEdMAeOfHS99jAw/yDi.adjzC0yJe1nVwfBXXG',
       'Activo',
       NOW()
FROM trabajadores t WHERE t.dni = '00000000'
ON CONFLICT (usuario) DO NOTHING;