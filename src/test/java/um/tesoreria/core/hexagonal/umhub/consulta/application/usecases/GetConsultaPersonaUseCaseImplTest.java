package um.tesoreria.core.hexagonal.umhub.consulta.application.usecases;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.personas.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.personas.documento.application.service.DocumentoService;
import um.tesoreria.core.hexagonal.personas.documento.domain.model.Documento;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaPersona;
import um.tesoreria.core.service.LocalidadService;
import um.tesoreria.core.service.ProvinciaService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetConsultaPersonaUseCaseImplTest {

    private static final BigDecimal NUMERO = new BigDecimal("30123456");

    private final PersonaService personaService = mock(PersonaService.class);
    private final DomicilioService domicilioService = mock(DomicilioService.class);
    private final DocumentoService documentoService = mock(DocumentoService.class);
    private final ProvinciaService provinciaService = mock(ProvinciaService.class);
    private final LocalidadService localidadService = mock(LocalidadService.class);

    private final GetConsultaPersonaUseCaseImpl useCase = new GetConsultaPersonaUseCaseImpl(
            personaService, domicilioService, documentoService, provinciaService, localidadService);

    private Persona persona(Integer documentoId, String apellido, String nombre) {
        return Persona.builder()
                .personaId(NUMERO)
                .documentoId(documentoId)
                .apellido(apellido)
                .nombre(nombre)
                .sexo("F")
                .build();
    }

    private Documento documento(Integer documentoId, String nombre) {
        return Documento.builder().documentoId(documentoId).nombre(nombre).build();
    }

    @Test
    void resuelveIdentidadTiposYContactoPorNumeroSinTipo() {
        when(personaService.findAllByNumeroDocumento(NUMERO))
                .thenReturn(List.of(persona(1, "GARCIA", "Maria"), persona(8, "Garcia", "MARIA")));
        when(documentoService.findByDocumentoId(anyInt())).thenAnswer(call ->
                documento(call.getArgument(0), call.<Integer>getArgument(0) == 1 ? "LC" : "DNI"));
        when(domicilioService.findFirstByPersonaId(NUMERO)).thenReturn(Optional.of(Domicilio.builder()
                .personaId(NUMERO).documentoId(8).facultadId(1).provinciaId(13).localidadId(255)
                .calle("Av. San Martin").puerta("1234").codigoPostal("M5500")
                .telefono("02614000000").movil("02615000000")
                .emailPersonal("maria@example.com").emailInstitucional("mgarcia@um.edu.ar")
                .build()));

        Optional<ConsultaPersona> result = useCase.findByNumeroDocumento(NUMERO);

        assertThat(result).isPresent();
        ConsultaPersona persona = result.get();
        assertThat(persona.getApellido()).isEqualTo("GARCIA");
        assertThat(persona.getDocumentos()).hasSize(2)
                .extracting("documentoId", "nombre")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(1, "LC"),
                        org.assertj.core.groups.Tuple.tuple(8, "DNI"));
        assertThat(persona.getDomicilio()).isNotNull();
        assertThat(persona.getDomicilio().getEmailPersonal()).isEqualTo("maria@example.com");
        assertThat(persona.getDomicilio().getMovil()).isEqualTo("02615000000");
        assertThat(persona.getDomicilio().getProvinciaNombre()).isNull();
        assertThat(persona.getDomicilio().getLocalidadNombre()).isNull();
    }

    @Test
    void devuelveVacioCuandoElNumeroNoTienePersonas() {
        when(personaService.findAllByNumeroDocumento(NUMERO)).thenReturn(List.of());

        assertThat(useCase.findByNumeroDocumento(NUMERO)).isEmpty();
    }

    @Test
    void conTitularesDivergentesExponeSoloLaIdentidadPrimaria() {
        when(personaService.findAllByNumeroDocumento(NUMERO))
                .thenReturn(List.of(persona(1, "PEREZ", "Juan"), persona(8, "GARCIA", "Maria")));
        when(documentoService.findByDocumentoId(anyInt()))
                .thenAnswer(call -> documento(call.getArgument(0), "TIPO"));
        when(domicilioService.findFirstByPersonaId(NUMERO)).thenReturn(Optional.empty());

        Optional<ConsultaPersona> result = useCase.findByNumeroDocumento(NUMERO);

        assertThat(result).isPresent();
        assertThat(result.get().getApellido()).isEqualTo("PEREZ");
        assertThat(result.get().getDocumentos())
                .extracting("documentoId").containsExactly(1);
    }

    @Test
    void nombreDeDocumentoFallidoNoRompeLaConsulta() {
        when(personaService.findAllByNumeroDocumento(NUMERO))
                .thenReturn(List.of(persona(8, "GARCIA", "Maria")));
        when(documentoService.findByDocumentoId(8)).thenThrow(new RuntimeException("documento inexistente"));
        when(domicilioService.findFirstByPersonaId(NUMERO)).thenReturn(Optional.empty());

        Optional<ConsultaPersona> result = useCase.findByNumeroDocumento(NUMERO);

        assertThat(result).isPresent();
        assertThat(result.get().getDocumentos()).singleElement()
                .satisfies(tipo -> assertThat(tipo.getNombre()).isNull());
        assertThat(result.get().getDomicilio()).isNull();
    }
}
