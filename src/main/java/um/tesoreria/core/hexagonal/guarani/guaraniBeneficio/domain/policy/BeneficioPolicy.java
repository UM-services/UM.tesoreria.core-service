package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.policy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.RequisitoPresentadoGuarani;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BeneficioPolicy {

    public BigDecimal porcentajeEfectivo(List<RequisitoPresentadoGuarani> requisitos,
                                         List<GuaraniBeneficio> beneficios) {
        if (requisitos == null || beneficios == null) {
            return BigDecimal.ZERO;
        }

        Map<Integer, List<GuaraniBeneficio>> beneficiosPorRequisito = beneficios.stream()
                .filter(Objects::nonNull)
                .filter(beneficio -> beneficio.getRequisito() != null)
                .collect(Collectors.groupingBy(GuaraniBeneficio::getRequisito));

        return requisitos.stream()
                .filter(Objects::nonNull)
                .filter(this::esRequisitoElegible)
                .map(RequisitoPresentadoGuarani::getRequisito)
                .filter(Objects::nonNull)
                .map(beneficiosPorRequisito::get)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(GuaraniBeneficio::getPorcentajeBeneficio)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private boolean esRequisitoElegible(RequisitoPresentadoGuarani requisito) {
        if (requisito.getRequisitoRel() == null) {
            log.warn("Beneficio omitido: requisitoRel nulo para persona={} requisito={}",
                    requisito.getPersona(), requisito.getRequisito());
            return false;
        }

        return "S".equalsIgnoreCase(requisito.getRequisitoRel().getRequisitoIngreso())
                && "S".equalsIgnoreCase(requisito.getRequisitoRel().getActivo());
    }
}
