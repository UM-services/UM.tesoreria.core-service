package um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.mapper.ClaseChequeraMapper;
import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.mapper.GeograficaMapper;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.entity.TipoChequeraEntity;

@Component
public class TipoChequeraMapper {

    private final GeograficaMapper geograficaMapper;
    private final ClaseChequeraMapper claseChequeraMapper;

    public TipoChequeraMapper(GeograficaMapper geograficaMapper, ClaseChequeraMapper claseChequeraMapper) {
        this.geograficaMapper = geograficaMapper;
        this.claseChequeraMapper = claseChequeraMapper;
    }

    public TipoChequera toDomainModel(TipoChequeraEntity entity) {
        if (entity == null) return null;
        Geografica geografica = geograficaMapper.toDomainModel(entity.getGeografica());
        ClaseChequera claseChequera = claseChequeraMapper.toDomainModel(entity.getClaseChequera());
        return TipoChequera.builder()
                .tipoChequeraId(entity.getTipoChequeraId())
                .nombre(entity.getNombre())
                .prefijo(entity.getPrefijo())
                .geograficaId(entity.getGeograficaId())
                .claseChequeraId(entity.getClaseChequeraId())
                .imprimir(entity.getImprimir())
                .contado(entity.getContado())
                .multiple(entity.getMultiple())
                .emailCopia(entity.getEmailCopia())
                .geografica(geografica)
                .claseChequera(claseChequera)
                .build();
    }

    public TipoChequeraEntity toEntity(TipoChequera domain) {
        if (domain == null) return null;
        return TipoChequeraEntity.builder()
                .tipoChequeraId(domain.getTipoChequeraId())
                .nombre(domain.getNombre())
                .prefijo(domain.getPrefijo())
                .geograficaId(domain.getGeograficaId())
                .claseChequeraId(domain.getClaseChequeraId())
                .imprimir(domain.getImprimir())
                .contado(domain.getContado())
                .multiple(domain.getMultiple())
                .emailCopia(domain.getEmailCopia())
                .build();
    }
}
