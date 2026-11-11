package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in;

import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoImportado;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.CanalImportacion;

import java.util.List;

public interface ListarImportacionesUseCase {

    List<ArchivoImportado> listar(CanalImportacion canal);

}
