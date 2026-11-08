package um.tesoreria.core.hexagonal.usuarios.permiso.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoException;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.SyncPermisosResultado;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.out.PermisoRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SyncPermisosUseCaseImplTest {

    private PermisoRepository repository;
    private SyncPermisosUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(PermisoRepository.class);
        useCase = new SyncPermisosUseCaseImpl(repository);
        when(repository.findAll()).thenReturn(List.of());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Permiso permiso(String clave, String descripcion, String modulo) {
        return Permiso.builder()
                .aplicacion("TESORERIA")
                .clave(clave)
                .descripcion(descripcion)
                .modulo(modulo)
                .build();
    }

    @Test
    void claveNueva_seAgrega() {
        SyncPermisosResultado resultado = useCase.sync(
                List.of(permiso("compras.iniciar_pedido", "Iniciar", "compras")), false);

        assertThat(resultado.getAgregados()).isEqualTo(1);
        assertThat(resultado.getClavesAgregadas()).containsExactly("TESORERIA::compras.iniciar_pedido");
        ArgumentCaptor<Permiso> captor = ArgumentCaptor.forClass(Permiso.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getActivo()).isEqualTo((byte) 1);
    }

    @Test
    void claveExistenteConCambios_seActualiza() {
        Permiso existente = permiso("compras.iniciar_pedido", "Vieja", "compras");
        existente.setPermisoId(7L);
        existente.setActivo((byte) 1);
        when(repository.findAll()).thenReturn(List.of(existente));

        SyncPermisosResultado resultado = useCase.sync(
                List.of(permiso("compras.iniciar_pedido", "Nueva", "compras")), false);

        assertThat(resultado.getActualizados()).isEqualTo(1);
        assertThat(existente.getDescripcion()).isEqualTo("Nueva");
        verify(repository).save(existente);
    }

    @Test
    void claveExistenteSinCambios_noGuarda() {
        Permiso existente = permiso("compras.iniciar_pedido", "Iniciar", "compras");
        existente.setPermisoId(7L);
        existente.setActivo((byte) 1);
        when(repository.findAll()).thenReturn(List.of(existente));

        SyncPermisosResultado resultado = useCase.sync(
                List.of(permiso("compras.iniciar_pedido", "Iniciar", "compras")), false);

        assertThat(resultado.getSinCambios()).isEqualTo(1);
        assertThat(resultado.getActualizados()).isZero();
        verify(repository, never()).save(any());
    }

    @Test
    void claveSobranteActiva_seRetiraConActivoCero() {
        Permiso existente = permiso("compras.vieja", "Vieja", "compras");
        existente.setPermisoId(9L);
        existente.setActivo((byte) 1);
        when(repository.findAll()).thenReturn(List.of(existente));

        SyncPermisosResultado resultado = useCase.sync(
                List.of(permiso("compras.iniciar_pedido", "Iniciar", "compras")), false);

        assertThat(resultado.getRetirados()).isEqualTo(1);
        assertThat(resultado.getClavesRetiradas()).containsExactly("TESORERIA::compras.vieja");
        assertThat(existente.getActivo()).isZero();
        verify(repository).save(existente);
    }

    @Test
    void claveSobranteYaInactiva_noSeCuentaComoRetiro() {
        Permiso existente = permiso("compras.vieja", "Vieja", "compras");
        existente.setPermisoId(9L);
        existente.setActivo((byte) 0);
        when(repository.findAll()).thenReturn(List.of(existente));

        SyncPermisosResultado resultado = useCase.sync(
                List.of(permiso("compras.iniciar_pedido", "Iniciar", "compras")), false);

        assertThat(resultado.getRetirados()).isZero();
        verify(repository, never()).save(existente);
    }

    @Test
    void dryRunNoEscribe() {
        Permiso existente = permiso("compras.vieja", "Vieja", "compras");
        existente.setPermisoId(9L);
        existente.setActivo((byte) 1);
        when(repository.findAll()).thenReturn(List.of(existente));

        SyncPermisosResultado resultado = useCase.sync(
                List.of(permiso("compras.iniciar_pedido", "Iniciar", "compras")), true);

        assertThat(resultado.getAgregados()).isEqualTo(1);
        assertThat(resultado.getRetirados()).isEqualTo(1);
        assertThat(resultado.isDryRun()).isTrue();
        verify(repository, never()).save(any());
    }

    @Test
    void manifiestoVacioSeRechazaParaNoRetirarTodo() {
        assertThatThrownBy(() -> useCase.sync(List.of(), false))
                .isInstanceOf(PermisoException.class)
                .hasMessageContaining("vacío");
        verify(repository, never()).findAll();
    }

    @Test
    void claveDuplicadaSeRechaza() {
        assertThatThrownBy(() -> useCase.sync(List.of(
                permiso("compras.gastos", "A", "compras"),
                permiso("compras.gastos", "B", "compras")), false))
                .isInstanceOf(PermisoException.class)
                .hasMessageContaining("duplicada");
    }

    @Test
    void sinAplicacionUsaTesoreríaPorDefecto() {
        Permiso sinAplicacion = Permiso.builder()
                .clave("compras.gastos")
                .descripcion("Gastos")
                .modulo("compras")
                .build();

        SyncPermisosResultado resultado = useCase.sync(List.of(sinAplicacion), true);

        assertThat(resultado.getClavesAgregadas()).containsExactly("TESORERIA::compras.gastos");
    }
}
