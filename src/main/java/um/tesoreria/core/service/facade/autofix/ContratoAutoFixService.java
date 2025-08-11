package um.tesoreria.core.service.facade.autofix;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.model.Contrato;
import um.tesoreria.core.service.ContratadoService;
import um.tesoreria.core.service.ContratoPeriodoService;
import um.tesoreria.core.service.ContratoService;
import um.tesoreria.core.service.PersonaService;

@Service
@Slf4j
public class ContratoAutoFixService {

    private final ContratoService contratoService;
    private final PersonaService personaService;
    private final ContratadoService contratadoService;
    private final ContratoPeriodoService contratoPeriodoService;

    public ContratoAutoFixService(ContratoService contratoService,
                                  PersonaService personaService,
                                  ContratadoService contratadoService, ContratoPeriodoService contratoPeriodoService) {
        this.contratoService = contratoService;
        this.personaService = personaService;
        this.contratadoService = contratadoService;
        this.contratoPeriodoService = contratoPeriodoService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Contrato fixContratadoId(Long contratoId) {
        var contrato = contratoService.findByContratoId(contratoId);
        var persona = personaService.findByUnique(contrato.getPersonaId(), contrato.getDocumentoId());
        var contratado = contratadoService.findByPersona(persona.getUniqueId());
        contrato.setContratadoId(contratado.getContratadoId());
        contratoService.update(contrato, contratoId);
        return contratoService.findByContratoId(contratoId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetMarcaTemporal(Long contratoId) {
        var contratoPeriodos = contratoPeriodoService.findAllByContrato(contratoId);
        contratoPeriodoService.findAllByContrato(contratoId).forEach(c -> c.setMarcaTemporal((byte) 0));
        contratoPeriodoService.saveAll(contratoPeriodos);
    }

}
