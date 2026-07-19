package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.politicaArancelaria.infrastructure.web.dto.PoliticaArancelariaResponse;

@Component
public class PoliticaArancelariaDtoMapper {

    public PoliticaArancelariaResponse toResponse(ChequeraCuota domain) {
        if (domain == null) return null;
        return PoliticaArancelariaResponse.builder()
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

}
