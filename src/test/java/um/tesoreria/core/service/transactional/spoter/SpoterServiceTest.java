package um.tesoreria.core.service.transactional.spoter;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.factory.ChequeraCuotaFactory;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.hexagonal.personas.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.personas.legajo.application.service.LegajoService;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.kotlin.model.Build;
import um.tesoreria.core.kotlin.model.CarreraChequera;
import um.tesoreria.core.kotlin.model.Curso;
import um.tesoreria.core.kotlin.model.SpoterData;
import um.tesoreria.core.model.ChequeraSerieControl;
import um.tesoreria.core.service.BuildService;
import um.tesoreria.core.service.ChequeraAlternativaService;
import um.tesoreria.core.service.ChequeraSerieControlService;
import um.tesoreria.core.service.LectivoAlternativaService;
import um.tesoreria.core.service.LectivoTotalService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpoterServiceTest {

    @Test
    void makeChequeraSpoter_characterizesLegacyZeroBenefitQuotaValues() {
        var buildService = mock(BuildService.class);
        var personaService = mock(PersonaService.class);
        var serieService = mock(ChequeraSerieService.class);
        var domicilioService = mock(DomicilioService.class);
        var controlService = mock(ChequeraSerieControlService.class);
        var legajoService = mock(LegajoService.class);
        var lectivoTotalService = mock(LectivoTotalService.class);
        var totalService = mock(ChequeraTotalService.class);
        var lectivoAlternativaService = mock(LectivoAlternativaService.class);
        var alternativaService = mock(ChequeraAlternativaService.class);
        var lectivoCuotaService = mock(LectivoCuotaService.class);
        var cuotaService = mock(ChequeraCuotaService.class);
        var build = mock(Build.class);
        when(build.getBuild()).thenReturn(1L);
        when(buildService.findLast()).thenReturn(build);
        when(personaService.findByUnique(any(), anyInt())).thenReturn(mock(um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona.class));
        when(domicilioService.findByUnique(any(), anyInt())).thenReturn(mock(um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio.class));
        var control = new ChequeraSerieControl(null, 1, 2, 3L, (byte) 0, 0, 0, 0L, (byte) 0);
        when(controlService.findLastByTipoChequera(1, 2)).thenReturn(control);
        when(controlService.add(any())).thenReturn(control);
        when(legajoService.findByFacultadIdAndPersonaIdAndDocumentoId(anyInt(), any(), anyInt()))
                .thenReturn(mock(um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo.class));
        when(serieService.add(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(lectivoTotalService.findAllByTipo(1, 99, 2)).thenReturn(List.of());
        when(totalService.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(lectivoAlternativaService.findAllByTipo(1, 99, 2, 1)).thenReturn(List.of());
        when(alternativaService.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var vencimiento1 = OffsetDateTime.now().plusDays(1);
        var lectivoCuota = LectivoCuota.builder().productoId(3).alternativaId(1).cuotaId(4).mes(5).anho(2026)
                .tramoId(99).vencimiento1(vencimiento1).importe1(new BigDecimal("100.50"))
                .vencimiento2(vencimiento1.plusDays(10)).importe2(new BigDecimal("120.50"))
                .vencimiento3(vencimiento1.plusDays(20)).importe3(new BigDecimal("140.50")).build();
        when(lectivoCuotaService.findAllByTipo(1, 99, 2, 1)).thenReturn(List.of(lectivoCuota));
        when(cuotaService.calculateCodigoBarras(any())).thenReturn("barcode");
        when(cuotaService.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var spoter = mock(SpoterData.class);
        when(spoter.getPersonaId()).thenReturn(BigDecimal.ONE);
        when(spoter.getDocumentoId()).thenReturn(10);
        when(spoter.getFacultadId()).thenReturn(1);
        when(spoter.getGeograficaId()).thenReturn(4);
        when(spoter.getPlanId()).thenReturn(5);
        when(spoter.getCarreraId()).thenReturn(6);
        var curso = mock(Curso.class);
        when(curso.getCursoId()).thenReturn(7);
        var carrera = mock(CarreraChequera.class);
        when(carrera.getFacultadId()).thenReturn(1);
        when(carrera.getTipoChequeraId()).thenReturn(2);

        service(buildService, personaService, serieService, domicilioService, controlService, legajoService,
                lectivoTotalService, totalService, lectivoAlternativaService, alternativaService, lectivoCuotaService,
                new ChequeraCuotaFactory(cuotaService), cuotaService).makeChequeraSpoter(spoter, 99, curso, carrera);

        var series = ArgumentCaptor.forClass(ChequeraSerie.class);
        org.mockito.Mockito.verify(serieService).add(series.capture());
        assertThat(series.getValue().getBecaPorcentaje()).isEqualByComparingTo(BigDecimal.ZERO);
        var cuotas = captor(ChequeraCuota.class);
        org.mockito.Mockito.verify(cuotaService).saveAll(cuotas.capture());
        var cuota = cuotas.getValue().getFirst();
        assertThat(cuota.getTramoId()).isZero();
        assertThat(cuota.getVencimiento1()).isEqualTo(lectivoCuota.getVencimiento1());
        assertThat(cuota.getVencimiento2()).isEqualTo(lectivoCuota.getVencimiento2());
        assertThat(cuota.getVencimiento3()).isEqualTo(lectivoCuota.getVencimiento3());
        assertThat(cuota.getImporte1()).isEqualByComparingTo(lectivoCuota.getImporte1());
        assertThat(cuota.getImporte2()).isEqualByComparingTo(lectivoCuota.getImporte2());
        assertThat(cuota.getImporte3()).isEqualByComparingTo(lectivoCuota.getImporte3());
        assertThat(cuota.getImporte1Original()).isEqualByComparingTo(lectivoCuota.getImporte1());
        assertThat(cuota.getImporte2Original()).isEqualByComparingTo(lectivoCuota.getImporte2());
        assertThat(cuota.getImporte3Original()).isEqualByComparingTo(lectivoCuota.getImporte3());
        assertThat(cuota.getCodigoBarras()).isEqualTo("barcode");
        assertThat(cuota.getPagado()).isZero();
        assertThat(cuota.getBaja()).isZero();
        assertThat(cuota.getManual()).isZero();
        assertThat(cuota.getCompensada()).isZero();
    }

    private static SpoterService service(BuildService buildService, PersonaService personaService,
                                         ChequeraSerieService serieService, DomicilioService domicilioService,
                                         ChequeraSerieControlService controlService, LegajoService legajoService,
                                         LectivoTotalService lectivoTotalService, ChequeraTotalService totalService,
                                         LectivoAlternativaService lectivoAlternativaService,
                                         ChequeraAlternativaService alternativaService,
                                         LectivoCuotaService lectivoCuotaService, ChequeraCuotaFactory factory,
                                         ChequeraCuotaService cuotaService) {
        return new SpoterService(buildService, personaService, serieService, domicilioService, controlService,
                legajoService, lectivoTotalService, totalService, lectivoAlternativaService, alternativaService,
                lectivoCuotaService, factory, cuotaService);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ArgumentCaptor<List<ChequeraCuota>> captor(Class<ChequeraCuota> ignored) {
        return ArgumentCaptor.forClass((Class) List.class);
    }
}
