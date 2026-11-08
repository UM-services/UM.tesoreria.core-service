package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.application.exception.SolicitudAccesoException;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.EstadoSolicitudAcceso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.SolicitudAcceso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.out.SolicitudAccesoRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SolicitudAccesoUseCasesTest {

    private SolicitudAccesoRepository repository;
    private CreateSolicitudAccesoUseCaseImpl createUseCase;
    private ResolveSolicitudAccesoUseCaseImpl resolveUseCase;

    @BeforeEach
    void setUp() {
        repository = mock(SolicitudAccesoRepository.class);
        createUseCase = new CreateSolicitudAccesoUseCaseImpl(repository);
        resolveUseCase = new ResolveSolicitudAccesoUseCaseImpl(repository);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void altaQuedaPendienteYAplicaLosDefaults() {
        var creada = createUseCase.create(SolicitudAcceso.builder()
                .userId(7L)
                .clave("compras.gastos")
                .build());

        assertThat(creada.getEstado()).isEqualTo(EstadoSolicitudAcceso.PENDIENTE.name());
        assertThat(creada.getAplicacion()).isEqualTo("TESORERIA");
        assertThat(creada.getResuelto()).isNull();
        verify(repository).save(any());
    }

    @Test
    void altaDuplicadaDeLaMismaClaveSeRechaza() {
        when(repository.existsPendienteByUserIdAndClave(7L, "compras.gastos")).thenReturn(true);

        assertThatThrownBy(() -> createUseCase.create(SolicitudAcceso.builder()
                .userId(7L)
                .clave("compras.gastos")
                .build()))
                .isInstanceOf(SolicitudAccesoException.class)
                .hasMessageContaining("compras.gastos");

        verify(repository, never()).save(any());
    }

    @Test
    void resolverRegistraDecisionYAuditoria() {
        when(repository.findById(3L)).thenReturn(Optional.of(SolicitudAcceso.builder()
                .solicitudAccesoId(3L)
                .userId(7L)
                .clave("compras.gastos")
                .estado(EstadoSolicitudAcceso.PENDIENTE.name())
                .build()));

        var resuelta = resolveUseCase.resolve(3L, "aprobada", "ok", 1L);

        ArgumentCaptor<SolicitudAcceso> captor = ArgumentCaptor.forClass(SolicitudAcceso.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoSolicitudAcceso.APROBADA.name());
        assertThat(captor.getValue().getResueltoPor()).isEqualTo(1L);
        assertThat(captor.getValue().getResuelto()).isNotNull();
        assertThat(captor.getValue().getObservacion()).isEqualTo("ok");
        assertThat(resuelta.getEstado()).isEqualTo(EstadoSolicitudAcceso.APROBADA.name());
    }

    @Test
    void noSePuedeResolverDosVeces() {
        when(repository.findById(3L)).thenReturn(Optional.of(SolicitudAcceso.builder()
                .solicitudAccesoId(3L)
                .estado(EstadoSolicitudAcceso.APROBADA.name())
                .build()));

        assertThatThrownBy(() -> resolveUseCase.resolve(3L, "RECHAZADA", null, 1L))
                .isInstanceOf(SolicitudAccesoException.class)
                .hasMessageContaining("ya fue resuelta");
    }

    @Test
    void estadoInvalidoSeRechazaAntesDeBuscar() {
        assertThatThrownBy(() -> resolveUseCase.resolve(3L, "PENDIENTE", null, 1L))
                .isInstanceOf(SolicitudAccesoException.class);

        verify(repository, never()).findById(any());
    }
}
