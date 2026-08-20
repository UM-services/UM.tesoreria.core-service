package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.PreuniversitarioChequeraData;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.RequisitoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.RequisitoPresentadoGuarani;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.service.GuaraniBeneficioService;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.policy.BeneficioPolicy;
import um.tesoreria.core.kotlin.model.Build;
import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;
import um.tesoreria.core.service.ChequeraSerieControlService;
import um.tesoreria.core.model.ChequeraSerieControl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreuniversitarioChequeraServiceTest {

    @Mock
    private PreuniversitarioDataResolver dataResolver;
    @Mock
    private PreuniversitarioLegajoManager legajoManager;
    @Mock
    private ChequeraSerieService chequeraSerieService;
    @Mock
    private ChequeraSerieControlService chequeraSerieControlService;
    @Mock
    private PreuniversitarioChequeraDetailsCreator detailsCreator;
    @Mock
    private PreuniversitarioChequeraPolicy policy;
    @Mock
    private GuaraniBeneficioService guaraniBeneficioService;
    @Mock
    private BeneficioPolicy beneficioPolicy;

    @InjectMocks
    private PreuniversitarioChequeraService service;

    @Test
    void create_returnsNullWhenRequiredReferencesCannotBeResolved() {
        var data = new PreuniversitarioChequeraData(10, 20, 30,
                BigDecimal.ONE, 50);
        when(dataResolver.resolve(data)).thenReturn(Optional.empty());

        assertThat(service.create(data)).isNull();
        verifyNoInteractions(legajoManager, chequeraSerieService,
                chequeraSerieControlService, detailsCreator);
    }

    @Test
    void create_persistsResolvedMaximumBenefitAndContinuesWithEmission() {
        var requisito = RequisitoPresentadoGuarani.builder().requisito(99).persona(1)
                .requisitoRel(RequisitoGuarani.builder().requisitoIngreso("S").activo("S").build()).build();
        var data = new PreuniversitarioChequeraData(10, 20, 30, BigDecimal.ONE, 50, List.of(requisito));
        var context = new PreuniversitarioChequeraContext(1, 2, 3, 4, BigDecimal.ONE, 50,
                new Build(1L));
        var control = new ChequeraSerieControl(null, 3, 2, 1L, (byte) 0, 0, 0, 0L, (byte) 0);
        var serie = ChequeraSerie.builder().facultadId(3).tipoChequeraId(2).chequeraSerieId(1L)
                .becaPorcentaje(new BigDecimal("30")).build();

        when(dataResolver.resolve(data)).thenReturn(Optional.of(context));
        when(legajoManager.findOrCreate(context)).thenReturn(mock(Legajo.class));
        when(chequeraSerieService.findPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
                BigDecimal.ONE, 50, 3, 1, 4)).thenThrow(new ChequeraSerieException(1L));
        when(guaraniBeneficioService.findByRequisitos(List.of(99)))
                .thenReturn(List.of(GuaraniBeneficio.builder().requisito(99).porcentajeBeneficio(new BigDecimal("30")).build()));
        when(beneficioPolicy.porcentajeEfectivo(eq(List.of(requisito)), any())).thenReturn(new BigDecimal("30"));
        when(chequeraSerieControlService.findLastByTipoChequera(3, 2)).thenThrow(new um.tesoreria.core.exception.ChequeraSerieControlException(3, 2));
        when(chequeraSerieControlService.add(any())).thenReturn(control);
        when(policy.createSerie(context, control, new BigDecimal("30"))).thenReturn(serie);
        when(chequeraSerieService.add(serie)).thenReturn(serie);

        assertThat(service.create(data)).isSameAs(serie);
        assertThat(serie.getBecaPorcentaje()).isEqualByComparingTo(new BigDecimal("30"));
        verify(detailsCreator).create(serie);
    }

    @Test
    void create_emitsWithZeroBenefitWhenRequirementsAreAbsent() {
        var data = new PreuniversitarioChequeraData(10, 20, 30, BigDecimal.ONE, 50, List.of());
        var context = context();
        var control = control();
        var serie = ChequeraSerie.builder().facultadId(3).tipoChequeraId(2).chequeraSerieId(1L)
                .becaPorcentaje(BigDecimal.ZERO).build();
        stubNewSeriesCreation(data, context, control, serie, BigDecimal.ZERO);

        assertThat(service.create(data)).isSameAs(serie);

        verifyNoInteractions(guaraniBeneficioService);
        verify(beneficioPolicy, never()).porcentajeEfectivo(any(), any());
        verify(policy).createSerie(context, control, BigDecimal.ZERO);
        verify(detailsCreator).create(serie);
    }

    @Test
    void create_emitsWithZeroBenefitWhenBenefitLookupFails() {
        var requisito = RequisitoPresentadoGuarani.builder().requisito(99).persona(1)
                .requisitoRel(RequisitoGuarani.builder().requisitoIngreso("S").activo("S").build()).build();
        var data = new PreuniversitarioChequeraData(10, 20, 30, BigDecimal.ONE, 50, List.of(requisito));
        var context = context();
        var control = control();
        var serie = ChequeraSerie.builder().facultadId(3).tipoChequeraId(2).chequeraSerieId(1L)
                .becaPorcentaje(BigDecimal.ZERO).build();
        stubNewSeriesCreation(data, context, control, serie, BigDecimal.ZERO);
        when(guaraniBeneficioService.findByRequisitos(List.of(99))).thenThrow(new IllegalStateException("db down"));

        assertThat(service.create(data)).isSameAs(serie);

        verify(policy).createSerie(context, control, BigDecimal.ZERO);
        verify(detailsCreator).create(serie);
    }

    @Test
    void create_emitsWithZeroBenefitWhenALegacyBenefitHasNoPercentage() {
        var requisito = RequisitoPresentadoGuarani.builder().requisito(99).persona(1)
                .requisitoRel(RequisitoGuarani.builder().requisitoIngreso("S").activo("S").build()).build();
        var data = new PreuniversitarioChequeraData(10, 20, 30, BigDecimal.ONE, 50, List.of(requisito));
        var context = context();
        var control = control();
        var serie = ChequeraSerie.builder().facultadId(3).tipoChequeraId(2).chequeraSerieId(1L)
                .becaPorcentaje(BigDecimal.ZERO).build();
        stubNewSeriesCreation(data, context, control, serie, BigDecimal.ZERO);
        var beneficioIncompleto = GuaraniBeneficio.builder().requisito(99).porcentajeBeneficio(null).build();
        when(guaraniBeneficioService.findByRequisitos(List.of(99))).thenReturn(List.of(beneficioIncompleto));
        when(beneficioPolicy.porcentajeEfectivo(List.of(requisito), List.of(beneficioIncompleto)))
                .thenReturn(BigDecimal.ZERO);

        assertThat(service.create(data)).isSameAs(serie);

        verify(policy).createSerie(context, control, BigDecimal.ZERO);
        verify(detailsCreator).create(serie);
    }

    private void stubNewSeriesCreation(PreuniversitarioChequeraData data, PreuniversitarioChequeraContext context,
                                       ChequeraSerieControl control, ChequeraSerie serie, BigDecimal beneficio) {
        when(dataResolver.resolve(data)).thenReturn(Optional.of(context));
        when(legajoManager.findOrCreate(context)).thenReturn(mock(Legajo.class));
        when(chequeraSerieService.findPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
                BigDecimal.ONE, 50, 3, 1, 4)).thenThrow(new ChequeraSerieException(1L));
        when(chequeraSerieControlService.findLastByTipoChequera(3, 2))
                .thenThrow(new um.tesoreria.core.exception.ChequeraSerieControlException(3, 2));
        when(chequeraSerieControlService.add(any())).thenReturn(control);
        when(policy.createSerie(context, control, beneficio)).thenReturn(serie);
        when(chequeraSerieService.add(serie)).thenReturn(serie);
    }

    private static PreuniversitarioChequeraContext context() {
        return new PreuniversitarioChequeraContext(1, 2, 3, 4, BigDecimal.ONE, 50, new Build(1L));
    }

    private static ChequeraSerieControl control() {
        return new ChequeraSerieControl(null, 3, 2, 1L, (byte) 0, 0, 0, 0L, (byte) 0);
    }
}
