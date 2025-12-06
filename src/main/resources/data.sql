SET NAMES utf8mb4;
-- 1. SALAS SECRETAS
INSERT INTO sala_secreta (codigo_acceso, funcion_principal, salida_emergencia) VALUES
   ('OMG-99', 'Laboratorio de Mutaciones', true),       -- ID 1 (Volcán)
   ('DOOM-1', 'Sala de Control de Misiles', false),     -- ID 2 (Torre)
   ('FISH-X', 'Tanque de Alimentación', true),          -- ID 3 (Submarino)
   ('GOLD-7', 'Cámara del Tesoro', false),              -- ID 4 (Búnker)
   ('LION-K', 'Cueva de los Huesos', true),             -- ID 5 (Scar)
   ('VOODOO', 'Sala de los Espíritus', false),          -- ID 6 (Facilier)
   ('SITH-66', 'Cámara de Meditación Hiperbárica', true), -- ID 7 (Vader)
   ('HADES-0', 'Piscina de Almas Perdidas', false);     -- ID 8 (Hades)

-- 2. COMODIDADES
INSERT INTO comodidad (nombre, auto_destruccion) VALUES
('Foso con Tiburones', false),          -- ID 1
('Rayo Láser Orbital', true),           -- ID 2
('Ejército de Minions', false),         -- ID 3
('Wifi 6G Encriptado', false),          -- ID 4
('Silla Giratoria Maligna', false),     -- ID 5
('Hienas Hambrientas', false),          -- ID 6
('Sombras Vivientes', true),            -- ID 7
('Sistema de Soporte Vital', false),    -- ID 8
('Fuego Azul Decorativo', false),       -- ID 9
('Abrigos de Piel de Dálmata', false);  -- ID 10

-- 3. VILLANOS (Corregidos con el patrón: 3num + 1letra + 6num)
INSERT INTO villano (nombre, alias, carne_villano, email) VALUES
-- Clásicos (Ids corregidos)
('Jack Napier', 'The Joker', '666J000001', 'joker@arkham.net'),                 -- ID 1
('Victor Fries', 'Mr. Freeze', '000F000002', 'frio@hielo.com'),                 -- ID 2
('Heinz Doofenshmirtz', 'Dr. Doofenshmirtz', '999D000003', 'heinz@evilinc.com'),-- ID 3
('Gru', 'Mi Villano Favorito', '001G000004', 'gru@minions.com'),                -- ID 4

-- Nuevos Fichajes
('Scar', 'El Verdadero Rey', '100S123123', 'scar@pridelands.com'),              -- ID 5
('Dr. Facilier', 'El Hombre Sombra', '666F654321', 'facilier@friendsontheotherside.com'), -- ID 6
('Anakin Skywalker', 'Darth Vader', '501V501501', 'vader@empire.gov'),          -- ID 7
('Hades', 'Dios del Inframundo', '666H666666', 'hades@olympus.rejects.com'),    -- ID 8
('Cruella de Vil', 'Reina de la Moda', '101C101101', 'cruella@hellhall.fashion'); -- ID 9

-- 4. GUARIDAS
INSERT INTO guarida (nombre, descripcion, ubicacion, precio_noche, imagen, sala_secreta_id) VALUES
('Volcán del Pacífico', 'Acogedor volcán activo. Ideal para planes que requieren magma.', 'Isla Calavera', 500.00, 'volcan.jpg', 1),
('Ático Torre Oscura', 'Vistas panorámicas a la ciudad que planeas destruir.', 'Gotham City', 1200.00, 'torre.jpg', 2),
('Submarino Nuclear', 'Totalmente indetectable. Incluye torpedos.', 'Fosa de las Marianas', 850.00, 'submarino.jpg', 3),
('Búnker del Desierto', 'A prueba de bombas y superhéroes.', 'Area 51', 300.00, 'bunker.jpg', 4),

-- Nuevas Guaridas
('Cementerio de Elefantes', 'Ambiente tétrico con mucha neblina verde. Cuidado con las hienas.', 'Sabana Africana', 150.00, 'cementerio.jpg', 5),
('Emporio de Vudú', 'Local en Nueva Orleans con acceso directo al "Otro Lado".', 'Nueva Orleans', 400.00, 'vudu.jpg', 6),
('Estrella de la Muerte (En obras)', 'Estación espacial completa. Ojo con el conducto de ventilación.', 'Espacio Exterior', 5000.00, 'deathstar.jpg', 7),
('El Inframundo', 'Hace un poco de calor, pero es genial para reuniones familiares eternas.', 'Grecia Antigua', 666.00, 'inframundo.jpg', 8);


-- 5. RELACIÓN GUARIDA-COMODIDAD
-- Volcán (1): Tiburones, Minions
INSERT INTO guarida_comodidades VALUES (1, 1), (1, 3);
-- Torre (2): Wifi, Silla, Láser
INSERT INTO guarida_comodidades VALUES (2, 4), (2, 5), (2, 2);
-- Submarino (3): Wifi
INSERT INTO guarida_comodidades VALUES (3, 4);
-- Cementerio Elefantes (5): Hienas (6)
INSERT INTO guarida_comodidades VALUES (5, 6);
-- Emporio Vudú (6): Sombras (7), Wifi (4)
INSERT INTO guarida_comodidades VALUES (6, 7), (6, 4);
-- Estrella Muerte (7): Láser (2), Soporte Vital (8), Silla (5)
INSERT INTO guarida_comodidades VALUES (7, 2), (7, 8), (7, 5);
-- Inframundo (8): Fuego Azul (9)
INSERT INTO guarida_comodidades VALUES (8, 9);


-- 6. RESERVAS
INSERT INTO reserva (fecha_inicio, fecha_fin, coste_total, estado, villano_id, guarida_id) VALUES
('2025-12-01', '2025-12-05', 2000.00, true, 1, 1),   -- Joker en Volcán
('2025-11-20', '2025-11-22', 1700.00, false, 3, 3),  -- Doofenshmirtz en Submarino
('2026-01-10', '2026-01-20', 3000.00, true, 4, 4),   -- Gru en Búnker

-- Nuevas Reservas
('2025-10-31', '2025-11-02', 800.00, true, 9, 6),    -- Cruella en Emporio Vudú (Halloween)
('2026-05-04', '2026-05-10', 30000.00, true, 2, 7),  -- Mr. Freeze en Estrella de la Muerte (Ironía: busca frío en el espacio)
('2025-07-15', '2025-07-20', 750.00, false, 5, 8);   -- Scar en el Inframundo (Visitando parientes)


-- 7. FACTURAS
INSERT INTO factura (fecha_emision, importe, impuestos_malignos, metodo_pago, reserva_id) VALUES
('2025-11-30', 2000.00, 21.00, 'Lingotes de Oro', 1),
('2025-12-28', 3000.00, 50.00, 'Bitcoin', 3),
('2025-11-03', 800.00, 100.00, 'Pieles de Abrigo', 4), -- Factura de Cruella
('2026-05-11', 30000.00, 5000.00, 'Créditos Imperiales', 5); -- Factura de Vader/Freeze

-- 8. RESEÑAS
INSERT INTO resena (comentario, puntuacion, fecha_publicacion, villano_id, guarida_id) VALUES
('La lava estaba un poco fría, pero los esbirros muy atentos.', 4, '2025-12-06', 1, 1),
('Demasiada humedad para mis artilugios. Se me oxidó el Inator.', 2, '2025-11-23', 3, 3),
('Excelente para esconder la Luna.', 5, '2026-01-21', 4, 4),

-- Nuevas Reseñas Graciosas
('NO ME GUSTA LA ARENA. Es tosca, áspera e irritante y se te mete por todas partes. Pésima ubicación.', 1, '2026-05-12', 7, 4), -- Vader odia la arena (Búnker desierto)
('Encantador. Las sombras tienen voluntad propia y hacen buenos masajes.', 5, '2025-11-05', 9, 6), -- Cruella en Vudú
('Mufasa nunca tuvo una cueva así. El trono es un poco duro, pero las vistas al cementerio son inspiradoras.', 4, '2025-08-01', 5, 5); -- Scar
