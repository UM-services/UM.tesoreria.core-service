package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.proveedor.application.service.ProveedorService;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException.Codigo;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.ArmadoAsientos;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.LecturaRango;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.TopeMovimientos;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoNoCuadrado;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientosNoCuadrados;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarNoCuadradosUseCaseImplTest {

    static final OffsetDateTime DIA_1 = OffsetDateTime.parse("2025-03-01T00:00:00Z");
    static final OffsetDateTime DIA_2 = OffsetDateTime.parse("2025-03-02T00:00:00Z");
    static final OffsetDateTime DIA_3 = OffsetDateTime.parse("2025-03-03T00:00:00Z");

    @Mock CuentaMovimientoService cuentaMovimientoService;
    @Mock CuentaService cuentaService;
    @Mock ComprobanteService comprobanteService;
    @Mock ProveedorService proveedorService;

    ConsultarNoCuadradosUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultarNoCuadradosUseCaseImpl(
                new LecturaRango(cuentaMovimientoService, new TopeMovimientos(10, 400)),
                new ArmadoAsientos(cuentaService, comprobanteService, proveedorService));
        lenient().when(cuentaService.findAllByNumeroCuentaIn(anyList())).thenReturn(List.of(
                Cuenta.builder().numeroCuenta(new BigDecimal("11101010001")).nombre("Caja").build()));
    }

    static LineaMovimiento linea(long id, OffsetDateTime fecha, int orden, int item, int debita, String importe) {
        return new LineaMovimiento(id, fecha, orden, item, new BigDecimal("11101010001"), (byte) debita,
                new BigDecimal(importe), "concepto " + id, null, null, (byte) 0);
    }

    @Test
    void listaSoloLosNoCuadradosConLaDiferenciaExacta() {
        when(cuentaMovimientoService.countByFechaContable(DIA_1, DIA_3)).thenReturn(7L);
        when(cuentaMovimientoService.findLineasByFechaContable(DIA_1, DIA_3, 11)).thenReturn(List.of(
                // Cuadrado, con escalas distintas: no se lista.
                linea(1, DIA_1, 1, 1, 1, "100.00"),
                linea(2, DIA_1, 1, 2, 0, "100"),
                // Debe mayor por un centavo.
                linea(3, DIA_1, 2, 1, 1, "50.01"),
                linea(4, DIA_1, 2, 2, 0, "50.00"),
                // Haber mayor, sin redondeo.
                linea(5, DIA_2, 1, 1, 1, "0.001"),
                linea(6, DIA_2, 1, 2, 0, "10.00"),
                // Sólo un lado.
                linea(7, DIA_2, 2, 1, 0, "3.33")));

        AsientosNoCuadrados resultado = useCase.consultarNoCuadrados(DIA_1, DIA_2);

        assertThat(resultado.asientos())
                .extracting(AsientoNoCuadrado::fechaContable, AsientoNoCuadrado::ordenContable)
                .containsExactly(tuple(DIA_1, 2), tuple(DIA_2, 1), tuple(DIA_2, 2));
        AsientoNoCuadrado centavo = resultado.asientos().getFirst();
        assertThat(centavo.totalDebe()).isEqualByComparingTo("50.01");
        assertThat(centavo.totalHaber()).isEqualByComparingTo("50.00");
        assertThat(centavo.diferencia()).isEqualByComparingTo("0.01");
        assertThat(centavo.lineas()).hasSize(2);
        assertThat(centavo.lineas().getFirst().nombreCuenta()).isEqualTo("Caja");
        assertThat(resultado.asientos().get(1).diferencia()).isEqualTo(new BigDecimal("-9.999"));
        assertThat(resultado.asientos().get(2).diferencia()).isEqualByComparingTo("-3.33");
        assertThat(resultado.desde()).isEqualTo(DIA_1);
        assertThat(resultado.hasta()).isEqualTo(DIA_2);
    }

    @Test
    void rangoSinDiferenciasDevuelveListaVacia() {
        when(cuentaMovimientoService.countByFechaContable(any(), any())).thenReturn(2L);
        when(cuentaMovimientoService.findLineasByFechaContable(any(), any(), anyInt())).thenReturn(List.of(
                linea(1, DIA_1, 1, 1, 1, "10.00"),
                linea(2, DIA_1, 1, 2, 0, "10.00")));

        assertThat(useCase.consultarNoCuadrados(DIA_1, DIA_1).asientos()).isEmpty();
    }

    @Test
    void mismosRechazosQueElLibroDiario() {
        assertThatThrownBy(() -> useCase.consultarNoCuadrados(null, DIA_1))
                .isInstanceOf(MovimientoConsultaException.class)
                .extracting("codigo").isEqualTo(Codigo.FECHAS_REQUERIDAS);
        assertThatThrownBy(() -> useCase.consultarNoCuadrados(DIA_2, DIA_1))
                .extracting("codigo").isEqualTo(Codigo.DESDE_POSTERIOR);
        verifyNoInteractions(cuentaMovimientoService);

        when(cuentaMovimientoService.countByFechaContable(any(), any())).thenReturn(11L);
        assertThatThrownBy(() -> useCase.consultarNoCuadrados(DIA_1, DIA_2))
                .extracting("codigo").isEqualTo(Codigo.DEMASIADOS_MOVIMIENTOS);
        verify(cuentaMovimientoService, never()).findLineasByFechaContable(any(), any(), anyInt());
    }
}
