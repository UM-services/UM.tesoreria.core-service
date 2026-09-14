package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.ContactoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.DocumentoPrincipalGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PersonaGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.TipoDocumentoGuarani;
import um.tesoreria.core.hexagonal.personas.domicilio.application.exception.DomicilioException;
import um.tesoreria.core.hexagonal.personas.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.personas.documento.application.service.DocumentoService;
import um.tesoreria.core.hexagonal.personas.documento.domain.model.Documento;
import um.tesoreria.core.hexagonal.personas.persona.application.exception.PersonaException;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePersonalesUseCaseImplTest {

    @Mock
    private DocumentoService documentoService;

    @Mock
    private PersonaService personaService;

    @Mock
    private DomicilioService domicilioService;

    @InjectMocks
    private CreatePersonalesUseCaseImpl useCase;

    private AlumnoGuarani alumnoGuarani(String nroDocumento, String numeroPrefijo, String numeroPosfijo, Long guaraniPersona) {
        return AlumnoGuarani.builder()
                .personaRel(PersonaGuarani.builder()
                        .persona(guaraniPersona)
                        .apellido("García")
                        .nombres("Juan")
                        .sexo("M")
                        .numeroPrefijo(numeroPrefijo)
                        .numeroPosfijo(numeroPosfijo)
                        .documentoPrincipalRel(DocumentoPrincipalGuarani.builder()
                                .nroDocumento(nroDocumento)
                                .tipoDocumentoRel(TipoDocumentoGuarani.builder().tipoDocumento(9).build())
                                .build())
                        .contactos(List.of(ContactoGuarani.builder().email("juan@test.um").build()))
                        .build())
                .build();
    }

    private Persona personaExistente(String numeroPrefijo, String numeroPosfijo, Long guaraniPersona) {
        return Persona.builder()
                .uniqueId(77L)
                .personaId(new BigDecimal("1234567"))
                .documentoId(2)
                .apellido("García")
                .nombre("Juan")
                .sexo("M")
                .cbu("")
                .cuit("")
                .hpum((byte) 0)
                .primero((byte) 0)
                .numeroPrefijo(numeroPrefijo)
                .numeroPosfijo(numeroPosfijo)
                .guaraniPersona(guaraniPersona)
                .build();
    }

    private void stubTipoDocumento() {
        when(documentoService.findFirstByGuaraniTipoDocumento(9)).thenReturn(Documento.builder().documentoId(2).build());
    }

    @Test
    void createPersonales_nuevaPersona_persistePrefijoPosfijoYGuaraniPersona() {
        BigDecimal personaId = new BigDecimal("1234567");
        stubTipoDocumento();
        when(personaService.findByUnique(personaId, 2)).thenThrow(new PersonaException(personaId, 2));
        when(personaService.create(any(Persona.class))).thenAnswer(inv -> inv.getArgument(0));
        when(domicilioService.findByUnique(personaId, 2)).thenThrow(new DomicilioException(personaId, 2));
        when(domicilioService.create(any(Domicilio.class))).thenAnswer(inv -> inv.getArgument(0));

        var resultado = useCase.createPersonales(alumnoGuarani("1234567", "AA", "B", 4500L));

        assertThat(resultado.getResult()).isTrue();
        ArgumentCaptor<Persona> captor = ArgumentCaptor.forClass(Persona.class);
        verify(personaService).create(captor.capture());
        Persona creada = captor.getValue();
        assertThat(creada.getPersonaId()).isEqualByComparingTo("1234567");
        assertThat(creada.getNumeroPrefijo()).isEqualTo("AA");
        assertThat(creada.getNumeroPosfijo()).isEqualTo("B");
        assertThat(creada.getGuaraniPersona()).isEqualTo(4500L);
    }

    @Test
    void createPersonales_documentoConLetras_devuelveFalseYNoCreaNada() {
        var resultado = useCase.createPersonales(alumnoGuarani("AA1234567", "AA", "", 4500L));

        assertThat(resultado.getResult()).isFalse();
        assertThat(resultado.getPersona()).isNull();
        verifyNoInteractions(documentoService, personaService, domicilioService);
    }

    @Test
    void createPersonales_personaExistenteSinPrefijoPosfijo_sincroniza() {
        BigDecimal personaId = new BigDecimal("1234567");
        stubTipoDocumento();
        when(personaService.findByUnique(personaId, 2)).thenReturn(personaExistente("", "", null));
        when(personaService.create(any(Persona.class))).thenAnswer(inv -> inv.getArgument(0));
        when(domicilioService.findByUnique(personaId, 2)).thenReturn(Domicilio.builder().domicilioId(5L).emailPersonal("juan@test.um").build());

        var resultado = useCase.createPersonales(alumnoGuarani("1234567", "AA", "B", 4500L));

        assertThat(resultado.getResult()).isTrue();
        ArgumentCaptor<Persona> captor = ArgumentCaptor.forClass(Persona.class);
        verify(personaService).create(captor.capture());
        Persona sincronizada = captor.getValue();
        assertThat(sincronizada.getUniqueId()).isEqualTo(77L);
        assertThat(sincronizada.getNumeroPrefijo()).isEqualTo("AA");
        assertThat(sincronizada.getNumeroPosfijo()).isEqualTo("B");
        assertThat(sincronizada.getGuaraniPersona()).isEqualTo(4500L);
    }

    @Test
    void createPersonales_personaExistenteConValoresManuales_noPisa() {
        BigDecimal personaId = new BigDecimal("1234567");
        stubTipoDocumento();
        when(personaService.findByUnique(personaId, 2)).thenReturn(personaExistente("XY", "Z", 999L));
        when(domicilioService.findByUnique(personaId, 2)).thenReturn(Domicilio.builder().domicilioId(5L).emailPersonal("juan@test.um").build());

        var resultado = useCase.createPersonales(alumnoGuarani("1234567", "AA", "B", 4500L));

        assertThat(resultado.getResult()).isTrue();
        verify(personaService, never()).create(any(Persona.class));
    }

    @Test
    void createPersonales_personaExistenteSoloFaltaGuaraniPersona_sincronizaSoloEseCampo() {
        BigDecimal personaId = new BigDecimal("1234567");
        stubTipoDocumento();
        when(personaService.findByUnique(personaId, 2)).thenReturn(personaExistente("XY", "Z", null));
        when(personaService.create(any(Persona.class))).thenAnswer(inv -> inv.getArgument(0));
        when(domicilioService.findByUnique(personaId, 2)).thenReturn(Domicilio.builder().domicilioId(5L).emailPersonal("juan@test.um").build());

        var resultado = useCase.createPersonales(alumnoGuarani("1234567", "AA", "B", 4500L));

        assertThat(resultado.getResult()).isTrue();
        ArgumentCaptor<Persona> captor = ArgumentCaptor.forClass(Persona.class);
        verify(personaService).create(captor.capture());
        Persona sincronizada = captor.getValue();
        assertThat(sincronizada.getNumeroPrefijo()).isEqualTo("XY");
        assertThat(sincronizada.getNumeroPosfijo()).isEqualTo("Z");
        assertThat(sincronizada.getGuaraniPersona()).isEqualTo(4500L);
    }

    @Test
    void createPersonales_nuevaPersona_extraeContactosCompletosHaciaDomicilio() {
        BigDecimal personaId = new BigDecimal("1234567");
        stubTipoDocumento();
        when(personaService.findByUnique(personaId, 2)).thenThrow(new PersonaException(personaId, 2));
        when(personaService.create(any(Persona.class))).thenAnswer(inv -> inv.getArgument(0));
        when(domicilioService.findByUnique(personaId, 2)).thenThrow(new DomicilioException(personaId, 2));
        when(domicilioService.create(any(Domicilio.class))).thenAnswer(inv -> inv.getArgument(0));

        var alumno = AlumnoGuarani.builder()
                .personaRel(PersonaGuarani.builder()
                        .persona(5000L)
                        .apellido("Sepúlveda")
                        .nombres("Javiera")
                        .sexo("F")
                        .documentoPrincipalRel(DocumentoPrincipalGuarani.builder()
                                .nroDocumento("1234567")
                                .tipoDocumentoRel(TipoDocumentoGuarani.builder().tipoDocumento(9).build())
                                .build())
                        .contactos(List.of(
                                ContactoGuarani.builder()
                                        .contactoTipo("C")
                                        .telefonoCodigoArea("569")
                                        .telefonoNumero("56130877")
                                        .build(),
                                ContactoGuarani.builder()
                                        .contactoTipo("MP")
                                        .email("javisepes@gmail.com")
                                        .build(),
                                ContactoGuarani.builder()
                                        .contactoTipo("MI")
                                        .email("javiera.sepulveda@um.edu.ar")
                                        .build(),
                                ContactoGuarani.builder()
                                        .contactoTipo("TF")
                                        .telefonoCodigoArea("261")
                                        .telefonoNumero("4202020")
                                        .build(),
                                ContactoGuarani.builder()
                                        .contactoTipo("TL")
                                        .telefonoNumero("4112233")
                                        .build(),
                                ContactoGuarani.builder()
                                        .contactoTipo("OT")
                                        .otrosContactos("Contacto de emergencia mamá")
                                        .build()
                        ))
                        .build())
                .build();

        var resultado = useCase.createPersonales(alumno);

        assertThat(resultado.getResult()).isTrue();
        ArgumentCaptor<Domicilio> captor = ArgumentCaptor.forClass(Domicilio.class);
        verify(domicilioService).create(captor.capture());
        Domicilio domicilioCreado = captor.getValue();
        assertThat(domicilioCreado.getEmailPersonal()).isEqualTo("javisepes@gmail.com");
        assertThat(domicilioCreado.getEmailInstitucional()).isEqualTo("javiera.sepulveda@um.edu.ar");
        assertThat(domicilioCreado.getMovil()).isEqualTo("(569) 56130877");
        assertThat(domicilioCreado.getTelefono()).isEqualTo("(261) 4202020");
        assertThat(domicilioCreado.getLaboral()).isEqualTo("4112233");
        assertThat(domicilioCreado.getObservaciones()).isEqualTo("Contacto de emergencia mamá");
    }

    @Test
    void createPersonales_domicilioExistente_sincronizaContactosSinPisarExistentes() {
        BigDecimal personaId = new BigDecimal("1234567");
        stubTipoDocumento();
        when(personaService.findByUnique(personaId, 2)).thenReturn(personaExistente("", "", 5000L));
        when(domicilioService.findByUnique(personaId, 2)).thenReturn(Domicilio.builder()
                .domicilioId(10L)
                .personaId(personaId)
                .documentoId(2)
                .emailPersonal("existente@um.edu.ar") // Ya existía en Tesorería
                .movil("") // Vacío, debe sincronizarse
                .telefono("") // Vacío, debe sincronizarse
                .build());
        when(domicilioService.create(any(Domicilio.class))).thenAnswer(inv -> inv.getArgument(0));

        var alumno = AlumnoGuarani.builder()
                .personaRel(PersonaGuarani.builder()
                        .persona(5000L)
                        .apellido("Sepúlveda")
                        .nombres("Javiera")
                        .sexo("F")
                        .documentoPrincipalRel(DocumentoPrincipalGuarani.builder()
                                .nroDocumento("1234567")
                                .tipoDocumentoRel(TipoDocumentoGuarani.builder().tipoDocumento(9).build())
                                .build())
                        .contactos(List.of(
                                ContactoGuarani.builder()
                                        .contactoTipo("C")
                                        .telefonoCodigoArea("569")
                                        .telefonoNumero("56130877")
                                        .build(),
                                ContactoGuarani.builder()
                                        .contactoTipo("MP")
                                        .email("nuevo@gmail.com")
                                        .build(),
                                ContactoGuarani.builder()
                                        .contactoTipo("TF")
                                        .telefonoNumero("4202020")
                                        .build()
                        ))
                        .build())
                .build();

        var resultado = useCase.createPersonales(alumno);

        assertThat(resultado.getResult()).isTrue();
        ArgumentCaptor<Domicilio> captor = ArgumentCaptor.forClass(Domicilio.class);
        verify(domicilioService).create(captor.capture());
        Domicilio domicilioSincronizado = captor.getValue();
        // El email personal NO debe pisar el existente
        assertThat(domicilioSincronizado.getEmailPersonal()).isEqualTo("existente@um.edu.ar");
        // El móvil y teléfono vacíos SÍ deben sincronizarse
        assertThat(domicilioSincronizado.getMovil()).isEqualTo("(569) 56130877");
        assertThat(domicilioSincronizado.getTelefono()).isEqualTo("4202020");
    }

}
