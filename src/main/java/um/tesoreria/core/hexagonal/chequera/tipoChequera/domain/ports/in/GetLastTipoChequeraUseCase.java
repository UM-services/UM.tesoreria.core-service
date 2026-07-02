package um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import java.util.Optional;

public interface GetLastTipoChequeraUseCase {
    Optional<TipoChequera> getLastTipoChequera();
}
