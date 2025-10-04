package um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model.CursoCargoContratado;
import um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.persistence.entity.CursoCargoContratadoEntity;

@Component
public class CursoCargoContratadoMapper {

    public CursoCargoContratado toDomainModel(CursoCargoContratadoEntity cursoCargoContratadoEntity) {
        if (cursoCargoContratadoEntity == null) {
            return null;
        }
        return CursoCargoContratado
                .builder()
                .cursoCargoContratadoId(cursoCargoContratadoEntity.getCursoCargoContratadoId())
                .cursoId(cursoCargoContratadoEntity.getCursoId())
                .anho(cursoCargoContratadoEntity.getAnho())
                .mes(cursoCargoContratadoEntity.getMes())
                .contratoId(cursoCargoContratadoEntity.getContratoId())
                .personaId(cursoCargoContratadoEntity.getPersonaId())
                .documentoId(cursoCargoContratadoEntity.getDocumentoId())
                .cargoTipoId(cursoCargoContratadoEntity.getCargoTipoId())
                .horasSemanales(cursoCargoContratadoEntity.getHorasSemanales())
                .horasTotales(cursoCargoContratadoEntity.getHorasTotales())
                .designacionTipoId(cursoCargoContratadoEntity.getDesignacionTipoId())
                .categoriaId(cursoCargoContratadoEntity.getCategoriaId())
                .cursoCargoNovedadId(cursoCargoContratadoEntity.getCursoCargoNovedadId())
                .acreditado(cursoCargoContratadoEntity.getAcreditado())
                .build();
    }

}
