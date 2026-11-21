package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Paridad del motor de asientos con {@code clsCtlOPago.makeAsientosOrdenPago}. Los datos usan
 * los casos reales de las OP 0015-00004086/4087 del 2026-11-13.
 */
class AsientoPropuestoBuilderTest {

    private static final OffsetDateTime REFERENCIA = OffsetDateTime.of(2026, 11, 13, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime FECHA_FACTURA = OffsetDateTime.of(2026, 11, 10, 0, 0, 0, 0, ZoneOffset.UTC);

    @Test
    void unaFacturaYaContabilizadaSeProponeEnSuPropioAsiento() {
        OrdenPagoContexto contexto = contexto(List.of(factura(FECHA_FACTURA, 20)), List.of(), List.of());

        List<AsientoPropuesto> asientos = AsientoPropuestoBuilder.buildOrdenPago(contexto, false);

        assertThat(asientos).hasSize(1);
        AsientoPropuesto asiento = asientos.getFirst();
        assertThat(asiento.fechaContable()).isEqualTo(FECHA_FACTURA);
        assertThat(asiento.ordenContable()).isEqualTo(20);
        assertThat(asiento.origenes()).containsExactly(new OrigenAsiento(OrigenAsiento.Tipo.FACTURA, 104222L));
        assertThat(asiento.lineas()).hasSize(2);

        LineaPropuesta proveedor = asiento.lineas().getFirst();
        assertThat(proveedor.numeroCuenta()).isEqualByComparingTo("20101090099");
        assertThat(proveedor.debita()).isZero();
        assertThat(proveedor.importe()).isEqualByComparingTo("555854.06");
        assertThat(proveedor.comprobanteId()).isEqualTo(4);
        assertThat(proveedor.concepto()).isEqualTo("Pago 0015-00004086");

        LineaPropuesta articulo = asiento.lineas().get(1);
        assertThat(articulo.numeroCuenta()).isEqualByComparingTo("40102980004");
        assertThat(articulo.debita()).isEqualTo((byte) 1);
        assertThat(articulo.importe()).isEqualByComparingTo("555854.06");
        assertThat(articulo.comprobanteId()).isEqualTo(4);
        assertThat(asiento.cuadra()).isTrue();
    }

    @Test
    void unaFacturaSinAsientoProponeOrdenNuloConLaFechaDeLaReferencia() {
        OrdenPagoContexto contexto = contexto(List.of(factura(null, 0)), List.of(), List.of());

        AsientoPropuesto asiento = AsientoPropuestoBuilder.buildOrdenPago(contexto, false).getFirst();

        assertThat(asiento.fechaContable()).isEqualTo(REFERENCIA);
        assertThat(asiento.ordenContable()).isNull();
    }

    @Test
    void cadaValorSinAsientoGeneraSuPropioAsientoConElComprobanteDeLaOrdenDePago() {
        OrdenPagoContexto contexto = contexto(List.of(), List.of(), List.of(
                valor(0, "4707.67", "2026-11-13"),
                valor(1, "551146.39", "2026-11-13")));

        List<AsientoPropuesto> asientos = AsientoPropuestoBuilder.buildOrdenPago(contexto, false);

        assertThat(asientos).hasSize(2);
        for (AsientoPropuesto asiento : asientos) {
            assertThat(asiento.ordenContable()).isNull();
            assertThat(asiento.lineas()).hasSize(2);
            assertThat(asiento.lineas()).allMatch(linea -> linea.comprobanteId().equals(6));
            assertThat(asiento.lineas().getFirst().numeroCuenta()).isEqualByComparingTo("20101090099");
            assertThat(asiento.lineas().getFirst().debita()).isEqualTo((byte) 1);
            assertThat(asiento.lineas().get(1).debita()).isZero();
            assertThat(asiento.cuadra()).isTrue();
        }
        assertThat(asientos.getFirst().lineas().getFirst().importe()).isEqualByComparingTo("4707.67");
        assertThat(asientos.get(1).lineas().getFirst().importe()).isEqualByComparingTo("551146.39");
    }

    @Test
    void soloSinAsientoDejaAfueraLosHijosYaContabilizados() {
        OrdenPagoContexto contexto = contexto(List.of(factura(FECHA_FACTURA, 20)), List.of(), List.of(
                valor(0, "4707.67", "2026-11-13")));

        assertThat(AsientoPropuestoBuilder.buildOrdenPago(contexto, true))
                .singleElement()
                .satisfies(asiento -> assertThat(asiento.origenes())
                        .containsExactly(new OrigenAsiento(OrigenAsiento.Tipo.VALOR, 0L)));
    }

    @Test
    void laAsignacionDeCostosUsaElComprobanteUnoYLaCuentaDeLaUbicacion() {
        ContextoEntregaDetalle detalle = new ContextoEntregaDetalle(32, 15L, new BigDecimal("1250.50"),
                new BigDecimal("40102980004"), new BigDecimal("40102020024"), "Rectorado", "Insumos");
        ContextoEntrega entrega = new ContextoEntrega(92492L, 4, (byte) 0, 4484, FECHA_FACTURA, 29, List.of(detalle));
        OrdenPagoContexto contexto = contexto(List.of(), List.of(entrega), List.of());

        AsientoPropuesto asiento = AsientoPropuestoBuilder.buildOrdenPago(contexto, false).getFirst();

        assertThat(asiento.fechaContable()).isEqualTo(FECHA_FACTURA);
        assertThat(asiento.ordenContable()).isEqualTo(29);
        assertThat(asiento.lineas()).hasSize(2);
        assertThat(asiento.lineas()).allMatch(linea -> linea.comprobanteId().equals(1));
        assertThat(asiento.lineas()).allMatch(linea -> linea.concepto().equals("Pago 0015-00004086 - Asignacion de Costos"));
        assertThat(asiento.lineas().getFirst().numeroCuenta()).isEqualByComparingTo("40102980004");
        assertThat(asiento.lineas().getFirst().debita()).isZero();
        assertThat(asiento.lineas().get(1).numeroCuenta()).isEqualByComparingTo("40102020024");
        assertThat(asiento.lineas().get(1).debita()).isEqualTo((byte) 1);
    }

    @Test
    void sinCuentaDeUbicacionElConceptoEsUbicacionBarraArticulo() {
        ContextoEntregaDetalle detalle = new ContextoEntregaDetalle(32, 15L, new BigDecimal("10"),
                new BigDecimal("40102980004"), null, "Rectorado", "Insumos");
        ContextoEntrega entrega = new ContextoEntrega(92492L, 4, (byte) 0, 4484, FECHA_FACTURA, 29, List.of(detalle));
        OrdenPagoContexto contexto = contexto(List.of(), List.of(entrega), List.of());

        AsientoPropuesto asiento = AsientoPropuestoBuilder.buildOrdenPago(contexto, false).getFirst();

        assertThat(asiento.lineas()).allMatch(linea -> linea.concepto().equals("Rectorado/Insumos"));
    }

    @Test
    void unArticuloSinPrecioFinalUsaElImporteDeLaFactura() {
        ContextoArticulo articulo = new ContextoArticulo(new BigDecimal("40102980004"), null);
        ContextoFactura factura = new ContextoFactura(104222L, 4, (byte) 0, 4484,
                new BigDecimal("555854.06"), new BigDecimal("20101090099"), FECHA_FACTURA, 20, List.of(articulo));
        OrdenPagoContexto contexto = contexto(List.of(factura), List.of(), List.of());

        AsientoPropuesto asiento = AsientoPropuestoBuilder.buildOrdenPago(contexto, false).getFirst();

        assertThat(asiento.lineas().get(1).importe()).isEqualByComparingTo("555854.06");
    }

    private OrdenPagoContexto contexto(List<ContextoFactura> facturas, List<ContextoEntrega> entregas,
                                       List<ContextoValor> valores) {
        return new OrdenPagoContexto(4484, 6, (byte) 1, new BigDecimal("20101090099"), 15, 4086L,
                REFERENCIA, null, facturas, entregas, valores);
    }

    private ContextoFactura factura(OffsetDateTime fechaContable, Integer ordenContable) {
        ContextoArticulo articulo = new ContextoArticulo(new BigDecimal("40102980004"), new BigDecimal("555854.06"));
        return new ContextoFactura(104222L, 4, (byte) 0, 4484, new BigDecimal("555854.06"),
                new BigDecimal("20101090099"), fechaContable, ordenContable, List.of(articulo));
    }

    private ContextoValor valor(int posicion, String importe, String fechaEmision) {
        return new ContextoValor(null, posicion == 0 ? 6 : 5, new BigDecimal(importe),
                OffsetDateTime.parse(fechaEmision + "T00:00:00Z"), null, null,
                posicion == 0 ? new BigDecimal("20101010009") : new BigDecimal("10102040001"), posicion);
    }
}
