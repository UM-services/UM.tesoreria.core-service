package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.dto;

/**
 * Asignación usuario ↔ perfil con los datos mínimos del usuario para la pantalla de
 * administración de perfiles de autoridad.
 */
public record CompraAutoridadUsuarioAsignacionResponse(
        Integer usuarioId,
        Long autoridadPerfilId,
        String login,
        String nombre,
        Byte activo) {

}
