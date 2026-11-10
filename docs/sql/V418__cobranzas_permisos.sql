-- Permisos del módulo Cobranzas de Chequeras (migración del menú Pagos).
-- Base: tesium. Sólo datos de catálogo: no toca esquema ni asigna roles/usuarios.
--
-- La asignación a roles (`rol_permiso`) y a usuarios (`usuario_permiso`) es administración de
-- datos y se hace desde la pantalla de Permisos del módulo Administrador; no se versiona acá.
-- Hasta que un rol o usuario tenga la clave, las pantallas de Cobranzas quedan ocultas (la UI
-- falla cerrada a propósito).
--
-- Rollback (borra primero las asignaciones, por las FKs):
--   DELETE FROM rol_permiso     WHERE permiso_id IN (SELECT permiso_id FROM permiso WHERE clave LIKE 'chequeras.cobranzas.%');
--   DELETE FROM usuario_permiso WHERE permiso_id IN (SELECT permiso_id FROM permiso WHERE clave LIKE 'chequeras.cobranzas.%');
--   DELETE FROM permiso WHERE clave LIKE 'chequeras.cobranzas.%';

INSERT INTO permiso (clave, descripcion, modulo, aplicacion, activo, created, updated)
VALUES
    ('chequeras.cobranzas.importar',   'Importar rendiciones de pago, consultar el historial y eliminar una importación', 'chequeras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('chequeras.cobranzas.generar',    'Generar archivos de facturación/débito y anular o reprogramar un envío',       'chequeras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('chequeras.cobranzas.pagoManual', 'Registrar y eliminar pagos manuales de una cuota',                            'chequeras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('chequeras.cobranzas.baja',       'Dar de baja una chequera y anular una baja vigente',                          'chequeras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('chequeras.cobranzas.consultar',  'Consultar chequeras por unidad académica y número',                           'chequeras', 'TESORERIA', 1, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    modulo = VALUES(modulo),
    activo = 1,
    updated = NOW(6);
