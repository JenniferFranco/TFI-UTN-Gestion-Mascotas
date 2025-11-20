USE gestion_mascota;

-- 1. INSERTAR DUEÑOS

INSERT INTO duenios (dni, nombre, apellido, telefono, email, direccion, eliminado) VALUES 
('11111111', 'Juan', 'Perez', '1144556677', 'juan.perez@email.com', 'Av. Siempre Viva 742', FALSE),
('22222222', 'Maria', 'Gomez', '1155667788', 'maria.gomez@email.com', 'Calle Falsa 123', FALSE),
('33333333', 'Carlos', 'Lopez', '1166778899', 'carlos.lopez@email.com', 'San Martin 456', FALSE),
('44444444', 'Ana', 'Martinez', '1177889900', 'ana.martinez@email.com', NULL, FALSE); -- Dirección NULL es válida

-- 2. INSERTAR MASCOTAS
-- (Asociadas a los dueños por ID.)

INSERT INTO mascotas (nombre, especie, raza, fecha_nacimiento, duenio_id, eliminado) VALUES 
('Firulais', 'PERRO', 'Labrador', '2020-05-20', 1, FALSE), -- Mascota de Juan
('Mishi', 'GATO', 'Siames', '2019-10-15', 2, FALSE),      -- Mascota de Maria
('Rex', 'PERRO', 'Ovejero', '2021-01-01', 3, FALSE),      -- Mascota de Carlos
('Nemo', 'PEZ', 'Dorado', '2023-03-10', 1, FALSE),        -- Otra mascota de Juan (1-a-N)
('Luna', 'GATO', 'Callejero', '2022-07-07', 4, FALSE);    -- Mascota de Ana

-- 3. INSERTAR MICROCHIPS
-- (Relación 1-a-1. Cada mascota_id debe ser único aquí)

INSERT INTO microchips (codigo, observaciones, veterinaria, mascota_id, eliminado) VALUES 
('CHIP-001-ABC', 'Vacunas al día', 'Veterinaria Central', 1, FALSE),       -- Para Firulais
('CHIP-002-DEF', 'Alergico a la penicilina', 'Veterinaria Norte', 2, FALSE), -- Para Mishi
('CHIP-003-GHI', NULL, 'Hospital Veterinario', 3, FALSE),                  -- Para Rex
('CHIP-004-JKL', 'Pez de acuario grande', 'Mundo Marino', 4, FALSE),       -- Para Nemo
('CHIP-005-MNO', 'Rescatada', 'Refugio Patitas', 5, FALSE);                -- Para Luna

-- =============================================
-- VERIFICACIÓN

SELECT * FROM duenios;
SELECT * FROM mascotas;
SELECT * FROM microchips;

-- Ver la relación completa (Mascota + Dueño + Chip)
SELECT 
    m.nombre AS Mascota, 
    m.especie, 
    d.nombre AS Duenio, 
    mc.codigo AS Microchip
FROM mascotas m
JOIN duenios d ON m.duenio_id = d.id
JOIN microchips mc ON m.id = mc.mascota_id;
