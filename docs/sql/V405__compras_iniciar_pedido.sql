-- Clave de permiso de la etapa "Iniciar pedido de compra" (frontend compras → fachada compras-service).
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente.
-- La clave se consume en la fachada (tesoreria-compras) contra GET /api/tesoreria/core/permisoEfectivo/usuario/{userId}.
-- La asignación a roles/usuarios (rol_permiso / usuario_permiso) es administración de datos y no se versiona acá.

INSERT INTO permiso (clave, descripcion, modulo, aplicacion, activo, created, updated)
VALUES ('compras.iniciar_pedido', 'Iniciar y enviar un pedido de compra', 'compras', 'TESORERIA', 1, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    descripcion = 'Iniciar y enviar un pedido de compra',
    modulo = 'compras',
    activo = 1,
    updated = NOW(6);
