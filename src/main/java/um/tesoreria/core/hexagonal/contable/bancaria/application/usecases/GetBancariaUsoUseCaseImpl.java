package um.tesoreria.core.hexagonal.contable.bancaria.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaNoEncontradaException;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaUso;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.GetBancariaUsoUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaRepository;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaUsoPort;

@Component
@RequiredArgsConstructor
public class GetBancariaUsoUseCaseImpl implements GetBancariaUsoUseCase {
    private final BancariaRepository repository;
    private final BancariaUsoPort uso;

    @Override
    @Transactional(readOnly = true)
    public BancariaUso getBancariaUso(Long bancariaId) {
        if (repository.findByBancariaId(bancariaId).isEmpty()) {
            throw new BancariaNoEncontradaException(bancariaId);
        }
        return uso.contar(bancariaId);
    }
}
