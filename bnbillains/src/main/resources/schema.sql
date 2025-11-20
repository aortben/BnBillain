DROP DATABASE IF EXISTS bnbillain_db;
CREATE DATABASE bnbillain_db;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS sala_secreta (
    id LONG AUTO_INCREMENT PRIMARY KEY,
    codigo_acceso VARCHAR(8) NOT NULL,
    funcion_principal VARCHAR(255) NOT NULL,
    salida_emergencia BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS comodidad (
    id LONG AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    auto_destruccion BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS villano (
     id LONG AUTO_INCREMENT PRIMARY KEY,
     nombre VARCHAR(255) NOT NULL,
     alias VARCHAR(255) NOT NULL,
     carne_villano VARCHAR(20) NOT NULL UNIQUE,
     email VARCHAR(255) NOT NULL UNIQUE
);

-- Tabla Guarida (1:1 con Sala Secreta)
CREATE TABLE IF NOT EXISTS guarida (
     id LONG AUTO_INCREMENT PRIMARY KEY,
     nombre VARCHAR(255) NOT NULL,
     descripcion VARCHAR(1000),
     ubicacion VARCHAR(255) NOT NULL,
     precio_noche DECIMAL(10, 2) NOT NULL,
     imagen VARCHAR(255),
     sala_secreta_id LONG UNIQUE,
     FOREIGN KEY (sala_secreta_id) REFERENCES sala_secreta(id) ON DELETE CASCADE ON UPDATE CASCADE,
     CONSTRAINT chk_precio_positivo CHECK (precio_noche > 0)
);

-- Tabla (N:M Guarida-Comodidad)
CREATE TABLE IF NOT EXISTS guarida_comodidades (
     guarida_id LONG,
     comodidades_id LONG,
     PRIMARY KEY (guarida_id, comodidades_id),
     FOREIGN KEY (guarida_id) REFERENCES guarida(id)
         ON DELETE CASCADE ON UPDATE CASCADE,
     FOREIGN KEY (comodidades_id) REFERENCES comodidad(id)
         ON DELETE CASCADE ON UPDATE CASCADE
);


-- Tabla Reserva (N:1 con Villano y Guarida)
CREATE TABLE IF NOT EXISTS reserva (
     id LONG AUTO_INCREMENT PRIMARY KEY,
     fecha_inicio DATE NOT NULL,
     fecha_fin DATE NOT NULL,
     coste_total DECIMAL(10, 2) NOT NULL,
     estado BOOLEAN DEFAULT FALSE,
     villano_id LONG,
     guarida_id LONG,
     FOREIGN KEY (villano_id) REFERENCES villano(id)
         ON DELETE CASCADE ON UPDATE CASCADE,
     FOREIGN KEY (guarida_id) REFERENCES guarida(id)
         ON DELETE CASCADE ON UPDATE CASCADE,
    -- Restricción: La fecha de inicio debe ser ANTERIOR a la de fin
     CONSTRAINT chk_fechas_validas CHECK (fecha_inicio < fecha_fin),
     CONSTRAINT chk_coste_positivo CHECK (coste_total >= 0)
);

-- Tabla Factura (1:1 con Reserva)
CREATE TABLE IF NOT EXISTS factura (
     id LONG AUTO_INCREMENT PRIMARY KEY,
     fecha_emision DATE NOT NULL,
     importe DECIMAL(10, 2) NOT NULL,
     impuestos_malignos DECIMAL(10, 2) DEFAULT 0.0,
     metodo_pago VARCHAR(255),
     reserva_id LONG UNIQUE,
     FOREIGN KEY (reserva_id) REFERENCES reserva(id)
         ON DELETE CASCADE ON UPDATE CASCADE,
     CONSTRAINT chk_importe_positivo CHECK (importe >= 0)
);

-- Tabla Reseña (N:1 con Villano y Guarida)
CREATE TABLE reseña (
    id LONG AUTO_INCREMENT PRIMARY KEY,
    comentario VARCHAR(1000),
    puntuacion LONG NOT NULL,
    fecha_publicacion DATE,
    villano_id LONG,
    guarida_id LONG,
    FOREIGN KEY (villano_id) REFERENCES villano(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (guarida_id) REFERENCES guarida(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
-- Restricción: Puntuación entre 1 y 5
    CONSTRAINT chk_puntuacion_rango CHECK (puntuacion >= 1 AND puntuacion <= 5)
);
