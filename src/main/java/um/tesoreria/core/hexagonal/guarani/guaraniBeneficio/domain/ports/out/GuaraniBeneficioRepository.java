package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.out;

import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;

import java.util.List;
import java.util.Optional;

public interface GuaraniBeneficioRepository {
    List<GuaraniBeneficio> findAll();
    Optional<GuaraniBeneficio> findByRequisito(Integer requisito);
    GuaraniBeneficio save(GuaraniBeneficio guaraniBeneficio);
}
