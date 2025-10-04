package um.tesoreria.core.hexagonal.cursoCargoContratado.domain.ports.out;

import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model.CursoCargoContratado;

import java.math.BigDecimal;
import java.util.List;

public interface CursoCargoContratadoRepository {

    List<CursoCargoContratado> findAllByContrato(Long contratoId, Integer anho, Integer mes, BigDecimal personaId, Integer documentoId);

}
