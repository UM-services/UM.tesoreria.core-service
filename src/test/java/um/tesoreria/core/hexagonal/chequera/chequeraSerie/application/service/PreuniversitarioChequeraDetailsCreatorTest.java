package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.factory.ChequeraCuotaFactory;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraAlternativaFaltanteException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.kotlin.model.ChequeraAlternativa;
import um.tesoreria.core.kotlin.model.LectivoAlternativa;
import um.tesoreria.core.model.LectivoTotal;
import um.tesoreria.core.service.ChequeraAlternativaService;
import um.tesoreria.core.service.LectivoAlternativaService;
import um.tesoreria.core.service.LectivoTotalService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreuniversitarioChequeraDetailsCreatorTest {

    @Mock
    private LectivoTotalService lectivoTotalService;
    @Mock
    private ChequeraTotalService chequeraTotalService;
    @Mock
    private LectivoAlternativaService lectivoAlternativaService;
    @Mock
    private ChequeraAlternativaService chequeraAlternativaService;
    @Mock
    private LectivoCuotaService lectivoCuotaService;
    @Mock
    private ChequeraCuotaService chequeraCuotaService;

    @Test
    void create_characterizesCurrentListPricesOriginalsBarcodeAndOrder() {
        var series = serie();
        var source = LectivoCuota.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3)
                .productoId(4).alternativaId(5).cuotaId(6).mes(7).anho(2026).tramoId(99)
                .vencimiento1(OffsetDateTime.now().plusDays(10)).importe1(new BigDecimal("100.50"))
                .vencimiento2(OffsetDateTime.now().plusDays(20)).importe2(new BigDecimal("120.50"))
                .vencimiento3(OffsetDateTime.now().plusDays(30)).importe3(new BigDecimal("140.50"))
                .build();

        when(lectivoAlternativaService.findAllByTipo(1, 2, 3, 5)).thenReturn(List.of(lectivoAlternativa(4, 5)));
        when(lectivoCuotaService.findAllByTipo(1, 2, 3, 5)).thenReturn(List.of(source));
        when(chequeraCuotaService.calculateCodigoBarras(any(ChequeraCuota.class))).thenReturn("barcode");
        when(chequeraCuotaService.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraTotalService.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraAlternativaService.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        creator().create(series);

        var cuotas = captor(ChequeraCuota.class);
        org.mockito.Mockito.verify(chequeraCuotaService).saveAll(cuotas.capture());
        var cuota = cuotas.getValue().getFirst();
        assertThat(cuota.getVencimiento1()).isEqualTo(source.getVencimiento1());
        assertThat(cuota.getVencimiento2()).isEqualTo(source.getVencimiento2());
        assertThat(cuota.getVencimiento3()).isEqualTo(source.getVencimiento3());
        assertThat(cuota.getImporte1()).isEqualByComparingTo(source.getImporte1());
        assertThat(cuota.getImporte2()).isEqualByComparingTo(source.getImporte2());
        assertThat(cuota.getImporte3()).isEqualByComparingTo(source.getImporte3());
        assertThat(cuota.getImporte1Original()).isEqualByComparingTo(source.getImporte1());
        assertThat(cuota.getImporte2Original()).isEqualByComparingTo(source.getImporte2());
        assertThat(cuota.getImporte3Original()).isEqualByComparingTo(source.getImporte3());
        assertThat(cuota.getArancelTipoId()).isEqualTo(series.getArancelTipoId());
        assertThat(cuota.getTramoId()).isZero();
        assertThat(cuota.getCodigoBarras()).isEqualTo("barcode");
        assertThat(cuota.getPagado()).isZero();
        assertThat(cuota.getBaja()).isZero();
        assertThat(cuota.getManual()).isZero();
        assertThat(cuota.getCompensada()).isZero();

        InOrder order = inOrder(chequeraTotalService, chequeraAlternativaService, chequeraCuotaService);
        order.verify(chequeraTotalService).saveAll(anyList());
        order.verify(chequeraAlternativaService).saveAll(anyList());
        order.verify(chequeraCuotaService).saveAll(anyList());
    }

    @Test
    void create_calculatesTotalsFromSavedActiveCuotas() {
        var series = serie();
        var source = LectivoCuota.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3)
                .productoId(4).alternativaId(5).cuotaId(6).mes(7).anho(2026)
                .vencimiento1(OffsetDateTime.now().plusDays(10)).importe1(new BigDecimal("999.99"))
                .vencimiento2(OffsetDateTime.now().plusDays(20)).importe2(new BigDecimal("1000"))
                .vencimiento3(OffsetDateTime.now().plusDays(30)).importe3(new BigDecimal("1001"))
                .build();

        when(lectivoAlternativaService.findAllByTipo(1, 2, 3, 5)).thenReturn(List.of(lectivoAlternativa(4, 5)));
        when(lectivoCuotaService.findAllByTipo(1, 2, 3, 5)).thenReturn(List.of(source));
        when(chequeraCuotaService.calculateCodigoBarras(any(ChequeraCuota.class))).thenReturn("barcode");
        when(chequeraCuotaService.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraTotalService.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraAlternativaService.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        creator().create(series);

        var totals = captor(ChequeraTotal.class);
        org.mockito.Mockito.verify(chequeraTotalService).saveAll(totals.capture());
        assertThat(totals.getValue()).singleElement().satisfies(saved -> {
            assertThat(saved.getProductoId()).isEqualTo(4);
            assertThat(saved.getTotal()).isEqualByComparingTo("999.99");
            assertThat(saved.getPagado()).isEqualByComparingTo(BigDecimal.ZERO);
        });
    }

    @Test
    void create_replacesExpiredDueDatesAndIncrementsOnlyExpiredQuotaOffsets() {
        var series = serie();
        var future = cuota(4, OffsetDateTime.now().plusDays(10));
        var expired1 = cuota(5, OffsetDateTime.now().minusDays(2));
        var expired2 = cuota(6, OffsetDateTime.now().minusDays(1));
        when(chequeraCuotaService.calculateCodigoBarras(any(ChequeraCuota.class))).thenReturn("barcode");
        stubCreateSources(List.of(future, expired1, expired2));

        creator().create(series);

        var cuotas = captor(ChequeraCuota.class);
        org.mockito.Mockito.verify(chequeraCuotaService).saveAll(cuotas.capture());
        var saved = cuotas.getValue();
        assertThat(saved.get(0).getVencimiento1()).isEqualTo(future.getVencimiento1());
        assertThat(saved.get(1).getVencimiento2()).isEqualTo(saved.get(1).getVencimiento1().plusDays(13));
        assertThat(saved.get(1).getVencimiento3()).isEqualTo(saved.get(1).getVencimiento1().plusDays(33));
        assertThat(saved.get(2).getVencimiento1()).isEqualTo(saved.get(1).getVencimiento1().plusDays(30));
    }

    @Test
    void create_savesEmptyCuotasAndTotalsWhenNoLectivoCuotasExist() {
        var series = serie();
        stubCreateSources(List.of());

        creator().create(series);

        var cuotas = captor(ChequeraCuota.class);
        var totals = captor(ChequeraTotal.class);
        org.mockito.Mockito.verify(chequeraCuotaService).saveAll(cuotas.capture());
        org.mockito.Mockito.verify(chequeraTotalService).saveAll(totals.capture());
        assertThat(cuotas.getValue()).isEmpty();
        assertThat(totals.getValue()).isEmpty();
    }

    @Test
    void create_totalsOnlyActiveCuotasAndKeepsProductsSeparated() {
        var series = serie();
        var factory = org.mockito.Mockito.mock(ChequeraCuotaFactory.class);
        var activaProducto1 = ChequeraCuota.builder().productoId(1).alternativaId(5)
                .importe1(new BigDecimal("11")).baja((byte) 0).build();
        var bajaProducto1 = ChequeraCuota.builder().productoId(1).alternativaId(5)
                .importe1(new BigDecimal("99")).baja((byte) 1).build();
        var activaProducto2 = ChequeraCuota.builder().productoId(2).alternativaId(5)
                .importe1(new BigDecimal("22")).baja((byte) 0).build();
        stubCreateSources(List.of(cuota(1, OffsetDateTime.now().plusDays(1)),
                cuota(2, OffsetDateTime.now().plusDays(2)), cuota(3, OffsetDateTime.now().plusDays(3))));
        when(lectivoAlternativaService.findAllByTipo(1, 2, 3, 5))
                .thenReturn(List.of(lectivoAlternativa(1, 5), lectivoAlternativa(2, 5)));
        when(factory.crear(any(), any(), org.mockito.ArgumentMatchers.anyInt(), any()))
                .thenReturn(activaProducto1, bajaProducto1, activaProducto2);

        creator(factory).create(series);

        var totals = captor(ChequeraTotal.class);
        org.mockito.Mockito.verify(chequeraTotalService).saveAll(totals.capture());
        assertThat(totals.getValue()).extracting(ChequeraTotal::getProductoId, ChequeraTotal::getTotal)
                .containsExactlyInAnyOrder(org.assertj.core.groups.Tuple.tuple(1, new BigDecimal("11")),
                        org.assertj.core.groups.Tuple.tuple(2, new BigDecimal("22")));
    }

    @Test
    void create_keepsProductsWithoutCuotasInTheChosenAlternativeAsZeroTotals() {
        var series = serie();
        when(lectivoTotalService.findAllByTipo(1, 2, 3)).thenReturn(List.of(lectivoTotal(4), lectivoTotal(8)));
        when(chequeraCuotaService.calculateCodigoBarras(any(ChequeraCuota.class))).thenReturn("barcode");
        stubCreateSources(List.of(cuota(1, OffsetDateTime.now().plusDays(1))));

        creator().create(series);

        var totals = captor(ChequeraTotal.class);
        org.mockito.Mockito.verify(chequeraTotalService).saveAll(totals.capture());
        assertThat(totals.getValue()).extracting(ChequeraTotal::getProductoId, ChequeraTotal::getTotal)
                .containsExactlyInAnyOrder(org.assertj.core.groups.Tuple.tuple(4, BigDecimal.TEN),
                        org.assertj.core.groups.Tuple.tuple(8, BigDecimal.ZERO));
        assertThat(totals.getValue()).allSatisfy(saved ->
                assertThat(saved.getPagado()).isEqualByComparingTo(BigDecimal.ZERO));
    }

    @Test
    void create_persistsZeroTotalWhenTheProductHasNoCuotasAtAll() {
        var series = serie();
        when(lectivoTotalService.findAllByTipo(1, 2, 3)).thenReturn(List.of(lectivoTotal(8)));
        stubCreateSources(List.of());

        creator().create(series);

        var totals = captor(ChequeraTotal.class);
        org.mockito.Mockito.verify(chequeraTotalService).saveAll(totals.capture());
        assertThat(totals.getValue()).singleElement().satisfies(saved -> {
            assertThat(saved.getProductoId()).isEqualTo(8);
            assertThat(saved.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        });
    }

    @Test
    void create_excludesNullImportesFromTotalsWithoutAbortingEmission() {
        var series = serie();
        var factory = org.mockito.Mockito.mock(ChequeraCuotaFactory.class);
        var conImporte = ChequeraCuota.builder().productoId(4).alternativaId(5).cuotaId(1)
                .importe1(new BigDecimal("70")).baja((byte) 0).build();
        var sinImporte = ChequeraCuota.builder().productoId(4).alternativaId(5).cuotaId(2)
                .importe1(null).baja((byte) 0).build();
        stubCreateSources(List.of(cuota(1, OffsetDateTime.now().plusDays(1)),
                cuota(2, OffsetDateTime.now().plusDays(2))));
        when(factory.crear(any(), any(), org.mockito.ArgumentMatchers.anyInt(), any()))
                .thenReturn(conImporte, sinImporte);

        creator(factory).create(series);

        var totals = captor(ChequeraTotal.class);
        org.mockito.Mockito.verify(chequeraTotalService).saveAll(totals.capture());
        assertThat(totals.getValue()).singleElement().satisfies(saved -> {
            assertThat(saved.getProductoId()).isEqualTo(4);
            assertThat(saved.getTotal()).isEqualByComparingTo("70");
        });
    }

    @Test
    void create_doesNotShiftOffsetWhenTheFirstDueDateIsNull() {
        var series = serie();
        var sinVencimiento = LectivoCuota.builder().facultadId(1).lectivoId(2).tipoChequeraId(3)
                .productoId(4).alternativaId(5).cuotaId(1).mes(7).anho(2026)
                .importe1(BigDecimal.TEN).importe2(BigDecimal.valueOf(20)).importe3(BigDecimal.valueOf(30))
                .build();
        var futura = cuota(2, OffsetDateTime.now().plusDays(10));
        when(chequeraCuotaService.calculateCodigoBarras(any(ChequeraCuota.class))).thenReturn("barcode");
        stubCreateSources(List.of(sinVencimiento, futura));

        creator().create(series);

        var cuotas = captor(ChequeraCuota.class);
        org.mockito.Mockito.verify(chequeraCuotaService).saveAll(cuotas.capture());
        var saved = cuotas.getValue();
        assertThat(saved.get(0).getVencimiento1()).isNull();
        assertThat(saved.get(0).getCodigoBarras()).isEmpty();
        assertThat(saved.get(1).getVencimiento1()).isEqualTo(futura.getVencimiento1());
    }

    @Test
    void create_copiesAlternativesForTheConstructedCuotas() {
        var source = org.mockito.Mockito.mock(LectivoAlternativa.class);
        when(source.getProductoId()).thenReturn(7);
        when(source.getAlternativaId()).thenReturn(5);
        when(source.getTitulo()).thenReturn("Plan de pagos");
        when(source.getCuotas()).thenReturn(3);
        when(lectivoAlternativaService.findAllByTipo(1, 2, 3, 5)).thenReturn(List.of(source));
        when(lectivoCuotaService.findAllByTipo(1, 2, 3, 5)).thenReturn(List.of());
        when(chequeraAlternativaService.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraCuotaService.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraTotalService.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        creator().create(serie());

        var alternatives = captor(ChequeraAlternativa.class);
        org.mockito.Mockito.verify(chequeraAlternativaService).saveAll(alternatives.capture());
        assertThat(alternatives.getValue()).singleElement().satisfies(alternative -> {
            assertThat(alternative.getProductoId()).isEqualTo(7);
            assertThat(alternative.getTitulo()).isEqualTo("Plan de pagos");
            assertThat(alternative.getCuotas()).isEqualTo(3);
        });
    }

    @Test
    void create_failsWithTheMissingKeyWhenTheCuotaHasNoMatchingAlternative() {
        var series = serie();
        when(lectivoAlternativaService.findAllByTipo(1, 2, 3, 5)).thenReturn(List.of(lectivoAlternativa(8, 5)));
        when(lectivoCuotaService.findAllByTipo(1, 2, 3, 5))
                .thenReturn(List.of(cuota(1, OffsetDateTime.now().plusDays(1))));
        when(chequeraAlternativaService.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> creator().create(series))
                .isInstanceOf(ChequeraAlternativaFaltanteException.class)
                .hasMessageContaining("1/3/4/4/5");

        org.mockito.Mockito.verify(chequeraCuotaService, org.mockito.Mockito.never()).saveAll(anyList());
        org.mockito.Mockito.verify(chequeraTotalService).saveAll(anyList());
    }

    private void stubCreateSources(List<LectivoCuota> cuotas) {
        when(lectivoAlternativaService.findAllByTipo(1, 2, 3, 5)).thenReturn(List.of(lectivoAlternativa(4, 5)));
        when(lectivoCuotaService.findAllByTipo(1, 2, 3, 5)).thenReturn(cuotas);
        when(chequeraCuotaService.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraTotalService.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraAlternativaService.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private static LectivoAlternativa lectivoAlternativa(int productoId, int alternativaId) {
        return new LectivoAlternativa(null, 1, 2, 3, productoId, alternativaId, "Plan de pagos", 1, 0);
    }

    private static LectivoCuota cuota(int cuotaId, OffsetDateTime vencimiento1) {
        return LectivoCuota.builder().facultadId(1).lectivoId(2).tipoChequeraId(3)
                .productoId(4).alternativaId(5).cuotaId(cuotaId).mes(7).anho(2026)
                .vencimiento1(vencimiento1).importe1(BigDecimal.TEN)
                .vencimiento2(vencimiento1.plusDays(10)).importe2(BigDecimal.valueOf(20))
                .vencimiento3(vencimiento1.plusDays(20)).importe3(BigDecimal.valueOf(30)).build();
    }

    private PreuniversitarioChequeraDetailsCreator creator() {
        return creator(new ChequeraCuotaFactory(chequeraCuotaService));
    }

    private PreuniversitarioChequeraDetailsCreator creator(ChequeraCuotaFactory factory) {
        return new PreuniversitarioChequeraDetailsCreator(lectivoTotalService, chequeraTotalService,
                lectivoAlternativaService, chequeraAlternativaService, lectivoCuotaService,
                factory, chequeraCuotaService);
    }

    private static LectivoTotal lectivoTotal(int productoId) {
        var total = new LectivoTotal();
        total.setFacultadId(1);
        total.setLectivoId(2);
        total.setTipoChequeraId(3);
        total.setProductoId(productoId);
        total.setTotal(new BigDecimal("5000"));
        return total;
    }

    private static ChequeraSerie serie() {
        return ChequeraSerie.builder()
                .chequeraId(10L).facultadId(1).lectivoId(2).tipoChequeraId(3)
                .chequeraSerieId(4L).alternativaId(5).arancelTipoId(6)
                .becaPorcentaje(BigDecimal.ZERO)
                .build();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T> ArgumentCaptor<List<T>> captor(Class<T> ignored) {
        return ArgumentCaptor.forClass((Class) List.class);
    }
}
