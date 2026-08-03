package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.model.GuaraniPropuestaTipoChequera;

public interface UpdateGuaraniPropuestaTipoChequeraUseCase {
    GuaraniPropuestaTipoChequera update(GuaraniPropuestaTipoChequera data);
}
