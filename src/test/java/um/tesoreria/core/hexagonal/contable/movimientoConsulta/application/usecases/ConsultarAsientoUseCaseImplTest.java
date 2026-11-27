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
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LineaDiario;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarAsientoUseCaseImplTest {

    static final OffsetDateTime DIA = OffsetDateTime.parse("2025-03-01T00:00:00Z");
    static final OffsetDateTime DIA_SIGUIENTE = OffsetDateTime.parse("2025-03-02T00:00:00Z");

    @Mock CuentaMovimientoService cuentaMovimientoService;
    @Mock CuentaService cuentaService;
    @Mock ComprobanteService comprobanteService;
    @Mock ProveedorService proveedorService;

    ConsultarAsientoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultarAsientoUseCaseImpl(cuentaMovimientoService,
                new ArmadoAsientos(cuentaService, comprobanteService, proveedorService));
        lenient().when(comprobanteService.findAll()).thenReturn(List.of(
                Comprobante.builder().comprobanteId(3).descripcion("Factura B").build()));
        lenient().when(cuentaService.findAllByNumeroCuentaIn(anyList())).thenReturn(List.of(
                Cuenta.builder().numeroCuenta(new BigDecimal("11101010001")).nombre("Caja").build(),
                Cuenta.builder().numeroCuenta(new BigDecimal("21101010001")).nombre("Proveedores").build()));
        lenient().when(proveedorService.getNombres(anyCollection())).thenReturn(List.of(
                new ProveedorNombre(7, "Proveedor Siete")));
    }

    static LineaMovimiento linea(long id, int item, String cuenta, int debita, String importe, Integer proveedorId) {
        return new LineaMovimiento(id, DIA, 4, item, new BigDecimal(cuenta), (byte) debita,
                new BigDecimal(importe), "concepto " + id, 3, proveedorId, (byte) 0);
    }

    @Test
    void devuelveLasLineasConNombresYTotales() {
        when(cuentaMovimientoService.findLineasByAsiento(DIA, DIA_SIGUIENTE, 4)).thenReturn(List.of(
                linea(1, 1, "11101010001", 1, "100.10", 7),
                linea(2, 2, "21101010001", 0, "60.05", 0),
                linea(3, 3, "21101010001", 0, "40.05", null)));

        AsientoDiario asiento = useCase.consultarAsiento(DIA, 4);

        assertThat(asiento.fechaContable()).isEqualTo(DIA);
        assertThat(asiento.ordenContable()).isEqualTo(4);
        assertThat(asiento.lineas()).extracting(LineaDiario::item).containsExactly(1, 2, 3);
        LineaDiario debe = asiento.lineas().getFirst();
        assertThat(debe.nombreCuenta()).isEqualTo("Caja");
        assertThat(debe.comprobante()).isEqualTo("Factura B");
        assertThat(debe.proveedor()).isEqualTo("Proveedor Siete");
        assertThat(debe.debe()).isEqualByComparingTo("100.10");
        assertThat(asiento.lineas().get(1).proveedor()).isNull();
        assertThat(asiento.totalDebe()).isEqualByComparingTo("100.10");
        assertThat(asiento.totalHaber()).isEqualByComparingTo("100.10");
    }

    @Test
    void tomaElDiaDeCalendarioConCualquierHoraYOffset() {
        when(cuentaMovimientoService.findLineasByAsiento(DIA, DIA_SIGUIENTE, 4))
                .thenReturn(List.of(linea(1, 1, "11101010001", 1, "1.00", null)));

        useCase.consultarAsiento(OffsetDateTime.parse("2025-03-01T00:00:00-03:00"), 4);
        useCase.consultarAsiento(OffsetDateTime.parse("2025-03-01T21:30:00-03:00"), 4);
        useCase.consultarAsiento(OffsetDateTime.parse("2025-03-01T15:00:00Z"), 4);

        verify(cuentaMovimientoService, times(3)).findLineasByAsiento(DIA, DIA_SIGUIENTE, 4);
    }

    @Test
    void asientoSinLineasEsInexistente() {
        when(cuentaMovimientoService.findLineasByAsiento(any(), any(), eq(9))).thenReturn(List.of());

        assertThatThrownBy(() -> useCase.consultarAsiento(DIA, 9))
                .isInstanceOf(MovimientoConsultaException.class)
                .extracting("codigo").isEqualTo(Codigo.ASIENTO_INEXISTENTE);
        verifyNoInteractions(cuentaService, comprobanteService, proveedorService);
    }

    @Test
    void ordenCeroNegativoONuloEsOrdenInvalido() {
        for (Integer orden : new Integer[]{0, -1, null}) {
            assertThatThrownBy(() -> useCase.consultarAsiento(DIA, orden))
                    .isInstanceOf(MovimientoConsultaException.class)
                    .extracting("codigo").isEqualTo(Codigo.ORDEN_INVALIDO);
        }
        verifyNoInteractions(cuentaMovimientoService);
    }

    @Test
    void sinFechaEsFechasRequeridas() {
        assertThatThrownBy(() -> useCase.consultarAsiento(null, 1))
                .extracting("codigo").isEqualTo(Codigo.FECHAS_REQUERIDAS);
        verifyNoInteractions(cuentaMovimientoService);
    }

    @Test
    void fechaFueraDelRangoDeDatetimeSeRechaza() {
        assertThatThrownBy(() -> useCase.consultarAsiento(OffsetDateTime.parse("0999-12-31T00:00:00Z"), 1))
                .extracting("codigo").isEqualTo(Codigo.FECHA_FUERA_DE_RANGO);
        assertThatThrownBy(() -> useCase.consultarAsiento(OffsetDateTime.parse("9999-12-31T00:00:00Z"), 1))
                .extracting("codigo").isEqualTo(Codigo.FECHA_FUERA_DE_RANGO);
        verifyNoInteractions(cuentaMovimientoService);
    }
}
