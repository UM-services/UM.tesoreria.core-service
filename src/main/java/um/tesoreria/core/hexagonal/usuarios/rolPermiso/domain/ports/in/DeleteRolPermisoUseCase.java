package um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.in;

public interface DeleteRolPermisoUseCase {
    void deleteRolPermiso(Long rolId, Long permisoId);
}
