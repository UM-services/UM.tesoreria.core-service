package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.audit.AuditBulkScope;
import um.tesoreria.core.audit.AuditBulkService;
import um.tesoreria.core.exception.FacultadPagoCuentaException;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.application.exception.AsientoException;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service.AsientoIngresoCatalogo;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service.AsientoIngresoResumenBuilder;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoAccion;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResumen;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResultado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.ClaveImputacion;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.ClaveSerie;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.LineaChequeraPagoAsiento;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.PagoIngreso;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.AsientoIngresoReferenciaRepository;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.ChequeraPagoAsientoEscrituraRepository;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.IngresoAsientoMapaRepository;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.IngresoItemRepository;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.model.FacultadPagoCuenta;
import um.tesoreria.core.model.dto.ItemAsientoDto;
import um.tesoreria.core.model.dto.PagoDto;
import um.tesoreria.core.service.EjercicioService;
import um.tesoreria.core.service.FacultadPagoCuentaService;
import um.tesoreria.core.service.facade.PagoService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegenerarAsientoIngresoUseCaseImplTest {

    private static final OffsetDateTime FECHA = OffsetDateTime.of(2026, 11, 17, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final Integer TIPO_PAGO = 3;

    @Mock IngresoAsientoMapaRepository mapaRepository;
    @Mock IngresoItemRepository ingresoItemRepository;
    @Mock ChequeraPagoAsientoEscrituraRepository chequeraPagoAsientoRepository;
    @Mock PagoService pagoService;
    @Mock ReservarOrdenContableUseCase reservarOrdenContable;
    @Mock AsientoService asientoService;
    @Mock CuentaMovimientoService cuentaMovimientoService;
    @Mock FacultadPagoCuentaService facultadPagoCuentaService;
    @Mock AsientoIngresoReferenciaRepository referenciaRepository;
    @Mock EjercicioService ejercicioService;
    @Mock AsientoIngresoCatalogo catalogo;
    @Mock AsientoIngresoResumenBuilder resumenBuilder;
    @Mock AuditBulkService auditBulkService;

    RegenerarAsientoIngresoUseCaseImpl useCase;

    @BeforeEach
    void preparar() {
        useCase = new RegenerarAsientoIngresoUseCaseImpl(
                mapaRepository,
                ingresoItemRepository,
                chequeraPagoAsientoRepository,
                pagoService,
                reservarOrdenContable,
                asientoService,
                cuentaMovimientoService,
                facultadPagoCuentaService,
                referenciaRepository,
                ejercicioService,
                catalogo,
                resumenBuilder,
                auditBulkService,
                TransactionOperations.withoutTransaction());
        lenient().when(auditBulkService.begin(any())).thenReturn(mock(AuditBulkScope.class));
    }

    private void ejercicioHabilitado() {
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setBloqueado((byte) 0);
        when(ejercicioService.findByFecha(FECHA)).thenReturn(ejercicio);
    }

    private PagoDto pago(int facultadId, BigDecimal importe) {
        return PagoDto.builder()
                .facultadId(facultadId)
                .tipoChequeraId(1)
                .chequeraSerieId(10L)
                .productoId(1)
                .alternativaId(1)
                .cuotaId(1)
                .orden(1)
                .fecha(FECHA)
                .importePagado(importe)
                .tipoPagoId(TIPO_PAGO)
                .chequeraPagoId(100L)
                .build();
    }

    private void prepararPagoConImputacion(Boolean conImputacion) {
        ejercicioHabilitado();
        when(pagoService.getPagos(TIPO_PAGO, FECHA)).thenReturn(List.of(pago(1, new BigDecimal("100.00"))));
        when(facultadPagoCuentaService.findByUnique(1, TIPO_PAGO))
                .thenReturn(new FacultadPagoCuenta(1L, 1, TIPO_PAGO, new BigDecimal("11100000001")));
        when(referenciaRepository.findLectivosBySeries(any()))
                .thenReturn(Map.of(new ClaveSerie(1, 1, 10L), 2026));
        when(referenciaRepository.findCuentasByFacultades(any()))
                .thenReturn(conImputacion
                        ? Map.of(new ClaveImputacion(1, 2026, 1, 1), new BigDecimal("41100000003"))
                        : Map.of());
        when(mapaRepository.findWithLockByFechaAndTipoPago(FECHA, TIPO_PAGO)).thenReturn(Optional.empty());
        when(mapaRepository.create(FECHA, TIPO_PAGO, 7)).thenReturn(
                um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoRegistro.builder()
                        .id(1L).fechaContable(FECHA).tipoPagoId(TIPO_PAGO).ordenContable(7).build());
        when(reservarOrdenContable.reservar(FECHA)).thenReturn(7);
        when(asientoService.findByAsiento(FECHA, 7)).thenReturn(AsientoEntity.builder()
                .asientoId(1L).fecha(FECHA).orden(7).vinculo("Asiento Ingreso").build());
        when(pagoService.getItems(TIPO_PAGO, FECHA)).thenReturn(List.of(ItemAsientoDto.builder()
                .fecha(FECHA)
                .numeroCuenta(new BigDecimal("11100000001"))
                .debita((byte) 1)
                .importe(new BigDecimal("100.00"))
                .build()));
        when(ingresoItemRepository.findAllByFechaAndTipoPago(FECHA, TIPO_PAGO)).thenReturn(List.of());
        when(resumenBuilder.build(eq(FECHA), eq(TIPO_PAGO), any()))
                .thenReturn(AsientoIngresoResumen.builder().fecha(FECHA).tipoPagoId(TIPO_PAGO)
                        .existe(true).ordenContable(7).totalDebe(new BigDecimal("100.00"))
                        .totalHaber(new BigDecimal("100.00")).cuadra(true).build());
    }

    @Test
    void regeneraConDebeYHaberCuandoHayImputacion() {
        prepararPagoConImputacion(true);

        AsientoIngresoResultado resultado = useCase.regenerar(FECHA, TIPO_PAGO);

        assertThat(resultado.getAccion()).isEqualTo(AsientoIngresoAccion.GENERADO);
        assertThat(resultado.getFaltantes()).isEmpty();
        verify(mapaRepository).create(FECHA, TIPO_PAGO, 7);
        verify(asientoService).deleteByAsientoId(1L);
        verify(asientoService).add(any());
        verify(cuentaMovimientoService).createCuentaMovimiento(any());
        verify(chequeraPagoAsientoRepository).deleteAllByFechaAndTipoPago(FECHA, TIPO_PAGO);
        ArgumentCaptor<List<LineaChequeraPagoAsiento>> captor = ArgumentCaptor.forClass(List.class);
        verify(chequeraPagoAsientoRepository).createAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertThat(captor.getValue().get(1).item()).isEqualTo(2);
        assertThat(captor.getValue().get(1).cuenta()).isEqualByComparingTo("41100000003");
    }

    @Test
    void sinImputacionGeneraFaltanteYNoloEscribeEnElHaber() {
        prepararPagoConImputacion(false);
        when(catalogo.tipoChequera(1)).thenReturn(null);

        AsientoIngresoResultado resultado = useCase.regenerar(FECHA, TIPO_PAGO);

        assertThat(resultado.getFaltantes()).hasSize(1);
        assertThat(resultado.getFaltantes().get(0).getFacultadId()).isEqualTo(1);
        ArgumentCaptor<List<LineaChequeraPagoAsiento>> captor = ArgumentCaptor.forClass(List.class);
        verify(chequeraPagoAsientoRepository).createAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).item()).isEqualTo(1);
        assertThat(captor.getValue().get(0).debita()).isEqualTo((byte) 1);
    }

    @Test
    void sinItemsNiIngresoItemsBorraElMapaYDevuelveSinMovimientos() {
        prepararPagoConImputacion(true);
        when(pagoService.getItems(TIPO_PAGO, FECHA)).thenReturn(List.of());
        when(resumenBuilder.build(FECHA, TIPO_PAGO, null)).thenReturn(AsientoIngresoResumen.builder()
                .fecha(FECHA).tipoPagoId(TIPO_PAGO).existe(false)
                .totalDebe(BigDecimal.ZERO).totalHaber(BigDecimal.ZERO).cuadra(true).lineas(List.of()).build());

        AsientoIngresoResultado resultado = useCase.regenerar(FECHA, TIPO_PAGO);

        assertThat(resultado.getAccion()).isEqualTo(AsientoIngresoAccion.SIN_MOVIMIENTOS);
        verify(mapaRepository).delete(any());
        verify(asientoService, never()).add(any());
    }

    @Test
    void sinCuentaDePagoNoEscribeNada() {
        ejercicioHabilitado();
        when(pagoService.getPagos(TIPO_PAGO, FECHA)).thenReturn(List.of(pago(1, new BigDecimal("100.00"))));
        when(facultadPagoCuentaService.findByUnique(1, TIPO_PAGO))
                .thenThrow(new FacultadPagoCuentaException(1, TIPO_PAGO));

        assertThatThrownBy(() -> useCase.regenerar(FECHA, TIPO_PAGO))
                .isInstanceOf(AsientoIngresoException.class)
                .hasMessageContaining("facultad");

        verify(reservarOrdenContable, never()).reservar(any());
        verify(asientoService, never()).add(any());
    }

    @Test
    void ejercicioBloqueadoNoTocaDatos() {
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setBloqueado((byte) 1);
        when(ejercicioService.findByFecha(FECHA)).thenReturn(ejercicio);

        assertThatThrownBy(() -> useCase.regenerar(FECHA, TIPO_PAGO))
                .isInstanceOf(AsientoIngresoException.class)
                .hasMessageContaining("bloqueado");

        verify(reservarOrdenContable, never()).reservar(any());
    }

    @Test
    void guardaElDiaContableDelPagoAunqueVengaConHora() {
        prepararPagoConImputacion(true);
        OffsetDateTime conHora = FECHA.withHour(0).withMinute(3).withSecond(20);
        when(pagoService.getPagos(TIPO_PAGO, FECHA)).thenReturn(List.of(PagoDto.builder()
                .facultadId(1)
                .tipoChequeraId(1)
                .chequeraSerieId(10L)
                .productoId(1)
                .alternativaId(1)
                .cuotaId(1)
                .orden(1)
                .fecha(conHora)
                .importePagado(new BigDecimal("100.00"))
                .tipoPagoId(TIPO_PAGO)
                .chequeraPagoId(100L)
                .build()));

        useCase.regenerar(FECHA, TIPO_PAGO);

        ArgumentCaptor<List<LineaChequeraPagoAsiento>> captor = ArgumentCaptor.forClass(List.class);
        verify(chequeraPagoAsientoRepository, org.mockito.Mockito.atLeastOnce()).createAll(captor.capture());
        assertThat(captor.getValue().get(0).pago().getFecha()).isEqualTo(FECHA);
    }

    @Test
    void cuandoElOrdenDelMapaEstaOcupadoReservaUnoNuevo() {
        ejercicioHabilitado();
        when(pagoService.getPagos(TIPO_PAGO, FECHA)).thenReturn(List.of(pago(1, new BigDecimal("100.00"))));
        when(facultadPagoCuentaService.findByUnique(1, TIPO_PAGO))
                .thenReturn(new FacultadPagoCuenta(1L, 1, TIPO_PAGO, new BigDecimal("11100000001")));
        when(referenciaRepository.findLectivosBySeries(any()))
                .thenReturn(Map.of(new ClaveSerie(1, 1, 10L), 2026));
        when(referenciaRepository.findCuentasByFacultades(any()))
                .thenReturn(Map.of(new ClaveImputacion(1, 2026, 1, 1), new BigDecimal("41100000003")));

        var registro = um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoRegistro
                .builder().id(1L).fechaContable(FECHA).tipoPagoId(TIPO_PAGO).ordenContable(7).build();
        when(mapaRepository.findWithLockByFechaAndTipoPago(FECHA, TIPO_PAGO)).thenReturn(Optional.of(registro));
        when(mapaRepository.existsOtherByFechaAndOrden(FECHA, 7, TIPO_PAGO)).thenReturn(false);
        when(asientoService.findByAsiento(FECHA, 7)).thenReturn(AsientoEntity.builder()
                .asientoId(9L).fecha(FECHA).orden(7).vinculo("Pago 0015-00004086").build());
        when(reservarOrdenContable.reservar(FECHA)).thenReturn(8);
        var reasignado = um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoRegistro
                .builder().id(1L).fechaContable(FECHA).tipoPagoId(TIPO_PAGO).ordenContable(8).build();
        when(mapaRepository.updateOrden(registro, 8)).thenReturn(reasignado);
        when(asientoService.findByAsiento(FECHA, 8)).thenThrow(new AsientoException(FECHA, 8));

        when(pagoService.getItems(TIPO_PAGO, FECHA)).thenReturn(List.of(ItemAsientoDto.builder()
                .fecha(FECHA)
                .numeroCuenta(new BigDecimal("11100000001"))
                .debita((byte) 1)
                .importe(new BigDecimal("100.00"))
                .build()));
        when(ingresoItemRepository.findAllByFechaAndTipoPago(FECHA, TIPO_PAGO)).thenReturn(List.of());
        when(resumenBuilder.build(eq(FECHA), eq(TIPO_PAGO), any())).thenReturn(AsientoIngresoResumen.builder()
                .fecha(FECHA).tipoPagoId(TIPO_PAGO).existe(true).ordenContable(8)
                .totalDebe(new BigDecimal("100.00")).totalHaber(new BigDecimal("100.00")).cuadra(true).build());

        AsientoIngresoResultado resultado = useCase.regenerar(FECHA, TIPO_PAGO);

        assertThat(resultado.getAccion()).isEqualTo(AsientoIngresoAccion.GENERADO);
        verify(mapaRepository).updateOrden(registro, 8);
        verify(mapaRepository, never()).create(any(), any(), any());
        verify(asientoService, never()).deleteByAsientoId(9L);
    }

}
