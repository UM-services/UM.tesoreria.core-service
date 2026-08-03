package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.model.GuaraniPropuestaTipoChequera;

public interface CreateGuaraniPropuestaTipoChequeraUseCase {
    GuaraniPropuestaTipoChequera create(GuaraniPropuestaTipoChequera data);
}
