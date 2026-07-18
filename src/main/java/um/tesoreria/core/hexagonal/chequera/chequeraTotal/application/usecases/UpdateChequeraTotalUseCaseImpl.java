package um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.in.UpdateChequeraTotalUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.out.ChequeraTotalRepository;

@Component
@RequiredArgsConstructor
public class UpdateChequeraTotalUseCaseImpl implements UpdateChequeraTotalUseCase {

    private final ChequeraTotalRepository repository;

    @Override
    public ChequeraTotal update(ChequeraTotal chequeraTotal, Long chequeraTotalId) {
        return repository.findByChequeraTotalId(chequeraTotalId).map(existing -> {
            ChequeraTotal updated = ChequeraTotal.builder()
                    .chequeraTotalId(chequeraTotalId)
                    .facultadId(chequeraTotal.getFacultadId())
                    .tipoChequeraId(chequeraTotal.getTipoChequeraId())
                    .chequeraSerieId(chequeraTotal.getChequeraSerieId())
                    .productoId(chequeraTotal.getProductoId())
                    .total(chequeraTotal.getTotal())
                    .pagado(chequeraTotal.getPagado())
                    .build();
            return repository.save(updated);
        }).orElseThrow(() -> new um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.exception.ChequeraTotalException(chequeraTotalId));
    }
}
