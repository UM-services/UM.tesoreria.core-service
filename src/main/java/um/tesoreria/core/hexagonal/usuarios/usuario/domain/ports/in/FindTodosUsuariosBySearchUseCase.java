package um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;

import java.util.List;

/**
 * Búsqueda de usuarios incluyendo los inactivos (a diferencia de
 * {@link FindUsuariosBySearchUseCase}, que sólo devuelve activos).
 */
public interface FindTodosUsuariosBySearchUseCase {
    List<Usuario> findTodosUsuariosBySearch(String texto);
}
