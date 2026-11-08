package um.tesoreria.core.hexagonal.chequera.baja.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import um.tesoreria.core.hexagonal.chequera.baja.application.exception.BajaException;
import um.tesoreria.core.hexagonal.chequera.baja.domain.model.DatosBaja;
import um.tesoreria.core.hexagonal.chequera.baja.domain.model.DebitoBaja;
import um.tesoreria.core.hexagonal.chequera.baja.domain.model.ResultadoAnulacionBaja;
import um.tesoreria.core.hexagonal.chequera.baja.domain.ports.out.DebitoBajaRepository;
import um.tesoreria.core.hexagonal.chequera.baja.domain.ports.out.SeguroBajaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AnularBajaServiceTest {

    @Mock
    private BajaService bajaService;
    @Mock
    private ChequeraCuotaService chequeraCuotaService;
    @Mock
    private ChequeraTotalService chequeraTotalService;
    @Mock
    private DebitoBajaRepository debitoBajaRepository;
    @Mock
    private SeguroBajaRepository seguroBajaRepository;

    private AnularBajaService service;

    @BeforeEach
    void setUp() {
        service = new AnularBajaService(bajaService, chequeraCuotaService, chequeraTotalService,
                debitoBajaRepository, seguroBajaRepository);
        when(bajaService.findDatosByUnique(6, 2, 123L)).thenReturn(Optional.of(new DatosBaja(
                6, 2, 123L, 1, new BigDecimal("45719365"), 12345678,
                OffsetDateTime.parse("2026-03-15T00:00:00Z"), "Baja")));
        when(chequeraCuotaService.findAllByChequera(6, 2, 123L)).thenReturn(List.of(cuota()));
        when(chequeraCuotaService.update(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraCuotaService.calculateTotalCuotasActivas(any(), any(), any(), any(), any()))
                .thenReturn(new BigDecimal("100.00"));
        when(chequeraCuotaService.calculateTotalCuotasPagadas(any(), any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(debitoBajaRepository.findAllAsociados(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(DebitoBaja.builder().debitoId(10L).build()));
        when(chequeraTotalService.findByUnique(any(), any(), any(), any())).thenReturn(
                ChequeraTotal.builder().chequeraTotalId(5L).build());
        when(chequeraTotalService.update(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void anulaLaBajaEnUnaSolaOperacion() {
        ResultadoAnulacionBaja resultado = service.anular(6, 2, 123L);

        assertThat(resultado.cuotasActualizadas()).isEqualTo(1);
        assertThat(resultado.debitosActualizados()).isEqualTo(1);
        assertThat(resultado.bajaEliminada()).isTrue();
        ArgumentCaptor<ChequeraCuota> cuota = ArgumentCaptor.forClass(ChequeraCuota.class);
        verify(chequeraCuotaService).update(cuota.capture(), any());
        assertThat(cuota.getValue().getBaja()).isEqualTo((byte) 0);
        verify(debitoBajaRepository).limpiarFechaBaja(10L);
        verify(seguroBajaRepository).resetBaja(12345678, new BigDecimal("45719365"), 1);
        verify(bajaService).deleteByUnique(6, 2, 123L);
    }

    @Test
    void fallaSiNoHayBajaVigente() {
        when(bajaService.findDatosByUnique(6, 2, 999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.anular(6, 2, 999L))
                .isInstanceOf(BajaException.class);
        verify(bajaService, never()).deleteByUnique(any(), any(), any());
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
                .baja((byte) 1)
                .build();
    }

}
