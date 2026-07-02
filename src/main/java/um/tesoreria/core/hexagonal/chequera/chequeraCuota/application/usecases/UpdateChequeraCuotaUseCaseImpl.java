package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.exception.ChequeraCuotaException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.UpdateChequeraCuotaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;

@Component
@RequiredArgsConstructor
public class UpdateChequeraCuotaUseCaseImpl implements UpdateChequeraCuotaUseCase {

    private final ChequeraCuotaRepository repository;

    @Override
    public ChequeraCuota update(ChequeraCuota newChequeraCuota, Long chequeraCuotaId) {
        return repository.findByChequeraCuotaId(chequeraCuotaId).map(chequeraCuota -> {
            chequeraCuota = ChequeraCuota.builder()
                    .chequeraCuotaId(chequeraCuotaId)
                    .chequeraId(newChequeraCuota.getChequeraId())
                    .facultadId(newChequeraCuota.getFacultadId())
                    .tipoChequeraId(newChequeraCuota.getTipoChequeraId())
                    .chequeraSerieId(newChequeraCuota.getChequeraSerieId())
                    .productoId(newChequeraCuota.getProductoId())
                    .alternativaId(newChequeraCuota.getAlternativaId())
                    .cuotaId(newChequeraCuota.getCuotaId())
                    .mes(newChequeraCuota.getMes())
                    .anho(newChequeraCuota.getAnho())
                    .arancelTipoId(newChequeraCuota.getArancelTipoId())
                    .vencimiento1(newChequeraCuota.getVencimiento1())
                    .importe1(newChequeraCuota.getImporte1())
                    .importe1Original(newChequeraCuota.getImporte1Original())
                    .vencimiento2(newChequeraCuota.getVencimiento2())
                    .importe2(newChequeraCuota.getImporte2())
                    .importe2Original(newChequeraCuota.getImporte2Original())
                    .vencimiento3(newChequeraCuota.getVencimiento3())
                    .importe3(newChequeraCuota.getImporte3())
                    .importe3Original(newChequeraCuota.getImporte3Original())
                    .codigoBarras(newChequeraCuota.getCodigoBarras())
                    .i2Of5(newChequeraCuota.getI2Of5())
                    .pagado(newChequeraCuota.getPagado())
                    .baja(newChequeraCuota.getBaja())
                    .manual(newChequeraCuota.getManual())
                    .compensada(newChequeraCuota.getCompensada())
                    .tramoId(newChequeraCuota.getTramoId())
                    .build();
            return repository.save(chequeraCuota);
        }).orElseThrow(() -> new ChequeraCuotaException(chequeraCuotaId));
    }
}
