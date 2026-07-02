package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.CalculateTotalCuotasPagadasUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CalculateTotalCuotasPagadasUseCaseImpl implements CalculateTotalCuotasPagadasUseCase {

    private final ChequeraCuotaRepository repository;

    @Override
    public BigDecimal calculateTotalCuotasPagadas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId) {
        return repository.findAllByCuotasPagadas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, (byte) 1)
                .stream()
                .map(ChequeraCuota::getImporte1)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
