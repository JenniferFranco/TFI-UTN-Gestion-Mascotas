-- Creamos la base de datos si no existe
CREATE DATABASE IF NOT EXISTS gestion_mascota;
USE gestion_mascota;

-- BLOQUE DE IDEMPOTENCIA
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS mascota;
DROP TABLE IF EXISTS duenio;
DROP TABLE IF EXISTS microchip;
SET FOREIGN_KEY_CHECKS = 1;

-- DUENIOS (Tabla independiente)
CREATE TABLE duenios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dni VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    telefono VARCHAR(30),
    email VARCHAR(120) UNIQUE,
    direccion VARCHAR (50), 
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    
    CONSTRAINT chk_email CHECK (email LIKE '%@%'),
    CONSTRAINT chk_telefono CHECK (LENGTH(telefono) >= 7) 
);

-- MASCOTA (Depende de Duenio)
-- Esta es 'A' en la relación A->B
CREATE TABLE mascotas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nombre VARCHAR(60)  NOT NULL,
    especie VARCHAR(30) NOT NULL,
    raza VARCHAR(60),
    fecha_nacimiento DATE,
    duenio_id BIGINT NOT NULL,
    
    -- RELACIONES 
    CONSTRAINT fk_mascota_duenio
        FOREIGN KEY (duenio_id) REFERENCES duenios(id),
        
    -- RESTRICCIONES
    CONSTRAINT chk_mascota_nombre CHECK (TRIM(nombre) <> '')
);

-- MICROCHIP (Depende de Mascota)
-- Esta es 'B' en la relación A->B
CREATE TABLE microchips (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    codigo VARCHAR(25) NOT NULL UNIQUE, 
    observaciones TEXT,
    veterinaria VARCHAR(120),
    mascota_id BIGINT NOT NULL UNIQUE, 

    -- RELACIONES
    CONSTRAINT fk_microchip_mascota
        FOREIGN KEY (mascota_id) REFERENCES mascotas(id),

    -- RESTRICCIONES
    CONSTRAINT chk_microchip_codigo CHECK (TRIM(codigo) <> '')
);