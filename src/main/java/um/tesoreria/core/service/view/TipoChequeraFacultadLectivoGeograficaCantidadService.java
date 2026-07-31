package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.view.TipoChequeraFacultadLectivoGeograficaCantidad;
import um.tesoreria.core.kotlin.repository.view.TipoChequeraFacultadLectivoGeograficaCantidadRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoChequeraFacultadLectivoGeograficaCantidadService {

    private final TipoChequeraFacultadLectivoGeograficaCantidadRepository repository;

    public List<TipoChequeraFacultadLectivoGeograficaCantidad> findAllBySede(Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return repository.findAllByFacultadIdAndLectivoIdAndGeograficaId(facultadId, lectivoId, geograficaId);
    }

}
