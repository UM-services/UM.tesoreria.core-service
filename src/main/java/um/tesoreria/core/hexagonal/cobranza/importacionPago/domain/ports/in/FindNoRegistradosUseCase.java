package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in;

import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoBanco;

import java.util.List;

public interface FindNoRegistradosUseCase {

    List<ArchivoBanco> noRegistrados(Integer importacionId);

}
