package um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.in;

public interface DeleteUsuarioRolUseCase {
    void deleteUsuarioRol(Long userId, Long rolId);
}
