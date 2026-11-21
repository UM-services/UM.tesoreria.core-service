package um.tesoreria.core.hexagonal.chequera.envio.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ChequeraEnvioAlta;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.in.GetChequeraEnvioAltasUseCase;

import java.util.List;

/**
 * Fachada del envío masivo de chequeras. Por ahora expone la lectura del padrón; las operaciones
 * masivas (capturar correos, extender débitos y mailing) se agregan en las fases siguientes.
 */
@Service
@RequiredArgsConstructor
public class EnvioService {

    private final GetChequeraEnvioAltasUseCase getChequeraEnvioAltasUseCase;

    public List<ChequeraEnvioAlta> altas(Integer lectivoId, Integer facultadId, Integer geograficaId) {
        return getChequeraEnvioAltasUseCase.getAltas(lectivoId, facultadId, geograficaId);
    }
}
