package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetChequeraSerieByPersonaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;
import um.tesoreria.core.service.ChequeraImpresionCabeceraService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GetChequeraSerieByPersonaUseCaseImpl implements GetChequeraSerieByPersonaUseCase {

    private final ChequeraSerieRepository repository;
    private final CalculateDeudaUseCase calculateDeudaUseCase;
    private final ChequeraImpresionCabeceraService chequeraImpresionCabeceraService;

    @Override
    public List<ChequeraSerie> getAllByPersona(BigDecimal personaId, Integer documentoId) {
        return repository.findAllByPersonaIdAndDocumentoId(personaId, documentoId, Sort.by("lectivoId").descending());
    }

    @Override
    public List<ChequeraSerie> getAllByPersonaExtended(BigDecimal personaId, Integer documentoId) {
        List<ChequeraSerie> chequeras = repository.findAllByPersonaIdAndDocumentoId(personaId, documentoId, Sort.by("lectivoId").descending());
        for (ChequeraSerie chequera : chequeras) {
            setDeuda(chequera);
        }
        return chequeras;
    }

    @Override
    public List<ChequeraSerie> getAllByPersonaLectivo(BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        List<ChequeraSerie> chequeras = repository.findAllByPersonaIdAndDocumentoIdAndLectivoId(personaId, documentoId, lectivoId);
        for (ChequeraSerie chequera : chequeras) {
            setUltimoEnvio(chequera);
        }
        return chequeras;
    }

    @Override
    public List<ChequeraSerie> getAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId) {
        return repository.findAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(personaId, documentoId, facultadId, lectivoId);
    }

    @Override
    public List<ChequeraSerie> getAllByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId, Sort sort) {
        return repository.findAllByPersonaIdAndDocumentoId(personaId, documentoId, sort);
    }

    private void setDeuda(ChequeraSerie chequera) {
        var deuda = calculateDeudaUseCase.calculateDeuda(chequera);
        chequera.setCuotasDeuda(deuda.getCuotas());
        chequera.setImporteDeuda(deuda.getDeuda());
    }

    private void setUltimoEnvio(ChequeraSerie chequera) {
        var ultimoEnvio = chequeraImpresionCabeceraService.findLastByUnique(
                chequera.getFacultadId(),
                chequera.getTipoChequeraId(),
                chequera.getChequeraSerieId()
        )
        .map(cabecera -> Objects.requireNonNull(cabecera.getFecha()).plusHours(-3))
        .orElse(null);

        chequera.setUltimoEnvio(ultimoEnvio);
    }
}
