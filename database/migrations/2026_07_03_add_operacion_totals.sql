-- Migration script to add missing columns to the indicador table and modify the cronograma table.

USE autofinpe;

ALTER TABLE cronograma
ADD COLUMN IF NOT EXISTS cuota_credito DECIMAL(12,2) NOT NULL DEFAULT 0 AFTER portes;

ALTER TABLE indicador
ADD COLUMN IF NOT EXISTS total_amortizacion DECIMAL(12,2) NOT NULL DEFAULT 0 AFTER total_intereses;

ALTER TABLE indicador
ADD COLUMN IF NOT EXISTS total_seguros DECIMAL(12,2) NOT NULL DEFAULT 0 AFTER total_amortizacion;

ALTER TABLE indicador
ADD COLUMN IF NOT EXISTS total_portes DECIMAL(12,2) NOT NULL DEFAULT 0 AFTER total_seguros;