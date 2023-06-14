package um.tesoreria.rest.service.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.tesoreria.rest.kotlin.model.view.TipoChequeraFacultadLectivoGeograficaCantidad;
import um.tesoreria.rest.kotlin.repository.view.TipoChequeraFacultadLectivoGeograficaCantidadRepository;

import java.util.List;

@Service
public class TipoChequeraFacultadLectivoGeograficaCantidadService {

    @Autowired
    private TipoChequeraFacultadLectivoGeograficaCantidadRepository repository;

    public List<TipoChequeraFacultadLectivoGeograficaCantidad> findAllBySede(Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return repository.findAllByFacultadIdAndLectivoIdAndGeograficaId(facultadId, lectivoId, geograficaId);
    }

}
