SET NAMES utf8mb4;
-- 1. INSERTAR SALAS SECRETAS
INSERT INTO sala_secreta (codigo_acceso, funcion_principal, salida_emergencia) VALUES
   ('OMG-99', 'Laboratorio de Mutaciones', true),
   ('DOOM-1', 'Sala de Control de Misiles', false),
   ('FISH-X', 'Tanque de Alimentación', true),
   ('GOLD-7', 'Cámara del Tesoro', false);

-- 2. INSERTAR COMODIDADES
INSERT INTO comodidad (nombre, auto_destruccion) VALUES
('Foso con Tiburones', false),
('Rayo Láser Orbital', true),
('Ejército de Minions', false),
('Wifi 6G Encriptado', false),
('Silla Giratoria Maligna', false);

-- 3. INSERTAR VILLANOS
INSERT INTO villano (nombre, alias, carne_villano, email) VALUES
('Jack Napier', 'The Joker', 'V-001', 'joker@arkham.net'),
('Victor Fries', 'Mr. Freeze', 'V-002', 'frio@hielo.com'),
('Heinz Doofenshmirtz', 'Dr. Doofenshmirtz', 'V-003', 'heinz@evilinc.com'),
('Gru', 'Mi Villano Favorito', 'V-004', 'gru@minions.com');

-- 4. INSERTAR GUARIDAS
INSERT INTO guarida (nombre, descripcion, ubicacion, precio_noche, imagen, sala_secreta_id) VALUES
('Volcán del Pacífico', 'Acogedor volcán activo. Ideal para planes que requieren magma.', 'Isla Calavera', 500.00, 'volcan.jpg', 1),
('Ático Torre Oscura', 'Vistas panorámicas a la ciudad que planeas destruir.', 'Gotham City', 1200.00, 'torre.jpg', 2),
('Submarino Nuclear', 'Totalmente indetectable. Incluye torpedos.', 'Fosa de las Marianas', 850.00, 'submarino.jpg', 3),
('Búnker del Desierto', 'A prueba de bombas y superhéroes.', 'Area 51', 300.00, 'bunker.jpg', 4);

-- 5. VINCULAR GUARIDAS CON COMODIDADES (Tabla N:M)
-- (Depende de: Guarida y Comodidad)
-- El Volcán (1) tiene Tiburones (1) y Minions (3)
INSERT INTO guarida_comodidades (guarida_id, comodidades_id) VALUES (1, 1);
INSERT INTO guarida_comodidades (guarida_id, comodidades_id) VALUES (1, 3);

-- La Torre (2) tiene Wifi (4), Silla (5) y Rayo Láser (2)
INSERT INTO guarida_comodidades (guarida_id, comodidades_id) VALUES (2, 4);
INSERT INTO guarida_comodidades (guarida_id, comodidades_id) VALUES (2, 5);
INSERT INTO guarida_comodidades (guarida_id, comodidades_id) VALUES (2, 2);

-- El Submarino (3) tiene Wifi (4)
INSERT INTO guarida_comodidades (guarida_id, comodidades_id) VALUES (3, 4);

-- 6. INSERTAR RESERVAS
INSERT INTO reserva (fecha_inicio, fecha_fin, coste_total, estado, villano_id, guarida_id) VALUES
('2025-12-01', '2025-12-05', 2000.00, true, 1, 1),   -- Joker en el Volcán (Confirmada)
('2025-11-20', '2025-11-22', 1700.00, false, 3, 3),  -- Doofenshmirtz en Submarino (Pendiente)
('2026-01-10', '2026-01-20', 3000.00, true, 4, 4);   -- Gru en el Búnker (Confirmada)

-- 7. INSERTAR FACTURAS
INSERT INTO factura (fecha_emision, importe, impuestos_malignos, metodo_pago, reserva_id) VALUES
('2025-11-30', 2000.00, 21.00, 'Lingotes de Oro', 1),
('2025-12-28', 3000.00, 50.00, 'Bitcoin', 3);

-- 8. INSERTAR RESEÑAS
INSERT INTO reseña (comentario, puntuacion, fecha_publicacion, villano_id, guarida_id) VALUES
('La lava estaba un poco fría, pero los esbirros muy atentos.', 4, '2025-12-06', 1, 1),
('Demasiada humedad para mis artilugios. Se me oxidó el Inator.', 2, '2025-11-23', 3, 3),
('Excelente para esconder la Luna.', 5, '2026-01-21', 4, 4);