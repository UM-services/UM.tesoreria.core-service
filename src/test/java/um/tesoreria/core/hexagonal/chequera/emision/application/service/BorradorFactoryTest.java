package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.service.ArancelPorcentajeService;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.AlternativaEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorCuota;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.DatosConstruccion;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.ReglasEmision;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BorradorFactoryTest {

    private ArancelPorcentajeService arancelPorcentajeService;
    private BorradorFactory factory;

    @BeforeEach
    void setUp() {
        arancelPorcentajeService = mock(ArancelPorcentajeService.class);
        when(arancelPorcentajeService.findByUnique(anyInt(), anyInt()))
                .thenReturn(ArancelPorcentaje.builder().porcentaje(new BigDecimal("100")).build());
        factory = new BorradorFactory(arancelPorcentajeService);
    }

    @Test
    void aplicaBecaSobreElDiseno() {
        List<BorradorCuota> cuotas = factory.construir(List.of(), datos(new BigDecimal("0.20"), false, 1));

        assertEquals(1, cuotas.size());
        assertEquals(new BigDecimal("1000.00"), cuotas.get(0).getImporte1Original());
        assertEquals(new BigDecimal("800.00"), cuotas.get(0).getImporte1Borrador());
        assertEquals(new BigDecimal("0.00"), cuotas.get(0).getDescuentoPagoTermino());
    }

    @Test
    void productoDosNoAplicaBeca() {
        List<BorradorCuota> cuotas = factory.construir(List.of(), datos(new BigDecimal("0.20"), false, 2));

        assertEquals(new BigDecimal("1000.00"), cuotas.get(0).getImporte1Borrador());
    }

    @Test
    void aplicaDescuentoPagoTerminoEnElPrimerImporte() {
        DatosConstruccion datos = datos(BigDecimal.ZERO, true, 1);
        datos.setEnTerminoAnterior(true);
        datos.setAlternativas(List.of(AlternativaEmision.builder()
                .alternativaId(1).productoId(1).descuentoPagoTermino(10).build()));

        List<BorradorCuota> cuotas = factory.construir(List.of(), datos);

        assertEquals(new BigDecimal("900.00"), cuotas.get(0).getImporte1Borrador());
        assertEquals(new BigDecimal("1100.00"), cuotas.get(0).getImporte2Borrador());
        assertEquals(10, cuotas.get(0).getDescuentoPagoTermino().intValue());
    }

    @Test
    void respetaCuotaManualSinRecalculoDeBeca() {
        ChequeraCuota persistida = persistida();
        persistida.setImporte1(new BigDecimal("700.00"));
        persistida.setManual((byte) 1);

        List<BorradorCuota> cuotas = factory.construir(List.of(persistida), datos(new BigDecimal("0.20"), false, 1));

        assertEquals(new BigDecimal("700.00"), cuotas.get(0).getImporte1Borrador());
        assertEquals((byte) 1, cuotas.get(0).getManual());
    }

    @Test
    void recalculaBecaCuandoSePideExplicitamente() {
        ChequeraCuota persistida = persistida();
        persistida.setImporte1(new BigDecimal("700.00"));
        persistida.setManual((byte) 1);

        List<BorradorCuota> cuotas = factory.construir(List.of(persistida), datos(new BigDecimal("0.20"), true, 1));

        assertEquals(new BigDecimal("800.00"), cuotas.get(0).getImporte1Borrador());
    }

    @Test
    void noPisaCuotasCompensadas() {
        ChequeraCuota persistida = persistida();
        persistida.setCompensada((byte) 1);
        persistida.setImporte1(BigDecimal.ZERO);
        persistida.setImporte2(BigDecimal.ZERO);
        persistida.setImporte3(BigDecimal.ZERO);

        List<BorradorCuota> cuotas = factory.construir(List.of(persistida), datos(new BigDecimal("0.20"), false, 1));

        assertEquals(new BigDecimal("0.00"), cuotas.get(0).getImporte1Borrador());
    }

    @Test
    void aplicaArancelDeLaSerieAntesDelCorte202108() {
        DatosConstruccion datos = datos(BigDecimal.ZERO, false, 1);
        datos.setDiseno(List.of(cuotaDiseno(8, 2021)));
        datos.setArancelTipoIdSeleccionado(7);
        datos.setArancelTipoIdChequera(3);

        List<BorradorCuota> cuotas = factory.construir(List.of(), datos);

        assertEquals(3, cuotas.get(0).getArancelTipoId());
    }

    @Test
    void aplicaArancelSeleccionadoDespuesDelCorte202108() {
        DatosConstruccion datos = datos(BigDecimal.ZERO, false, 1);
        datos.setDiseno(List.of(cuotaDiseno(9, 2021)));
        datos.setArancelTipoIdSeleccionado(7);
        datos.setArancelTipoIdChequera(3);

        List<BorradorCuota> cuotas = factory.construir(List.of(), datos);

        assertEquals(7, cuotas.get(0).getArancelTipoId());
    }

    @Test
    void noModificaUnaCuotaPagada() {
        BorradorCuota pagada = cuotaBorrador((byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 1);
        List<BorradorCuota> cuotas = new java.util.ArrayList<>(List.of(pagada));

        EmisionException error = assertThrows(EmisionException.class,
                () -> factory.aplicarEdiciones(cuotas, List.of(edicion(new BigDecimal("500.00")))));

        assertEquals(ReglasEmision.CUOTA_INMUTABLE, error.getCodigo());
        assertEquals(new BigDecimal("1000.00"), cuotas.get(0).getImporte1Borrador());
    }

    @Test
    void noModificaUnaCuotaDeBajaNiUnaCompensada() {
        BorradorCuota deBaja = cuotaBorrador((byte) 0, (byte) 1, (byte) 0, (byte) 0, (byte) 1);
        BorradorCuota compensada = cuotaBorrador((byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 1);

        assertThrows(EmisionException.class,
                () -> factory.aplicarEdiciones(new java.util.ArrayList<>(List.of(deBaja)),
                        List.of(edicion(new BigDecimal("500.00")))));
        assertThrows(EmisionException.class,
                () -> factory.aplicarEdiciones(new java.util.ArrayList<>(List.of(compensada)),
                        List.of(edicion(new BigDecimal("500.00")))));
    }

    @Test
    void aceptaElPayloadSinCambiosDeUnaCuotaInmutable() {
        BorradorCuota pagada = cuotaBorrador((byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 1);
        List<BorradorCuota> cuotas = new java.util.ArrayList<>(List.of(pagada));

        factory.aplicarEdiciones(cuotas, List.of(edicion(new BigDecimal("1000.00"))));

        assertEquals(new BigDecimal("1000.00"), cuotas.get(0).getImporte1Borrador());
    }

    @Test
    void unaCuotaEditadaEnElBorradorSigueSiendoEditable() {
        BorradorCuota editada = cuotaBorrador((byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 0);
        List<BorradorCuota> cuotas = new java.util.ArrayList<>(List.of(editada));

        factory.aplicarEdiciones(cuotas, List.of(edicion(new BigDecimal("500.00"))));

        assertEquals(new BigDecimal("500.00"), cuotas.get(0).getImporte1Borrador());
        assertEquals((byte) 1, cuotas.get(0).getManual());
        assertEquals((byte) 0, cuotas.get(0).getBloqueado());
        assertFalse(cuotas.get(0).inmutable());
    }

    @Test
    void laCuotaPagadaDeLaChequeraRealQuedaBloqueadaEnElBorrador() {
        ChequeraCuota persistida = persistida();
        persistida.setPagado((byte) 1);

        List<BorradorCuota> cuotas = factory.construir(List.of(persistida), datos(BigDecimal.ZERO, false, 1));

        assertEquals((byte) 1, cuotas.get(0).getBloqueado());
        assertTrue(cuotas.get(0).inmutable());
    }

    @Test
    void completaFaltantesDelDisenoYConservaLasPersistidas() {
        ChequeraCuota persistida = persistida();
        BorradorCuota extra = BorradorCuota.builder()
                .productoId(9).alternativaId(1).cuotaId(1)
                .importe1Original(BigDecimal.TEN).importe1Borrador(BigDecimal.TEN)
                .pagado((byte) 0).baja((byte) 0).manual((byte) 0).compensada((byte) 0)
                .build();
        List<BorradorCuota> base = List.of(extra);
        DatosConstruccion datos = datos(BigDecimal.ZERO, false, 1);

        List<BorradorCuota> resultado = factory.construirDesdeBorrador(new java.util.ArrayList<>(base), datos);

        assertTrue(resultado.stream().anyMatch(cuota -> cuota.key().equals("9.1.1")));
        assertTrue(resultado.stream().anyMatch(cuota -> cuota.key().equals("1.1.1")));
    }

    private BorradorCuota cuotaBorrador(byte pagado, byte baja, byte manual, byte compensada, byte bloqueado) {
        return BorradorCuota.builder()
                .productoId(1).alternativaId(1).cuotaId(1).mes(3).anio(2026)
                .vencimiento1(OffsetDateTime.parse("2026-03-01T00:00:00Z"))
                .vencimiento2(OffsetDateTime.parse("2026-03-11T00:00:00Z"))
                .vencimiento3(OffsetDateTime.parse("2026-03-26T00:00:00Z"))
                .importe1Borrador(new BigDecimal("1000.00"))
                .importe2Borrador(new BigDecimal("1100.00"))
                .importe3Borrador(new BigDecimal("1200.00"))
                .pagado(pagado).baja(baja).manual(manual).compensada(compensada)
                .modificado((byte) 0).bloqueado(bloqueado)
                .build();
    }

    private BorradorCuota edicion(BigDecimal importe1) {
        return BorradorCuota.builder()
                .productoId(1).alternativaId(1).cuotaId(1).mes(3).anio(2026)
                .vencimiento1(OffsetDateTime.parse("2026-03-01T00:00:00Z"))
                .vencimiento2(OffsetDateTime.parse("2026-03-11T00:00:00Z"))
                .vencimiento3(OffsetDateTime.parse("2026-03-26T00:00:00Z"))
                .importe1Borrador(importe1)
                .importe2Borrador(new BigDecimal("1100.00"))
                .importe3Borrador(new BigDecimal("1200.00"))
                .build();
    }

    private DatosConstruccion datos(BigDecimal beca, boolean calculaBeca, int productoId) {
        return DatosConstruccion.builder()
                .facultadId(1)
                .lectivoId(2026)
                .tipoChequeraId(1)
                .arancelTipoIdSeleccionado(1)
                .arancelTipoIdChequera(1)
                .becaPorcentaje(beca)
                .calculaBeca(calculaBeca)
                .personaId(BigDecimal.ONE)
                .documentoId(1)
                .enTerminoAnterior(false)
                .diseno(List.of(cuotaDiseno(3, 2026, productoId)))
                .alternativas(List.of())
                .build();
    }

    private LectivoCuota cuotaDiseno(int mes, int anio) {
        return cuotaDiseno(mes, anio, 1);
    }

    private LectivoCuota cuotaDiseno(int mes, int anio, int productoId) {
        return LectivoCuota.builder()
                .facultadId(1).lectivoId(2026).tipoChequeraId(1)
                .productoId(productoId).alternativaId(1).cuotaId(1)
                .mes(mes).anho(anio)
                .vencimiento1(OffsetDateTime.parse("2026-03-01T00:00:00Z"))
                .vencimiento2(OffsetDateTime.parse("2026-03-11T00:00:00Z"))
                .vencimiento3(OffsetDateTime.parse("2026-03-26T00:00:00Z"))
                .importe1(new BigDecimal("1000")).importe2(new BigDecimal("1100")).importe3(new BigDecimal("1200"))
                .build();
    }

    private ChequeraCuota persistida() {
        return ChequeraCuota.builder()
                .chequeraCuotaId(1L).facultadId(1).tipoChequeraId(1).chequeraSerieId(1L)
                .productoId(1).alternativaId(1).cuotaId(1).mes(3).anho(2026)
                .vencimiento1(OffsetDateTime.parse("2026-03-01T00:00:00Z"))
                .vencimiento2(OffsetDateTime.parse("2026-03-11T00:00:00Z"))
                .vencimiento3(OffsetDateTime.parse("2026-03-26T00:00:00Z"))
                .importe1(new BigDecimal("1000")).importe2(new BigDecimal("1000")).importe3(new BigDecimal("1000"))
                .importe1Original(new BigDecimal("1000")).importe2Original(new BigDecimal("1000"))
                .importe3Original(new BigDecimal("1000"))
                .pagado((byte) 0).baja((byte) 0).manual((byte) 0).compensada((byte) 0)
                .build();
    }
}
