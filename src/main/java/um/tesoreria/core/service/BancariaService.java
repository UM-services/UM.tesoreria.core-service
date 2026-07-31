package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.exception.BancariaException;
import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.Bancaria;
import um.tesoreria.core.kotlin.repository.BancariaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BancariaService {

    private final BancariaRepository repository;

    public List<Bancaria> findAll() {
        return repository.findAll();
    }

    public Bancaria findByBancariaId(Long bancariaId) {
        return repository.findByBancariaId(bancariaId).orElseThrow(() -> new BancariaException(bancariaId));
    }
}
