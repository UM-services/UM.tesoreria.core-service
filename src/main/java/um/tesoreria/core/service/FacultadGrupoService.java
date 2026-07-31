package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.FacultadGrupo;
import um.tesoreria.core.kotlin.repository.FacultadGrupoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacultadGrupoService {

    private final FacultadGrupoRepository repository;


    public List<FacultadGrupo> findAllByGrupo(Integer grupo) {
        return repository.findAllByGrupo(grupo);
    }

}
