package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindAllInconsistenciasUseCaseImplTest {

    @Test
    void findAllInconsistencias_acceptsBonifiedAndHundredPercentCuotas() {
        var repository = mock(ChequeraCuotaRepository.class);
        var parcial = cuota(new BigDecimal("70"), new BigDecimal("140"), new BigDecimal("210"),
                new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"));
        var completa = cuota(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"));
        when(repository.findAllByVencimiento1Between(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(parcial, completa));

        assertThat(new FindAllInconsistenciasUseCaseImpl(repository)
                .findAllInconsistencias(OffsetDateTime.now().minusDays(1), OffsetDateTime.now().plusDays(1), false)).isEmpty();
    }

    @Test
    void findAllInconsistencias_reportsHistoricalNullFieldsInsteadOfThrowing() {
        var repository = mock(ChequeraCuotaRepository.class);
        var cuota = cuota(new BigDecimal("70"), new BigDecimal("140"), new BigDecimal("210"),
                null, new BigDecimal("200"), new BigDecimal("300"));
        when(repository.findAllByVencimiento1Between(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(cuota));

        assertThat(new FindAllInconsistenciasUseCaseImpl(repository)
                .findAllInconsistencias(OffsetDateTime.now().minusDays(1), OffsetDateTime.now().plusDays(1), false)).containsExactly(cuota);
    }

    @Test
    void findAllInconsistencias_reportsAQuotaWhenOnlyOneTramoBreaksTheOrder() {
        var repository = mock(ChequeraCuotaRepository.class);
        var cuota = cuota(new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("50"),
                new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"));
        when(repository.findAllByVencimiento1Between(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(cuota));

        assertThat(new FindAllInconsistenciasUseCaseImpl(repository)
                .findAllInconsistencias(OffsetDateTime.now().minusDays(1), OffsetDateTime.now().plusDays(1), false))
                .containsExactly(cuota);
    }

    @Test
    void findAllInconsistencias_acceptsZeroBenefitAndMaximumBenefitWithinMultiplierMargin() {
        var repository = mock(ChequeraCuotaRepository.class);
        var sinBeneficio = cuota(new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"),
                new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"));
        var maximo = cuota(new BigDecimal("3"), new BigDecimal("6"), new BigDecimal("9"),
                new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300"));
        when(repository.findAllByVencimiento1Between(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(sinBeneficio, maximo));

        assertThat(new FindAllInconsistenciasUseCaseImpl(repository)
                .findAllInconsistencias(OffsetDateTime.now().minusDays(1), OffsetDateTime.now().plusDays(1), false)).isEmpty();
    }

    private static ChequeraCuota cuota(BigDecimal importe1, BigDecimal importe2, BigDecimal importe3,
                                       BigDecimal original1, BigDecimal original2, BigDecimal original3) {
        var now = OffsetDateTime.now();
        return ChequeraCuota.builder().vencimiento1(now).vencimiento2(now.plusDays(1)).vencimiento3(now.plusDays(2))
                .importe1(importe1).importe2(importe2).importe3(importe3)
                .importe1Original(original1).importe2Original(original2).importe3Original(original3).build();
    }
}
