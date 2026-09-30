package um.tesoreria.core.hexagonal.chequera.estadoChequera.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.tesoreria.core.exception.FacultadException;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.exception.ArancelTipoException;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.service.ArancelTipoService;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.EstadoChequera;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.CuotaEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.DebitoEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.ProductoEstado;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.lectivo.application.exception.LectivoException;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.personas.persona.application.exception.PersonaException;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.kotlin.model.ChequeraAlternativa;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.model.TipoImpresion;
import um.tesoreria.core.model.dto.ChequeraCuotaPagosDto;
import um.tesoreria.core.model.dto.ChequeraPagoDto;
import um.tesoreria.core.service.ChequeraAlternativaService;
import um.tesoreria.core.service.DebitoService;
import um.tesoreria.core.service.TipoImpresionService;
import um.tesoreria.core.service.facade.ChequeraService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetEstadoChequeraUseCaseImplTest {

    private static final Integer FACULTAD_ID = 1;
    private static final Integer TIPO_CHEQUERA_ID = 2;
    private static final Long CHEQUERA_SERIE_ID = 12345L;
    private static final Integer ALTERNATIVA_ID = 1;
    private static final Integer DEBITO_TIPO_ID = 2;
    private static final Integer MATRICULA_ID = 1;
    private static final Integer ARANCEL_ID = 2;
    private static final ZoneOffset MENDOZA = ZoneOffset.ofHours(-3);

    private final ChequeraSerieService chequeraSerieService = mock(ChequeraSerieService.class);
    private final FacultadService facultadService = mock(FacultadService.class);
    private final TipoChequeraService tipoChequeraService = mock(TipoChequeraService.class);
    private final PersonaService personaService = mock(PersonaService.class);
    private final LectivoService lectivoService = mock(LectivoService.class);
    private final ArancelTipoService arancelTipoService = mock(ArancelTipoService.class);
    private final TipoImpresionService tipoImpresionService = mock(TipoImpresionService.class);
    private final ChequeraService chequeraService = mock(ChequeraService.class);
    private final ChequeraTotalService chequeraTotalService = mock(ChequeraTotalService.class);
    private final ChequeraAlternativaService chequeraAlternativaService = mock(ChequeraAlternativaService.class);
    private final DebitoService debitoService = mock(DebitoService.class);

    private final GetEstadoChequeraUseCaseImpl useCase = new GetEstadoChequeraUseCaseImpl(chequeraSerieService,
            facultadService, tipoChequeraService, personaService, lectivoService, arancelTipoService,
            tipoImpresionService, chequeraService, chequeraTotalService, chequeraAlternativaService, debitoService);

    private ChequeraSerie.ChequeraSerieBuilder serieBuilder() {
        return ChequeraSerie.builder()
                .facultadId(FACULTAD_ID)
                .tipoChequeraId(TIPO_CHEQUERA_ID)
                .chequeraSerieId(CHEQUERA_SERIE_ID)
                .personaId(BigDecimal.valueOf(12345678))
                .documentoId(1)
                .lectivoId(37)
                .arancelTipoId(5)
                .tipoImpresionId(1)
                .becaPorcentaje(new BigDecimal("0.15"));
    }

    @BeforeEach
    void datosDeLaChequera() {
        ChequeraSerie serie = serieBuilder().hpum((byte) 1).build();
        when(chequeraSerieService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID)).thenReturn(serie);
        when(facultadService.findByFacultadId(FACULTAD_ID))
                .thenReturn(Facultad.builder().facultadId(FACULTAD_ID).nombre("Facultad de Ingeniería").build());
        when(tipoChequeraService.findByTipoChequeraId(TIPO_CHEQUERA_ID))
                .thenReturn(TipoChequera.builder().tipoChequeraId(TIPO_CHEQUERA_ID).nombre("Matrícula y Arancel").build());
        when(personaService.findByUnique(BigDecimal.valueOf(12345678), 1))
                .thenReturn(Persona.builder().personaId(BigDecimal.valueOf(12345678)).apellido("MUÑOZ").nombre("Ana Ejemplo").build());
        when(lectivoService.findByLectivoId(37))
                .thenReturn(Lectivo.builder().lectivoId(37).nombre("Lectivo 2026 - 2027").build());
        when(arancelTipoService.findByArancelTipoId(5))
                .thenReturn(ArancelTipoEntity.builder().arancelTipoId(5).descripcion("Ciclo Completo").build());
        TipoImpresion rapipago = new TipoImpresion();
        rapipago.setTipoImpresionId(1);
        rapipago.setNombre("Rapipago");
        when(tipoImpresionService.findByTipoImpresionId(1)).thenReturn(rapipago);
    }

    @Test
    void mapsTheHeaderFromTheChequeraSerieAndItsLookups() {
        EstadoChequera estado = estado();

        assertThat(estado.hpum()).isTrue();

        assertThat(estado.facultadId()).isEqualTo(FACULTAD_ID);
        assertThat(estado.facultadNombre()).isEqualTo("Facultad de Ingeniería");
        assertThat(estado.tipoChequeraId()).isEqualTo(TIPO_CHEQUERA_ID);
        assertThat(estado.tipoChequeraNombre()).isEqualTo("Matrícula y Arancel");
        assertThat(estado.chequeraSerieId()).isEqualTo(CHEQUERA_SERIE_ID);
        assertThat(estado.personaId()).isEqualByComparingTo("12345678");
        assertThat(estado.personaApellido()).isEqualTo("MUÑOZ");
        assertThat(estado.personaNombre()).isEqualTo("Ana Ejemplo");
        assertThat(estado.arancelTipoDescripcion()).isEqualTo("Ciclo Completo");
        assertThat(estado.lectivoNombre()).isEqualTo("Lectivo 2026 - 2027");
        assertThat(estado.becaPorcentaje()).isEqualByComparingTo("0.15");
        assertThat(estado.tipoImpresionNombre()).isEqualTo("Rapipago");
        assertThat(estado.alternativaId()).isEqualTo(ALTERNATIVA_ID);
    }

    @Test
    void ordersTheProductsByProductoIdEvenWhenTheyArriveInReverse() {
        when(chequeraService.findAllCuotaPagosByChequera(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, ALTERNATIVA_ID))
                .thenReturn(List.of(
                        cuota(ARANCEL_ID, 1, "331000", null),
                        cuota(MATRICULA_ID, 1, "182000", null),
                        cuota(ARANCEL_ID, 2, "331000", null)));

        EstadoChequera estado = estado();

        assertThat(estado.productos()).extracting(ProductoEstado::productoId).containsExactly(MATRICULA_ID, ARANCEL_ID);
        assertThat(estado.productos().get(1).cuotas()).extracting(CuotaEstado::cuotaId).containsExactly(1, 2);
    }

    @Test
    void takesTheTitleInstallmentCountAndTotalsFromAlternativaAndChequeraTotal() {
        when(chequeraService.findAllCuotaPagosByChequera(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, ALTERNATIVA_ID))
                .thenReturn(List.of(cuota(ARANCEL_ID, 1, "331000", null)));
        when(chequeraAlternativaService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, ARANCEL_ID, ALTERNATIVA_ID))
                .thenReturn(alternativa("Arancel Mensual", 12));
        when(chequeraTotalService.findAllByChequera(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID))
                .thenReturn(List.of(ChequeraTotal.builder().productoId(ARANCEL_ID)
                        .total(new BigDecimal("3243000.00")).pagado(new BigDecimal("2471000.00")).build()));

        ProductoEstado arancel = estado().productos().get(0);

        assertThat(arancel.nombre()).isEqualTo("Arancel");
        assertThat(arancel.tituloCuota()).isEqualTo("Arancel Mensual");
        assertThat(arancel.totalCuotas()).isEqualTo(12);
        // Los subtotales son los oficiales de chequera_total, no la suma de las cuotas recibidas.
        assertThat(arancel.total()).isEqualByComparingTo("3243000.00");
        assertThat(arancel.pagado()).isEqualByComparingTo("2471000.00");
    }

    @Test
    void fallsBackToTheProductNameAndTheReceivedCuotasWhenThereIsNoAlternativa() {
        when(chequeraService.findAllCuotaPagosByChequera(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, ALTERNATIVA_ID))
                .thenReturn(List.of(cuota(ARANCEL_ID, 1, "331000", null), cuota(ARANCEL_ID, 2, "331000", null)));
        when(chequeraAlternativaService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, ARANCEL_ID, ALTERNATIVA_ID))
                .thenThrow(new IllegalStateException("sin alternativa"));

        ProductoEstado arancel = estado().productos().get(0);

        assertThat(arancel.tituloCuota()).isEqualTo("Arancel");
        assertThat(arancel.totalCuotas()).isEqualTo(2);
        assertThat(arancel.total()).isEqualByComparingTo("0");
        assertThat(arancel.pagado()).isEqualByComparingTo("0");
    }

    @Test
    void takesTheFirstPaymentOfEachCuotaAndLeavesUnpaidCuotasWithoutPaymentData() {
        ChequeraPagoDto primero = pago(0, OffsetDateTime.of(2026, 6, 19, 10, 0, 0, 0, ZoneOffset.UTC), "182000", "D2026062301_30000000000");
        ChequeraPagoDto segundo = pago(1, OffsetDateTime.of(2026, 7, 1, 10, 0, 0, 0, ZoneOffset.UTC), "5", "otro");
        ChequeraCuotaPagosDto paga = cuota(MATRICULA_ID, 1, "182000", primero);
        paga.setChequeraPagos(List.of(primero, segundo));
        when(chequeraService.findAllCuotaPagosByChequera(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, ALTERNATIVA_ID))
                .thenReturn(List.of(paga, cuota(MATRICULA_ID, 2, "193000", null)));

        List<CuotaEstado> cuotas = estado().productos().get(0).cuotas();

        assertThat(cuotas.get(0).ordenPago()).isZero();
        assertThat(cuotas.get(0).fechaPago()).isEqualTo(LocalDate.of(2026, 6, 19));
        assertThat(cuotas.get(0).importePagado()).isEqualByComparingTo("182000");
        assertThat(cuotas.get(0).referenciaPago()).isEqualTo("D2026062301_30000000000");
        assertThat(cuotas.get(1).ordenPago()).isNull();
        assertThat(cuotas.get(1).fechaPago()).isNull();
        assertThat(cuotas.get(1).importePagado()).isNull();
        assertThat(cuotas.get(1).referenciaPago()).isNull();
    }

    @Test
    void keepsTheCalendarDayOfThePrimerVencimientoAndConvertsPaymentDatesToUtc() {
        // 23:00 en Mendoza ya es el día siguiente en UTC: el vencimiento conserva su día, el pago no.
        ChequeraCuotaPagosDto cuota = cuota(MATRICULA_ID, 1, "182000",
                pago(0, OffsetDateTime.of(2026, 6, 19, 22, 0, 0, 0, MENDOZA), "182000", "MercadoPago"));
        cuota.setVencimiento1(OffsetDateTime.of(2026, 6, 10, 23, 0, 0, 0, MENDOZA));
        when(chequeraService.findAllCuotaPagosByChequera(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, ALTERNATIVA_ID))
                .thenReturn(List.of(cuota));

        CuotaEstado mapeada = estado().productos().get(0).cuotas().get(0);

        assertThat(mapeada.primerVencimiento()).isEqualTo(LocalDate.of(2026, 6, 10));
        assertThat(mapeada.fechaPago()).isEqualTo(LocalDate.of(2026, 6, 20));
    }

    @Test
    void leavesTheOptionalHeaderDataNullWhenTheLookupsFail() {
        when(facultadService.findByFacultadId(FACULTAD_ID)).thenThrow(new FacultadException(FACULTAD_ID));
        when(personaService.findByUnique(BigDecimal.valueOf(12345678), 1)).thenThrow(new PersonaException(BigDecimal.valueOf(12345678), 1));
        when(lectivoService.findByLectivoId(37)).thenThrow(new LectivoException(37));
        when(arancelTipoService.findByArancelTipoId(5)).thenThrow(new ArancelTipoException(5));
        when(tipoImpresionService.findByTipoImpresionId(1)).thenThrow(new IllegalStateException("sin tipo de impresión"));

        EstadoChequera estado = estado();

        assertThat(estado.facultadNombre()).isNull();
        assertThat(estado.personaApellido()).isNull();
        assertThat(estado.personaNombre()).isNull();
        assertThat(estado.lectivoNombre()).isNull();
        assertThat(estado.arancelTipoDescripcion()).isNull();
        assertThat(estado.tipoImpresionNombre()).isNull();
        // Lo que sí es obligatorio se mantiene.
        assertThat(estado.tipoChequeraNombre()).isEqualTo("Matrícula y Arancel");
        assertThat(estado.chequeraSerieId()).isEqualTo(CHEQUERA_SERIE_ID);
    }

    @Test
    void takesTheDebitAmountFromTheMatchingCuotaAndConvertsItsDatesToUtc() {
        when(chequeraService.findAllCuotaPagosByChequera(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, ALTERNATIVA_ID))
                .thenReturn(List.of(cuota(MATRICULA_ID, 1, "182000", null), cuota(ARANCEL_ID, 1, "331000", null)));
        Debito debito = new Debito();
        debito.setProductoId(ARANCEL_ID);
        debito.setAlternativaId(ALTERNATIVA_ID);
        debito.setCuotaId(1);
        debito.setCbu("1234567890123456789012");
        debito.setFechaVencimiento(OffsetDateTime.of(2026, 7, 22, 21, 0, 0, 0, MENDOZA));
        debito.setFechaEnvio(OffsetDateTime.of(2026, 7, 3, 10, 30, 0, 0, MENDOZA));
        debito.setRechazado((byte) 1);
        debito.setMotivoRechazo("0011000100");
        Debito sinCuota = new Debito();
        sinCuota.setProductoId(ARANCEL_ID);
        sinCuota.setAlternativaId(ALTERNATIVA_ID);
        sinCuota.setCuotaId(99);
        when(debitoService.findAllByChequera(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, DEBITO_TIPO_ID))
                .thenReturn(List.of(debito, sinCuota));

        List<DebitoEstado> debitos = estado().debitos();

        assertThat(debitos).hasSize(2);
        assertThat(debitos.get(0).importe()).isEqualByComparingTo("331000");
        assertThat(debitos.get(0).cbu()).isEqualTo("1234567890123456789012");
        assertThat(debitos.get(0).fechaVencimiento()).isEqualTo(LocalDate.of(2026, 7, 23));
        assertThat(debitos.get(0).fechaEnvio()).isEqualTo(LocalDateTime.of(2026, 7, 3, 13, 30));
        assertThat(debitos.get(0).rechazado()).isTrue();
        assertThat(debitos.get(0).motivoRechazo()).isEqualTo("0011000100");
        // Sin cuota que coincida, el importe queda en cero y el débito no figura rechazado.
        assertThat(debitos.get(1).importe()).isEqualByComparingTo("0");
        assertThat(debitos.get(1).rechazado()).isFalse();
        assertThat(debitos.get(1).fechaEnvio()).isNull();
    }

    @Test
    void failsWhenTheChequeraDoesNotExist() {
        when(chequeraSerieService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID))
                .thenThrow(new ChequeraSerieException(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID));

        assertThatThrownBy(this::estado).isInstanceOf(ChequeraSerieException.class);
    }

    @Test
    void hpumIsFalseWhenTheChequeraSerieHasNoHpumFlag() {
        when(chequeraSerieService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID))
                .thenReturn(serieBuilder().hpum(null).build());

        assertThat(estado().hpum()).isFalse();
    }

    @Test
    void hpumIsFalseWhenTheChequeraSerieHasHpumZero() {
        when(chequeraSerieService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID))
                .thenReturn(serieBuilder().hpum((byte) 0).build());

        assertThat(estado().hpum()).isFalse();
    }

    private EstadoChequera estado() {
        return useCase.getEstadoChequera(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, ALTERNATIVA_ID, DEBITO_TIPO_ID);
    }

    private static ChequeraCuotaPagosDto cuota(Integer productoId, Integer cuotaId, String importe, ChequeraPagoDto pago) {
        return ChequeraCuotaPagosDto.builder()
                .productoId(productoId)
                .alternativaId(ALTERNATIVA_ID)
                .cuotaId(cuotaId)
                .mes(cuotaId + 2)
                .anho(2026)
                .importe1(new BigDecimal(importe))
                .vencimiento1(OffsetDateTime.of(2026, cuotaId + 2, 10, 0, 0, 0, 0, MENDOZA))
                .producto(Producto.builder().productoId(productoId)
                        .nombre(productoId.equals(MATRICULA_ID) ? "Matrícula" : "Arancel").build())
                .chequeraPagos(pago == null ? List.of() : List.of(pago))
                .build();
    }

    private static ChequeraPagoDto pago(int orden, OffsetDateTime fecha, String importe, String archivo) {
        return ChequeraPagoDto.builder().orden(orden).fecha(fecha).importe(new BigDecimal(importe)).archivo(archivo).build();
    }

    private static ChequeraAlternativa alternativa(String titulo, int cuotas) {
        ChequeraAlternativa alternativa = new ChequeraAlternativa();
        alternativa.setTitulo(titulo);
        alternativa.setCuotas(cuotas);
        return alternativa;
    }
}