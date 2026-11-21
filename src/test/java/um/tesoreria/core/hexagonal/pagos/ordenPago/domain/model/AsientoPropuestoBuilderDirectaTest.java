package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Paridad con {@code clsCtlOPago.makeAsientosDirecta} y {@code clsCtlCompras.saveDirecto}:
 * el encabezado de una orden de pago directa lleva los gastos al debe y la obligación al haber
 * con {@code comprobanteId = 1}, y cada valor genera su propio asiento.
 */
class AsientoPropuestoBuilderDirectaTest {

    private static final OffsetDateTime FECHA = OffsetDateTime.of(2026, 11, 21, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime FECHA_VALOR = OffsetDateTime.of(2026, 11, 25, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final BigDecimal OBLIGACION = new BigDecimal("20101090099");
    private static final BigDecimal GASTO_UNO = new BigDecimal("60101010001");
    private static final BigDecimal GASTO_DOS = new BigDecimal("60101020002");
    private static final BigDecimal BANCO = new BigDecimal("10102040001");
    private static final Long ORDEN_PAGO_ID = 104460L;

    @Test
    void proponeElEncabezadoConLosGastosAlDebeYLaObligacionAlHaber() {
        List<AsientoPropuesto> asientos = AsientoPropuestoBuilder.buildDirecta(contexto(null, null, List.of()), false);

        assertThat(asientos).hasSize(1);
        AsientoPropuesto encabezado = asientos.getFirst();

        assertThat(encabezado.fechaContable()).isEqualTo(FECHA);
        assertThat(encabezado.ordenContable()).isNull();
        assertThat(encabezado.origenes()).containsExactly(new OrigenAsiento(OrigenAsiento.Tipo.ENCABEZADO, ORDEN_PAGO_ID));
        assertThat(encabezado.lineas()).hasSize(3);
        assertThat(encabezado.lineas())
                .allSatisfy(linea -> {
                    assertThat(linea.comprobanteId()).isEqualTo(1);
                    assertThat(linea.concepto()).isEqualTo("Pago 0015-00004170");
                    assertThat(linea.proveedorId()).isNull();
                });

        assertLinea(encabezado.lineas().get(0), GASTO_UNO, (byte) 1, "100.00");
        assertLinea(encabezado.lineas().get(1), GASTO_DOS, (byte) 1, "50.00");
        assertLinea(encabezado.lineas().get(2), OBLIGACION, (byte) 0, "150.00");
        assertThat(encabezado.cuadra()).isTrue();
    }

    @Test
    void omiteElEncabezadoCuandoYaTieneAsientoYSePidenSoloLosFaltantes() {
        List<AsientoPropuesto> asientos = AsientoPropuestoBuilder.buildDirecta(contexto(FECHA, 3, List.of()), true);

        assertThat(asientos).isEmpty();
    }

    @Test
    void proponeUnAsientoPorValorConLaObligacionAlDebe() {
        ContextoValor valor = valor(84190L, FECHA_VALOR, null, null, "90.00", 0);

        List<AsientoPropuesto> asientos = AsientoPropuestoBuilder.buildDirecta(contexto(null, null, List.of(valor)), false);

        assertThat(asientos).hasSize(2);
        AsientoPropuesto asientoValor = asientos.get(1);
        assertThat(asientoValor.fechaContable()).isEqualTo(FECHA_VALOR);
        assertThat(asientoValor.ordenContable()).isNull();
        assertThat(asientoValor.origenes()).containsExactly(new OrigenAsiento(OrigenAsiento.Tipo.VALOR, 84190L));
        assertThat(asientoValor.lineas()).hasSize(2);
        assertLinea(asientoValor.lineas().get(0), OBLIGACION, (byte) 1, "90.00");
        assertLinea(asientoValor.lineas().get(1), BANCO, (byte) 0, "90.00");
        assertThat(asientoValor.lineas()).allSatisfy(linea -> assertThat(linea.comprobanteId()).isEqualTo(1));
        assertThat(asientoValor.cuadra()).isTrue();
    }

    @Test
    void fuerzaUnAsientoNuevoCuandoElValorComparteFechaYOrdenConElEncabezado() {
        ContextoValor valor = valor(84191L, FECHA_VALOR, FECHA, 3, "90.00", 0);

        List<AsientoPropuesto> asientos = AsientoPropuestoBuilder.buildDirecta(contexto(FECHA, 3, List.of(valor)), false);

        assertThat(asientos).hasSize(2);
        assertThat(asientos.get(0).ordenContable()).isEqualTo(3);
        AsientoPropuesto asientoValor = asientos.get(1);
        assertThat(asientoValor.ordenContable()).isNull();
        assertThat(asientoValor.fechaContable()).isEqualTo(FECHA_VALOR);
    }

    @Test
    void respetaElAsientoPropioDeUnValorQueYaEstabaContabilizado() {
        OffsetDateTime fechaContable = OffsetDateTime.of(2026, 11, 26, 0, 0, 0, 0, ZoneOffset.UTC);
        ContextoValor valor = valor(84192L, FECHA_VALOR, fechaContable, 7, "90.00", 0);

        List<AsientoPropuesto> asientos = AsientoPropuestoBuilder.buildDirecta(contexto(FECHA, 3, List.of(valor)), false);

        assertThat(asientos).hasSize(2);
        AsientoPropuesto asientoValor = asientos.get(1);
        assertThat(asientoValor.fechaContable()).isEqualTo(fechaContable);
        assertThat(asientoValor.ordenContable()).isEqualTo(7);
    }

    private OrdenPagoDirectaContexto contexto(OffsetDateTime fechaContable, Integer orden, List<ContextoValor> valores) {
        return new OrdenPagoDirectaContexto(6, (byte) 1, 15, 4170L, FECHA, fechaContable, orden, ORDEN_PAGO_ID,
                OBLIGACION, new BigDecimal("150.00"),
                List.of(new ContextoGasto(GASTO_UNO, new BigDecimal("100.00")),
                        new ContextoGasto(GASTO_DOS, new BigDecimal("50.00"))),
                valores);
    }

    private ContextoValor valor(Long valorMovimientoId, OffsetDateTime fechaEmision, OffsetDateTime fechaContable,
                                Integer ordenContable, String importe, Integer posicion) {
        return new ContextoValor(valorMovimientoId, 5, new BigDecimal(importe), fechaEmision, fechaContable,
                ordenContable, BANCO, posicion);
    }

    private void assertLinea(LineaPropuesta linea, BigDecimal cuenta, byte debita, String importe) {
        assertThat(linea.numeroCuenta()).isEqualByComparingTo(cuenta);
        assertThat(linea.debita()).isEqualTo(debita);
        assertThat(linea.importe()).isEqualByComparingTo(new BigDecimal(importe));
    }
}
