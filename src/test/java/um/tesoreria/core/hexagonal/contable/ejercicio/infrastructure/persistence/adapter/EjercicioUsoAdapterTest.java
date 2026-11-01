package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ExistsAsientoByFechaBetweenUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.ExistsByFechaContableBetweenUseCase;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EjercicioUsoAdapterTest {
    ExistsByFechaContableBetweenUseCase movimientos = mock(ExistsByFechaContableBetweenUseCase.class);
    ExistsAsientoByFechaBetweenUseCase asientos = mock(ExistsAsientoByFechaBetweenUseCase.class);
    EjercicioUsoAdapter adapter = new EjercicioUsoAdapter(movimientos, asientos);
    LocalDate desde = LocalDate.parse("2027-03-01");
    LocalDate hasta = LocalDate.parse("2028-02-29");
    OffsetDateTime inicio = OffsetDateTime.parse("2027-03-01T00:00:00Z");
    OffsetDateTime fin = OffsetDateTime.parse("2028-02-29T23:59:59Z");

    @Test
    void sinFilasEnNingunaTablaNoHayUso() {
        assertThat(adapter.hayMovimientos(desde, hasta)).isFalse();
        verify(movimientos).existsByFechaContableBetween(inicio, fin);
        verify(asientos).existsByFechaBetween(inicio, fin);
    }

    @Test
    void unMovimientoSinAsientoBasta() {
        when(movimientos.existsByFechaContableBetween(inicio, fin)).thenReturn(true);
        assertThat(adapter.hayMovimientos(desde, hasta)).isTrue();
    }

    @Test
    void unAsientoSinMovimientosBasta() {
        when(asientos.existsByFechaBetween(inicio, fin)).thenReturn(true);
        assertThat(adapter.hayMovimientos(desde, hasta)).isTrue();
    }
}
