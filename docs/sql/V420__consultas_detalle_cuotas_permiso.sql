-- Permiso de la sección Consultas de Chequeras: detalle de cuotas por facultad y sede.
-- Base: tesium. Sólo datos de catálogo: no toca esquema ni asigna roles/usuarios.
--
-- La asignación a roles (`rol_permiso`) y a usuarios (`usuario_permiso`) es administración de
-- datos y se hace desde la pantalla de Permisos del módulo Administrador; no se versiona acá.
-- Hasta que un rol o usuario tenga la clave, la pantalla queda oculta (la UI falla cerrada).
--
-- Rollback (borra primero las asignaciones, por las FKs):
--   DELETE FROM rol_permiso     WHERE permiso_id IN (SELECT permiso_id FROM permiso WHERE clave = 'chequeras.consultas.detalleCuotas');
--   DELETE FROM usuario_permiso WHERE permiso_id IN (SELECT permiso_id FROM permiso WHERE clave = 'chequeras.consultas.detalleCuotas');
--   DELETE FROM permiso WHERE clave = 'chequeras.consultas.detalleCuotas';

INSERT INTO permiso (clave, descripcion, modulo, aplicacion, activo, created, updated)
VALUES
    ('chequeras.consultas.detalleCuotas',
     'Descargar el detalle de cuotas por facultad y sede en formato horizontal o vertical',
     'chequeras', 'TESORERIA', 1, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    modulo = VALUES(modulo),
    activo = 1,
    updated = NOW(6);
