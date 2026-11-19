package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.service.ArancelPorcentajeService;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.service.ArancelTipoService;
import um.tesoreria.core.hexagonal.chequera.baja.application.service.BajaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorCuota;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.SolicitudEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.NumeradorChequeraPort;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.ReglasEmision;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.setup.application.service.SetupService;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.service.UsuarioService;
import um.tesoreria.core.service.ChequeraAlternativaService;
import um.tesoreria.core.service.DebitoService;
import um.tesoreria.core.service.LectivoAlternativaService;
import um.tesoreria.core.service.LectivoTotalService;
import um.tesoreria.core.service.facade.ChequeraService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regresión del riesgo R3/R7: emitir un borrador construido desde una serie con beca no debe
 * borrar la beca ni el hpum de la serie.
 */
@ExtendWith(MockitoExtension.class)
class EmisionChequeraServiceTest {

    @Mock
    private BorradorEmisionService borradorEmisionService;
    @Mock
    private BorradorFactory borradorFactory;
    @Mock
    private BorradorChequeraRepository borradorRepository;
    @Mock
    private NumeradorChequeraPort numeradorChequeraPort;
    @Mock
    private ChequeraSerieService chequeraSerieService;
    @Mock
    private ChequeraCuotaService chequeraCuotaService;
    @Mock
    private ChequeraTotalService chequeraTotalService;
    @Mock
    private ChequeraAlternativaService chequeraAlternativaService;
    @Mock
    private ChequeraPagoService chequeraPagoService;
    @Mock
    private LectivoCuotaService lectivoCuotaService;
    @Mock
    private BajaService bajaService;
    @Mock
    private LectivoTotalService lectivoTotalService;
    @Mock
    private LectivoAlternativaService lectivoAlternativaService;
    @Mock
    private ArancelPorcentajeService arancelPorcentajeService;
    @Mock
    private ArancelTipoService arancelTipoService;
    @Mock
    private TipoChequeraService tipoChequeraService;
    @Mock
    private PersonaService personaService;
    @Mock
    private SetupService setupService;
    @Mock
    private DebitoService debitoService;
    @Mock
    private ChequeraService chequeraService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private jakarta.persistence.EntityManager entityManager;

    @InjectMocks
    private EmisionChequeraService service;

    @Test
    void emitirSobreUnaSerieExistentePreservaBecaHpumYReseteaEnviado() {
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
                .enviado((byte) 1)
                .hpum((byte) 5)
                .becaPorcentaje(new BigDecimal("0.30"))
                .becaResolucion("RES-2026-001")
                .becaFecha(OffsetDateTime.parse("2026-03-01T00:00:00-03:00"))
                .becaUserId(77L)
                .build();

        BorradorChequera borrador = BorradorChequera.builder()
                .borradorId(1L)
                .estado(EstadoBorrador.ABIERTO)
                .personaId(new BigDecimal("49024730"))
                .documentoId(1)
                .facultadId(5)
                .lectivoId(38)
                .tipoChequeraId(2)
                .arancelTipoId(3)
                .alternativaId(2)
                .cursoId(7)
                .chequeraSerieId(10L)
                .fecha(OffsetDateTime.parse("2026-03-05T00:00:00-03:00"))
                .hpum((byte) 5)
                .becaPorcentaje(new BigDecimal("0.30"))
                .becaResolucion("RES-2026-001")
                .becaFecha(OffsetDateTime.parse("2026-03-01T00:00:00-03:00"))
                .becaUserId(77L)
                .cuotas(List.of(BorradorCuota.builder()
                        .productoId(3).alternativaId(2).cuotaId(1)
                        .mes(3).anio(2026)
                        .importe1Original(new BigDecimal("3000.00"))
                        .importe1Borrador(new BigDecimal("2100.00"))
                        .build()))
                .build();

        when(borradorEmisionService.cargarAbierto(1L)).thenReturn(borrador);
        when(tipoChequeraService.findByTipoChequeraId(2))
                .thenReturn(TipoChequera.builder().tipoChequeraId(2).nombre("Cuota")
                        .geograficaId(1).build());
        when(chequeraSerieService.findAllByPersonaLectivo(any(), any(), any())).thenReturn(List.of(serie));
        when(bajaService.findDatosByUnique(5, 2, 10L)).thenReturn(java.util.Optional.empty());
        when(chequeraSerieService.update(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(5, 38, 2)).thenReturn(List.of());
        when(chequeraCuotaService.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraCuotaService.calculateCodigoBarras(any())).thenReturn("1234567890123");
        when(lectivoTotalService.findAllByTipo(any(), any(), any())).thenReturn(List.of());
        when(chequeraTotalService.findAllByChequera(5, 2, 10L)).thenReturn(List.of());
        when(chequeraAlternativaService.findAllByChequera(any(), any(), any(), any())).thenReturn(List.of());
        when(lectivoAlternativaService.findAllByTipo(any(), any(), any(), any())).thenReturn(List.of());
        when(debitoService.findAllByChequera(any(), any(), any())).thenReturn(List.of());
        when(borradorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.emitir(1L, SolicitudEmision.builder().forzarNueva(false).build(), null);

        assertEquals(new BigDecimal("0.30"), serie.getBecaPorcentaje());
        assertEquals("RES-2026-001", serie.getBecaResolucion());
        assertEquals(OffsetDateTime.parse("2026-03-01T00:00:00-03:00"), serie.getBecaFecha());
        assertEquals(77L, serie.getBecaUserId());
        assertEquals((byte) 5, serie.getHpum());
        assertEquals((byte) 0, serie.getEnviado());
        assertEquals(2, serie.getTipoImpresionId());
    }

    @Test
    void noEscribeUnIdNumericoEnUsuarioIdPorqueLaColumnaEsUnLoginConFk() {
        ChequeraSerie serie = serieConBeca();
        BorradorChequera borrador = borradorAbierto();
        borrador.setUsuarioId("5");
        prepararStubs(serie, borrador);
        when(usuarioService.findByLogin("5"))
                .thenThrow(new um.tesoreria.core.hexagonal.usuarios.usuario.application.exception.UsuarioException("5"));

        service.emitir(1L, SolicitudEmision.builder().forzarNueva(false).build(), null);

        assertEquals(null, serie.getUsuarioId());
    }

    @Test
    void usaElLoginDelOperadorCuandoLoTraeLaSolicitud() {
        ChequeraSerie serie = serieConBeca();
        BorradorChequera borrador = borradorAbierto();
        prepararStubs(serie, borrador);

        service.emitir(1L, SolicitudEmision.builder().forzarNueva(false).usuarioLogin("edgardo").build(), null);

        assertEquals("edgardo", serie.getUsuarioId());
    }

    @Test
    void noEmiteSinTipoDeArancelCuandoLaChequeraEsNueva() {
        BorradorChequera borrador = borradorAbierto();
        borrador.setArancelTipoId(0);
        borrador.setChequeraSerieId(null);
        when(borradorEmisionService.cargarAbierto(1L)).thenReturn(borrador);
        when(tipoChequeraService.findByTipoChequeraId(2))
                .thenReturn(TipoChequera.builder().tipoChequeraId(2).nombre("Cuota").geograficaId(1).build());
        when(chequeraSerieService.findAllByPersonaLectivo(any(), any(), any())).thenReturn(List.of());

        EmisionException exception = assertThrows(EmisionException.class,
                () -> service.emitir(1L, SolicitudEmision.builder().forzarNueva(false).build(), null));

        assertEquals(ReglasEmision.SIN_ARANCEL_TIPO, exception.getCodigo());
        verify(chequeraSerieService, never()).update(any(), any());
    }

    @Test
    void heredaElArancelDeLaSerieCuandoElBorradorNoLoTre() {
        ChequeraSerie serie = ChequeraSerie.builder()
                .chequeraId(1L).facultadId(5).tipoChequeraId(2).chequeraSerieId(10L)
                .personaId(new BigDecimal("49024730")).documentoId(1).lectivoId(38)
                .arancelTipoId(9).alternativaId(2).cursoId(7)
                .becaPorcentaje(new BigDecimal("0.30")).hpum((byte) 5)
                .build();
        BorradorChequera borrador = borradorAbierto();
        borrador.setArancelTipoId(0);
        prepararStubs(serie, borrador);

        service.emitir(1L, SolicitudEmision.builder().forzarNueva(false).build(), null);

        assertEquals(9, serie.getArancelTipoId());
        assertEquals(9, borrador.getArancelTipoId());
    }

    private void prepararStubs(ChequeraSerie serie, BorradorChequera borrador) {
        when(borradorEmisionService.cargarAbierto(1L)).thenReturn(borrador);
        when(tipoChequeraService.findByTipoChequeraId(2))
                .thenReturn(TipoChequera.builder().tipoChequeraId(2).nombre("Cuota").geograficaId(1).build());
        when(chequeraSerieService.findAllByPersonaLectivo(any(), any(), any())).thenReturn(List.of(serie));
        when(bajaService.findDatosByUnique(5, 2, 10L)).thenReturn(java.util.Optional.empty());
        when(chequeraSerieService.update(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(lectivoCuotaService.findAllByFacultadLectivoTipo(5, 38, 2)).thenReturn(List.of());
        when(chequeraCuotaService.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraCuotaService.calculateCodigoBarras(any())).thenReturn("1234567890123");
        when(lectivoTotalService.findAllByTipo(any(), any(), any())).thenReturn(List.of());
        when(chequeraTotalService.findAllByChequera(5, 2, 10L)).thenReturn(List.of());
        when(lectivoAlternativaService.findAllByTipo(any(), any(), any(), any())).thenReturn(List.of());
        when(chequeraAlternativaService.findAllByChequera(any(), any(), any(), any())).thenReturn(List.of());
        when(debitoService.findAllByChequera(any(), any(), any())).thenReturn(List.of());
        when(borradorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private static ChequeraSerie serieConBeca() {
        return ChequeraSerie.builder()
                .chequeraId(1L).facultadId(5).tipoChequeraId(2).chequeraSerieId(10L)
                .personaId(new BigDecimal("49024730")).documentoId(1).lectivoId(38)
                .arancelTipoId(3).alternativaId(2).cursoId(7)
                .becaPorcentaje(new BigDecimal("0.30")).hpum((byte) 5)
                .build();
    }

    private static BorradorChequera borradorAbierto() {
        return BorradorChequera.builder()
                .borradorId(1L).estado(EstadoBorrador.ABIERTO)
                .personaId(new BigDecimal("49024730")).documentoId(1)
                .facultadId(5).lectivoId(38).tipoChequeraId(2)
                .arancelTipoId(3).alternativaId(2).cursoId(7).chequeraSerieId(10L)
                .becaPorcentaje(new BigDecimal("0.30")).hpum((byte) 5)
                .cuotas(List.of(BorradorCuota.builder()
                        .productoId(3).alternativaId(2).cuotaId(1)
                        .mes(3).anio(2026)
                        .importe1Original(new BigDecimal("3000.00"))
                        .importe1Borrador(new BigDecimal("2100.00"))
                        .build()))
                .build();
    }
}
