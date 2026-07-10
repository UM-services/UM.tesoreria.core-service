package um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.web.dto.ChequeraCuotaRequest;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.web.dto.ChequeraCuotaResponse;

@Component
public class ChequeraCuotaDtoMapper {

    public ChequeraCuota toDomain(ChequeraCuotaRequest request) {
        if (request == null) return null;
        return ChequeraCuota.builder()
                .chequeraCuotaId(request.getChequeraCuotaId())
                .chequeraId(request.getChequeraId())
                .facultadId(request.getFacultadId())
                .tipoChequeraId(request.getTipoChequeraId())
                .chequeraSerieId(request.getChequeraSerieId())
                .productoId(request.getProductoId())
                .alternativaId(request.getAlternativaId())
                .cuotaId(request.getCuotaId())
                .mes(request.getMes())
                .anho(request.getAnho())
                .arancelTipoId(request.getArancelTipoId())
                .vencimiento1(request.getVencimiento1())
                .importe1(request.getImporte1())
                .importe1Original(request.getImporte1Original())
                .vencimiento2(request.getVencimiento2())
                .importe2(request.getImporte2())
                .importe2Original(request.getImporte2Original())
                .vencimiento3(request.getVencimiento3())
                .importe3(request.getImporte3())
                .importe3Original(request.getImporte3Original())
                .codigoBarras(request.getCodigoBarras())
                .i2Of5(request.getI2Of5())
                .pagado(request.getPagado())
                .baja(request.getBaja())
                .manual(request.getManual())
                .compensada(request.getCompensada())
                .tramoId(request.getTramoId())
                .build();
    }

    public ChequeraCuotaResponse toResponse(ChequeraCuota domain) {
        if (domain == null) return null;
        return ChequeraCuotaResponse.builder()
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
                .facultad(domain.getFacultad())
                .tipoChequera(domain.getTipoChequera())
                .producto(domain.getProducto())
                .chequeraSerie(domain.getChequeraSerie())
                .build();
    }
}
