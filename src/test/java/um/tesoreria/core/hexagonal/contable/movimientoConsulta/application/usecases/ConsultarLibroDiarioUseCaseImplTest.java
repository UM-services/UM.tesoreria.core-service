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
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.TopeMovimientos;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LibroDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LineaDiario;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
class ConsultarLibroDiarioUseCaseImplTest {

    static final OffsetDateTime DIA_1 = OffsetDateTime.parse("2025-03-01T00:00:00Z");
    static final OffsetDateTime DIA_2 = OffsetDateTime.parse("2025-03-02T00:00:00Z");
    static final OffsetDateTime DIA_3 = OffsetDateTime.parse("2025-03-03T00:00:00Z");

    @Mock CuentaMovimientoService cuentaMovimientoService;
    @Mock CuentaService cuentaService;
    @Mock ComprobanteService comprobanteService;
    @Mock ProveedorService proveedorService;

    ConsultarLibroDiarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultarLibroDiarioUseCaseImpl(cuentaMovimientoService,
                new ArmadoAsientos(cuentaService, comprobanteService, proveedorService), new TopeMovimientos(10, 400));
        lenient().when(comprobanteService.findAll()).thenReturn(List.of(
                Comprobante.builder().comprobanteId(3).descripcion("Factura B").build()));
        lenient().when(cuentaService.findAllByNumeroCuentaIn(anyList())).thenReturn(List.of(
                Cuenta.builder().numeroCuenta(new BigDecimal("11101010001")).nombre("Caja").build(),
                Cuenta.builder().numeroCuenta(new BigDecimal("21101010001.00")).nombre("Proveedores").build()));
        lenient().when(proveedorService.getNombres(anyCollection())).thenReturn(List.of(
                new ProveedorNombre(7, "Proveedor Siete")));
    }

    static LineaMovimiento linea(long id, OffsetDateTime fecha, int orden, int item, String cuenta, int debita,
                                 String importe, Integer comprobanteId, Integer proveedorId) {
        return new LineaMovimiento(id, fecha, orden, item, new BigDecimal(cuenta), (byte) debita,
                new BigDecimal(importe), "concepto " + id, comprobanteId, proveedorId, (byte) 0);
    }

    @Test
    void agrupaPorAsientoConNombresYTotales() {
        when(cuentaMovimientoService.countByFechaContable(DIA_1, DIA_3)).thenReturn(4L);
        when(cuentaMovimientoService.findLineasByFechaContable(DIA_1, DIA_3, 11)).thenReturn(List.of(
                linea(1, DIA_1, 1, 1, "11101010001", 1, "100.00", 3, 7),
                linea(2, DIA_1, 1, 2, "21101010001", 0, "100.00", 3, 0),
                linea(3, DIA_1, 2, 1, "11101010001", 1, "50.25", null, null),
                linea(4, DIA_2, 1, 1, "99999999999", 0, "10.00", 99, 8)));

        LibroDiario libro = useCase.consultarLibroDiario(DIA_1, DIA_2);

        assertThat(libro.asientos()).extracting(AsientoDiario::fechaContable, AsientoDiario::ordenContable)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(DIA_1, 1),
                        org.assertj.core.groups.Tuple.tuple(DIA_1, 2),
                        org.assertj.core.groups.Tuple.tuple(DIA_2, 1));
        AsientoDiario primero = libro.asientos().getFirst();
        assertThat(primero.totalDebe()).isEqualByComparingTo("100.00");
        assertThat(primero.totalHaber()).isEqualByComparingTo("100.00");

        LineaDiario debe = primero.lineas().get(0);
        assertThat(debe.nombreCuenta()).isEqualTo("Caja");
        assertThat(debe.comprobante()).isEqualTo("Factura B");
        assertThat(debe.proveedor()).isEqualTo("Proveedor Siete");
        assertThat(debe.debe()).isEqualByComparingTo("100.00");
        assertThat(debe.haber()).isNull();

        LineaDiario haber = primero.lineas().get(1);
        assertThat(haber.nombreCuenta()).as("la escala del número no importa").isEqualTo("Proveedores");
        assertThat(haber.proveedor()).as("proveedor 0 es sin proveedor").isNull();
        assertThat(haber.debe()).isNull();
        assertThat(haber.haber()).isEqualByComparingTo("100.00");

        LineaDiario sinNombres = libro.asientos().get(2).lineas().getFirst();
        assertThat(sinNombres.nombreCuenta()).isNull();
        assertThat(sinNombres.comprobante()).isNull();
        assertThat(sinNombres.proveedor()).isNull();

        assertThat(libro.totalDebe()).isEqualByComparingTo("150.25");
        assertThat(libro.totalHaber()).isEqualByComparingTo("110.00");
        assertThat(libro.desde()).isEqualTo(DIA_1);
        assertThat(libro.hasta()).isEqualTo(DIA_2);
        verify(proveedorService).getNombres(Set.of(7, 8));
    }

    @Test
    void rangoSinMovimientosDevuelveListaVaciaYTotalesEnCero() {
        when(cuentaMovimientoService.countByFechaContable(any(), any())).thenReturn(0L);
        when(cuentaMovimientoService.findLineasByFechaContable(any(), any(), anyInt())).thenReturn(List.of());

        LibroDiario libro = useCase.consultarLibroDiario(DIA_1, DIA_1);

        assertThat(libro.asientos()).isEmpty();
        assertThat(libro.totalDebe()).isEqualByComparingTo("0");
        assertThat(libro.totalHaber()).isEqualByComparingTo("0");
        verifyNoInteractions(cuentaService, comprobanteService, proveedorService);
    }

    @Test
    void tomaElDiaDeCalendarioRecibidoConCualquierHoraYOffset() {
        when(cuentaMovimientoService.countByFechaContable(DIA_1, DIA_3)).thenReturn(0L);
        when(cuentaMovimientoService.findLineasByFechaContable(DIA_1, DIA_3, 11)).thenReturn(List.of());

        useCase.consultarLibroDiario(OffsetDateTime.parse("2025-03-01T00:00:00-03:00"),
                OffsetDateTime.parse("2025-03-02T23:30:00-03:00"));
        useCase.consultarLibroDiario(OffsetDateTime.parse("2025-03-01T15:00:00Z"),
                OffsetDateTime.parse("2025-03-02T00:00:00Z"));

        verify(cuentaMovimientoService, org.mockito.Mockito.times(2)).findLineasByFechaContable(DIA_1, DIA_3, 11);
    }

    @Test
    void mismoDiaConHorasDistintasNoEsDesdePosterior() {
        when(cuentaMovimientoService.countByFechaContable(any(), any())).thenReturn(0L);
        when(cuentaMovimientoService.findLineasByFechaContable(any(), any(), anyInt())).thenReturn(List.of());

        useCase.consultarLibroDiario(OffsetDateTime.parse("2025-03-01T20:00:00-03:00"),
                OffsetDateTime.parse("2025-03-01T00:00:00-03:00"));

        verify(cuentaMovimientoService).findLineasByFechaContable(DIA_1, DIA_2, 11);
    }

    @Test
    void faltaUnaFechaEsFechasRequeridas() {
        assertThatThrownBy(() -> useCase.consultarLibroDiario(null, DIA_1))
                .isInstanceOf(MovimientoConsultaException.class)
                .extracting("codigo").isEqualTo(Codigo.FECHAS_REQUERIDAS);
        assertThatThrownBy(() -> useCase.consultarLibroDiario(DIA_1, null))
                .extracting("codigo").isEqualTo(Codigo.FECHAS_REQUERIDAS);
        verifyNoInteractions(cuentaMovimientoService);
    }

    @Test
    void desdePosteriorAHastaSeRechaza() {
        assertThatThrownBy(() -> useCase.consultarLibroDiario(DIA_2, DIA_1))
                .extracting("codigo").isEqualTo(Codigo.DESDE_POSTERIOR);
        verifyNoInteractions(cuentaMovimientoService);
    }

    @Test
    void masLineasQueElTopeSeRechazaSinLeerlas() {
        when(cuentaMovimientoService.countByFechaContable(any(), any())).thenReturn(11L);

        assertThatThrownBy(() -> useCase.consultarLibroDiario(DIA_1, DIA_2))
                .isInstanceOf(MovimientoConsultaException.class)
                .extracting("codigo").isEqualTo(Codigo.DEMASIADOS_MOVIMIENTOS);
        verify(cuentaMovimientoService, never()).findLineasByFechaContable(any(), any(), anyInt());
    }

    @Test
    void justoElTopeSeResponde() {
        when(cuentaMovimientoService.countByFechaContable(any(), any())).thenReturn(10L);
        when(cuentaMovimientoService.findLineasByFechaContable(any(), any(), anyInt())).thenReturn(List.of());

        assertThat(useCase.consultarLibroDiario(DIA_1, DIA_2).asientos()).isEmpty();
    }

    @Test
    void siLaLecturaTraeMasQueElTopeSeRechazaAunqueElConteoPase() {
        // Entre el conteo y la lectura otra transacción pudo insertar líneas.
        when(cuentaMovimientoService.countByFechaContable(any(), any())).thenReturn(10L);
        List<LineaMovimiento> once = java.util.stream.LongStream.rangeClosed(1, 11)
                .mapToObj(i -> linea(i, DIA_1, 1, (int) i, "11101010001", 1, "1.00", null, null))
                .toList();
        when(cuentaMovimientoService.findLineasByFechaContable(any(), any(), anyInt())).thenReturn(once);

        assertThatThrownBy(() -> useCase.consultarLibroDiario(DIA_1, DIA_1))
                .isInstanceOf(MovimientoConsultaException.class)
                .extracting("codigo").isEqualTo(Codigo.DEMASIADOS_MOVIMIENTOS);
        verifyNoInteractions(cuentaService, comprobanteService, proveedorService);
    }

    @Test
    void fechasFueraDelRangoDeDatetimeSeRechazanSin500() {
        assertThatThrownBy(() -> useCase.consultarLibroDiario(DIA_1, OffsetDateTime.parse("+999999999-12-31T00:00:00Z")))
                .isInstanceOf(MovimientoConsultaException.class)
                .extracting("codigo").hasToString("FECHA_FUERA_DE_RANGO");
        assertThatThrownBy(() -> useCase.consultarLibroDiario(OffsetDateTime.parse("0999-12-31T00:00:00Z"), DIA_1))
                .isInstanceOf(MovimientoConsultaException.class)
                .extracting("codigo").hasToString("FECHA_FUERA_DE_RANGO");
        assertThatThrownBy(() -> useCase.consultarLibroDiario(DIA_1, OffsetDateTime.parse("9999-12-31T00:00:00Z")))
                .isInstanceOf(MovimientoConsultaException.class)
                .extracting("codigo").hasToString("FECHA_FUERA_DE_RANGO");
        verifyNoInteractions(cuentaMovimientoService);
    }

    @Test
    void bordesValidosDelRangoDeDatetimeSeAceptan() {
        when(cuentaMovimientoService.countByFechaContable(any(), any())).thenReturn(0L);
        when(cuentaMovimientoService.findLineasByFechaContable(any(), any(), anyInt())).thenReturn(List.of());

        useCase.consultarLibroDiario(OffsetDateTime.parse("1000-01-01T00:00:00Z"),
                OffsetDateTime.parse("1000-01-01T00:00:00Z"));
        useCase.consultarLibroDiario(OffsetDateTime.parse("9999-12-30T00:00:00Z"),
                OffsetDateTime.parse("9999-12-30T00:00:00Z"));

        verify(cuentaMovimientoService).findLineasByFechaContable(OffsetDateTime.parse("1000-01-01T00:00:00Z"),
                OffsetDateTime.parse("1000-01-02T00:00:00Z"), 11);
        verify(cuentaMovimientoService).findLineasByFechaContable(OffsetDateTime.parse("9999-12-30T00:00:00Z"),
                OffsetDateTime.parse("9999-12-31T00:00:00Z"), 11);
    }

    @Test
    void rangoDeMasDiasQueElMaximoSeRechazaSinContar() {
        // 2025-03-01 a 2026-04-04 son 400 días; un día más pasa el máximo.
        assertThatThrownBy(() -> useCase.consultarLibroDiario(OffsetDateTime.parse("2025-03-01T00:00:00Z"),
                OffsetDateTime.parse("2026-04-05T00:00:00Z")))
                .isInstanceOf(MovimientoConsultaException.class)
                .extracting("codigo").hasToString("RANGO_DEMASIADO_LARGO");
        verifyNoInteractions(cuentaMovimientoService);
    }

    @Test
    void rangoDeExactamenteElMaximoDeDiasSeAcepta() {
        when(cuentaMovimientoService.countByFechaContable(any(), any())).thenReturn(0L);
        when(cuentaMovimientoService.findLineasByFechaContable(any(), any(), anyInt())).thenReturn(List.of());

        useCase.consultarLibroDiario(OffsetDateTime.parse("2025-03-01T00:00:00Z"),
                OffsetDateTime.parse("2026-04-04T00:00:00Z"));

        verify(cuentaMovimientoService).countByFechaContable(OffsetDateTime.parse("2025-03-01T00:00:00Z"),
                OffsetDateTime.parse("2026-04-05T00:00:00Z"));
    }

    @Test
    void debitaDistintoDeUnoCuentaComoHaberIgualQueElMayor() {
        when(cuentaMovimientoService.countByFechaContable(any(), any())).thenReturn(1L);
        when(cuentaMovimientoService.findLineasByFechaContable(any(), any(), anyInt())).thenReturn(List.of(
                linea(1, DIA_1, 1, 1, "11101010001", 2, "5.00", null, null)));

        LibroDiario libro = useCase.consultarLibroDiario(DIA_1, DIA_1);

        LineaDiario unica = libro.asientos().getFirst().lineas().getFirst();
        assertThat(unica.debe()).isNull();
        assertThat(unica.haber()).isEqualByComparingTo("5.00");
        assertThat(libro.totalHaber()).isEqualByComparingTo("5.00");
    }
}
