package um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import java.math.BigDecimal;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.entity.ChequeraCuotaEntity;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.mapper.ProductoMapper;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.mapper.ChequeraSerieMapper;

@Component
@RequiredArgsConstructor
public class ChequeraCuotaMapper {

    private final ProductoMapper productoMapper;
    private final ChequeraSerieMapper chequeraSerieMapper;

    public ChequeraCuota toDomain(ChequeraCuotaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ChequeraCuota.builder()
                .chequeraCuotaId(entity.getChequeraCuotaId())
                .chequeraId(entity.getChequeraId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .productoId(entity.getProductoId())
                .alternativaId(entity.getAlternativaId())
                .cuotaId(entity.getCuotaId())
                .mes(entity.getMes())
                .anho(entity.getAnho())
                .arancelTipoId(entity.getArancelTipoId())
                .vencimiento1(entity.getVencimiento1())
                .importe1(entity.getImporte1())
                .importe1Original(entity.getImporte1Original())
                .vencimiento2(entity.getVencimiento2())
                .importe2(entity.getImporte2())
                .importe2Original(entity.getImporte2Original())
                .vencimiento3(entity.getVencimiento3())
                .importe3(entity.getImporte3())
                .importe3Original(entity.getImporte3Original())
                .codigoBarras(entity.getCodigoBarras())
                .i2Of5(entity.getI2Of5())
                .pagado(entity.getPagado())
                .baja(entity.getBaja())
                .manual(entity.getManual())
                .compensada(entity.getCompensada())
                .tramoId(entity.getTramoId())
                .producto(productoMapper.toDomainModel(entity.getProducto()))
                .chequeraSerie(chequeraSerieMapper.toDomainModel(entity.getChequeraSerie()))
                .build();
    }

    public ChequeraCuotaEntity toEntity(ChequeraCuota domain) {
        if (domain == null) {
            return null;
        }
        return ChequeraCuotaEntity.builder()
                .chequeraCuotaId(domain.getChequeraCuotaId())
                .chequeraId(domain.getChequeraId())
                .facultadId(domain.getFacultadId())
                .tipoChequeraId(domain.getTipoChequeraId())
                .chequeraSerieId(domain.getChequeraSerieId())
                .productoId(domain.getProductoId())
                .alternativaId(domain.getAlternativaId())
                .cuotaId(domain.getCuotaId())
                .mes(domain.getMes())
                .anho(domain.getAnho())
                .arancelTipoId(domain.getArancelTipoId())
                .vencimiento1(domain.getVencimiento1())
                .importe1(domain.getImporte1())
                .importe1Original(domain.getImporte1Original())
                .vencimiento2(domain.getVencimiento2())
                .importe2(domain.getImporte2())
                .importe2Original(domain.getImporte2Original())
                .vencimiento3(domain.getVencimiento3())
                .importe3(domain.getImporte3())
                .importe3Original(domain.getImporte3Original())
                .codigoBarras(domain.getCodigoBarras())
                .i2Of5(domain.getI2Of5())
                .pagado(domain.getPagado())
                .baja(domain.getBaja())
                .manual(domain.getManual())
                .compensada(domain.getCompensada())
                .tramoId(domain.getTramoId())
                .build();
    }

    public void updateEntity(ChequeraCuota domain, ChequeraCuotaEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setChequeraId(domain.getChequeraId());
        entity.setFacultadId(domain.getFacultadId());
        entity.setTipoChequeraId(domain.getTipoChequeraId());
        entity.setChequeraSerieId(domain.getChequeraSerieId());
        entity.setProductoId(domain.getProductoId());
        entity.setAlternativaId(domain.getAlternativaId());
        entity.setCuotaId(domain.getCuotaId());
        entity.setMes(domain.getMes());
        entity.setAnho(domain.getAnho());
        entity.setArancelTipoId(domain.getArancelTipoId());
        entity.setVencimiento1(domain.getVencimiento1());
        entity.setImporte1(domain.getImporte1());
        entity.setImporte1Original(domain.getImporte1Original());
        entity.setVencimiento2(domain.getVencimiento2());
        entity.setImporte2(domain.getImporte2());
        entity.setImporte2Original(domain.getImporte2Original());
        entity.setVencimiento3(domain.getVencimiento3());
        entity.setImporte3(domain.getImporte3());
        entity.setImporte3Original(domain.getImporte3Original());
        entity.setCodigoBarras(domain.getCodigoBarras());
        entity.setI2Of5(domain.getI2Of5());
        entity.setPagado(domain.getPagado());
        entity.setBaja(domain.getBaja());
        entity.setManual(domain.getManual());
        entity.setCompensada(domain.getCompensada());
        entity.setTramoId(domain.getTramoId());
    }

    public ChequeraPago toDomain(um.tesoreria.core.kotlin.model.ChequeraPago entity) {
        if (entity == null) {
            return null;
        }
        return ChequeraPago.builder()
                .chequeraPagoId(entity.getChequeraPagoId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .productoId(entity.getProductoId())
                .alternativaId(entity.getAlternativaId())
                .cuotaId(entity.getCuotaId())
                .importe(entity.getImporte())
                .build();
    }

    public ChequeraTotal toDomain(um.tesoreria.core.model.ChequeraTotal entity) {
        if (entity == null) {
            return null;
        }
        return ChequeraTotal.builder()
                .chequeraTotalId(entity.getChequeraTotalId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .productoId(entity.getProductoId())
                .total(entity.getTotal())
                .build();
    }

}
