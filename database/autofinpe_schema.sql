-- AutoFinPe - Fase 2
-- Script MySQL 8 basado estrictamente en el DER del documento AutoFinPe.

DROP DATABASE IF EXISTS autofinpe;
CREATE DATABASE autofinpe
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE autofinpe;

CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  login VARCHAR(20) NOT NULL,
  clave_hash VARCHAR(64) NOT NULL,
  nombres VARCHAR(120) NOT NULL,
  rol VARCHAR(20) NOT NULL,
  estado CHAR(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (id_usuario),
  UNIQUE KEY uk_usuario_login (login),
  CONSTRAINT chk_usuario_login_formato
    CHECK (login REGEXP '^[A-Za-z0-9._-]{4,20}$'),
  CONSTRAINT chk_usuario_clave_hash_formato
    CHECK (clave_hash REGEXP '^[A-Fa-f0-9]{64}$'),
  CONSTRAINT chk_usuario_rol
    CHECK (rol IN ('ADMIN', 'EJECUTIVO')),
  CONSTRAINT chk_usuario_estado
    CHECK (estado IN ('A', 'I'))
) ENGINE = InnoDB;

CREATE TABLE cliente (
  id_cliente INT NOT NULL AUTO_INCREMENT,
  dni VARCHAR(8) NOT NULL,
  nombres VARCHAR(60) NOT NULL,
  apellidos VARCHAR(60) NOT NULL,
  ingreso_mensual DECIMAL(12,2) NOT NULL,
  calificacion VARCHAR(20) NOT NULL,
  telefono VARCHAR(20) NULL,
  email VARCHAR(100) NULL,
  PRIMARY KEY (id_cliente),
  UNIQUE KEY uk_cliente_dni (dni),
  UNIQUE KEY uk_cliente_email (email),
  KEY idx_cliente_apellidos_nombres (apellidos, nombres),
  CONSTRAINT chk_cliente_dni_formato
    CHECK (dni REGEXP '^[0-9]{8}$'),
  CONSTRAINT chk_cliente_ingreso_mensual
    CHECK (ingreso_mensual >= 0),
  CONSTRAINT chk_cliente_calificacion
    CHECK (calificacion IN ('A', 'B', 'C', 'D', 'E', 'SIN_CALIFICAR')),
  CONSTRAINT chk_cliente_email_formato
    CHECK (email IS NULL OR email REGEXP '^[^@[:space:]]+@[^@[:space:]]+\\.[^@[:space:]]+$')
) ENGINE = InnoDB;

CREATE TABLE vehiculo (
  id_vehiculo INT NOT NULL AUTO_INCREMENT,
  marca VARCHAR(30) NOT NULL,
  modelo VARCHAR(30) NOT NULL,
  anio YEAR NOT NULL,
  precio_pen DECIMAL(12,2) NOT NULL,
  precio_usd DECIMAL(12,2) NOT NULL,
  categoria VARCHAR(30) NOT NULL,
  imagen_url VARCHAR(255) NULL,
  PRIMARY KEY (id_vehiculo),
  KEY idx_vehiculo_marca_modelo (marca, modelo),
  KEY idx_vehiculo_categoria (categoria),
  CONSTRAINT chk_vehiculo_precio_pen
    CHECK (precio_pen > 0),
  CONSTRAINT chk_vehiculo_precio_usd
    CHECK (precio_usd > 0),
  CONSTRAINT chk_vehiculo_anio
    CHECK (anio BETWEEN 2000 AND 2155)
) ENGINE = InnoDB;

CREATE TABLE configuracion (
  id_config INT NOT NULL AUTO_INCREMENT,
  moneda CHAR(3) NOT NULL DEFAULT 'PEN',
  tipo_tasa CHAR(1) NOT NULL DEFAULT 'E',
  capitalizacion SMALLINT NOT NULL DEFAULT 12,
  tipo_gracia CHAR(1) NOT NULL DEFAULT 'S',
  meses_gracia TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id_config),
  UNIQUE KEY uk_configuracion_parametros (
    moneda,
    tipo_tasa,
    capitalizacion,
    tipo_gracia,
    meses_gracia
  ),
  CONSTRAINT chk_configuracion_moneda
    CHECK (moneda IN ('PEN', 'USD')),
  CONSTRAINT chk_configuracion_tipo_tasa
    CHECK (tipo_tasa IN ('N', 'E')),
  CONSTRAINT chk_configuracion_capitalizacion
    CHECK (capitalizacion IN (1, 2, 4, 12, 365)),
  CONSTRAINT chk_configuracion_tipo_gracia
    CHECK (tipo_gracia IN ('S', 'T', 'P')),
  CONSTRAINT chk_configuracion_meses_gracia
    CHECK (meses_gracia BETWEEN 0 AND 6),
  CONSTRAINT chk_configuracion_gracia_sin_meses
    CHECK (
      (tipo_gracia = 'S' AND meses_gracia = 0)
      OR (tipo_gracia IN ('T', 'P') AND meses_gracia BETWEEN 1 AND 6)
    )
) ENGINE = InnoDB;

CREATE TABLE operacion (
  id_operacion INT NOT NULL AUTO_INCREMENT,
  id_cliente INT NOT NULL,
  id_vehiculo INT NOT NULL,
  id_config INT NOT NULL,
  id_usuario INT NOT NULL,
  fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  plazo INT NOT NULL,
  cuota_inicial_pct DECIMAL(5,2) NOT NULL,
  cuota_balon_pct DECIMAL(5,2) NOT NULL,
  valor_tasa DECIMAL(6,4) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'REGISTRADA',
  PRIMARY KEY (id_operacion),
  KEY idx_operacion_cliente (id_cliente),
  KEY idx_operacion_vehiculo (id_vehiculo),
  KEY idx_operacion_configuracion (id_config),
  KEY idx_operacion_usuario (id_usuario),
  KEY idx_operacion_fecha (fecha),
  KEY idx_operacion_estado (estado),
  CONSTRAINT fk_operacion_cliente
    FOREIGN KEY (id_cliente)
    REFERENCES cliente (id_cliente)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_operacion_vehiculo
    FOREIGN KEY (id_vehiculo)
    REFERENCES vehiculo (id_vehiculo)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_operacion_configuracion
    FOREIGN KEY (id_config)
    REFERENCES configuracion (id_config)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_operacion_usuario
    FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT chk_operacion_plazo
    CHECK (plazo BETWEEN 12 AND 84),
  CONSTRAINT chk_operacion_cuota_inicial_pct
    CHECK (cuota_inicial_pct BETWEEN 0 AND 60),
  CONSTRAINT chk_operacion_cuota_balon_pct
    CHECK (cuota_balon_pct BETWEEN 0 AND 50),
  CONSTRAINT chk_operacion_valor_tasa
    CHECK (valor_tasa > 0),
  CONSTRAINT chk_operacion_estado
    CHECK (estado IN ('REGISTRADA', 'SIMULADA', 'APROBADA', 'RECHAZADA', 'DESEMBOLSADA', 'ANULADA'))
) ENGINE = InnoDB;

CREATE TABLE cronograma (
  id_cronograma INT NOT NULL AUTO_INCREMENT,
  id_operacion INT NOT NULL,
  nro_cuota INT NOT NULL,
  saldo_inicial DECIMAL(12,2) NOT NULL,
  interes DECIMAL(12,2) NOT NULL,
  amortizacion DECIMAL(12,2) NOT NULL,
  seguro_desgrav DECIMAL(12,2) NOT NULL,
  seguro_vehic DECIMAL(12,2) NOT NULL,
  portes DECIMAL(12,2) NOT NULL,
  cuota_credito DECIMAL(12,2) NOT NULL,
  cuota_total DECIMAL(12,2) NOT NULL,
  saldo_final DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (id_cronograma),
  UNIQUE KEY uk_cronograma_operacion_cuota (id_operacion, nro_cuota),
  KEY idx_cronograma_operacion (id_operacion),
  CONSTRAINT fk_cronograma_operacion
    FOREIGN KEY (id_operacion)
    REFERENCES operacion (id_operacion)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT chk_cronograma_nro_cuota
    CHECK (nro_cuota >= 1),
  CONSTRAINT chk_cronograma_saldo_inicial
    CHECK (saldo_inicial >= 0),
  CONSTRAINT chk_cronograma_interes
    CHECK (interes >= 0),
  CONSTRAINT chk_cronograma_amortizacion
    CHECK (amortizacion >= 0),
  CONSTRAINT chk_cronograma_seguro_desgrav
    CHECK (seguro_desgrav >= 0),
  CONSTRAINT chk_cronograma_seguro_vehic
    CHECK (seguro_vehic >= 0),
  CONSTRAINT chk_cronograma_portes
    CHECK (portes >= 0),
  CONSTRAINT chk_cronograma_cuota_total
    CHECK (cuota_total >= 0),
  CONSTRAINT chk_cronograma_saldo_final
    CHECK (saldo_final >= 0)
) ENGINE = InnoDB;

CREATE TABLE indicador (
  id_indicador INT NOT NULL AUTO_INCREMENT,
  id_operacion INT NOT NULL,
  tcea DECIMAL(6,4) NOT NULL,
  van DECIMAL(12,2) NOT NULL,
  tir DECIMAL(6,4) NOT NULL,
  total_intereses DECIMAL(12,2) NOT NULL,
  total_amortizacion DECIMAL(12,2) NOT NULL,
  total_seguros DECIMAL(12,2) NOT NULL,
  total_portes DECIMAL(12,2) NOT NULL,
  total_pagado DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (id_indicador),
  UNIQUE KEY uk_indicador_operacion (id_operacion),
  CONSTRAINT fk_indicador_operacion
    FOREIGN KEY (id_operacion)
    REFERENCES operacion (id_operacion)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT chk_indicador_tcea
    CHECK (tcea >= 0),
  CONSTRAINT chk_indicador_tir
    CHECK (tir >= 0),
  CONSTRAINT chk_indicador_total_intereses
    CHECK (total_intereses >= 0),
  CONSTRAINT chk_indicador_total_amortizacion
    CHECK (total_amortizacion >= 0),
  CONSTRAINT chk_indicador_total_pagado
    CHECK (total_pagado >= 0)
) ENGINE = InnoDB;

CREATE TABLE auditoria (
  id_log INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  accion VARCHAR(100) NOT NULL,
  tabla_afectada VARCHAR(50) NOT NULL,
  fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_log),
  KEY idx_auditoria_usuario (id_usuario),
  KEY idx_auditoria_fecha_hora (fecha_hora),
  KEY idx_auditoria_tabla_afectada (tabla_afectada),
  CONSTRAINT fk_auditoria_usuario
    FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE = InnoDB;

INSERT INTO usuario (login, clave_hash, nombres, rol, estado)
VALUES (
  'admin',
  SHA2('Admin123!', 256),
  'Administrador AutoFinPe',
  'ADMIN',
  'A'
);
