package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.AsientoIngresoJobRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelarJobAsientoIngresoUseCaseImplTest {

    private static final OffsetDateTime FECHA = OffsetDateTime.of(2026, 11, 17, 0, 0, 0, 0, ZoneOffset.UTC);

    @Mock AsientoIngresoJobRepository repository;

    CancelarJobAsientoIngresoUseCaseImpl useCase;

    @BeforeEach
    void preparar() {
        useCase = new CancelarJobAsientoIngresoUseCaseImpl(repository, TransactionOperations.withoutTransaction());
    }

    private AsientoIngresoJob job(AsientoIngresoJobEstado estado) {
        return AsientoIngresoJob.builder()
                .jobId(1L)
                .estado(estado)
                .fechaDesde(FECHA)
                .fechaHasta(FECHA)
                .totalTareas(2)
                .tareasPendientes(2)
                .build();
    }

    @Test
    void cancelaUnJobPendiente() {
        when(repository.findWithLockByJobId(1L)).thenReturn(Optional.of(job(AsientoIngresoJobEstado.PENDIENTE)));
        when(repository.update(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AsientoIngresoJob cancelado = useCase.cancelar(1L);

        assertThat(cancelado.getEstado()).isEqualTo(AsientoIngresoJobEstado.CANCELADO);
        assertThat(cancelado.getFinalizado()).isNotNull();
    }

    @Test
    void noCancelaUnJobFinalizado() {
        when(repository.findWithLockByJobId(1L)).thenReturn(Optional.of(job(AsientoIngresoJobEstado.COMPLETADO)));

        assertThatThrownBy(() -> useCase.cancelar(1L))
                .isInstanceOf(AsientoIngresoException.class)
                .hasMessageContaining("finalizado");
    }

    @Test
    void jobInexistenteDevuelveNoEncontrado() {
        when(repository.findWithLockByJobId(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.cancelar(9L))
                .isInstanceOf(AsientoIngresoException.class)
                .hasMessageContaining("No existe el job 9");
    }

}
