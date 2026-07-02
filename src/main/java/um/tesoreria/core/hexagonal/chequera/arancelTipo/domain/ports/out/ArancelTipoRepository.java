package um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import java.util.List;
import java.util.Optional;

public interface ArancelTipoRepository {
    List<ArancelTipo> findAll();
    List<ArancelTipo> findAllByHabilitado(Byte habilitado);
    Optional<ArancelTipo> findByArancelTipoId(Integer id);
    Optional<ArancelTipo> findByArancelTipoIdCompleto(Integer id);
    Optional<ArancelTipo> findLast();
    ArancelTipo create(ArancelTipo arancelTipo);
    Optional<ArancelTipo> update(Integer id, ArancelTipo arancelTipo);
    void deleteById(Integer id);
}
