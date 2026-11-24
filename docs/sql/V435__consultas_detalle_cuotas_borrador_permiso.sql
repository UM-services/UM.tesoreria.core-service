-- Permiso de la exportación de borradores abiertos por facultad y sede (Consultas de Chequeras).
-- Base: tesium. Sólo datos de catálogo: no toca esquema ni asigna roles/usuarios.
--
-- La asignación a roles (`rol_permiso`) y a usuarios (`usuario_permiso`) es administración de
-- datos y se hace desde la pantalla de Permisos del módulo Administrador; no se versiona acá.
-- Se recomienda otorgarla junto con `chequeras.consultas.detalleCuotas`.
--
-- Rollback (borra primero las asignaciones, por las FKs):
--   DELETE FROM rol_permiso     WHERE permiso_id IN (SELECT permiso_id FROM permiso WHERE clave = 'chequeras.consultas.detalleCuotasBorrador');
--   DELETE FROM usuario_permiso WHERE permiso_id IN (SELECT permiso_id FROM permiso WHERE clave = 'chequeras.consultas.detalleCuotasBorrador');
--   DELETE FROM permiso WHERE clave = 'chequeras.consultas.detalleCuotasBorrador';

INSERT INTO permiso (clave, descripcion, modulo, aplicacion, activo, created, updated)
VALUES
    ('chequeras.consultas.detalleCuotasBorrador',
     'Descargar las planillas de borradores abiertos por facultad y sede en formato horizontal o vertical',
     'chequeras', 'TESORERIA', 1, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    modulo = VALUES(modulo),
    activo = 1,
    updated = NOW(6);
