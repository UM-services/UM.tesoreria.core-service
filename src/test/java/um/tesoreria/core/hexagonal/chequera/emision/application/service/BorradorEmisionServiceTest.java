package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.service.ArancelTipoService;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;
import um.tesoreria.core.hexagonal.chequera.baja.application.service.BajaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorCuota;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ArancelTipoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.CrearBorradorData;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.dependencias.geografica.application.service.GeograficaService;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.personas.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.setup.application.service.SetupService;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.ports.in.GetPermisosEfectivosUseCase;
import um.tesoreria.core.service.LectivoAlternativaService;
import um.tesoreria.core.service.view.PlantillaArancelService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
    private LectivoService lectivoService;
    @Mock
    private GeograficaService geograficaService;
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
                .thenReturn(TipoChequera.builder().tipoChequeraId(2).nombre("Cuota").geograficaId(1).build());
        when(lectivoAlternativaService.findAllByFacultadLectivoTipo(5, 38, 2)).thenReturn(List.of());
        when(arancelTipoService.findAllHabilitados()).thenReturn(List.of());
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(5, 38, 2)).thenReturn(List.of());
        when(chequeraSerieService.findAllByPersonaLectivo(any(), any(), any())).thenReturn(List.of());
        when(lectivoService.findByLectivoId(38))
                .thenReturn(um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo.builder()
                        .lectivoId(38).nombre("Lectivo 2026 - 2027").build());
        when(personaService.findByUnique(any(), any()))
                .thenReturn(um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona.builder()
                        .apellido("González").nombre("Manuel").build());
        when(geograficaService.findByGeograficaId(1))
                .thenReturn(java.util.Optional.of(new um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica(
                        1, "Centro", null)));

        BorradorChequera borrador = service.enriquecer(borrador());

        assertEquals("Ingeniería", borrador.getFacultadNombre());
        assertEquals("Cuota", borrador.getTipoChequeraNombre());
        assertEquals("González", borrador.getPersonaApellido());
        assertEquals("Manuel", borrador.getPersonaNombre());
        assertEquals("Lectivo 2026 - 2027", borrador.getLectivoNombre());
        assertEquals(1, borrador.getGeograficaId());
        assertEquals("Centro", borrador.getGeograficaNombre());
    }

    @Test
    void noRompeElEnriquecidoSiElCatalogoNoResuelveLosNombres() {
        when(facultadService.findByFacultadId(5)).thenThrow(new RuntimeException("facultad inexistente"));
        when(tipoChequeraService.findByTipoChequeraId(2)).thenThrow(new RuntimeException("tipo inexistente"));
        when(lectivoAlternativaService.findAllByFacultadLectivoTipo(5, 38, 2)).thenReturn(List.of());
        when(arancelTipoService.findAllHabilitados()).thenReturn(List.of());
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(5, 38, 2)).thenReturn(List.of());
        when(chequeraSerieService.findAllByPersonaLectivo(any(), any(), any())).thenReturn(List.of());

        BorradorChequera borrador = service.enriquecer(borrador());

        assertNull(borrador.getFacultadNombre());
        assertNull(borrador.getTipoChequeraNombre());
        assertNotNull(borrador.getAvisos());
        assertTrue(borrador.getAvisos().stream().anyMatch(aviso -> aviso.isBloqueante()));
    }

    @Test
    void creaElBorradorPreservandoLaBecaYElHpumDeLaSerie() {
        ChequeraSerie serie = ChequeraSerie.builder()
                .chequeraId(1L)
                .facultadId(5)
                .tipoChequeraId(2)
                .chequeraSerieId(10L)
                .personaId(new BigDecimal("49024730"))
                .documentoId(1)
                .lectivoId(38)
                .arancelTipoId(3)
                .alternativaId(2)
                .cursoId(7)
                .hpum((byte) 5)
                .becaPorcentaje(new BigDecimal("0.30"))
                .becaResolucion("RES-2026-001")
                .becaFecha(OffsetDateTime.parse("2026-03-01T00:00:00-03:00"))
                .becaUserId(77L)
                .build();
        when(personaService.findByUnique(any(), any()))
                .thenReturn(Persona.builder().personaId(new BigDecimal("49024730")).documentoId(1)
                        .apellido("Pérez").nombre("Ana").hpum((byte) 9).build());
        when(tipoChequeraService.findByTipoChequeraId(2))
                .thenReturn(TipoChequera.builder().tipoChequeraId(2).nombre("Cuota").build());
        when(chequeraSerieService.findAllByPersonaLectivo(any(), any(), eq(38))).thenReturn(List.of(serie));
        when(domicilioService.findByUnique(any(), any())).thenThrow(new RuntimeException("sin domicilio"));
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(5, 38, 2)).thenReturn(List.of());
        when(chequeraCuotaService.findAllByChequera(5, 2, 10L)).thenReturn(List.of());
        when(borradorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(facultadService.findByFacultadId(5)).thenThrow(new RuntimeException("sin facultad"));
        when(arancelTipoService.findAllHabilitados()).thenReturn(List.of());

        BorradorChequera borrador = service.crearBorrador(CrearBorradorData.builder()
                .personaId(new BigDecimal("49024730"))
                .documentoId(1)
                .facultadId(5)
                .lectivoId(38)
                .tipoChequeraId(2)
                .chequeraSerieId(10L)
                .arancelTipoId(3)
                .build(), "tester");

        assertEquals(new BigDecimal("0.30"), borrador.getBecaPorcentaje());
        assertEquals("RES-2026-001", borrador.getBecaResolucion());
        assertEquals(OffsetDateTime.parse("2026-03-01T00:00:00-03:00"), borrador.getBecaFecha());
        assertEquals(77L, borrador.getBecaUserId());
        assertEquals((byte) 5, borrador.getHpum());
        assertEquals(2, borrador.getAlternativaId());
    }

    @Test
    void arancelTiposSoloOfreceLosHabilitadosEnLosLectivosNuevos() {
        var habilitado = ArancelTipoEntity.builder()
                .arancelTipoId(9).descripcion("Ciclo Completo 50% HPUM").habilitado((byte) 1).build();
        var deshabilitado = ArancelTipoEntity.builder()
                .arancelTipoId(3).descripcion("Ciclo Completo + 1/4 Beca").habilitado((byte) 0).build();
        when(arancelTipoService.findAllHabilitados()).thenReturn(List.of(habilitado));
        when(arancelTipoService.findAll()).thenReturn(List.of(deshabilitado, habilitado));
        when(arancelTipoService.findByArancelTipoId(3)).thenReturn(deshabilitado);

        assertEquals(List.of(9), ids(service.arancelTipos(38, null)));
        assertEquals(List.of(3, 9), ids(service.arancelTipos(36, null)));
        // El arancel ya elegido por el borrador se ofrece aunque hoy esté deshabilitado.
        assertEquals(List.of(3, 9), ids(service.arancelTipos(38, 3)));
    }

    private static List<Integer> ids(List<ArancelTipoEmision> tipos) {
        return tipos.stream().map(ArancelTipoEmision::getArancelTipoId).toList();
    }

    private BorradorChequera borrador() {        return BorradorChequera.builder()
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

    @Test
    void retomaElBorradorAbiertoDeLaSerieEnLugarDeDuplicarlo() {
        ChequeraSerie serie = ChequeraSerie.builder()
                .chequeraId(100L).facultadId(5).tipoChequeraId(2).chequeraSerieId(10L)
                .personaId(new BigDecimal("49024730")).documentoId(1).lectivoId(38)
                .arancelTipoId(3).alternativaId(2).cursoId(7)
                .build();
        BorradorChequera abierto = BorradorChequera.builder()
                .borradorId(55L).estado(EstadoBorrador.ABIERTO)
                .personaId(new BigDecimal("49024730")).documentoId(1)
                .facultadId(5).lectivoId(38).tipoChequeraId(2).chequeraSerieId(10L)
                .arancelTipoId(3)
                .build();
        when(personaService.findByUnique(any(), any()))
                .thenReturn(um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona.builder()
                        .apellido("Pérez").nombre("Ana").build());
        when(tipoChequeraService.findByTipoChequeraId(2))
                .thenReturn(TipoChequera.builder().tipoChequeraId(2).nombre("Cuota").build());
        when(chequeraSerieService.findAllByPersonaLectivo(any(), any(), any())).thenReturn(List.of(serie));
        when(borradorRepository.findAbiertoBySerie(5, 2, 10L)).thenReturn(java.util.Optional.of(abierto));
        when(borradorRepository.findById(55L)).thenReturn(java.util.Optional.of(abierto));
        when(borradorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(lectivoAlternativaService.findAllByFacultadLectivoTipo(any(), any(), any())).thenReturn(List.of());
        when(arancelTipoService.findAllHabilitados()).thenReturn(List.of());
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(any(), any(), any())).thenReturn(List.of());

        BorradorChequera resultado = service.crearBorrador(CrearBorradorData.builder()
                .personaId(new BigDecimal("49024730")).documentoId(1)
                .facultadId(5).lectivoId(38).tipoChequeraId(2)
                .chequeraSerieId(10L).arancelTipoId(3)
                .build(), "tester");

        assertEquals(55L, resultado.getBorradorId());
        // Retomar un borrador abierto completa sus cuotas faltantes (y persiste el resultado).
        verify(borradorRepository).save(any());
    }

    @Test
    void retomaElBorradorAbiertoDeUnaChequeraQueTodaviaNoExiste() {
        BorradorChequera abierto = BorradorChequera.builder()
                .borradorId(77L).estado(EstadoBorrador.ABIERTO)
                .personaId(new BigDecimal("49024730")).documentoId(1)
                .facultadId(5).lectivoId(38).tipoChequeraId(2)
                .build();
        when(personaService.findByUnique(any(), any()))
                .thenReturn(um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona.builder()
                        .apellido("Pérez").nombre("Ana").build());
        when(tipoChequeraService.findByTipoChequeraId(2))
                .thenReturn(TipoChequera.builder().tipoChequeraId(2).nombre("Cuota").build());
        when(chequeraSerieService.findAllByPersonaLectivo(any(), any(), any())).thenReturn(List.of());
        when(borradorRepository.findAbiertoByNueva(new BigDecimal("49024730"), 1, 38, 5, 2))
                .thenReturn(java.util.Optional.of(abierto));
        when(borradorRepository.findById(77L)).thenReturn(java.util.Optional.of(abierto));
        when(borradorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(lectivoAlternativaService.findAllByFacultadLectivoTipo(any(), any(), any())).thenReturn(List.of());
        when(arancelTipoService.findAllHabilitados()).thenReturn(List.of());
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(any(), any(), any())).thenReturn(List.of());

        BorradorChequera resultado = service.crearBorrador(CrearBorradorData.builder()
                .personaId(new BigDecimal("49024730")).documentoId(1)
                .facultadId(5).lectivoId(38).tipoChequeraId(2)
                .build(), "tester");

        assertEquals(77L, resultado.getBorradorId());
        verify(borradorRepository).save(any());
    }

    @Test
    void heredaElArancelDeLaSerieCuandoElRequestNoLoTre() {
        ChequeraSerie serie = ChequeraSerie.builder()
                .chequeraId(100L).facultadId(5).tipoChequeraId(2).chequeraSerieId(10L)
                .personaId(new BigDecimal("49024730")).documentoId(1).lectivoId(38)
                .arancelTipoId(9).alternativaId(2).cursoId(7)
                .build();
        when(personaService.findByUnique(any(), any()))
                .thenReturn(Persona.builder().apellido("Pérez").nombre("Ana").build());
        when(tipoChequeraService.findByTipoChequeraId(2))
                .thenReturn(TipoChequera.builder().tipoChequeraId(2).nombre("Cuota").build());
        when(chequeraSerieService.findAllByPersonaLectivo(any(), any(), any())).thenReturn(List.of(serie));
        when(bajaService.findDatosByUnique(5, 2, 10L)).thenReturn(java.util.Optional.empty());
        when(borradorRepository.findAbiertoBySerie(5, 2, 10L)).thenReturn(java.util.Optional.empty());
        when(domicilioService.findByUnique(any(), any())).thenThrow(new RuntimeException("sin domicilio"));
        when(lectivoAlternativaService.findAllByFacultadLectivoTipo(any(), any(), any())).thenReturn(List.of());
        when(arancelTipoService.findAllHabilitados()).thenReturn(List.of());
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(any(), any(), any())).thenReturn(List.of());
        when(facultadService.findByFacultadId(5)).thenThrow(new RuntimeException("sin facultad"));
        when(borradorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BorradorChequera borrador = service.crearBorrador(CrearBorradorData.builder()
                .personaId(new BigDecimal("49024730")).documentoId(1)
                .facultadId(5).lectivoId(38).tipoChequeraId(2)
                .chequeraSerieId(10L)
                .build(), "tester");

        assertEquals(9, borrador.getArancelTipoId());
        // El resto de la cabecera sigue heredándose de la serie.
        assertEquals(2, borrador.getAlternativaId());
        assertEquals(7, borrador.getCursoId());
    }

    @Test
    void reparaElBorradorAbiertoQueQuedoConArancelCero() {
        ChequeraSerie serie = ChequeraSerie.builder()
                .chequeraId(100L).facultadId(5).tipoChequeraId(2).chequeraSerieId(10L)
                .personaId(new BigDecimal("49024730")).documentoId(1).lectivoId(38)
                .arancelTipoId(9).alternativaId(2).cursoId(7)
                .build();
        BorradorChequera abierto = BorradorChequera.builder()
                .borradorId(55L).estado(EstadoBorrador.ABIERTO)
                .personaId(new BigDecimal("49024730")).documentoId(1)
                .facultadId(5).lectivoId(38).tipoChequeraId(2).chequeraSerieId(10L)
                .arancelTipoId(0)
                .cuotas(List.of(BorradorCuota.builder()
                        .productoId(3).alternativaId(2).cuotaId(1).mes(3).anio(2026)
                        .arancelTipoId(0)
                        .importe1Original(new BigDecimal("3000.00")).importe1Borrador(new BigDecimal("3000.00"))
                        .build()))
                .build();
        BorradorCuota reparada = BorradorCuota.builder()
                .productoId(3).alternativaId(2).cuotaId(1).mes(3).anio(2026)
                .arancelTipoId(9)
                .importe1Original(new BigDecimal("3000.00")).importe1Borrador(new BigDecimal("2700.00"))
                .build();
        when(personaService.findByUnique(any(), any()))
                .thenReturn(Persona.builder().apellido("Pérez").nombre("Ana").build());
        when(tipoChequeraService.findByTipoChequeraId(2))
                .thenReturn(TipoChequera.builder().tipoChequeraId(2).nombre("Cuota").build());
        when(chequeraSerieService.findAllByPersonaLectivo(any(), any(), any())).thenReturn(List.of(serie));
        when(bajaService.findDatosByUnique(5, 2, 10L)).thenReturn(java.util.Optional.empty());
        when(borradorRepository.findAbiertoBySerie(5, 2, 10L)).thenReturn(java.util.Optional.of(abierto));
        when(borradorRepository.findById(55L)).thenReturn(java.util.Optional.of(abierto));
        when(borradorFactory.construirDesdeBorrador(any(), any())).thenReturn(List.of(reparada));
        when(borradorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(lectivoAlternativaService.findAllByFacultadLectivoTipo(any(), any(), any())).thenReturn(List.of());
        when(arancelTipoService.findAllHabilitados()).thenReturn(List.of());
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(any(), any(), any())).thenReturn(List.of());
        when(facultadService.findByFacultadId(5)).thenThrow(new RuntimeException("sin facultad"));

        BorradorChequera resultado = service.crearBorrador(CrearBorradorData.builder()
                .personaId(new BigDecimal("49024730")).documentoId(1)
                .facultadId(5).lectivoId(38).tipoChequeraId(2)
                .chequeraSerieId(10L)
                .build(), "tester");

        assertEquals(9, resultado.getArancelTipoId());
        assertEquals(9, resultado.getCuotas().get(0).getArancelTipoId());
        verify(borradorRepository, atLeastOnce()).save(any());
    }
}
