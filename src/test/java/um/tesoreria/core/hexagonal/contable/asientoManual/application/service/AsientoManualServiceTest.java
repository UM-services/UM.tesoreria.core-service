package um.tesoreria.core.hexagonal.contable.asientoManual.application.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.asiento.application.exception.AsientoException;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableLinea;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableResultado;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.asientoManual.application.exception.AsientoManualException;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsientoManualServiceTest {

    private static final OffsetDateTime FECHA =
            OffsetDateTime.of(2026, 11, 17, 0, 0, 0, 0, ZoneOffset.UTC);

    @Mock AsientoService asientoService;
    @Mock CuentaMovimientoService cuentaMovimientoService;
    @Mock CuentaService cuentaService;
    @Mock ReservarOrdenContableUseCase reservarOrdenContable;
    @Mock EjercicioRepository ejercicioRepository;

    AsientoManualService service;

    @BeforeEach
    void preparar() {
        service = new AsientoManualService(asientoService, cuentaMovimientoService, cuentaService,
                reservarOrdenContable, ejercicioRepository);
        lenient().when(cuentaService.findAllByNumeroCuentaIn(any())).thenReturn(List.of());
        lenient().when(cuentaMovimientoService.createCuentaMovimiento(any()))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    private void ejercicioHabilitado() {
        when(ejercicioRepository.findByFecha(FECHA.toLocalDate())).thenReturn(Optional.of(
                Ejercicio.builder().ejercicioId(15).nombre("Ejercicio 2026")
                        .fechaInicio(LocalDate.of(2026, 3, 1)).fechaFinal(LocalDate.of(2027, 2, 28))
                        .bloqueado((byte) 0).build()));
    }

    private List<AsientoContableLinea> lineas() {
        return List.of(
                new AsientoContableLinea(new BigDecimal("10102040001"), null, (byte) 1, new BigDecimal("100.00")),
                new AsientoContableLinea(new BigDecimal("30101020002"), null, (byte) 0, new BigDecimal("100.00")));
    }

    @Test
    void registraConOrdenReservadoYCabeceraPropia() {
        ejercicioHabilitado();
        when(reservarOrdenContable.reservar(FECHA)).thenReturn(7);

        AsientoContableResultado resultado = service.registrar(FECHA, null, "Ajuste", lineas());

        assertThat(resultado.orden()).isEqualTo(7);
        assertThat(resultado.totalDebe()).isEqualByComparingTo("100.00");
        assertThat(resultado.totalHaber()).isEqualByComparingTo("100.00");
        assertThat(resultado.cuadra()).isTrue();
        ArgumentCaptor<AsientoEntity> cabecera = ArgumentCaptor.forClass(AsientoEntity.class);
        verify(asientoService).add(cabecera.capture());
        assertThat(cabecera.getValue().getVinculo()).isEqualTo("Registro de Asiento");
        verify(cuentaMovimientoService, org.mockito.Mockito.times(2)).createCuentaMovimiento(any());
    }

    @Test
    void conNumeroManualLibreNoReservaYRegistraUso() {
        ejercicioHabilitado();
        when(asientoService.findByAsiento(FECHA, 9)).thenThrow(new AsientoException(FECHA, 9));
        when(cuentaMovimientoService.findAllByAsiento(FECHA, 9, 0, 2)).thenReturn(List.of());

        AsientoContableResultado resultado = service.registrar(FECHA, 9, "Ajuste", lineas());

        assertThat(resultado.orden()).isEqualTo(9);
        verify(reservarOrdenContable, never()).reservar(any());
        verify(reservarOrdenContable).registrarUso(FECHA, 9);
    }

    @Test
    void conNumeroManualOcupadoRechaza() {
        ejercicioHabilitado();
        when(asientoService.findByAsiento(FECHA, 9)).thenReturn(AsientoEntity.builder()
                .asientoId(1L).fecha(FECHA).orden(9).vinculo("Registro de Asiento").build());

        assertThatThrownBy(() -> service.registrar(FECHA, 9, "Ajuste", lineas()))
                .isInstanceOf(AsientoManualException.class)
                .hasMessageContaining("ya existe");

        verify(reservarOrdenContable, never()).reservar(any());
        verify(asientoService, never()).add(any());
    }

    @Test
    void ejercicioBloqueadoRechaza() {
        when(ejercicioRepository.findByFecha(FECHA.toLocalDate())).thenReturn(Optional.of(
                Ejercicio.builder().ejercicioId(15).fechaInicio(LocalDate.of(2026, 3, 1))
                        .fechaFinal(LocalDate.of(2027, 2, 28)).bloqueado((byte) 1).build()));

        assertThatThrownBy(() -> service.registrar(FECHA, null, "Ajuste", lineas()))
                .isInstanceOf(AsientoManualException.class)
                .hasMessageContaining("bloqueado");

        verify(reservarOrdenContable, never()).reservar(any());
    }

    @Test
    void importaPlanillaConEncabezadosCuentaDebeHaber() throws Exception {
        byte[] archivo;
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            var hoja = workbook.createSheet("Asiento");
            var encabezado = hoja.createRow(0);
            encabezado.createCell(0).setCellValue("cuenta");
            encabezado.createCell(1).setCellValue("debe");
            encabezado.createCell(2).setCellValue("haber");
            var fila1 = hoja.createRow(1);
            fila1.createCell(0).setCellValue(10102040001d);
            fila1.createCell(1).setCellValue(150.0);
            var fila2 = hoja.createRow(2);
            fila2.createCell(0).setCellValue(30101020002d);
            fila2.createCell(2).setCellValue(150.0);
            workbook.write(salida);
            archivo = salida.toByteArray();
        }

        List<AsientoContableLinea> lineas = service.importar(new ByteArrayInputStream(archivo));

        assertThat(lineas).hasSize(2);
        assertThat(lineas.get(0).numeroCuenta()).isEqualByComparingTo("10102040001");
        assertThat(lineas.get(0).debita()).isEqualTo((byte) 1);
        assertThat(lineas.get(0).importe()).isEqualByComparingTo("150.0");
        assertThat(lineas.get(1).debita()).isEqualTo((byte) 0);
    }

    @Test
    void consultaInexistenteDevuelveError() {
        when(asientoService.findByAsiento(FECHA, 7)).thenThrow(new AsientoException(FECHA, 7));
        when(cuentaMovimientoService.findAllByAsiento(FECHA, 7, 0, 2)).thenReturn(List.of());

        assertThatThrownBy(() -> service.consultar(FECHA, 7))
                .isInstanceOf(AsientoManualException.class)
                .hasMessageContaining("No existe");
    }

    @Test
    void eliminaSoloElAsientoManual() {
        ejercicioHabilitado();
        when(asientoService.findByAsiento(FECHA, 7)).thenReturn(AsientoEntity.builder()
                .asientoId(3L).fecha(FECHA).orden(7).vinculo("Registro de Asiento").build());
        service.eliminar(FECHA, 7);

        verify(cuentaMovimientoService).deleteAsiento(FECHA, 7, 1);
        verify(asientoService).deleteByAsientoId(3L);
    }

}
