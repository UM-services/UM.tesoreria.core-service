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
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    void alineaElVencimientoDelBorradorConElDisenoAunqueLaCuotaTengaImporte() {
        ChequeraCuota persistida = ChequeraCuota.builder()
                .chequeraCuotaId(1L).facultadId(1).tipoChequeraId(1).chequeraSerieId(1L)
                .productoId(1).alternativaId(1).cuotaId(1).mes(12).anho(2026)
                .vencimiento1(OffsetDateTime.parse("2026-12-09T00:00:00Z"))
                .vencimiento2(OffsetDateTime.parse("2026-12-31T00:00:00Z"))
                .vencimiento3(OffsetDateTime.parse("2027-01-31T00:00:00Z"))
                .importe1(new BigDecimal("313000")).importe2(new BigDecimal("313000"))
                .importe3(new BigDecimal("313000"))
                .importe1Original(new BigDecimal("313000")).importe2Original(new BigDecimal("313000"))
                .importe3Original(new BigDecimal("313000"))
                .pagado((byte) 0).baja((byte) 0).manual((byte) 0).compensada((byte) 0)
                .build();

        List<BorradorCuota> cuotas = factory.construir(List.of(persistida), datosDe(disenoDiciembre()));

        assertEquals(OffsetDateTime.parse("2026-12-10T00:00:00Z"), cuotas.get(0).getVencimiento1());
        assertEquals(OffsetDateTime.parse("2026-12-31T00:00:00Z"), cuotas.get(0).getVencimiento2());
        assertEquals(OffsetDateTime.parse("2027-01-31T00:00:00Z"), cuotas.get(0).getVencimiento3());
    }

    @Test
    void noAlineaElVencimientoDeUnaCuotaPagadaBajaOCompensada() {
        ChequeraCuota pagada = ChequeraCuota.builder()
                .chequeraCuotaId(1L).facultadId(1).tipoChequeraId(1).chequeraSerieId(1L)
                .productoId(1).alternativaId(1).cuotaId(1).mes(12).anho(2026)
                .vencimiento1(OffsetDateTime.parse("2026-12-09T00:00:00Z"))
                .importe1(new BigDecimal("313000")).importe1Original(new BigDecimal("313000"))
                .pagado((byte) 1).baja((byte) 0).manual((byte) 0).compensada((byte) 0)
                .build();

        List<BorradorCuota> cuotas = factory.construir(List.of(pagada), datosDe(disenoDiciembre()));

        assertEquals(OffsetDateTime.parse("2026-12-09T00:00:00Z"), cuotas.get(0).getVencimiento1());
    }

    @Test
    void noAlineaElVencimientoDeUnaCuotaEditadaAManoEnElBorrador() {
        BorradorCuota editada = cuotaBorrador((byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 0);
        editada.setVencimiento1(OffsetDateTime.parse("2026-03-02T00:00:00Z"));

        List<BorradorCuota> cuotas = factory.construirDesdeBorrador(new java.util.ArrayList<>(List.of(editada)),
                datosDe(disenoDiciembre()));

        assertEquals(OffsetDateTime.parse("2026-03-02T00:00:00Z"), cuotas.get(0).getVencimiento1());
        assertEquals(3, cuotas.get(0).getMes());
        assertEquals(2026, cuotas.get(0).getAnio());
        assertEquals(new BigDecimal("1000.00"), cuotas.get(0).getImporte1Borrador());
        assertNull(cuotas.get(0).getImporte1Original());
    }

    @Test
    void completaImportesCeroYAlineaElVencimientoEnLaMismaPasada() {
        ChequeraCuota sinImporte = ChequeraCuota.builder()
                .chequeraCuotaId(1L).facultadId(1).tipoChequeraId(1).chequeraSerieId(1L)
                .productoId(1).alternativaId(1).cuotaId(1).mes(12).anho(2026)
                .vencimiento1(OffsetDateTime.parse("2026-12-09T00:00:00Z"))
                .importe1(BigDecimal.ZERO).importe2(BigDecimal.ZERO).importe3(BigDecimal.ZERO)
                .importe1Original(BigDecimal.ZERO).importe2Original(BigDecimal.ZERO)
                .importe3Original(BigDecimal.ZERO)
                .pagado((byte) 0).baja((byte) 0).manual((byte) 0).compensada((byte) 0)
                .build();

        List<BorradorCuota> cuotas = factory.construir(List.of(sinImporte), datosDe(disenoDiciembre()));

        assertEquals(0, cuotas.get(0).getImporte1Original().compareTo(new BigDecimal("313000")));
        assertEquals(OffsetDateTime.parse("2026-12-10T00:00:00Z"), cuotas.get(0).getVencimiento1());
    }

    @Test
    void noModificaNiFechasNiImportesDeCuotasPagadasDeBajaOCompensadas() {
        byte[][] inmutables = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
        for (byte[] flags : inmutables) {
            ChequeraCuota cuota = ChequeraCuota.builder()
                    .chequeraCuotaId(1L).facultadId(1).tipoChequeraId(1).chequeraSerieId(1L)
                    .productoId(1).alternativaId(1).cuotaId(1).mes(12).anho(2026)
                    .vencimiento1(OffsetDateTime.parse("2026-12-09T00:00:00Z"))
                    .vencimiento2(OffsetDateTime.parse("2026-12-31T00:00:00Z"))
                    .vencimiento3(OffsetDateTime.parse("2027-01-31T00:00:00Z"))
                    .importe1(BigDecimal.ZERO).importe2(BigDecimal.ZERO).importe3(BigDecimal.ZERO)
                    .importe1Original(BigDecimal.ZERO).importe2Original(BigDecimal.ZERO)
                    .importe3Original(BigDecimal.ZERO)
                    .pagado(flags[0]).baja(flags[1]).manual((byte) 0).compensada(flags[2])
                    .build();

            BorradorCuota resultado = factory.construir(List.of(cuota), datosDe(disenoDiciembre())).get(0);

            assertEquals(0, resultado.getImporte1Original().signum(),
                    "no debe completar importes de una cuota inmutable");
            assertEquals(0, resultado.getImporte1Borrador().signum(),
                    "no debe completar importes de una cuota inmutable");
            assertEquals(OffsetDateTime.parse("2026-12-09T00:00:00Z"), resultado.getVencimiento1(),
                    "no debe cambiar la fecha de una cuota inmutable");
        }
    }

    @Test
    void noAplicaBecaArancelNiDescuentoAUnaCuotaDeBajaBloqueadaOPagada() {
        ChequeraCuota deBaja = ChequeraCuota.builder()
                .chequeraCuotaId(1L).facultadId(1).tipoChequeraId(1).chequeraSerieId(1L)
                .productoId(1).alternativaId(1).cuotaId(1).mes(12).anho(2026)
                .vencimiento1(OffsetDateTime.parse("2026-12-09T00:00:00Z"))
                .importe1(new BigDecimal("1000")).importe2(new BigDecimal("1000"))
                .importe3(new BigDecimal("1000"))
                .importe1Original(new BigDecimal("1000")).importe2Original(new BigDecimal("1000"))
                .importe3Original(new BigDecimal("1000"))
                .arancelTipoId(9)
                .pagado((byte) 0).baja((byte) 1).manual((byte) 0).compensada((byte) 0)
                .build();
        DatosConstruccion datos = DatosConstruccion.builder()
                .facultadId(1).lectivoId(37).tipoChequeraId(1)
                .arancelTipoIdSeleccionado(1).arancelTipoIdChequera(1)
                .becaPorcentaje(new BigDecimal("50")).calculaBeca(false)
                .personaId(BigDecimal.ONE).documentoId(1).enTerminoAnterior(true)
                .diseno(List.of(disenoDiciembre()))
                .alternativas(List.of())
                .build();

        BorradorCuota resultado = factory.construir(List.of(deBaja), datos).get(0);

        assertEquals(0, resultado.getImporte1Borrador().compareTo(new BigDecimal("1000")),
                "la beca y el arancel no deben tocar el importe de una cuota de baja");
        assertEquals(Integer.valueOf(9), resultado.getArancelTipoId(),
                "el arancel de una cuota de baja no debe cambiar");
        assertEquals(OffsetDateTime.parse("2026-12-09T00:00:00Z"), resultado.getVencimiento1());
    }

    private DatosConstruccion datosDe(LectivoCuota diseno) {
        return DatosConstruccion.builder()
                .facultadId(1)
                .lectivoId(37)
                .tipoChequeraId(1)
                .arancelTipoIdSeleccionado(1)
                .arancelTipoIdChequera(1)
                .becaPorcentaje(BigDecimal.ZERO)
                .calculaBeca(false)
                .personaId(BigDecimal.ONE)
                .documentoId(1)
                .enTerminoAnterior(false)
                .diseno(List.of(diseno))
                .alternativas(List.of())
                .build();
    }

    private LectivoCuota disenoDiciembre() {
        return LectivoCuota.builder()
                .facultadId(1).lectivoId(37).tipoChequeraId(1)
                .productoId(1).alternativaId(1).cuotaId(1)
                .mes(12).anho(2026)
                .vencimiento1(OffsetDateTime.parse("2026-12-10T00:00:00Z"))
                .vencimiento2(OffsetDateTime.parse("2026-12-31T00:00:00Z"))
                .vencimiento3(OffsetDateTime.parse("2027-01-31T00:00:00Z"))
                .importe1(new BigDecimal("313000")).importe2(new BigDecimal("313000"))
                .importe3(new BigDecimal("313000"))
                .build();
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
