package um.tesoreria.core.hexagonal.chequera.envio.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ChequeraEnvioAlta;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.in.GetChequeraEnvioAltasUseCase;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.ChequeraEnvioAltaReader;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetChequeraEnvioAltasUseCaseImpl implements GetChequeraEnvioAltasUseCase {

    private final ChequeraEnvioAltaReader reader;

    @Override
    public List<ChequeraEnvioAlta> getAltas(Integer lectivoId, Integer facultadId, Integer geograficaId) {
        log.debug("Processing GetChequeraEnvioAltasUseCaseImpl.getAltas {}/{}/{}",
                lectivoId, facultadId, geograficaId);
        return reader.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId);
    }
}
