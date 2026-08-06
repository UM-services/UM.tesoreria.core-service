package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import java.math.BigDecimal;
import java.util.Optional;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

public interface FindPreuniversitarioFromDatosGuaraniUseCase {
    Optional<ChequeraSerie> find(BigDecimal nroDocumento, Integer tipoDocumento, Integer ubicacion,
                                 Integer responsableAcademica, Integer lectivoId);
}
