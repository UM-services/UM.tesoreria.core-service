package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.view.OrdenPagoDetalle;
import um.tesoreria.core.repository.view.OrdenPagoDetalleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenPagoDetalleService {

    private final OrdenPagoDetalleRepository repository;

    public List<OrdenPagoDetalle> findAllByStrings(List<String> conditions) {
        return repository.findAllByStrings(conditions);
    }
}
