package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.application.usecases;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.politicaArancelaria.domain.ports.in.SanitizeChequeraCuotaArancelUseCase;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecalculateCuotaByUniqueIndexUseCaseImplTest {

    private static final Integer FACULTAD_ID = 2;
    private static final Integer TIPO_CHEQUERA_ID = 517;
    private static final Long CHEQUERA_SERIE_ID = 2788L;
    private static final Integer PRODUCTO_ID = 3;
    private static final Integer ALTERNATIVA_ID = 1;
    private static final Integer CUOTA_ID = 1;

    private final ChequeraCuotaService chequeraCuotaService = mock(ChequeraCuotaService.class);
    private final LectivoCuotaService lectivoCuotaService = mock(LectivoCuotaService.class);
    private final LectivoService lectivoService = mock(LectivoService.class);
    private final ChequeraSerieService chequeraSerieService = mock(ChequeraSerieService.class);
    private final SanitizeChequeraCuotaArancelUseCase sanitizeUseCase = mock(SanitizeChequeraCuotaArancelUseCase.class);

    private final RecalculateCuotaByUniqueIndexUseCaseImpl useCase = new RecalculateCuotaByUniqueIndexUseCaseImpl(
            chequeraCuotaService, lectivoCuotaService, lectivoService, chequeraSerieService, sanitizeUseCase);

    /** Snapshot (importe1/importe3) de cada cuota en el momento de persistirse. */
    private final List<ChequeraCuota> persistidos = new ArrayList<>();

    private ChequeraCuota.ChequeraCuotaBuilder cuota(Long id, Integer cuotaId, String importe1, String importe2, String importe3) {
        return ChequeraCuota.builder()
                .chequeraCuotaId(id)
                .facultadId(FACULTAD_ID)
                .tipoChequeraId(TIPO_CHEQUERA_ID)
                .chequeraSerieId(CHEQUERA_SERIE_ID)
                .productoId(PRODUCTO_ID)
                .alternativaId(ALTERNATIVA_ID)
                .cuotaId(cuotaId)
                .arancelTipoId(46)
                .importe1(new BigDecimal(importe1)).importe1Original(new BigDecimal(importe1))
                .importe2(new BigDecimal(importe2)).importe2Original(new BigDecimal(importe2))
                .importe3(new BigDecimal(importe3)).importe3Original(new BigDecimal(importe3))
                .pagado((byte) 0).baja((byte) 0).manual((byte) 0).compensada((byte) 0);
    }

    /** Simula el saneamiento: aplica 50% y reporta corrección. */
    private void stubSaneamientoCorrige() {
        when(sanitizeUseCase.sanitize(any(), any(), any())).thenAnswer(invocation -> {
            ChequeraCuota cuota = invocation.getArgument(0);
            cuota.aplicarPorcentajeArancel(new BigDecimal("50"));
            return true;
        });
    }

    /** Simula un saneamiento que no encuentra diferencias. */
    private void stubSaneamientoSinCambios() {
        when(sanitizeUseCase.sanitize(any(), any(), any())).thenAnswer(invocation -> false);
    }

    private void stubPersistencia() {
        when(chequeraCuotaService.saveAll(any())).thenAnswer(invocation -> {
            List<ChequeraCuota> cuotas = invocation.getArgument(0);
            cuotas.forEach(c -> persistidos.add(ChequeraCuota.builder()
                    .chequeraCuotaId(c.getChequeraCuotaId())
                    .cuotaId(c.getCuotaId())
                    .importe1(c.getImporte1())
                    .importe3(c.getImporte3())
                    .build()));
            return cuotas;
        });
    }

    private void stubSerie() {
        when(chequeraSerieService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID))
                .thenReturn(ChequeraSerie.builder()
                        .facultadId(FACULTAD_ID).tipoChequeraId(TIPO_CHEQUERA_ID).chequeraSerieId(CHEQUERA_SERIE_ID)
                        .arancelTipoId(46).lectivoId(37).build());
    }

    private void stubCuotas() {
        when(chequeraCuotaService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID))
                .thenReturn(cuota(3167274L, 1, "325000.0000", "341250.0000", "357500.0000")
                        .vencimiento3(OffsetDateTime.now().minusDays(30)).build());
        when(chequeraCuotaService.getCuotaActual(any(), any(), any(), any(), any(), any()))
                .thenReturn(cuota(3167281L, 8, "380000.0000", "391400.0000", "402800.0000")
                        .vencimiento3(OffsetDateTime.now().plusDays(30)).build());
    }

    @Test
    void recalculaSobreLosImportesSaneadosYDevuelveElResultado() {
        stubCuotas();
        stubSerie();
        stubSaneamientoCorrige();
        stubPersistencia();

        var result = useCase.recalculateCuota(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID, 10);

        // La respuesta devuelve el recálculo: referencia saneada 190000, revisión saneada 178750 -> max = 190000
        assertThat(result.getImporte3()).isEqualByComparingTo("190000.0000");
        assertThat(result.getVencimiento3()).isAfter(OffsetDateTime.now());
    }

    @Test
    void persisteLaSanitizacionDeRevisionYReferenciaPeroNoElRecalculo() {
        stubCuotas();
        stubSerie();
        stubSaneamientoCorrige();
        stubPersistencia();

        useCase.recalculateCuota(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID, 10);

        assertThat(persistidos).hasSize(2);
        // Revisión (cuota 1): se persiste saneada… importe3 = 178750 (NO el 190000 del recálculo)
        assertThat(persistidos.get(0).getCuotaId()).isEqualTo(1);
        assertThat(persistidos.get(0).getImporte1()).isEqualByComparingTo("162500.0000");
        assertThat(persistidos.get(0).getImporte3()).isEqualByComparingTo("178750.0000");
        // Referencia (cuota 8): se persiste saneada
        assertThat(persistidos.get(1).getCuotaId()).isEqualTo(8);
        assertThat(persistidos.get(1).getImporte1()).isEqualByComparingTo("190000.0000");
    }

    @Test
    void noPersisteSiElCalculoYaCorresponde() {
        stubCuotas();
        stubSerie();
        stubSaneamientoSinCambios();

        useCase.recalculateCuota(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID, 10);

        verify(chequeraCuotaService, never()).saveAll(any());
    }

    @Test
    void corteTempranoNoSaneaNiPersiste() {
        when(chequeraCuotaService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID))
                .thenReturn(cuota(3167274L, 1, "325000.0000", "341250.0000", "357500.0000")
                        .vencimiento3(OffsetDateTime.now().plusDays(5)).build());

        var result = useCase.recalculateCuota(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID, 10);

        assertThat(result.getImporte1()).isEqualByComparingTo("325000.0000");
        verify(sanitizeUseCase, never()).sanitize(any(), any(), any());
        verify(chequeraCuotaService, never()).saveAll(any());
    }

    @Test
    void noPersisteCuotasPagadasAunqueHayaCorreccion() {
        when(chequeraCuotaService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID))
                .thenReturn(cuota(3167274L, 1, "325000.0000", "341250.0000", "357500.0000")
                        .vencimiento3(OffsetDateTime.now().minusDays(30)).pagado((byte) 1).build());
        when(chequeraCuotaService.getCuotaActual(any(), any(), any(), any(), any(), any()))
                .thenReturn(cuota(3167281L, 8, "380000.0000", "391400.0000", "402800.0000")
                        .vencimiento3(OffsetDateTime.now().plusDays(30)).pagado((byte) 1).build());
        stubSerie();
        stubSaneamientoCorrige();

        useCase.recalculateCuota(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID, 10);

        verify(chequeraCuotaService, never()).saveAll(any());
    }

    @Test
    void persisteSoloLaReferenciaSiLaRevisionYaEstaSaneada() {
        stubCuotas();
        stubSerie();
        // La revisión ya está bien; la referencia no.
        when(sanitizeUseCase.sanitize(any(), any(), any())).thenAnswer(invocation -> {
            ChequeraCuota cuota = invocation.getArgument(0);
            if (cuota.getCuotaId() == 8) {
                cuota.aplicarPorcentajeArancel(new BigDecimal("50"));
                return true;
            }
            return false;
        });
        stubPersistencia();

        useCase.recalculateCuota(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID, 10);

        assertThat(persistidos).hasSize(1);
        assertThat(persistidos.get(0).getCuotaId()).isEqualTo(8);
    }

}
