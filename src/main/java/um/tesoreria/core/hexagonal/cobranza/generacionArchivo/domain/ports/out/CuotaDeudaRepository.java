package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out;

import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.CuotaDeuda;

import java.time.OffsetDateTime;
import java.util.List;

public interface CuotaDeudaRepository {

    List<CuotaDeuda> findAllByRango(OffsetDateTime desde, OffsetDateTime hasta);

    List<CuotaDeuda> findAllByGrupoRango(Integer grupo, OffsetDateTime desde, OffsetDateTime hasta);

    List<CuotaDeuda> findAllMacroByRango(OffsetDateTime desde, OffsetDateTime hasta);

    List<CuotaDeuda> findPptByRango(OffsetDateTime desde, OffsetDateTime hasta);

}
