package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.application.usecases;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.PreuniversitarioChequeraData;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.CreatePreuniversitarioChequeraUseCase;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PersonaGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PropuestaGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PropuestaResponsableAcademicaGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.RequisitoPresentadoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.PersonalesResponse;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.PersonaResponse;
import um.tesoreria.core.service.facade.MailChequeraService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreatePreuniversitarioUseCaseImplTest {

    @Test
    void createPreuniversitario_keepsSendChequeraForHundredPercentBenefit() {
        var createChequera = mock(CreatePreuniversitarioChequeraUseCase.class);
        var mailChequera = mock(MailChequeraService.class);
        var request = request();
        var alumno = request.getAlumnoGuarani();
        var serie = ChequeraSerie.builder()
                .facultadId(1).tipoChequeraId(2).chequeraSerieId(3L).alternativaId(4)
                .becaPorcentaje(new BigDecimal("100")).justCreated(true).build();
        when(createChequera.create(any())).thenReturn(serie);

        assertThat(new CreatePreuniversitarioUseCaseImpl(createChequera, mailChequera)
                .createPreuniversitario(request)).isSameAs(alumno);

        var data = ArgumentCaptor.forClass(PreuniversitarioChequeraData.class);
        verify(createChequera).create(data.capture());
        assertThat(data.getValue().requisitosPresentados()).isEmpty();
        verify(mailChequera).sendChequera(1, 2, 3L, 4, false, false, false);
    }

    @Test
    void createPreuniversitario_passesPresentedRequirementsUnchangedToChequeraCreation() {
        var createChequera = mock(CreatePreuniversitarioChequeraUseCase.class);
        var mailChequera = mock(MailChequeraService.class);
        var request = request();
        var requisitos = List.of(RequisitoPresentadoGuarani.builder().persona(1).requisito(10).build());
        when(request.getAlumnoGuarani().getPersonaRel()).thenReturn(PersonaGuarani.builder()
                .requisitosPresentados(requisitos).build());
        when(createChequera.create(any())).thenReturn(null);

        new CreatePreuniversitarioUseCaseImpl(createChequera, mailChequera).createPreuniversitario(request);

        var data = ArgumentCaptor.forClass(PreuniversitarioChequeraData.class);
        verify(createChequera).create(data.capture());
        assertThat(data.getValue().requisitosPresentados()).isSameAs(requisitos);
    }

    private static PersonalesResponse request() {
        var alumno = mock(AlumnoGuarani.class);
        when(alumno.getPropuesta()).thenReturn(10);
        when(alumno.getUbicacion()).thenReturn(20);
        var responsable = mock(PropuestaResponsableAcademicaGuarani.class);
        when(responsable.getResponsableAcademica()).thenReturn(30);
        var propuesta = mock(PropuestaGuarani.class);
        when(propuesta.getResponsablesAcademicas()).thenReturn(List.of(responsable));
        var persona = mock(PersonaResponse.class);
        when(persona.getPersonaId()).thenReturn(BigDecimal.ONE);
        when(persona.getDocumentoId()).thenReturn(40);
        return PersonalesResponse.builder().alumnoGuarani(alumno).propuestaGuarani(propuesta).persona(persona).build();
    }
}
