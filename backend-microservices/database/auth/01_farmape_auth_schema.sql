-- FARMAPE Auth Service - Schema PostgreSQL
-- Base de datos: farmape_auth

CREATE TABLE IF NOT EXISTS roles (
    id_rol      SERIAL PRIMARY KEY,
    nombre_rol  VARCHAR(50)  NOT NULL UNIQUE,
    codigo      VARCHAR(50)  NOT NULL UNIQUE,
    descripcion TEXT,
    activo      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS permisos (
    id_permiso  SERIAL PRIMARY KEY,
    codigo      VARCHAR(80)  NOT NULL UNIQUE,
    nombre      VARCHAR(100) NOT NULL,
    modulo      VARCHAR(50)  NOT NULL,
    activo      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS rol_permisos (
    id_rol      INTEGER NOT NULL REFERENCES roles(id_rol)     ON DELETE CASCADE ON UPDATE CASCADE,
    id_permiso  INTEGER NOT NULL REFERENCES permisos(id_permiso) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (id_rol, id_permiso)
);

CREATE TABLE IF NOT EXISTS trabajadores (
    id_trabajador   SERIAL PRIMARY KEY,
    id_rol          INTEGER      NOT NULL REFERENCES roles(id_rol) ON DELETE RESTRICT ON UPDATE CASCADE,
    dni             VARCHAR(20)  NOT NULL UNIQUE,
    nombres         VARCHAR(100) NOT NULL,
    apellidos       VARCHAR(100) NOT NULL,
    telefono        VARCHAR(20),
    direccion       VARCHAR(150),
    estado          VARCHAR(20)  NOT NULL DEFAULT 'Activo',
    fecha_registro  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS cuentas_usuario (
    id_cuenta       SERIAL PRIMARY KEY,
    id_trabajador   INTEGER      NOT NULL UNIQUE REFERENCES trabajadores(id_trabajador) ON DELETE RESTRICT ON UPDATE CASCADE,
    usuario         VARCHAR(50)  NOT NULL UNIQUE,
    email           VARCHAR(100) NOT NULL UNIQUE,
    clave           VARCHAR(255) NOT NULL,
    estado          VARCHAR(20)  NOT NULL DEFAULT 'Activo',
    ultimo_acceso   TIMESTAMP,
    fecha_creacion  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS solicitudes_restablecimiento_clave (
    id_solicitud        BIGSERIAL PRIMARY KEY,
    usuario_o_correo    VARCHAR(100) NOT NULL,
    id_cuenta           INTEGER      REFERENCES cuentas_usuario(id_cuenta) ON DELETE SET NULL,
    mensaje             VARCHAR(300),
    estado              VARCHAR(30)  NOT NULL DEFAULT 'Pendiente',
    fecha_solicitud     TIMESTAMP    NOT NULL DEFAULT NOW()
);

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO farmape;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO farmape;