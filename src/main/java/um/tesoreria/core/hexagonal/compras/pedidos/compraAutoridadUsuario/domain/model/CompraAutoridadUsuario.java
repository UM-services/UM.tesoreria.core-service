package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.model;

/**
 * Asignación de un perfil de autoridad por monto a un usuario.
 */
public record CompraAutoridadUsuario(
        Integer usuarioId,
        Long autoridadPerfilId) {

}
