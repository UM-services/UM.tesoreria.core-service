package um.tesoreria.core.hexagonal.contable.asientoCierre.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableLinea;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableResultado;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.asientoCierre.domain.model.VarianteCierre;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.SumaCuenta;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsientoCierreServiceTest {

    private static final Integer EJERCICIO = 15;
    private static final OffsetDateTime INICIO =
            OffsetDateTime.of(2026, 3, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime FIN =
            OffsetDateTime.of(2027, 2, 28, 0, 0, 0, 0, ZoneOffset.UTC);

    @Mock AsientoService asientoService;
    @Mock CuentaMovimientoService cuentaMovimientoService;
    @Mock CuentaService cuentaService;
    @Mock ReservarOrdenContableUseCase reservarOrdenContable;
    @Mock EjercicioRepository ejercicioRepository;

    AsientoCierreService service;

    @BeforeEach
    void preparar() {
        service = new AsientoCierreService(asientoService, cuentaMovimientoService, cuentaService,
                reservarOrdenContable, ejercicioRepository);
        lenient().when(cuentaMovimientoService.createCuentaMovimiento(any()))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    private void ejercicio() {
        when(ejercicioRepository.findByEjercicioId(EJERCICIO)).thenReturn(Optional.of(
                Ejercicio.builder().ejercicioId(EJERCICIO).nombre("Ejercicio 2026")
                        .fechaInicio(LocalDate.of(2026, 3, 1))
                        .fechaFinal(LocalDate.of(2027, 2, 28))
                        .bloqueado((byte) 0).build()));
    }

    private void cuentasYSaldos() {
        lenient().when(cuentaService.findAllByCierreResultado()).thenReturn(List.of(
                Cuenta.builder().numeroCuenta(new BigDecimal("10102040001")).nombre("Banco").build(),
                Cuenta.builder().numeroCuenta(new BigDecimal("30101020002")).nombre("Ingresos").build(),
                Cuenta.builder().numeroCuenta(new BigDecimal("10102040002")).nombre("Sin saldo").build()));
        lenient().when(cuentaService.findAllByCierreActivoPasivo()).thenReturn(List.of(
                Cuenta.builder().numeroCuenta(new BigDecimal("10102040001")).nombre("Banco").build()));
        when(cuentaMovimientoService.sumarPorCuenta(INICIO, FIN, (byte) 0)).thenReturn(List.of(
                new SumaCuenta(new BigDecimal("10102040001"), new BigDecimal("100.00"), BigDecimal.ZERO),
                new SumaCuenta(new BigDecimal("30101020002"), BigDecimal.ZERO, new BigDecimal("100.00")),
                new SumaCuenta(new BigDecimal("10102040002"), new BigDecimal("50.00"), new BigDecimal("50.00"))));
    }

    @Test
    void previewFiltraSaldoCeroEInvierteElSentido() {
        ejercicio();
        cuentasYSaldos();

        AsientoContableResultado resultado = service.preview(EJERCICIO, VarianteCierre.RESULTADO);

        assertThat(resultado.fecha()).isEqualTo(FIN);
        assertThat(resultado.vinculo()).isEqualTo("Asiento Cierre Resultado");
        assertThat(resultado.lineas()).hasSize(2);
        assertThat(resultado.lineas().get(0).numeroCuenta()).isEqualByComparingTo("10102040001");
        assertThat(resultado.lineas().get(0).debita()).isEqualTo((byte) 0);
        assertThat(resultado.lineas().get(0).importe()).isEqualByComparingTo("100.00");
        assertThat(resultado.lineas().get(1).numeroCuenta()).isEqualByComparingTo("30101020002");
        assertThat(resultado.lineas().get(1).debita()).isEqualTo((byte) 1);
        assertThat(resultado.totalDebe()).isEqualByComparingTo("100.00");
        assertThat(resultado.totalHaber()).isEqualByComparingTo("100.00");
        assertThat(resultado.cuadra()).isTrue();
    }

    @Test
    void registrarReservaYGrabaCabeceraDeResultado() {
        ejercicio();
        when(reservarOrdenContable.reservar(FIN)).thenReturn(7);
        List<AsientoContableLinea> lineas = List.of(
                new AsientoContableLinea(new BigDecimal("10102040001"), "Banco", (byte) 0, new BigDecimal("100.00")),
                new AsientoContableLinea(new BigDecimal("30101020002"), "Ingresos", (byte) 1, new BigDecimal("100.00")));

        AsientoContableResultado resultado = service.registrar(EJERCICIO, VarianteCierre.RESULTADO, "Cierre", lineas);

        assertThat(resultado.orden()).isEqualTo(7);
        assertThat(resultado.cuadra()).isTrue();
        ArgumentCaptor<AsientoEntity> cabecera = ArgumentCaptor.forClass(AsientoEntity.class);
        verify(asientoService).add(cabecera.capture());
        assertThat(cabecera.getValue().getVinculo()).isEqualTo("Asiento Cierre Resultado");
    }

    @Test
    void previewActivoPasivoUsaSuPropiaListaDeCuentas() {
        ejercicio();
        cuentasYSaldos();

        AsientoContableResultado resultado = service.preview(EJERCICIO, VarianteCierre.ACTIVO_PASIVO);

        assertThat(resultado.vinculo()).isEqualTo("Asiento Cierre Activo-Pasivo");
        assertThat(resultado.lineas()).hasSize(1);
        assertThat(resultado.lineas().get(0).numeroCuenta()).isEqualByComparingTo("10102040001");
    }

}
