package um.tesoreria.core.hexagonal.contable.asientoApertura.application.service;

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
import um.tesoreria.core.hexagonal.contable.asientoApertura.application.exception.AsientoAperturaException;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsientoAperturaServiceTest {

    private static final Integer EJERCICIO = 15;
    private static final OffsetDateTime INICIO =
            OffsetDateTime.of(2026, 3, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime CIERRE_ANTERIOR =
            OffsetDateTime.of(2026, 2, 28, 0, 0, 0, 0, ZoneOffset.UTC);

    @Mock AsientoService asientoService;
    @Mock CuentaMovimientoService cuentaMovimientoService;
    @Mock CuentaService cuentaService;
    @Mock ReservarOrdenContableUseCase reservarOrdenContable;
    @Mock EjercicioRepository ejercicioRepository;

    AsientoAperturaService service;

    @BeforeEach
    void preparar() {
        service = new AsientoAperturaService(asientoService, cuentaMovimientoService, cuentaService,
                reservarOrdenContable, ejercicioRepository);
        lenient().when(cuentaService.findAllByNumeroCuentaIn(any())).thenReturn(List.of());
        lenient().when(cuentaMovimientoService.createCuentaMovimiento(any()))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    private void ejercicios() {
        lenient().when(ejercicioRepository.findByEjercicioId(EJERCICIO)).thenReturn(Optional.of(
                Ejercicio.builder().ejercicioId(EJERCICIO).nombre("Ejercicio 2026")
                        .fechaInicio(LocalDate.of(2026, 3, 1))
                        .fechaFinal(LocalDate.of(2027, 2, 28))
                        .bloqueado((byte) 0).build()));
        lenient().when(ejercicioRepository.findByEjercicioId(EJERCICIO - 1)).thenReturn(Optional.of(
                Ejercicio.builder().ejercicioId(EJERCICIO - 1).nombre("Ejercicio 2025")
                        .fechaInicio(LocalDate.of(2025, 3, 1))
                        .fechaFinal(LocalDate.of(2026, 2, 28))
                        .bloqueado((byte) 0).build()));
    }

    private void referencia() {
        when(cuentaMovimientoService.findAllByAsiento(CIERRE_ANTERIOR, 93, 0, 2)).thenReturn(List.of(
                CuentaMovimiento.builder().numeroCuenta(new BigDecimal("10102040001"))
                        .debita((byte) 1).importe(new BigDecimal("100.00")).build(),
                CuentaMovimiento.builder().numeroCuenta(new BigDecimal("30101020002"))
                        .debita((byte) 0).importe(new BigDecimal("100.00")).build()));
    }

    @Test
    void previewClonaEInvierteDebeHaber() {
        ejercicios();
        referencia();

        AsientoContableResultado resultado = service.preview(EJERCICIO, 93);

        assertThat(resultado.fecha()).isEqualTo(INICIO);
        assertThat(resultado.orden()).isNull();
        assertThat(resultado.vinculo()).isEqualTo("Asiento Apertura");
        assertThat(resultado.lineas()).hasSize(2);
        assertThat(resultado.lineas().get(0).debita()).isEqualTo((byte) 0);
        assertThat(resultado.lineas().get(1).debita()).isEqualTo((byte) 1);
        assertThat(resultado.totalDebe()).isEqualByComparingTo("100.00");
        assertThat(resultado.totalHaber()).isEqualByComparingTo("100.00");
        assertThat(resultado.cuadra()).isTrue();
    }

    @Test
    void registrarReservaYGrabaCabecera() {
        ejercicios();
        when(reservarOrdenContable.reservar(INICIO)).thenReturn(7);
        List<AsientoContableLinea> lineas = List.of(
                new AsientoContableLinea(new BigDecimal("10102040001"), "Banco", (byte) 1, new BigDecimal("100.00")),
                new AsientoContableLinea(new BigDecimal("30101020002"), "Ingreso", (byte) 0, new BigDecimal("100.00")));

        AsientoContableResultado resultado = service.registrar(EJERCICIO, null, lineas);

        assertThat(resultado.orden()).isEqualTo(7);
        assertThat(resultado.cuadra()).isTrue();
        ArgumentCaptor<AsientoEntity> cabecera = ArgumentCaptor.forClass(AsientoEntity.class);
        verify(asientoService).add(cabecera.capture());
        assertThat(cabecera.getValue().getVinculo()).isEqualTo("Asiento Apertura");
        verify(cuentaMovimientoService, org.mockito.Mockito.times(2)).createCuentaMovimiento(any());
    }

    @Test
    void referenciaInexistenteFalla() {
        ejercicios();
        when(cuentaMovimientoService.findAllByAsiento(CIERRE_ANTERIOR, 93, 0, 2)).thenReturn(List.of());

        assertThatThrownBy(() -> service.preview(EJERCICIO, 93))
                .isInstanceOf(AsientoAperturaException.class)
                .hasMessageContaining("referencia");
    }

}
