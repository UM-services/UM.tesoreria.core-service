-- Claves de permiso de las opciones "Gastos" y "Proveedores" del módulo de compras
-- (frontend compras: menú, ruta y acciones por botón).
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente.
-- Las claves se consumen en el frontend (tesoreria-frontend, app compras): menú con
-- ShellMenuItem.permiso, ruta con permisoGuard y botones con *uiPermiso.
-- La asignación a roles/usuarios (rol_permiso / usuario_permiso) es administración de
-- datos y no se versiona acá.

INSERT INTO permiso (clave, descripcion, modulo, aplicacion, activo, created, updated)
VALUES
    ('compras.gastos', 'Acceder a Gastos (ver artículos de tipo gasto)', 'compras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('compras.gastos.crear', 'Crear gastos', 'compras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('compras.gastos.editar', 'Editar gastos', 'compras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('compras.gastos.eliminar', 'Eliminar gastos', 'compras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('compras.gastos.imputar', 'Asignar imputaciones por ubicación a un gasto', 'compras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('compras.proveedores', 'Acceder a Proveedores (ver el padrón)', 'compras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('compras.proveedores.crear', 'Crear proveedores', 'compras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('compras.proveedores.editar', 'Editar proveedores', 'compras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('compras.proveedores.eliminar', 'Eliminar proveedores', 'compras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('compras.proveedores.descargar', 'Descargar la planilla de proveedores', 'compras', 'TESORERIA', 1, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    modulo = VALUES(modulo),
    activo = 1,
    updated = NOW(6);
