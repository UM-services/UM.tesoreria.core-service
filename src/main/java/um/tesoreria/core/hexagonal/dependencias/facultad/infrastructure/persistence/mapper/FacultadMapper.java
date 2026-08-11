package um.tesoreria.core.hexagonal.dependencias.facultad.infrastructure.persistence.mapper;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.dependencias.facultad.infrastructure.persistence.entity.FacultadEntity;

@Component
public class FacultadMapper {
    public Facultad toDomain(FacultadEntity entity) {
        if (entity == null) return null;
        return Facultad.builder()
                .facultadId(entity.getFacultadId())
                .nombre(entity.getNombre())
                .codigoempresa(entity.getCodigoempresa())
                .server(entity.getServer())
                .dbadm(entity.getDbadm())
                .dsn(entity.getDsn())
                .cuentacontable(entity.getCuentacontable())
                .apiserver(entity.getApiserver())
                .apiport(entity.getApiport())
                .guaraniResponsableAcademica(entity.getGuaraniResponsableAcademica())
                .build();
    }
}
