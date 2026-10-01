package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.DebitoTipoException;
import um.tesoreria.core.kotlin.model.DebitoTipo;
import um.tesoreria.core.repository.DebitoTipoRepository;

@Service
@RequiredArgsConstructor
public class DebitoTipoService {

    private final DebitoTipoRepository repository;

    public DebitoTipo findByDebitoTipoId(Integer debitoTipoId) {
        return repository.findByDebitoTipoId(debitoTipoId)
                .orElseThrow(() -> new DebitoTipoException(debitoTipoId));
    }

}