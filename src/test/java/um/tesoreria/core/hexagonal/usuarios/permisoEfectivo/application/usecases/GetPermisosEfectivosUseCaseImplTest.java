package um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.application.exception.PermisoEfectivoException;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.in.GetRolPermisosByRolIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.GetUsuarioByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.in.GetUsuarioPermisosByUserIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.in.GetUsuarioRolesByUserIdUseCase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPermisosEfectivosUseCaseImplTest {

    @Mock
    private GetUsuarioRolesByUserIdUseCase getUsuarioRolesByUserIdUseCase;
    @Mock
    private GetRolPermisosByRolIdUseCase getRolPermisosByRolIdUseCase;
    @Mock
    private GetUsuarioPermisosByUserIdUseCase getUsuarioPermisosByUserIdUseCase;
    @Mock
    private GetUsuarioByIdUseCase getUsuarioByIdUseCase;

    @InjectMocks
    private GetPermisosEfectivosUseCaseImpl useCase;

    private Permiso permiso(Long id, String clave, byte activo) {
        return Permiso.builder().permisoId(id).clave(clave).activo(activo).build();
    }

    @Test
    void mergesRolesOverridesAndLegacyBridge() {
        var usuario = Usuario.builder().userId(1L).imprimeChequera((byte) 1).administrador((byte) 0).build();
        when(getUsuarioByIdUseCase.getUsuarioById(1L)).thenReturn(Optional.of(usuario));
        when(getUsuarioRolesByUserIdUseCase.getByUserId(1L))
                .thenReturn(List.of(UsuarioRol.builder().rolId(5L).build()));
        when(getRolPermisosByRolIdUseCase.getByRolId(5L)).thenReturn(List.of(
                RolPermiso.builder().rolId(5L).permisoId(10L).permiso(permiso(10L, "chequeras.eliminar", (byte) 1)).build(),
                RolPermiso.builder().rolId(5L).permisoId(11L).permiso(permiso(11L, "compras.ver", (byte) 0)).build()));
        when(getUsuarioPermisosByUserIdUseCase.getByUserId(1L)).thenReturn(List.of(
                // revoca lo heredado del rol
                UsuarioPermiso.builder().permisoId(10L).otorgado((byte) 0).permiso(permiso(10L, "chequeras.eliminar", (byte) 1)).build(),
                // otorga un permiso puntual
                UsuarioPermiso.builder().permisoId(12L).otorgado((byte) 1).permiso(permiso(12L, "pagos.aprobar", (byte) 1)).build()));

        var result = useCase.getPermisosEfectivos(1L);

        // rol aporta chequeras.eliminar (revocado por override) y compras.ver (inactivo, excluido);
        // override otorga pagos.aprobar; bridge legacy agrega chequeras.imprimir.
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getPermisos()).containsExactly("chequeras.imprimir", "pagos.aprobar");
    }

    @Test
    void whenUserIdNull_throws() {
        assertThatThrownBy(() -> useCase.getPermisosEfectivos(null))
                .isInstanceOf(PermisoEfectivoException.class);
        verifyNoInteractions(getUsuarioByIdUseCase);
    }

    @Test
    void whenUsuarioMissing_throws() {
        when(getUsuarioByIdUseCase.getUsuarioById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getPermisosEfectivos(99L))
                .isInstanceOf(PermisoEfectivoException.class);
    }
}
