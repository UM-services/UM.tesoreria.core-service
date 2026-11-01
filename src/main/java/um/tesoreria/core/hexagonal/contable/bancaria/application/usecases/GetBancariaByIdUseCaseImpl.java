package um.tesoreria.core.hexagonal.contable.bancaria.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaNoEncontradaException;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.GetBancariaByIdUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaRepository;

@Component
@RequiredArgsConstructor
public class GetBancariaByIdUseCaseImpl implements GetBancariaByIdUseCase {
    private final BancariaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Bancaria getBancariaById(Long bancariaId) {
        return repository.findByBancariaId(bancariaId).orElseThrow(() -> new BancariaNoEncontradaException(bancariaId));
    }
}
