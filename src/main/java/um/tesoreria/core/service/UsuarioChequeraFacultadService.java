package um.tesoreria.core.service;

import org.springframework.stereotype.Service;
import um.tesoreria.core.model.UsuarioChequeraFacultad;
import um.tesoreria.core.repository.UsuarioChequeraFacultadRepository;

import java.util.List;

@Service
public class UsuarioChequeraFacultadService {

    private final UsuarioChequeraFacultadRepository repository;

    public UsuarioChequeraFacultadService(UsuarioChequeraFacultadRepository repository) {
        this.repository = repository;
    }

    public List<UsuarioChequeraFacultad> findAllByUserId(Long userId) {
        return repository.findAllByUserId(userId);
    }

}
