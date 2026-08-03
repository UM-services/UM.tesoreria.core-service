package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.application.exception.GuaraniPropuestaTipoChequeraException;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.model.GuaraniPropuestaTipoChequera;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.in.CreateGuaraniPropuestaTipoChequeraUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.in.DeleteGuaraniPropuestaTipoChequeraUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.in.GetGuaraniPropuestaTipoChequeraByPropuestaAndLectivoUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.in.UpdateGuaraniPropuestaTipoChequeraUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.ports.out.GuaraniPropuestaTipoChequeraRepository;

@Service
@RequiredArgsConstructor
public class GuaraniPropuestaTipoChequeraService {
    private final GuaraniPropuestaTipoChequeraRepository repository;
    private final CreateGuaraniPropuestaTipoChequeraUseCase createUseCase;
    private final UpdateGuaraniPropuestaTipoChequeraUseCase updateUseCase;
    private final DeleteGuaraniPropuestaTipoChequeraUseCase deleteUseCase;
    private final GetGuaraniPropuestaTipoChequeraByPropuestaAndLectivoUseCase getByPropuestaAndLectivoUseCase;

    public GuaraniPropuestaTipoChequera create(GuaraniPropuestaTipoChequera data) {
        return createUseCase.create(data);
    }

    public GuaraniPropuestaTipoChequera update(GuaraniPropuestaTipoChequera data) {
        if (data.getGuaraniPropuestaTipoChequeraId() == null
                || !repository.existsById(data.getGuaraniPropuestaTipoChequeraId())) {
            throw new GuaraniPropuestaTipoChequeraException(data.getGuaraniPropuestaTipoChequeraId());
        }
        return updateUseCase.update(data);
    }

    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new GuaraniPropuestaTipoChequeraException(id);
        }
        deleteUseCase.delete(id);
    }

    public GuaraniPropuestaTipoChequera findByPropuestaAndLectivo(Integer propuestaGuarani, Integer lectivoId) {
        return getByPropuestaAndLectivoUseCase.getByPropuestaAndLectivo(propuestaGuarani, lectivoId)
                .orElseThrow(() -> new GuaraniPropuestaTipoChequeraException(propuestaGuarani, lectivoId));
    }
}
