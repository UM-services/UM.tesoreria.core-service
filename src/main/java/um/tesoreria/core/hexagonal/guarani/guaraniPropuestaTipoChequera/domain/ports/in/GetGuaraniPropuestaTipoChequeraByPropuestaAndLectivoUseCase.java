package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.in;

import java.util.Optional;

import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.model.GuaraniPropuestaTipoChequera;

public interface GetGuaraniPropuestaTipoChequeraByPropuestaAndLectivoUseCase {
    Optional<GuaraniPropuestaTipoChequera> getByPropuestaAndLectivo(
            Integer propuestaGuarani, Integer lectivoId);
}
