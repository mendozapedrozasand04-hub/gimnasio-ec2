CREATE DATABASE IF NOT EXISTS gimnasio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gimnasio_db;

CREATE TABLE IF NOT EXISTS administrador (
 id_administrador INT AUTO_INCREMENT PRIMARY KEY,
 nombres VARCHAR(100) NOT NULL,
 apellidos VARCHAR(100) NOT NULL,
 correo VARCHAR(150) NOT NULL UNIQUE,
 password_hash VARCHAR(255) NOT NULL,
 estado VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',
 fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS cliente (
 id_cliente INT AUTO_INCREMENT PRIMARY KEY,
 tipo_documento VARCHAR(10) NOT NULL DEFAULT 'DNI',
 documento VARCHAR(20) NOT NULL UNIQUE,
 nombres VARCHAR(100) NOT NULL,
 apellidos VARCHAR(100) NOT NULL,
 telefono VARCHAR(20) NOT NULL,
 correo VARCHAR(150),
 estado VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',
 fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS plan (
 id_plan INT AUTO_INCREMENT PRIMARY KEY,
 nombre VARCHAR(80) NOT NULL,
 duracion_dias INT NOT NULL,
 precio DECIMAL(10,2) NOT NULL,
 descripcion VARCHAR(250),
 estado VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',
 CHECK(duracion_dias>0), CHECK(precio>0)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS membresia (
 id_membresia INT AUTO_INCREMENT PRIMARY KEY,
 id_cliente INT NOT NULL,
 id_plan INT NOT NULL,
 fecha_inicio DATE NOT NULL,
 fecha_fin DATE NOT NULL,
 precio_pactado DECIMAL(10,2) NOT NULL,
 estado VARCHAR(15) NOT NULL DEFAULT 'REGISTRADA',
 fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(id_cliente) REFERENCES cliente(id_cliente),
 FOREIGN KEY(id_plan) REFERENCES plan(id_plan),
 INDEX idx_membresia_fin(fecha_fin),
 CHECK(fecha_fin>=fecha_inicio), CHECK(precio_pactado>0)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS pago (
 id_pago INT AUTO_INCREMENT PRIMARY KEY,
 id_membresia INT NOT NULL UNIQUE,
 monto DECIMAL(10,2) NOT NULL,
 fecha_pago DATETIME NOT NULL,
 metodo_pago VARCHAR(25) NOT NULL,
 estado VARCHAR(10) NOT NULL DEFAULT 'PAGADO',
 fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(id_membresia) REFERENCES membresia(id_membresia),
 INDEX idx_pago_fecha(fecha_pago), CHECK(monto>0)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS asistencia (
 id_asistencia INT AUTO_INCREMENT PRIMARY KEY,
 id_cliente INT NOT NULL,
 fecha DATE NOT NULL,
 hora_ingreso TIME NOT NULL,
 FOREIGN KEY(id_cliente) REFERENCES cliente(id_cliente),
 UNIQUE KEY uq_asistencia_diaria(id_cliente,fecha),
 INDEX idx_asistencia_fecha(fecha)
) ENGINE=InnoDB;
