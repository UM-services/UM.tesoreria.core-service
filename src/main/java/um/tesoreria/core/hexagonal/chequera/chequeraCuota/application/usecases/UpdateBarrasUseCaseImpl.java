package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllByChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.SaveAllChequeraCuotasUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.UpdateBarrasUseCase;
import um.tesoreria.core.util.Tool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateBarrasUseCaseImpl implements UpdateBarrasUseCase {

    private final FindAllByChequeraUseCase findAllByChequeraUseCase;
    private final SaveAllChequeraCuotasUseCase saveAllChequeraCuotasUseCase;

    @Override
    public List<ChequeraCuota> updateBarras(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        List<ChequeraCuota> chequeraCuotas = findAllByChequeraUseCase.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);

        List<ChequeraCuota> actualizadas = chequeraCuotas.stream()
                .filter(chequeraCuota -> chequeraCuota.getBaja() == 0 &&
                        chequeraCuota.getPagado() == 0 &&
                        chequeraCuota.getImporte1().compareTo(BigDecimal.ZERO) > 0)
                .peek(chequeraCuota -> chequeraCuota.setCodigoBarras(this.calculateCodigoBarras(chequeraCuota)))
                .collect(Collectors.toList());

        return saveAllChequeraCuotasUseCase.saveAll(actualizadas);
    }

    @Override
    public String calculateCodigoBarras(ChequeraCuota chequeraCuota) {
        String codigoBarras = "";

        if (Objects.requireNonNull(chequeraCuota.getVencimiento3()).isBefore(chequeraCuota.getVencimiento2())) {
            chequeraCuota.setVencimiento2(chequeraCuota.getVencimiento3());
        }
        if (Objects.requireNonNull(chequeraCuota.getVencimiento2()).isBefore(chequeraCuota.getVencimiento1())) {
            chequeraCuota.setVencimiento1(chequeraCuota.getVencimiento2());
        }
        // Codigo Gire
        codigoBarras += "255";
        // Codigo Unidad Academica
        codigoBarras += new DecimalFormat("00").format(chequeraCuota.getFacultadId());
        // Tipo de Chequera
        codigoBarras += new DecimalFormat("000").format(chequeraCuota.getTipoChequeraId());
        // Numero de Chequera
        codigoBarras += new DecimalFormat("00000").format(chequeraCuota.getChequeraSerieId());
        // Producto
        codigoBarras += new DecimalFormat("0").format(chequeraCuota.getProductoId());
        // Mes
        codigoBarras += new DecimalFormat("00").format(chequeraCuota.getMes());
        // Año
        int anho = chequeraCuota.getAnho();
        if (anho > 2000) {
            anho -= 2000;
        }
        codigoBarras += new DecimalFormat("00").format(anho);
        // Cuota ID
        codigoBarras += new DecimalFormat("00").format(chequeraCuota.getCuotaId());
        // 1er Importe
        codigoBarras += new DecimalFormat("0000000").format(chequeraCuota.getImporte1());
        OffsetDateTime vencimiento1 = Objects.requireNonNull(chequeraCuota.getVencimiento1()).plusHours(3);
        OffsetDateTime vencimiento2 = chequeraCuota.getVencimiento2().plusHours(3);
        OffsetDateTime vencimiento3 = chequeraCuota.getVencimiento3().plusHours(3);
        // Dia 1er Vencimiento
        codigoBarras += new DecimalFormat("00").format(vencimiento1.getDayOfMonth());
        log.info("Vencimiento1 -> {}", vencimiento1);
        // Mes 1er Vencimiento
        codigoBarras += new DecimalFormat("00").format(vencimiento1.getMonthValue());
        // Año 1er Vencimiento
        codigoBarras += new DecimalFormat("0000").format(vencimiento1.getYear());
        // Dif 2do Importe
        BigDecimal diferenciaImporte1 = chequeraCuota.getImporte2().subtract(chequeraCuota.getImporte1()).setScale(0,
                RoundingMode.HALF_UP);
        if (diferenciaImporte1.compareTo(BigDecimal.ZERO) < 0) {
            log.debug("chequera_cuota_service.calculate_codigo_barras.diferencia_importe_1 < 0");
        }
        codigoBarras += new DecimalFormat("00000")
                .format(diferenciaImporte1);
        // Dif 2do Vencimiento
        Long diferencia1 = ChronoUnit.DAYS.between(vencimiento1.toLocalDate(), vencimiento2.toLocalDate());
        if (diferencia1 < 0) {
            log.debug("chequera_cuota_service.calculate_codigo_barras.diferencia_1 < 0");
        }
        codigoBarras += new DecimalFormat("000").format(diferencia1);
        // Dif 3er Importe
        BigDecimal diferenciaImporte2 = chequeraCuota.getImporte3().subtract(chequeraCuota.getImporte2()).setScale(0,
                RoundingMode.HALF_UP);
        if (diferenciaImporte2.compareTo(BigDecimal.ZERO) < 0) {
            log.debug("chequera_cuota_service.calculate_codigo_barras.diferencia_importe_2 < 0");
        }
        codigoBarras += new DecimalFormat("00000")
                .format(diferenciaImporte2);
        // Dif 3er Vencimiento
        Long diferencia2 = ChronoUnit.DAYS.between(vencimiento2.toLocalDate(), vencimiento3.toLocalDate());
        if (diferencia2 < 0) {
            log.debug("chequera_cuota_service.calculate_codigo_barras.diferencia2 < 0");
        }
        codigoBarras += new DecimalFormat("000").format(diferencia2);
        // Codigo Verificador
        codigoBarras += Tool.calculateVerificadorRapiPago(codigoBarras);

        return codigoBarras;
    }
}
