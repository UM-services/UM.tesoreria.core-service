package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
class PreuniversitarioChequeraDetailsCreator {

    private final LectivoTotalService lectivoTotalService;
    private final ChequeraTotalService chequeraTotalService;
    private final LectivoAlternativaService lectivoAlternativaService;
    private final ChequeraAlternativaService chequeraAlternativaService;
    private final LectivoCuotaService lectivoCuotaService;
    private final ChequeraCuotaService chequeraCuotaService;

    void create(ChequeraSerie chequeraSerie) {
        createTotals(chequeraSerie);
        createAlternatives(chequeraSerie);
        createCuotas(chequeraSerie);
    }

    private void createTotals(ChequeraSerie chequeraSerie) {
        List<ChequeraTotal> totals = new ArrayList<>();
        for (LectivoTotal source : lectivoTotalService.findAllByTipo(
                chequeraSerie.getFacultadId(), chequeraSerie.getLectivoId(), chequeraSerie.getTipoChequeraId())) {
            totals.add(ChequeraTotal.builder()
                    .facultadId(chequeraSerie.getFacultadId())
                    .tipoChequeraId(chequeraSerie.getTipoChequeraId())
                    .chequeraSerieId(chequeraSerie.getChequeraSerieId())
                    .productoId(source.getProductoId())
                    .total(source.getTotal())
                    .pagado(BigDecimal.ZERO)
                    .build());
        }
        log.debug("ChequeraTotals -> {}", Jsonifier.builder(chequeraTotalService.saveAll(totals)).build());
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

    private void createCuotas(ChequeraSerie chequeraSerie) {
        List<ChequeraCuota> cuotas = new ArrayList<>();
        int offset = 0;
        for (LectivoCuota source : lectivoCuotaService.findAllByTipo(
                chequeraSerie.getFacultadId(), chequeraSerie.getLectivoId(), chequeraSerie.getTipoChequeraId(),
                chequeraSerie.getAlternativaId())) {
            OffsetDateTime vencimiento1 = source.getVencimiento1();
            OffsetDateTime vencimiento2 = source.getVencimiento2();
            OffsetDateTime vencimiento3 = source.getVencimiento3();
            if (OffsetDateTime.now().isAfter(vencimiento1)) {
                vencimiento1 = Tool.dateAbsoluteArgentina().plusDays(7 + 30L * offset);
                vencimiento2 = Tool.dateAbsoluteArgentina().plusDays(20 + 30L * offset);
                vencimiento3 = Tool.dateAbsoluteArgentina().plusDays(40 + 30L * offset);
                offset++;
            }
            ChequeraCuota cuota = new ChequeraCuota(null, chequeraSerie.getChequeraId(),
                    chequeraSerie.getFacultadId(), chequeraSerie.getTipoChequeraId(),
                    chequeraSerie.getChequeraSerieId(), source.getProductoId(), source.getAlternativaId(),
                    source.getCuotaId(), source.getMes(), source.getAnho(), chequeraSerie.getArancelTipoId(),
                    vencimiento1, source.getImporte1(), source.getImporte1(), vencimiento2, source.getImporte2(),
                    source.getImporte2(), vencimiento3, source.getImporte3(), source.getImporte3(), "", "",
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, 0, null, null, null, null);
            log.debug("chequera_cuota -> {}", cuota.jsonify());
            cuota.setCodigoBarras(chequeraCuotaService.calculateCodigoBarras(cuota));
            cuotas.add(cuota);
        }
        log.debug("ChequeraCuotas -> {}", Jsonifier.builder(chequeraCuotaService.saveAll(cuotas)).build());
    }
}
