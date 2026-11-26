package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.proveedor.application.service.ProveedorService;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.ProveedorNombre;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException.Codigo;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.ArmadoAsientos;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.LecturaRango;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.TopeMovimientos;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LineaMovimientoContable;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.MovimientosContables;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarMovimientosUseCaseImplTest {

    static final OffsetDateTime DIA_1 = OffsetDateTime.parse("2025-03-01T00:00:00Z");
    static final OffsetDateTime DIA_2 = OffsetDateTime.parse("2025-03-02T00:00:00Z");
    static final OffsetDateTime DIA_3 = OffsetDateTime.parse("2025-03-03T00:00:00Z");

    @Mock CuentaMovimientoService cuentaMovimientoService;
    @Mock CuentaService cuentaService;
    @Mock ComprobanteService comprobanteService;
    @Mock ProveedorService proveedorService;

    ConsultarMovimientosUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultarMovimientosUseCaseImpl(
                new LecturaRango(cuentaMovimientoService, new TopeMovimientos(10, 400)),
                new ArmadoAsientos(cuentaService, comprobanteService, proveedorService));
        lenient().when(comprobanteService.findAll()).thenReturn(List.of(
                Comprobante.builder().comprobanteId(3).descripcion("Factura B").build()));
        lenient().when(cuentaService.findAllByNumeroCuentaIn(anyList())).thenReturn(List.of(
                Cuenta.builder().numeroCuenta(new BigDecimal("11101010001")).nombre("Caja").build()));
        lenient().when(proveedorService.getNombres(anyCollection())).thenReturn(List.of(
                new ProveedorNombre(7, "Proveedor Siete", "30-70000007-1")));
    }

    static LineaMovimiento linea(long id, OffsetDateTime fecha, int orden, int item, int debita, String importe,
                                 Integer proveedorId) {
        return new LineaMovimiento(id, fecha, orden, item, new BigDecimal("11101010001"), (byte) debita,
                new BigDecimal(importe), "concepto " + id, 3, proveedorId, (byte) 0);
    }

    @Test
    void lineasSueltasEnOrdenConProveedorYCuitYTotales() {
        when(cuentaMovimientoService.countByFechaContable(DIA_1, DIA_3)).thenReturn(3L);
        when(cuentaMovimientoService.findLineasByFechaContable(DIA_1, DIA_3, 11)).thenReturn(List.of(
                linea(1, DIA_1, 1, 1, 1, "100.00", 7),
                linea(2, DIA_1, 1, 2, 0, "99.99", 0),
                linea(3, DIA_2, 1, 1, 0, "0.01", 8)));

        MovimientosContables movimientos = useCase.consultarMovimientos(DIA_1, DIA_2);

        assertThat(movimientos.lineas())
                .extracting(LineaMovimientoContable::fechaContable, LineaMovimientoContable::ordenContable,
                        LineaMovimientoContable::item)
                .containsExactly(tuple(DIA_1, 1, 1), tuple(DIA_1, 1, 2), tuple(DIA_2, 1, 1));
        LineaMovimientoContable primera = movimientos.lineas().getFirst();
        assertThat(primera.nombreCuenta()).isEqualTo("Caja");
        assertThat(primera.comprobante()).isEqualTo("Factura B");
        assertThat(primera.proveedorId()).isEqualTo(7);
        assertThat(primera.proveedor()).isEqualTo("Proveedor Siete");
        assertThat(primera.cuitProveedor()).isEqualTo("30-70000007-1");
        assertThat(primera.debe()).isEqualByComparingTo("100.00");
        assertThat(primera.haber()).isNull();
        LineaMovimientoContable sinProveedor = movimientos.lineas().get(1);
        assertThat(sinProveedor.proveedorId()).isNull();
        assertThat(sinProveedor.cuitProveedor()).isNull();
        assertThat(movimientos.lineas().get(2).proveedor()).as("proveedor inexistente").isNull();
        assertThat(movimientos.totalDebe()).isEqualByComparingTo("100.00");
        assertThat(movimientos.totalHaber()).isEqualByComparingTo("100.00");
        verify(proveedorService).getNombres(Set.of(7, 8));
    }

    @Test
    void rangoSinMovimientosDevuelveListaVacia() {
        when(cuentaMovimientoService.countByFechaContable(any(), any())).thenReturn(0L);
        when(cuentaMovimientoService.findLineasByFechaContable(any(), any(), anyInt())).thenReturn(List.of());

        MovimientosContables movimientos = useCase.consultarMovimientos(DIA_1, DIA_1);

        assertThat(movimientos.lineas()).isEmpty();
        assertThat(movimientos.totalDebe()).isEqualByComparingTo("0");
        verifyNoInteractions(cuentaService, comprobanteService, proveedorService);
    }

    @Test
    void mismosRechazosQueElLibroDiario() {
        assertThatThrownBy(() -> useCase.consultarMovimientos(DIA_1, null))
                .isInstanceOf(MovimientoConsultaException.class)
                .extracting("codigo").isEqualTo(Codigo.FECHAS_REQUERIDAS);
        assertThatThrownBy(() -> useCase.consultarMovimientos(DIA_2, DIA_1))
                .extracting("codigo").isEqualTo(Codigo.DESDE_POSTERIOR);
        verifyNoInteractions(cuentaMovimientoService);

        when(cuentaMovimientoService.countByFechaContable(any(), any())).thenReturn(11L);
        assertThatThrownBy(() -> useCase.consultarMovimientos(DIA_1, DIA_2))
                .extracting("codigo").isEqualTo(Codigo.DEMASIADOS_MOVIMIENTOS);
        verify(cuentaMovimientoService, never()).findLineasByFechaContable(any(), any(), anyInt());
    }
}
