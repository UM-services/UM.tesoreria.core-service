package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.util.Tool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChequeraCuotaFactory {

    private final ChequeraCuotaService chequeraCuotaService;

    /**
     * Una cuota está vencida cuando su primer vencimiento quedó en el pasado. Con
     * {@code vencimiento1} nulo no hay forma de saberlo, así que se trata como no vencida: los
     * vencimientos se copian tal cual y el llamador no incrementa el offset.
     */
    public static boolean vencio(LectivoCuota lectivoCuota, OffsetDateTime ahora) {
        return lectivoCuota.getVencimiento1() != null && ahora.isAfter(lectivoCuota.getVencimiento1());
    }

    public ChequeraCuota crear(ChequeraSerie chequeraSerie, LectivoCuota lectivoCuota, int offset,
                               OffsetDateTime ahora) {
        OffsetDateTime vencimiento1 = lectivoCuota.getVencimiento1();
        OffsetDateTime vencimiento2 = lectivoCuota.getVencimiento2();
        OffsetDateTime vencimiento3 = lectivoCuota.getVencimiento3();
        if (vencio(lectivoCuota, ahora)) {
            OffsetDateTime fechaBase = Tool.firstTime(ahora);
            vencimiento1 = fechaBase.plusDays(7 + 30L * offset);
            vencimiento2 = fechaBase.plusDays(20 + 30L * offset);
            vencimiento3 = fechaBase.plusDays(40 + 30L * offset);
        }

        BigDecimal beneficio = normalizarBeneficio(chequeraSerie);
        ChequeraCuota cuota = ChequeraCuota.builder()
                .chequeraId(chequeraSerie.getChequeraId())
                .facultadId(chequeraSerie.getFacultadId())
                .tipoChequeraId(chequeraSerie.getTipoChequeraId())
                .chequeraSerieId(chequeraSerie.getChequeraSerieId())
                .productoId(lectivoCuota.getProductoId())
                .alternativaId(lectivoCuota.getAlternativaId())
                .cuotaId(lectivoCuota.getCuotaId())
                .mes(lectivoCuota.getMes())
                .anho(lectivoCuota.getAnho())
                .arancelTipoId(chequeraSerie.getArancelTipoId())
                .vencimiento1(vencimiento1)
                .importe1(aplicarBeneficio(lectivoCuota.getImporte1(), beneficio, chequeraSerie, lectivoCuota, 1))
                .importe1Original(lectivoCuota.getImporte1())
                .vencimiento2(vencimiento2)
                .importe2(aplicarBeneficio(lectivoCuota.getImporte2(), beneficio, chequeraSerie, lectivoCuota, 2))
                .importe2Original(lectivoCuota.getImporte2())
                .vencimiento3(vencimiento3)
                .importe3(aplicarBeneficio(lectivoCuota.getImporte3(), beneficio, chequeraSerie, lectivoCuota, 3))
                .importe3Original(lectivoCuota.getImporte3())
                .codigoBarras("")
                .i2Of5("")
                .pagado((byte) 0)
                .baja((byte) 0)
                .manual((byte) 0)
                .compensada((byte) 0)
                .tramoId(0)
                .build();
        cuota.setCodigoBarras(calcularCodigoBarras(cuota, chequeraSerie, lectivoCuota));
        return cuota;
    }

    /**
     * El importe de lista nulo se conserva nulo: no se lo reemplaza por cero, porque cero es un
     * importe válido y encubriría el dato faltante. Sin importe no hay factor que aplicar, así que
     * el tramo queda igual que en {@code lectivo_cuota} y la emisión sigue adelante.
     */
    private BigDecimal aplicarBeneficio(BigDecimal importeLista, BigDecimal beneficio,
                                        ChequeraSerie chequeraSerie, LectivoCuota lectivoCuota, int tramo) {
        if (importeLista == null) {
            log.warn("Importe de lista nulo; se conserva nulo y no se aplica beneficio. {} tramo={} beneficio={}",
                    contexto(chequeraSerie, lectivoCuota), tramo, beneficio);
            return null;
        }
        if (beneficio.compareTo(BigDecimal.ZERO) == 0) {
            return importeLista;
        }
        return importeLista.multiply(BigDecimal.ONE.subtract(beneficio))
                .setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * {@code becaPorcentaje} se persiste como fracción: {@code 0.50} representa 50 % y
     * {@code 1.00}, 100 %. La defensa evita que un dato histórico fuera de esa escala genere
     * importes negativos o una bonificación distinta de la configurada.
     */
    private BigDecimal normalizarBeneficio(ChequeraSerie chequeraSerie) {
        BigDecimal beneficio = chequeraSerie.getBecaPorcentaje();
        if (beneficio == null) {
            return BigDecimal.ZERO;
        }
        if (beneficio.compareTo(BigDecimal.ZERO) < 0 || beneficio.compareTo(BigDecimal.ONE) > 0) {
            log.warn("Beneficio fuera de la escala fraccional [0,1]; se emite con 0%. chequeraSerieId={} beneficio={}",
                    chequeraSerie.getChequeraSerieId(), beneficio);
            return BigDecimal.ZERO;
        }
        return beneficio;
    }

    /**
     * {@code calculateCodigoBarras} formatea importes y desreferencia vencimientos sin control de
     * nulo. Con datos incompletos la cuota queda sin código de barras en vez de abortar la emisión
     * de toda la chequera; tesorería lo corrige a mano, que es el camino que ya usa.
     */
    private String calcularCodigoBarras(ChequeraCuota cuota, ChequeraSerie chequeraSerie,
                                        LectivoCuota lectivoCuota) {
        if (cuota.getImporte1() == null || cuota.getImporte2() == null || cuota.getImporte3() == null
                || cuota.getVencimiento1() == null || cuota.getVencimiento2() == null
                || cuota.getVencimiento3() == null) {
            log.warn("Código de barras no calculado por importes o vencimientos nulos; la chequera se emite igual. {}",
                    contexto(chequeraSerie, lectivoCuota));
            return "";
        }
        return chequeraCuotaService.calculateCodigoBarras(cuota);
    }

    private String contexto(ChequeraSerie chequeraSerie, LectivoCuota lectivoCuota) {
        return "facultadId=" + chequeraSerie.getFacultadId()
                + " tipoChequeraId=" + chequeraSerie.getTipoChequeraId()
                + " chequeraSerieId=" + chequeraSerie.getChequeraSerieId()
                + " productoId=" + lectivoCuota.getProductoId()
                + " alternativaId=" + lectivoCuota.getAlternativaId()
                + " cuotaId=" + lectivoCuota.getCuotaId();
    }
}
