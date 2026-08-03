package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.out;

import java.util.Optional;

import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.model.GuaraniPropuestaTipoChequera;

public interface GuaraniPropuestaTipoChequeraRepository {
    Optional<GuaraniPropuestaTipoChequera> findById(Integer id);

    Optional<GuaraniPropuestaTipoChequera> findByPropuestaGuaraniAndLectivoId(
            Integer propuestaGuarani, Integer lectivoId);

    GuaraniPropuestaTipoChequera save(GuaraniPropuestaTipoChequera guaraniPropuestaTipoChequera);

    boolean existsById(Integer id);

    void deleteById(Integer id);
}
