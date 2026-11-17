package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.service.ArancelTipoService;
import um.tesoreria.core.hexagonal.chequera.baja.application.service.BajaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.personas.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.setup.application.service.SetupService;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.ports.in.GetPermisosEfectivosUseCase;
import um.tesoreria.core.service.LectivoAlternativaService;
import um.tesoreria.core.service.view.PlantillaArancelService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorradorEmisionServiceTest {

    @Mock
    private PersonaService personaService;
    @Mock
    private DomicilioService domicilioService;
    @Mock
    private SetupService setupService;
    @Mock
    private ChequeraSerieService chequeraSerieService;
    @Mock
    private ChequeraCuotaService chequeraCuotaService;
    @Mock
    private ChequeraPagoService chequeraPagoService;
    @Mock
    private BajaService bajaService;
    @Mock
    private LectivoCuotaService lectivoCuotaService;
    @Mock
    private LectivoAlternativaService lectivoAlternativaService;
    @Mock
    private TipoChequeraService tipoChequeraService;
    @Mock
    private FacultadService facultadService;
    @Mock
    private ArancelTipoService arancelTipoService;
    @Mock
    private PlantillaArancelService plantillaArancelService;
    @Mock
    private GetPermisosEfectivosUseCase getPermisosEfectivosUseCase;
    @Mock
    private BorradorFactory borradorFactory;
    @Mock
    private BorradorChequeraRepository borradorRepository;

    @InjectMocks
    private BorradorEmisionService service;

    @Test
    void enriqueceElBorradorConElNombreDeFacultadYTipoDeChequera() {
        when(facultadService.findByFacultadId(5))
                .thenReturn(Facultad.builder().facultadId(5).nombre("Ingeniería").build());
        when(tipoChequeraService.findByTipoChequeraId(2))
                .thenReturn(TipoChequera.builder().tipoChequeraId(2).nombre("Cuota").build());
        when(lectivoAlternativaService.findAllByFacultadLectivoTipo(5, 38, 2)).thenReturn(List.of());
        when(arancelTipoService.findAll()).thenReturn(List.of());
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(5, 38, 2)).thenReturn(List.of());
        when(chequeraSerieService.findAllByPersonaLectivo(any(), any(), any())).thenReturn(List.of());

        BorradorChequera borrador = service.enriquecer(borrador());

        assertEquals("Ingeniería", borrador.getFacultadNombre());
        assertEquals("Cuota", borrador.getTipoChequeraNombre());
    }

    @Test
    void noRompeElEnriquecidoSiElCatalogoNoResuelveLosNombres() {
        when(facultadService.findByFacultadId(5)).thenThrow(new RuntimeException("facultad inexistente"));
        when(tipoChequeraService.findByTipoChequeraId(2)).thenThrow(new RuntimeException("tipo inexistente"));
        when(lectivoAlternativaService.findAllByFacultadLectivoTipo(5, 38, 2)).thenReturn(List.of());
        when(arancelTipoService.findAll()).thenReturn(List.of());
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(5, 38, 2)).thenReturn(List.of());
        when(chequeraSerieService.findAllByPersonaLectivo(any(), any(), any())).thenReturn(List.of());

        BorradorChequera borrador = service.enriquecer(borrador());

        assertNull(borrador.getFacultadNombre());
        assertNull(borrador.getTipoChequeraNombre());
        assertNotNull(borrador.getAvisos());
        assertTrue(borrador.getAvisos().stream().anyMatch(aviso -> aviso.isBloqueante()));
    }

    private BorradorChequera borrador() {
        return BorradorChequera.builder()
                .borradorId(1L)
                .personaId(new BigDecimal("49024730"))
                .documentoId(1)
                .facultadId(5)
                .lectivoId(38)
                .tipoChequeraId(2)
                .alternativaId(2)
                .emailPersonal("manuel.gonzalez@um.edu.ar")
                .build();
    }
}
