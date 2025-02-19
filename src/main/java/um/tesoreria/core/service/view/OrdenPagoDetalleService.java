package um.tesoreria.core.service.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.view.OrdenPagoDetalle;
import um.tesoreria.core.repository.view.OrdenPagoDetalleRepository;

import java.util.List;

@Service
public class OrdenPagoDetalleService {

    @Autowired
    private OrdenPagoDetalleRepository repository;

    public List<OrdenPagoDetalle> findAllByStrings(List<String> conditions) {
        return repository.findAllByStrings(conditions);
    }
}
