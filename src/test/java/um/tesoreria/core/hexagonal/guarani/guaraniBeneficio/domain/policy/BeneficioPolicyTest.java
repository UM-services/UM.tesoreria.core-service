package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.policy;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.RequisitoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.RequisitoPresentadoGuarani;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BeneficioPolicyTest {

    private final BeneficioPolicy policy = new BeneficioPolicy();

    @Test
    void porcentajeEfectivo_usesMaximumOnlyAmongActiveIngresoRequirements() {
        var requisitos = List.of(requisito(1, "S", "S"), requisito(2, "s", "S"),
                requisito(3, "N", "S"), requisito(4, "S", "N"));
        var beneficios = List.of(beneficio(1, "0.10"), beneficio(2, "0.30"), beneficio(3, "0.90"), beneficio(4, "0.80"));

        assertThat(policy.porcentajeEfectivo(requisitos, beneficios)).isEqualByComparingTo("0.30");
    }

    @Test
    void porcentajeEfectivo_isNullSafeAndIgnoresLegacyNullPercentages() {
        assertThat(policy.porcentajeEfectivo(null, List.of(beneficio(1, "0.50"))))
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(policy.porcentajeEfectivo(List.of(requisito(1, "S", "S")), null))
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(policy.porcentajeEfectivo(List.of(requisito(1, "S", "S")),
                List.of(beneficio(1, null)))).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void porcentajeEfectivo_ignoresNullRelationsAndDoesNotCountUnpresentedBenefits() {
        var sinRelacion = RequisitoPresentadoGuarani.builder().persona(123).requisito(1).build();

        assertThat(policy.porcentajeEfectivo(List.of(sinRelacion, requisito(2, "S", "S")),
                List.of(beneficio(1, "1"), beneficio(2, "0.20"), beneficio(3, "0.99"))))
                .isEqualByComparingTo("0.20");
    }

    @Test
    void porcentajeEfectivo_ignoresRequirementWhenRelationPointsToAnotherId() {
        var inconsistente = requisito(1, "S", "S");
        inconsistente.getRequisitoRel().setRequisito(2);

        assertThat(policy.porcentajeEfectivo(List.of(inconsistente), List.of(beneficio(1, "1.00"))))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void porcentajeEfectivo_isIdempotentForRepeatedEligibleRequirement() {
        var requisito = requisito(1, "S", "S");

        assertThat(policy.porcentajeEfectivo(List.of(requisito, requisito), List.of(beneficio(1, "0.20"))))
                .isEqualByComparingTo("0.20");
    }

    @Test
    void porcentajeEfectivo_returnsZeroForEmptyOrUnconfiguredRequirements() {
        assertThat(policy.porcentajeEfectivo(List.of(), List.of())).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(policy.porcentajeEfectivo(List.of(requisito(1, "S", "S")), List.of(beneficio(2, "0.20"))))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void porcentajeEfectivo_ignoresBenefitsOutsideTheFractionalScale() {
        assertThat(policy.porcentajeEfectivo(List.of(requisito(1, "S", "S")),
                List.of(beneficio(1, "50"), beneficio(1, "-0.10"))))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    private static RequisitoPresentadoGuarani requisito(Integer id, String ingreso, String activo) {
        return RequisitoPresentadoGuarani.builder().persona(123).requisito(id)
                .requisitoRel(RequisitoGuarani.builder().requisito(id).requisitoIngreso(ingreso).activo(activo).build())
                .build();
    }

    private static GuaraniBeneficio beneficio(Integer requisito, String porcentaje) {
        return GuaraniBeneficio.builder().requisito(requisito)
                .porcentajeBeneficio(porcentaje == null ? null : new BigDecimal(porcentaje)).build();
    }
}
