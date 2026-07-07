ALTER TABLE operacion
  DROP CHECK chk_operacion_plazo;

ALTER TABLE operacion
  ADD CONSTRAINT chk_operacion_plazo
    CHECK (plazo BETWEEN 12 AND 84);
