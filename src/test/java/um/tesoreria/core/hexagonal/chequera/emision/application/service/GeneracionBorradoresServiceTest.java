package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.service.ArancelTipoService;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;
import um.tesoreria.core.hexagonal.chequera.baja.application.service.BajaService;
import um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.entity.BajaEntity;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.claseChequera.application.service.ClaseChequeraService;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FiltroGeneracion;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FuenteCompletado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionTareaEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoGeneracionFila;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.SolicitudEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAlta;
import um.tesoreria.core.service.view.ChequeraSerieAltaService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeneracionBorradoresServiceTest {

    @Mock
    private ChequeraSerieAltaService chequeraSerieAltaService;
    @Mock
    private ChequeraSerieService chequeraSerieService;
    @Mock
    private ChequeraCuotaService chequeraCuotaService;
    @Mock
    private BajaService bajaService;
    @Mock
    private LectivoCuotaService lectivoCuotaService;
    @Mock
    private TipoChequeraService tipoChequeraService;
    @Mock
    private ClaseChequeraService claseChequeraService;
    @Mock
    private ArancelTipoService arancelTipoService;
    @Mock
    private BorradorEmisionService borradorEmisionService;
    @Mock
    private BorradorChequeraRepository borradorRepository;
    @Mock
    private EmisionChequeraService emisionChequeraService;

    @InjectMocks
    private GeneracionBorradoresService service;

    @Test
    void seleccionarFiltraEnviadasClaseYSeleccionExplicita() {
        when(chequeraSerieAltaService.findAllByLectivoIdAndFacultadIdAndGeograficaId(38, 5, 1))
                .thenReturn(List.of(alta(1L, 2, (byte) 0), alta(2L, 2, (byte) 1), alta(3L, 3, (byte) 0)));
        when(tipoChequeraService.findAll()).thenReturn(List.of(
                TipoChequera.builder().tipoChequeraId(2).claseChequeraId(1).nombre("Grado").build(),
                TipoChequera.builder().tipoChequeraId(3).claseChequeraId(2).nombre("Posgrado").build()));
        when(bajaService.findAllByChequeraIdIn(any())).thenReturn(List.of());

        List<GeneracionJobTarea> tareas = service.seleccionar(FiltroGeneracion.builder()
                .lectivoId(38).facultadId(5).geograficaId(1).claseChequeraId(1).soloNoEnviadas(true).build());
        assertEquals(1, tareas.size());
        assertEquals(1L, tareas.get(0).getChequeraId());

        List<GeneracionJobTarea> seleccion = service.seleccionar(FiltroGeneracion.builder()
                .lectivoId(38).facultadId(5).geograficaId(1).soloNoEnviadas(false)
                .chequeraSerieIds(List.of(3L)).build());
        assertEquals(1, seleccion.size());
        assertEquals(3L, seleccion.get(0).getChequeraId());
    }

    @Test
    void omiteLasSeriesDadasDeBaja() {
        when(chequeraSerieAltaService.findAllByLectivoIdAndFacultadIdAndGeograficaId(38, 5, 1))
                .thenReturn(List.of(alta(1L, 2, (byte) 0), alta(2L, 2, (byte) 0)));
        when(tipoChequeraService.findAll()).thenReturn(List.of(
                TipoChequera.builder().tipoChequeraId(2).claseChequeraId(1).build()));
        when(bajaService.findAllByChequeraIdIn(any())).thenReturn(List.of(
                BajaEntity.builder().chequeraId(2L).build()));

        List<GeneracionJobTarea> tareas = service.seleccionar(filtro());

        assertEquals(1, tareas.size());
        assertEquals(1L, tareas.get(0).getChequeraId());
    }

    @Test
    void prepararBorradorCreaConLosDatosDeLaSerie() {
        when(chequeraSerieService.findByChequeraId(100L)).thenReturn(serie());
        when(bajaService.findDatosByUnique(5, 2, 10L)).thenReturn(Optional.empty());
        when(chequeraCuotaService
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(5, 2, 10L, 1))
                .thenReturn(List.of(ChequeraCuota.builder().pagado((byte) 0).build()));
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(5, 38, 2))
                .thenReturn(List.of(LectivoCuota.builder().build()));
        when(borradorRepository.findAbiertoBySerie(5, 2, 10L)).thenReturn(Optional.empty());
        when(borradorEmisionService.crearBorrador(any(), eq("tester")))
                .thenReturn(BorradorChequera.builder().borradorId(55L).build());

        ResultadoGeneracionFila resultado = service.prepararBorrador(tarea(100L), "tester");

        assertEquals(GeneracionTareaEstado.BORRADOR_CREADO, resultado.getEstado());
        assertEquals(55L, resultado.getBorradorId());
    }

    @Test
    void prepararBorradorReutilizaElAbierto() {
        when(chequeraSerieService.findByChequeraId(100L)).thenReturn(serie());
        when(bajaService.findDatosByUnique(5, 2, 10L)).thenReturn(Optional.empty());
        when(chequeraCuotaService
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(5, 2, 10L, 1))
                .thenReturn(List.of(ChequeraCuota.builder().pagado((byte) 0).build()));
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(5, 38, 2))
                .thenReturn(List.of(LectivoCuota.builder().build()));
        when(borradorRepository.findAbiertoBySerie(5, 2, 10L))
                .thenReturn(Optional.of(BorradorChequera.builder().borradorId(77L).build()));
        when(borradorEmisionService.completarBorrador(77L, FuenteCompletado.DISENO))
                .thenReturn(BorradorChequera.builder().borradorId(77L).build());

        ResultadoGeneracionFila resultado = service.prepararBorrador(tarea(100L), "tester");

        assertEquals(GeneracionTareaEstado.BORRADOR_REUTILIZADO, resultado.getEstado());
        assertEquals(77L, resultado.getBorradorId());
        verify(borradorEmisionService).completarBorrador(77L, FuenteCompletado.DISENO);
        verify(borradorEmisionService, never()).crearBorrador(any(), any());
    }

    @Test
    void omiteSeriesTotalmentePagadas() {
        when(chequeraSerieService.findByChequeraId(100L)).thenReturn(serie());
        when(bajaService.findDatosByUnique(5, 2, 10L)).thenReturn(Optional.empty());
        when(chequeraCuotaService
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(5, 2, 10L, 1))
                .thenReturn(List.of(ChequeraCuota.builder().pagado((byte) 1).build()));

        ResultadoGeneracionFila resultado = service.prepararBorrador(tarea(100L), "tester");

        assertEquals(GeneracionTareaEstado.OMITIDA, resultado.getEstado());
        assertEquals("PAGADA", resultado.getCodigo());
        verify(borradorEmisionService, never()).crearBorrador(any(), any());
    }

    @Test
    void omiteSeriesSinDisenoDeCuotas() {
        when(chequeraSerieService.findByChequeraId(100L)).thenReturn(serie());
        when(bajaService.findDatosByUnique(5, 2, 10L)).thenReturn(Optional.empty());
        when(chequeraCuotaService
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(5, 2, 10L, 1))
                .thenReturn(List.of());
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(5, 38, 2)).thenReturn(List.of());

        ResultadoGeneracionFila resultado = service.prepararBorrador(tarea(100L), "tester");

        assertEquals("SIN_DISENO", resultado.getCodigo());
        verify(borradorEmisionService, never()).crearBorrador(any(), any());
    }

    @Test
    void emitirEmiteElBorradorAbiertoConLaVersionActual() {
        when(borradorEmisionService.cargar(55L)).thenReturn(BorradorChequera.builder()
                .borradorId(55L).estado(EstadoBorrador.ABIERTO).version(3).build());
        when(emisionChequeraService.emitir(eq(55L), any(SolicitudEmision.class), isNull()))
                .thenReturn(ResultadoEmision.builder().chequeraSerieId(10L).build());

        ResultadoGeneracionFila resultado = service.emitir(tareaConBorrador(55L), job());

        assertEquals(GeneracionTareaEstado.EMITIDA, resultado.getEstado());
        assertEquals("Chequera 10 emitida", resultado.getMensaje());
    }

    @Test
    void emitirOmiteUnBorradorQueYaNoEstaAbierto() {
        when(borradorEmisionService.cargar(55L)).thenReturn(BorradorChequera.builder()
                .borradorId(55L).estado(EstadoBorrador.EMITIDO).build());

        ResultadoGeneracionFila resultado = service.emitir(tareaConBorrador(55L), job());

        assertEquals(GeneracionTareaEstado.OMITIDA, resultado.getEstado());
        assertEquals("BORRADOR_NO_ABIERTO", resultado.getCodigo());
    }

    @Test
    void emitirPropagaElErrorDeNegocio() {
        when(borradorEmisionService.cargar(55L)).thenReturn(BorradorChequera.builder()
                .borradorId(55L).estado(EstadoBorrador.ABIERTO).version(1).build());
        when(emisionChequeraService.emitir(eq(55L), any(SolicitudEmision.class), isNull()))
                .thenThrow(new EmisionException("DEUDA_EXCEDIDA", "La persona tiene deuda"));

        ResultadoGeneracionFila resultado = service.emitir(tareaConBorrador(55L), job());

        assertEquals(GeneracionTareaEstado.ERROR, resultado.getEstado());
        assertEquals("DEUDA_EXCEDIDA", resultado.getCodigo());
    }

    @Test
    void previewIndicaElBorradorAbiertoYLosNombres() {
        ChequeraSerieAlta alta = alta(1L, 2, (byte) 0);
        when(chequeraSerieAltaService.findAllByLectivoIdAndFacultadIdAndGeograficaId(38, 5, 1))
                .thenReturn(List.of(alta));
        when(tipoChequeraService.findAll()).thenReturn(List.of(
                TipoChequera.builder().tipoChequeraId(2).claseChequeraId(1).nombre("Grado").build()));
        when(claseChequeraService.findAll()).thenReturn(List.of(
                ClaseChequera.builder().claseChequeraId(1).nombre("Grado").build()));
        when(arancelTipoService.findAll()).thenReturn(List.of(
                ArancelTipoEntity.builder().arancelTipoId(1).descripcion("Base").build()));
        when(bajaService.findAllByChequeraIdIn(any())).thenReturn(List.of());
        when(borradorRepository.findAllByFacultadIdAndChequeraSerieIdInAndEstado(eq(5), any(),
                eq(EstadoBorrador.ABIERTO)))
                .thenReturn(List.of(BorradorChequera.builder().borradorId(88L).tipoChequeraId(2)
                        .chequeraSerieId(10L).build()));

        var preview = service.preview(filtro());

        assertEquals(1, preview.size());
        assertEquals("Grado", preview.get(0).getTipoChequeraNombre());
        assertEquals("Base", preview.get(0).getArancelTipoNombre());
        assertEquals(88L, preview.get(0).getBorradorAbiertoId());
    }

    private static FiltroGeneracion filtro() {
        return FiltroGeneracion.builder().lectivoId(38).facultadId(5).geograficaId(1).soloNoEnviadas(true).build();
    }

    private static GeneracionJobTarea tarea(Long chequeraId) {
        return GeneracionJobTarea.builder().chequeraId(chequeraId).build();
    }

    private static GeneracionJobTarea tareaConBorrador(Long borradorId) {
        return GeneracionJobTarea.builder().chequeraId(100L).borradorId(borradorId).build();
    }

    private static GeneracionJob job() {
        return GeneracionJob.builder().jobId(1L).usuarioLogin("tester").build();
    }

    private static ChequeraSerie serie() {
        return ChequeraSerie.builder()
                .chequeraId(100L)
                .facultadId(5)
                .tipoChequeraId(2)
                .chequeraSerieId(10L)
                .personaId(new BigDecimal("1234567"))
                .documentoId(1)
                .lectivoId(38)
                .arancelTipoId(1)
                .cursoId(2)
                .alternativaId(1)
                .build();
    }

    private static ChequeraSerieAlta alta(Long chequeraId, Integer tipoChequeraId, Byte enviado) {
        return new ChequeraSerieAlta(chequeraId, 5, tipoChequeraId, 10L, new BigDecimal("1234567"), 1, 38,
                1, 2, 0, 1, null, 0, null, 1, (byte) 0, 2, (byte) 0, null, enviado,
                null, null, null, null, null, null, null);
    }
}
