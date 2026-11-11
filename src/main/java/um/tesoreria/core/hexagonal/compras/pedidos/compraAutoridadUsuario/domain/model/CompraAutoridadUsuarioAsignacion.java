package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.model;

/**
 * Vista de una asignación usuario ↔ perfil con los datos mínimos del usuario para mostrarla
 * en la pantalla de administración (nombre/login), sin exponer el resto del padrón.
 */
public record CompraAutoridadUsuarioAsignacion(
        Integer usuarioId,
        Long autoridadPerfilId,
        String login,
        String nombre,
        Byte activo) {

}
