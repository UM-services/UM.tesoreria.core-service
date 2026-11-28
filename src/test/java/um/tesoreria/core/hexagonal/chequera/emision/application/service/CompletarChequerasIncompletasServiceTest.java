package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.baja.application.service.BajaService;
import um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.entity.BajaEntity;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.CrearBorradorData;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FiltroIncompletas;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FuenteCompletado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasTareaEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoIncompleta;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.entity.PersonaEntity;
import um.tesoreria.core.model.view.ChequeraIncompleta;
import um.tesoreria.core.service.facade.ChequeraService;
import um.tesoreria.core.service.view.ChequeraIncompletaService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompletarChequerasIncompletasServiceTest {

    @Mock
    private ChequeraIncompletaService chequeraIncompletaService;
    @Mock
    private BajaService bajaService;
    @Mock
    private ChequeraSerieService chequeraSerieService;
    @Mock
    private ChequeraCuotaService chequeraCuotaService;
    @Mock
    private LectivoCuotaService lectivoCuotaService;
    @Mock
    private BorradorEmisionService borradorEmisionService;
    @Mock
    private BorradorChequeraRepository borradorRepository;
    @Mock
    private EmisionChequeraService emisionChequeraService;
    @Mock
    private ChequeraService chequeraService;

    @InjectMocks
    private CompletarChequerasIncompletasService service;

    @Test
    void seleccionarExcluyeEnviadasYBajasYOrdenaElPadron() {
        ChequeraIncompleta primera = incompleta(1L, (byte) 0, "Pérez", "Ana");
        ChequeraIncompleta enviada = incompleta(2L, (byte) 1, "Gómez", "Luis");
        ChequeraIncompleta deBaja = incompleta(3L, (byte) 0, "Ruiz", "Eva");
        when(chequeraIncompletaService.findAllByLectivoIdAndFacultadIdAndGeograficaIdAndClaseChequeraId(38, 5, 1, 2))
                .thenReturn(List.of(primera, enviada, deBaja));
        when(bajaService.findAllByChequeraIdIn(any())).thenReturn(List.of(
                BajaEntity.builder().chequeraId(3L).build()));

        List<IncompletasJobTarea> tareas = service.seleccionar(filtro(true, null));

        assertEquals(1, tareas.size());
        assertEquals(1L, tareas.get(0).getChequeraId());
        assertEquals("Pérez, Ana", tareas.get(0).getPersonaNombre());
    }

    @Test
    void seleccionarRespetaLaListaExplicitaDeChequeras() {
        when(chequeraIncompletaService.findAllByLectivoIdAndFacultadIdAndGeograficaIdAndClaseChequeraId(38, 5, 1, 2))
                .thenReturn(List.of(incompleta(1L, (byte) 0, "Pérez", "Ana"),
                        incompleta(2L, (byte) 0, "Gómez", "Luis")));
        when(bajaService.findAllByChequeraIdIn(any())).thenReturn(List.of());

        List<IncompletasJobTarea> tareas = service.seleccionar(filtro(false, List.of(2L)));

        assertEquals(1, tareas.size());
        assertEquals(2L, tareas.get(0).getChequeraId());
    }

    @Test
    void omiteSeriesTotalmentePagadas() {
        when(chequeraSerieService.findByChequeraId(100L)).thenReturn(serie());
        when(bajaService.findDatosByUnique(5, 2, 10L)).thenReturn(Optional.empty());
        when(chequeraCuotaService.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(5, 2, 10L, 1))
                .thenReturn(List.of(ChequeraCuota.builder().pagado((byte) 1).build()));

        ResultadoIncompleta resultado = service.prepararBorrador(tarea(100L), "tester");

        assertEquals("PAGADA", resultado.getCodigo());
        verify(borradorEmisionService, never()).crearBorrador(any(), any());
    }

    @Test
    void omiteSeriesSinDisenoDeCuotas() {
        when(chequeraSerieService.findByChequeraId(100L)).thenReturn(serie());
        when(bajaService.findDatosByUnique(5, 2, 10L)).thenReturn(Optional.empty());
        when(chequeraCuotaService.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(5, 2, 10L, 1))
                .thenReturn(List.of());
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(5, 38, 2)).thenReturn(List.of());

        assertEquals("SIN_DISENO", service.prepararBorrador(tarea(100L), "tester").getCodigo());
    }

    @Test
    void creaElBorradorConLosDatosDeLaSerie() {
        ChequeraSerie serie = serie();
        when(chequeraSerieService.findByChequeraId(100L)).thenReturn(serie);
        when(bajaService.findDatosByUnique(5, 2, 10L)).thenReturn(Optional.empty());
        when(chequeraCuotaService.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(5, 2, 10L, 1))
                .thenReturn(List.of());
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(5, 38, 2))
                .thenReturn(List.of(LectivoCuota.builder().productoId(3).cuotaId(1).build()));
        when(borradorRepository.findAbiertoBySerie(5, 2, 10L)).thenReturn(Optional.empty());
        when(borradorEmisionService.crearBorrador(any(), eq("operador")))
                .thenReturn(BorradorChequera.builder().borradorId(77L).build());

        ResultadoIncompleta resultado = service.prepararBorrador(tarea(100L), "operador");

        assertEquals(IncompletasTareaEstado.BORRADOR_CREADO, resultado.getEstado());
        assertEquals(77L, resultado.getBorradorId());
        ArgumentCaptor<CrearBorradorData> captor = ArgumentCaptor.forClass(CrearBorradorData.class);
        verify(borradorEmisionService).crearBorrador(captor.capture(), eq("operador"));
        CrearBorradorData datos = captor.getValue();
        assertEquals(new BigDecimal("0.25"), datos.getBecaPorcentaje());
        assertEquals("RES-2026-007", datos.getBecaResolucion());
        assertEquals((byte) 5, datos.getHpum());
        assertEquals(3, datos.getArancelTipoId());
        assertEquals(7, datos.getCursoId());
        assertEquals(FuenteCompletado.PLANTILLA, datos.getFuente());
        assertEquals(false, datos.getCalculaBeca());
    }

    @Test
    void reutilizaElBorradorAbiertoYLoCompleta() {
        when(chequeraSerieService.findByChequeraId(100L)).thenReturn(serie());
        when(bajaService.findDatosByUnique(5, 2, 10L)).thenReturn(Optional.empty());
        when(chequeraCuotaService.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(5, 2, 10L, 1))
                .thenReturn(List.of());
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(5, 38, 2))
                .thenReturn(List.of(LectivoCuota.builder().productoId(3).cuotaId(1).build()));
        BorradorChequera abierto = BorradorChequera.builder()
                .borradorId(55L)
                .estado(EstadoBorrador.ABIERTO)
                .becaPorcentaje(new BigDecimal("0.25"))
                .becaResolucion("RES-2026-007")
                .hpum((byte) 5)
                .version(3)
                .build();
        when(borradorRepository.findAbiertoBySerie(5, 2, 10L)).thenReturn(Optional.of(abierto));
        when(borradorEmisionService.actualizarBorrador(eq(55L), any())).thenReturn(abierto);
        when(borradorEmisionService.completarBorrador(55L, FuenteCompletado.PLANTILLA)).thenReturn(abierto);

        ResultadoIncompleta resultado = service.prepararBorrador(tarea(100L), "operador");

        assertEquals(IncompletasTareaEstado.BORRADOR_REUTILIZADO, resultado.getEstado());
        assertEquals(55L, resultado.getBorradorId());
        verify(borradorEmisionService).actualizarBorrador(eq(55L), any());
        verify(borradorEmisionService, never()).crearBorrador(any(), any());
    }

    @Test
    void emiteElBorradorAbiertoYExtiendeElDebito() {
        BorradorChequera borrador = BorradorChequera.builder()
                .borradorId(55L)
                .estado(EstadoBorrador.ABIERTO)
                .version(2)
                .build();
        when(borradorEmisionService.cargar(55L)).thenReturn(borrador);
        when(emisionChequeraService.emitir(eq(55L), any(), eq(null)))
                .thenReturn(um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoEmision.builder()
                        .chequeraSerieId(10L)
                        .build());

        ResultadoIncompleta resultado = service.emitir(tareaConBorrador(100L, 55L), null);

        assertEquals(IncompletasTareaEstado.EMITIDA, resultado.getEstado());
        verify(chequeraService).extendDebito(5, 2, 10L);
    }

    @Test
    void noEmiteUnBorradorQueYaNoEstaAbierto() {
        when(borradorEmisionService.cargar(55L)).thenReturn(BorradorChequera.builder()
                .borradorId(55L)
                .estado(EstadoBorrador.EMITIDO)
                .build());

        ResultadoIncompleta resultado = service.emitir(tareaConBorrador(100L, 55L), null);

        assertEquals("BORRADOR_NO_ABIERTO", resultado.getCodigo());
        verify(emisionChequeraService, never()).emitir(any(), any(), any());
    }

    private static FiltroIncompletas filtro(boolean soloNoEnviadas, List<Long> chequeraIds) {
        return FiltroIncompletas.builder()
                .lectivoId(38).facultadId(5).geograficaId(1).claseChequeraId(2)
                .soloNoEnviadas(soloNoEnviadas)
                .chequeraIds(chequeraIds)
                .build();
    }

    private static ChequeraIncompleta incompleta(Long chequeraId, byte enviado, String apellido, String nombre) {
        ChequeraIncompleta serie = new ChequeraIncompleta();
        serie.setChequeraId(chequeraId);
        serie.setPersonaId(new BigDecimal("49024730"));
        serie.setDocumentoId(1);
        serie.setFacultadId(5);
        serie.setTipoChequeraId(2);
        serie.setChequeraSerieId(10L);
        serie.setEnviado(enviado);
        serie.setPersona(PersonaEntity.builder().apellido(apellido).nombre(nombre).build());
        return serie;
    }

    private static ChequeraSerie serie() {
        return ChequeraSerie.builder()
                .chequeraId(100L)
                .facultadId(5)
                .tipoChequeraId(2)
                .chequeraSerieId(10L)
                .personaId(new BigDecimal("49024730"))
                .documentoId(1)
                .lectivoId(38)
                .arancelTipoId(3)
                .alternativaId(1)
                .cursoId(7)
                .hpum((byte) 5)
                .becaPorcentaje(new BigDecimal("0.25"))
                .becaResolucion("RES-2026-007")
                .becaFecha(OffsetDateTime.parse("2026-03-01T00:00:00-03:00"))
                .becaUserId(9L)
                .build();
    }

    private static IncompletasJobTarea tarea(Long chequeraId) {
        return IncompletasJobTarea.builder().chequeraId(chequeraId).facultadId(5).tipoChequeraId(2)
                .chequeraSerieId(10L).build();
    }

    private static IncompletasJobTarea tareaConBorrador(Long chequeraId, Long borradorId) {
        IncompletasJobTarea tarea = tarea(chequeraId);
        tarea.setBorradorId(borradorId);
        return tarea;
    }
}
