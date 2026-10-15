package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.application.usecases;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.exception.ArancelPorcentajeException;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.service.ArancelPorcentajeService;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.exception.LectivoCuotaException;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SanitizeChequeraCuotaArancelUseCaseImplTest {

    private static final Integer FACULTAD_ID = 2;
    private static final Integer TIPO_CHEQUERA_ID = 517;
    private static final Long CHEQUERA_SERIE_ID = 2788L;
    private static final Integer PRODUCTO_ID = 3;
    private static final Integer ALTERNATIVA_ID = 1;
    private static final Integer CUOTA_ID = 1;
    private static final Integer ARANCEL_TIPO_ID = 46;
    private static final Integer LECTIVO_ID = 37;

    private final ArancelPorcentajeService arancelPorcentajeService = mock(ArancelPorcentajeService.class);
    private final LectivoCuotaService lectivoCuotaService = mock(LectivoCuotaService.class);

    private final SanitizeChequeraCuotaArancelUseCaseImpl useCase = new SanitizeChequeraCuotaArancelUseCaseImpl(
            arancelPorcentajeService, lectivoCuotaService);

    private ChequeraCuota.ChequeraCuotaBuilder cuotaCompleta() {
        return ChequeraCuota.builder()
                .chequeraCuotaId(3167274L)
                .facultadId(FACULTAD_ID)
                .tipoChequeraId(TIPO_CHEQUERA_ID)
                .chequeraSerieId(CHEQUERA_SERIE_ID)
                .productoId(PRODUCTO_ID)
                .alternativaId(ALTERNATIVA_ID)
                .cuotaId(CUOTA_ID)
                .arancelTipoId(ARANCEL_TIPO_ID)
                .importe1(new BigDecimal("325000.0000"))
                .importe1Original(new BigDecimal("325000.0000"))
                .importe2(new BigDecimal("341250.0000"))
                .importe2Original(new BigDecimal("341250.0000"))
                .importe3(new BigDecimal("357500.0000"))
                .importe3Original(new BigDecimal("357500.0000"))
                .pagado((byte) 0).baja((byte) 0).manual((byte) 0).compensada((byte) 0);
    }

    private void porcentaje(Integer arancelTipoId, Integer productoId, String porcentaje) {
        when(arancelPorcentajeService.findByUnique(arancelTipoId, productoId))
                .thenReturn(ArancelPorcentaje.builder().porcentaje(new BigDecimal(porcentaje)).build());
    }

    private void diseno(String importe1, String importe2, String importe3) {
        when(lectivoCuotaService.findByUniqueKey(FACULTAD_ID, LECTIVO_ID, TIPO_CHEQUERA_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID))
                .thenReturn(LectivoCuota.builder()
                        .importe1(new BigDecimal(importe1))
                        .importe2(new BigDecimal(importe2))
                        .importe3(new BigDecimal(importe3))
                        .build());
    }

    @Test
    void saneaImportesCompletosAplicandoElPorcentajeYReportaCorreccion() {
        porcentaje(ARANCEL_TIPO_ID, PRODUCTO_ID, "50.00");
        diseno("325000.0000", "341250.0000", "357500.0000");
        var cuota = cuotaCompleta().build();

        var corregida = useCase.sanitize(cuota, ARANCEL_TIPO_ID, LECTIVO_ID);

        assertThat(corregida).isTrue();
        assertThat(cuota.getImporte1()).isEqualByComparingTo("162500.0000");
        assertThat(cuota.getImporte2()).isEqualByComparingTo("170625.0000");
        assertThat(cuota.getImporte3()).isEqualByComparingTo("178750.0000");
        assertThat(cuota.getImporte1Original()).isEqualByComparingTo("325000.0000");
    }

    @Test
    void noReportaCorreccionSiYaEstaParcial() {
        porcentaje(ARANCEL_TIPO_ID, PRODUCTO_ID, "50.00");
        diseno("325000.0000", "341250.0000", "357500.0000");
        var cuota = cuotaCompleta()
                .importe1(new BigDecimal("162500.0000"))
                .importe2(new BigDecimal("170625.0000"))
                .importe3(new BigDecimal("178750.0000"))
                .build();

        var corregida = useCase.sanitize(cuota, ARANCEL_TIPO_ID, LECTIVO_ID);

        assertThat(corregida).isFalse();
        assertThat(cuota.getImporte1()).isEqualByComparingTo("162500.0000");
    }

    @Test
    void sinFilaDePorcentajeAsumeCienYNoCorrige() {
        when(arancelPorcentajeService.findByUnique(ARANCEL_TIPO_ID, PRODUCTO_ID))
                .thenThrow(new ArancelPorcentajeException(ARANCEL_TIPO_ID, PRODUCTO_ID));
        diseno("325000.0000", "341250.0000", "357500.0000");
        var cuota = cuotaCompleta().build();

        var corregida = useCase.sanitize(cuota, ARANCEL_TIPO_ID, LECTIVO_ID);

        assertThat(corregida).isFalse();
        assertThat(cuota.getImporte1()).isEqualByComparingTo("325000.0000");
    }

    @Test
    void usaElArancelTipoDeLaSerieSiLaCuotaNoLoTiene() {
        porcentaje(99, PRODUCTO_ID, "50.00");
        diseno("325000.0000", "341250.0000", "357500.0000");
        var cuota = cuotaCompleta().arancelTipoId(null).build();

        var corregida = useCase.sanitize(cuota, 99, LECTIVO_ID);

        assertThat(corregida).isTrue();
        assertThat(cuota.getImporte1()).isEqualByComparingTo("162500.0000");
    }

    @Test
    void saneaAunqueNoExistaElDiseno() {
        porcentaje(ARANCEL_TIPO_ID, PRODUCTO_ID, "50.00");
        when(lectivoCuotaService.findByUniqueKey(FACULTAD_ID, LECTIVO_ID, TIPO_CHEQUERA_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID))
                .thenThrow(new LectivoCuotaException(FACULTAD_ID, LECTIVO_ID, TIPO_CHEQUERA_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID));
        var cuota = cuotaCompleta().build();

        var corregida = useCase.sanitize(cuota, ARANCEL_TIPO_ID, LECTIVO_ID);

        assertThat(corregida).isTrue();
        assertThat(cuota.getImporte1()).isEqualByComparingTo("162500.0000");
    }

    @Test
    void noRompeSiLaCuotaEsNula() {
        assertThat(useCase.sanitize(null, ARANCEL_TIPO_ID, LECTIVO_ID)).isFalse();
    }

}
