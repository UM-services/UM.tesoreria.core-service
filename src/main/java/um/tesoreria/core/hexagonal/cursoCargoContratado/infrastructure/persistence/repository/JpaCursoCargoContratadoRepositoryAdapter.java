package um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model.CursoCargoContratado;
import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.ports.out.CursoCargoContratadoRepository;
import um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.persistence.mapper.CursoCargoContratadoMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaCursoCargoContratadoRepositoryAdapter implements CursoCargoContratadoRepository {

    private final JpaCursoCargoContratadoRepository jpaCursoCargoContratadoRepository;
    private final CursoCargoContratadoMapper cursoCargoContratadoMapper;

    @Override
    public List<CursoCargoContratado> findAllByContrato(Long contratoId, Integer anho, Integer mes, BigDecimal personaId, Integer documentoId) {
        return jpaCursoCargoContratadoRepository
                .findAllByContratoIdAndAnhoAndMesAndPersonaIdAndDocumentoId(contratoId, anho, mes, personaId, documentoId)
                .stream()
                .map(cursoCargoContratadoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

}
