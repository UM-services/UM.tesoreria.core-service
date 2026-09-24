package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.TipoImpresionException;
import um.tesoreria.core.model.TipoImpresion;
import um.tesoreria.core.repository.TipoImpresionRepository;

@Service
@RequiredArgsConstructor
public class TipoImpresionService {

    private final TipoImpresionRepository repository;

    public TipoImpresion findByTipoImpresionId(Integer tipoImpresionId) {
        return repository.findByTipoImpresionId(tipoImpresionId)
                .orElseThrow(() -> new TipoImpresionException(tipoImpresionId));
    }

}