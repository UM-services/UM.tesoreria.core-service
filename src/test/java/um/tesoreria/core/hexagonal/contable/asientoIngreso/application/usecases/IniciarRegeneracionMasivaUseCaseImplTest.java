package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTarea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.AsientoIngresoJobRepository;
import um.tesoreria.core.kotlin.model.TipoPago;
import um.tesoreria.core.service.TipoPagoService;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IniciarRegeneracionMasivaUseCaseImplTest {

    @Mock AsientoIngresoJobRepository repository;
    @Mock TipoPagoService tipoPagoService;

    private IniciarRegeneracionMasivaUseCaseImpl useCase(int maxDias) {
        return new IniciarRegeneracionMasivaUseCaseImpl(repository, tipoPagoService,
                TransactionOperations.withoutTransaction(), maxDias);
    }

    @BeforeEach
    void preparar() {
        lenient().when(tipoPagoService.findAll()).thenReturn(List.of(
                new TipoPago(3, "Efectivo"),
                new TipoPago(18, "Mercado Pago")));
        lenient().when(repository.create(any(), anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void expandeDiasPorTiposDePago() {
        OffsetDateTime desde = OffsetDateTime.of(2026, 11, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime hasta = OffsetDateTime.of(2026, 11, 2, 0, 0, 0, 0, ZoneOffset.UTC);

        AsientoIngresoJob job = useCase(366).iniciar(desde, hasta, 99L);

        assertThat(job.getEstado()).isEqualTo(AsientoIngresoJobEstado.PENDIENTE);
        assertThat(job.getTotalTareas()).isEqualTo(4);
        assertThat(job.getTareasPendientes()).isEqualTo(4);
        assertThat(job.getUsuarioId()).isEqualTo(99L);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AsientoIngresoJobTarea>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).create(any(), captor.capture());
        assertThat(captor.getValue()).hasSize(4);
        assertThat(captor.getValue().get(0).getFechaContable()).isEqualTo(desde);
        assertThat(captor.getValue().get(0).getTipoPagoId()).isEqualTo(3);
        assertThat(captor.getValue().get(3).getFechaContable()).isEqualTo(hasta);
    }

    @Test
    void rangoInvertidoSeRechaza() {
        OffsetDateTime desde = OffsetDateTime.of(2026, 11, 2, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime hasta = OffsetDateTime.of(2026, 11, 1, 0, 0, 0, 0, ZoneOffset.UTC);

        assertThatThrownBy(() -> useCase(366).iniciar(desde, hasta, null))
                .isInstanceOf(AsientoIngresoException.class)
                .hasMessageContaining("posterior");
    }

    @Test
    void rangoMayorAlTopeSeRechaza() {
        OffsetDateTime desde = OffsetDateTime.of(2026, 11, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime hasta = OffsetDateTime.of(2026, 11, 3, 0, 0, 0, 0, ZoneOffset.UTC);

        assertThatThrownBy(() -> useCase(1).iniciar(desde, hasta, null))
                .isInstanceOf(AsientoIngresoException.class)
                .hasMessageContaining("máximo");
    }

}
