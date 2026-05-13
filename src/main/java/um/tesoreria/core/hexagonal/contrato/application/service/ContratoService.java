package um.tesoreria.core.hexagonal.contrato.application.service;

import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import um.tesoreria.core.hexagonal.contrato.domain.ports.in.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final CreateContratoUseCase createContratoUseCase;
    private final DeleteContratoUseCase deleteContratoUseCase;
    private final GetAllContratosAjustablesUseCase getAllContratosAjustablesUseCase;
    private final GetAllContratosVigentesUseCase getAllContratosVigentesUseCase;
    private final GetAllContratosByPersonaUseCase getAllContratosByPersonaUseCase;
    private final GetAllContratosByFacultadUseCase getAllContratosByFacultadUseCase;
    private final GetContratoByIdUseCase getContratoByIdUseCase;
    private final UpdateContratoUseCase updateContratoUseCase;
    private final SaveAllContratosUseCase saveAllContratosUseCase;

    public Contrato createContrato(Contrato contrato) {
        return createContratoUseCase.createContrato(contrato);
    }

    public boolean deleteContrato(Long id) {
        return deleteContratoUseCase.deleteContrato(id);
    }

    public List<Contrato> getContratosAjustables(OffsetDateTime referencia) {
        return getAllContratosAjustablesUseCase.getContratosAjustables(referencia);
    }

    public List<Contrato> getContratosVigentes(OffsetDateTime referencia) {
        return getAllContratosVigentesUseCase.getContratosVigentes(referencia);
    }

    public List<Contrato> getContratosByPersona(BigDecimal personaId, Integer documentoId) {
        return getAllContratosByPersonaUseCase.getContratosByPersona(personaId, documentoId);
    }

    public List<Contrato> getContratosByFacultad(Integer facultadId, Integer geograficaId) {
        return getAllContratosByFacultadUseCase.getContratosByFacultad(facultadId, geograficaId);
    }

    public Optional<Contrato> getContratoById(Long id) {
        return getContratoByIdUseCase.getContratoById(id);
    }

    public Optional<Contrato> updateContrato(Long id, Contrato contrato) {
        return updateContratoUseCase.updateContrato(id, contrato);
    }

    public List<Contrato> saveAllContratos(List<Contrato> contratos) {
        return saveAllContratosUseCase.saveAllContratos(contratos);
    }
}
