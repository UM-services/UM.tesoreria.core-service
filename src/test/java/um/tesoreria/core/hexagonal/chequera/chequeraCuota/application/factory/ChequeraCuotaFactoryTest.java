package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChequeraCuotaFactoryTest {

    @Mock
    private ChequeraCuotaService chequeraCuotaService;

    @Test
    void crear_appliesSameHalfUpFactorAndPreservesListPrices() {
        when(chequeraCuotaService.calculateCodigoBarras(any(ChequeraCuota.class))).thenReturn("barcode");

        var cuota = new ChequeraCuotaFactory(chequeraCuotaService).crear(serie("33.33"), lectivo(), 0, referencia());

        assertThat(cuota.getImporte1()).isEqualByComparingTo("67");
        assertThat(cuota.getImporte2()).isEqualByComparingTo("134");
        assertThat(cuota.getImporte3()).isEqualByComparingTo("200");
        assertThat(cuota.getImporte1Original()).isEqualByComparingTo("100.50");
        assertThat(cuota.getImporte2Original()).isEqualByComparingTo("200.50");
        assertThat(cuota.getImporte3Original()).isEqualByComparingTo("300.50");
        assertThat(cuota.getTramoId()).isZero();
        assertThat(cuota.getCodigoBarras()).isEqualTo("barcode");
    }

    @Test
    void crear_normalizesNullBenefitToZeroAndOffsetsExpiredDueDates() {
        when(chequeraCuotaService.calculateCodigoBarras(any(ChequeraCuota.class))).thenReturn("barcode");
        var expired = lectivo();
        expired.setVencimiento1(referencia().minusDays(1));

        var cuota = new ChequeraCuotaFactory(chequeraCuotaService).crear(serie(null), expired, 1, referencia());

        assertThat(cuota.getImporte1()).isEqualByComparingTo("100.50");
        assertThat(cuota.getImporte2()).isEqualByComparingTo("200.50");
        assertThat(cuota.getImporte3()).isEqualByComparingTo("300.50");
        assertThat(cuota.getVencimiento1()).isEqualTo(referencia().toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC).plusDays(37));
        assertThat(cuota.getVencimiento2()).isEqualTo(referencia().toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC).plusDays(50));
        assertThat(cuota.getVencimiento3()).isEqualTo(referencia().toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC).plusDays(70));
    }

    @Test
    void crear_preservesZeroBenefitAndZerosEveryTramoAtHundredPercent() {
        when(chequeraCuotaService.calculateCodigoBarras(any(ChequeraCuota.class))).thenReturn("barcode");
        var factory = new ChequeraCuotaFactory(chequeraCuotaService);

        var sinBeneficio = factory.crear(serie("0"), lectivo(), 0, referencia());
        var becaCompleta = factory.crear(serie("100"), lectivo(), 0, referencia());

        assertThat(sinBeneficio.getImporte1()).isEqualByComparingTo(sinBeneficio.getImporte1Original());
        assertThat(sinBeneficio.getImporte2()).isEqualByComparingTo(sinBeneficio.getImporte2Original());
        assertThat(sinBeneficio.getImporte3()).isEqualByComparingTo(sinBeneficio.getImporte3Original());
        assertThat(becaCompleta.getImporte1()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(becaCompleta.getImporte2()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(becaCompleta.getImporte3()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(becaCompleta.getImporte1Original()).isEqualByComparingTo("100.50");
    }

    @Test
    void crear_appliesTwentyPercentWithoutBreakingTramoOrder() {
        when(chequeraCuotaService.calculateCodigoBarras(any(ChequeraCuota.class))).thenReturn("barcode");

        var cuota = new ChequeraCuotaFactory(chequeraCuotaService).crear(serie("20"), lectivo(), 0, referencia());

        assertThat(cuota.getImporte1()).isEqualByComparingTo("80");
        assertThat(cuota.getImporte2()).isEqualByComparingTo("160");
        assertThat(cuota.getImporte3()).isEqualByComparingTo("240");
        assertThat(cuota.getImporte1()).isLessThanOrEqualTo(cuota.getImporte2());
        assertThat(cuota.getImporte2()).isLessThanOrEqualTo(cuota.getImporte3());
    }

    @Test
    void crear_keepsNullListPriceAsNullAndSkipsBarcodeInsteadOfFailing() {
        var incompleta = lectivo();
        incompleta.setImporte2(null);

        var cuota = new ChequeraCuotaFactory(chequeraCuotaService).crear(serie("20"), incompleta, 0, referencia());

        assertThat(cuota.getImporte2()).isNull();
        assertThat(cuota.getImporte2Original()).isNull();
        assertThat(cuota.getImporte1()).isEqualByComparingTo("80");
        assertThat(cuota.getImporte1Original()).isEqualByComparingTo("100.50");
        assertThat(cuota.getCodigoBarras()).isEmpty();
        org.mockito.Mockito.verify(chequeraCuotaService, org.mockito.Mockito.never())
                .calculateCodigoBarras(any(ChequeraCuota.class));
    }

    @Test
    void crear_keepsNullListPriceAsNullWithoutBenefit() {
        var incompleta = lectivo();
        incompleta.setImporte1(null);

        var cuota = new ChequeraCuotaFactory(chequeraCuotaService).crear(serie("0"), incompleta, 0, referencia());

        assertThat(cuota.getImporte1()).isNull();
        assertThat(cuota.getImporte1Original()).isNull();
        assertThat(cuota.getCodigoBarras()).isEmpty();
    }

    @Test
    void crear_treatsNullFirstDueDateAsNotExpiredAndCopiesDatesUntouched() {
        var sinVencimiento = lectivo();
        sinVencimiento.setVencimiento1(null);

        var cuota = new ChequeraCuotaFactory(chequeraCuotaService).crear(serie("20"), sinVencimiento, 3, referencia());

        assertThat(ChequeraCuotaFactory.vencio(sinVencimiento, referencia())).isFalse();
        assertThat(cuota.getVencimiento1()).isNull();
        assertThat(cuota.getVencimiento2()).isEqualTo(sinVencimiento.getVencimiento2());
        assertThat(cuota.getVencimiento3()).isEqualTo(sinVencimiento.getVencimiento3());
        assertThat(cuota.getImporte1()).isEqualByComparingTo("80");
        assertThat(cuota.getCodigoBarras()).isEmpty();
    }

    @Test
    void vencio_detectsExpiredFirstDueDate() {
        var vencida = lectivo();
        vencida.setVencimiento1(referencia().minusDays(1));

        assertThat(ChequeraCuotaFactory.vencio(vencida, referencia())).isTrue();
        assertThat(ChequeraCuotaFactory.vencio(lectivo(), referencia())).isFalse();
    }

    private static ChequeraSerie serie(String beneficio) {
        return ChequeraSerie.builder().chequeraId(1L).facultadId(2).tipoChequeraId(3).chequeraSerieId(4L)
                .arancelTipoId(5).becaPorcentaje(beneficio == null ? null : new BigDecimal(beneficio)).build();
    }

    private static LectivoCuota lectivo() {
        return LectivoCuota.builder().productoId(6).alternativaId(7).cuotaId(8).mes(9).anho(2026).tramoId(10)
                .vencimiento1(referencia().plusDays(1)).vencimiento2(referencia().plusDays(10)).vencimiento3(referencia().plusDays(20))
                .importe1(new BigDecimal("100.50")).importe2(new BigDecimal("200.50")).importe3(new BigDecimal("300.50")).build();
    }

    private static OffsetDateTime referencia() {
        return OffsetDateTime.of(2026, 8, 20, 13, 0, 0, 0, ZoneOffset.UTC);
    }
}
