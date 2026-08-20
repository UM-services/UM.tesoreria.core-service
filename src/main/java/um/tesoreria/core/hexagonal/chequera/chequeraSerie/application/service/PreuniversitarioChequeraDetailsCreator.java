package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.factory.ChequeraCuotaFactory;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.kotlin.model.ChequeraAlternativa;
import um.tesoreria.core.kotlin.model.LectivoAlternativa;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.model.LectivoTotal;
import um.tesoreria.core.service.ChequeraAlternativaService;
import um.tesoreria.core.service.LectivoAlternativaService;
import um.tesoreria.core.service.LectivoTotalService;
import um.tesoreria.core.util.Jsonifier;
import um.tesoreria.core.util.Tool;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
class PreuniversitarioChequeraDetailsCreator {

    private final LectivoTotalService lectivoTotalService;
    private final ChequeraTotalService chequeraTotalService;
    private final LectivoAlternativaService lectivoAlternativaService;
    private final ChequeraAlternativaService chequeraAlternativaService;
    private final LectivoCuotaService lectivoCuotaService;
    private final ChequeraCuotaFactory chequeraCuotaFactory;
    private final ChequeraCuotaService chequeraCuotaService;

    void create(ChequeraSerie chequeraSerie) {
        createAlternatives(chequeraSerie);
        var cuotas = createCuotas(chequeraSerie);
        createTotals(chequeraSerie, cuotas);
    }

    private void createTotals(ChequeraSerie chequeraSerie, List<ChequeraCuota> cuotas) {
        Map<Integer, List<ChequeraCuota>> activasPorProducto = cuotas.stream()
                .filter(Objects::nonNull)
                .filter(cuota -> cuota.getProductoId() != null)
                .filter(cuota -> Byte.valueOf((byte) 0).equals(cuota.getBaja()))
                .collect(Collectors.groupingBy(ChequeraCuota::getProductoId));
        List<ChequeraTotal> totals = new ArrayList<>();
        for (Integer productoId : productos(chequeraSerie, activasPorProducto.keySet())) {
            totals.add(ChequeraTotal.builder()
                    .facultadId(chequeraSerie.getFacultadId())
                    .tipoChequeraId(chequeraSerie.getTipoChequeraId())
                    .chequeraSerieId(chequeraSerie.getChequeraSerieId())
                    .productoId(productoId)
                    .total(sumarActivas(chequeraSerie, productoId,
                            activasPorProducto.getOrDefault(productoId, List.of())))
                    .pagado(BigDecimal.ZERO)
                    .build());
        }
        log.debug("ChequeraTotals -> {}", Jsonifier.builder(chequeraTotalService.saveAll(totals)).build());
    }

    /**
     * Los productos salen de {@code lectivo_total}, que no filtra por alternativa, unidos a los que
     * efectivamente generaron cuotas. Derivarlos solo de las cuotas dejaría sin fila de
     * {@code chequera_total} a un producto con total configurado pero sin cuotas en la alternativa
     * elegida; esa fila se persiste igual, en cero.
     */
    private Collection<Integer> productos(ChequeraSerie chequeraSerie, Set<Integer> conCuotas) {
        Set<Integer> productos = new LinkedHashSet<>();
        for (LectivoTotal source : lectivoTotalService.findAllByTipo(
                chequeraSerie.getFacultadId(), chequeraSerie.getLectivoId(), chequeraSerie.getTipoChequeraId())) {
            if (source.getProductoId() != null) {
                productos.add(source.getProductoId());
            }
        }
        productos.addAll(conCuotas);
        return productos;
    }

    /**
     * Mismo invariante que {@code CalculateTotalCuotasActivasUseCaseImpl}: suma de {@code importe1}
     * de las cuotas con {@code baja = 0}. Un importe nulo se excluye de la suma y se loguea, pero no
     * aborta la emisión ni se confunde con un cero legítimo del beneficio del 100 %.
     */
    private BigDecimal sumarActivas(ChequeraSerie chequeraSerie, Integer productoId, List<ChequeraCuota> cuotas) {
        BigDecimal total = BigDecimal.ZERO;
        for (ChequeraCuota cuota : cuotas) {
            if (cuota.getImporte1() == null) {
                log.warn("Importe1 nulo excluido del total; la chequera se emite igual. facultadId={} "
                                + "tipoChequeraId={} chequeraSerieId={} productoId={} cuotaId={}",
                        chequeraSerie.getFacultadId(), chequeraSerie.getTipoChequeraId(),
                        chequeraSerie.getChequeraSerieId(), productoId, cuota.getCuotaId());
                continue;
            }
            total = total.add(cuota.getImporte1());
        }
        return total;
    }

    private void createAlternatives(ChequeraSerie chequeraSerie) {
        List<ChequeraAlternativa> alternatives = new ArrayList<>();
        for (LectivoAlternativa source : lectivoAlternativaService.findAllByTipo(
                chequeraSerie.getFacultadId(), chequeraSerie.getLectivoId(), chequeraSerie.getTipoChequeraId(),
                chequeraSerie.getAlternativaId())) {
            alternatives.add(new ChequeraAlternativa(null, chequeraSerie.getFacultadId(),
                    chequeraSerie.getTipoChequeraId(), chequeraSerie.getChequeraSerieId(), source.getProductoId(),
                    source.getAlternativaId(), Objects.requireNonNull(source.getTitulo()),
                    Objects.requireNonNull(source.getCuotas())));
        }
        log.debug("ChequeraAlternativas -> {}", Jsonifier.builder(chequeraAlternativaService.saveAll(alternatives)).build());
    }

    private List<ChequeraCuota> createCuotas(ChequeraSerie chequeraSerie) {
        List<ChequeraCuota> cuotas = new ArrayList<>();
        int offset = 0;
        OffsetDateTime ahora = Tool.hourAbsoluteArgentina();
        for (LectivoCuota source : lectivoCuotaService.findAllByTipo(
                chequeraSerie.getFacultadId(), chequeraSerie.getLectivoId(), chequeraSerie.getTipoChequeraId(),
                chequeraSerie.getAlternativaId())) {
            ChequeraCuota cuota = chequeraCuotaFactory.crear(chequeraSerie, source, offset, ahora);
            if (ChequeraCuotaFactory.vencio(source, ahora)) {
                offset++;
            }
            log.debug("chequera_cuota -> {}", cuota.jsonify());
            cuotas.add(cuota);
        }
        List<ChequeraCuota> cuotasGuardadas = chequeraCuotaService.saveAll(cuotas);
        log.debug("ChequeraCuotas -> {}", Jsonifier.builder(cuotasGuardadas).build());
        return cuotasGuardadas;
    }
}
