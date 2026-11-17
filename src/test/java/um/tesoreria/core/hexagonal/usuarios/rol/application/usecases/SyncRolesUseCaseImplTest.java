package um.tesoreria.core.hexagonal.usuarios.rol.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.GetAllPermisosUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.application.exception.RolManifestException;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.RolSemilla;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.SyncRolesResultado;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.out.RolRepository;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.out.RolPermisoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SyncRolesUseCaseImplTest {

    private RolRepository rolRepository;
    private RolPermisoRepository rolPermisoRepository;
    private GetAllPermisosUseCase getAllPermisosUseCase;
    private SyncRolesUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        rolRepository = mock(RolRepository.class);
        rolPermisoRepository = mock(RolPermisoRepository.class);
        getAllPermisosUseCase = mock(GetAllPermisosUseCase.class);
        useCase = new SyncRolesUseCaseImpl(rolRepository, rolPermisoRepository, getAllPermisosUseCase);

        when(getAllPermisosUseCase.getAllPermisos()).thenReturn(List.of(
                permiso(1L, "chequeras.eliminar"),
                permiso(2L, "chequeras.imprimir"),
                permiso(3L, "compras.gastos")));
        when(rolRepository.save(any())).thenAnswer(invocation -> {
            Rol rol = invocation.getArgument(0);
            if (rol.getRolId() == null) {
                rol.setRolId(10L);
            }
            return rol;
        });
        when(rolPermisoRepository.findAllByRolId(anyLong())).thenReturn(List.of());
    }

    private Permiso permiso(Long id, String clave) {
        return Permiso.builder()
                .permisoId(id)
                .aplicacion("TESORERIA")
                .clave(clave)
                .modulo(clave.substring(0, clave.indexOf('.')))
                .descripcion(clave)
                .activo((byte) 1)
                .build();
    }

    private RolSemilla semilla(String nombre, String... permisos) {
        return RolSemilla.builder()
                .nombre(nombre)
                .descripcion("Rol " + nombre)
                .aplicacion("TESORERIA")
                .activo((byte) 1)
                .permisos(List.of(permisos))
                .build();
    }

    private RolPermiso asignacion(Long rolId, Long permisoId) {
        return RolPermiso.builder().rolId(rolId).permisoId(permisoId).build();
    }

    @Test
    void rolNuevoSeCreaConSusPermisos() {
        SyncRolesResultado resultado = useCase.sync(
                List.of(semilla("OPERADOR_CHEQUERAS", "chequeras.*")), false);

        assertThat(resultado.getRolesAgregados()).isEqualTo(1);
        assertThat(resultado.getRolesCreados()).containsExactly("TESORERIA::OPERADOR_CHEQUERAS");
        assertThat(resultado.getPermisosAgregados()).isEqualTo(2);
        verify(rolRepository).save(any(Rol.class));
        verify(rolPermisoRepository, times(2)).save(any(RolPermiso.class));
        verify(rolPermisoRepository, never()).deleteByRolIdAndPermisoId(anyLong(), anyLong());
    }

    @Test
    void comodinTotalAsignaTodoElCatalogo() {
        SyncRolesResultado resultado = useCase.sync(
                List.of(semilla("ADMIN_TESORERIA", "*")), false);

        assertThat(resultado.getPermisosAgregados()).isEqualTo(3);
        verify(rolPermisoRepository, times(3)).save(any(RolPermiso.class));
    }

    @Test
    void rolExistenteReconciliaLaMatriz() {
        Rol existente = Rol.builder()
                .rolId(10L)
                .nombre("OPERADOR_CHEQUERAS")
                .descripcion("Descripción vieja")
                .aplicacion("TESORERIA")
                .activo((byte) 1)
                .build();
        when(rolRepository.findByAplicacionAndNombre("TESORERIA", "OPERADOR_CHEQUERAS"))
                .thenReturn(Optional.of(existente));
        // Tiene compras.gastos (3), que no está declarado: debe quitarse.
        when(rolPermisoRepository.findAllByRolId(10L))
                .thenReturn(List.of(asignacion(10L, 3L)));

        SyncRolesResultado resultado = useCase.sync(
                List.of(semilla("OPERADOR_CHEQUERAS", "chequeras.*")), false);

        assertThat(resultado.getRolesAgregados()).isZero();
        assertThat(resultado.getRolesActualizados()).isEqualTo(1);
        assertThat(resultado.getPermisosAgregados()).isEqualTo(2);
        assertThat(resultado.getPermisosQuitados()).isEqualTo(1);
        verify(rolRepository).save(existente);
        verify(rolPermisoRepository).deleteByRolIdAndPermisoId(10L, 3L);
    }

    @Test
    void permisoInexistenteCortaElSync() {
        assertThatThrownBy(() -> useCase.sync(
                List.of(semilla("OPERADOR_CHEQUERAS", "chequeras.no_existe")), false))
                .isInstanceOf(RolManifestException.class)
                .hasMessageContaining("chequeras.no_existe");

        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void comodinSinCoincidenciasSoloAvisa() {
        SyncRolesResultado resultado = useCase.sync(
                List.of(semilla("CONSULTA_EXTERNO", "externo.*")), false);

        assertThat(resultado.getRolesAgregados()).isEqualTo(1);
        assertThat(resultado.getPermisosAgregados()).isZero();
        verify(rolPermisoRepository, never()).save(any(RolPermiso.class));
    }

    @Test
    void dryRunNoEscribe() {
        SyncRolesResultado resultado = useCase.sync(
                List.of(semilla("OPERADOR_CHEQUERAS", "chequeras.*")), true);

        assertThat(resultado.isDryRun()).isTrue();
        assertThat(resultado.getRolesAgregados()).isEqualTo(1);
        assertThat(resultado.getPermisosAgregados()).isEqualTo(2);
        verify(rolRepository, never()).save(any(Rol.class));
        verify(rolPermisoRepository, never()).save(any(RolPermiso.class));
    }

    @Test
    void manifiestoVacioFalla() {
        assertThatThrownBy(() -> useCase.sync(List.of(), false))
                .isInstanceOf(RolManifestException.class)
                .hasMessageContaining("vacío");
    }

    @Test
    void claveExactaSeResuelveSinComodin() {
        SyncRolesResultado resultado = useCase.sync(
                List.of(semilla("OPERADOR_CHEQUERAS", "chequeras.eliminar")), false);

        assertThat(resultado.getPermisosAgregados()).isEqualTo(1);
        verify(rolPermisoRepository).save(any(RolPermiso.class));
    }
}
