package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.ChequeraSerieControlException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.PreuniversitarioChequeraData;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.CreatePreuniversitarioChequeraUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.service.GuaraniBeneficioService;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.RequisitoPresentadoGuarani;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.policy.BeneficioPolicy;
import um.tesoreria.core.model.ChequeraSerieControl;
import um.tesoreria.core.service.ChequeraSerieControlService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class PreuniversitarioChequeraService implements CreatePreuniversitarioChequeraUseCase {

    private final PreuniversitarioDataResolver dataResolver;
    private final PreuniversitarioLegajoManager legajoManager;
    private final ChequeraSerieService chequeraSerieService;
    private final ChequeraSerieControlService chequeraSerieControlService;
    private final PreuniversitarioChequeraDetailsCreator detailsCreator;
    private final PreuniversitarioChequeraPolicy policy;
    private final GuaraniBeneficioService guaraniBeneficioService;
    private final BeneficioPolicy beneficioPolicy;

    @Transactional
    @Override
    public ChequeraSerie create(PreuniversitarioChequeraData alumnoGuaraniFull) {
        log.debug("Processing PreuniversitarioChequeraService.create");
        var context = dataResolver.resolve(alumnoGuaraniFull);
        if (context.isEmpty()) {
            return null;
        }

        var data = context.get();
        var legajo = legajoManager.findOrCreate(data);
        log.debug("Legajo -> {}", legajo.jsonify());

        // Determina si la chequera ya existe
        try {
            return chequeraSerieService.findPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(data.personaId(), data.documentoId(), data.facultadId(), data.lectivoId(), data.geograficaId());
        } catch (ChequeraSerieException e) {
            log.error(e.getMessage());
        }

        // Determina beneficio
        var becaPorcentaje = resolverBeneficio(alumnoGuaraniFull);

        long chequeraSerieId = nextChequeraSerieId(data);
        var control = chequeraSerieControlService.add(new ChequeraSerieControl(
                null, data.facultadId(), data.tipoChequeraId(), chequeraSerieId,
                (byte) 0, 0, 0, 0L, (byte) 0));
        log.debug("ChequeraSerieControl -> {}", control.jsonify());

        var chequeraSerie = chequeraSerieService.add(policy.createSerie(data, control, becaPorcentaje));
        chequeraSerie.setJustCreated(true);
        log.debug("ChequeraSerie -> {}", chequeraSerie.jsonify());

        detailsCreator.create(chequeraSerie);
        return chequeraSerie;
    }

    private long nextChequeraSerieId(PreuniversitarioChequeraContext context) {
        try {
            return chequeraSerieControlService
                    .findLastByTipoChequera(context.facultadId(), context.tipoChequeraId())
                    .getChequeraSerieId() + 1L;
        } catch (ChequeraSerieControlException exception) {
            log.error("Error al obtener el último chequeraSerieControl -> {}", exception.getMessage());
            return 1L;
        }
    }

    private BigDecimal resolverBeneficio(PreuniversitarioChequeraData data) {
        if (data.requisitosPresentados() == null || data.requisitosPresentados().isEmpty()) {
            log.warn("Beneficio no resuelto; se emite con 0%. persona={} documentoId={} causa=requisitos ausentes",
                    data.personaId(), data.documentoId());
            return BigDecimal.ZERO;
        }
        List<Integer> requisitos = data.requisitosPresentados().stream()
                .filter(Objects::nonNull)
                .map(RequisitoPresentadoGuarani::getRequisito)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (requisitos.isEmpty()) {
            log.warn("Beneficio no resuelto; se emite con 0%. persona={} documentoId={} causa=requisitos sin identificador",
                    data.personaId(), data.documentoId());
            return BigDecimal.ZERO;
        }
        try {
            List<GuaraniBeneficio> beneficios = guaraniBeneficioService.findByRequisitos(requisitos);
            if (beneficios == null) {
                log.warn("Beneficio no resuelto; se emite con 0%. persona={} documentoId={} causa=beneficios ausentes",
                        data.personaId(), data.documentoId());
                return BigDecimal.ZERO;
            }
            if (beneficios.stream().filter(Objects::nonNull)
                    .anyMatch(beneficio -> beneficio.getPorcentajeBeneficio() == null)) {
                log.warn("Beneficio incompleto; porcentaje nulo tratado como 0%. persona={} documentoId={}",
                        data.personaId(), data.documentoId());
            }
            return beneficioPolicy.porcentajeEfectivo(data.requisitosPresentados(), beneficios);
        } catch (RuntimeException exception) {
            log.warn("Beneficio no resuelto; se emite con 0%. persona={} documentoId={} causa={}",
                    data.personaId(), data.documentoId(), exception.getMessage());
            return BigDecimal.ZERO;
        }
    }
}
