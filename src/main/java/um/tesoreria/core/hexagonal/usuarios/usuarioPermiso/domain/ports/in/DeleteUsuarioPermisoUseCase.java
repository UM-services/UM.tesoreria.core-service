package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.in;

public interface DeleteUsuarioPermisoUseCase {
    void deleteUsuarioPermiso(Long userId, Long permisoId);
}
