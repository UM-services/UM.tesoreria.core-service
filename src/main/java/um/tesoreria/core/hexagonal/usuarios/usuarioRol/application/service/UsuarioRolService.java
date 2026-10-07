package um.tesoreria.core.hexagonal.usuarios.usuarioRol.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.in.CreateUsuarioRolUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.in.DeleteUsuarioRolUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.in.GetUsuarioRolesByUserIdUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioRolService {

    private final GetUsuarioRolesByUserIdUseCase getUsuarioRolesByUserIdUseCase;
    private final CreateUsuarioRolUseCase createUsuarioRolUseCase;
    private final DeleteUsuarioRolUseCase deleteUsuarioRolUseCase;

    public List<UsuarioRol> findAllByUserId(Long userId) {
        return getUsuarioRolesByUserIdUseCase.getByUserId(userId);
    }

    public UsuarioRol add(UsuarioRol usuarioRol) {
        return createUsuarioRolUseCase.createUsuarioRol(usuarioRol);
    }

    public void delete(Long userId, Long rolId) {
        deleteUsuarioRolUseCase.deleteUsuarioRol(userId, rolId);
    }
}
