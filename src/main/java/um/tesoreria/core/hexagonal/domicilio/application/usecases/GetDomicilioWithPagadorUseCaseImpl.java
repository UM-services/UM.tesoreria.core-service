package um.tesoreria.core.hexagonal.domicilio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.domicilio.domain.ports.in.GetDomicilioWithPagadorUseCase;
import um.tesoreria.core.hexagonal.domicilio.domain.ports.out.DomicilioRepository;
import um.tesoreria.core.hexagonal.facultad.application.service.FacultadService;
import um.tesoreria.core.extern.consumer.InscripcionFacultadConsumer;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class GetDomicilioWithPagadorUseCaseImpl implements GetDomicilioWithPagadorUseCase {

    private final DomicilioRepository repository;
    private final FacultadService facultadService;
    private final InscripcionFacultadConsumer inscripcionFacultadConsumer;

    @Override
    public Domicilio getDomicilioWithPagador(Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        var facultad = facultadService.findByFacultadId(facultadId);
        var inscripcionFull = inscripcionFacultadConsumer.findInscripcionFull(
                facultad.getApiserver(), facultad.getApiport(),
                facultad.getFacultadId(), personaId, documentoId, lectivoId);
        Domicilio domicilio = repository.findByUnique(personaId, documentoId)
                .orElseThrow(() -> new RuntimeException("Cannot find Domicilio " + personaId + "/" + documentoId));
        if (inscripcionFull.getDomicilioPago() != null) {
            var domicilioPago = inscripcionFull.getDomicilioPago();
            if (!domicilioPago.getEmailPersonal().isEmpty()) {
                domicilio.setEmailPagador(domicilioPago.getEmailPersonal());
            }
        }
        return domicilio;
    }
}
