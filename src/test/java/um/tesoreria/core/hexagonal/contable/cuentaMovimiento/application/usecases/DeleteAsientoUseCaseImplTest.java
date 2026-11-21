package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.exception.AsientoConflictoException;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteAsientoUseCaseImplTest {

    private static final OffsetDateTime FECHA =
            OffsetDateTime.of(2026, 11, 13, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final Integer ORDEN = 70;

    @Mock CuentaMovimientoRepository repository;

    DeleteAsientoUseCaseImpl useCase;

    @BeforeEach
    void preparar() {
        useCase = new DeleteAsientoUseCaseImpl(repository);
    }

    private CuentaMovimiento linea(Long id, Integer comprobanteId) {
        return CuentaMovimiento.builder()
                .cuentaMovimientoId(id)
                .comprobanteId(comprobanteId)
                .numeroCuenta(new BigDecimal("10102040001"))
                .importe(new BigDecimal("100.00"))
                .build();
    }

    @Test
    void borraSoloLasLineasDelComprobante() {
        when(repository.findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(FECHA, ORDEN, 0))
                .thenReturn(List.of(linea(1L, 1), linea(2L, 1), linea(3L, 1)));

        useCase.deleteAsiento(FECHA, ORDEN, 1);

        verify(repository).deleteAllByCuentaMovimientoIdIn(List.of(1L, 2L, 3L));
    }

    @Test
    void abortaSiHayLineasDeOtroComprobante() {
        when(repository.findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(FECHA, ORDEN, 0))
                .thenReturn(List.of(linea(1L, 1), linea(2L, 6)));

        assertThatThrownBy(() -> useCase.deleteAsiento(FECHA, ORDEN, 1))
                .isInstanceOf(AsientoConflictoException.class)
                .hasMessageContaining("otro comprobante");

        verify(repository, never()).deleteAllByCuentaMovimientoIdIn(any());
    }

    @Test
    void sinLineasNoBorraNada() {
        when(repository.findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(FECHA, ORDEN, 0))
                .thenReturn(List.of());

        useCase.deleteAsiento(FECHA, ORDEN, 1);

        verify(repository, never()).deleteAllByCuentaMovimientoIdIn(any());
    }

    @Test
    void unaLineaSinComprobanteSeConsideraAjena() {
        when(repository.findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(FECHA, ORDEN, 0))
                .thenReturn(List.of(linea(1L, 1), linea(2L, null)));

        assertThatThrownBy(() -> useCase.deleteAsiento(FECHA, ORDEN, 1))
                .isInstanceOf(AsientoConflictoException.class);
    }

}
