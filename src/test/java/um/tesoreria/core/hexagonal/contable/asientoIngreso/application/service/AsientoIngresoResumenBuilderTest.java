package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoRegistro;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResumen;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsientoIngresoResumenBuilderTest {

    private static final OffsetDateTime FECHA = OffsetDateTime.of(2026, 11, 17, 0, 0, 0, 0, ZoneOffset.UTC);

    @Mock CuentaMovimientoService cuentaMovimientoService;
    @Mock AsientoService asientoService;
    @Mock CuentaService cuentaService;
    @Mock AsientoIngresoCatalogo catalogo;

    @InjectMocks AsientoIngresoResumenBuilder builder;

    @Test
    void sinRegistroDevuelveTotalesEnCeroYNoExiste() {
        when(catalogo.tipoPagoNombre(3)).thenReturn("Efectivo");

        AsientoIngresoResumen resumen = builder.build(FECHA, 3, null);

        assertThat(resumen.isExiste()).isFalse();
        assertThat(resumen.getTotalDebe()).isEqualByComparingTo("0");
        assertThat(resumen.getTotalHaber()).isEqualByComparingTo("0");
        assertThat(resumen.isCuadra()).isTrue();
        assertThat(resumen.getLineas()).isEmpty();
    }

    @Test
    void completaNombresPorLoteCuandoLaEntidadNoTraeLaAsociacion() {
        when(catalogo.tipoPagoNombre(3)).thenReturn("Efectivo");
        when(cuentaMovimientoService.findAllByAsiento(FECHA, 7, 0, 2)).thenReturn(List.of(
                CuentaMovimiento.builder()
                        .numeroCuenta(new BigDecimal("11100000001"))
                        .debita((byte) 1)
                        .importe(new BigDecimal("100.00"))
                        .build()));
        when(cuentaService.findAllByNumeroCuentaIn(List.of(new BigDecimal("11100000001"))))
                .thenReturn(List.of(Cuenta.builder()
                        .numeroCuenta(new BigDecimal("11100000001"))
                        .nombre("Caja")
                        .build()));
        when(asientoService.findByAsiento(FECHA, 7))
                .thenReturn(new AsientoEntity(1L, FECHA, 7, "Asiento Ingreso", null, null, null, null));

        AsientoIngresoResumen resumen = builder.build(FECHA, 3,
                AsientoIngresoRegistro.builder().fechaContable(FECHA).tipoPagoId(3).ordenContable(7).build());

        assertThat(resumen.getLineas()).extracting("nombreCuenta").containsExactly("Caja");
    }

    @Test
    void sumaDebeYHaberYOrdenaDesdeElCore() {
        when(catalogo.tipoPagoNombre(3)).thenReturn("Efectivo");
        when(cuentaMovimientoService.findAllByAsiento(FECHA, 7, 0, 2)).thenReturn(List.of(
                linea("11100000001", "Caja", (byte) 1, "100.00"),
                linea("41100000003", "Matrícula", (byte) 0, "60.00"),
                linea("41100000004", "Otro ingreso", (byte) 0, "40.00")));
        when(asientoService.findByAsiento(FECHA, 7))
                .thenReturn(new AsientoEntity(1L, FECHA, 7, "Asiento Ingreso", null, null, null, null));

        AsientoIngresoResumen resumen = builder.build(FECHA, 3,
                AsientoIngresoRegistro.builder().fechaContable(FECHA).tipoPagoId(3).ordenContable(7).build());

        assertThat(resumen.isExiste()).isTrue();
        assertThat(resumen.getOrdenContable()).isEqualTo(7);
        assertThat(resumen.getTotalDebe()).isEqualByComparingTo("100.00");
        assertThat(resumen.getTotalHaber()).isEqualByComparingTo("100.00");
        assertThat(resumen.isCuadra()).isTrue();
        assertThat(resumen.getLineas()).extracting("nombreCuenta")
                .containsExactly("Caja", "Matrícula", "Otro ingreso");
    }

    private static CuentaMovimiento linea(String numero, String nombre, byte debita, String importe) {
        return CuentaMovimiento.builder()
                .numeroCuenta(new BigDecimal(numero))
                .cuenta(Cuenta.builder().numeroCuenta(new BigDecimal(numero)).nombre(nombre).build())
                .debita(debita)
                .importe(new BigDecimal(importe))
                .build();
    }

}
