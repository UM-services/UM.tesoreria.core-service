package um.tesoreria.core.hexagonal.cobranza.pagoManual.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.application.exception.PagoManualException;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.PagoManual;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.SolicitudPagoManual;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PagoManualServiceTest {

    @Mock
    private ChequeraPagoService chequeraPagoService;
    @Mock
    private ChequeraCuotaService chequeraCuotaService;
    @Mock
    private ChequeraSerieService chequeraSerieService;

    private PagoManualService service;

    @BeforeEach
    void setUp() {
        service = new PagoManualService(chequeraPagoService, chequeraCuotaService, chequeraSerieService);
        when(chequeraCuotaService.findByChequeraCuotaId(99L)).thenReturn(cuota());
        when(chequeraPagoService.findAllByCuota(any(), any(), any(), any(), any(), any())).thenReturn(List.of());
        when(chequeraPagoService.nextOrden(any(), any(), any(), any(), any(), any())).thenReturn(0);
        when(chequeraPagoService.create(any())).thenAnswer(invocation -> {
            ChequeraPago pago = invocation.getArgument(0);
            pago.setChequeraPagoId(77L);
            return pago;
        });
    }

    @Test
    void registraElPagoYMarcarLaCuota() {
        PagoManual resultado = service.registrar(new SolicitudPagoManual(99L,
                OffsetDateTime.parse("2026-03-15T00:00:00Z"), new BigDecimal("100.00"),
                "Pago en efectivo", null));

        assertThat(resultado.chequeraPagoId()).isEqualTo(77L);
        assertThat(resultado.importe()).isEqualByComparingTo(new BigDecimal("100.00"));
        ArgumentCaptor<ChequeraPago> pago = ArgumentCaptor.forClass(ChequeraPago.class);
        verify(chequeraPagoService).create(pago.capture());
        assertThat(pago.getValue().getTipoPagoId()).isEqualTo(2);
        assertThat(pago.getValue().getObservaciones()).isEqualTo("Pago en efectivo");
        verify(chequeraPagoService).marcarPago(6, 2, 123L, 1, 1, 1);
    }

    @Test
    void rechazaUnImporteMayorAlSaldo() {
        assertThatThrownBy(() -> service.registrar(new SolicitudPagoManual(99L,
                OffsetDateTime.parse("2026-03-15T00:00:00Z"), new BigDecimal("150.00"), "", null)))
                .isInstanceOf(PagoManualException.class)
                .hasMessageContaining("supera el saldo");
        verify(chequeraPagoService, never()).create(any());
    }

    @Test
    void rechazaUnaCuotaYaPagada() {
        ChequeraPago pago = ChequeraPago.builder().importe(new BigDecimal("100.00")).build();
        when(chequeraPagoService.findAllByCuota(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(pago));

        assertThatThrownBy(() -> service.registrar(new SolicitudPagoManual(99L,
                OffsetDateTime.parse("2026-03-15T00:00:00Z"), new BigDecimal("10.00"), "", null)))
                .isInstanceOf(PagoManualException.class)
                .hasMessageContaining("ya está pagada");
    }

    @Test
    void eliminaSoloPagosManuales() {
        ChequeraPago manual = ChequeraPago.builder().chequeraPagoId(77L).facultadId(6).tipoChequeraId(2)
                .chequeraSerieId(123L).productoId(1).alternativaId(1).cuotaId(1).tipoPagoId(2).build();
        when(chequeraPagoService.findByChequeraPagoId(77L)).thenReturn(manual);

        service.eliminar(77L);

        verify(chequeraPagoService).delete(77L);
        verify(chequeraPagoService).marcarPago(6, 2, 123L, 1, 1, 1);
    }

    @Test
    void noEliminaUnPagoQueNoEsManual() {
        ChequeraPago automatico = ChequeraPago.builder().chequeraPagoId(78L).tipoPagoId(4).build();
        when(chequeraPagoService.findByChequeraPagoId(78L)).thenReturn(automatico);

        assertThatThrownBy(() -> service.eliminar(78L))
                .isInstanceOf(PagoManualException.class)
                .hasMessageContaining("pagos manuales");
        verify(chequeraPagoService, never()).delete(78L);
    }

    private ChequeraCuota cuota() {
        return ChequeraCuota.builder()
                .chequeraCuotaId(99L)
                .facultadId(6)
                .tipoChequeraId(2)
                .chequeraSerieId(123L)
                .productoId(1)
                .alternativaId(1)
                .cuotaId(1)
                .mes(3)
                .anho(2026)
                .importe1(new BigDecimal("100.00"))
                .pagado((byte) 0)
                .baja((byte) 0)
                .build();
    }

}
