-- Un solo borrador ABIERTO por chequera (y por persona/lectivo cuando la chequera todavía no existe).
-- Limpieza única de los duplicados que dejó la emisión individual antes del refactor.
-- Idempotente: si se vuelve a ejecutar, mantiene un único borrador por clave.

CREATE TEMPORARY TABLE tmp_borrador_vigente AS
SELECT MAX(chequera_borrador_id) AS keep_id
FROM chequera_borrador
WHERE estado = 'ABIERTO'
GROUP BY facultad_id, tipo_chequera_id, COALESCE(chequera_serie_id, 0), persona_id, documento_id, lectivo_id;

UPDATE chequera_borrador
SET estado = 'DESCARTADO'
WHERE estado = 'ABIERTO'
  AND chequera_borrador_id NOT IN (SELECT keep_id FROM tmp_borrador_vigente);

DROP TEMPORARY TABLE tmp_borrador_vigente;
