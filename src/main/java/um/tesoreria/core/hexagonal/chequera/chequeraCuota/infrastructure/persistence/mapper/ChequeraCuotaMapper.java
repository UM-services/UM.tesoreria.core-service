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
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.mapper.FacultadMapper;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.mapper.TipoChequeraMapper;

@Component
@RequiredArgsConstructor
public class ChequeraCuotaMapper {

    private final ProductoMapper productoMapper;
    private final ChequeraSerieMapper chequeraSerieMapper;
    private final FacultadMapper facultadMapper;
    private final TipoChequeraMapper tipoChequeraMapper;

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
                .facultad(facultadMapper.toDomain(entity.getFacultad()))
                .tipoChequera(tipoChequeraMapper.toDomainModel(entity.getTipoChequera()))
                .producto(productoMapper.toDomainModel(entity.getProducto()))
                .chequeraSerie(chequeraSerieMapper.toDomainModel(entity.getChequeraSerie()))
                .build();
    }

    public ChequeraCuotaEntity toEntity(ChequeraCuota domain) {
        if (domain == null) {
            return null;
        }
        ChequeraCuotaEntity.ChequeraCuotaEntityBuilder builder = ChequeraCuotaEntity.builder()
                .chequeraCuotaId(domain.getChequeraCuotaId())
                .chequeraId(domain.getChequeraId())
                .facultadId(domain.getFacultadId())
                .tipoChequeraId(domain.getTipoChequeraId())
                .chequeraSerieId(domain.getChequeraSerieId())
                .productoId(domain.getProductoId())
                .alternativaId(domain.getAlternativaId())
                .cuotaId(domain.getCuotaId())
                .arancelTipoId(domain.getArancelTipoId())
                .vencimiento1(domain.getVencimiento1())
                .vencimiento2(domain.getVencimiento2())
                .vencimiento3(domain.getVencimiento3());

        if (domain.getMes() != null) builder.mes(domain.getMes());
        if (domain.getAnho() != null) builder.anho(domain.getAnho());
        if (domain.getImporte1() != null) builder.importe1(domain.getImporte1());
        if (domain.getImporte1Original() != null) builder.importe1Original(domain.getImporte1Original());
        if (domain.getImporte2() != null) builder.importe2(domain.getImporte2());
        if (domain.getImporte2Original() != null) builder.importe2Original(domain.getImporte2Original());
        if (domain.getImporte3() != null) builder.importe3(domain.getImporte3());
        if (domain.getImporte3Original() != null) builder.importe3Original(domain.getImporte3Original());
        if (domain.getCodigoBarras() != null) builder.codigoBarras(domain.getCodigoBarras());
        if (domain.getI2Of5() != null) builder.i2Of5(domain.getI2Of5());
        if (domain.getPagado() != null) builder.pagado(domain.getPagado());
        if (domain.getBaja() != null) builder.baja(domain.getBaja());
        if (domain.getManual() != null) builder.manual(domain.getManual());
        if (domain.getCompensada() != null) builder.compensada(domain.getCompensada());
        if (domain.getTramoId() != null) builder.tramoId(domain.getTramoId());

        return builder.build();
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
        
        if (domain.getMes() != null) entity.setMes(domain.getMes());
        if (domain.getAnho() != null) entity.setAnho(domain.getAnho());
        entity.setArancelTipoId(domain.getArancelTipoId());
        entity.setVencimiento1(domain.getVencimiento1());
        if (domain.getImporte1() != null) entity.setImporte1(domain.getImporte1());
        if (domain.getImporte1Original() != null) entity.setImporte1Original(domain.getImporte1Original());
        entity.setVencimiento2(domain.getVencimiento2());
        if (domain.getImporte2() != null) entity.setImporte2(domain.getImporte2());
        if (domain.getImporte2Original() != null) entity.setImporte2Original(domain.getImporte2Original());
        entity.setVencimiento3(domain.getVencimiento3());
        if (domain.getImporte3() != null) entity.setImporte3(domain.getImporte3());
        if (domain.getImporte3Original() != null) entity.setImporte3Original(domain.getImporte3Original());
        if (domain.getCodigoBarras() != null) entity.setCodigoBarras(domain.getCodigoBarras());
        if (domain.getI2Of5() != null) entity.setI2Of5(domain.getI2Of5());
        if (domain.getPagado() != null) entity.setPagado(domain.getPagado());
        if (domain.getBaja() != null) entity.setBaja(domain.getBaja());
        if (domain.getManual() != null) entity.setManual(domain.getManual());
        if (domain.getCompensada() != null) entity.setCompensada(domain.getCompensada());
        if (domain.getTramoId() != null) entity.setTramoId(domain.getTramoId());
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
