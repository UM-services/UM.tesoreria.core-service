package um.tesoreria.core.hexagonal.usuarios.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.FindTodosUsuariosBySearchUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out.UsuarioRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindTodosUsuariosBySearchUseCaseImpl implements FindTodosUsuariosBySearchUseCase {

    private final UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> findTodosUsuariosBySearch(String texto) {
        // Sin texto de búsqueda se devuelve el padrón completo (activos e inactivos).
        String normalizado = (texto == null || texto.isBlank()) ? null : texto.trim();
        return usuarioRepository.findAllBySearch(normalizado);
    }
}
