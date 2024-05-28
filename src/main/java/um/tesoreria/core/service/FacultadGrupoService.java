package um.tesoreria.core.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.FacultadGrupo;
import um.tesoreria.core.kotlin.repository.FacultadGrupoRepository;

import java.util.List;

@Service
public class FacultadGrupoService {

    private final FacultadGrupoRepository repository;

    @Autowired
    public FacultadGrupoService(FacultadGrupoRepository repository) {
        this.repository = repository;
    }

    public List<FacultadGrupo> findAllByGrupo(Integer grupo) {
        return repository.findAllByGrupo(grupo);
    }

}
